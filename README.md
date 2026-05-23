# OnWork

이 저장소는 agent-driven development workflow를 기본 구조로 사용한다.

## 실행

```bash
# 프로젝트에 맞는 실행/개발 명령을 정한 뒤 기록한다.
```

## 검증

```bash
test -f AGENTS.md
test -f plan.md
# 프로젝트에 맞는 lint/format 명령을 정한 뒤 기록한다.
```

## 개발 절차

1. `AGENTS.md`에서 저장소 규칙을 확인한다.
2. `memory-bank/activeContext.md`에서 현재 맥락을 확인한다.
3. 구현 전에 `plan.md`에 범위, 수정 파일, 검증 전략을 기록한다.
4. 구현 후 검증 명령을 실행하고 `memory-bank/progress.md`를 갱신한다.

## 구조

```text
.
├── AGENTS.md
├── plan.md
├── memory-bank/
├── workflows/
├── .claude/agents/
└── .github/workflows/
```
