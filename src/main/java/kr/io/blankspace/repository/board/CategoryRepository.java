package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByBoard_IdOrderByIdAsc(Integer boardId);
    Optional<Category> findByBoard_IdAndName(Integer boardId, String name);
}