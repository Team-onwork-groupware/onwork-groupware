package kr.onwork.common.security;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** 로그인 5회 연속 실패 시 30분 잠금 (ADR-SYS-002). 카운터는 Redis. */
@Service
public class LoginAttemptService {

    private static final String PREFIX = "login:fail:";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(30);

    private final StringRedisTemplate redis;

    public LoginAttemptService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public boolean isLocked(String email) {
        String v = redis.opsForValue().get(PREFIX + email);
        return v != null && Integer.parseInt(v) >= MAX_ATTEMPTS;
    }

    public void recordFailure(String email) {
        String key = PREFIX + email;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, LOCK_DURATION);
        }
    }

    public void reset(String email) {
        redis.delete(PREFIX + email);
    }
}
