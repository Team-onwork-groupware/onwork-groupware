package kr.onwork.onboarding.dto;

import jakarta.validation.constraints.NotNull;

public record OnboardingUpdateRequest(
        @NotNull Action action,
        Short step
) {
    public enum Action { ADVANCE, COMPLETE, DISMISS }
}
