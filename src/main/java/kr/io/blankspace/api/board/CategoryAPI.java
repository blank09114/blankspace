package kr.io.blankspace.api.board;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.board.CategoryDTO;
import kr.io.blankspace.service.board.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardName}/category")
public class CategoryAPI {
    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryDTO.Basic> create(@PathVariable String boardName, @Valid @RequestBody CategoryDTO.CreateReq req) {
        CategoryDTO.Basic res = categoryService.create(boardName, req);
        URI location = URI.create("/api/board/" + boardName.trim().toUpperCase() + "/category/" + res.getCategoryId());
        return ResponseEntity.created(location).body(res);
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable String boardName, @PathVariable Integer categoryId) {
        categoryService.delete(boardName, categoryId);
        return ResponseEntity.noContent().build();
    }

    // 카테고리 이름 변경
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO.Basic> edit
    (@PathVariable String boardName, @PathVariable Integer categoryId, @Valid @RequestBody CategoryDTO.EditReq req)
    { return ResponseEntity.ok(categoryService.edit(boardName, categoryId, req)); }
}