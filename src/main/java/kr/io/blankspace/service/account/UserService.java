package kr.io.blankspace.service.account;

import kr.io.blankspace.repository.account.LoginLogRepository;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.dto.account.user.LoginLogDTO;
import kr.io.blankspace.dto.account.user.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserInfoDTO getUserCard(String userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        return UserInfoDTO.from(user);
    }

    // 닉네임 변경
    @Transactional
    public UserInfoDTO changeMyName(String viewerId, String targetUserId, String newName) {
        if (viewerId == null) throw new IllegalStateException("로그인이 필요합니다.");
        if (!viewerId.equals(targetUserId)) throw new SecurityException("본인만 변경할 수 있습니다.");

        String trimmed = newName == null ? "" : newName.trim();
        if (trimmed.isBlank()) throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        if (trimmed.length() > 10) throw new IllegalArgumentException("닉네임은 10자 이하여야 합니다.");

        User user = userRepository.findById(targetUserId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + targetUserId));

        user.changeName(trimmed);

        return UserInfoDTO.from(user);
    }

    // 차단/차단 해제
    @Transactional
    public UserInfoDTO toggleBlock(String targetUserId, String reasonOrNull) {
        User user = userRepository.findById(targetUserId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + targetUserId));

        if (user.getUserRole() == User.UserRole.ADMIN) { throw new IllegalArgumentException("관리자 계정은 차단할 수 없습니다."); }

        if (user.isBlocked()) { user.unblock(); return UserInfoDTO.from(user); }
        String reason = (reasonOrNull == null) ? "" : reasonOrNull.trim();
        if (reason.isBlank()) throw new IllegalArgumentException("차단 사유를 입력해주세요.");
        if (reason.length() > 20) throw new IllegalArgumentException("차단 사유는 20자 이하여야 합니다.");

        user.block(reason);
        return UserInfoDTO.from(user);
    }

    // 로그인 기록 조회
    @Transactional(readOnly = true)
    public Page<LoginLogDTO> getLoginLogs(UserDetails principal, String targetUserId, int page) {
        if (principal == null) throw new IllegalStateException("로그인이 필요합니다.");

        String viewerId = principal.getUsername();
        boolean isAdmin = principal.getAuthorities().stream()
        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!viewerId.equals(targetUserId) && !isAdmin)
        { throw new SecurityException("본인 또는 관리자만 접근할 수 있습니다."); }

        int safePage = Math.max(0, page);
        Pageable pageable = PageRequest.of(safePage, 10);

        return loginLogRepository
        .findByUser_UserIdOrderByLoginDateDesc(targetUserId, pageable).map(LoginLogDTO::from);
    }

    // 회원 목록 조회
    @Transactional(readOnly = true)
    public Page<UserInfoDTO> getUserList(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "userDate"));

        return userRepository.findAll(pageable).map(UserInfoDTO::from);
    }
}