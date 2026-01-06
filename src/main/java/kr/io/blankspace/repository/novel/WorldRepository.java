package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.World;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorldRepository extends JpaRepository<World, Long> {
    Optional<World> findByIdAndNovelId(Long worldId, Integer novelId);
    Optional<World> findTopByNovelIdAndIdLessThanOrderByIdDesc(Integer novelId, Long worldId);
    Optional<World> findTopByNovelIdAndIdGreaterThanOrderByIdAsc(Integer novelId, Long worldId);
}