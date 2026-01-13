package kr.io.blankspace.api.novel;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.CommentDTO;
import kr.io.blankspace.service.comment.EpisodeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/episode/{episodeId}/comment")
public class EpisodeCommentAPI {
    private final EpisodeCommentService episodeCommentService;

    // 댓글 조회
    @GetMapping
    public CommentDTO.ThreadPageRes getEpisodeComments(
        @PathVariable Long episodeId, @RequestParam(defaultValue = "0") int page,
        Authentication authentication
    ) {
        String loginUserId = (authentication == null) ? null : authentication.getName();
        return episodeCommentService.getEpisodeComments(episodeId, page, loginUserId);
    }

    // 댓글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.CreateRes createEpisodeComment(
        @PathVariable Long episodeId, @Valid @RequestBody CommentDTO.CreateReq req,
        Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long commentId = episodeCommentService.createEpisodeComment(episodeId, req.getContent(), loginUserId);
        return new CommentDTO.CreateRes(commentId);
    }

    // 대댓글 작성
    @PostMapping("/{commentId}/recomment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO.RecommentCreateRes createEpisodeRecomment(
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @Valid @RequestBody CommentDTO.RecommentCreateReq req, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        Long recommentId = episodeCommentService.createEpisodeRecomment(
            episodeId, commentId, req.getContent(),
            loginUserId, req.getMentionUserId()
        );
        return new CommentDTO.RecommentCreateRes(recommentId);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpisodeComment(
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @RequestParam(defaultValue = "false") boolean force, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        episodeCommentService.deleteEpisodeComment(episodeId, commentId, loginUserId, force);
    }

    // 대댓글 삭제
    @DeleteMapping("/{commentId}/recomment/{recommentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpisodeRecomment(
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId, Authentication authentication
    ) {
        String loginUserId = authentication.getName();
        episodeCommentService.deleteEpisodeRecomment(episodeId, commentId, recommentId, loginUserId);
    }
}