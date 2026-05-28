package kr.onwork.approval.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kr.onwork.approval.dto.ApprovalItem;
import kr.onwork.approval.dto.BatchProcessRequest;
import kr.onwork.approval.dto.BatchProcessResponse;
import kr.onwork.attendance.dto.AttendanceProcessRequest;
import kr.onwork.attendance.service.AttendanceService;
import kr.onwork.common.domain.Role;
import kr.onwork.common.domain.User;
import kr.onwork.common.error.BusinessException;
import kr.onwork.common.repository.UserRepository;
import kr.onwork.common.security.AuthPrincipal;
import kr.onwork.hr.domain.RequestStatus;
import kr.onwork.hr.dto.ProcessRequest;
import kr.onwork.hr.repository.HrChangeRequestRepository;
import kr.onwork.hr.service.HrService;
import kr.onwork.leave.dto.LeaveProcessRequest;
import kr.onwork.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통합 결재함 — 휴가/시간외/인사 대기 건을 한 곳에서 보고 일괄 처리.
 * 결재 피로도 개선 #2(일괄 처리) + #3(에이징/긴급도 정렬·배지).
 */
@Service
public class ApprovalService {

    private static final long URGENT_DAYS = 2L;   // 이 일수 이상 미처리면 긴급

    private final LeaveService leaveService;
    private final AttendanceService attendanceService;
    private final HrService hrService;
    private final HrChangeRequestRepository hrRepository;
    private final UserRepository userRepository;

    public ApprovalService(LeaveService leaveService,
                           AttendanceService attendanceService,
                           HrService hrService,
                           HrChangeRequestRepository hrRepository,
                           UserRepository userRepository) {
        this.leaveService = leaveService;
        this.attendanceService = attendanceService;
        this.hrService = hrService;
        this.hrRepository = hrRepository;
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------------- 결재함 (정렬·에이징)
    @Transactional(readOnly = true)
    public List<ApprovalItem> inbox(AuthPrincipal principal) {
        List<ApprovalItem> items = new ArrayList<>();

        leaveService.inbox(principal).forEach(r -> items.add(item(
                "LEAVE", r.id(), "휴가 신청", r.userName(),
                r.startDate() + " ~ " + r.endDate() + " (" + r.daysUsed() + "일)",
                r.createdAt())));

        attendanceService.overtimeInbox(principal).forEach(r -> items.add(item(
                "OVERTIME", r.id(), "시간외근로", nameOf(r.userId()),
                r.requestDate() + " " + r.reason(),
                r.createdAt())));

        if (principal.role() == Role.CEO || principal.role() == Role.VP) {
            hrRepository.findByStatusOrderByIdDesc(RequestStatus.PENDING).forEach(r -> items.add(item(
                    "HR", r.getId(), "인사 변경(" + r.getChangeType().name() + ")",
                    nameOf(r.getRequestedBy()), r.getReason() != null ? r.getReason() : "",
                    r.getCreatedAt())));
        }

        // 결재 피로도 개선 #3: 긴급(>=2일) 먼저, 같은 등급에선 오래된 순
        items.sort(Comparator
                .comparing(ApprovalItem::urgent).reversed()
                .thenComparing(Comparator.comparingInt(ApprovalItem::ageDays).reversed()));
        return items;
    }

    // ---------------------------------------------------------------- 일괄 처리
    @Transactional
    public BatchProcessResponse batchProcess(AuthPrincipal principal, BatchProcessRequest req) {
        List<BatchProcessResponse.Result> results = new ArrayList<>();
        int ok = 0, fail = 0;
        for (BatchProcessRequest.Item it : req.items()) {
            try {
                dispatch(principal, it.type(), it.id(), req.action(), req.reason());
                results.add(new BatchProcessResponse.Result(it.type(), it.id(), true, null));
                ok++;
            } catch (BusinessException be) {
                results.add(new BatchProcessResponse.Result(it.type(), it.id(), false,
                        be.getErrorCode().name() + ": " + be.getMessage()));
                fail++;
            } catch (RuntimeException ex) {
                results.add(new BatchProcessResponse.Result(it.type(), it.id(), false, ex.getMessage()));
                fail++;
            }
        }
        return new BatchProcessResponse(req.items().size(), ok, fail, results);
    }

    private void dispatch(AuthPrincipal principal, String type, Long id,
                          BatchProcessRequest.Action action, String reason) {
        switch (type.toUpperCase()) {
            case "LEAVE" -> {
                LeaveProcessRequest.Action a = action == BatchProcessRequest.Action.APPROVE
                        ? LeaveProcessRequest.Action.APPROVE : LeaveProcessRequest.Action.ON_HOLD;
                leaveService.process(principal, id, new LeaveProcessRequest(a, reason));
            }
            case "OVERTIME" -> {
                AttendanceProcessRequest.Action a = action == BatchProcessRequest.Action.APPROVE
                        ? AttendanceProcessRequest.Action.APPROVE
                        : AttendanceProcessRequest.Action.REJECT;
                attendanceService.processOvertime(principal, id, new AttendanceProcessRequest(a, reason));
            }
            case "HR" -> {
                ProcessRequest.Action a = action == BatchProcessRequest.Action.APPROVE
                        ? ProcessRequest.Action.APPROVE : ProcessRequest.Action.REJECT;
                hrService.process(principal, id, new ProcessRequest(a, reason));
            }
            default -> throw new IllegalArgumentException("알 수 없는 결재 타입: " + type);
        }
    }

    // ---------------------------------------------------------------- helpers
    private ApprovalItem item(String type, Long id, String title, String requester,
                              String summary, LocalDateTime createdAt) {
        long days = createdAt != null
                ? Math.max(Duration.between(createdAt, LocalDateTime.now()).toDays(), 0L) : 0L;
        return new ApprovalItem(type, id, title, requester, summary,
                (int) days, days >= URGENT_DAYS);
    }

    private String nameOf(Long userId) {
        return userRepository.findById(userId).map(User::getName).orElse("?");
    }
}
