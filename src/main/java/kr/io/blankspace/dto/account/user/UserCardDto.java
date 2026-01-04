package kr.io.blankspace.dto.account.user;

public record UserCardDTO(
    String userId,
    String userName,
    String userMail,
    java.time.LocalDateTime userDate,
    boolean isBlocked,
    String blockedReason
) {
    public static UserCardDTO from(kr.io.blankspace.domain.account.user.User u) {
        return new UserCardDTO(
            u.getUserId(),
            u.getUserName(),
            u.getUserMail(),
            u.getUserDate(),
            u.isBlocked(),
            u.getBlockedReason()
        );
    }
}