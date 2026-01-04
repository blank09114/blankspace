package kr.io.blankspace.infra.loginLog;

import kr.io.blankspace.account.repository.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginLogCleanupJob {
    private final LoginLogRepository loginLogRepository;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        loginLogRepository.deleteEndedBefore(cutoff);
    }
}