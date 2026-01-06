package kr.io.blankspace.service.comment;

import kr.io.blankspace.dto.comment.CommentDTO;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.board.Post;
import kr.io.blankspace.entity.comment.Comment;
import kr.io.blankspace.entity.comment.Recomment;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.repository.board.PostRepository;
import kr.io.blankspace.repository.comment.CommentRepository;
import kr.io.blankspace.repository.comment.RecommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;

    // 댓글 조회
    @Transactional(readOnly = true)
    public Page<CommentDTO.ThreadItem> getPostCommentThreads(Long postId, int page, String loginUserId) {
        final int size = 10;
        int resolvedPage = resolvePageForLast(postId, page, size);
        Pageable pageable = PageRequest.of(resolvedPage, size);

        Page<Comment> comments =
        commentRepository.findByPost_IdOrderByCreatedAtAsc(postId, pageable);

        List<Long> commentIds = comments.getContent().stream()
        .map(Comment::getId).toList();

        List<Recomment> recomments = commentIds.isEmpty() ? Collections.emptyList()
        : recommentRepository.findByComment_IdInOrderByCreatedAtAsc(commentIds);

        Map<Long, List<Recomment>> recommentMap = recomments.stream()
        .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentDTO.ThreadItem> threads = comments.getContent().stream()
        .map(c -> CommentDTO.ThreadItem.builder()
        .comment(toCommentItem(c, loginUserId))
        .recomments(recommentMap.getOrDefault(c.getId(), List.of())
        .stream().map(r -> toRecommentItem(r, loginUserId)).toList())
        .build()).toList();

        return new PageImpl<>(threads, pageable, comments.getTotalElements());
    }

    // 댓글 작성
    @Transactional
    public Long createPostComment(Long postId, String content, String loginUserId) {

        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        validatePostPolicy(post);

        User user = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        validateContent(content);

        Comment saved = commentRepository.save(Comment.forPost(post, user, content.trim()));
        return saved.getId();
    }

    // page == -1 이면 마지막 페이지
    private int resolvePageForLast(Long postId, int page, int size) {
        if (page >= 0) return page;

        long total = commentRepository.countByPost_Id(postId);
        if (total <= 0) return 0;

        return (int) ((total - 1) / size);
    }

    // DTO 변환
    private CommentDTO.CommentItem toCommentItem(Comment c, String loginUserId) {
        User u = c.getUser();

        String writerId = u.getUserId();
        String writerName = u.getUserName();

        return CommentDTO.CommentItem.builder()
        .commentId(c.getId()).content(c.getContent()).writerId(writerId)
        .writerName(writerName).isMine(loginUserId != null && loginUserId.equals(writerId))
        .createdAt(c.getCreatedAt()).build();
    }

    private CommentDTO.RecommentItem toRecommentItem(Recomment r, String loginUserId) {
        User author = r.getAuthor();
        User mention = r.getMentionUser();

        String authorId = author.getUserId();
        String authorName = author.getUserName();

        String mentionId = mention == null ? null : mention.getUserId();
        String mentionName = mention == null ? null : mention.getUserName();

        return CommentDTO.RecommentItem.builder()
        .recommentId(r.getId()).commentId(r.getComment().getId()).content(r.getContent())
        .authorId(authorId).authorName(authorName).mentionUserId(mentionId)
        .mentionUserName(mentionName).isMine(loginUserId != null && loginUserId.equals(authorId))
        .createdAt(r.getCreatedAt()).build();
    }

    // 유효성 검사
    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) { throw new IllegalArgumentException("댓글 내용을 입력해주세요."); }
        if (content.length() > 500) { throw new IllegalArgumentException("댓글은 최대 500자까지 입력할 수 있습니다."); }
    }
    private void validatePostPolicy(Post post) {
        String boardName = post.getCategory().getBoard().getName();
        if (!"BLOG".equals(boardName)) { throw new IllegalStateException("이 게시판에는 댓글을 작성할 수 없습니다."); }
    }
}