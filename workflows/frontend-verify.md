# Workflow: Frontend Verify

## Trigger

frontend-agent가 화면 하나 이상을 완료하고 hand-off note를 남겼을 때.

## Steps

1. `python3 tools/inject_tokens.py`로 최신 CSS 변수를 생성한다.
2. `python3 tools/check_static_site.py`로 정적 구조 검증.
3. qa-frontend-agent 실행 — `data-testid` 존재 여부, 콘솔 에러, 토큰 사용률, API trace.
4. `reports/qa-frontend.md`와 `reports/screens/*.png`를 검토.
5. 실패가 있으면 frontend-agent로 회송 (qa-frontend-agent는 product 코드를 수정하지 않는다).
6. 통과하면 plan.md의 해당 UC Use Cases 행 status를 `verified`로 갱신.

## Required Human Checkpoint

- design/tokens.json 값 변경이 발생한 경우 사용자 승인 필요.
- 외부 dependency를 새로 도입한 경우 사용자 승인 필요.
- 그 외 자동 진행.

## Optional: Playwright Mode

plan.md에 `Use Playwright: true` 가 명시된 경우에만 활성화. 외부 dep 추가가 발생하므로 사용자 승인 후 실행.

```bash
npx playwright install --with-deps
npx playwright test
```
