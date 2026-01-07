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
    public GuestbookDTO.CreateRes createGuestbook(
        Principal principal, @Valid @RequestBody GuestbookDTO.CreateReq req
    ) {
        String userId = principal.getName();
        return guestbookService.create(userId, req);
    }
}