package kr.io.blankspace.dto.account.auth;

public class PasswordResetRequestDTO {
    @jakarta.validation.constraints.NotBlank
    private String userMail;
    public String getUserMail() { return userMail; }
    public void setUserMail(String userMail) { this.userMail = userMail; }
}