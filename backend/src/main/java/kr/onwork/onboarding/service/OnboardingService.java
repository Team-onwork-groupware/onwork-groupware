package kr.onwork.onboarding.service;

import kr.onwork.onboarding.domain.OnboardingProgress;
import kr.onwork.onboarding.dto.OnboardingResponse;
import kr.onwork.onboarding.dto.OnboardingUpdateRequest;
import kr.onwork.onboarding.repository.OnboardingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 온보딩 가이드 투어 진행 관리 (조회 시 없으면 생성, 단계 진행/완료/해제). */
@Service
public class OnboardingService {

    private final OnboardingProgressRepository repository;

    public OnboardingService(OnboardingProgressRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OnboardingResponse getOrStart(Long userId, String tutorialCode) {
        OnboardingProgress progress = repository.findByUserIdAndTutorialCode(userId, tutorialCode)
                .orElseGet(() -> repository.save(OnboardingProgress.start(userId, tutorialCode)));
        return OnboardingResponse.from(progress);
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
        return OnboardingResponse.from(progress);
    }
}
