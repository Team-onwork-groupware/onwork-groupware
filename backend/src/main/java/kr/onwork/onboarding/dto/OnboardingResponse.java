package kr.onwork.onboarding.dto;

import java.time.LocalDateTime;
import kr.onwork.onboarding.domain.OnboardingProgress;

public record OnboardingResponse(
        Long id,
        String tutorialCode,
        String status,
        int currentStep,
        boolean autoVisible,
        boolean expired,
        LocalDateTime lastShownAt,
        LocalDateTime expiresAt
) {
    public static OnboardingResponse from(OnboardingProgress p, boolean autoVisible, boolean expired) {
        LocalDateTime expiresAt = p.getCreatedAt() != null ? p.getCreatedAt().plusDays(30) : null;
        return new OnboardingResponse(p.getId(), p.getTutorialCode(), p.getStatus().name(),
                p.getCurrentStep(), autoVisible, expired, p.getLastShownAt(), expiresAt);
    }
}
