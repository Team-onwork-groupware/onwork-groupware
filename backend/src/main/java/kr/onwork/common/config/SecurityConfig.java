package kr.onwork.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.onwork.common.error.ErrorCode;
import kr.onwork.common.error.ErrorResponse;
import kr.onwork.common.security.JwtAuthenticationFilter;
import kr.onwork.common.security.JwtProperties;
import kr.onwork.common.security.JwtTokenProvider;
import kr.onwork.common.security.TokenBlacklistService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 보안 설정 (ADR-SYS-002): Stateless JWT + RBAC 5단계.
 * 인증/인가 실패는 {code, message} 형식으로 응답.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String[] PUBLIC = {
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/ping",
            "/actuator/health"
    };

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtTokenProvider tokenProvider,
                                    TokenBlacklistService blacklist) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC).permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, blacklist),
                    UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        writeError(res, ErrorCode.UNAUTHORIZED))
                .accessDeniedHandler((req, res, e) ->
                        writeError(res, ErrorCode.FORBIDDEN)));
        return http.build();
    }

    private void writeError(jakarta.servlet.http.HttpServletResponse res, ErrorCode code)
            throws java.io.IOException {
        res.setStatus(code.status().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.of(code, code.defaultMessage())));
    }

    /** RBAC 계층: CEO > VP > HR_MANAGER > MANAGER > EMPLOYEE. */
    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("CEO").implies("VP")
                .role("VP").implies("HR_MANAGER")
                .role("HR_MANAGER").implies("MANAGER")
                .role("MANAGER").implies("EMPLOYEE")
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 개발용 CORS — React dev 서버(5173) 허용. 운영 도메인은 환경별로 조정. */
    @Bean
    org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of("http://localhost:5173", "http://localhost:5174"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
