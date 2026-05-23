package kr.onwork.dashboard.web;

import kr.onwork.common.security.SecurityUtil;
import kr.onwork.dashboard.dto.DashboardSummary;
import kr.onwork.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 대시보드 API (/api/v1/dashboard). */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardService.summary(SecurityUtil.currentPrincipal());
    }
}
