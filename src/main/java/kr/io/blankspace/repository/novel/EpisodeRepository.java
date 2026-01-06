package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    long countByNovelId(Integer novelId);
}