package kr.onwork.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.onwork.attendance.domain.OvertimeRequest;
import kr.onwork.common.dto.ApproverView;

public record OvertimeResponse(
        Long id,
        Long userId,
        String userName,
        String userDepartment,
        LocalDate requestDate,
        LocalDateTime expectedStartAt,
        LocalDateTime expectedEndAt,
        String reason,
        String status,
        Long approverId,
        String rejectReason,
        LocalDateTime createdAt,
        ApproverView approver
) {
    public static OvertimeResponse from(OvertimeRequest r) {
        return from(r, null, null, null);
    }

    public static OvertimeResponse from(OvertimeRequest r, String userName) {
        return from(r, userName, null, null);
    }

    public static OvertimeResponse from(OvertimeRequest r, String userName, ApproverView approver) {
        return from(r, userName, null, approver);
    }

    public static OvertimeResponse from(OvertimeRequest r, String userName, String userDepartment,
                                        ApproverView approver) {
        return new OvertimeResponse(r.getId(), r.getUserId(), userName, userDepartment, r.getRequestDate(),
                r.getExpectedStartAt(), r.getExpectedEndAt(), r.getReason(),
                r.getStatus().name(), r.getApproverId(), r.getRejectReason(), r.getCreatedAt(), approver);
    }
}
