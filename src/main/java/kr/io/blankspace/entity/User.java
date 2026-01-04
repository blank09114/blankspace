package kr.io.blankspace.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_tbl",
    uniqueConstraints = { @UniqueConstraint(name = "uk_user_mail", columnNames = "user_mail") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "user_mail", length = 255, nullable = false, unique = true)
    private String userMail;

    @Column(name = "user_name", length = 10, nullable = false)
    private String userName;

    @Column(name = "user_pw", length = 255, nullable = false)
    private String userPw;

    @Column(name = "pw_change_at")
    private LocalDateTime pwChangeAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", length = 5, nullable = false)
    private UserRole userRole;

    @Column(name = "user_date", nullable = false)
    private LocalDateTime userDate;

    @Column(name = "user_enabled", nullable = false)
    private boolean userEnabled;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "blocked_reason", length = 20)
    private String blockedReason;

    @PrePersist
    void prePersist() {
        if (userRole == null) userRole = UserRole.USER;
        if (userDate == null) userDate = LocalDateTime.now();
    }

    public void enable() { this.userEnabled = true; }

    public void changePassword(String encodedPw) {
        this.userPw = encodedPw;
        this.pwChangeAt = LocalDateTime.now();
    }

    public void block(String reason) {
        this.isBlocked = true;
        this.blockedReason = reason;
    }

    public void unblock() {
        this.isBlocked = false;
        this.blockedReason = null;
    }

    public enum UserRole { USER, ADMIN }

    public void withdrawAnonymize(PasswordEncoder encoder, String rawRandomPassword) {
        this.userEnabled = false;
        this.isBlocked = false;
        this.blockedReason = null;
        this.userName = "탈퇴한 사용자";
        this.changePassword(encoder.encode(rawRandomPassword));
        String suffix = java.util.UUID.randomUUID().toString().replace("-", "");
        this.userMail = "deleted_" + suffix + "@deleted.local";
    }

    public void changeName(String newName) { this.userName = newName; }
}