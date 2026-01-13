package kr.io.blankspace.api.board;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.CommentDTO;
import kr.io.blankspace.service.comment.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}/comment")
public class PostCommentAPI {
    private final PostCommentService postCommentService;

    // 댓글 조회
    @GetMapping
    public CommentDTO.ThreadPageRes getPostComments(
        @PathVariable Long postId, @RequestParam(defaultValue = "0") int page,
        Authentication authentication
    ) {
        String loginUserId = (authentication == null) ? null : authentication.getName();
        return postCommentService.getPostComments(postId, page, loginUserId);
    }

    // 댓글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.CreateRes createPostComment(
        @PathVariable Long postId, @Valid @RequestBody CommentDTO.CreateReq req,
        Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long commentId = postCommentService.createPostComment(postId, req.getContent(), loginUserId);
        return new CommentDTO.CreateRes(commentId);
    }

    // 대댓글 작성
    @PostMapping("/{commentId}/recomment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.RecommentCreateRes createPostRecomment(
        @PathVariable Long postId, @PathVariable Long commentId,
        @Valid @RequestBody CommentDTO.RecommentCreateReq req,
        Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long recommentId = postCommentService.createPostRecomment(
            postId,
            commentId,
            req.getContent(),
            loginUserId,
            req.getMentionUserId()
        );
        return new CommentDTO.RecommentCreateRes(recommentId);
    }

    // 원댓글 삭제
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostComment(
        @PathVariable Long postId, @PathVariable Long commentId,
        @RequestParam(defaultValue = "false") boolean force, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        postCommentService.deletePostComment(postId, commentId, loginUserId, force);
    }

    // 대댓글 삭제
    @DeleteMapping("/{commentId}/recomment/{recommentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostRecomment(
        @PathVariable Long postId, @PathVariable Long commentId,
        @PathVariable Long recommentId, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        postCommentService.deletePostRecomment(postId, commentId, recommentId, loginUserId);
    }
}