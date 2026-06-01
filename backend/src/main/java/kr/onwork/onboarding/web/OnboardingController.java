package kr.onwork.onboarding.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.onwork.common.security.SecurityUtil;
import kr.onwork.onboarding.dto.OnboardingResponse;
import kr.onwork.onboarding.dto.OnboardingStepRequest;
import kr.onwork.onboarding.dto.OnboardingUpdateRequest;
import kr.onwork.onboarding.dto.OnboardingVisibilityRequest;
import kr.onwork.onboarding.service.OnboardingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 온보딩 가이드 투어 API (/api/v1/onboarding). */
@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/{tutorialCode}")
    public OnboardingResponse get(@PathVariable String tutorialCode) {
        return onboardingService.getOrStart(SecurityUtil.currentUserId(), tutorialCode);
    }

    @PatchMapping("/{tutorialCode}")
    public OnboardingResponse update(@PathVariable String tutorialCode,
                                     @Valid @RequestBody OnboardingUpdateRequest req) {
        return onboardingService.update(SecurityUtil.currentUserId(), tutorialCode, req);
    }

    @GetMapping("/tutorials/me")
    public Map<String, Object> tutorialsMe() {
        List<OnboardingResponse> items = onboardingService.listMine(
                SecurityUtil.currentUserId(), SecurityUtil.currentPrincipal().role());
        return Map.of("total", items.size(), "items", items);
    }

    @PatchMapping("/tutorials/me/{tutorialCode}/step")
    public OnboardingResponse step(@PathVariable String tutorialCode,
                                   @Valid @RequestBody OnboardingStepRequest req) {
        return onboardingService.step(SecurityUtil.currentUserId(), tutorialCode, req);
    }

    @PatchMapping("/tutorials/me/{tutorialCode}/visibility")
    public OnboardingResponse visibility(@PathVariable String tutorialCode,
                                         @Valid @RequestBody OnboardingVisibilityRequest req) {
        return onboardingService.visibility(SecurityUtil.currentUserId(), tutorialCode, req);
    }

    @org.springframework.web.bind.annotation.PostMapping("/tutorials/me/{tutorialCode}/restart")
    public OnboardingResponse restart(@PathVariable String tutorialCode) {
        return onboardingService.restart(SecurityUtil.currentUserId(), tutorialCode);
    }
}
