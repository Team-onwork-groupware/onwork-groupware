package kr.onwork.common.domain;

/** 계정 상태. RESIGNED는 로그인 차단(soft delete, ADR-HR-003). */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    RESIGNED
}
