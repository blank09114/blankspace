package kr.io.blankspace.service.comment;

import kr.io.blankspace.dto.CommentDTO;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentCommonService common;

    // 조회
    @Transactional(readOnly = true)
    public Page<CommentDTO.ThreadItem> getPostComments(Long postId, int page, String loginUserId) {
        final int size = 10;

        int resolvedPage = resolvePageForLast(postId, page, size);
        Pageable pageable = PageRequest.of(resolvedPage, size);

        Page<Comment> commentPage =
        commentRepository.findByPost_IdOrderByCreatedAtAsc(postId, pageable);

        List<Comment> comments = commentPage.getContent();
        if (comments.isEmpty())
        { return new PageImpl<>(List.of(), pageable, commentPage.getTotalElements()); }

        List<Long> commentIds = comments.stream().map(Comment::getId).toList();

        List<Recomment> recomments = commentIds.isEmpty() ? Collections.emptyList()
        : recommentRepository.findByComment_IdInOrderByCreatedAtAsc(commentIds);

        Map<Long, List<Recomment>> recommentMap = recomments.stream()
        .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentDTO.ThreadItem> threads = comments.stream()
        .map(c -> CommentDTO.ThreadItem.builder()
        .comment(common.toCommentItem(c, loginUserId))
        .recomments(
            recommentMap.getOrDefault(c.getId(), List.of()).stream()
            .map(r -> common.toRecommentItem(r, loginUserId)).toList()
        ).build()).toList();

        return new PageImpl<>(threads, pageable, commentPage.getTotalElements());
    }

    // page == -1 이면 마지막 페이지
    private int resolvePageForLast(Long postId, int page, int size) {
        if (page >= 0) return page;

        long total = commentRepository.countByPost_Id(postId);
        if (total <= 0) return 0;

        return (int) ((total - 1) / size);
    }

    // 작성
    @Transactional
    public Long createPostComment(Long postId, String content, String loginUserId) {
        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        validateBlogPost(post);

        User writer = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        Comment comment = Comment.forPost(post, writer, content.trim());
        return common.createComment(comment);
    }

    @Transactional
    public Long createPostRecomment
    (Long postId, Long commentId, String content, String loginUserId, String mentionUserId) {
        Comment parent = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("원댓글이 존재하지 않습니다."));

        if (parent.getPost() == null || !parent.getPost().getId().equals(postId))
        { throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다."); }

        validateBlogPost(parent.getPost());

        User mentionUser = null;
        if (mentionUserId != null && !mentionUserId.trim().isEmpty()) {
            mentionUser = userRepository.findById(mentionUserId.trim())
            .orElseThrow(() -> new IllegalArgumentException("언급 대상 사용자를 찾을 수 없습니다."));
        }

        return common.createRecomment(parent, content, loginUserId, mentionUser);
    }

    private void validateBlogPost(Post post) {
        String boardName = post.getCategory().getBoard().getName();
        if (!"BLOG".equals(boardName))
        { throw new IllegalStateException("이 게시판에는 댓글을 작성할 수 없습니다."); }
    }

    // 삭제
    @Transactional
    public void deletePostComment(Long postId, Long commentId, String loginUserId, boolean force) {
        Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (comment.getPost() == null || !comment.getPost().getId().equals(postId))
        { throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다."); }

        common.deleteComment(comment, loginUserId, force);
    }

    @Transactional
    public void deletePostRecomment(Long postId, Long commentId, Long recommentId, String loginUserId) {
        Recomment r = recommentRepository.findById(recommentId)
        .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));

        Comment parent = r.getComment();
        if (!parent.getId().equals(commentId) || parent.getPost() == null || !parent.getPost().getId().equals(postId))
        { throw new IllegalArgumentException("잘못된 요청입니다."); }

        common.deleteRecomment(r, loginUserId);
    }
}