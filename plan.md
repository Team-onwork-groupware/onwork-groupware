# plan.md (DevAuto Execution Index)

이 파일은 OnWork 보완 개발의 실행 인덱스다. 상세 요구사항은 `inputs/`, 기능 흐름은 `usecases/`, 구현 검증 상태는 `docs/추적성_매핑.md`와 `memory-bank/progress.md`에 분산 기록한다.

## Task

- 작업명: DevAuto 기반 OnWork 0529 명세 정합화 및 교수님 피드백 반영
- 요청자: 사용자
- 관련 Issue/PR: local execution
- 작성일: 2026-06-01

## Vision

- 비전 요약: see [inputs/vision.md](inputs/vision.md)
- 핵심 사용자 문제: 그룹웨어 핵심 업무는 구현돼 보이지만 0529 명세와 실제 API/UI/검증 근거가 어긋나 있어 발표·평가 시 설명 가능한 완성도가 부족하다.

## Inputs Index

| Doc | Path | Owner |
|---|---|---|
| 비전 | inputs/vision.md | requirement-analyzer |
| 유저 스토리 | inputs/user-stories.md | requirement-analyzer |
| 비기능 요구사항 | inputs/nfr.md | nfr-performance-agent |
| 정보구조 | inputs/ia.md | requirement-analyzer + design-agent |
| API 스펙 | inputs/api-spec.yaml | api-integration-agent + implementation-agent |
| 교수님 피드백 | inputs/professor-feedback.md | requirement-analyzer |
| 디자인 토큰 | design/tokens.json | design-agent |
| 컴포넌트 카탈로그 | design/components.md | design-agent |
| 추적성 | docs/추적성_매핑.md | test-agent + docs-agent |

## Use Cases

| ID | Title | Owner | Status | Link |
|---|---|---|---|---|
| UC-HR-0529 | HR 변경 요청/일괄 승인 명세 정합화 | implementation-agent | implemented | [usecases/UC-HR-0529.md](usecases/UC-HR-0529.md) |
| UC-ATT-0529 | 근태 출퇴근/이상/시간외/월마감 정합화 | implementation-agent | implemented | [usecases/UC-ATT-0529.md](usecases/UC-ATT-0529.md) |
| UC-LEAVE-0529 | 휴가 신청/승인자 취소/보상휴가 부여 정합화 | implementation-agent | implemented | [usecases/UC-LEAVE-0529.md](usecases/UC-LEAVE-0529.md) |
| UC-APP-0529 | 통합 결재함 canonical API와 상세 확인 UI | api-integration-agent | implemented | [usecases/UC-APP-0529.md](usecases/UC-APP-0529.md) |
| UC-ONB-0529 | 온보딩 튜토리얼 API/UI 정합화 | frontend-agent | implemented | [usecases/UC-ONB-0529.md](usecases/UC-ONB-0529.md) |
| UC-NFR-0529 | 품질 증거/성능 검증 리포트 | nfr-performance-agent | partially implemented | [usecases/UC-NFR-0529.md](usecases/UC-NFR-0529.md) |

## Agent Dispatch Order

1. `requirement-analyzer`: 0529 PDF와 녹음 피드백을 입력 산출물과 use case로 고정한다.
2. `nfr-performance-agent`: p95/p99/TPS 예산과 부하 시나리오를 `inputs/nfr.md`, `reports/benchmark/`에 정의한다.
3. `api-integration-agent`: PDF 기준 canonical API와 화면용 aggregation/compat alias를 분리한다.
4. `implementation-agent`: 백엔드 API/서비스/DB/Jackson snake_case 정합화를 구현한다.
5. `frontend-agent`: 프론트 endpoint 이전, 상세 drawer, 인사 상세, 근태 UX, 마이페이지, 온보딩 UI를 구현한다.
6. `test-agent`: 계약 테스트와 서비스 통합 테스트를 추가하고 `./gradlew test`를 실행한다.
7. `qa-frontend-agent`: 브라우저/콘솔/상호작용 검증을 수행한다.
8. `docs-agent` + `git-history-agent`: README, ADR, 추적성, PR 요약을 정리한다.

## Implementation Phases

### Phase 0: DevAuto Readiness

- [x] DevAuto 템플릿 validate 확인
- [x] OnWork validate 실패 원인 확인: `README.md`에 `실행` 섹션 누락
- [x] OnWork DevAuto 문서 stack/명령 갱신
- [x] OnWork `python3 -m devauto validate <onwork>` 통과

### Phase 1: Specification Contract

| Task ID | UC | Change | Owner Agent | Done Criteria |
|---|---|---|---|---|
| TASK-SPEC-001 | all | 0529 API PDF 기준 `inputs/api-spec.yaml` 갱신 | api-integration-agent | canonical endpoint 목록과 compat 정책 명시 |
| TASK-SPEC-002 | all | 교수님 피드백을 `inputs/professor-feedback.md`와 use cases에 연결 | requirement-analyzer | 결재 상세/시간대/마이페이지/품질 근거가 추적 가능 |
| TASK-SPEC-003 | UC-NFR-0529 | 성능 예산 정의 | nfr-performance-agent | p95/p99/TPS/오류율 기준 수치화 |

