package kr.onwork.notification.dto;

import java.util.List;

/** 알림 요약 — 결재 피로도 개선 #5. 이벤트 폭주 대신 1회 호출로 핵심 지표 + 최근 항목만. */
public record NotificationDigest(
        long unread,
        int pendingApprovals,
        int longPending,
        long recentApproved,
        List<NotificationResponse> recentItems
) {
}
