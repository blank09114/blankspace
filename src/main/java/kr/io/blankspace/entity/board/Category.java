package kr.io.blankspace.entity.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "category_tbl",
    uniqueConstraints = @UniqueConstraint(name = "uk_category_board_name", columnNames = {"board_id", "category_name"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "category_name", nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    public static Category create(Board board, String name) {
        Category c = new Category();
        c.board = board;
        c.name = name;
        return c;
    }

    public void rename(String name) { this.name = name; }
}