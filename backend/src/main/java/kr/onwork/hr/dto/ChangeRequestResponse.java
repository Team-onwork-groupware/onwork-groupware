package kr.onwork.hr.dto;

import java.time.LocalDateTime;
import java.util.Map;
import kr.onwork.hr.domain.HrChangeRequest;

/** 인사 변경 요청 응답 (결재함 항목). */
public record ChangeRequestResponse(
        Long id,
        String changeType,
        Long targetUserId,
        Map<String, Object> payload,
        String reason,
        String status,
        Long requestedBy,
        Long approverId,
        String rejectReason,
        LocalDateTime createdAt,
        String batchId
) {
    public static ChangeRequestResponse from(HrChangeRequest r) {
        return new ChangeRequestResponse(
                r.getId(),
                r.getChangeType().name(),
                r.getTargetUserId(),
                r.getPayload(),
                r.getReason(),
                r.getStatus().name(),
                r.getRequestedBy(),
                r.getApproverId(),
                r.getRejectReason(),
                r.getCreatedAt(),
                r.getBatchId()
        );
    }
}
