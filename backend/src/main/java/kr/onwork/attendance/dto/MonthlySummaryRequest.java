package kr.onwork.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MonthlySummaryRequest(
        @NotNull @Min(2000) @Max(2100) Integer year,
        @NotNull @Min(1) @Max(12) Integer month,
        Boolean forceConfirm
) {
    public boolean force() {
        return Boolean.TRUE.equals(forceConfirm);
    }
}
