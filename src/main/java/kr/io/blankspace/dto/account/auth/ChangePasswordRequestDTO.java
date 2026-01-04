package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePasswordRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
    private String currentPw;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
    private String newPw;
}