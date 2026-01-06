package kr.io.blankspace.controller;

import kr.io.blankspace.dto.novel.NovelDTO;
import kr.io.blankspace.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping("/")
    public String main(Model model) {
        List<NovelDTO.Card> recentUpdatedNovels =
        mainService.getRecentlyUpdatedNovelsByEpisode(7, 10);

        model.addAttribute("recentUpdatedNovels", recentUpdatedNovels);
        model.addAttribute("recentBlogPosts", mainService.getRecentBlogPosts(10));

        return "main";
    }
}