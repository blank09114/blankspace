package kr.io.blankspace.entity.novel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "episode_tbl",
    indexes = @Index(name = "ix_episode_list", columnList = "novel_id, episode_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    @Column(name = "episode_name", nullable = false, length = 20)
    private String name;

    @Column(name = "episode_timestamp", nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "episode_content")
    private String content;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public static Episode create(Novel novel, String name, String content) {
        Episode e = new Episode();
        e.novel = novel;
        e.name = name;
        e.content = content;
        return e;
    }

    public void update(String name, String content) {
        this.name = name;
        this.content = content;
    }
}