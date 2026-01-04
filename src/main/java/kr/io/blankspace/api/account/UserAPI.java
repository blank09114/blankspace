package kr.io.blankspace.api.account;


import jakarta.validation.Valid;
import kr.io.blankspace.dto.account.user.ChangeNameRequestDTO;
import kr.io.blankspace.dto.account.user.UserCardDto;
import kr.io.blankspace.service.account.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserAPI {
    private final UserService userService;

    // 닉네임 변경
    @PatchMapping("/{userId}/name")
    public UserCardDto changeName(@PathVariable String userId,
    @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal,
    @RequestBody @Valid ChangeNameRequestDTO req) {
        String viewerId = principal.getUsername();
        return userService.changeMyName(viewerId, userId, req.userName());
    }
}