package kr.onwork.onboarding.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.onwork.common.domain.Role;
import kr.onwork.onboarding.domain.OnboardingProgress;
import kr.onwork.onboarding.domain.OnboardingStatus;
import kr.onwork.onboarding.dto.OnboardingResponse;
import kr.onwork.onboarding.dto.OnboardingStepRequest;
import kr.onwork.onboarding.dto.OnboardingUpdateRequest;
import kr.onwork.onboarding.dto.OnboardingVisibilityRequest;
import kr.onwork.onboarding.repository.OnboardingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 온보딩 가이드 투어 진행 관리 (조회 시 없으면 생성, 단계 진행/완료/해제). */
@Service
public class OnboardingService {

    private static final String NEW_HIRE_TOUR = "NEW_HIRE_TOUR";
    private static final String MANAGER_TOUR = "MANAGER_TOUR";

    private final OnboardingProgressRepository repository;

    public OnboardingService(OnboardingProgressRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OnboardingResponse getOrStart(Long userId, String tutorialCode) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        return response(progress);
    }

    @Transactional
    public OnboardingResponse update(Long userId, String tutorialCode, OnboardingUpdateRequest req) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        switch (req.action()) {
            case ADVANCE -> progress.advance(req.step() != null ? req.step() : (short) (progress.getCurrentStep() + 1));
            case COMPLETE -> progress.complete();
            case DISMISS -> progress.dismiss();
        }
        return response(progress);
    }

    @Transactional
    public List<OnboardingResponse> listMine(Long userId, Role role) {
        String code = tutorialFor(role);
        getOrStart(userId, code);
        return repository.findByUserIdOrderByTutorialCode(userId)
                .stream()
                .filter(progress -> progress.getTutorialCode().equals(code))
                .map(this::response).toList();
    }

    @Transactional
    public OnboardingResponse step(Long userId, String tutorialCode, OnboardingStepRequest req) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        progress.advance(req.step());
        return response(progress);
    }

    @Transactional
    public OnboardingResponse visibility(Long userId, String tutorialCode, OnboardingVisibilityRequest req) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        if (Boolean.TRUE.equals(req.visible())) {
            progress.restart();
        } else {
            progress.snoozeToday();
        }
        return response(progress);
    }

    @Transactional
    public OnboardingResponse restart(Long userId, String tutorialCode) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        progress.restart();
        return response(progress);
    }

    private String tutorialFor(Role role) {
        return role == Role.MANAGER ? MANAGER_TOUR : NEW_HIRE_TOUR;
    }

    private OnboardingResponse response(OnboardingProgress progress) {
        boolean expired = isExpired(progress);
        boolean autoVisible = !expired
                && progress.getStatus() != OnboardingStatus.COMPLETED
                && progress.getStatus() != OnboardingStatus.DISMISSED
                && (progress.getLastShownAt() == null
                || !progress.getLastShownAt().toLocalDate().equals(LocalDate.now()));
        return OnboardingResponse.from(progress, autoVisible, expired);
    }

    private boolean isExpired(OnboardingProgress progress) {
        LocalDateTime createdAt = progress.getCreatedAt();
        return createdAt != null && createdAt.plusDays(30).toLocalDate().isBefore(LocalDate.now());
    }
}
