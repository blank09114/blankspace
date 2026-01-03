package kr.io.blankspace.domain.account.loginLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update LoginLog l
            set l.logoutDate = :logoutAt
        where l.loginHash = :loginHash
            and l.logoutDate is null
    """)
    int markLogout(@Param("loginHash") String loginHash, @Param("logoutAt") LocalDateTime logoutAt);
}