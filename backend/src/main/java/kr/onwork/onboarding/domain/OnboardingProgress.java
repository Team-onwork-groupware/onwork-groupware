package kr.onwork.onboarding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 온보딩 가이드 투어 진행 (onboarding_tutorial_progress). 사용자×투어코드 1행. */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "onboarding_tutorial_progress")
public class OnboardingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tutorial_code", nullable = false, length = 30)
    private String tutorialCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OnboardingStatus status;

    @Column(name = "current_step", nullable = false)
    private short currentStep;

    @Column(name = "last_shown_at")
    private LocalDateTime lastShownAt;

    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    public static OnboardingProgress start(Long userId, String tutorialCode) {
        OnboardingProgress p = new OnboardingProgress();
        p.userId = userId;
        p.tutorialCode = tutorialCode;
        p.status = OnboardingStatus.NOT_STARTED;
        p.currentStep = 0;
        return p;
    }

    public void advance(short step) {
        this.currentStep = step;
        this.status = OnboardingStatus.IN_PROGRESS;
        this.lastShownAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = OnboardingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void dismiss() {
        this.status = OnboardingStatus.DISMISSED;
        this.dismissedAt = LocalDateTime.now();
    }

    public void snoozeToday() {
        this.lastShownAt = LocalDateTime.now();
    }

    public void restart() {
        this.status = OnboardingStatus.NOT_STARTED;
        this.currentStep = 0;
        this.lastShownAt = null;
        this.dismissedAt = null;
        this.completedAt = null;
        this.createdAt = LocalDateTime.now();
    }
}
