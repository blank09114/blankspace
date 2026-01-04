package kr.io.blankspace.account.dto.user;

import kr.io.blankspace.account.entity.LoginLog;

import java.time.LocalDateTime;

public record LoginLogDTO(
    String loginIp,
    String loginRegion,
    LocalDateTime loginDate,
    LocalDateTime logoutDate
) {
    public static LoginLogDTO from(LoginLog log) {
        return new LoginLogDTO(
            log.getLoginIp(),
            log.getLoginRegion(),
            log.getLoginDate(),
            log.getLogoutDate()
        );
    }
}