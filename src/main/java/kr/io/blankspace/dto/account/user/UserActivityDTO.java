package kr.io.blankspace.dto.account.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserActivityDTO {
    @Getter
    @AllArgsConstructor
    public static class Item {
        private String type;
        private Long id;
        private String content;
        private LocalDateTime createdAt;

        private String targetUrl;
        private Long postId;
        private Long episodeId;
        private Integer novelId;
    }
}