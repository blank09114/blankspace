package kr.io.blankspace.api.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiOk> requestJoin(@RequestBody @Valid JoinRequestDTO dto) {
        authService.requestJoin(dto);
        return ok();
    }

    // 토큰 재발급
    @PostMapping("/join/resend")
    public ResponseEntity<ApiOk> resendJoin(@RequestBody @Valid ResendJoinTokenRequest req) {
        authService.resendJoinToken(req.getUserMail());
        return ok();
    }

    // 회원가입
    @GetMapping("/join/verify")
    public ResponseEntity<Void> verifyJoin(@RequestParam("token") String token) {
        authService.verifyJoin(token);
        return redirect("/?joined=1");
    }

    // ID 중복 검사
    @GetMapping("/exists/{userId}")
    public Map<String, Boolean> exists(@PathVariable String userId)
    { return Map.of("exists", authService.existsUserId(userId)); }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiOk> login(@RequestBody @Valid LoginRequestDTO dto, HttpServletRequest request) {
        authService.login(dto, request);
        return ok();
    }

    // 로그인 정보 반환
    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication)
    { return ResponseEntity.ok(authService.me(authentication)); }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiOk> logout(HttpServletRequest request) {
        authService.logout(request);
        return ok();
    }

    // 비밀번호 재설정 요청
    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiOk> resetRequest(@RequestBody @Valid PasswordResetRequestDTO dto) {
        authService.requestPasswordReset(dto.getUserMail());
        return ok();
    }

    // 비밀번호 재설정
    @GetMapping("/password/reset/apply")
    public ResponseEntity<Void> resetApply(@RequestParam("token") String token) {
        try {
            authService.applyPasswordReset(token);
            return redirect("/?reset=done");
        } catch (IllegalArgumentException e) { return redirect("/?reset=expired"); }
    }

    // 헬퍼
    private ResponseEntity<ApiOk> ok() { return ResponseEntity.ok(new ApiOk(true)); }
    private ResponseEntity<Void> redirect(String location) { return ResponseEntity.status(302).location(URI.create(location)).build(); }
}