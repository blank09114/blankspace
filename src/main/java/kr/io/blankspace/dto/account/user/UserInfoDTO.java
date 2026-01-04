package kr.io.blankspace.dto.account.user;

public record UserInfoDTO(
    String userId,
    String userName,
    String userMail,
    java.time.LocalDateTime userDate,
    boolean isBlocked,
    String blockedReason
) {
    public static UserInfoDTO from(kr.io.blankspace.domain.account.user.User u) {
        return new UserInfoDTO(
            u.getUserId(),
            u.getUserName(),
            u.getUserMail(),
            u.getUserDate(),
            u.isBlocked(),
            u.getBlockedReason()
        );
    }
}