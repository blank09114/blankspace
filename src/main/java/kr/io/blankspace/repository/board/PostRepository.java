package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        select p
        from Post p
        join fetch p.category c
        join fetch c.board b
        where p.id = :postId
    """)
    Optional<Post> findDetailById(@Param("postId") Long postId);

    Page<Post> findByCategory_Id(Long categoryId, Pageable pageable);

    @Query("""
        select p
        from Post p
        join p.category c
        join c.board b
        where b.name = :boardName
        and (:categoryId is null or c.id = :categoryId)
    """)
    Page<Post> findListByBoardName(
        @Param("boardName") String boardName,
        @Param("categoryId") Integer categoryId,
        Pageable pageable
    );

    boolean existsByCategory_Id(Integer categoryId);
}
