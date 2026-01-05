package kr.io.blankspace.controller.board;

import kr.io.blankspace.dto.board.PostDTO;
import kr.io.blankspace.service.board.BoardService;
import kr.io.blankspace.service.board.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController
{
    private final CategoryService categoryService;
    private final BoardService boardService;

    // 게시판
    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName, Model model) {
        String normalized = boardName.trim().toUpperCase();
        model.addAttribute("boardName", normalized);
        model.addAttribute("categories", categoryService.list(normalized));
        return "board/board";
    }

    // 게시글

    // 게시글 작성 페이지
    @GetMapping("/{boardName}/write")
    public String write(@PathVariable String boardName, Model model) {
        String normalized = boardName.trim().toUpperCase();
        model.addAttribute("boardName", normalized);
        model.addAttribute("categories", categoryService.list(normalized));
        return "board/postForm";
    }

    // 게시글 작성
    @PostMapping("/{boardName}/write")
    public String writePost(
        @PathVariable String boardName,

        @RequestParam Integer categoryId,
        @RequestParam String postTitle,
        @RequestParam(required = false) String postSubTitle,
        @RequestParam(required = false) String postDtlLink,
        @RequestParam(required = false) String thumbnailUrl,
        @RequestParam String postContent
    ) {
        PostDTO.CreateReq req = new PostDTO.CreateReq();
        req.setCategoryId(categoryId);
        req.setTitle(postTitle);
        req.setSubTitle(postSubTitle);
        req.setDetailLink(postDtlLink);
        req.setThumbnailUrl(thumbnailUrl);
        req.setContent(postContent);

        Long postId = boardService.createPost(boardName, req);

        return "redirect:/board/" + boardName.trim().toUpperCase();
        // 상세 페이지 만들면:
        // return "redirect:/board/" + boardName.trim().toUpperCase() + "/post/" + postId;
    }

    // 게시글 수정

    // 게시글 삭제
}