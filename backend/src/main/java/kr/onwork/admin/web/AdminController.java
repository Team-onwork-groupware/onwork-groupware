package kr.onwork.admin.web;

import java.util.Map;
import kr.onwork.admin.service.DemoResetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 시연용 운영 엔드포인트 (/api/v1/admin). 인증된 사용자만 호출 가능. */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final DemoResetService demoResetService;

    public AdminController(DemoResetService demoResetService) {
        this.demoResetService = demoResetService;
    }

    /** 시연 데이터 초기화 — 기본 더미데이터 복원 + 개발팀장·기획팀장 오늘 휴가 세팅. */
    @PostMapping("/demo-reset")
    public Map<String, Object> demoReset() {
        demoResetService.reset();
        return Map.of("status", "ok", "message", "기본 더미데이터로 초기화되었습니다");
    }
}
