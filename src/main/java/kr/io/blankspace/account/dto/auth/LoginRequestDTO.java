package kr.io.blankspace.account.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String userPw;
}