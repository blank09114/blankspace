package kr.io.blankspace.service.comment;

import kr.io.blankspace.dto.CommentDTO;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.comment.Comment;
import kr.io.blankspace.entity.comment.Recomment;
import kr.io.blankspace.entity.novel.Episode;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.repository.comment.CommentRepository;
import kr.io.blankspace.repository.comment.RecommentRepository;
import kr.io.blankspace.repository.novel.EpisodeRepository;
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
public class EpisodeCommentService {
    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final EpisodeRepository episodeRepository;
    private final UserRepository userRepository;
    private final CommentCommonService common;

    // 조회
    @Transactional(readOnly = true)
    public CommentDTO.ThreadPageRes getEpisodeComments(Long episodeId, int page, String loginUserId) {
        final int size = 10;

        if (!episodeRepository.existsById(episodeId))
        { throw new IllegalArgumentException("회차가 존재하지 않습니다. id=" + episodeId); }

        int resolvedPage = resolvePageForLast(episodeId, page, size);
        Pageable pageable = PageRequest.of(resolvedPage, size);

        Page<Comment> commentPage = commentRepository.findByEpisodeIdOrderByCreatedAtAsc(episodeId, pageable);

        List<Comment> comments = commentPage.getContent();
        List<Long> commentIds = comments.stream().map(Comment::getId).toList();

        List<Recomment> recomments = commentIds.isEmpty()
        ? Collections.emptyList() : recommentRepository.findByComment_IdInOrderByCreatedAtAsc(commentIds);

        Map<Long, List<Recomment>> recommentMap = recomments.stream()
        .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentDTO.ThreadItem> threads = comments.stream()
        .map(c -> CommentDTO.ThreadItem.builder()
        .comment(common.toCommentItem(c, loginUserId))
        .recomments(
            recommentMap.getOrDefault(c.getId(), List.of()).stream()
            .map(r -> common.toRecommentItem(r, loginUserId)).toList()
        ).build()).toList();

        long parentCount = commentPage.getTotalElements();
        long childCount  = recommentRepository.countByComment_EpisodeId(episodeId);
        long totalCount  = parentCount + childCount;

        return new CommentDTO.ThreadPageRes(
            threads, commentPage.getNumber(), commentPage.getTotalPages(),
            parentCount, totalCount
        );
    }

    // page == -1 이면 마지막 페이지
    private int resolvePageForLast(Long episodeId, int page, int size) {
        if (page >= 0) return page;

        long total = commentRepository.countByEpisodeId(episodeId);
        if (total <= 0) return 0;

        return (int) ((total - 1) / size);
    }

    // 작성
    @Transactional
    public Long createEpisodeComment(Long episodeId, String content, String loginUserId) {
        // FK 기준으로 존재 체크
        Episode episode = episodeRepository.findById(episodeId)
        .orElseThrow(() -> new IllegalArgumentException("회차가 존재하지 않습니다. id=" + episodeId));

        User writer = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        Comment comment = Comment.forEpisode(episode.getId(), writer, content.trim());

        return common.createComment(comment);
    }

    @Transactional
    public Long createEpisodeRecomment
    (Long episodeId, Long commentId, String content, String loginUserId, String mentionUserId) {
        // 존재 체크
        if (!episodeRepository.existsById(episodeId))
        { throw new IllegalArgumentException("회차가 존재하지 않습니다. id=" + episodeId); }

        Comment parent = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("원댓글이 존재하지 않습니다."));

        // 이 회차의 댓글이 맞는지 검증
        if (parent.getEpisodeId() == null || !parent.getEpisodeId().equals(episodeId))
        { throw new IllegalArgumentException("해당 회차의 댓글이 아닙니다."); }

        User mentionUser = null;
        if (mentionUserId != null && !mentionUserId.trim().isEmpty()) {
            mentionUser = userRepository.findById(mentionUserId.trim())
            .orElseThrow(() -> new IllegalArgumentException("언급 대상 사용자를 찾을 수 없습니다."));
        }

        return common.createRecomment(parent, content, loginUserId, mentionUser);
    }

    // 삭제
    @Transactional
    public void deleteEpisodeComment(Long episodeId, Long commentId, String loginUserId, boolean force) {
        Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (comment.getEpisodeId() == null || !comment.getEpisodeId().equals(episodeId))
        { throw new IllegalArgumentException("해당 회차의 댓글이 아닙니다."); }

        common.deleteComment(comment, loginUserId, force);
    }

    @Transactional
    public void deleteEpisodeRecomment(Long episodeId, Long commentId, Long recommentId, String loginUserId) {
        Recomment r = recommentRepository.findById(recommentId)
        .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));

        Comment parent = r.getComment();

        if (!parent.getId().equals(commentId) || parent.getEpisodeId() == null
        || !parent.getEpisodeId().equals(episodeId))
        { throw new IllegalArgumentException("잘못된 요청입니다."); }

        common.deleteRecomment(r, loginUserId);
    }
}