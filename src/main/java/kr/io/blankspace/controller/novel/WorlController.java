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

    // 목록

    // 상세
    @GetMapping("/{novelId}/world/{worldId}")
    public String worldDetail
    (@PathVariable Integer novelId, @PathVariable Long worldId, Model model) {
        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelService.getCard(novelId));
        model.addAttribute("world", worldService.getDetail(novelId, worldId));

        model.addAttribute("prevWorld", worldService.getPrevWorld(novelId, worldId));
        model.addAttribute("nextWorld", worldService.getNextWorld(novelId, worldId));

        return "novel/world/world";
    }

    // 등록 페이지
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

    // 등록
    @PostMapping("/{novelId}/world/submit")
    public String submitWorld
    (@PathVariable Integer novelId, @ModelAttribute("form") WorldDTO.Form form) {
        WorldDTO.Created created = worldService.create(novelId, form);
        return "redirect:/novel/" + novelId + "/world/" + created.getWorldId();
    }

    // 수정 페이지
    @GetMapping("/{novelId}/world/{worldId}/edit")
    public String worldEditForm
    (@PathVariable Integer novelId, @PathVariable Long worldId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);
        WorldDTO.Form form = worldService.getFormForEdit(novelId, worldId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);
        model.addAttribute("worldId", worldId);

        model.addAttribute("mode", "EDIT");
        model.addAttribute("formAction", "/novel/" + novelId + "/world/" + worldId + "/edit");
        model.addAttribute("submitText", "수정");
        model.addAttribute("form", form);

        return "novel/world/worldForm";
    }

    // 수정
    @PostMapping("/{novelId}/world/{worldId}/edit")
    public String submitWorldEdit(
        @PathVariable Integer novelId, @PathVariable Long worldId,
        @ModelAttribute("form") WorldDTO.Form form
    ) {
        worldService.update(novelId, worldId, form);
        return "redirect:/novel/" + novelId + "/world/" + worldId;
    }

    // 삭제
    @PostMapping("/{novelId}/world/{worldId}/delete")
    public String deleteWorld(@PathVariable Integer novelId, @PathVariable Long worldId) {
        worldService.delete(novelId, worldId);
        return "redirect:/novel/" + novelId + "/world";
    }
}