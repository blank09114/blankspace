package kr.io.blankspace.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "login_log_tbl",
    indexes = { @Index(name = "ix_login_user", columnList = "user_id") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_log_id")
    private Long loginLogId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_hash", nullable = false, length = 64)
    private String loginHash;

    @Column(name = "login_ip", nullable = false, length = 255)
    private String loginIp;

    @Column(name = "login_region", nullable = false, length = 255)
    private String loginRegion;

    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;

    @Column(name = "logout_date")
    private LocalDateTime logoutDate;

    @PrePersist
    void prePersist() { if (loginDate == null) loginDate = LocalDateTime.now(); }

    public void markLogout() { if (this.logoutDate == null) this.logoutDate = LocalDateTime.now(); }
}