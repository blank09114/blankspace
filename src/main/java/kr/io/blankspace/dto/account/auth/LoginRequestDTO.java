package kr.io.blankspace.dto.account.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String userPw;
}