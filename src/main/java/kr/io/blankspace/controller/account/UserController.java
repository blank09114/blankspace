package kr.io.blankspace.controller.account;

import kr.io.blankspace.service.account.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    // 내 정보
    @GetMapping("/me")
    public String myInfo
    (@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal, Model model) {
        String userId = principal.getUsername();
        model.addAttribute("userInfo", userService.getUserCard(userId));
        model.addAttribute("isSelf", true);
        return "account/user/userInfo";
    }

    // 회원 정보
    @GetMapping("/{userId}")
    public String userInfo
    (@PathVariable String userId, @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal, Model model) {
        String viewerId = (principal != null) ? principal.getUsername() : null;
        model.addAttribute("userInfo", userService.getUserCard(userId));
        model.addAttribute("isSelf", viewerId != null && viewerId.equals(userId));
        return "account/user/userInfo";
    }

    // 회원 목록
    @GetMapping("/list")
    public String userList() { return "account/user/userList"; }
}