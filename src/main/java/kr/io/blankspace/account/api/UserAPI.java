package kr.io.blankspace.account.api;


import jakarta.validation.Valid;
import kr.io.blankspace.account.dto.user.BlockToggleRequest;
import kr.io.blankspace.account.dto.user.ChangeNameRequest;
import kr.io.blankspace.account.dto.user.LoginLogDTO;
import kr.io.blankspace.account.dto.user.UserInfoDTO;
import kr.io.blankspace.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserAPI {
    private final UserService userService;

    // 닉네임 변경
    @PatchMapping("/{userId}/name")
    public UserInfoDTO changeName(@PathVariable String userId,
                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal,
                                  @RequestBody @Valid ChangeNameRequest req) {
        String viewerId = principal.getUsername();
        return userService.changeMyName(viewerId, userId, req.userName());
    }

    // 차단/차단 해제
    @PatchMapping("/{userId}/block")
    public UserInfoDTO toggleBlock
    (@PathVariable String userId, @RequestBody(required = false) @Valid BlockToggleRequest req) {
        String reason = (req == null) ? null : req.reason();
        return userService.toggleBlock(userId, reason);
    }

    // 로그인 기록 조회
    @GetMapping("/{userId}/login-logs")
    public Page<LoginLogDTO> getLoginLogs(
        @PathVariable String userId, @RequestParam(defaultValue = "0") int page,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal
    ) { return userService.getLoginLogs(principal, userId, page); }

    // 회원 목록 조회
    @GetMapping("/list")
    public Page<UserInfoDTO> getUserList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    { return userService.getUserList(page, size); }
}