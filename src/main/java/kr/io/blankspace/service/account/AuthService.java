package kr.io.blankspace.service.account;

import kr.io.blankspace.domain.account.token.Token;
import kr.io.blankspace.domain.account.token.TokenRepository;
import kr.io.blankspace.domain.account.user.User;
import kr.io.blankspace.domain.account.user.UserRepository;
import kr.io.blankspace.dto.account.auth.JoinRequestDTO;
import kr.io.blankspace.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.join-token-minutes:30}")
    private long joinTokenMinutes;

    // 회원가입 요청
    @Transactional
    public void requestJoin(JoinRequestDTO req) {
        // 서버에서도 정책 강제: naver.com만 가입 허용
        validateNaverOnly(req.getUserMail());

        if (userRepository.existsById(req.getUserId()))
        { throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); }
        if (userRepository.existsByUserMail(req.getUserMail()))
        { throw new IllegalArgumentException("이미 사용 중인 이메일입니다."); }

        // user 생성
        User user = User.builder()
        .userId(req.getUserId())
        .userMail(req.getUserMail())
        .userName(req.getUserName())
        .userPw(passwordEncoder.encode(req.getUserPw()))
        .userRole(User.UserRole.USER)
        .userEnabled(false)
        .isBlocked(false)
        .build();
        userRepository.save(user);

        // JOIN 토큰 저장
        String rawToken = TokenUtil.generateToken(48);
        String tokenHash = TokenUtil.sha256Hex(rawToken);

        Token token = Token.builder()
        .user(user)
        .tokenType(Token.TokenType.JOIN)
        .tokenHash(tokenHash)
        .expiresAt(LocalDateTime.now().plusMinutes(joinTokenMinutes))
        .build();
        tokenRepository.save(token);

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

        if (user.isUserEnabled()) { return; }

        String rawToken = TokenUtil.generateToken(48);
        String tokenHash = TokenUtil.sha256Hex(rawToken);

        Token token = Token.builder()
        .user(user).tokenType(Token.TokenType.JOIN).tokenHash(tokenHash)
        .expiresAt(LocalDateTime.now().plusMinutes(joinTokenMinutes)).build();
        tokenRepository.save(token);

        String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String verifyLink = baseUrl + "/api/auth/join/verify?token=" + encoded;

        mailService.sendJoinVerifyMail(user.getUserMail(), verifyLink);
    }

    // 인증
    @Transactional
    public void verifyJoin(String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);

        Token token = tokenRepository.findValidWithUser(hash, Token.TokenType.JOIN, LocalDateTime.now())
        .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않거나 만료되었습니다."));

        User user = token.getUser();

        // 이미 인증 완료면 멱등 처리
        if (!user.isUserEnabled()) { user.enable(); }
        token.markUsed();
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
}