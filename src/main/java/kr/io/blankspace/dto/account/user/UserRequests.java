package kr.io.blankspace.dto.account.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class UserRequests {
    // 닉네임 변경
    @Getter @Setter
    public static class ChangeName {
        @NotBlank
        @Size(max = 10)
        private String userName;
    }

    // 차단
    @Getter @Setter
    public static class BlockToggle {
        @Size(max = 20)
        private String reason;
    }
}