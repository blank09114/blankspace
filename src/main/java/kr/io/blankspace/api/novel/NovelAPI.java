package kr.io.blankspace.api.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.dto.novel.WorldDTO;
import kr.io.blankspace.service.novel.NovelService;
import kr.io.blankspace.service.novel.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelAPI {
    private final NovelService novelService;
    private final WorldService worldService;

    // 소설 목록 조회
    @GetMapping
    public List<NovelDTO.Card> list(@RequestParam(required = false) String type)
    { return novelService.getNovelList(type); }

    // 설정 목록 조회
    @GetMapping("/{novelId}/worlds")
    public List<WorldDTO.ListItem> worldList(
            @PathVariable Integer novelId,
            @RequestParam(required = false) String category
    ) {
        return worldService.getWorldList(novelId, category);
    }
}