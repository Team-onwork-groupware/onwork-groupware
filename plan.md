# plan.md (Index)

이 파일은 인덱스다. 상세 내용은 `inputs/`, `usecases/`, `design/` 하위 파일에 분산 저장한다.

## Task

- 작업명:
- 요청자:
- 관련 Issue/PR:
- 작성일:

## Vision

- 비전 요약: see [inputs/vision.md](inputs/vision.md)
- 핵심 사용자 문제: (한 줄)

## Inputs Index

| Doc | Path | Owner |
|---|---|---|
| 비전 | inputs/vision.md | requirement-analyzer |
| 유저 스토리 | inputs/user-stories.md | requirement-analyzer |
| 비기능 요구사항 | inputs/nfr.md | requirement-analyzer |
| 정보구조 | inputs/ia.md | requirement-analyzer |
| API 스펙 | inputs/api-spec.yaml | requirement-analyzer + implementation-agent |
| 디자인 토큰 | design/tokens.json | design-agent |
| 컴포넌트 카탈로그 | design/components.md | design-agent |

## Use Cases

| ID | Title | Owner | Status | Link |
|---|---|---|---|---|
| UC-001 |  | requirement-analyzer | planned | [usecases/UC-001.md](usecases/UC-001.md) |

## Implementation Phases

### Phase 1: Research / Context Gathering

- [ ] inputs/ 전체 읽기
- [ ] usecases/UC-*.md 검토
- [ ] design/tokens.json + components.md 확인

### Phase 2: Implementation

| Task ID | UC | File | Change | Owner Agent | Done Criteria |
|---|---|---|---|---|---|
| TASK-001 | UC-001 | path/to/file | | implementation-agent | |

### Phase 3: Testing

| Test ID | Type | Command/File | Expected Result |
|---|---|---|---|
| TEST-001 | Unit | `test -f AGENTS.md
test -f plan.md` | pass |
| TEST-002 | Frontend | workflows/frontend-verify.md | pass |

### Phase 4: Documentation

- [ ] README 갱신
- [ ] usecases/UC-*.md status 갱신
- [ ] memory-bank/progress.md 기록

### Phase 5: Commit / PR Preparation

- [ ] `git diff` 검토
- [ ] 테스트 결과 요약

## Auto-Continue Policy

orchestrator는 다음 항목에서 사용자에게 묻지 않고 자동 진행한다.

- 파일 생성/수정 (계획 범위 안)
- 테스트 실행
- 인덱스(plan.md) 갱신
- usecases/UC-*.md 추가
- design/spec.md 갱신

다음 항목은 반드시 사용자에게 묻는다.

- 외부 의존성 추가/제거
- design/tokens.json 의 값 변경
- DB 스키마 변경
- 인증/권한 정책 변경
- 외부 API 키 작업

## Self-Healing Policy

검증 에이전트(qa-frontend-agent / test-agent / nfr-performance-agent)가 `"status": "fail"`을 반환하면 orchestrator는 `workflows/self-healing.md`를 자동 실행한다.

- `MAX_HEAL = 3` — 실패 핸드오프당 자동 재시도 최대 횟수.
- 각 반복은 `memory-bank/progress.md`에 체크포인트 1줄을 남긴다 (벤치마크가 `self_heal_iterations`로 집계).
- 다음일 때만 사용자에게 surface: `MAX_HEAL` 도달, 동일 실패가 진전 없이 반복, 또는 수정이 위 Auto-Continue boundary를 건드림.

## Automated Verification

```bash
test -f AGENTS.md
test -f plan.md
# 프로젝트에 맞는 lint/format 명령을 정한 뒤 기록한다.
# 프로젝트에 맞는 build 명령을 정한 뒤 기록한다.
```

## Manual Verification

- [ ] 기능 시나리오 직접 확인
- [ ] UI 또는 API 동작 확인
- [ ] 성능/보안/권한 관련 특이사항 확인

## Rollback Plan

- 되돌릴 파일:
- 되돌릴 설정:
- 주의할 데이터:

## Progress Log

| Time | Agent/User | Event | Notes |
|---|---|---|---|
|  |  |  |  |
