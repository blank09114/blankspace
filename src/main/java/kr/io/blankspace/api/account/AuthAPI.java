package kr.io.blankspace.api.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.io.blankspace.domain.account.user.User;
import kr.io.blankspace.dto.account.auth.*;
import kr.io.blankspace.service.account.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthAPI {
    private final AuthService authService;

    // 회원가입 요청
    @PostMapping("/join/request")
    public ResponseEntity<ApiOk> request(@RequestBody @Valid JoinRequestDTO dto) {
        authService.requestJoin(dto);
        return ResponseEntity.ok(new ApiOk(true));
    }

    // 토큰 재발급
    @PostMapping("/join/resend")
    public ResponseEntity<ApiOk> resend(@RequestBody @Valid ResendJoinTokenRequest req) {
        authService.resendJoinToken(req.getUserMail());
        return ResponseEntity.ok(new ApiOk(true));
    }

    // 인증
    @GetMapping("/join/verify")
    public ResponseEntity<Void> verify(@RequestParam("token") String token) {
        authService.verifyJoin(token);
        return ResponseEntity.status(302).location(URI.create("/?joined=1")).build();
    }

    // ID 중복 검사
    @GetMapping("/exists/{userId}")
    public Map<String, Boolean> exists(@PathVariable String userId) {
        boolean exists = authService.existsUserId(userId);
        return Map.of("exists", exists);
    }

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<ApiOk> login
    (@RequestBody @Valid LoginRequestDTO dto, HttpServletRequest request) {
        authService.login(dto, request);
        return ResponseEntity.ok(new ApiOk(true));
    }

    // 로그인 정보 반환
    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication)
    { return ResponseEntity.ok(authService.me(authentication)); }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiOk> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiOk(true));
    }
}