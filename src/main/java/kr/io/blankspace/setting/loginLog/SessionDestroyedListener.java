package kr.io.blankspace.setting.loginLog;

import kr.io.blankspace.service.account.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionDestroyedListener {
    private final LoginLogService loginLogService;

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent event) {
        if (event == null) return;
        loginLogService.markLogoutBySessionId(event.getId());
    }
}