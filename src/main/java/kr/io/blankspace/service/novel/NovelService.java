package kr.io.blankspace.service.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.entity.novel.Novel;
import kr.io.blankspace.repository.novel.EpisodeRepository;
import kr.io.blankspace.repository.novel.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NovelService {
    private final NovelRepository novelRepository;
    private final EpisodeRepository episodeRepository;

    // 소설 카드 조회
    @Transactional(readOnly = true)
    public NovelDTO.Card getCard(Integer novelId) {
        Novel novel = novelRepository.findById(novelId)
        .orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다. id=" + novelId));

        long episodeCount = episodeRepository.countByNovelId(novelId);

        return new NovelDTO.Card(novel, episodeCount);
    }

    // 소설 등록
    @Transactional
    public NovelDTO.Created create(NovelDTO.Form form) {
        String origin = normalizeOrigin(form.getType(), form.getOrigin());

        Novel novel = Novel.create(
            form.getType(), origin, form.getName(),
            form.getCoverUrl(), form.getIntro()
        );

        Novel saved = novelRepository.save(novel);
        return new NovelDTO.Created(saved.getId());
    }

    // 소설 수정 페이지용 폼 조회
    @Transactional(readOnly = true)
    public NovelDTO.Form getForm(Integer novelId) {

        Novel novel = novelRepository.findById(novelId)
        .orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다. id=" + novelId));

        NovelDTO.Form form = new NovelDTO.Form();
        form.setType(novel.getType());
        form.setOrigin(novel.getOrigin());
        form.setName(novel.getName());
        form.setCoverUrl(novel.getCoverUrl());
        form.setIntro(novel.getIntro());
        form.setEnd(novel.isEnd());

        return form;
    }

    // 소설 수정 저장
    @Transactional
    public void update(Integer novelId, NovelDTO.Form form) {
        Novel novel = novelRepository.findById(novelId)
        .orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다. id=" + novelId));

        String origin = normalizeOrigin(form.getType(), form.getOrigin());

        novel.update(
            form.getType(), origin, form.getName(),
            form.getCoverUrl(), form.getIntro(), form.isEnd()
        );
    }

    // 1차면 origin은 null로 통일
    private String normalizeOrigin(String type, String origin) {

        if ("1차".equals(type)) return null;
        if (origin == null) return null;

        String t = origin.trim();
        return t.isEmpty() ? null : t;
    }
}