package kr.io.blankspace.repository.board;

import kr.io.blankspace.entity.board.Guestbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
    Page<Guestbook> findAllByOrderByCreatedAtDesc(Pageable pageable);
}