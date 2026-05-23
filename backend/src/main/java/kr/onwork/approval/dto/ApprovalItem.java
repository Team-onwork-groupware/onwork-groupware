package kr.onwork.approval.dto;

/** 통합 결재함 항목 — 모듈별 대기 건을 한 목록으로. type별 처리 엔드포인트는 프론트가 분기. */
public record ApprovalItem(
        String type,        // LEAVE / OVERTIME / HR
        Long refId,
        String title,
        String requesterName,
        String summary
) {
}
