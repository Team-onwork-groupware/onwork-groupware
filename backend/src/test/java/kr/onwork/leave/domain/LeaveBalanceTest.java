package kr.onwork.leave.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/** 휴가 잔여 차감/복원 로직 (승인 차감 + 취소 롤백). */
class LeaveBalanceTest {

    private LeaveBalance balance(String total, String used) {
        LeaveBalance b = new LeaveBalance();
        ReflectionTestUtils.setField(b, "totalDays", new BigDecimal(total));
        ReflectionTestUtils.setField(b, "usedDays", new BigDecimal(used));
        return b;
    }

    @Test
    void remaining_isTotalMinusUsed() {
        assertThat(balance("20.0", "3.0").remaining()).isEqualByComparingTo("17.0");
    }

    @Test
    void deduct_thenRestore_roundTrips() {
        LeaveBalance b = balance("20.0", "0.0");
        b.deduct(new BigDecimal("3.0"));
        assertThat(b.remaining()).isEqualByComparingTo("17.0");
        b.restore(new BigDecimal("3.0"));
        assertThat(b.remaining()).isEqualByComparingTo("20.0");
    }

    @Test
    void restore_neverGoesNegative() {
        LeaveBalance b = balance("20.0", "1.0");
        b.restore(new BigDecimal("5.0"));
        assertThat(b.getUsedDays()).isEqualByComparingTo("0.0");
    }
}
