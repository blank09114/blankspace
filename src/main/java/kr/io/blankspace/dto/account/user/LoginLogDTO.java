package kr.io.blankspace.dto.account.user;

import kr.io.blankspace.entity.account.LoginLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginLogDTO {
    private final String loginIp;
    private final String loginRegion;
    private final LocalDateTime loginDate;
    private final LocalDateTime logoutDate;

    public static LoginLogDTO from(LoginLog log)
    { return new LoginLogDTO(log.getLoginIp(), log.getLoginRegion(), log.getLoginDate(), log.getLogoutDate()); }
}