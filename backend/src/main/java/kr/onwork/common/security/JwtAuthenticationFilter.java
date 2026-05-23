package kr.onwork.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import kr.onwork.common.domain.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Authorization: Bearer 토큰을 검증하고 SecurityContext에 인증을 설정한다. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService blacklist;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, TokenBlacklistService blacklist) {
        this.tokenProvider = tokenProvider;
        this.blacklist = blacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && !blacklist.isBlacklisted(token)) {
            try {
                Claims claims = tokenProvider.parse(token);
                if (JwtTokenProvider.TYPE_ACCESS.equals(claims.get("type", String.class))) {
                    Long userId = Long.valueOf(claims.getSubject());
                    String email = claims.get("email", String.class);
                    Role role = Role.valueOf(claims.get("role", String.class));
                    AuthPrincipal principal = new AuthPrincipal(userId, email, role);
                    var auth = new UsernamePasswordAuthenticationToken(
                            principal, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (RuntimeException ignored) {
                // 유효하지 않은 토큰 → 인증 미설정 → 보호 자원은 EntryPoint가 401 처리
            }
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
