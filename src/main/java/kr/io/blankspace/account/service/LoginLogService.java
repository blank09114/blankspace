package kr.io.blankspace.account.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.io.blankspace.account.entity.LoginLog;
import kr.io.blankspace.account.repository.LoginLogRepository;
import kr.io.blankspace.account.entity.User;
import kr.io.blankspace.infra.geoip.GeoIpService;
import kr.io.blankspace.infra.security.TokenUtil;
import kr.io.blankspace.infra.loginLog.LoginLogKeys;
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
        String loginHash = TokenUtil.sha256Hex(sessionId);

        session.setAttribute(LoginLogKeys.SESSION_LOGIN_HASH, loginHash);
        session.setAttribute(LoginLogKeys.SESSION_LOGIN_AT, LocalDateTime.now());

        String ip = resolveClientIp(request);
        String region = geoIpService.resolveRegion(ip);

        LoginLog log = LoginLog.builder()
        .user(user).loginHash(loginHash).loginIp(ip).loginRegion(region).build();

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

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        String xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return request.getRemoteAddr();
    }
}