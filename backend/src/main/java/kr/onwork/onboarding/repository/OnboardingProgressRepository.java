package kr.onwork.onboarding.repository;

import java.util.List;
import java.util.Optional;
import kr.onwork.onboarding.domain.OnboardingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingProgressRepository extends JpaRepository<OnboardingProgress, Long> {
    Optional<OnboardingProgress> findByUserIdAndTutorialCode(Long userId, String tutorialCode);

    List<OnboardingProgress> findByUserIdOrderByTutorialCode(Long userId);
}
