# Information Architecture — OnWork

## Screens

| ID | Screen | Purpose | Entry From | Exits To |
|---|---|---|---|---|
| S-001 | Login | JWT login and seeded account access | first entry | S-002 |
| S-002 | Dashboard | Today's work, pending approvals, notification digest | S-001 | S-003..S-007 |
| S-003 | HR / My Page | Executives/HR manage employees; employees see own profile | nav | detail drawer, approval request |
| S-004 | Attendance | Clock in/out, anomalies, overtime requests, monthly closing | nav | anomaly detail, overtime forms |
| S-005 | Leave | Balances, leave request, approver processing, grants/summary | nav | request form, approver cancel |
| S-006 | Approvals | Unified approval inbox with detail before processing | nav/dashboard | detail drawer, process action |
| S-007 | Notifications | Notification list and read/read-all state | topbar | ref detail |
| S-008 | Onboarding | Tutorial modal and settings restart entry | dashboard/settings | current screen |

## Navigation

- Sidebar: Dashboard, HR/My Page, Attendance, Leave, Approvals.
- Topbar: notification bell, user identity, logout.
- Employee role should not see a broad HR management experience; it should land on own profile/마이페이지.

## Critical Paths

- HR create approval: S-003 → create request → S-006 → detail drawer → approve → employee appears/login enabled.
- Attendance: S-004 → clock-in → clock-out → anomaly list if manager → confirm → monthly close.
- Leave: S-005 → request → S-006 or S-005 inbox → approve/on hold → cancel-approved if already approved.
- Approval fatigue: S-006 list → filter/sort/age badge → row detail → single/batch process.
- Onboarding: S-002 first visit → tutorial modal → later/dismiss/advance → settings restart.

## Professor Feedback Anchors

- 결재함은 행만 보여주면 안 되고 “내용을 봐야” 한다.
- 근태는 서버 지역 시간대 영향을 제거해야 한다.
- 출퇴근 UX는 퇴근 버튼 위치와 상태가 즉시 이해돼야 한다.
- 인사 탭이 직원에게는 마이페이지처럼 보이는 편이 낫다.
- 품질은 “진짜 검증했는지”를 숫자와 재현 절차로 보여줘야 한다.
