package kr.onwork.hr.domain;

/**
 * 인사 변경 요청 처리 상태 (hr_change_requests.status).
 * 흐름: DRAFT → PENDING → APPROVED/REJECTED. DRAFT/PENDING 단계에서 본인이 CANCELLED 가능.
 * DRAFT(임시저장)는 본인만 조회·수정·삭제 가능, 알림 미발송 (UC-HR-01 A1).
 */
public enum RequestStatus {
    DRAFT,
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
