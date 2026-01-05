package kr.io.blankspace.service.board;

import jakarta.persistence.EntityNotFoundException;
import kr.io.blankspace.dto.board.PostDTO;
import kr.io.blankspace.entity.board.Board;
import kr.io.blankspace.entity.board.Category;
import kr.io.blankspace.entity.board.Post;
import kr.io.blankspace.repository.board.BoardRepository;
import kr.io.blankspace.repository.board.CategoryRepository;
import kr.io.blankspace.repository.board.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    private String norm(String boardNameRaw) {return boardNameRaw.trim().toUpperCase(); }

    private Board getBoardOrThrow(String boardName) {
        return boardRepository.findByName(boardName)
        .orElseThrow(() -> new EntityNotFoundException("Board not found: " + boardName));
    }

    private Category getCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
    }

    private void assertCategoryBelongsToBoard(Board board, Category category)
    { if (!category.getBoard().getId().equals(board.getId())) { throw new IllegalArgumentException("Invalid category for board."); } }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public Post getPostDetail(String boardNameRaw, Long postId) {
        String boardName = norm(boardNameRaw);

        return postRepository.findDetailByBoardNameAndId(boardName, postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found: boardName=" + boardName + ", postId=" + postId));
    }

    // 이전 글
    public Optional<Post> getPrevPost(String boardNameRaw, Long postId) {
        String boardName = boardNameRaw.trim().toUpperCase();
        return postRepository.findPrev(boardName, postId, PageRequest.of(0, 1)).stream().findFirst();
    }

    // 다음 글
    public Optional<Post> getNextPost(String boardNameRaw, Long postId) {
        String boardName = boardNameRaw.trim().toUpperCase();
        return postRepository.findNext(boardName, postId, PageRequest.of(0, 1)).stream().findFirst();
    }

    // 게시글 작성
    @Transactional
    public Long createPost(String boardNameRaw, PostDTO.UpsertReq req) {
        String boardName = norm(boardNameRaw);

        Board board = getBoardOrThrow(boardName);
        Category category = getCategoryOrThrow(req.getCategoryId());
        assertCategoryBelongsToBoard(board, category);

        Post saved = postRepository.save(Post.create(
            category, req.getTitle().trim(), trimOrNull(req.getSubTitle()),
            trimOrNull(req.getThumbnailUrl()), trimOrNull(req.getDetailLink()), req.getContent()
        ));

        return saved.getId();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(String boardNameRaw, Long postId, PostDTO.UpsertReq req) {
        String boardName = norm(boardNameRaw);

        Board board = getBoardOrThrow(boardName);
        Category category = getCategoryOrThrow(req.getCategoryId());
        assertCategoryBelongsToBoard(board, category);

        Post post = getPostDetail(boardName, postId);

        if (!post.getCategory().getId().equals(category.getId())) { post.changeCategory(category); }

        post.update(
            req.getTitle().trim(),
            trimOrNull(req.getSubTitle()),
            trimOrNull(req.getThumbnailUrl()),
            trimOrNull(req.getDetailLink()),
            req.getContent()
        );
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(String boardNameRaw, Long postId) {
        String boardName = boardNameRaw.trim().toUpperCase();
        Post post = getPostDetail(boardName, postId);
        postRepository.delete(post);
    }
}