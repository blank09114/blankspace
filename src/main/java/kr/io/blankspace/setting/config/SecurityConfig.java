package kr.io.blankspace.setting.config;

import kr.io.blankspace.domain.account.user.User;
import kr.io.blankspace.domain.account.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

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

            // user_enabled / is_blocked 정책을 “계정 상태”로 반영
            boolean disabled = !u.isUserEnabled();
            boolean locked = u.isBlocked();

            return org.springframework.security.core.userdetails.User
            .withUsername(u.getUserId()).password(u.getUserPw()).roles(u.getUserRole().name())
            .disabled(disabled).accountLocked(locked).build();
        };
    }

    // 세션
    @Bean
    public AuthenticationManager authenticationManager
    (UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .formLogin(f -> f.disable())
        .httpBasic(b -> b.disable());

        return http.build();
    }
}