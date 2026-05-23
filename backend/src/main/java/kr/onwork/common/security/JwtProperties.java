package kr.onwork.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** onwork.jwt.* 바인딩 (application.yml). */
@ConfigurationProperties(prefix = "onwork.jwt")
public record JwtProperties(
        String secret,
        long accessTokenValidityMinutes,
        long refreshTokenValidityDays
) {
}
