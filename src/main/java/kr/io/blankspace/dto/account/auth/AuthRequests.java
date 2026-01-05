package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class AuthRequests {
    // 회원가입
    @Getter @Setter
    public static class Join {
        @NotBlank @Size(max = 20)
        private String userId;

        @NotBlank @Size(max = 10)
        private String userName;

        @NotBlank @Size(max = 255)
        private String userPw;

        @NotBlank @Email @Size(max = 255)
        private String userMail;
    }

    // 로그인
    @Getter @Setter
    public static class Login {
        @NotBlank
        private String userId;

        @NotBlank
        private String userPw;
    }

    // 메일 발송
    @Getter @Setter
    public static class EmailRequest {
        @NotBlank @Email
        private String userMail;
    }

    // 비밀번호 변경
    @Getter @Setter
    public static class ChangePassword {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
        private String currentPw;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
        private String newPw;
    }

    // 회원 탈퇴
    @Getter @Setter
    public static class WithdrawRequest {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
        private String userPw;
    }
}