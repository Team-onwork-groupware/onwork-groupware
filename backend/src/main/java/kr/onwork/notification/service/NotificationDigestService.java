package kr.onwork.notification.service;

import java.util.List;
import kr.onwork.approval.dto.ApprovalItem;
import kr.onwork.approval.service.ApprovalService;
import kr.onwork.common.security.AuthPrincipal;
import kr.onwork.notification.dto.NotificationDigest;
import kr.onwork.notification.dto.NotificationResponse;
import kr.onwork.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 다이제스트 — 결재 피로도 개선 #5.
 * NotificationService와 ApprovalService 사이의 순환 의존을 피하기 위해 별도 서비스로 분리.
 */
@Service
public class NotificationDigestService {

    private static final int RECENT_LIMIT = 5;

    private final NotificationRepository repository;
    private final ApprovalService approvalService;

    public NotificationDigestService(NotificationRepository repository,
                                     ApprovalService approvalService) {
        this.repository = repository;
        this.approvalService = approvalService;
    }

    @Transactional(readOnly = true)
    public NotificationDigest digest(AuthPrincipal principal) {
        long unread = repository.countByUserIdAndIsReadFalse(principal.userId());

        List<ApprovalItem> inbox = approvalService.inbox(principal);
        int pendingApprovals = inbox.size();
        int longPending = (int) inbox.stream().filter(ApprovalItem::urgent).count();

        List<NotificationResponse> recent = repository.findByUserIdOrderByIdDesc(principal.userId())
                .stream().limit(RECENT_LIMIT).map(NotificationResponse::from).toList();

        long recentApproved = recent.stream()
                .filter(n -> n.type().contains("APPROVED")).count();

        return new NotificationDigest(unread, pendingApprovals, longPending, recentApproved, recent);
    }
}
