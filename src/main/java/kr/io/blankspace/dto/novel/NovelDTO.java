package kr.io.blankspace.dto.novel;

import kr.io.blankspace.entity.novel.Novel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class NovelDTO {
    // 등록/수정 공용
    @Getter
    @Setter
    public static class Form {
        private String type;
        private String origin;
        private String name;
        private String coverUrl;
        private String intro;
        private boolean end;
    }

    // 등록 결과
    @Getter
    public static class Created {
        private final Integer novelId;
        public Created(Integer novelId) { this.novelId = novelId; }
    }

    // 소설 카드
    @Getter
    public static class Card {
        private final Integer novelId;
        private final String type;
        private final String origin;
        private final String name;
        private final String coverUrl;
        private final String intro;
        private final long episodeCount;
        private final boolean end;

        public Card(Novel novel, long episodeCount) {
            this.novelId = novel.getId();
            this.type = novel.getType();
            this.origin = novel.getOrigin();
            this.name = novel.getName();
            this.coverUrl = novel.getCoverUrl();
            this.intro = novel.getIntro();
            this.episodeCount = episodeCount;
            this.end = novel.isEnd();
        }
    }
}