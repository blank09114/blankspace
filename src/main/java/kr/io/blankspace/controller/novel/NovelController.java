package kr.io.blankspace.controller.novel;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.service.novel.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/novel")
@RequiredArgsConstructor
public class NovelController {
    private final NovelService novelService;

    // 소설 목록
    @GetMapping
    public String novelList() { return "novel/novelList"; }

    // 소설 등록 페이지
    @GetMapping("/form")
    public String novelForm(Model model) {
        model.addAttribute("mode", "CREATE");
        model.addAttribute("formAction", "/novel/submit");
        model.addAttribute("submitText", "등록");

        model.addAttribute("form", new NovelDTO.Form());
        return "novel/novelForm";
    }

    // 소설 등록 요청
    @PostMapping("/submit")
    public String submit(@ModelAttribute("form") NovelDTO.Form form) {
        NovelDTO.Created result = novelService.create(form);
        return "redirect:/novel/" + result.getNovelId();
    }

    // 소설 수정 페이지
    @GetMapping("/{novelId}/edit")
    public String editForm(@PathVariable Integer novelId, Model model) {
        model.addAttribute("mode", "EDIT");
        model.addAttribute("formAction", "/novel/" + novelId + "/edit/submit");
        model.addAttribute("submitText", "수정");

        model.addAttribute("form", novelService.getForm(novelId));

        return "novel/novelForm";
    }

    // 소설 수정 요청
    @PostMapping("/{novelId}/edit/submit")
    public String editSubmit(@PathVariable Integer novelId, @ModelAttribute("form") NovelDTO.Form form) {
        novelService.update(novelId, form);
        return "redirect:/novel/" + novelId;
    }

    // 완결 상태 토글
    @PostMapping("/{novelId}/end/toggle")
    public String toggleEnd(@PathVariable Integer novelId) {
        novelService.toggleEnd(novelId);
        return "redirect:/novel/" + novelId;
    }

    // 소설 삭제
    @PostMapping("/{novelId}/delete")
    public String delete(@PathVariable Integer novelId) {
        novelService.delete(novelId);
        return "redirect:/novel";
    }
}