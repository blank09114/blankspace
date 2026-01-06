package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
    interface NovelLatestEpisodeRow { Integer getNovelId(); LocalDateTime getLatestEpisodeAt(); }
    @Query("""
        select e.novel.id as novelId, max(e.createdAt) as latestEpisodeAt
        from Episode e
        where e.createdAt >= :since
        group by e.novel.id
        order by max(e.createdAt) desc
    """)
    List<NovelLatestEpisodeRow> findNovelIdsWithLatestEpisodeSince(@Param("since") LocalDateTime since);
}