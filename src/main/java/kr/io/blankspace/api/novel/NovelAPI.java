package kr.io.blankspace.api.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.service.novel.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelAPI {
    private final NovelService novelService;

    // 소설 목록 조회
    @GetMapping
    public List<NovelDTO.Card> list(@RequestParam(required = false) String type)
    { return novelService.getNovelList(type); }
}
