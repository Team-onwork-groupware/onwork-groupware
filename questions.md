# Questions — 비동기 Q&A 보드

에이전트가 진행 중 사람의 결정이 필요할 때, 멈추는 대신 여기에 질문을 **append** 한다.
사람은 비동기로 Answer를 채우고, orchestrator는 다음 디스패치 전에 이 파일을 폴링해 반영한다.
(교수님 회의 요청: 에이전트들이 일하는 동안 사람이 옆에서 비동기로 결정을 던질 수 있는 채널.)

## 사용 규칙

- **에이전트**: 결정이 필요하면 아래 표에 `OPEN` 으로 질문을 추가하고, 자기 작업은 가능한 범위까지 계속 진행한다 (블로킹 최소화).
- **사람**: `Answer` 칸을 채우고 `Status` 를 `ANSWERED` 로 바꾼다.
- **orchestrator**: 다음 에이전트 디스패치 전에 이 파일을 읽어, `ANSWERED` 항목을 반영하고 `RESOLVED` 로 바꾼다. `OPEN` 항목이 진행을 막으면 `plan.md` Auto-Continue Policy 에 따라 처리한다.

## Board

| ID | Asked by | Status | Question | Answer |
|---|---|---|---|---|
| Q-001 | requirement-analyzer | OPEN | `users` 테이블에 RBAC 권한(`role`: CEO/VP/HR_MANAGER/MANAGER/EMPLOYEE)과 직급(`position`: 과장/대리 등)이 데이터사전엔 없으나 API 응답·RBAC·조직도가 요구함. 두 컬럼을 추가했는데, 팀의 의도와 일치하는가? (별도 테이블 분리 vs users 컬럼) | |
| Q-002 | requirement-analyzer | OPEN | 일정(Schedule) 모듈은 발표 nav엔 있으나 유스케이스·데이터사전·API에 미명세. v1 범위에서 제외했는데 맞는가? 포함이면 스펙 보강 필요. | |
| Q-003 | requirement-analyzer | OPEN | DB가 데이터사전은 MySQL 표기(BIGINT AUTO_INCREMENT)인데 확정 아키텍처는 PostgreSQL. PostgreSQL 기준(IDENTITY/JSONB)으로 변환했는데 맞는가? | |
