package kr.onwork.approval.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.onwork.approval.dto.ApprovalItem;
import kr.onwork.approval.dto.ApprovalProcessRequest;
import kr.onwork.approval.dto.BatchProcessRequest;
import kr.onwork.approval.dto.BatchProcessResponse;
import kr.onwork.approval.scheduler.EscalationScheduler;
import kr.onwork.approval.service.ApprovalService;
import kr.onwork.common.security.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 통합 결재함 API (/api/v1/approvals). */
@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final EscalationScheduler escalationScheduler;

    public ApprovalController(ApprovalService approvalService,
                              EscalationScheduler escalationScheduler) {
        this.approvalService = approvalService;
        this.escalationScheduler = escalationScheduler;
    }

    @GetMapping("/inbox")
    public Map<String, Object> inbox() {
        List<ApprovalItem> items = approvalService.inbox(SecurityUtil.currentPrincipal());
        long urgent = items.stream().filter(ApprovalItem::urgent).count();
        return Map.of("total", items.size(), "urgent", urgent, "items", items);
    }

    @GetMapping
    public Map<String, Object> approvals() {
        List<ApprovalItem> items = approvalService.inbox(SecurityUtil.currentPrincipal());
        long urgent = items.stream().filter(ApprovalItem::urgent).count();
        return Map.of("total", items.size(), "urgent", urgent, "items", items);
    }

    @PatchMapping("/{id}/process")
    public void process(@PathVariable Long id, @Valid @RequestBody ApprovalProcessRequest req) {
        approvalService.process(SecurityUtil.currentPrincipal(), id, req);
    }

    /** 결재 피로도 개선 #2: 선택한 여러 건 일괄 승인/반려. */
    @PostMapping("/batch")
    public BatchProcessResponse batch(@Valid @RequestBody BatchProcessRequest req) {
        return approvalService.batchProcess(SecurityUtil.currentPrincipal(), req);
    }

    /** 운영용: 자동 에스컬레이션 즉시 실행 (스케줄러와 동일 로직, CEO 전용). */
    @PreAuthorize("hasRole('CEO')")
    @PostMapping("/admin/escalate")
    public Map<String, Object> escalateNow() {
        int sent = escalationScheduler.runNow();
        return Map.of("escalatedNotifications", sent);
    }
}
