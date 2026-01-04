package kr.io.blankspace.account.dto.user;

import kr.io.blankspace.account.entity.User;

public record UserInfoDTO(
    String userId,
    String userName,
    String userMail,
    java.time.LocalDateTime userDate,
    boolean isBlocked,
    String blockedReason
) {
    public static UserInfoDTO from(User u) {
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