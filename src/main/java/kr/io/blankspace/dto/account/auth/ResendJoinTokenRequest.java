package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendJoinTokenRequest {
    @NotBlank
    @Email
    private String userMail;
}