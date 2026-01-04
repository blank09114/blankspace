package kr.io.blankspace.account.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.io.blankspace.account.dto.auth.*;
import kr.io.blankspace.infra.ApiOk;
import kr.io.blankspace.account.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<ApiOk> resendJoin(@RequestBody @Valid ResendTokenRequest req) {
        authService.resendJoinToken(req.getUserMail());
        return ok();
    }

    // 회원가입
    @GetMapping("/join/verify")
    public void verifyJoin(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        try {
            authService.verifyJoin(token);
            response.sendRedirect("/?joined=1");
        } catch (IllegalArgumentException e) { response.sendRedirect("/?joined=expired"); }
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
    public ResponseEntity<ApiOk> resetRequest(@RequestBody @Valid PWResetRequestDTO dto) {
        authService.requestPasswordReset(dto.getUserMail());
        return ok();
    }

    // 비밀번호 재설정
    @GetMapping("/password/reset/apply")
    public void resetApply(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        try {
            authService.applyPasswordReset(token);
            response.sendRedirect("/?reset=done");
        } catch (IllegalArgumentException e) { response.sendRedirect("/?reset=expired"); }
    }

    // 비밀번호 변경
    @PostMapping("/password/change")
    public ResponseEntity<ApiOk> changePassword
    (@RequestBody @Valid ChangePWRequestDTO dto, Authentication authentication, HttpServletRequest request) {
        authService.changePassword(authentication.getName(), dto.getCurrentPw(), dto.getNewPw(), request);
        return ok();
    }

    // 회원 탈퇴 요청
    @PostMapping("/withdraw/request")
    public ResponseEntity<ApiOk> withdrawRequest
    (@RequestBody @Valid WithdrawRequestDTO dto, Authentication authentication) {
        authService.requestWithdraw(authentication.getName(), dto.getUserPw());
        return ok();
    }

    // 회원 탈퇴
    @GetMapping("/withdraw/apply")
    public void withdrawApply(@RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            authService.applyWithdraw(token);
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            SecurityContextHolder.clearContext();

            response.sendRedirect("/?withdraw=done");
        } catch (IllegalArgumentException e) { response.sendRedirect("/?withdraw=expired"); }
    }

    // 헬퍼
    private ResponseEntity<ApiOk> ok() { return ResponseEntity.ok(new ApiOk(true)); }
    private ResponseEntity<Void> redirect(String location) { return ResponseEntity.status(302).location(URI.create(location)).build(); }
}