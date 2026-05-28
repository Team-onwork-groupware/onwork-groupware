-- OnWork 시연용 더미 데이터 (계획서 22명 시드 위에 얹는 활동 데이터)
-- 기준일: 2026-05-23 (Sat) — 지난 한 주(5/18~22) 근태 + 진행 중인 결재/휴가/알림
-- 적용: PGPASSWORD=onwork psql -h localhost -U onwork -d onwork -f db/demo_seed.sql

BEGIN;

-- ============================================================
-- 1) 근태 — 지난 5 영업일 (5/18 Mon ~ 5/22 Fri), 일부 이상 포함
-- ============================================================
INSERT INTO daily_work_records (user_id, date, clock_in_at, clock_out_at, overtime_minutes, status) VALUES
-- 5/18 Mon
(1,  '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:05', 0,  'NORMAL'),
(2,  '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:10', 0,  'NORMAL'),
(3,  '2026-05-18', '2026-05-18 08:58', '2026-05-18 18:02', 0,  'NORMAL'),
(5,  '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:00', 0,  'NORMAL'),
(6,  '2026-05-18', '2026-05-18 09:00', '2026-05-18 18:00', 0,  'NORMAL'),
(7,  '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:05', 0,  'NORMAL'),
(8,  '2026-05-18', '2026-05-18 09:00', '2026-05-18 19:30', 90, 'NORMAL'),
(9,  '2026-05-18', '2026-05-18 09:18', '2026-05-18 18:00', 0,  'ANOMALY'),  -- 류하은 지각
(10, '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:00', 0,  'NORMAL'),
(11, '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:00', 0,  'NORMAL'),
(17, '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:05', 0,  'NORMAL'),
(19, '2026-05-18', NULL,               NULL,               0,  'ANOMALY'),  -- 이보람 결근

-- 5/19 Tue
(1,  '2026-05-19', '2026-05-19 08:55', '2026-05-19 18:00', 0,  'NORMAL'),
(5,  '2026-05-19', '2026-05-19 08:50', '2026-05-19 18:05', 0,  'NORMAL'),
(7,  '2026-05-19', '2026-05-19 08:55', '2026-05-19 17:35', 0,  'ANOMALY'),  -- 오다연 조퇴
(9,  '2026-05-19', '2026-05-19 08:58', '2026-05-19 18:00', 0,  'NORMAL'),
(11, '2026-05-19', '2026-05-19 08:50', '2026-05-19 18:00', 0,  'NORMAL'),
(17, '2026-05-19', '2026-05-19 08:55', '2026-05-19 18:05', 0,  'NORMAL'),

-- 5/20 Wed
(5,  '2026-05-20', '2026-05-20 08:55', '2026-05-20 18:00', 0,  'NORMAL'),
(9,  '2026-05-20', '2026-05-20 09:00', '2026-05-20 18:00', 0,  'NORMAL'),
(10, '2026-05-20', '2026-05-20 08:58', NULL,               0,  'ANOMALY'),  -- 임준서 퇴근누락
(11, '2026-05-20', '2026-05-20 08:50', '2026-05-20 18:00', 0,  'NORMAL'),
(15, '2026-05-20', NULL,               NULL,               0,  'ANOMALY'),  -- 신유진 결근
(17, '2026-05-20', '2026-05-20 08:55', '2026-05-20 18:00', 0,  'NORMAL'),

-- 5/21 Thu
(5,  '2026-05-21', '2026-05-21 08:55', '2026-05-21 18:05', 0,  'NORMAL'),
(6,  '2026-05-21', '2026-05-21 09:00', '2026-05-21 18:00', 0,  'NORMAL'),
(9,  '2026-05-21', '2026-05-21 08:58', '2026-05-21 18:00', 0,  'NORMAL'),
(11, '2026-05-21', '2026-05-21 08:55', '2026-05-21 18:00', 0,  'NORMAL'),
(17, '2026-05-21', '2026-05-21 08:50', '2026-05-21 18:00', 0,  'NORMAL'),

-- 5/22 Fri
(5,  '2026-05-22', '2026-05-22 08:55', '2026-05-22 18:05', 0,  'NORMAL'),
(8,  '2026-05-22', '2026-05-22 09:00', '2026-05-22 18:00', 0,  'NORMAL'),
(9,  '2026-05-22', '2026-05-22 09:15', '2026-05-22 18:00', 0,  'ANOMALY'),  -- 류하은 지각
(11, '2026-05-22', '2026-05-22 08:50', '2026-05-22 18:00', 0,  'NORMAL'),
(12, '2026-05-22', '2026-05-22 08:55', '2026-05-22 17:35', 0,  'ANOMALY'),  -- 백민준 조퇴
(17, '2026-05-22', '2026-05-22 08:55', '2026-05-22 18:00', 0,  'NORMAL');

