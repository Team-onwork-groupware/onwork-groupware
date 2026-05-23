package kr.onwork.common.domain;

/** RBAC 5단계 (ADR-SYS-002 / API 명세). 상위가 하위 권한을 포함한다. */
public enum Role {
    CEO,
    VP,
    HR_MANAGER,
    MANAGER,
    EMPLOYEE
}
