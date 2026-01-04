package kr.io.blankspace.service.account.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.io.blankspace.entity.Token;
import kr.io.blankspace.entity.User;
import kr.io.blankspace.repository.UserRepository;
import kr.io.blankspace.dto.account.auth.JoinRequestDTO;
import kr.io.blankspace.dto.account.auth.LoginRequestDTO;
import kr.io.blankspace.dto.account.auth.LoginResponseDTO;
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
    public void requestJoin(JoinRequestDTO req) {
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

        // 메일 발송
        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String verifyLink = baseUrl + "/api/auth/join/verify?token=" + encoded;
        mailService.sendJoinVerifyMail(user.getUserMail(), verifyLink);
    }

    // 토큰 재발급
    @Transactional
    public void resendJoinToken(String userMail) {
        validateNaverOnly(userMail);

        User user = userRepository.findByUserMail(userMail.trim()).orElse(null);

        // 계정 유무 노출 방지: 없어도 그냥 성공처럼 처리
        if (user == null) return;
        if (user.isUserEnabled()) return;

        String rawToken = tokenService.issue(user, Token.TokenType.JOIN, joinTokenMinutes, 48);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String verifyLink = baseUrl + "/api/auth/join/verify?token=" + encoded;

        mailService.sendJoinVerifyMail(user.getUserMail(), verifyLink);
    }

    // 인증
    @Transactional
    public void verifyJoin(String rawToken) {
        Token token = tokenService.getValidTokenWithUser(rawToken, Token.TokenType.JOIN);
        User user = token.getUser();

        // 이미 인증 완료면 멱등 처리
        if (!user.isUserEnabled()) user.enable();

        tokenService.markUsed(token);
    }

    // 이메일 형식 검증
    private void validateNaverOnly(String mail) {
        String lower = mail.trim().toLowerCase();
        if (!lower.endsWith("@naver.com"))
        { throw new IllegalArgumentException("네이버 메일(@naver.com)만 가입 가능합니다."); }
    }

    // ID 중복 검사
    @Transactional(readOnly = true)
    public boolean existsUserId(String userId) {
        if (userId == null) return false;
        String trimmed = userId.trim();
        if (trimmed.isEmpty()) return false;
        return userRepository.existsById(trimmed);
    }

    // 로그인
    @Transactional
    public void login(LoginRequestDTO req, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate
            (new UsernamePasswordAuthenticationToken(req.getUserId().trim(), req.getUserPw()));

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            String userId = auth.getName();
            User user = userRepository.findById(userId).orElseThrow();
            loginLogService.recordLogin(user, session, request);
        } catch (DisabledException e)
        { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "미인증 계정입니다."); }
        catch (LockedException e)
        { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "차단된 계정입니다."); }
        catch (BadCredentialsException | UsernameNotFoundException e)
        { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."); }
    }

    // 로그인 정보 반환
    @Transactional(readOnly = true)
    public LoginResponseDTO me(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) return null;

        String userId = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        return LoginResponseDTO.builder()
        .userId(user.getUserId()).userMail(user.getUserMail()).userName(user.getUserName())
        .userRole(user.getUserRole().name()).build();
    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        loginLogService.markLogout(session);

        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

    // 계정 찾기
    @Transactional
    public void requestPasswordReset(String userMail) {
        if (userMail == null) return;

        String mail = userMail.trim();
        if (!mail.toLowerCase().endsWith("@naver.com")) return;

        User user = userRepository.findByUserMail(mail).orElse(null);
        if (user == null) return; // 존재 여부 노출 방지

        // 임시 비밀번호 + 토큰 생성
        String tempPw = TokenUtil.generateToken(12);
        String nonce = TokenUtil.generateToken(36);
        String rawToken = tempPw + "." + nonce;

        tokenService.issueWithRaw(user, Token.TokenType.RESET, resetTokenMinutes, rawToken);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String link = baseUrl + "/api/auth/password/reset/apply?token=" + encoded;

        mailService.sendPasswordResetMail(user.getUserMail(), user.getUserId(), tempPw, link);
    }

    // 비밀번호 재설정 적용
    @Transactional
    public void applyPasswordReset(String rawToken) {
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

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(cur, user.getUserPw())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "현재 비밀번호가 올바르지 않습니다.");
        }

        // 같은 비밀번호로 변경 방지(선택)
        if (passwordEncoder.matches(next, user.getUserPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        // 저장 (pw_change_at 갱신 포함)
        user.changePassword(passwordEncoder.encode(next));

        // 비밀번호 변경 시: 모든 세션 로그아웃 정책 -> "현재 세션" 즉시 종료
        HttpSession session = request.getSession(false);
        loginLogService.markLogout(session);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

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

    @Transactional
    public void applyWithdraw(String encodedToken) {
        String rawToken = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

        Token token = tokenService.getValidTokenWithUser(rawToken, Token.TokenType.WITHDRAW);
        User user = token.getUser();

        tokenService.markUsed(token);
        user.withdrawAnonymize(passwordEncoder, TokenUtil.generateToken(32));
    }
}