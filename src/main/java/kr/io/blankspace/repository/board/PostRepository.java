package kr.io.blankspace.repository.board;

import kr.io.blankspace.dto.board.PostDTO;
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
        select new kr.io.blankspace.dto.board.PostDTO$ListItem(
            p.id, c.id, c.name,
            p.title, p.subTitle,
            p.thumbnailUrl, p.detailLink,
            p.createdAt
        )
        from Post p
        join p.category c
        join c.board b
        where b.name = :boardName
        order by p.id desc
    """)
    Page<PostDTO.ListItem> findPageByBoardName(@Param("boardName") String boardName, Pageable pageable);

    @Query("""
        select new kr.io.blankspace.dto.board.PostDTO$ListItem(
            p.id, c.id, c.name,
            p.title, p.subTitle,
            p.thumbnailUrl, p.detailLink,
            p.createdAt
        )
        from Post p
        join p.category c
        join c.board b
        where b.name = :boardName
          and c.id = :categoryId
        order by p.id desc
    """)
    Page<PostDTO.ListItem> findPageByBoardNameAndCategoryId(
        @Param("boardName") String boardName, @Param("categoryId") Integer categoryId,
        Pageable pageable
    );

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

    @Query("""
        select new kr.io.blankspace.dto.board.PostDTO$ListItem
        (p.id, c.id, c.name, p.title, p.subTitle, p.thumbnailUrl, p.detailLink, p.createdAt)
        from Post p
        join p.category c
        join c.board b
        where b.name = :boardName and p.createdAt >= :since
        order by p.id desc
    """)
    List<PostDTO.ListItem> findRecentListByBoardNameSince(
        @Param("boardName") String boardName, @Param("since") java.time.LocalDateTime since,
        org.springframework.data.domain.Pageable pageable
    );
}