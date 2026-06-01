package kr.onwork.leave.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 휴가 잔여 (leave_balances). 최종 승인 시점에만 used_days 차감(승인 전 미반영). */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "leave_balances")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "leave_type_id", nullable = false)
    private Long leaveTypeId;

    @Column(name = "total_days", nullable = false)
    private BigDecimal totalDays;

    @Column(name = "used_days", nullable = false)
    private BigDecimal usedDays;

    @Column(name = "year", nullable = false)
    private short year;

    public BigDecimal remaining() {
        return totalDays.subtract(usedDays);
    }

    public static LeaveBalance create(Long userId, Long leaveTypeId, BigDecimal totalDays, short year) {
        LeaveBalance b = new LeaveBalance();
        b.userId = userId;
        b.leaveTypeId = leaveTypeId;
        b.totalDays = totalDays;
        b.usedDays = BigDecimal.ZERO;
        b.year = year;
        return b;
    }

    public void grant(BigDecimal days) {
        this.totalDays = this.totalDays.add(days);
    }

    public void deduct(BigDecimal days) {
        this.usedDays = this.usedDays.add(days);
    }

    public void restore(BigDecimal days) {
        this.usedDays = this.usedDays.subtract(days);
        if (this.usedDays.signum() < 0) {
            this.usedDays = BigDecimal.ZERO;
        }
    }
}
