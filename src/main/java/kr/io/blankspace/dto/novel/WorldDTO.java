package kr.io.blankspace.dto.novel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class WorldDTO {
    // 등록/수정 공통 폼
    @Getter
    @Setter
    public static class Form {
        private String category;
        private String name;
        private String content;
    }

    // 등록 결과
    @Getter
    @AllArgsConstructor
    public static class Created { private final Long worldId; }
}