package kr.io.blankspace.dto.account.user;

import kr.io.blankspace.domain.account.loginLog.LoginLog;

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