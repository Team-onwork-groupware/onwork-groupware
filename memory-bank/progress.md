# Progress

| Date | Item | Status | Notes |
|---|---|---|---|
| 2026-05-23 | 프로젝트 초기화 | Done | `devauto init` 거버넌스 골격, validate 122 통과 |
| 2026-05-23 | Phase 0: 기반 세팅 | Done | db/schema.sql 21테이블 + seed 22명(라이브 PostgreSQL 검증), docker-compose(PG16+Redis7), Spring Boot 3.3/Java21 골격 build OK, React+Vite+TS 골격 build OK |
| 2026-05-23 | Phase 1: 공통 기반 + Auth | Done | JWT(Access30m/Refresh7d)+RBAC5단계 계층, Redis 블랙리스트+5회잠금, {code,message} 전역예외, User/Department/WorkGroup/UserCredential 엔티티+리포지토리, AuthService/Controller(login·refresh·logout·me), CORS. 프론트 로그인→대시보드 셸. **브라우저 E2E 로그인 검증 완료**(daehan@onwork.kr) |
| 2026-05-23 | Phase 2~6 | Not started | HR/근태/휴가/결재·알림·온보딩·대시보드/통합검증 — 다음 세션에서 동일 파이프라인으로 진행 |

## 다음 세션 재개 가이드
- 인프라: `cd onwork && docker compose up -d` (PostgreSQL 5432 / Redis 6379, 스키마·시드 자동)
- 백엔드: `cd backend && ./gradlew bootRun` → http://localhost:8080 / 시드 비번 전원 `onwork1234!`
- 프론트: `cd frontend && npm run dev` → http://localhost:5173
- 다음 작업: Phase 2 HR — `hr_change_requests`/`employee_change_histories` 엔티티+리포지토리, ApprovalHandler 인터페이스(ADR-HR-002), HrService(승인 전 미반영 패턴), `/api/v1/hr/*` 컨트롤러, 프론트 인사 화면. 스펙 갭은 `questions.md` 참고.
