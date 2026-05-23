package kr.onwork.onboarding.dto;

import kr.onwork.onboarding.domain.OnboardingProgress;

public record OnboardingResponse(
        Long id,
        String tutorialCode,
        String status,
        int currentStep
) {
    public static OnboardingResponse from(OnboardingProgress p) {
        return new OnboardingResponse(p.getId(), p.getTutorialCode(), p.getStatus().name(), p.getCurrentStep());
    }
}
