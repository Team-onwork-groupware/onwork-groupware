# Workflow: Verify Before Commit

## Trigger

Use this after code/doc changes and before any commit or PR.

## Checklist

- [ ] `git status` reviewed
- [ ] `git diff` reviewed
- [ ] unrelated changes removed or explained
- [ ] tests executed
- [ ] lint/format executed if applicable
- [ ] docs updated or not-needed reason recorded
- [ ] `memory-bank/progress.md` updated
- [ ] commit message prepared
- [ ] PR body prepared

## Required Commands

```bash
test -f AGENTS.md
test -f plan.md
# 프로젝트에 맞는 lint/format 명령을 정한 뒤 기록한다.
# 프로젝트에 맞는 build 명령을 정한 뒤 기록한다.
```
