package kr.io.blankspace.entity.comment;

import jakarta.persistence.*;
import kr.io.blankspace.entity.account.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "recomment_tbl")
public class Recomment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recomment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // 부모 원댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mention_user_id")
    private User mentionUser; // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    @Column(name = "recomment_timestamp", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "recomment_content", nullable = false, length = 500)
    private String content;

    @PrePersist
    private void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public static Recomment of(Comment comment, User author, User mentionUser, String content) {
        Recomment r = new Recomment();
        r.comment = comment;
        r.author = author;
        r.mentionUser = mentionUser;
        r.content = content;
        return r;
    }
}