# UC-LEAVE-0529: 휴가 신청/승인자 취소/보상휴가 부여 정합화

## Goal

휴가 API를 0529 명세 경로로 정리하고, 승인된 휴가는 신청자가 직접 취소하지 못하도록 한다. 승인자 전용 취소와 보상휴가 부여 흐름을 구현한다.

## Actors

- 직원: 휴가 잔여일수를 확인하고 휴가를 신청한다.
- 승인자: 휴가 신청을 승인, 반려, 보류, 승인 취소 처리한다.
- HR: 보상휴가를 부여하고 요약을 확인한다.

## Main Flow

1. 직원이 `GET /api/v1/leave-balances/me`로 잔여일수를 확인한다.
2. 직원이 `POST /api/v1/leave-requests`로 휴가를 신청한다.
3. 승인자가 신청 상세를 확인하고 처리한다.
4. 승인된 휴가를 취소해야 할 경우 승인자가 `cancel-approved` API를 호출한다.
5. 시스템은 잔여일수를 환원하고 승인 취소 이력을 남긴다.
6. HR은 `/api/v1/leave-grants`로 보상휴가를 부여하고 `/leave-requests/summary`로 현황을 본다.

## Rules

- 신청자는 `APPROVED` 상태의 휴가를 직접 취소할 수 없다.
- 승인자 취소는 잔여일수 환원과 감사 이력을 포함한다.
- 보류 상태는 승인 대기와 구분되어야 한다.
- 응답 필드는 `snake_case`를 사용한다.

## Acceptance Criteria

- `/leave-balances/me`, `/leave-requests*`, `/leave-grants`, `/leave-requests/summary`가 canonical API로 동작한다.
- 승인 휴가 신청자 직접 취소 시 권한 오류 또는 상태 오류를 반환한다.
- 승인자 취소 후 잔여일수와 이력이 일관된다.
- 직원 휴가 신청, 승인자 처리, 승인자 취소 통합 테스트가 통과한다.
