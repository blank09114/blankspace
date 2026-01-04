package kr.io.blankspace.account.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePWRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
    private String currentPw;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,40}$")
    private String newPw;
}