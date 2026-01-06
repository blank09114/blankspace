package kr.io.blankspace.controller.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.service.novel.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/novel")
@RequiredArgsConstructor
public class EpisodeController {
    private final NovelService novelService;

    // 회차 목록(소설 상세) - 소설 등록 후 redirect 되는 페이지
    @GetMapping("/{novelId}")
    public String episodeList(@PathVariable Integer novelId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);

        // TODO: 회차 목록 추후 추가

        return "novel/episode/novel";
    }

    // 회차 등록 페이지

    // 회차 등록 요청

    // 회차 상세

    // 회차 수정 페이지

    // 회차 수정 요청

    // 회차 삭제
}