package kr.io.blankspace.setting.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.setting.loginLog.LoginLogKeys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class PasswordChangedLogoutFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public PasswordChangedLogoutFilter(UserRepository userRepository)
    { this.userRepository = userRepository; }

    @Override
    protected void doFilterInternal
    (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (session != null
        && auth != null && auth.isAuthenticated()
        && !"anonymousUser".equals(auth.getPrincipal())) {
            Object loginAtObj = session.getAttribute(LoginLogKeys.SESSION_LOGIN_AT);

            if (loginAtObj instanceof LocalDateTime loginAt) {
                String userId = auth.getName();
                User user = userRepository.findById(userId).orElse(null);

                if (user != null
                && user.getPwChangeAt() != null && user.getPwChangeAt().isAfter(loginAt)) {
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}