### Phase 2: Backend Contract Implementation

| Task ID | UC | Change | Owner Agent | Done Criteria |
|---|---|---|---|---|
| TASK-BE-001 | UC-HR-0529 | DRAFT 제거, HR batch-process 추가, `batch_id` 기록 | implementation-agent | 일괄 APPROVE만 허용, partial success |
| TASK-BE-002 | UC-ATT-0529 | `PATCH clock-out`, `GET me`, `overtime-requests`, `monthly-summaries`, anomaly confirm body 구현 | implementation-agent | PDF 경로/메서드/응답 계약 통과 |
| TASK-BE-003 | UC-LEAVE-0529 | `/leave-balances/me`, `/leave-requests*`, `cancel-approved`, `leave-grants`, summary 구현 | implementation-agent | 신청자 승인건 직접 취소 차단 |
| TASK-BE-004 | UC-APP-0529 | `GET /approvals`, `PATCH /approvals/{id}/process` canonical 구현 | api-integration-agent + implementation-agent | 상세 확인용 payload 제공 |
| TASK-BE-005 | UC-ONB-0529 | `/onboarding/tutorials/me*` API 구현 | implementation-agent | visibility/restart/step 동작 |

### Phase 3: Frontend and UX

| Task ID | UC | Change | Owner Agent | Done Criteria |
|---|---|---|---|---|
| TASK-FE-001 | UC-APP-0529 | 통합 결재함 행 클릭 상세 drawer/modal | frontend-agent | 승인 전 상세 내용 확인 가능 |
| TASK-FE-002 | UC-HR-0529 | 직원 행 클릭 상세, 직원 권한은 마이페이지 중심 | frontend-agent | 역할별 노출 차등 |
| TASK-FE-003 | UC-ATT-0529 | 출근/퇴근 버튼 상태와 Asia/Seoul 기준 UX 정리 | frontend-agent | 퇴근 버튼 위치/상태 명확 |
| TASK-FE-004 | UC-ONB-0529 | 홈 진입 온보딩, 나중에/그만보기/재시작 | frontend-agent | 명세 흐름 검증 가능 |

### Phase 4: Automated Verification and Documentation

## Automated Verification

| Test ID | Type | Command/File | Expected Result |
|---|---|---|---|
| TEST-001 | DevAuto | `python3 -m devauto validate <onwork>` | pass |
| TEST-002 | Backend | `cd backend && ./gradlew test` | pass; Docker 미기동 시 원인 기록 |
| TEST-003 | Frontend Build | `cd frontend && npm run build` | pass |
| TEST-004 | Frontend Lint | `cd frontend && npm run lint` | pass after hooks fixes |
| TEST-005 | Contract | controller/API tests | PDF 경로, method, snake_case 검증 |
| TEST-006 | E2E | browser QA | 결재 상세, HR batch, 휴가 cancel-approved, 출퇴근, 온보딩 |
| TEST-007 | NFR | `reports/benchmark/*` | p95/p99/TPS 결과 저장 |

## Auto-Continue Policy

orchestrator는 다음 항목에서 사용자에게 묻지 않고 자동 진행한다.

- 계획 범위 안의 파일 생성/수정
- 0529 명세 정합화를 위한 DB 스키마/JPA/API/프론트 호출 변경
- 테스트 실행과 self-healing 최대 3회
- 문서/추적성/ADR 갱신

다음 항목은 반드시 사용자에게 묻는다.

- 외부 의존성 추가/제거
- 외부 API 키 또는 secret 작업
- production 배포 설정 변경
- 연구실 통합 플랫폼 v2의 실제 구현 착수

## Self-Healing Policy

검증 에이전트가 `"status": "fail"`을 반환하면 orchestrator는 `workflows/self-healing.md`를 자동 실행한다.

- `MAX_HEAL = 3`
- 각 반복은 `memory-bank/progress.md`에 체크포인트를 남긴다.
- 같은 실패가 반복되거나 정책 경계를 넘으면 사용자에게 보고한다.

## Rollback Plan

- 되돌릴 파일: 이번 작업에서 수정한 `inputs/`, `usecases/`, `AGENTS.md`, `plan.md`, `README.md`, `memory-bank/*`, 이후 product code 변경 파일
- 되돌릴 설정: API compat alias와 CI 변경
- 주의할 데이터: 로컬 PostgreSQL 볼륨과 사용자 홈 디렉터리 git 변경은 작업 범위 밖

## Progress Log

| Time | Agent/User | Event | Notes |
|---|---|---|---|
| 2026-06-01 | user | 실행 요청 | DevAuto 기반 OnWork 0529 보완 개발 착수 |
| 2026-06-01 | orchestrator | baseline validate | DevAuto 자체 pass, OnWork는 README `실행` 섹션 누락으로 fail |
