package kr.io.blankspace.controller.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.dto.novel.WorldDTO;
import kr.io.blankspace.service.novel.NovelService;
import kr.io.blankspace.service.novel.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/novel")
@RequiredArgsConstructor
public class WorlController {
    private final NovelService novelService;
    private final WorldService worldService;

    // 설정 목록

    // 설정 등록 페이지
    @GetMapping("/{novelId}/world/form")
    public String worldForm(@PathVariable Integer novelId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);

        model.addAttribute("mode", "CREATE");
        model.addAttribute("formAction", "/novel/" + novelId + "/world/submit");
        model.addAttribute("submitText", "등록");

        model.addAttribute("form", new WorldDTO.Form());

        return "novel/world/worldForm";
    }

    // 설정 등록
    @PostMapping("/{novelId}/world/submit")
    public String submitWorld
    (@PathVariable Integer novelId, @ModelAttribute("form") WorldDTO.Form form) {
        WorldDTO.Created created = worldService.create(novelId, form);
        return "redirect:/novel/" + novelId + "/world/" + created.getWorldId();
    }

    // 설정 상세

    // 설정 수정 페이지

    // 설정 수정 요청

    // 설정 삭제
}
