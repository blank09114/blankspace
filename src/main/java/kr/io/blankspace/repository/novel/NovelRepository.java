package kr.io.blankspace.repository.novel;

import kr.io.blankspace.entity.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel, Integer> {
}