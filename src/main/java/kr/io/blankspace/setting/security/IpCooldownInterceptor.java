package kr.io.blankspace.setting.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpCooldownInterceptor implements HandlerInterceptor {
    private final ConcurrentHashMap<String, Long> lastHit = new ConcurrentHashMap<>();
    private static final long WINDOW_MS = 10_000L; // 10 seconds

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) return true;
        String uri = request.getRequestURI();

        boolean match =
        uri.matches("^/api/post/\\d+/comment$") ||
        uri.matches("^/api/post/\\d+/comment/\\d+/recomment$") ||
        uri.matches("^/api/episode/\\d+/comment$") ||
        uri.matches("^/api/episode/\\d+/comment/\\d+/recomment$");

        if (!match) return true;

        String subject = extractSubject(request);
        String key = subject + "|POST|" + uri;

        long now = System.currentTimeMillis();
        Long prev = lastHit.putIfAbsent(key, now);

        if (prev != null) {
            long delta = now - prev;
            if (delta < WINDOW_MS) {
                long retryAfterSec = (WINDOW_MS - delta + 999) / 1000;

                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(retryAfterSec));
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                response.getWriter().write("{\"ok\":false,\"message\":\"10초 후 다시 시도해주세요.\"}");
                return false;
            } else { lastHit.put(key, now); }
        }

        if ((now & 0xFF) == 0) cleanupOld(now);

        return true;
    }

    private void cleanupOld(long now) {
        long threshold = now - (WINDOW_MS * 6);
        for (var e : lastHit.entrySet()) { if (e.getValue() < threshold) lastHit.remove(e.getKey(), e.getValue()); }
    }

    private String extractSubject(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "user:" + auth.getName();
        }
        return "ip:" + extractClientIp(request);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        String xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return request.getRemoteAddr();
    }
}