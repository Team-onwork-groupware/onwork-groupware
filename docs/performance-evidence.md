# OnWork 성능 검증 기록

## 목적
0529 문서와 교수님 피드백의 품질 근거 요구에 맞춰, 발표/시연 핵심 API가 어느 정도 응답 성능을 내는지 재현 가능한 방식으로 기록한다.

## 대상 API
- `POST /api/v1/auth/login`
- `GET /api/v1/approvals`
- `GET /api/v1/hr/employees`
- `GET /api/v1/attendance/anomalies`

## 실행
```bash
ONWORK_BASE_URL=http://localhost:8080/api/v1 \
ONWORK_EMAIL=hyunjun@onwork.kr \
ONWORK_PASSWORD='onwork1234!' \
ONWORK_LOAD_CONCURRENCY=8 \
ONWORK_LOAD_DURATION_SEC=30 \
node tools/perf/onwork-load.mjs
```

## 기록 양식
| 일시 | 브랜치/커밋 | 시나리오 | 동시성 | p95 | p99 | 실패율 | DB 병목/메모 |
| --- | --- | --- | ---: | ---: | ---: | ---: | --- |
|  |  | login | 8 |  |  |  |  |
|  |  | approvals | 8 |  |  |  |  |
|  |  | employees | 8 |  |  |  |  |
|  |  | attendance_anomalies | 8 |  |  |  |  |

## 판정 기준
- 발표 시연 기준: p95 500ms 이하, 실패율 1% 이하.
- 초과 시 우선 확인: DB 인덱스, 결재함 조회 범위, N+1 조회, 인증/Redis 병목.
- 구조 변경이 필요한 경우 ADR에 선택지와 개선 전후 수치를 함께 남긴다.
