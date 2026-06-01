package kr.onwork.onboarding.dto;

import jakarta.validation.constraints.NotNull;

public record OnboardingStepRequest(@NotNull Short step) {
}
