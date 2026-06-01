# UC-NFR-0529: 품질 증거/성능 검증 리포트

## Goal

발표와 평가에서 설명 가능한 품질 증거를 남긴다. 핵심 API 성능, 테스트 재현성, ADR 선택 근거를 문서와 스크립트로 보강한다.

## Scope

- 로그인
- 결재함 조회
- 직원 목록 조회
- 근태 이상 조회
- 휴가 신청/승인 처리

## Performance Budget

- 핵심 조회 API p95: 500ms 이하
- 핵심 조회 API p99: 1000ms 이하
- 쓰기 API p95: 800ms 이하
- 오류율: 1% 이하
- 테스트 데이터: 최소 사용자 100명, 결재/근태/휴가 데이터 각 1000건 이상

## Required Evidence

- `reports/benchmark/`에 부하 테스트 스크립트와 결과 저장
- `docs/ADR` 또는 기존 ADR 문서에 선택 이유와 대안 비교 추가
- `docs/추적성_매핑.md`에 요구사항, API, 테스트 연결
- Docker/PostgreSQL 테스트 전제 조건 또는 Testcontainers/테스트 프로필 정리

## Acceptance Criteria

- 핵심 API별 p95/p99, 실패율, 실행 조건이 기록된다.
- `./gradlew test`, `npm run build`, `npm run lint` 실행 결과가 진행 문서에 남는다.
- Docker 미기동 등 환경 실패는 제품 결함과 구분해 기록된다.
- 0529 명세 대비 구현 완료/미완료 상태가 추적성 문서에서 확인 가능하다.
