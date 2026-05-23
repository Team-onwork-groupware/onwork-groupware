package kr.onwork.common.security;

import kr.onwork.common.domain.Role;

/** SecurityContext에 저장되는 인증 주체. 컨트롤러/서비스에서 현재 사용자 식별에 사용. */
public record AuthPrincipal(Long userId, String email, Role role) {
}
