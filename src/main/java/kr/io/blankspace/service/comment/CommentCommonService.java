package kr.io.blankspace.service.comment;

import kr.io.blankspace.dto.comment.CommentDTO;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.comment.Comment;
import kr.io.blankspace.entity.comment.Recomment;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.repository.comment.CommentRepository;
import kr.io.blankspace.repository.comment.RecommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommonService {
    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final UserRepository userRepository;

    // 작성
    @Transactional
    public Long createComment(Comment comment) {
        validateContent(comment.getContent());
        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long createRecomment(Comment parent, String content, String loginUserId, User mentionUser) {
        if (parent.isDeleted())
        { throw new IllegalStateException("삭제된 댓글에는 답글을 달 수 없습니다."); }

        validateContent(content);

        User author = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        Recomment r = Recomment.of(parent, author, mentionUser, content.trim());
        recommentRepository.save(r);
        return r.getId();
    }

    // 삭제
    @Transactional
    public void deleteComment(Comment comment, String loginUserId, boolean force) {
        boolean isAdmin = isAdmin();
        boolean isMine = comment.getUser().getUserId().equals(loginUserId);

        if (!isMine && !isAdmin) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 관리자 슈퍼 삭제
        if (isAdmin && force) {
            recommentRepository.deleteByComment_Id(comment.getId());
            commentRepository.delete(comment);
            return;
        }

        // 일반 삭제 정책
        long childCount = recommentRepository.countByComment_Id(comment.getId());
        if (childCount == 0) { commentRepository.delete(comment); } else { comment.softDelete(); }
    }

    @Transactional
    public void deleteRecomment(Recomment r, String loginUserId) {
        boolean isAdmin = isAdmin();
        boolean isMine = r.getAuthor().getUserId().equals(loginUserId);

        if (!isMine && !isAdmin) { throw new IllegalStateException("삭제 권한이 없습니다."); }

        recommentRepository.delete(r);
    }

    // DTO 변환
    public CommentDTO.CommentItem toCommentItem(Comment c, String loginUserId) {
        User u = c.getUser();

        String writerId = u.getUserId();
        String writerName = u.getUserName();

        return CommentDTO.CommentItem.builder()
        .commentId(c.getId()).content(c.getContent()).writerId(writerId)
        .writerName(writerName).isMine(loginUserId != null && loginUserId.equals(writerId))
        .deleted(c.isDeleted()).createdAt(c.getCreatedAt()).build();
    }

    public CommentDTO.RecommentItem toRecommentItem(Recomment r, String loginUserId) {
        User author = r.getAuthor();
        User mention = r.getMentionUser();

        String authorId = author.getUserId();
        String authorName = author.getUserName();

        String mentionId = (mention == null) ? null : mention.getUserId();
        String mentionName = (mention == null) ? null : mention.getUserName();

        return CommentDTO.RecommentItem.builder()
        .recommentId(r.getId()).commentId(r.getComment().getId()).content(r.getContent())
        .authorId(authorId).authorName(authorName).mentionUserId(mentionId)
        .mentionUserName(mentionName).isMine(loginUserId != null && loginUserId.equals(authorId))
        .createdAt(r.getCreatedAt()).build();
    }

    // 유틸
    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty())
        { throw new IllegalArgumentException("댓글 내용을 입력해주세요."); }
        if (content.length() > 500)
        { throw new IllegalArgumentException("댓글은 최대 500자까지 입력할 수 있습니다."); }
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}