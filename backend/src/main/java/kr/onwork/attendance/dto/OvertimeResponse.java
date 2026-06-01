package kr.onwork.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.onwork.attendance.domain.OvertimeRequest;

public record OvertimeResponse(
        Long id,
        Long userId,
        String userName,
        LocalDate requestDate,
        LocalDateTime expectedStartAt,
        LocalDateTime expectedEndAt,
        String reason,
        String status,
        Long approverId,
        String rejectReason,
        LocalDateTime createdAt
) {
    public static OvertimeResponse from(OvertimeRequest r) {
        return from(r, null);
    }

    public static OvertimeResponse from(OvertimeRequest r, String userName) {
        return new OvertimeResponse(r.getId(), r.getUserId(), userName, r.getRequestDate(),
                r.getExpectedStartAt(), r.getExpectedEndAt(), r.getReason(),
                r.getStatus().name(), r.getApproverId(), r.getRejectReason(), r.getCreatedAt());
    }
}
