package kr.io.blankspace.setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
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

        String ip = extractClientIp(request);
        String key = ip + "|" + request.getMethod() + "|" + request.getRequestURI();

        long now = System.currentTimeMillis();
        Long prev = lastHit.putIfAbsent(key, now);

        if (prev != null) {
            long delta = now - prev;
            if (delta < WINDOW_MS) {
                response.setStatus(429);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                response.getWriter().write("{\"ok\":false,\"message\":\"10초 후 다시 시도해주세요.\"}");
                return false;
            } else { lastHit.put(key, now); }
        }

        if ((now & 0xFF) == 0) { cleanupOld(now); }

        return true;
    }

    private void cleanupOld(long now) {
        long threshold = now - (WINDOW_MS * 6); // 60초 이상 안 쓰인 키 제거
        for (var e : lastHit.entrySet()) { if (e.getValue() < threshold) lastHit.remove(e.getKey(), e.getValue()); }
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        String xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return request.getRemoteAddr();
    }
}