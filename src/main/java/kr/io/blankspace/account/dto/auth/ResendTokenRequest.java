package kr.io.blankspace.account.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendTokenRequest {
    @NotBlank
    @Email
    private String userMail;
}