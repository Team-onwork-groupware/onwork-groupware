# User Stories — 0529 정합화

## US-HR-001
As an HR manager, I want to register CREATE/UPDATE/RESIGN change requests without touching real employee data before approval, so that HR records stay auditable and reversible.

Acceptance:
- [ ] `POST /api/v1/hr/change-requests` creates `PENDING` requests only.
- [ ] DRAFT temporary-save flow is removed from the canonical path.
- [ ] Duplicate email, missing fields, invalid resign date return documented error codes.

## US-HR-002
As an executive, I want to batch-approve HR changes, so that high-volume approval work does not pile up.

Acceptance:
- [ ] `POST /api/v1/hr/change-requests/batch-process` supports `APPROVE` only.
- [ ] Max 50 ids, partial success, and `batch_id` audit grouping are implemented.
- [ ] Reject remains single-item because it needs per-request reason.

## US-ATT-001
As an employee, I want clock-in/clock-out to use the correct Korean business day/time, so that deployment region does not corrupt attendance.

Acceptance:
- [ ] `POST /api/v1/attendance/clock-in`, `PATCH /api/v1/attendance/clock-out`, `GET /api/v1/attendance/me` match the PDF contract.
- [ ] Service time uses `Asia/Seoul` through an injectable `Clock`.
- [ ] UI clearly shows whether clock-out is available.

## US-ATT-002
As a manager, I want to confirm attendance anomalies with type correction and overtime recognition, so that monthly closing reflects actual work.

Acceptance:
- [ ] `PATCH /api/v1/attendance/anomalies/{anomaly_id}/confirm` accepts `anomaly_type` and `overtime_approved`.
- [ ] `UNAPPROVED_OVERTIME` can keep overtime minutes or set them to 0.
- [ ] Monthly closing API exists and reports unconfirmed anomaly count.

## US-LEAVE-001
As an employee, I want to request and track leave through the documented endpoints, so that balances and request state are predictable.

Acceptance:
- [ ] `/api/v1/leave-balances/me`, `/api/v1/leave-requests`, `/api/v1/leave-requests/me` are canonical.
- [ ] Employee cancellation is allowed only for `PENDING` and `ON_HOLD`.
- [ ] Approved leave cancellation moves to approver-only `cancel-approved`.

## US-LEAVE-002
As an executive, I want to grant compensation leave to multiple employees with partial success, so that overtime compensation can be recorded in one operation.

Acceptance:
- [ ] `POST /api/v1/leave-grants` updates balances, writes grant/history records, and returns per-user results.
- [ ] `GET /api/v1/leave-requests/summary` supports executive/HR overview.

## US-APP-001
As an approver, I want one inbox with enough detail before approving, so that I do not approve blind rows.

Acceptance:
- [ ] `GET /api/v1/approvals` is canonical and supports type/status/page filters.
- [ ] `PATCH /api/v1/approvals/{id}/process` dispatches to HR/leave/attendance handlers.
- [ ] Frontend row click opens detail drawer/modal with source payload and before/after or period details.

## US-ONB-001
As a new employee or promoted user, I want onboarding tutorials that can be advanced, hidden, or restarted, so that guidance appears when relevant but can be controlled.

Acceptance:
- [ ] `/api/v1/onboarding/tutorials/me` and step/visibility/restart endpoints exist.
- [ ] Frontend supports later, dismiss, and restart behavior.

## US-NFR-001
As a project presenter, I want reproducible quality evidence, so that I can explain why architecture choices and performance claims are credible.

Acceptance:
- [ ] Backend contract tests prove PDF endpoint compatibility.
- [ ] Frontend QA captures interactions and console health.
- [ ] Benchmark scripts record p95/p99/TPS/failure-rate evidence.
