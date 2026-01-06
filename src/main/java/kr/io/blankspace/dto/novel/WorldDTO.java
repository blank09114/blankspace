package kr.io.blankspace.dto.novel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class WorldDTO {
    // 목록 조회
    @Getter
    @AllArgsConstructor
    public static class ListItem {
        private final Long id;
        private final String category;
        private final String name;
        private final LocalDateTime createdAt;
    }

    // 상세 조회
    @Getter
    @Setter
    public static class Detail {
        private Long worldId;
        private Integer novelId;
        private String category;
        private String name;
        private String content;
        private LocalDateTime createdAt;
    }

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