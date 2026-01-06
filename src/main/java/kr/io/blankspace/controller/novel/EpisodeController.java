package kr.io.blankspace.controller.novel;

import kr.io.blankspace.dto.novel.EpisodeDTO;
import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.service.novel.EpisodeService;
import kr.io.blankspace.service.novel.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/novel")
@RequiredArgsConstructor
public class EpisodeController {
    private final NovelService novelService;
    private final EpisodeService episodeService;

    // 회차 목록(소설 상세)
    @GetMapping("/{novelId}")
    public String episodeList(@PathVariable Integer novelId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);
        model.addAttribute("chapterGroups", episodeService.getGroupedEpisodeList(novelId));

        return "novel/episode/novel";
    }

    // 회차 상세
    @GetMapping("/{novelId}/episode/{episodeId}")
    public String episodeDetail
    (@PathVariable Integer novelId, @PathVariable Long episodeId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);
        EpisodeDTO.DetailView episodeView = episodeService.getDetailView(novelId, episodeId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);
        model.addAttribute("episode", episodeView);

        model.addAttribute("prevEpisode", episodeService.getPrevEpisode(novelId, episodeId));
        model.addAttribute("nextEpisode", episodeService.getNextEpisode(novelId, episodeId));

        return "novel/episode/episode";
    }

    // 회차 등록 페이지
    @GetMapping("/{novelId}/episode/form")
    public String episodeForm(@PathVariable Integer novelId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);
        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);

        EpisodeDTO.Form form = new EpisodeDTO.Form();
        String prevName = episodeService.getLatestEpisodeNameOrNull(novelId);
        if (prevName != null) form.setName(prevName);

        model.addAttribute("mode", "CREATE");
        model.addAttribute("formAction", "/novel/" + novelId + "/episode/submit");
        model.addAttribute("submitText", "등록");
        model.addAttribute("form", form);

        return "novel/episode/episodeForm";
    }

    // 회차 등록
    @PostMapping("/{novelId}/episode/submit")
    public String submitEpisode
    (@PathVariable Integer novelId, @ModelAttribute("form") EpisodeDTO.Form form) {
        EpisodeDTO.Created created = episodeService.create(novelId, form);
        return "redirect:/novel/" + novelId + "/episode/" + created.getEpisodeId();
    }

    // 회차 수정 페이지
    @GetMapping("/{novelId}/episode/{episodeId}/edit")
    public String episodeEditForm
    (@PathVariable Integer novelId, @PathVariable Long episodeId, Model model) {
        NovelDTO.Card novelCard = novelService.getCard(novelId);
        EpisodeDTO.Form form = episodeService.getFormForEdit(novelId, episodeId);

        model.addAttribute("novelId", novelId);
        model.addAttribute("novelCard", novelCard);
        model.addAttribute("episodeId", episodeId);

        model.addAttribute("mode", "EDIT");
        model.addAttribute("formAction", "/novel/" + novelId + "/episode/" + episodeId + "/edit");
        model.addAttribute("submitText", "수정");
        model.addAttribute("form", episodeService.getFormForEdit(novelId, episodeId));

        return "novel/episode/episodeForm";
    }

    // 회차 수정 처리
    @PostMapping("/{novelId}/episode/{episodeId}/edit")
    public String submitEpisodeEdit
    (@PathVariable Integer novelId, @PathVariable Long episodeId, @ModelAttribute("form") EpisodeDTO.Form form) {
        episodeService.update(novelId, episodeId, form);

        return "redirect:/novel/" + novelId + "/episode/" + episodeId;
    }

    // 회차 삭제
    @PostMapping("/{novelId}/episode/{episodeId}/delete")
    public String deleteEpisode
    (@PathVariable Integer novelId, @PathVariable Long episodeId) {
        episodeService.delete(novelId, episodeId);
        return "redirect:/novel/" + novelId;
    }
}