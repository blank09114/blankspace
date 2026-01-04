package kr.io.blankspace.dto.account.user;

import kr.io.blankspace.entity.User;

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