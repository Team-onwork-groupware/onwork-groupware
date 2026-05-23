# CLAUDE.md

이 파일은 Claude Code 전용 운영 규칙이다. 공용 규칙은 `AGENTS.md`를 따른다.

## Subagent 호출

다음 작업이 발생하면 명시적으로 해당 subagent에 위임한다 (Claude Code의 Agent tool 사용).

| 상황 | 호출할 subagent |
|---|---|
| 신규 프로젝트 시작, 요구사항이 모호함 | `requirement-analyzer` |
| 화면별 디자인 명세 필요 | `design-agent` |
| 화면 코드 구현 | `frontend-agent` |
| 백엔드/API 코드 구현 | `implementation-agent` |
| 백엔드 테스트 | `test-agent` |
| 프론트엔드 DOM/콘솔/API 검증 | `qa-frontend-agent` |
| README/문서 갱신 | `docs-agent` |
| CI 워크플로우 수정 | `github-actions-agent` |
| 커밋/PR 메시지 작성 | `git-history-agent` |
| 위 모든 것을 분배 | `orchestrator` |

## Auto-Continue 정책

`plan.md`의 `Auto-Continue Policy` 섹션을 그대로 따른다. 사용자에게 "다음 진행할까요?"라고 물어보지 말 것. 다음 boundary 항목에 도달했을 때만 묻는다.

- 외부 dep 추가/제거
- design/tokens.json 값 변경
- DB 스키마 변경
- 인증/권한 정책 변경
- 외부 API 키 작업

## Subagent 작성 규칙

`.claude/agents/*.md` 파일은 다음 YAML frontmatter 표준을 따른다.

```yaml
---
name: <agent-name>
description: <when to use, one sentence>
tools: <comma-separated allowed tools>
model: sonnet | opus | haiku
---
```

본문은 Mission, Operating Rules, Output Contract 세 섹션을 포함한다.

`model:` 값은 `AGENTS.md`의 "Model Routing" 표를 따른다 (정형 작업=haiku, 구현/검증=sonnet, 무거운 추론=opus). 모델 변경 시 루트와 `devauto/templates/base/`의 같은 파일을 양쪽 다 수정한다. Output Contract는 다운스트림 에이전트가 파싱하는 핸드오프이면 ```json``` 코드펜스로 스키마를 명시한다 (validator가 강제).

## Codex와의 분리

- `AGENTS.md` — Codex 및 공용 규칙 (모든 도구가 읽는다)
- `CLAUDE.md` — Claude Code 전용 규칙 (이 파일)
- `.claude/agents/*.md` — Claude Code subagent 정의 (위 YAML 표준)
- Codex 전용 지시는 `AGENTS.md` 안의 하위 섹션 또는 별도 `.codex/` 디렉터리에 둔다 (현재 없음)
