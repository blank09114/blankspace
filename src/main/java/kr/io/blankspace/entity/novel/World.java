package kr.io.blankspace.entity.novel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "world_tbl",
        indexes = {
                @Index(name = "ix_world_list", columnList = "novel_id, world_id"),
                @Index(name = "ix_world_list_category", columnList = "novel_id, world_category, world_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class World {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "world_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    @Column(name = "world_category", nullable = false, length = 3)
    private String category; // '세계관' | '캐릭터' | '기타'

    @Column(name = "world_name", nullable = false, length = 20)
    private String name;

    @Column(name = "world_timestamp", nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "world_content")
    private String content;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public static World create(Novel novel, String category, String name, String content) {
        World w = new World();
        w.novel = novel;
        w.category = category;
        w.name = name;
        w.content = content;
        return w;
    }

    public void update(String category, String name, String content) {
        this.category = category;
        this.name = name;
        this.content = content;
    }
}