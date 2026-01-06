package kr.io.blankspace.api.board;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.comment.CommentDTO;
import kr.io.blankspace.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}/comment")
public class PostCommentAPI {
    private final CommentService commentService;

    // 댓글 조회
    @GetMapping
    public Page<CommentDTO.ThreadItem> getPostComments(
        @PathVariable Long postId, @RequestParam(defaultValue = "0") int page,
        Authentication authentication
    ) {
        String loginUserId = null;
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            loginUserId = authentication.getName();
        }

        return commentService.getPostCommentThreads(postId, page, loginUserId);
    }

    // 댓글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.CreateRes createPostComment(
        @PathVariable Long postId, @Valid @RequestBody CommentDTO.CreateReq req,
        Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long commentId = commentService.createPostComment(postId, req.getContent(), loginUserId);
        return new CommentDTO.CreateRes(commentId);
    }

    // 대댓글 작성
    // 대댓글 작성
    @PostMapping("/{commentId}/recomment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.RecommentCreateRes createPostRecomment(
        @PathVariable Long postId, @PathVariable Long commentId,
        @Valid @RequestBody CommentDTO.RecommentCreateReq req, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long recommentId = commentService.createPostRecomment(
                postId, commentId,
                req.getContent(),
                req.getMentionUserId(),
                loginUserId
        );
        return new CommentDTO.RecommentCreateRes(recommentId);
    }
}