package kr.io.blankspace.service.novel;

import kr.io.blankspace.dto.novel.EpisodeDTO;
import kr.io.blankspace.entity.novel.Episode;
import kr.io.blankspace.entity.novel.Novel;
import kr.io.blankspace.repository.novel.EpisodeRepository;
import kr.io.blankspace.repository.novel.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final NovelRepository novelRepository;

    // 회차 목록 조회
    @Transactional(readOnly = true)
    public List<EpisodeDTO.ChapterGroup> getGroupedEpisodeList(Integer novelId) {
        List<Episode> episodesAsc = episodeRepository.findByNovelIdOrderByIdAsc(novelId);
        Map<Long, Integer> noMap = new HashMap<>();
        for (int i = 0; i < episodesAsc.size(); i++)
        { noMap.put(episodesAsc.get(i).getId(), i + 1); }

        List<Episode> episodesDesc = new ArrayList<>(episodesAsc);
        Collections.reverse(episodesDesc);

        List<EpisodeDTO.ChapterGroup> groups = new ArrayList<>();
        List<EpisodeDTO.ListItem> current = new ArrayList<>();
        String currentTitle = null;

        for (Episode e : episodesDesc)
        {
            String chapterTitle = e.getName();
            int displayNo = noMap.get(e.getId());

            EpisodeDTO.ListItem item = new EpisodeDTO.ListItem
            (e.getId(), displayNo, chapterTitle, e.getCreatedAt());

            if (currentTitle == null) {
                currentTitle = chapterTitle;
                current.add(item);
                continue;
            }

            if (currentTitle.equals(chapterTitle))
            { current.add(item); } else {
                groups.add(new EpisodeDTO.ChapterGroup(currentTitle, current));
                currentTitle = chapterTitle;
                current = new ArrayList<>();
                current.add(item);
            }
        }

        if (currentTitle != null)
        { groups.add(new EpisodeDTO.ChapterGroup(currentTitle, current)); }

        return groups;
    }

    // 회차 상세
    @Transactional(readOnly = true)
    public EpisodeDTO.DetailView getDetailView(Integer novelId, Long episodeId) {
        Episode episode = episodeRepository.findByIdAndNovelId(episodeId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다. novelId=" + novelId + ", episodeId=" + episodeId));

        int displayNo = (int) episodeRepository.countByNovelIdAndIdLessThanEqual(novelId, episodeId);

        Long prevId = episodeRepository.findTopByNovelIdAndIdLessThanOrderByIdDesc(novelId, episodeId)
        .map(Episode::getId).orElse(null);

        Long nextId = episodeRepository.findTopByNovelIdAndIdGreaterThanOrderByIdAsc(novelId, episodeId)
        .map(Episode::getId).orElse(null);

        return new EpisodeDTO.DetailView(
            episode.getId(), novelId, episode.getName(), episode.getCreatedAt(),
            episode.getContent(), episode.getWriterComment(),
            displayNo, prevId, nextId
        );
    }

    // 이전 회차 소제목 가져오기
    @Transactional(readOnly = true)
    public String getLatestEpisodeNameOrNull(Integer novelId) {
        return episodeRepository.findTopByNovelIdOrderByIdDesc(novelId)
        .map(Episode::getName).orElse(null);
    }

    // 회차 등록
    @Transactional
    public EpisodeDTO.Created create(Integer novelId, EpisodeDTO.Form form) {
        Novel novel = novelRepository.findById(novelId)
        .orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다. id=" + novelId));

        String name = normalize(form.getName());
        if (name == null) throw new IllegalArgumentException("회차 제목은 필수입니다.");
        if (name.length() > 20) throw new IllegalArgumentException("회차 제목은 20자 이하여야 합니다.");

        String content = normalize(form.getContent());
        if (content == null) throw new IllegalArgumentException("본문은 필수입니다.");

        String writerComment = normalize(form.getWriterComment());

        Episode episode = Episode.create(novel, name, content, writerComment);
        Episode saved = episodeRepository.save(episode);

        return new EpisodeDTO.Created(saved.getId());
    }

    // 초기 값 바인딩
    @Transactional(readOnly = true)
    public EpisodeDTO.Form getFormForEdit(Integer novelId, Long episodeId) {
        Episode episode = episodeRepository.findByIdAndNovelId(episodeId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다. novelId=" + novelId + ", episodeId=" + episodeId));

        EpisodeDTO.Form form = new EpisodeDTO.Form();
        form.setName(episode.getName());
        form.setContent(episode.getContent());
        form.setWriterComment(episode.getWriterComment());
        return form;
    }

    // 수정
    @Transactional
    public void update(Integer novelId, Long episodeId, EpisodeDTO.Form form) {
        Episode episode = episodeRepository.findByIdAndNovelId(episodeId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다. novelId=" + novelId + ", episodeId=" + episodeId));

        String name = normalize(form.getName());
        if (name == null) throw new IllegalArgumentException("회차 제목은 필수입니다.");
        if (name.length() > 20) throw new IllegalArgumentException("회차 제목은 20자 이하여야 합니다.");

        String content = normalize(form.getContent());
        if (content == null) throw new IllegalArgumentException("본문은 필수입니다.");

        String writerComment = normalize(form.getWriterComment());

        episode.update(name, content, writerComment);
    }

    // 삭제
    @Transactional
    public void delete(Integer novelId, Long episodeId) {
        Episode episode = episodeRepository.findByIdAndNovelId(episodeId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다. novelId=" + novelId + ", episodeId=" + episodeId));

        episodeRepository.delete(episode);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // 이전화
    @Transactional(readOnly = true)
    public Episode getPrevEpisode(Integer novelId, Long episodeId)
    { return episodeRepository.findTopByNovelIdAndIdLessThanOrderByIdDesc(novelId, episodeId).orElse(null); }

    // 다음화
    @Transactional(readOnly = true)
    public Episode getNextEpisode(Integer novelId, Long episodeId)
    { return episodeRepository.findTopByNovelIdAndIdGreaterThanOrderByIdAsc(novelId, episodeId).orElse(null); }
}