package kr.onwork.leave.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.onwork.common.dto.ApproverView;
import kr.onwork.leave.domain.LeaveRequest;

public record LeaveRequestResponse(
        Long id,
        Long userId,
        String userName,
        String userDepartment,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal daysUsed,
        String reason,
        String status,
        Long approverId,
        boolean delegated,
        String holdReason,
        LocalDateTime createdAt,
        ApproverView approver
) {
    public static LeaveRequestResponse of(LeaveRequest r, String userName) {
        return of(r, userName, null, null);
    }

    public static LeaveRequestResponse of(LeaveRequest r, String userName, ApproverView approver) {
        return of(r, userName, null, approver);
    }

    public static LeaveRequestResponse of(LeaveRequest r, String userName, String userDepartment,
                                          ApproverView approver) {
        return new LeaveRequestResponse(r.getId(), r.getUserId(), userName, userDepartment,
                r.getStartDate(), r.getEndDate(), r.getDaysUsed(), r.getReason(),
                r.getStatus().name(), r.getApproverId(), r.isDelegated(), r.getHoldReason(),
                r.getCreatedAt(), approver);
    }
}
