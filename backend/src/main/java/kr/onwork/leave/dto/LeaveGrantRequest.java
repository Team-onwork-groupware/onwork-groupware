package kr.onwork.leave.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record LeaveGrantRequest(
        @NotEmpty List<Long> userIds,
        @NotNull @DecimalMin("0.5") BigDecimal days,
        Integer year,
        String reason
) {
}
