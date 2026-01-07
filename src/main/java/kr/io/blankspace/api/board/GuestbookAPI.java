package kr.io.blankspace.api.board;

import jakarta.validation.Valid;
import kr.io.blankspace.dto.board.GuestbookDTO;
import kr.io.blankspace.service.board.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guestbook")
public class GuestbookAPI {
    private final GuestbookService guestbookService;

    // 조회
    @GetMapping
    public Page<GuestbookDTO.ListItem> getGuestbooks
    (@RequestParam(defaultValue = "0") int page, Authentication authentication)
    { return guestbookService.getGuestbookPage(page, authentication); }

    // 등록
    @PostMapping
    public GuestbookDTO.CreateRes createGuestbook
    (Principal principal, @Valid @RequestBody GuestbookDTO.CreateReq req) {
        String userId = principal.getName();
        return guestbookService.create(userId, req);
    }

    // 삭제
    @DeleteMapping("/{guestbookId}")
    public void deleteGuestbook
    (@PathVariable Long guestbookId, Authentication authentication)
    { guestbookService.deleteGuestbook(guestbookId, authentication); }

    // 답변 등록
    @PostMapping("/{guestbookId}/answer")
    public GuestbookDTO.AnswerRes upsertAnswer
    (@PathVariable Long guestbookId, @Valid @RequestBody GuestbookDTO.AnswerReq req)
    { return guestbookService.upsertAnswer(guestbookId, req); }

    // 답변 삭제
    @DeleteMapping("/{guestbookId}/answer")
    public void deleteAnswer(@PathVariable Long guestbookId)
    { guestbookService.deleteAnswer(guestbookId); }
}