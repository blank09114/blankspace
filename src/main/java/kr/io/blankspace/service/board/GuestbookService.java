package kr.io.blankspace.service.board;

import kr.io.blankspace.dto.board.GuestbookDTO;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.board.Guestbook;
import kr.io.blankspace.repository.board.GuestbookRepository;
import kr.io.blankspace.repository.account.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final GuestbookRepository guestbookRepository;
    private final UserRepository userRepository;

    // 페이징 처리
    public Page<GuestbookDTO.ListItem> getGuestbookPage(int page, Authentication auth) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), 5);

        String requesterId = getRequesterId(auth);
        boolean isAdmin = hasAdminRole(auth);

        return guestbookRepository.findAllByOrderByCreatedAtDesc(pageable)
        .map(g -> toListItem(g, requesterId, isAdmin));
    }

    // 조회
    private GuestbookDTO.ListItem toListItem(Guestbook g, String requesterId, boolean isAdmin) {
        String authorId = safeUserId(g);
        String authorName = safeUserName(g);

        boolean mine = (requesterId != null && requesterId.equals(authorId));

        boolean canViewSecret = !g.isSecret() || isAdmin || mine;

        String content = canViewSecret ? g.getContent() : "비밀글입니다.";
        String answerContent = g.getAnswerContent();
        if (!canViewSecret && answerContent != null && !answerContent.isBlank())
        { answerContent = "비밀글입니다."; }

        boolean canDeleteGuestbook = isAdmin || mine;
        boolean canAnswer = isAdmin;
        boolean canDeleteAnswer = isAdmin;

        return new GuestbookDTO.ListItem(
            g.getId(), authorId, authorName, g.isSecret(), content, g.getCreatedAt(),
            g.getAnswerAt(), answerContent, mine, canDeleteGuestbook, canAnswer, canDeleteAnswer
        );
    }

    // 등록
    @Transactional
    public GuestbookDTO.CreateRes create(String userId, GuestbookDTO.CreateReq req) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 사용자 정보를 찾을 수 없습니다."));

        Guestbook guestbook = new Guestbook(user, req.isSecret(), req.getContent());
        Guestbook saved = guestbookRepository.save(guestbook);

        String userName = user.getUserName();

        return new GuestbookDTO.CreateRes(
            saved.getId(), user.getUserId(), userName,
            saved.isSecret(), saved.getContent(), saved.getCreatedAt()
        );
    }

    // 삭제
    @Transactional
    public void deleteGuestbook(Long guestbookId, Authentication auth) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방명록을 찾을 수 없습니다."));

        String requesterId = getRequesterId(auth);
        boolean isAdmin = hasAdminRole(auth);

        String authorId = guestbook.getUser().getUserId();
        boolean mine = requesterId != null && requesterId.equals(authorId);

        if (!isAdmin && !mine)
        { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."); }

        guestbookRepository.delete(guestbook);
    }

    // 답변 등록
    @Transactional
    public GuestbookDTO.AnswerRes upsertAnswer(Long guestbookId, GuestbookDTO.AnswerReq req) {
        LocalDateTime now = LocalDateTime.now();

        int updated = guestbookRepository.updateAnswer(guestbookId, now, req.getContent().trim());
        if (updated == 0)
        { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "방명록을 찾을 수 없습니다."); }

        return new GuestbookDTO.AnswerRes(guestbookId, now, req.getContent().trim());
    }

    // 답변 삭제
    @Transactional
    public void deleteAnswer(Long guestbookId) {
        int updated = guestbookRepository.clearAnswer(guestbookId);
        if (updated == 0)
        { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "방명록을 찾을 수 없습니다."); }
    }

    private String getRequesterId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        String name = auth.getName();
        return (name == null || "anonymousUser".equals(name)) ? null : name;
    }

    private boolean hasAdminRole(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            String role = a.getAuthority();
            if ("ROLE_ADMIN".equals(role) || "ADMIN".equals(role)) return true;
        }
        return false;
    }

    private String safeUserId(Guestbook g) { return g.getUser() != null ? g.getUser().getUserId() : null; }
    private String safeUserName(Guestbook g) { return g.getUser() != null ? g.getUser().getUserName() : null; }
}