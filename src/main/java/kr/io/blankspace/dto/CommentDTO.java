package kr.io.blankspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    // 원댓글 아이템
    @Getter
    @Builder
    public static class CommentItem {
        private Long commentId;
        private String content;

        private String writerId;
        private String writerName;

        private boolean isMine;
        private LocalDateTime createdAt;

        boolean deleted;
    }

    // 대댓글 아이템
    @Getter
    @Builder
    public static class RecommentItem {
        private Long recommentId;
        private Long commentId;
        private String content;

        private String authorId;
        private String authorName;

        private String mentionUserId;
        private String mentionUserName;

        private boolean isMine;
        private LocalDateTime createdAt;
    }

    // 스레드
    @Getter
    @Builder
    public static class ThreadItem {
        private CommentItem comment;
        private List<RecommentItem> recomments;
    }

    // 작성
    @Getter
    @NoArgsConstructor
    public static class CreateReq {
        @NotBlank
        @Size(max = 500)
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateRes { private Long commentId; }

    // 대댓글 작성
    @Getter
    @NoArgsConstructor
    public static class RecommentCreateReq {
        @NotBlank
        @Size(max = 500)
        private String content;
        private String mentionUserId;
    }

    @Getter
    @AllArgsConstructor
    public static class RecommentCreateRes {
        private Long recommentId;
    }
}