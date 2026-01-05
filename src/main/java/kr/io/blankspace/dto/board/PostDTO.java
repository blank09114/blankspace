package kr.io.blankspace.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// PostDTO.java
public class PostDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpsertReq {
        @NotNull
        private Integer categoryId;

        @NotBlank @Size(max = 20)
        private String title;

        @Size(max = 20)
        private String subTitle;

        private String thumbnailUrl;
        private String detailLink;

        @NotBlank
        private String content;
    }
}