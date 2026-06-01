# Progress

| Date | Item | Status | Notes |
|---|---|---|---|
| 2026-05-23 | 프로젝트 초기화 | Done | `devauto init` 거버넌스 골격, validate 122 통과 |
| 2026-05-23 | Phase 0: 기반 세팅 | Done | db/schema.sql 21테이블 + seed 22명(라이브 PostgreSQL 검증), docker-compose(PG16+Redis7), Spring Boot 3.3/Java21 골격 build OK, React+Vite+TS 골격 build OK |
| 2026-05-23 | Phase 1: 공통 기반 + Auth | Done | JWT(Access30m/Refresh7d)+RBAC5단계 계층, Redis 블랙리스트+5회잠금, {code,message} 전역예외, User/Department/WorkGroup/UserCredential 엔티티+리포지토리, AuthService/Controller(login·refresh·logout·me), CORS. 프론트 로그인→대시보드 셸. **브라우저 E2E 로그인 검증 완료**(daehan@onwork.kr) |
| 2026-05-23 | Phase 2: HR(인사) | Done | hr_change_requests/employee_change_histories 엔티티(JSONB), Notification+Service, HrService(CREATE/UPDATE/RESIGN 승인전 미반영·감사이력·알림·반려사유필수·자기승인금지), HrController(/hr/employees·/change-requests, RBAC 등록HR_MANAGER/승인VP↑), 프론트 인사화면(결재함+직원목록). **E2E 검증**: 요청→승인전 미반영→승인→계정생성→신규직원 로그인 성공 + 브라우저 직원 23명 |
| 2026-05-23 | Phase 3: 근태 | Done | DailyWorkRecord/WorkAnomaly/OvertimeRequest/AttendanceSetting, AttendanceService(출퇴근 grace 판정·결근배치 detectAbsences·팀범위 이상목록·이상확인·시간외 신청/승인), AttendanceScheduler(@Scheduled 자정, ADR-ATT-001), 프론트 근태화면. **E2E**: 지각/조퇴/중복차단/결근22건/시간외승인 + 브라우저 팀장8건 vs CEO24건 스코핑·확인 |
| 2026-05-23 | Phase 4: 휴가 | Done | Leave* 엔티티, 승인 차감/취소 롤백, 대행(ADR-003), 반차 0.5 계산, leave_histories. E2E + 프론트 |
| 2026-05-23 | Phase 5: 결재·알림·온보딩·대시보드 | Done | 통합 결재함(approvals 집계), 알림 목록/읽음, 온보딩 투어, 대시보드 요약 위젯 + 알림배지. E2E |
| 2026-05-23 | Phase 6: 통합검증·CI·추적성 | Done | ArchUnit 레이어 4규칙 + 도메인 13 + contextLoads 통과, CI(PG16+Redis7 서비스·스키마/시드·gradle test·프론트 build), docs/추적성_매핑.md, README 갱신 |
| 2026-05-23 | **전체 6단계 완료** | Done | Auth·HR·근태·휴가·결재/알림/온보딩/대시보드 풀스택 + 통합검증. 모든 ADR 동작 입증 |
| 2026-06-01 | DevAuto 0529 보완 실행 착수 | In Progress | DevAuto 템플릿 validate pass. OnWork validate는 README `실행` 섹션 누락으로 fail 확인 후 DevAuto 실행 계획/입력 산출물 갱신 중 |
| 2026-06-01 | DevAuto 검증 | Done | `python3 -m devauto validate <onwork>` 통과: 122 checks |
| 2026-06-01 | 프로젝트 기준선 검증 | Done | Docker Desktop 시작 후 `docker compose up -d`, `./gradlew test` 통과. hooks lint 5건 수정 후 `npm run lint` 통과. `npm run build` 통과 |
| 2026-06-01 | 0529 canonical API 1차 구현 | Done | 전역 `snake_case`, HR batch-process, 근태 canonical aliases/monthly summary/Asia-Seoul Clock, 휴가 canonical routes/cancel-approved/grants/summary, approvals canonical process, onboarding tutorials API/UI, 결재 상세 drawer/HR 직원 상세 drawer 반영 |
| 2026-06-01 | 역할별 화면 분기 및 0529 재점검 | Done | 직원은 `마이페이지` 중심으로 결재함 숨김 및 `/approvals` 직접 접근 차단, 매니저는 `팀원`, HR/임원은 `인사` 화면으로 분리. `docs/추적성_매핑.md`에 PDF/녹음 피드백 대비 반영·잔여 항목 재정리. `npm run lint`, `npm run build`, `./gradlew test --rerun-tasks`, DevAuto validate, 브라우저 역할 스모크 통과 |

## 다음 세션 재개 가이드
- 인프라: `cd onwork && docker compose up -d` (PostgreSQL 5432 / Redis 6379, 스키마·시드 자동)
- 백엔드: `cd backend && ./gradlew bootRun` → :8080 / 시드 비번 전원 `onwork1234!`
- 프론트: `cd frontend && npm run dev` → :5173
- 검증 계정: daehan@onwork.kr(CEO), jisoo@onwork.kr(HR_MANAGER), hyunjun@onwork.kr(MANAGER 개발팀), haeun@onwork.kr(EMPLOYEE)
- 패턴: DevAuto `plan.md` → `inputs/`/`usecases/` → API/DB/프론트 구현 → test-agent → qa-frontend-agent → docs-agent → git-history-agent
- **다음 작업: 잔여 0529 보완** — 근태 이상 유형 보정/월마감 잠금, 물리 `approvals` 라우팅 테이블 동기화, p95/p99 성능 스크립트와 보고서를 보강한다.
