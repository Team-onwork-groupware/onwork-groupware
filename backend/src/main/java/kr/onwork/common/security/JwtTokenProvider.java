package kr.onwork.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import kr.onwork.common.domain.Role;
import kr.onwork.common.error.BusinessException;
import kr.onwork.common.error.ErrorCode;
import org.springframework.stereotype.Component;

/** JWT 발급/검증 (ADR-SYS-002: Access 30분 / Refresh 7일, HS256). */
@Component
public class JwtTokenProvider {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessMs;
    private final long refreshMs;

    public JwtTokenProvider(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.accessMs = props.accessTokenValidityMinutes() * 60_000L;
        this.refreshMs = props.refreshTokenValidityDays() * 24L * 60L * 60_000L;
    }

    public String createAccessToken(Long userId, String email, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role.name())
                .claim("type", TYPE_ACCESS)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessMs))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", TYPE_REFRESH)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshMs))
                .signWith(key)
                .compact();
    }

    /** 토큰 파싱 + 서명/만료 검증. 실패 시 BusinessException(INVALID_TOKEN). */
    public Claims parse(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /** 남은 유효시간(ms) — Redis 블랙리스트 TTL 산정용. */
    public long remainingMillis(Claims claims) {
        long remain = claims.getExpiration().getTime() - System.currentTimeMillis();
        return Math.max(remain, 0);
    }
}
