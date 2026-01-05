package kr.io.blankspace.service.board;

import jakarta.persistence.EntityNotFoundException;
import kr.io.blankspace.dto.board.CategoryDTO;
import kr.io.blankspace.entity.board.Board;
import kr.io.blankspace.entity.board.Category;
import kr.io.blankspace.repository.board.BoardRepository;
import kr.io.blankspace.repository.board.CategoryRepository;
import kr.io.blankspace.repository.board.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    // 카테고리 조회
    @Transactional(readOnly = true)
    public List<CategoryDTO.Basic> list(String boardNameRaw) {
        String boardName = boardNameRaw.trim().toUpperCase();

        Board board = boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));

        return categoryRepository.findByBoard_IdOrderByIdAsc(board.getId())
        .stream().map(c -> new CategoryDTO.Basic(c.getId(), c.getName())).toList();
    }

    // 카테고리 생성
    @Transactional
    public CategoryDTO.Basic create(String boardNameRaw, CategoryDTO.CreateReq req) {
        String boardName = boardNameRaw.trim().toUpperCase();
        String name = req.getName().trim();

        Board board = boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));

        if (categoryRepository.findByBoard_IdAndName(board.getId(), name).isPresent())
        { throw new IllegalArgumentException("Category already exists: " + name); }

        Category saved = categoryRepository.save(Category.create(board, name));
        return new CategoryDTO.Basic(saved.getId(), saved.getName());
    }

    // 카테고리 삭제
    @Transactional
    public void delete(String boardNameRaw, Integer categoryId) {
        String boardName = boardNameRaw.trim().toUpperCase();

        Board board = boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));

        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        if (!category.getBoard().getId().equals(board.getId()))
        { throw new IllegalArgumentException("Invalid category for board."); }
        if (postRepository.existsByCategory_Id(categoryId)) { throw new IllegalArgumentException("카테고리에 게시글이 있어 삭제할 수 없습니다."); }

        categoryRepository.delete(category);
    }

    // 카테고리 이름 변경
    @Transactional
    public CategoryDTO.Basic edit(String boardNameRaw, Integer categoryId, CategoryDTO.EditReq req) {
        String boardName = boardNameRaw.trim().toUpperCase();
        String newName = req.getName().trim();

        Board board = boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));

        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        if (!category.getBoard().getId().equals(board.getId()))
        { throw new IllegalArgumentException("Invalid category for board."); }
        if (category.getName().equals(newName))
        { return new CategoryDTO.Basic(category.getId(), category.getName()); }
        if (categoryRepository.findByBoard_IdAndName(board.getId(), newName).isPresent())
        { throw new IllegalArgumentException("Category already exists: " + newName); }

        category.rename(newName);

        return new CategoryDTO.Basic(category.getId(), category.getName());
    }

    // 게시판 이름 대문자 변환
    private String normalizeBoardName(String boardNameRaw) {
        if (boardNameRaw == null) throw new IllegalArgumentException("boardName is required");
        return boardNameRaw.trim().toUpperCase();
    }
}