package kr.io.blankspace.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "token_tbl",
    indexes = { @Index(name = "idx_token_user_lookup", columnList = "user_id, expires_at, used_at") },
    uniqueConstraints = { @UniqueConstraint(name = "uk_token_hash", columnNames = "token_hash") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_token_user")
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", length = 8, nullable = false)
    private TokenType tokenType;

    @Column(name = "token_hash", length = 64, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public boolean isUsed() { return usedAt != null; }

    public boolean isExpired(LocalDateTime now) { return !expiresAt.isAfter(now); }

    public void markUsed() { this.usedAt = LocalDateTime.now(); }

    public enum TokenType { JOIN, RESET, WITHDRAW }
}