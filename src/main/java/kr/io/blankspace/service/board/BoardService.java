package kr.io.blankspace.service.board;

import jakarta.persistence.EntityNotFoundException;
import kr.io.blankspace.dto.board.PostDTO;
import kr.io.blankspace.entity.board.Board;
import kr.io.blankspace.entity.board.Category;
import kr.io.blankspace.entity.board.Post;
import kr.io.blankspace.repository.board.BoardRepository;
import kr.io.blankspace.repository.board.CategoryRepository;
import kr.io.blankspace.repository.board.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    // 게시글 작성
    @Transactional
    public Long createPost(String boardNameRaw, PostDTO.CreateReq req) {
        String boardName = boardNameRaw.trim().toUpperCase();

        Board board = boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));

        Category category = categoryRepository.findById(req.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + req.getCategoryId()));

        if (!category.getBoard().getId().equals(board.getId()))
        { throw new IllegalArgumentException("Invalid category for board."); }

        Post saved = postRepository.save(Post.create(
            category,
            req.getTitle().trim(),
            trim(req.getSubTitle()),
            trim(req.getThumbnailUrl()),
            trim(req.getDetailLink()),
            req.getContent()
        ));

        return saved.getId();
    }

    private String trim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}