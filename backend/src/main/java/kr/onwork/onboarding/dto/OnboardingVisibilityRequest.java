package kr.onwork.onboarding.dto;

import jakarta.validation.constraints.NotNull;

public record OnboardingVisibilityRequest(@NotNull Boolean visible) {
}
