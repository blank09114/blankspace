package kr.io.blankspace.account.repository;

import kr.io.blankspace.account.entity.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update LoginLog l
            set l.logoutDate = :logoutAt
        where l.loginHash = :loginHash
            and l.logoutDate is null
    """)
    int markLogoutIfNotExists(@Param("loginHash") String loginHash, @Param("logoutAt") LocalDateTime logoutAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from LoginLog l
            where l.logoutDate is not null
                and l.logoutDate < :cutoff
    """)
    int deleteEndedBefore(@Param("cutoff") LocalDateTime cutoff);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update LoginLog l
       set l.logoutDate = :now
     where l.logoutDate is null
""")
    int markAllActiveAsRestarted(@Param("now") LocalDateTime now);

    Page<LoginLog> findByUser_UserIdOrderByLoginDateDesc(String userId, Pageable pageable);
}