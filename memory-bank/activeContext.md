# Active Context

## Current Focus

DevAuto 파이프라인으로 OnWork 0529 명세 정합화와 교수님 피드백 반영을 1차 구현했다. 현재 초점은 검증 결과 고정과 남은 품질 증거 보강이다.

## Current Assumptions

- 기술 스택은 Spring Boot 3.3(Java 21), React/Vite(TypeScript), PostgreSQL 16, Redis 7이다.
- 0529 명세 충돌 시 우선순위는 `OnWork_변경사항요약_0529.pdf`와 `OnWork_API_0529.pdf`를 최우선으로 둔다.
- OnWork 제품 코드는 DevAuto 템플릿으로 덮어쓰지 않는다. 이미 적용된 DevAuto 골격 위에 실행 계획과 산출물을 갱신한다.
- 이번 1차 범위는 0529 명세/API/UI/검증 정합화다. 연구실 통합 플랫폼 모드는 v2 문서 범위로만 둔다.
- 외부 의존성, secret, production 배포 설정 변경은 사용자 확인이 필요하다.

## Completed This Run

- `README.md`, `AGENTS.md`, `plan.md`, `inputs/`, `usecases/`, `questions.md`를 DevAuto 실행 기준으로 갱신했다.
- `python3 -m devauto validate <onwork>`가 122 checks로 통과한다.
- 전역 JSON 계약을 `snake_case`로 고정하고 프론트 Axios 변환 레이어를 추가했다.
- HR `batch-process`, 근태 canonical aliases/monthly summary/Asia-Seoul Clock, 휴가 canonical routes/cancel-approved/grants/summary, approvals canonical process, onboarding tutorials API/UI를 구현했다.
- 결재함 행 클릭 상세 drawer, HR 임시저장 UI 제거, 휴가 승인건 신청자 직접 취소 차단 UI, 온보딩 홈 노출을 반영했다.
- Docker compose 인프라 구동 후 `./gradlew test`, `npm run lint`, `npm run build`가 통과한다.

## Known Gaps

- `approvals`는 아직 물리적 결재 라우팅 테이블이 아니라 기존 도메인 집계 인덱스 위의 canonical API다.
- 근태 이상 확정의 유형 재지정과 미승인 시간외 인정 여부는 API 스펙에 남아 있으나 이번 1차 구현에는 최소 확정 처리만 유지했다.
- UC-NFR-0529의 부하 테스트 스크립트와 p95/p99 결과 리포트는 아직 별도 산출물이 필요하다.
- 브라우저 기반 E2E 검증은 아직 실행하지 않았다.

## Next Agent Dispatch

1. `test-agent`: 0529 canonical endpoint별 controller/contract 테스트 추가
2. `qa-frontend-agent`: 브라우저 E2E로 로그인, 결재 상세, 출퇴근, 휴가 신청, 온보딩 재시작 검증
3. `nfr-performance-agent`: 핵심 API p95/p99 부하 테스트 스크립트와 결과 리포트 작성
4. `docs-agent`: ADR/추적성 매핑에 1차 구현 결과와 남은 gap 반영

## Open Questions

- 없음. 기존 Q-001~Q-003은 이번 실행에서 기본 가정으로 해소한다.
