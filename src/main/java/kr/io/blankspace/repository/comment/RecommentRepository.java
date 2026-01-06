package kr.io.blankspace.repository.comment;

import kr.io.blankspace.entity.comment.Recomment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {
    long countByComment_Id(Long commentId);

    List<Recomment> findByComment_IdInOrderByCreatedAtAsc(List<Long> commentIds);
    void deleteByComment_Id(Long commentId);
}