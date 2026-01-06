package kr.io.blankspace.repository.comment;

import kr.io.blankspace.entity.comment.Recomment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {
    // 대댓글 조회
    List<Recomment> findByComment_IdInOrderByCreatedAtAsc(List<Long> commentIds);
}
