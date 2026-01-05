package kr.io.blankspace.service.account.auth;

import kr.io.blankspace.entity.Token;
import kr.io.blankspace.repository.TokenRepository;
import kr.io.blankspace.entity.User;
import kr.io.blankspace.setting.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    // 랜덤 rawToken 생성 후 반환
    @Transactional
    public String issue(User user, Token.TokenType type, long minutes, int rawLen) {
        String rawToken = TokenUtil.generateToken(rawLen);
        return issueInternal(user, type, minutes, rawToken);
    }

    // rawToken을 외부애서 지정해서 저장
    @Transactional
    public String issueWithRaw(User user, Token.TokenType type, long minutes, String rawToken)
    { return issueInternal(user, type, minutes, rawToken); }

    // 유효 토큰 조회
    @Transactional(readOnly = true)
    public Token getValidTokenWithUser(String rawToken, Token.TokenType type) {
        String hash = TokenUtil.sha256Hex(rawToken);
        return tokenRepository.findValidWithUser(hash, type, LocalDateTime.now())
        .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않거나 만료되었습니다."));
    }

    // 사용 처리
    @Transactional
    public void markUsed(Token token) { token.markUsed(); }

    // 공통 저장 로직
    private String issueInternal(User user, Token.TokenType type, long minutes, String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);

        Token token = Token.builder()
        .user(user).tokenType(type).tokenHash(hash)
        .expiresAt(LocalDateTime.now().plusMinutes(minutes)).build();

        tokenRepository.save(token);
        return rawToken;
    }
}