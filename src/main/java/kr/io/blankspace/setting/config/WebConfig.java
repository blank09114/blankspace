package kr.io.blankspace.setting.config;

import kr.io.blankspace.setting.security.IpCooldownInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final IpCooldownInterceptor ipCooldownInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipCooldownInterceptor)
        .addPathPatterns(
            "/api/auth/join/request",
            "/api/auth/exists/**",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/password/reset/request",
            "/api/auth/withdraw/request",
            "/api/user/*/name",
            "/api/post/*/comment"
        );
    }
}