package kr.onwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * OnWork 그룹웨어 API 서버 진입점.
 * 아키텍처: Layered (Presentation -> Business -> Data Access), 도메인 패키지 분리.
 * @EnableScheduling: 근태 자정 배치(ADR-ATT-001) 등 스케줄러 활성화.
 */
@EnableScheduling
@SpringBootApplication
public class OnWorkApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnWorkApplication.class, args);
    }
}
