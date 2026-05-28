package kr.onwork.approval.scheduler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.onwork.attendance.domain.OvertimeStatus;
import kr.onwork.attendance.repository.OvertimeRequestRepository;
import kr.onwork.common.domain.Department;
import kr.onwork.common.domain.Role;
import kr.onwork.common.domain.User;
import kr.onwork.common.domain.UserStatus;
import kr.onwork.common.repository.DepartmentRepository;
import kr.onwork.common.repository.UserRepository;
import kr.onwork.hr.domain.HrChangeRequest;
import kr.onwork.hr.domain.RequestStatus;
import kr.onwork.hr.repository.HrChangeRequestRepository;
import kr.onwork.leave.domain.LeaveApprover;
import kr.onwork.leave.domain.LeaveStatus;
import kr.onwork.leave.repository.LeaveApproverRepository;
import kr.onwork.leave.repository.LeaveRequestRepository;
import kr.onwork.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결재 피로도 개선 #4 — 자동 에스컬레이션.
 * 매일 09:00 KST, 3일 이상 PENDING 상태인 결재 건을 집계해 현재 유효 결재자에게
 * 요약 알림 1건을 발송한다(인지 강화 — 자동 라우팅은 아님).
 */
@Component
public class EscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EscalationScheduler.class);
    private static final long THRESHOLD_DAYS = 3L;

    private final LeaveRequestRepository leaveRepo;
    private final OvertimeRequestRepository overtimeRepo;
    private final HrChangeRequestRepository hrRepo;
    private final LeaveApproverRepository leaveApproverRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final NotificationService notificationService;

    public EscalationScheduler(LeaveRequestRepository leaveRepo,
                               OvertimeRequestRepository overtimeRepo,
                               HrChangeRequestRepository hrRepo,
                               LeaveApproverRepository leaveApproverRepo,
                               UserRepository userRepo,
                               DepartmentRepository deptRepo,
                               NotificationService notificationService) {
        this.leaveRepo = leaveRepo;
        this.overtimeRepo = overtimeRepo;
        this.hrRepo = hrRepo;
        this.leaveApproverRepo = leaveApproverRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void daily() {
        int sent = runNow();
        log.info("[결재 에스컬레이션] 장기 대기 알림 {}건 발송", sent);
    }

    /** 운영용 즉시 실행(스케줄러와 동일 로직, 컨트롤러에서 호출 가능). */
    @Transactional
    public int runNow() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(THRESHOLD_DAYS);
        Map<Long, Integer> bucket = new HashMap<>();   // approver_id -> 누적 건수

        // 1) 휴가 PENDING — 부서 leave_approver의 active approver
        leaveRepo.findByStatusAndCreatedAtBefore(LeaveStatus.PENDING, threshold).forEach(lr -> {
            Long approverId = activeLeaveApprover(lr.getUserId());
            if (approverId != null) {
                bucket.merge(approverId, 1, Integer::sum);
            }
        });

        // 2) 시간외 PENDING — 부서 매니저
        overtimeRepo.findByStatusAndCreatedAtBefore(OvertimeStatus.PENDING, threshold).forEach(ot -> {
            Long managerId = departmentManager(ot.getUserId());
            if (managerId != null) {
                bucket.merge(managerId, 1, Integer::sum);
            }
        });

        // 3) HR PENDING — CEO/VP 전체에게 동일 카운트
        List<HrChangeRequest> hrPending = hrRepo.findByStatusAndCreatedAtBefore(RequestStatus.PENDING, threshold);
        if (!hrPending.isEmpty()) {
            userRepo.findByRoleInAndStatus(List.of(Role.CEO, Role.VP), UserStatus.ACTIVE)
                    .forEach(u -> bucket.merge(u.getId(), hrPending.size(), Integer::sum));
        }

        // 결재자별 요약 알림 1건
        int sent = 0;
        for (Map.Entry<Long, Integer> e : bucket.entrySet()) {
            notificationService.notify(e.getKey(), NotificationService.APPROVAL_LONG_PENDING,
                    "APPROVAL", null,
                    e.getValue() + "건의 결재가 3일 이상 대기 중입니다 — 확인 부탁드립니다");
            sent++;
        }
        return sent;
    }

    private Long activeLeaveApprover(Long requesterId) {
        User u = userRepo.findById(requesterId).orElse(null);
        if (u == null || u.getDepartment() == null) {
            return null;
        }
        return leaveApproverRepo.findByDepartmentId(u.getDepartment().getId())
                .map(LeaveApprover::activeApproverId).orElse(null);
    }

    private Long departmentManager(Long requesterId) {
        User u = userRepo.findById(requesterId).orElse(null);
        if (u == null || u.getDepartment() == null) {
            return null;
        }
        return deptRepo.findById(u.getDepartment().getId())
                .map(Department::getManagerId).orElse(null);
    }
}
