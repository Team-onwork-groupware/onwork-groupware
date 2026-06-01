# UC-APP-0529: 통합 결재함 canonical API와 상세 확인 UI

## Goal

통합 결재함을 0529 명세의 canonical API로 정리하고, 교수님 피드백에 따라 결재 전 상세 내용을 확인할 수 있게 한다.

## Actors

- 승인자: 본인에게 도착한 결재 항목을 조회하고 처리한다.
- 신청자: 본인이 올린 요청의 결재 상태를 확인한다.
- 시스템: HR, 휴가, 근태 등 업무 요청을 공통 결재 인덱스로 노출한다.

## Main Flow

1. 승인자가 `GET /api/v1/approvals`로 결재함을 조회한다.
2. 승인자가 결재 행을 클릭한다.
3. 프론트는 drawer 또는 modal로 원문, 변경 전후 값, 사유, 기간, 첨부 가능한 메타데이터를 보여준다.
4. 승인자가 `PATCH /api/v1/approvals/{id}/process`로 승인, 반려, 보류 중 하나를 처리한다.
5. 시스템은 원 업무 도메인의 상태와 결재 인덱스를 함께 갱신한다.

## Rules

- `approvals` 테이블은 실제 결재 라우팅 인덱스로 사용한다.
- 기존 `/approvals/inbox`, `/approvals/batch`는 프론트 의존을 제거하고 필요 시 호환 alias로만 둔다.
- 결재 목록만 보고 승인하지 않도록 상세 보기 진입이 쉬워야 한다.
- 응답 필드는 `snake_case`를 사용한다.

## Acceptance Criteria

- `GET /api/v1/approvals`와 `PATCH /api/v1/approvals/{id}/process`가 canonical API로 동작한다.
- 결재 상세 UI에서 업무 유형별 핵심 정보가 누락 없이 보인다.
- 결재 처리 후 목록 상태와 상세 상태가 즉시 갱신된다.
- 결재 피로도를 줄이기 위해 bulk action은 상세 확인 가능성과 충돌하지 않게 설계된다.
