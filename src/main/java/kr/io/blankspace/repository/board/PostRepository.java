package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        select p from Post p
        join fetch p.category c
        join fetch c.board b
        where b.name = :boardName and p.id = :postId
    """)
    Optional<Post> findDetailByBoardNameAndId(@Param("boardName") String boardName, @Param("postId") Long postId);

    @Query("""
        select p from Post p
        join p.category c
        join c.board b
        where b.name = :boardName and p.id < :postId
        order by p.id desc
    """)
    List<Post> findPrev
    (@Param("boardName") String boardName, @Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);

    @Query("""
        select p from Post p
        join p.category c
        join c.board b
        where b.name = :boardName and p.id > :postId
        order by p.id asc
    """)
    List<Post> findNext
    (@Param("boardName") String boardName, @Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);

    boolean existsByCategory_Id(Integer categoryId);
}