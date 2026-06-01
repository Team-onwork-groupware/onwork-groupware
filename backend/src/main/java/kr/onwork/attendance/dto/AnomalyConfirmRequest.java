package kr.onwork.attendance.dto;

import kr.onwork.attendance.domain.AnomalyType;

public record AnomalyConfirmRequest(
        AnomalyType anomalyType,
        Boolean overtimeApproved
) {
}
