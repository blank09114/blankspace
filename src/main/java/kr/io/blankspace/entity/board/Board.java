package kr.io.blankspace.entity.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Integer id;

    @Column(name = "board_name", nullable = false, unique = true, length = 10)
    private String name;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();
}