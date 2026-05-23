package kr.onwork.onboarding.web;

import jakarta.validation.Valid;
import kr.onwork.common.security.SecurityUtil;
import kr.onwork.onboarding.dto.OnboardingResponse;
import kr.onwork.onboarding.dto.OnboardingUpdateRequest;
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
}
