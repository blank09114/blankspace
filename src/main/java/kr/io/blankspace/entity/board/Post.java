package kr.io.blankspace.entity.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_tbl", indexes = @Index(name = "ix_post_list_category", columnList = "category_id, post_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Lob
    @Column(name = "post_thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "post_title", nullable = false, length = 20)
    private String title;

    @Column(name = "post_sub_title", length = 20)
    private String subTitle;

    @Lob
    @Column(name = "post_dtl_link")
    private String detailLink;

    @Column(name = "post_timestamp", nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "post_content")
    private String content;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public static Post create(
            Category category,
            String title,
            String subTitle,
            String thumbnailUrl,
            String detailLink,
            String content
    ) {
        Post p = new Post();
        p.category = category;
        p.title = title;
        p.subTitle = subTitle;
        p.thumbnailUrl = thumbnailUrl;
        p.detailLink = detailLink;
        p.content = content;
        return p;
    }

    public void update(String title, String subTitle, String thumbnailUrl, String detailLink, String content) {
        this.title = title;
        this.subTitle = subTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.detailLink = detailLink;
        this.content = content;
    }
}
