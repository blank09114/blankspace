package kr.io.blankspace.api.account;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.account.auth.ApiOk;
import kr.io.blankspace.dto.account.auth.JoinRequestDTO;
import kr.io.blankspace.dto.account.auth.ResendJoinTokenRequest;
import kr.io.blankspace.service.account.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}