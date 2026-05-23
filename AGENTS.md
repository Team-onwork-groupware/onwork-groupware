# AGENTS.md

이 파일은 AI 코딩 에이전트가 `OnWork` 저장소에서 작업할 때 따라야 하는 저장소 전체 운영 규칙이다.

## Project Overview

`OnWork`는 agent-driven development workflow를 적용하는 프로젝트다.

## Mandatory Workflow

1. 작업 전에 `plan.md`와 `memory-bank/activeContext.md`를 읽는다.
2. 변경 범위, 예상 수정 파일, 테스트 전략을 `plan.md`에 먼저 기록한다.
3. 구현은 승인된 계획 범위 안에서만 수행한다.
4. 새 기능 또는 버그 수정에는 관련 테스트를 추가하거나 기존 테스트를 갱신한다.
5. 구현 후 관련 검증 명령을 실행한다.
6. 문서 영향이 있으면 README, docs, API 문서, 사용 예시를 갱신한다.
7. 커밋 전 `git diff`, `git status`, 테스트 결과, 문서 변경 여부를 요약한다.
8. `memory-bank/progress.md`에 완료한 작업과 남은 이슈를 남긴다.

## Default Commands

Stack: `generic`

```bash
# 프로젝트에 맞는 설치 명령을 정한 뒤 기록한다.
# 프로젝트에 맞는 실행/개발 명령을 정한 뒤 기록한다.
test -f AGENTS.md
test -f plan.md
# 프로젝트에 맞는 lint/format 명령을 정한 뒤 기록한다.
# 프로젝트에 맞는 build 명령을 정한 뒤 기록한다.
```

## Role Map

| Role | File | Model | Main Responsibility |
|---|---|---|---|
| Orchestrator | `.claude/agents/orchestrator.md` | sonnet | 워크플로우 매니지먼트, 에이전트 분배 (콘텐츠 편집 금지) |
| Requirement Analyzer (PO) | `.claude/agents/requirement-analyzer.md` | opus | 사용자 요구를 inputs/, usecases/ 로 구조화 (Product Owner) |
| NFR / Performance Agent | `.claude/agents/nfr-performance-agent.md` | opus | 지연/TPS/동시접속 등 비기능 요구 정의 + 부하·스트레스 테스트 |
| API Integration Agent | `.claude/agents/api-integration-agent.md` | sonnet | 프론트-백 중간계층 API 재정의/통합·캐시 결정 |
| Design Agent | `.claude/agents/design-agent.md` | sonnet | design/tokens 기반 design/spec.md 생성 |
| Frontend Agent | `.claude/agents/frontend-agent.md` | sonnet | design/spec.md 따라 화면 구현 |
| QA Frontend Agent | `.claude/agents/qa-frontend-agent.md` | sonnet | DOM/콘솔/API trace 프론트 검증 |
| Project Bootstrapper | `.claude/agents/project-bootstrapper.md` | haiku | 빈 저장소 초기화, 기본 파일/구조 생성 |
| Implementation Agent | `.claude/agents/implementation-agent.md` | sonnet | 백엔드 코드 구현 |
| Test Agent | `.claude/agents/test-agent.md` | sonnet | 백엔드 테스트 작성, 실패 분석, 검증 |
| Docs Agent | `.claude/agents/docs-agent.md` | haiku | README/docs/API 문서화 |
| GitHub Actions Agent | `.claude/agents/github-actions-agent.md` | haiku | CI 워크플로우 작성/수정 |
| Git History Agent | `.claude/agents/git-history-agent.md` | haiku | diff 검토, 커밋 메시지, PR 초안 |
| Solution Research Agent (P1) | `.claude/agents/solution-research-agent.md` | sonnet | 기획 단계 솔루션 비교/마켓리서치 (기본 시퀀스 미편입) |
| Mockup Agent (P1) | `.claude/agents/mockup-agent.md` | sonnet | 프론트 앞단 HTML+페이크데이터 목업 (기본 시퀀스 미편입) |

Orchestrator vs Requirement Analyzer: orchestrator는 PM(Project Manager) 즉 워크플로우/일정 매니저, requirement-analyzer는 PO(Product Owner) 즉 요구사항·가치 정의자. Orchestrator는 콘텐츠 편집 권한이 없고, requirement-analyzer는 코드 편집 권한이 없다.

## Model Routing

각 에이전트는 작업 난이도에 맞는 모델을 쓴다. 모든 에이전트를 같은 모델로 돌리면 비용·지연이 불필요하게 커지므로, 추론 부담에 따라 3단계로 라우팅한다.

