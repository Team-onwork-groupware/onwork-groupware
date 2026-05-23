package kr.onwork.approval.web;

import java.util.List;
import java.util.Map;
import kr.onwork.approval.dto.ApprovalItem;
import kr.onwork.approval.service.ApprovalService;
import kr.onwork.common.security.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 통합 결재함 API (/api/v1/approvals). */
@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/inbox")
    public Map<String, Object> inbox() {
        List<ApprovalItem> items = approvalService.inbox(SecurityUtil.currentPrincipal());
        return Map.of("total", items.size(), "items", items);
    }
}
