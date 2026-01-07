package kr.io.blankspace.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class GuestbookDTO {
    @Getter
    @AllArgsConstructor
    public static class ListItem {
        private Long guestbookId;

        private String userId;
        private String userName;

        private boolean secret;
        private String content;

        private LocalDateTime createdAt;

        private LocalDateTime answerAt;
        private String answerContent;

        private boolean mine;
        private boolean canDeleteGuestbook;
        private boolean canAnswer;
        private boolean canDeleteAnswer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReq {
        private boolean secret = false;

        @NotBlank
        @Size(max = 500)
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateRes {
        private Long guestbookId;

        private String userId;
        private String userName;

        private boolean secret;
        private String content;

        private LocalDateTime createdAt;
    }
}