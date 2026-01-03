package kr.io.blankspace.setting;

import kr.io.blankspace.domain.account.loginLog.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SessionDestroyedListener {
    private final LoginLogRepository loginLogRepository;
    private static final String SESSION_LOGIN_HASH = "LOGIN_HASH";

    @EventListener
    @Transactional
    public void onSessionDestroyed(HttpSessionDestroyedEvent event) {
        Object hash = event.getSession().getAttribute(SESSION_LOGIN_HASH);
        if (hash == null) return;

        String loginHash = String.valueOf(hash);
        loginLogRepository.markLogoutIfNotExists(loginHash, LocalDateTime.now());
    }
}
