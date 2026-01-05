package kr.io.blankspace.dto.account.user;

import kr.io.blankspace.dto.account.auth.UserBasicDTO;
import kr.io.blankspace.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoDTO {
    private final UserBasicDTO user;
    private final LocalDateTime userDate;
    private final boolean blocked;
    private final String blockedReason;

    public static UserInfoDTO from(User u) {
        return new UserInfoDTO(
                UserBasicDTO.from(u),
                u.getUserDate(),
                u.isBlocked(),
                u.getBlockedReason()
        );
    }
}