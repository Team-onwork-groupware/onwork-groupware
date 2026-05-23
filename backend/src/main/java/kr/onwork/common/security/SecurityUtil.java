package kr.onwork.common.security;

import kr.onwork.common.error.BusinessException;
import kr.onwork.common.error.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** 현재 인증 주체 조회 헬퍼. */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static AuthPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return principal;
    }

    public static Long currentUserId() {
        return currentPrincipal().userId();
    }
}
