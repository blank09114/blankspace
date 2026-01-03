package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDTO {

    @NotBlank @Size(max = 20)
    private String userId;

    @NotBlank @Size(max = 10)
    private String userName;

    @NotBlank @Size(max = 255)
    private String userPw;

    @NotBlank @Email @Size(max = 255)
    private String userMail;
}