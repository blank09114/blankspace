package kr.io.blankspace.account.dto.auth;

public class PWResetRequestDTO {
    @jakarta.validation.constraints.NotBlank
    private String userMail;
    public String getUserMail() { return userMail; }
    public void setUserMail(String userMail) { this.userMail = userMail; }
}