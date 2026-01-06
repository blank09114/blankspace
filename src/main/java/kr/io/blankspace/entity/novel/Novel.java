package kr.io.blankspace.entity.novel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "novel_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Novel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id")
    private Integer id;

    @Column(name = "novel_type", nullable = false, length = 2)
    private String type;

    @Column(name = "novel_origin", length = 20)
    private String origin;

    @Column(name = "novel_name", nullable = false, length = 20)
    private String name;

    @Lob
    @Column(name = "novel_cover_url")
    private String coverUrl;

    @Lob
    @Column(name = "novel_intro")
    private String intro;

    @Column(name = "is_end", nullable = false)
    private boolean end;

    public static Novel create(String type, String origin, String name, String coverUrl, String intro) {
        Novel n = new Novel();
        n.type = type;
        n.origin = origin;
        n.name = name;
        n.coverUrl = coverUrl;
        n.intro = intro;
        n.end = false;
        return n;
    }

    public void update(String type, String origin, String name, String coverUrl, String intro, boolean end) {
        this.type = type;
        this.origin = origin;
        this.name = name;
        this.coverUrl = coverUrl;
        this.intro = intro;
        this.end = end;
    }

    public void toggleEnd() { this.end = !this.end; }
}