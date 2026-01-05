package kr.io.blankspace.setting.loginLog;

import kr.io.blankspace.service.account.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginLogCleanupJob {
    private final LoginLogService loginLogService;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        loginLogService.cleanupOldLogs(cutoff);
    }
}