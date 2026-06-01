# Non-Functional Requirements — OnWork 0529

## Performance

Target environment: local Docker PostgreSQL/Redis + Spring Boot backend + Vite frontend.

| Flow | Budget |
|---|---|
| Login | p95 <= 300ms, p99 <= 600ms |
| Approval inbox | p95 <= 400ms for 100 pending items |
| Employee list | p95 <= 350ms for 1,000 employees |
| Attendance anomalies | p95 <= 400ms for 1 month / 1 department |
| Frontend first usable screen | <= 2.0s on local dev |

Load test baseline:
- 50 concurrent users for normal smoke.
- 100 concurrent users for presentation evidence.
- Error rate <= 1%.

## Reliability

- Approval processing must be transactional per item.
- HR batch approval allows partial success and records failure reason per item.
- Self-healing loop may retry implementation/test failures up to 3 times.

## Security

- JWT access token: 30 minutes.
- RBAC hierarchy: CEO > VP > HR_MANAGER > MANAGER > EMPLOYEE.
- Resigned/inactive users cannot login.
- No secret, token, or production setting may be committed.

## Accessibility

- Keyboard focus must reach primary navigation, forms, drawer/modal close, and approval actions.
- Buttons must have visible labels; icon-only controls need accessible labels.
- Color must not be the only state signal.

## Observability and Evidence

- Tests and benchmark results are saved under `reports/` when generated.
- ADR decisions must explain why Redis/JWT/layered architecture/approvals routing are used.
- Performance claims must cite measured p95/p99 or be marked unverified.
