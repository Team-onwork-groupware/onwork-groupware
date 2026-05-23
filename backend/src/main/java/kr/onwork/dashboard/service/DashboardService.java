package kr.onwork.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import kr.onwork.attendance.domain.DailyWorkRecord;
import kr.onwork.attendance.repository.DailyWorkRecordRepository;
import kr.onwork.attendance.service.AttendanceService;
import kr.onwork.common.domain.Role;
import kr.onwork.common.domain.User;
import kr.onwork.common.repository.UserRepository;
import kr.onwork.common.security.AuthPrincipal;
import kr.onwork.dashboard.dto.DashboardSummary;
import kr.onwork.hr.domain.RequestStatus;
import kr.onwork.hr.repository.HrChangeRequestRepository;
import kr.onwork.leave.repository.LeaveBalanceRepository;
import kr.onwork.leave.service.LeaveService;
import kr.onwork.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 대시보드 위젯 데이터 집계 — 각 모듈 서비스/리포지토리를 조합. */
@Service
public class DashboardService {

    private static final long ANNUAL_TYPE_ID = 1L;

    private final DailyWorkRecordRepository recordRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final HrChangeRequestRepository hrRepository;
    private final NotificationService notificationService;
    private final LeaveService leaveService;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    public DashboardService(DailyWorkRecordRepository recordRepository,
                            LeaveBalanceRepository balanceRepository,
                            HrChangeRequestRepository hrRepository,
                            NotificationService notificationService,
                            LeaveService leaveService,
                            AttendanceService attendanceService,
                            UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.balanceRepository = balanceRepository;
        this.hrRepository = hrRepository;
        this.notificationService = notificationService;
        this.leaveService = leaveService;
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary summary(AuthPrincipal principal) {
        Long uid = principal.userId();
        LocalDate today = LocalDate.now();
        User me = userRepository.findById(uid).orElse(null);

        DailyWorkRecord rec = recordRepository.findByUserIdAndDate(uid, today).orElse(null);
        boolean clockedIn = rec != null && rec.hasClockIn();

        var balance = balanceRepository
                .findByUserIdAndLeaveTypeIdAndYear(uid, ANNUAL_TYPE_ID, (short) today.getYear())
                .orElse(null);
        BigDecimal total = balance != null ? balance.getTotalDays() : BigDecimal.ZERO;
        BigDecimal used = balance != null ? balance.getUsedDays() : BigDecimal.ZERO;
        BigDecimal remaining = balance != null ? balance.remaining() : BigDecimal.ZERO;

        boolean managerUp = principal.role() == Role.MANAGER || principal.role() == Role.HR_MANAGER
                || principal.role() == Role.VP || principal.role() == Role.CEO;
        boolean exec = principal.role() == Role.CEO || principal.role() == Role.VP;

        int pending = leaveService.inbox(principal).size()
                + attendanceService.overtimeInbox(principal).size()
                + (exec ? hrRepository.findByStatusOrderByIdDesc(RequestStatus.PENDING).size() : 0);
        int teamAnomalies = managerUp ? attendanceService.listAnomalies(principal, today).size() : 0;

        return new DashboardSummary(
                me != null ? me.getName() : "",
                principal.role().name(),
                clockedIn,
                rec != null ? rec.getClockInAt() : null,
                rec != null ? rec.getClockOutAt() : null,
                rec != null ? rec.getStatus().name() : "NONE",
                total, used, remaining,
                notificationService.unreadCount(uid),
                pending,
                teamAnomalies
        );
    }
}