| 모델 | 대상 에이전트 | 근거 |
|---|---|---|
| `haiku` | git-history-agent, docs-agent, github-actions-agent, project-bootstrapper | diff 요약, 문서 갱신, CI YAML, 스캐폴드 등 정형·기계적 작업. 추론 부담 낮음 → 비용/지연 최소화 |
| `sonnet` | orchestrator, design-agent, frontend-agent, implementation-agent, test-agent, qa-frontend-agent, api-integration-agent | 코드 구현·검증·설계 등 균형이 필요한 작업 |
| `opus` | requirement-analyzer, nfr-performance-agent | 모호한 요구의 구체화, 성능 병목 추론 등 가장 무거운 추론 |

규칙:
- 에이전트 frontmatter `model:` 값은 반드시 `haiku | sonnet | opus` 중 하나다 (validator가 강제).
- 모델 변경 시 `.claude/agents/`(루트)와 `devauto/templates/base/.claude/agents/`(템플릿) **양쪽**을 동기화한다.
- 비용·지연이 문제되면 먼저 sonnet→haiku 다운시프트를 검토하고, 품질 저하가 측정되면 되돌린다 (`reports/benchmark/` 수치 근거).

## Prompt Caching

호출마다 바뀌지 않는 정적 컨텍스트(이 `AGENTS.md`, 에이전트 정의, `plan.md`, `design/tokens.json`)는 프롬프트의 **안정적인 prefix**에 둔다. 자주 바뀌는 내용(현재 작업 지시, 직전 에이전트의 출력)은 뒤에 둔다. 이렇게 하면 반복 호출에서 정적 prefix가 캐시 적중되어 입력 토큰 비용이 크게 준다.

- 정적 prefix 안에서는 파일 순서를 임의로 바꾸지 않는다 (순서가 바뀌면 캐시가 깨진다).
- 절감 효과는 `reports/benchmark/cache_harness.py`로 측정한다 (2회차 `cache_read_input_tokens` 확인).

## Context Retrieval (RAG)

긴 작업에서 `memory-bank/`, `inputs/`, `usecases/` 전체를 매번 통째로 로드하면 컨텍스트(토큰)가 낭비된다. 관련 부분만 끌어와 쓴다.

- 1단계(현재): grep 기반 경량 검색 — `python3 tools/context_search.py "<질의어>"` 로 관련 줄만 가져온다 (stdlib, 외부 dep 없음).
- 2단계(P1): 임베딩 기반 시맨틱 검색. 이번 라운드 범위 밖.

## Event-Driven / Async 협업

- **산출물 측**: 실시간 기능은 이벤트 기반 비동기로 만든다. `websocket` stack의 `server.py`(asyncio broadcast)가 그 실동작 예시다. 플랫폼 전체를 위한 공용 이벤트 버스는 설계 단계(P1).
- **에이전트 협업 측**: 사람-에이전트 결정은 `questions.md`(비동기 Q&A 보드)로 주고받는다. 에이전트는 blocked 시 멈추지 않고 질문을 append 한 뒤 가능한 작업을 계속하고, orchestrator가 디스패치 전 폴링해 반영한다.

## Boundaries

### Always Do

- 작업 시작 전 `plan.md` 확인
- 변경한 코드에 대한 테스트 작성 또는 테스트 필요 없음의 근거 기록
- 실패한 테스트를 삭제하지 말고 원인 분석
- 커밋 전 `git diff` 기준으로 변경사항 요약
- 새 의존성, DB 스키마, 인증/보안 관련 변경은 명시적으로 표시

### Ask First

- 데이터베이스 스키마 변경
- 인증/권한 정책 변경
- 외부 API 키 또는 secret 관련 작업
- CI/CD 배포 설정 변경
- 대규모 리팩터링
- 의존성 추가/제거

### Never Do

- secret, token, credential 커밋
- 실패 테스트를 통과시키기 위해 테스트 삭제
- 사용자 승인 없이 force push, rebase, reset 실행
- 사용자 승인 없이 production 설정 수정
- 계획에 없는 광범위한 파일 수정

## Output Contract

```md
## Summary
- 변경 요약

## Files Changed
- `path/to/file`: 변경 내용

## Verification
- [x] 실행한 명령: 결과
- [ ] 실행하지 못한 명령: 이유

## Risks / Follow-up
- 남은 리스크 또는 후속 작업
```
