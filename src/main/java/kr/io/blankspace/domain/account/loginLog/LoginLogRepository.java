package kr.io.blankspace.domain.account.loginLog;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    // 로그인 로그
    Optional<LoginLog> findTopByUser_UserIdOrderByLoginDateDesc(String userId);

    // 활성 세션 로그 찾기
    Optional<LoginLog> findByLoginHashAndLogoutDateIsNull(String loginHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update LoginLog l
            set l.logoutDate = :logoutAt
        where l.loginHash = :loginHash
            and l.logoutDate is null
    """)
    int markLogoutByHash(@Param("loginHash") String loginHash, @Param("logoutAt") LocalDateTime logoutAt);
}