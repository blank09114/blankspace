package kr.io.blankspace.service.board;

import kr.io.blankspace.dto.board.GuestbookDTO;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.entity.board.Guestbook;
import kr.io.blankspace.repository.board.GuestbookRepository;
import kr.io.blankspace.repository.account.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final GuestbookRepository guestbookRepository;
    private final UserRepository userRepository;

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
}