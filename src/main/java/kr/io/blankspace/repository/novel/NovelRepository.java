package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Integer> {
    List<Novel> findAllByOrderByIdDesc();
    List<Novel> findByTypeOrderByIdDesc(String type);
    List<Novel> findByIdIn(List<Integer> ids);
}