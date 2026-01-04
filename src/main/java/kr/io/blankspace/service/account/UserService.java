package kr.io.blankspace.service.account;

import kr.io.blankspace.domain.account.user.User;
import kr.io.blankspace.domain.account.user.UserRepository;
import kr.io.blankspace.dto.account.user.UserCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserCardDto getUserCard(String userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        return UserCardDto.from(user);
    }

    // 닉네임 변경
    @Transactional
    public UserCardDto changeMyName(String viewerId, String targetUserId, String newName) {
        if (viewerId == null) throw new IllegalStateException("로그인이 필요합니다.");
        if (!viewerId.equals(targetUserId)) throw new SecurityException("본인만 변경할 수 있습니다.");

        String trimmed = newName == null ? "" : newName.trim();
        if (trimmed.isBlank()) throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        if (trimmed.length() > 10) throw new IllegalArgumentException("닉네임은 10자 이하여야 합니다.");

        User user = userRepository.findById(targetUserId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + targetUserId));

        user.changeName(trimmed);

        return UserCardDto.from(user);
    }
}