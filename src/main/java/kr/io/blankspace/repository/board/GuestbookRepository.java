package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Guestbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
    Page<Guestbook> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Guestbook g set g.answerAt = :answerAt, g.answerContent = :answerContent
        where g.id = :guestbookId
    """)
    int updateAnswer(
        @Param("guestbookId") Long guestbookId,
        @Param("answerAt") LocalDateTime answerAt,
        @Param("answerContent") String answerContent
    );
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Guestbook g set g.answerAt = null, g.answerContent = null
        where g.id = :guestbookId
    """)
    int clearAnswer(@Param("guestbookId") Long guestbookId);
    void deleteById(Long id);
}