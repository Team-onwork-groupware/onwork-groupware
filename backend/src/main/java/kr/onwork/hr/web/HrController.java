package kr.onwork.hr.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.onwork.common.domain.UserStatus;
import kr.onwork.common.security.SecurityUtil;
import kr.onwork.hr.domain.RequestStatus;
import kr.onwork.hr.dto.ChangeRequestResponse;
import kr.onwork.hr.dto.CreateChangeRequestRequest;
import kr.onwork.hr.dto.EmployeeResponse;
import kr.onwork.hr.dto.ProcessRequest;
import kr.onwork.hr.service.HrService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 인사관리 API (/api/v1/hr). */
@RestController
@RequestMapping("/api/v1/hr")
public class HrController {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    /** 직원 목록 조회 (역할별 범위 차등). */
    @GetMapping("/employees")
    public Map<String, Object> listEmployees(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword) {
        List<EmployeeResponse> items =
                hrService.listEmployees(SecurityUtil.currentPrincipal(), departmentId, status, keyword);
        return Map.of("total", items.size(), "items", items);
    }

    /** 직원 상세 조회. */
    @GetMapping("/employees/{id}")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return hrService.getEmployee(SecurityUtil.currentPrincipal(), id);
    }

    /** 인사 변경 요청 등록 (입사/수정/퇴사 통합) — 경영지원팀(HR_MANAGER). */
    @PreAuthorize("hasRole('HR_MANAGER')")
    @PostMapping("/change-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestResponse create(@Valid @RequestBody CreateChangeRequestRequest req) {
        return hrService.createChangeRequest(SecurityUtil.currentPrincipal(), req);
    }

    /** 인사 변경 요청 목록 (결재함). */
    @GetMapping("/change-requests")
    public Map<String, Object> listChangeRequests(@RequestParam(required = false) RequestStatus status) {
        List<ChangeRequestResponse> items =
                hrService.listChangeRequests(SecurityUtil.currentPrincipal(), status);
        return Map.of("total", items.size(), "items", items);
    }

    /** 인사 변경 요청 승인/반려 — 경영진(VP 이상). */
    @PreAuthorize("hasRole('VP')")
    @PatchMapping("/change-requests/{id}/process")
    public ChangeRequestResponse process(@PathVariable Long id, @Valid @RequestBody ProcessRequest req) {
        return hrService.process(SecurityUtil.currentPrincipal(), id, req);
    }
}
