# Progress

| Date | Item | Status | Notes |
|---|---|---|---|
| 2026-05-23 | 프로젝트 초기화 | Done | `devauto init` 거버넌스 골격, validate 122 통과 |
| 2026-05-23 | Phase 0: 기반 세팅 | Done | db/schema.sql 21테이블 + seed 22명(라이브 PostgreSQL 검증), docker-compose(PG16+Redis7), Spring Boot 3.3/Java21 골격 build OK, React+Vite+TS 골격 build OK |
| 2026-05-23 | Phase 1: 공통 기반 + Auth | Done | JWT(Access30m/Refresh7d)+RBAC5단계 계층, Redis 블랙리스트+5회잠금, {code,message} 전역예외, User/Department/WorkGroup/UserCredential 엔티티+리포지토리, AuthService/Controller(login·refresh·logout·me), CORS. 프론트 로그인→대시보드 셸. **브라우저 E2E 로그인 검증 완료**(daehan@onwork.kr) |
| 2026-05-23 | Phase 2: HR(인사) | Done | hr_change_requests/employee_change_histories 엔티티(JSONB), Notification+Service, HrService(CREATE/UPDATE/RESIGN 승인전 미반영·감사이력·알림·반려사유필수·자기승인금지), HrController(/hr/employees·/change-requests, RBAC 등록HR_MANAGER/승인VP↑), 프론트 인사화면(결재함+직원목록). **E2E 검증**: 요청→승인전 미반영→승인→계정생성→신규직원 로그인 성공 + 브라우저 직원 23명 |
| 2026-05-23 | Phase 3: 근태 | Done | DailyWorkRecord/WorkAnomaly/OvertimeRequest/AttendanceSetting, AttendanceService(출퇴근 grace 판정·결근배치 detectAbsences·팀범위 이상목록·이상확인·시간외 신청/승인), AttendanceScheduler(@Scheduled 자정, ADR-ATT-001), 프론트 근태화면. **E2E**: 지각/조퇴/중복차단/결근22건/시간외승인 + 브라우저 팀장8건 vs CEO24건 스코핑·확인 |
| 2026-05-23 | Phase 4~6 | Not started | 휴가/결재·알림·온보딩·대시보드/통합검증 — 동일 파이프라인 |

## 다음 세션 재개 가이드
- 인프라: `cd onwork && docker compose up -d` (PostgreSQL 5432 / Redis 6379, 스키마·시드 자동)
- 백엔드: `cd backend && ./gradlew bootRun` → :8080 / 시드 비번 전원 `onwork1234!`
- 프론트: `cd frontend && npm run dev` → :5173
- 검증 계정: daehan@onwork.kr(CEO), jisoo@onwork.kr(HR_MANAGER), hyunjun@onwork.kr(MANAGER 개발팀), haeun@onwork.kr(EMPLOYEE)
- 패턴: 엔티티(common.domain/<module>.domain) → 리포지토리 → Service(@Transactional, BusinessException+ErrorCode) → Controller(@PreAuthorize, SecurityUtil.currentPrincipal) → 프론트(AppLayout+api+data-testid) → curl/브라우저 E2E → 커밋
- **다음 작업: Phase 4 휴가** — leave_types/leave_balances/leave_requests/holidays 엔티티+리포지토리, LeaveService(휴가 신청 UC-LEAVE-01: 잔여일수 검증·기간 중복 검증·신청 시점엔 차감 보류, **2단계 승인+대행 ADR-003: 팀장→인사/경영 순차, 부재 시 대행 결재자**, 최종 승인 시점에만 leave_balances 차감, 반려/취소 시 롤백), 휴가 잔여 조회, 휴가 결재함, LeaveController /api/v1/leave/*, 프론트 휴가화면(신청 폼+잔여+결재함). 연차 부여는 입사일/회계연도 기준(배치 또는 조회시 계산). schema.sql에서 leave_* 테이블 컬럼 확인 후 매핑. 스펙 갭은 questions.md 참고.
- 이후: Phase 5(결재함 통합 approvals·알림 목록/읽음·온보딩 onboarding_tasks·대시보드 위젯), Phase 6(JUnit 슬라이스+ArchUnit 레이어 규칙, GitHub Actions CI, OnWork_추적성체크리스트 대조).
