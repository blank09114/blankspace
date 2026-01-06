package kr.io.blankspace.service.novel;

import kr.io.blankspace.dto.novel.WorldDTO;
import kr.io.blankspace.entity.novel.Novel;
import kr.io.blankspace.entity.novel.World;
import kr.io.blankspace.repository.novel.NovelRepository;
import kr.io.blankspace.repository.novel.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorldService {
    private final WorldRepository worldRepository;
    private final NovelRepository novelRepository;

    // 설정 등록
    @Transactional
    public WorldDTO.Created create(Integer novelId, WorldDTO.Form form) {
        Novel novel = novelRepository.findById(novelId)
        .orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다. id=" + novelId));

        String category = normalize(form.getCategory());
        if (category == null) throw new IllegalArgumentException("카테고리는 필수입니다.");
        if (!(category.equals("세계관") || category.equals("캐릭터") || category.equals("기타")))
            throw new IllegalArgumentException("카테고리가 올바르지 않습니다.");

        String name = normalize(form.getName());
        if (name == null) throw new IllegalArgumentException("제목은 필수입니다.");
        if (name.length() > 20) throw new IllegalArgumentException("제목은 20자 이하여야 합니다.");

        String content = normalize(form.getContent());
        if (content == null) throw new IllegalArgumentException("본문은 필수입니다.");

        World world = World.create(novel, category, name, content);
        World saved = worldRepository.save(world);

        return new WorldDTO.Created(saved.getId());
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}