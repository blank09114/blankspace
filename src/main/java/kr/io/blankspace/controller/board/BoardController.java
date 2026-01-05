package kr.io.blankspace.controller.board;

import kr.io.blankspace.service.board.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController
{
    private final CategoryService categoryService;

    // 게시판
    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName, Model model) {
        String normalized = boardName.trim().toUpperCase();

        model.addAttribute("boardName", normalized);
        model.addAttribute("categories", categoryService.list(normalized));
        return "board/board";
    }
}