-- 이상 매핑
INSERT INTO work_anomalies (daily_work_record_id, anomaly_type) VALUES
((SELECT id FROM daily_work_records WHERE user_id=9  AND date='2026-05-18'), 'LATE'),
((SELECT id FROM daily_work_records WHERE user_id=19 AND date='2026-05-18'), 'ABSENT'),
((SELECT id FROM daily_work_records WHERE user_id=7  AND date='2026-05-19'), 'EARLY_LEAVE'),
((SELECT id FROM daily_work_records WHERE user_id=10 AND date='2026-05-20'), 'CLOCK_MISSING'),
((SELECT id FROM daily_work_records WHERE user_id=15 AND date='2026-05-20'), 'ABSENT'),
((SELECT id FROM daily_work_records WHERE user_id=9  AND date='2026-05-22'), 'LATE'),
((SELECT id FROM daily_work_records WHERE user_id=12 AND date='2026-05-22'), 'EARLY_LEAVE');

-- ============================================================
-- 2) 시간외근로 — PENDING 2, APPROVED 1
-- ============================================================
INSERT INTO overtime_requests (user_id, request_date, expected_start_at, expected_end_at, reason, status, approver_id) VALUES
(9, '2026-05-25', '2026-05-25 18:30', '2026-05-25 20:30', '릴리스 대응',     'PENDING',  NULL),
(7, '2026-05-26', '2026-05-26 18:30', '2026-05-26 21:00', '월말 마감 작업',  'PENDING',  NULL),
(8, '2026-05-18', '2026-05-18 18:00', '2026-05-18 19:30', '장애 대응',       'APPROVED', 5);

-- ============================================================
-- 3) HR 변경 요청 — 경영진 결재 대기 2건
-- ============================================================
INSERT INTO hr_change_requests (change_type, target_user_id, payload, reason, status, requested_by) VALUES
('CREATE', NULL,
 '{"name":"김신입","email":"newhire@onwork.kr","hire_date":"2026-06-01","department_id":2,"position":"사원","role":"EMPLOYEE"}'::jsonb,
 '6월 개발팀 신규 입사 예정', 'PENDING', 3),
('UPDATE', 13,
 '{"position":"과장"}'::jsonb,
 '2026 상반기 진급', 'PENDING', 4);

-- ============================================================
-- 4) 휴가 — PENDING 3 (팀장 결재 대기) / APPROVED 2 (이력) / CANCELLED 1 / ON_HOLD 1
-- ============================================================
-- PENDING
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 6,  id, '2026-05-26', '2026-05-27', 2.0, '개인 사정',          'PENDING'
  FROM leave_balances WHERE user_id=6  AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 13, id, '2026-05-25', '2026-05-25', 0.5, '병원 예약 (오전반차)','PENDING'
  FROM leave_balances WHERE user_id=13 AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 18, id, '2026-05-27', '2026-05-29', 3.0, '가족 여행',          'PENDING'
  FROM leave_balances WHERE user_id=18 AND leave_type_id=1 AND year=2026;

-- APPROVED (이력 + 잔여 차감)
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 7,  id, '2026-05-15', '2026-05-15', 1.0, '경조사', 'APPROVED', 5, '2026-05-13 14:00'
  FROM leave_balances WHERE user_id=7  AND leave_type_id=1 AND year=2026;
UPDATE leave_balances SET used_days = used_days + 1.0 WHERE user_id=7  AND leave_type_id=1 AND year=2026;

INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 17, id, '2026-05-12', '2026-05-13', 2.0, '개인 사정', 'APPROVED', 1, '2026-05-10 11:00'
  FROM leave_balances WHERE user_id=17 AND leave_type_id=1 AND year=2026;
UPDATE leave_balances SET used_days = used_days + 2.0 WHERE user_id=17 AND leave_type_id=1 AND year=2026;

-- CANCELLED (한 번 승인 후 취소)
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 19, id, '2026-05-14', '2026-05-14', 1.0, '취소된 일정', 'CANCELLED', 17, '2026-05-12 09:00'
  FROM leave_balances WHERE user_id=19 AND leave_type_id=1 AND year=2026;

