package kr.io.blankspace.entity.board;

import jakarta.persistence.*;
import kr.io.blankspace.entity.account.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "guestbook_tbl",
    indexes =
    { @Index(name = "guestbook_user_list", columnList = "guestbook_user_id, guestbook_timestamp") })
public class Guestbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guestbook_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guestbook_user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "guestbook_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "guestbook_is_secret", nullable = false)
    private boolean secret;

    @Column(name = "guestbook_content", nullable = false, length = 500)
    private String content;

    @Column(name = "answer_timestamp")
    private LocalDateTime answerAt;

    @Lob
    @Column(name = "answer_content")
    private String answerContent;

    public Guestbook(User user, boolean secret, String content) {
        this.user = user;
        this.secret = secret;
        this.content = content;
    }

    public void setAnswer(LocalDateTime answerAt, String answerContent) {
        this.answerAt = answerAt;
        this.answerContent = answerContent;
    }
}