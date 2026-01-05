package kr.io.blankspace.service.account.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.io.blankspace.dto.account.auth.AuthRequests;
import kr.io.blankspace.dto.account.auth.UserBasicDTO;
import kr.io.blankspace.entity.Token;
import kr.io.blankspace.entity.User;
import kr.io.blankspace.repository.UserRepository;
import kr.io.blankspace.service.account.LoginLogService;
import kr.io.blankspace.setting.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final TokenService tokenService;
    private final LoginLogService loginLogService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.join-token-minutes:30}")
    private long joinTokenMinutes;

    @Value("${app.reset-token-minutes:30}")
    private long resetTokenMinutes;

    @Value("${app.withdraw-token-minutes:30}")
    private long withdrawTokenMinutes;

    // 회원가입 요청
    @Transactional
    public void requestJoin(AuthRequests.Join req) {
        validateNaverOnly(req.getUserMail());

        if (userRepository.existsById(req.getUserId()))
        { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); }
        if (userRepository.existsByUserMail(req.getUserMail()))
        { throw new IllegalArgumentException("이미 사용 중인 이메일입니다."); }

        User user = User.builder()
        .userId(req.getUserId()).userMail(req.getUserMail()).userName(req.getUserName())
        .userPw(passwordEncoder.encode(req.getUserPw())).userRole(User.UserRole.USER)
        .userEnabled(false).isBlocked(false).build();

        userRepository.save(user);

        String rawToken = tokenService.issue(user, Token.TokenType.JOIN, joinTokenMinutes, 48);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String verifyLink = baseUrl + "/api/auth/join/verify?token=" + encoded;
        mailService.sendJoinVerifyMail(user.getUserMail(), verifyLink);
    }

    // 토큰 재발급
    @Transactional
    public void resendJoinToken(String userMail) {
        validateNaverOnly(userMail);

        User user = userRepository.findByUserMail(userMail.trim()).orElse(null);

        if (user == null) return;
        if (user.isUserEnabled()) return;

        String rawToken = tokenService.issue(user, Token.TokenType.JOIN, joinTokenMinutes, 48);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String verifyLink = baseUrl + "/api/auth/join/verify?token=" + encoded;
        mailService.sendJoinVerifyMail(user.getUserMail(), verifyLink);
    }

    // 회원가입 검증
    @Transactional
    public void verifyJoin(String encodedToken) {
        String rawToken = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

        Token token = tokenService.getValidTokenWithUser(rawToken, Token.TokenType.JOIN);
        User user = token.getUser();

        tokenService.markUsed(token);
        user.enable();
    }

    // ID 중복 검사
    @Transactional(readOnly = true)
    public boolean existsUserId(String userId)
    { return userRepository.existsById(userId); }

    // 로그인
    @Transactional
    public void login(AuthRequests.Login req, HttpServletRequest request) {
        String userId = (req.getUserId() == null) ? "" : req.getUserId().trim();
        String userPw = (req.getUserPw() == null) ? "" : req.getUserPw().trim();

        Authentication authentication;
        try { authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, userPw)); }
        catch (BadCredentialsException | UsernameNotFoundException e)
        { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."); }

        String loginUserId = authentication.getName();
        User user = userRepository.findById(loginUserId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        );

        // 로그인 로그 기록
        loginLogService.recordLogin(user, session, request);
    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        loginLogService.markLogout(session);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

    // 로그인 정보 반환
    @Transactional(readOnly = true)
    public UserBasicDTO me(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) return null;

        String userId = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        return UserBasicDTO.from(user);
    }

    // 비밀번호 재설정 요청
    @Transactional
    public void requestPasswordReset(String userMail) {
        validateNaverOnly(userMail);

        User user = userRepository.findByUserMail(userMail.trim()).orElse(null);
        if (user == null) return;

        String tempPw = TokenUtil.generateToken(12);
        String nonce = TokenUtil.generateToken(36);
        String rawToken = tempPw + "." + nonce;

        tokenService.issueWithRaw(user, Token.TokenType.RESET, resetTokenMinutes, rawToken); // 중요: RESET + issueWithRaw

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String link = baseUrl + "/api/auth/password/reset/apply?token=" + encoded;

        mailService.sendPasswordResetMail(user.getUserMail(), user.getUserId(), tempPw, link); // 중요: 인자 4개
    }

    // 비밀번호 재설정 적용
    @Transactional
    public void applyPasswordReset(String encodedToken) {
        String rawToken = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

        Token token = tokenService.getValidTokenWithUser(rawToken, Token.TokenType.RESET);

        int dot = rawToken.indexOf('.');
        if (dot <= 0) throw new IllegalArgumentException("토큰 형식이 올바르지 않습니다.");
        String tempPw = rawToken.substring(0, dot);

        User user = token.getUser();
        user.changePassword(passwordEncoder.encode(tempPw));

        tokenService.markUsed(token);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String userId, String currentPw, String newPw, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")
        );

        String cur = currentPw.trim();
        String next = newPw.trim();

        if (!passwordEncoder.matches(cur, user.getUserPw())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "현재 비밀번호가 올바르지 않습니다.");
        }
        if (passwordEncoder.matches(next, user.getUserPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        user.changePassword(passwordEncoder.encode(next));

        HttpSession session = request.getSession(false);
        loginLogService.markLogout(session);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

    // 회원 탈퇴 요청
    @Transactional
    public void requestWithdraw(String userId, String userPw) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")
        );

        String pw = userPw == null ? "" : userPw.trim();

        if (!passwordEncoder.matches(pw, user.getUserPw())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 올바르지 않습니다.");
        }

        String rawToken = tokenService.issue(user, Token.TokenType.WITHDRAW, withdrawTokenMinutes, 48);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String link = baseUrl + "/api/auth/withdraw/apply?token=" + encoded;

        mailService.sendWithdrawMail(user.getUserMail(), link);
    }

    // 회원 탈퇴 적용
    @Transactional
    public void applyWithdraw(String encodedToken) {
        String rawToken = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

        Token token = tokenService.getValidTokenWithUser(rawToken, Token.TokenType.WITHDRAW);
        User user = token.getUser();

        tokenService.markUsed(token);
        user.withdrawAnonymize(passwordEncoder, TokenUtil.generateToken(32));
    }

    // 네이버 메일만 허용 (기존 규칙 유지)
    private void validateNaverOnly(String mail) {
        if (mail == null) throw new IllegalArgumentException("이메일을 입력해주세요.");
        String trimmed = mail.trim().toLowerCase();
        if (!trimmed.endsWith("@naver.com")) {
            throw new IllegalArgumentException("네이버 이메일(@naver.com)만 사용할 수 있습니다.");
        }
    }
}