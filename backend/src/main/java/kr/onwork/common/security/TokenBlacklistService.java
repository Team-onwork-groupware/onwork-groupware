package kr.onwork.common.security;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** 로그아웃/강제 만료 시 Access Token을 Redis 블랙리스트에 등록 (ADR-SYS-002). */
@Service
public class TokenBlacklistService {

    private static final String PREFIX = "bl:";

    private final StringRedisTemplate redis;

    public TokenBlacklistService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void blacklist(String token, long ttlMillis) {
        if (ttlMillis <= 0) {
            return;
        }
        redis.opsForValue().set(PREFIX + token, "1", Duration.ofMillis(ttlMillis));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + token));
    }
}
