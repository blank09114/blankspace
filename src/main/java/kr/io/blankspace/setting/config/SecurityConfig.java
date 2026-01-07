package kr.io.blankspace.setting.config;

import kr.io.blankspace.entity.account.User;
import kr.io.blankspace.repository.account.UserRepository;
import kr.io.blankspace.setting.security.PasswordChangedLogoutFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // 권한 조회
    @Bean
    public UserDetailsService userDetailsService() {
        return (String userId) -> {
            User u = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("not found"));

            boolean disabled = !u.isUserEnabled();
            boolean locked = u.isBlocked();

            return org.springframework.security.core.userdetails.User
            .withUsername(u.getUserId()).password(u.getUserPw()).roles(u.getUserRole().name())
            .disabled(disabled).accountLocked(locked).build();
        };
    }

    // 인증 매니저
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    // 비밀번호 변경 감지
    @Bean
    public PasswordChangedLogoutFilter passwordChangedLogoutFilter()
    { return new PasswordChangedLogoutFilter(userRepository); }

    // 접근 제어
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .formLogin(f -> f.disable())
        .httpBasic(b -> b.disable())
        .authorizeHttpRequests(auth -> auth
            // 정적 리소스
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()

            // 메일 링크
            .requestMatchers(HttpMethod.GET, "/api/auth/join/verify").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/password/reset/apply").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/withdraw/apply").permitAll()

            // 비로그인 사용자만 허용
            .requestMatchers(HttpMethod.GET, "/auth/login").anonymous()
            .requestMatchers(HttpMethod.GET, "/auth/join").anonymous()
            .requestMatchers(HttpMethod.GET, "/auth/findAccount").anonymous()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").anonymous()
            .requestMatchers(HttpMethod.POST, "/api/auth/join/request").anonymous()
            .requestMatchers(HttpMethod.POST, "/api/auth/join/resend").anonymous()
            .requestMatchers(HttpMethod.POST, "/api/auth/password/reset/request").anonymous()
            .requestMatchers(HttpMethod.GET,  "/api/auth/exists/**").anonymous()

            // 로그인 사용자만 허용
            .requestMatchers(HttpMethod.GET, "/auth/changePw").authenticated()
            .requestMatchers(HttpMethod.GET, "/auth/withdraw").authenticated()
            .requestMatchers(HttpMethod.GET,  "/api/auth/me").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/auth/password/change").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/auth/withdraw/request").authenticated()
            .requestMatchers(HttpMethod.GET, "/user/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/user/*/login-logs").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/api/user/*/name").authenticated()
            .requestMatchers(HttpMethod.POST,"/api/*/*/comment/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/*/*/comment/**").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/guestbook").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/guestbook/*").authenticated()

            // 관리자만 허용
            .requestMatchers(HttpMethod.GET, "/user/list").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/user/list").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/user/*/block").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/board/*/category/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/board/*/category/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/board/*/category/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,  "/board/*/write").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/board/*/write").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,  "/board/*/post/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/board/*/post/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/board/*/post/*/delete").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,  "/novel/form").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/submit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,  "/novel/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/delete").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/end").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/novel/*/episode/form").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/novel/*/episode/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/novel/*/world/form").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/novel/*/world/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/end/toggle").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/episode/submit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/episode/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/episode/*/delete").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/world/submit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/world/*/edit").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/novel/*/world/*/delete").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/guestbook/*/answer").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/guestbook/*/answer").hasRole("ADMIN")

            // 나머지는 전부 허용
            .anyRequest().permitAll()
        );

        http.addFilterAfter(passwordChangedLogoutFilter(), SecurityContextHolderFilter.class);

        return http.build();
    }
}