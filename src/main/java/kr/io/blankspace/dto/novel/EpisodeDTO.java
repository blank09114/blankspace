package kr.io.blankspace.dto.novel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class EpisodeDTO {
    // 회차 목록 조회
    @Getter
    @AllArgsConstructor
    public static class ListItem {
        private Long id;
        private int displayNo;
        private String name;
        private LocalDateTime createdAt;
    }

    // 그룹핑
    @Getter
    @AllArgsConstructor
    public static class ChapterGroup {
        private String chapterTitle;
        private List<ListItem> episodes;
    }

    // 회차 상세 조회
    @Getter
    @AllArgsConstructor
    public static class DetailView {
        private final Long id;
        private final Integer novelId;
        private final String name;
        private final LocalDateTime createdAt;
        private final String content;
        private final String writerComment;

        private final int displayNo;
        private final Long prevEpisodeId;
        private final Long nextEpisodeId;
    }

    // 등록/수정 공통
    @Getter
    @Setter
    public static class Form {
        private String name;
        private String content;
        private String writerComment;
    }

    // 등록 응답
    @Getter
    @AllArgsConstructor
    public static class Created {  private final Long episodeId; }
}