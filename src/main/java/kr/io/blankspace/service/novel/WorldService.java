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

    // 상세 조회
    @Transactional(readOnly = true)
    public WorldDTO.Detail getDetail(Integer novelId, Long worldId) {
        World world = worldRepository.findByIdAndNovelId(worldId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("설정이 존재하지 않습니다."));

        WorldDTO.Detail dto = new WorldDTO.Detail();
        dto.setWorldId(world.getId());
        dto.setNovelId(novelId);
        dto.setCategory(world.getCategory());
        dto.setName(world.getName());
        dto.setContent(world.getContent());
        dto.setCreatedAt(world.getCreatedAt());

        return dto;
    }

    // 이전 설정
    @Transactional(readOnly = true)
    public World getPrevWorld(Integer novelId, Long worldId) {
        return worldRepository
        .findTopByNovelIdAndIdLessThanOrderByIdDesc(novelId, worldId).orElse(null);
    }

    // 다음 설정
    @Transactional(readOnly = true)
    public World getNextWorld(Integer novelId, Long worldId) {
        return worldRepository
        .findTopByNovelIdAndIdGreaterThanOrderByIdAsc(novelId, worldId).orElse(null);
    }

    // 등록
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

    // 수정 폼 불러오기
    @Transactional(readOnly = true)
    public WorldDTO.Form getFormForEdit(Integer novelId, Long worldId) {
        World world = worldRepository.findByIdAndNovelId(worldId, novelId)
        .orElseThrow(() -> new IllegalArgumentException("설정을 찾을 수 없습니다. novelId=" + novelId + ", worldId=" + worldId));

        WorldDTO.Form form = new WorldDTO.Form();
        form.setCategory(world.getCategory());
        form.setName(world.getName());
        form.setContent(world.getContent());
        return form;
    }

    // 수정 처리
    @Transactional
    public void update(Integer novelId, Long worldId, WorldDTO.Form form) {
        World world = worldRepository.findByIdAndNovelId(worldId, novelId)
        .orElseThrow(() -> new IllegalArgumentException
        ("설정을 찾을 수 없습니다. novelId=" + novelId + ", worldId=" + worldId));

        String category = normalize(form.getCategory());
        if (category == null) throw new IllegalArgumentException("카테고리는 필수입니다.");
        if (!(category.equals("세계관") || category.equals("캐릭터") || category.equals("기타")))
            throw new IllegalArgumentException("카테고리가 올바르지 않습니다.");

        String name = normalize(form.getName());
        if (name == null) throw new IllegalArgumentException("제목은 필수입니다.");
        if (name.length() > 20) throw new IllegalArgumentException("제목은 20자 이하여야 합니다.");

        String content = normalize(form.getContent());
        if (content == null) throw new IllegalArgumentException("본문은 필수입니다.");

        world.update(category, name, content);
    }

    // 삭제
    @Transactional
    public void delete(Integer novelId, Long worldId) {
        World world = worldRepository.findByIdAndNovelId(worldId, novelId)
        .orElseThrow(() -> new IllegalArgumentException
        ("설정을 찾을 수 없습니다. novelId=" + novelId + ", worldId=" + worldId));

        worldRepository.delete(world);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}