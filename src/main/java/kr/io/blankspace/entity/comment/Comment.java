package kr.io.blankspace.entity.comment;

import jakarta.persistence.*;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.board.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comment_tbl")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "episode_id")
    private Long episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment_timestamp", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "comment_content", nullable = false, length = 500)
    private String content;

    @PrePersist
    private void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public static Comment forPost(Post post, User user, String content) {
        Comment c = new Comment();
        c.post = post;
        c.episodeId = null;
        c.user = user;
        c.content = content;
        return c;
    }

    public static Comment forEpisode(Long episodeId, User user, String content) {
        Comment c = new Comment();
        c.post = null;
        c.episodeId = episodeId;
        c.user = user;
        c.content = content;
        return c;
    }
}