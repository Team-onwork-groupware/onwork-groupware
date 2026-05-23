package kr.onwork.attendance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 일일 근무 기록 출퇴근 + 이상 상태 전이. */
class DailyWorkRecordTest {

    @Test
    void clockIn_normal_keepsNormal() {
        DailyWorkRecord r = DailyWorkRecord.of(1L, LocalDate.now());
        r.clockIn(LocalDateTime.now(), false);
        assertThat(r.hasClockIn()).isTrue();
        assertThat(r.getStatus()).isEqualTo(WorkStatus.NORMAL);
    }

    @Test
    void clockIn_late_marksAnomaly() {
        DailyWorkRecord r = DailyWorkRecord.of(1L, LocalDate.now());
        r.clockIn(LocalDateTime.now(), true);
        assertThat(r.getStatus()).isEqualTo(WorkStatus.ANOMALY);
    }

    @Test
    void clockOut_recordsOvertimeAndTime() {
        DailyWorkRecord r = DailyWorkRecord.of(1L, LocalDate.now());
        r.clockIn(LocalDateTime.now(), false);
        r.clockOut(LocalDateTime.now(), 30, false);
        assertThat(r.hasClockOut()).isTrue();
        assertThat(r.getOvertimeMinutes()).isEqualTo(30);
    }

    @Test
    void markAnomaly_setsAnomaly() {
        DailyWorkRecord r = DailyWorkRecord.of(1L, LocalDate.now());
        r.markAnomaly();
        assertThat(r.getStatus()).isEqualTo(WorkStatus.ANOMALY);
    }
}
