package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    Optional<Episode> findByIdAndNovelId(Long episodeId, Integer novelId);
    long countByNovelIdAndIdLessThanEqual(Integer novelId, Long episodeId);
    Optional<Episode> findTopByNovelIdAndIdLessThanOrderByIdDesc(Integer novelId, Long episodeId);
    Optional<Episode> findTopByNovelIdAndIdGreaterThanOrderByIdAsc(Integer novelId, Long episodeId);
    long countByNovelId(Integer novelId);
    Optional<Episode> findTopByNovelIdOrderByIdDesc(Integer novelId);
    List<Episode> findByNovelIdOrderByIdAsc(Integer novelId);
}