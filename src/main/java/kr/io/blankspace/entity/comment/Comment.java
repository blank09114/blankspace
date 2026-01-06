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

    @Column(name = "comment_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "comment_deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    private void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public void softDelete() {
        if (this.deleted) return;
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.content = "삭제된 댓글입니다";
    }

    public boolean isDeleted() { return deleted; }

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