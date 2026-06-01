package kr.onwork.auth.dto;

import kr.onwork.common.domain.User;

/** 로그인/내 정보 응답에 쓰는 사용자 요약. */
public record UserInfo(
        Long id,
        String name,
        String email,
        String role,
        String position,
        String departmentName
) {
    public static UserInfo from(User user) {
        String dept = user.getDepartment() != null ? user.getDepartment().getName() : null;
        return new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.displayPosition(),
                dept
        );
    }
}
