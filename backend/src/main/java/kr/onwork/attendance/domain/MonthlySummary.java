package kr.onwork.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "monthly_summaries")
public class MonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;

    @Column(name = "late_count", nullable = false)
    private int lateCount;

    @Column(name = "early_leave_count", nullable = false)
    private int earlyLeaveCount;

    @Column(name = "absent_count", nullable = false)
    private int absentCount;

    @Column(name = "total_overtime_minutes", nullable = false)
    private int totalOvertimeMinutes;

    @Column(name = "closed_by", nullable = false)
    private Long closedBy;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    public static MonthlySummary close(Long userId, String yearMonth, int lateCount, int earlyLeaveCount,
                                       int absentCount, int totalOvertimeMinutes, Long closedBy) {
        MonthlySummary s = new MonthlySummary();
        s.userId = userId;
        s.yearMonth = yearMonth;
        s.lateCount = lateCount;
        s.earlyLeaveCount = earlyLeaveCount;
        s.absentCount = absentCount;
        s.totalOvertimeMinutes = totalOvertimeMinutes;
        s.closedBy = closedBy;
        s.closedAt = LocalDateTime.now();
        return s;
    }
}
