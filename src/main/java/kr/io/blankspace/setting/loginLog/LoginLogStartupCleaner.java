package kr.io.blankspace.setting.loginLog;

import kr.io.blankspace.domain.account.loginLog.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginLogStartupCleaner {
    private final LoginLogRepository loginLogRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onStartup() {
        LocalDateTime now = LocalDateTime.now();
        int updated = loginLogRepository.markAllActiveAsRestarted(now);
        System.out.println("[Startup] forced logout logs = " + updated);
    }
}