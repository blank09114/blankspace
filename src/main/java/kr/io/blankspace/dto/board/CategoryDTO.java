package kr.io.blankspace.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CategoryDTO {
    // 공용
    @Getter
    @AllArgsConstructor
    public static class Basic {
        private Integer categoryId;
        private String name;
    }

    // 생성
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReq {
        @NotBlank
        @Size(max = 10)
        private String name;
    }

    // 이름 변경
    @Getter
    @Setter
    @NoArgsConstructor
    public static class EditReq {
        @NotBlank
        @Size(max = 10)
        private String name;
    }
}