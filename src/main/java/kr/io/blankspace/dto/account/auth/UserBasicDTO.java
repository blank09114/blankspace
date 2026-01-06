package kr.io.blankspace.dto.account.auth;

import kr.io.blankspace.entity.account.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBasicDTO {
    private final String userId;
    private final String userMail;
    private final String userName;
    private final String userRole;

    public static UserBasicDTO from(User u)
    { return new UserBasicDTO(u.getUserId(), u.getUserMail(), u.getUserName(), u.getUserRole().name()); }
}