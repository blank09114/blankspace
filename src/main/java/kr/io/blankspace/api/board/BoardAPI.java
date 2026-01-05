package kr.io.blankspace.api.board;

import kr.io.blankspace.dto.board.PostDTO;
import kr.io.blankspace.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardAPI {

    private final BoardService boardService;

    // 게시글 목록 조회
    @GetMapping("/{boardName}/posts")
    public Page<PostDTO.ListItem> getPosts(
        @PathVariable String boardName, @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) Integer categoryId
    ) { return boardService.getPostList(boardName, categoryId, page); }
}