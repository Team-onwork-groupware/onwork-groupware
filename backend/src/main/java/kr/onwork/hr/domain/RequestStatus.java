package kr.onwork.hr.domain;

/** 인사 변경 요청 처리 상태 (hr_change_requests.status). PENDING→APPROVED/REJECTED 단방향. */
public enum RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
