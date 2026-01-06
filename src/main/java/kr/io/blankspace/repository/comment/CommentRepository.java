package kr.io.blankspace.repository.comment;

import kr.io.blankspace.entity.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPost_Id(Long postId);
    long countByEpisodeId(Long episodeId);

    // 원댓글 페이징 (ASC)
    Page<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId, Pageable pageable);
    Page<Comment> findByEpisodeIdOrderByCreatedAtAsc(Long episodeId, Pageable pageable);
}