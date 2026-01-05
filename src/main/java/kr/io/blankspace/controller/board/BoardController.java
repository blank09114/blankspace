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
    @GetMapping("/{boardName}/post/{postId}")
    public String postDetail(@PathVariable String boardName, @PathVariable Long postId, Model model) {
        String normalized = boardName.trim().toUpperCase();

        model.addAttribute("boardName", normalized);
        model.addAttribute("post", boardService.getPostDetail(normalized, postId));
        model.addAttribute("prevPost", boardService.getPrevPost(normalized, postId).orElse(null));
        model.addAttribute("nextPost", boardService.getNextPost(normalized, postId).orElse(null));

        return "board/post";
    }

    // 게시글 작성 페이지
    @GetMapping("/{boardName}/write")
    public String write(@PathVariable String boardName, Model model) {
        String normalized = boardName.trim().toUpperCase();
        model.addAttribute("editMode", false);
        model.addAttribute("boardName", normalized);
        model.addAttribute("categories", categoryService.list(normalized));
        return "board/postForm";
    }

    // 게시글 작성
    @PostMapping("/{boardName}/write")
    public String writePost(@PathVariable String boardName, @ModelAttribute PostDTO.UpsertReq req) {
        Long postId = boardService.createPost(boardName, req);
        String normalized = boardName.trim().toUpperCase();
        return "redirect:/board/" + normalized + "/post/" + postId;
    }

    // 게시글 수정 페이지
    @GetMapping("/{boardName}/post/{postId}/edit")
    public String editForm(@PathVariable String boardName, @PathVariable Long postId, Model model) {
        String normalized = boardName.trim().toUpperCase();

        model.addAttribute("boardName", normalized);
        model.addAttribute("categories", categoryService.list(normalized));

        model.addAttribute("editMode", true);
        model.addAttribute("postId", postId);

        model.addAttribute("post", boardService.getPostDetail(normalized, postId));

        return "board/postForm";
    }

    // 게시글 수정
    @PostMapping("/{boardName}/post/{postId}/edit")
    public String editPost(@PathVariable String boardName, @PathVariable Long postId, @ModelAttribute PostDTO.UpsertReq req) {
        boardService.updatePost(boardName, postId, req);

        String normalized = boardName.trim().toUpperCase();
        return "redirect:/board/" + normalized + "/post/" + postId;
    }

    // 게시글 삭제
    @PostMapping("/{boardName}/post/{postId}/delete")
    public String deletePost(@PathVariable String boardName, @PathVariable Long postId) {
        boardService.deletePost(boardName, postId);
        String normalized = boardName.trim().toUpperCase();
        return "redirect:/board/" + normalized;
    }
}