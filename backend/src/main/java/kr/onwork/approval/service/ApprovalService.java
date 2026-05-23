package kr.onwork.approval.service;

import java.util.ArrayList;
import java.util.List;
import kr.onwork.approval.dto.ApprovalItem;
import kr.onwork.attendance.service.AttendanceService;
import kr.onwork.common.domain.Role;
import kr.onwork.common.domain.User;
import kr.onwork.common.repository.UserRepository;
import kr.onwork.common.security.AuthPrincipal;
import kr.onwork.hr.domain.RequestStatus;
import kr.onwork.hr.repository.HrChangeRequestRepository;
import kr.onwork.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 통합 결재함 — 휴가/시간외/인사 대기 건을 현재 사용자 권한 범위로 합쳐 제공. */
@Service
public class ApprovalService {

    private final LeaveService leaveService;
    private final AttendanceService attendanceService;
    private final HrChangeRequestRepository hrRepository;
    private final UserRepository userRepository;

    public ApprovalService(LeaveService leaveService,
                           AttendanceService attendanceService,
                           HrChangeRequestRepository hrRepository,
                           UserRepository userRepository) {
        this.leaveService = leaveService;
        this.attendanceService = attendanceService;
        this.hrRepository = hrRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ApprovalItem> inbox(AuthPrincipal principal) {
        List<ApprovalItem> items = new ArrayList<>();

        leaveService.inbox(principal).forEach(r -> items.add(new ApprovalItem(
                "LEAVE", r.id(), "휴가 신청", r.userName(),
                r.startDate() + " ~ " + r.endDate() + " (" + r.daysUsed() + "일)")));

        attendanceService.overtimeInbox(principal).forEach(r -> items.add(new ApprovalItem(
                "OVERTIME", r.id(), "시간외근로", nameOf(r.userId()),
                r.requestDate() + " " + r.reason())));

        if (principal.role() == Role.CEO || principal.role() == Role.VP) {
            hrRepository.findByStatusOrderByIdDesc(RequestStatus.PENDING).forEach(r -> items.add(new ApprovalItem(
                    "HR", r.getId(), "인사 변경(" + r.getChangeType().name() + ")",
                    nameOf(r.getRequestedBy()), r.getReason() != null ? r.getReason() : "")));
        }
        return items;
    }

    private String nameOf(Long userId) {
        return userRepository.findById(userId).map(User::getName).orElse("?");
    }
}
