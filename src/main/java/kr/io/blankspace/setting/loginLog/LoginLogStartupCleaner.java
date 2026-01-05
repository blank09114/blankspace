package kr.io.blankspace.setting.loginLog;

import kr.io.blankspace.service.account.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginLogStartupCleaner {
    private static final Logger log = LoggerFactory.getLogger(LoginLogStartupCleaner.class);

    private final LoginLogService loginLogService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onStartup() {
        LocalDateTime now = LocalDateTime.now();
        int updated = loginLogService.forceLogoutAllActive(now);
        log.info("[Startup] forced logout logs = {}", updated);
    }
}