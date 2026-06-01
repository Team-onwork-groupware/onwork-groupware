# OnWork — 그룹웨어 (근태 · 휴가 · 인사)

Spring Boot 3.3(Java 21) + React 18(TypeScript/Vite) + PostgreSQL 16 + Redis 7 기반의 사내 그룹웨어.
근태, 휴가, 인사 관리를 RBAC 5단계 권한과 결재 흐름으로 제공한다.

> 이 프로젝트는 **개발 자동화 플랫폼(devauto)의 멀티에이전트 파이프라인**으로 개발되었다.
> 문서(유스케이스/API/ADR/데이터사전)를 입력으로 기획→설계→구현→검증을 단계별로 수행했다.

## 요구사항
- Docker (PostgreSQL + Redis), JDK 21, Node 20+

## 실행
```bash
# 1) 인프라 (PostgreSQL 5432 / Redis 6379, 스키마·시드 자동 적용)
docker compose up -d

# 2) 백엔드 (http://localhost:8080)
cd backend && ./gradlew bootRun

# 3) 프론트엔드 (http://localhost:5173)
cd frontend && npm install && npm run dev
```
로그인 계정(시드, 비밀번호 전원 `onwork1234!`):
- `daehan@onwork.kr` (CEO) · `jisoo@onwork.kr`(HR_MANAGER) · `hyunjun@onwork.kr`(MANAGER/개발팀) · `haeun@onwork.kr`(EMPLOYEE)

## DevAuto 실행
이 저장소는 `남재현교수님_프로젝트/dev_automation_agent_templates`의 DevAuto 파이프라인을 적용한다.

```bash
cd /Users/sungminkim/Desktop/4-1/남재현교수님_프로젝트/dev_automation_agent_templates
python3 -m devauto validate /Users/sungminkim/Desktop/4-1/그룹웨어프로젝트/onwork
```

OnWork 보완 개발은 `plan.md`의 0529 명세 정합화 계획을 기준으로 `requirement-analyzer → nfr-performance-agent → api-integration-agent → implementation-agent → frontend-agent → test-agent → qa-frontend-agent → docs-agent` 순서로 진행한다.

## 검증
```bash
cd backend && ./gradlew test     # 단위 + ArchUnit(레이어 경계) + 컨텍스트 로드
cd frontend && npm run build      # 타입체크 + 번들
```

## 모듈
| 모듈 | 핵심 | 주요 API |
|---|---|---|
| 인증 | JWT + RBAC 5단계, Redis 블랙리스트/잠금 | `/api/v1/auth/*` |
| 인사 | 결재 전 미반영 승인 흐름, 감사 이력, soft delete | `/api/v1/hr/*` |
| 근태 | 출퇴근(grace 판정), 결근 자정 배치, 시간외 | `/api/v1/attendance/*` |
| 휴가 | 승인 차감/취소 롤백, 대행 결재(ADR-003) | `/api/v1/leave/*` |
| 결재·알림·온보딩·대시보드 | 통합 결재함, 알림, 가이드 투어, 위젯 | `/api/v1/{approvals,notifications,onboarding,dashboard}/*` |

## 아키텍처
- Layered (web → service → repository → domain), 모듈별 패키지 분리 (ADR-SYS-001, ArchUnit 강제)
- 인증/인가: Stateless JWT + RBAC 계층 (ADR-SYS-002)
- 결재 공통 원칙: 승인 전 실데이터 미반영, 반려/보류 사유 필수
- 상세 추적성: [docs/추적성_매핑.md](docs/추적성_매핑.md)

## 구조
```text
onwork/
├── backend/    Spring Boot (kr.onwork.{auth,hr,attendance,leave,approval,notification,onboarding,dashboard,common})
├── frontend/   React + Vite (pages, components, lib)
├── db/         schema.sql (21 tables) + seed.sql
├── docs/       추적성_매핑.md
├── docker-compose.yml
└── .github/workflows/ci.yml
```
