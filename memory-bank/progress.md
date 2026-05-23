# Progress

| Date | Item | Status | Notes |
|---|---|---|---|
| 2026-05-23 | 프로젝트 초기화 | Done | `devauto init` 거버넌스 골격, validate 122 통과 |
| 2026-05-23 | Phase 0: 기반 세팅 | Done | db/schema.sql 21테이블 + seed 22명(라이브 PostgreSQL 검증), docker-compose(PG16+Redis7), Spring Boot 3.3/Java21 골격 build OK, React+Vite+TS 골격 build OK |
| 2026-05-23 | Phase 1: 공통 기반 + Auth | Done | JWT(Access30m/Refresh7d)+RBAC5단계 계층, Redis 블랙리스트+5회잠금, {code,message} 전역예외, User/Department/WorkGroup/UserCredential 엔티티+리포지토리, AuthService/Controller(login·refresh·logout·me), CORS. 프론트 로그인→대시보드 셸. **브라우저 E2E 로그인 검증 완료**(daehan@onwork.kr) |
| 2026-05-23 | Phase 2: HR(인사) | Done | hr_change_requests/employee_change_histories 엔티티(JSONB), Notification+Service, HrService(CREATE/UPDATE/RESIGN 승인전 미반영·감사이력·알림·반려사유필수·자기승인금지), HrController(/hr/employees·/change-requests, RBAC 등록HR_MANAGER/승인VP↑), 프론트 인사화면(결재함+직원목록). **E2E 검증**: 요청→승인전 미반영→승인→계정생성→신규직원 로그인 성공 + 브라우저 직원 23명 |
| 2026-05-23 | Phase 3~6 | Not started | 근태/휴가/결재·알림·온보딩·대시보드/통합검증 — 동일 파이프라인 |

## 다음 세션 재개 가이드
- 인프라: `cd onwork && docker compose up -d` (PostgreSQL 5432 / Redis 6379, 스키마·시드 자동)
- 백엔드: `cd backend && ./gradlew bootRun` → :8080 / 시드 비번 전원 `onwork1234!`
- 프론트: `cd frontend && npm run dev` → :5173
- 검증 계정: daehan@onwork.kr(CEO), jisoo@onwork.kr(HR_MANAGER), hyunjun@onwork.kr(MANAGER 개발팀), haeun@onwork.kr(EMPLOYEE)
- 패턴: 엔티티(common.domain/<module>.domain) → 리포지토리 → Service(@Transactional, BusinessException+ErrorCode) → Controller(@PreAuthorize, SecurityUtil.currentPrincipal) → 프론트(AppLayout+api+data-testid) → curl/브라우저 E2E → 커밋
- **다음 작업: Phase 3 근태** — daily_work_records/work_anomalies/overtime_requests/monthly_summaries/attendance_settings 엔티티+리포지토리, AttendanceService(출퇴근 기록 UC-ATTENDANCE-01: 서버시각·당일1회·정시범위 grace 비교·status NORMAL/ANOMALY, 시간외근로 신청/승인 UC-04/03), **자정 배치(ADR-ATT-001: @Scheduled cron 0 0 0 * * * → 당일 clock_in 없는 ACTIVE 직원 ABSENT work_anomaly 자동생성)**, 팀근태 이상자 조회/확정(UC-02, 팀장 범위), AttendanceController /api/v1/attendance/*, 프론트 근태화면(출근/퇴근 버튼+이상자목록). 스펙 갭은 questions.md 참고.
