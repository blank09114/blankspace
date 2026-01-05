package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Optional<Board> findByName(String name);
}