package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class WithdrawRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
    private String userPw;
}