-- ON_HOLD (보류)
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, hold_reason)
SELECT 22, id, '2026-05-30', '2026-06-01', 1.0, '가족 행사', 'ON_HOLD', 17, '월말 마감 시기 — 일정 재조정 필요'
  FROM leave_balances WHERE user_id=22 AND leave_type_id=1 AND year=2026;

-- 잔여 변동 이력 (감사 추적)
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -1.0, 20.0, 19.0, 5
  FROM leave_requests lr WHERE lr.user_id=7 AND lr.start_date='2026-05-15';
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -2.0, 20.0, 18.0, 1
  FROM leave_requests lr WHERE lr.user_id=17 AND lr.start_date='2026-05-12';
-- 19번 boram: USE → CANCEL (롤백)
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -1.0, 20.0, 19.0, 17
  FROM leave_requests lr WHERE lr.user_id=19 AND lr.start_date='2026-05-14';
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'CANCEL', 1.0, 19.0, 20.0, 19
  FROM leave_requests lr WHERE lr.user_id=19 AND lr.start_date='2026-05-14';

-- ============================================================
-- 5) 알림
-- ============================================================
-- HR 변경 요청 → 경영진 알림
INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT u.id, 'HR_CHANGE_REQUESTED', 'HR', r.id,
       '새 인사 변경 요청('|| r.change_type ||')이 결재 대기 중입니다'
  FROM users u, hr_change_requests r
 WHERE u.role IN ('CEO','VP') AND r.status='PENDING';

-- 휴가 신청 → 각 부서 결재자 알림
INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT la.approver_id, 'LEAVE_REQUESTED', 'LEAVE', lr.id,
       '새 휴가 신청이 결재 대기 중입니다'
  FROM leave_requests lr
  JOIN users u ON u.id = lr.user_id
  JOIN leave_approvers la ON la.department_id = u.department_id
 WHERE lr.status='PENDING';

-- 휴가 승인 → 신청자 알림 (과거건 — 읽음 처리)
INSERT INTO notifications (user_id, type, ref_type, ref_id, message, is_read)
SELECT lr.user_id, 'LEAVE_APPROVED', 'LEAVE', lr.id, '휴가 신청이 승인되었습니다', TRUE
  FROM leave_requests lr WHERE lr.status='APPROVED';

-- 휴가 보류 → 신청자 알림
INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT lr.user_id, 'LEAVE_ON_HOLD', 'LEAVE', lr.id,
       '휴가 신청이 보류되었습니다: '||lr.hold_reason
  FROM leave_requests lr WHERE lr.status='ON_HOLD';

-- ============================================================
-- 6) 온보딩 — MANAGER_TOUR 진행 중
-- ============================================================
INSERT INTO onboarding_tutorial_progress (user_id, tutorial_code, status, current_step, last_shown_at)
VALUES (5, 'MANAGER_TOUR', 'IN_PROGRESS', 2, '2026-05-22 10:00');

-- ============================================================
-- 7) 시연용 에이징(긴급 배지 시각화) — 결재 피로도 개선 #3
-- ============================================================
UPDATE hr_change_requests SET created_at = NOW() - INTERVAL '4 days'
 WHERE status='PENDING' AND change_type='CREATE';
UPDATE leave_requests SET created_at = NOW() - INTERVAL '5 days'
 WHERE status='PENDING' AND user_id = 6;   -- 강태양 휴가
UPDATE overtime_requests SET created_at = NOW() - INTERVAL '3 days'
 WHERE status='PENDING' AND user_id = 9;   -- 류하은 시간외

COMMIT;

-- 결과 요약
\echo '====== 시연 데이터 적용 결과 ======'
SELECT 'daily_work_records' AS t, count(*)::text AS cnt FROM daily_work_records
UNION ALL SELECT 'work_anomalies', count(*)::text FROM work_anomalies
UNION ALL SELECT 'overtime_requests (PENDING)', count(*)::text FROM overtime_requests WHERE status='PENDING'
UNION ALL SELECT 'hr_change_requests (PENDING)', count(*)::text FROM hr_change_requests WHERE status='PENDING'
UNION ALL SELECT 'leave_requests (PENDING)', count(*)::text FROM leave_requests WHERE status='PENDING'
UNION ALL SELECT 'leave_requests (APPROVED)', count(*)::text FROM leave_requests WHERE status='APPROVED'
UNION ALL SELECT 'leave_requests (ON_HOLD)', count(*)::text FROM leave_requests WHERE status='ON_HOLD'
UNION ALL SELECT 'leave_histories', count(*)::text FROM leave_histories
UNION ALL SELECT 'notifications', count(*)::text FROM notifications;
