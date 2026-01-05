package kr.io.blankspace.service.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.io.blankspace.entity.LoginLog;
import kr.io.blankspace.entity.User;
import kr.io.blankspace.repository.LoginLogRepository;
import kr.io.blankspace.setting.geoip.GeoIpService;
import kr.io.blankspace.setting.loginLog.LoginLogKeys;
import kr.io.blankspace.setting.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;
    private final GeoIpService geoIpService;

    // 로그인 성공 후 기록 생성
    @Transactional
    public void recordLogin(User user, HttpSession session, HttpServletRequest request) {
        String sessionId = session.getId();
        String loginHash = computeLoginHash(sessionId);

        session.setAttribute(LoginLogKeys.SESSION_LOGIN_HASH, loginHash);
        session.setAttribute(LoginLogKeys.SESSION_LOGIN_AT, LocalDateTime.now());

        String ip = resolveClientIp(request);
        String region = geoIpService.resolveRegion(ip);

        LoginLog log = LoginLog.builder()
        .user(user).loginHash(loginHash).loginIp(ip)
        .loginRegion(region).build();

        loginLogRepository.save(log);
    }

    // 로그아웃 시 호출
    @Transactional
    public void markLogout(HttpSession session) {
        if (session == null) return;
        Object h = session.getAttribute(LoginLogKeys.SESSION_LOGIN_HASH);
        if (h == null) return;

        loginLogRepository.markLogout(String.valueOf(h), LocalDateTime.now());
    }

    // 세션 파괴 이벤트
    @Transactional
    public void markLogoutBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;
        String loginHash = computeLoginHash(sessionId);
        loginLogRepository.markLogout(loginHash, LocalDateTime.now());
    }

    // 서버 재시작 시 로그 정리
    @Transactional
    public int forceLogoutAllActive(LocalDateTime now) {
        if (now == null) now = LocalDateTime.now();
        return loginLogRepository.markAllActiveAsRestarted(now);
    }

    // 오래된 로그 삭제
    @Transactional
    public int cleanupOldLogs(LocalDateTime cutoff) {
        if (cutoff == null) cutoff = LocalDateTime.now().minusDays(7);
        return loginLogRepository.deleteEndedBefore(cutoff);
    }

    private String computeLoginHash(String sessionId) { return TokenUtil.sha256Hex(sessionId); }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        String xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return request.getRemoteAddr();
    }
}