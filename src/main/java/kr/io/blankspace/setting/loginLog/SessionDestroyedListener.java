package kr.io.blankspace.setting.loginLog;

import kr.io.blankspace.repository.LoginLogRepository;
import kr.io.blankspace.setting.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SessionDestroyedListener {
    private final LoginLogRepository loginLogRepository;

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent event) {
        if (event == null) return;

        String sessionId = event.getId();
        String loginHash = TokenUtil.sha256Hex(sessionId);

        loginLogRepository.markLogout(loginHash, LocalDateTime.now());
    }
}