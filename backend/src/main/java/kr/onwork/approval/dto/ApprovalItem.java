package kr.onwork.approval.dto;

/**
 * 통합 결재함 항목 — 모듈별 대기 건을 한 목록으로.
 * ageDays(생성 후 경과일), urgent(2일 이상 미처리) 포함 — 결재 피로도 개선 #3.
 */
public record ApprovalItem(
        String type,        // LEAVE / OVERTIME / HR
        Long refId,
        String title,
        String requesterName,
        String requesterDepartment,
        String summary,
        int ageDays,
        boolean urgent
) {
}
