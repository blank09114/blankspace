package kr.io.blankspace.service;

import kr.io.blankspace.dto.board.PostDTO;
import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.entity.novel.Novel;
import kr.io.blankspace.repository.board.PostRepository;
import kr.io.blankspace.repository.novel.EpisodeRepository;
import kr.io.blankspace.repository.novel.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final EpisodeRepository episodeRepository;
    private final NovelRepository novelRepository;
    private final PostRepository postRepository;

    // 최근 업데이트된 소설
    @Transactional(readOnly = true)
    public List<NovelDTO.Card> getRecentlyUpdatedNovelsByEpisode(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<EpisodeRepository.NovelLatestEpisodeRow> rows = episodeRepository.findNovelIdsWithLatestEpisodeSince(since);

        if (rows.isEmpty()) return List.of();

        List<Integer> orderedNovelIds = rows.stream()
        .limit(limit).map(EpisodeRepository.NovelLatestEpisodeRow::getNovelId).toList();

        List<Novel> novels = novelRepository.findByIdIn(orderedNovelIds);

        Map<Integer, Novel> novelMap = novels.stream()
        .collect(Collectors.toMap(Novel::getId, n -> n));

        List<NovelDTO.Card> result = new ArrayList<>();
        for (Integer novelId : orderedNovelIds) {
            Novel n = novelMap.get(novelId);
            if (n == null) continue;

            long episodeCount = episodeRepository.countByNovelId(novelId);
            result.add(new NovelDTO.Card(n, episodeCount));
        }

        return result;
    }

    // 블로그 최신글
    @Transactional(readOnly = true)
    public List<PostDTO.ListItem> getRecentBlogPosts(int size) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, size);

        return postRepository.findRecentListByBoardNameSince("BLOG", since, pageable);
    }
}