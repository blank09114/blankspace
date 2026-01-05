package kr.io.blankspace.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReq {
        @NotNull
        private Integer categoryId;

        @NotBlank
        @Size(max = 20)
        private String title;

        @Size(max = 20)
        private String subTitle;

        // 썸네일 URL (S3 업로드 후 url)
        private String thumbnailUrl;

        // GIT 링크 등
        private String detailLink;

        @NotBlank
        private String content;
    }

    @Getter
    public static class CreateRes {
        private final Long postId;
        public CreateRes(Long postId) { this.postId = postId; }
    }
}