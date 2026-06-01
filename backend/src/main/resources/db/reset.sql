-- OnWork 시연용 초기화 스크립트 (DemoResetService가 트랜잭션으로 실행).
-- 모든 데이터 테이블을 비우고 기본 더미데이터(seed + demo_seed + seed_0601)를 재삽입한 뒤,
-- 시연 시나리오용으로 개발팀장(최현준)·기획팀장(한소희)을 오늘 휴가 상태로 만든다.
-- 순수 SQL만 사용(psql 메타명령 \echo 등 금지). 비밀번호는 전원 'onwork1234!'.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================ 0) 전체 초기화
TRUNCATE TABLE
  employee_change_histories, leave_grants, leave_histories, leave_requests, leave_balances,
  leave_approvers, leave_settings, leave_types,
  approvals, hr_change_requests, notifications,
  overtime_requests, work_anomalies, monthly_summaries, daily_work_records, attendance_settings,
  schedules, salaries, onboarding_tutorial_progress,
  user_credentials, users, departments, work_groups
  RESTART IDENTITY CASCADE;

-- ============================================================ 1) 마스터 (seed.sql)
INSERT INTO work_groups (id, group_name, work_start_time, work_end_time, is_default)
VALUES (1, '기본 9-to-6', '09:00', '18:00', TRUE);

INSERT INTO departments (id, name, status) VALUES
  (1, '경영지원팀', 'ACTIVE'),
  (2, '개발팀',     'ACTIVE'),
  (3, '영업팀',     'ACTIVE'),
  (4, '기획팀',     'ACTIVE');

INSERT INTO users (id, department_id, work_group_id, employee_no, name, email, role, position, status, hire_date) VALUES
  (1,  NULL, 1, '2020-001', '김대한', 'daehan@onwork.kr', 'CEO',        '대표이사',  'ACTIVE', '2020-01-02'),
  (2,  NULL, 1, '2020-002', '이민국', 'minguk@onwork.kr', 'VP',         '부대표이사','ACTIVE', '2020-01-02'),
  (3,  1,    1, '2020-003', '박지수', 'jisoo@onwork.kr',  'HR_MANAGER', '팀장',      'ACTIVE', '2020-02-01'),
  (4,  1,    1, '2023-010', '송미래', 'mirae@onwork.kr',  'HR_MANAGER', '사원',      'ACTIVE', '2023-03-02'),
  (5,  2,    1, '2020-004', '최현준', 'hyunjun@onwork.kr','MANAGER',    '차장',      'ACTIVE', '2020-03-01'),
  (6,  2,    1, '2021-005', '강태양', 'taeyang@onwork.kr','EMPLOYEE',   '과장',      'ACTIVE', '2021-04-01'),
  (7,  2,    1, '2022-006', '오다연', 'dayeon@onwork.kr', 'EMPLOYEE',   '대리',      'ACTIVE', '2022-05-02'),
  (8,  2,    1, '2023-007', '윤성호', 'seongho@onwork.kr','EMPLOYEE',   '주임',      'ACTIVE', '2023-06-01'),
  (9,  2,    1, '2024-008', '류하은', 'haeun@onwork.kr',  'EMPLOYEE',   '사원',      'ACTIVE', '2024-01-02'),
  (10, 2,    1, '2025-009', '임준서', 'junseo@onwork.kr', 'EMPLOYEE',   '사원',      'ACTIVE', '2025-03-02'),
  (11, 3,    1, '2020-010', '정수연', 'suyeon@onwork.kr', 'MANAGER',    '차장',      'ACTIVE', '2020-03-01'),
  (12, 3,    1, '2021-011', '백민준', 'minjun@onwork.kr', 'EMPLOYEE',   '과장',      'ACTIVE', '2021-04-01'),
  (13, 3,    1, '2022-012', '홍나리', 'nari@onwork.kr',   'EMPLOYEE',   '대리',      'ACTIVE', '2022-05-02'),
  (14, 3,    1, '2023-013', '전지후', 'jihoo@onwork.kr',  'EMPLOYEE',   '주임',      'ACTIVE', '2023-06-01'),
  (15, 3,    1, '2024-014', '신유진', 'yujin@onwork.kr',  'EMPLOYEE',   '사원',      'ACTIVE', '2024-01-02'),
  (16, 3,    1, '2025-015', '문도현', 'dohyun@onwork.kr', 'EMPLOYEE',   '사원',      'ACTIVE', '2025-03-02'),
  (17, 4,    1, '2020-016', '한소희', 'sohee@onwork.kr',  'MANAGER',    '차장',      'ACTIVE', '2020-03-01'),
  (18, 4,    1, '2021-017', '장하늘', 'haneul@onwork.kr', 'EMPLOYEE',   '과장',      'ACTIVE', '2021-04-01'),
  (19, 4,    1, '2022-018', '이보람', 'boram@onwork.kr',  'EMPLOYEE',   '대리',      'ACTIVE', '2022-05-02'),
  (20, 4,    1, '2023-019', '채원우', 'wonwoo@onwork.kr', 'EMPLOYEE',   '주임',      'ACTIVE', '2023-06-01'),
  (21, 4,    1, '2024-020', '고은서', 'eunseo@onwork.kr', 'EMPLOYEE',   '사원',      'ACTIVE', '2024-01-02'),
  (22, 4,    1, '2025-021', '남지원', 'jiwon@onwork.kr',  'EMPLOYEE',   '사원',      'ACTIVE', '2025-03-02');

UPDATE departments SET manager_id = 3  WHERE id = 1;
UPDATE departments SET manager_id = 5  WHERE id = 2;
UPDATE departments SET manager_id = 11 WHERE id = 3;
UPDATE departments SET manager_id = 17 WHERE id = 4;

INSERT INTO user_credentials (user_id, password_hash)
SELECT id, crypt('onwork1234!', gen_salt('bf')) FROM users;

INSERT INTO leave_types (id, code, name, days_unit, is_active) VALUES
  (1, 'ANNUAL',  '연차',      1.0, TRUE),
  (2, 'COMP',    '보상휴가',  1.0, TRUE),
  (3, 'HALF_AM', '오전반차',  0.5, TRUE),
  (4, 'HALF_PM', '오후반차',  0.5, TRUE);

INSERT INTO attendance_settings (id, grace_in_minutes, grace_out_minutes, late_threshold_count, is_overtime_auto_collect, updated_by)
VALUES (1, 10, 10, 3, FALSE, 1);
INSERT INTO leave_settings (id, annual_rollover, comp_expire_warning_days) VALUES (1, FALSE, 7);

INSERT INTO leave_approvers (department_id, approver_id, delegate_id, is_absent) VALUES
  (1, 3,  4,  FALSE),
  (2, 5,  3,  FALSE),
  (3, 11, 3,  FALSE),
  (4, 17, 3,  FALSE);

INSERT INTO leave_balances (user_id, leave_type_id, total_days, used_days, year)
SELECT id, 1, 20.0, 0.0, 2026 FROM users WHERE status = 'ACTIVE';

SELECT setval(pg_get_serial_sequence('work_groups','id'),    (SELECT MAX(id) FROM work_groups));
SELECT setval(pg_get_serial_sequence('departments','id'),    (SELECT MAX(id) FROM departments));
SELECT setval(pg_get_serial_sequence('users','id'),          (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('leave_types','id'),    (SELECT MAX(id) FROM leave_types));

-- ============================================================ 2) 거래성 더미 (demo_seed.sql)
INSERT INTO daily_work_records (user_id, date, clock_in_at, clock_out_at, overtime_minutes, status) VALUES
(1,  '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:05', 0,  'NORMAL'),
(2,  '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:10', 0,  'NORMAL'),
(3,  '2026-05-18', '2026-05-18 08:58', '2026-05-18 18:02', 0,  'NORMAL'),
(5,  '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:00', 0,  'NORMAL'),
(6,  '2026-05-18', '2026-05-18 09:00', '2026-05-18 18:00', 0,  'NORMAL'),
(7,  '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:05', 0,  'NORMAL'),
(8,  '2026-05-18', '2026-05-18 09:00', '2026-05-18 19:30', 90, 'NORMAL'),
(9,  '2026-05-18', '2026-05-18 09:18', '2026-05-18 18:00', 0,  'ANOMALY'),
(10, '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:00', 0,  'NORMAL'),
(11, '2026-05-18', '2026-05-18 08:50', '2026-05-18 18:00', 0,  'NORMAL'),
(17, '2026-05-18', '2026-05-18 08:55', '2026-05-18 18:05', 0,  'NORMAL'),
(19, '2026-05-18', NULL,               NULL,               0,  'ANOMALY'),
(1,  '2026-05-19', '2026-05-19 08:55', '2026-05-19 18:00', 0,  'NORMAL'),
(5,  '2026-05-19', '2026-05-19 08:50', '2026-05-19 18:05', 0,  'NORMAL'),
(7,  '2026-05-19', '2026-05-19 08:55', '2026-05-19 17:35', 0,  'ANOMALY'),
(9,  '2026-05-19', '2026-05-19 08:58', '2026-05-19 18:00', 0,  'NORMAL'),
(11, '2026-05-19', '2026-05-19 08:50', '2026-05-19 18:00', 0,  'NORMAL'),
(17, '2026-05-19', '2026-05-19 08:55', '2026-05-19 18:05', 0,  'NORMAL'),
(5,  '2026-05-20', '2026-05-20 08:55', '2026-05-20 18:00', 0,  'NORMAL'),
(9,  '2026-05-20', '2026-05-20 09:00', '2026-05-20 18:00', 0,  'NORMAL'),
(10, '2026-05-20', '2026-05-20 08:58', NULL,               0,  'ANOMALY'),
(11, '2026-05-20', '2026-05-20 08:50', '2026-05-20 18:00', 0,  'NORMAL'),
(15, '2026-05-20', NULL,               NULL,               0,  'ANOMALY'),
(17, '2026-05-20', '2026-05-20 08:55', '2026-05-20 18:00', 0,  'NORMAL'),
(5,  '2026-05-21', '2026-05-21 08:55', '2026-05-21 18:05', 0,  'NORMAL'),
(6,  '2026-05-21', '2026-05-21 09:00', '2026-05-21 18:00', 0,  'NORMAL'),
(9,  '2026-05-21', '2026-05-21 08:58', '2026-05-21 18:00', 0,  'NORMAL'),
(11, '2026-05-21', '2026-05-21 08:55', '2026-05-21 18:00', 0,  'NORMAL'),
(17, '2026-05-21', '2026-05-21 08:50', '2026-05-21 18:00', 0,  'NORMAL'),
(5,  '2026-05-22', '2026-05-22 08:55', '2026-05-22 18:05', 0,  'NORMAL'),
(8,  '2026-05-22', '2026-05-22 09:00', '2026-05-22 18:00', 0,  'NORMAL'),
(9,  '2026-05-22', '2026-05-22 09:15', '2026-05-22 18:00', 0,  'ANOMALY'),
(11, '2026-05-22', '2026-05-22 08:50', '2026-05-22 18:00', 0,  'NORMAL'),
(12, '2026-05-22', '2026-05-22 08:55', '2026-05-22 17:35', 0,  'ANOMALY'),
(17, '2026-05-22', '2026-05-22 08:55', '2026-05-22 18:00', 0,  'NORMAL');

INSERT INTO work_anomalies (daily_work_record_id, anomaly_type) VALUES
((SELECT id FROM daily_work_records WHERE user_id=9  AND date='2026-05-18'), 'LATE'),
((SELECT id FROM daily_work_records WHERE user_id=19 AND date='2026-05-18'), 'ABSENT'),
((SELECT id FROM daily_work_records WHERE user_id=7  AND date='2026-05-19'), 'EARLY_LEAVE'),
((SELECT id FROM daily_work_records WHERE user_id=10 AND date='2026-05-20'), 'CLOCK_MISSING'),
((SELECT id FROM daily_work_records WHERE user_id=15 AND date='2026-05-20'), 'ABSENT'),
((SELECT id FROM daily_work_records WHERE user_id=9  AND date='2026-05-22'), 'LATE'),
((SELECT id FROM daily_work_records WHERE user_id=12 AND date='2026-05-22'), 'EARLY_LEAVE');

INSERT INTO overtime_requests (user_id, request_date, expected_start_at, expected_end_at, reason, status, approver_id) VALUES
(9, '2026-05-25', '2026-05-25 18:30', '2026-05-25 20:30', '릴리스 대응',     'PENDING',  NULL),
(7, '2026-05-26', '2026-05-26 18:30', '2026-05-26 21:00', '월말 마감 작업',  'PENDING',  NULL),
(8, '2026-05-18', '2026-05-18 18:00', '2026-05-18 19:30', '장애 대응',       'APPROVED', 5);

INSERT INTO hr_change_requests (change_type, target_user_id, payload, reason, status, requested_by) VALUES
('CREATE', NULL,
 '{"name":"김신입","email":"newhire@onwork.kr","hire_date":"2026-06-01","department_id":2,"position":"사원","role":"EMPLOYEE"}'::jsonb,
 '6월 개발팀 신규 입사 예정', 'PENDING', 3),
('UPDATE', 13,
 '{"position":"과장"}'::jsonb,
 '2026 상반기 진급', 'PENDING', 4);

INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 6,  id, '2026-05-26', '2026-05-27', 2.0, '개인 사정',          'PENDING'
  FROM leave_balances WHERE user_id=6  AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 13, id, '2026-05-25', '2026-05-25', 0.5, '병원 예약 (오전반차)','PENDING'
  FROM leave_balances WHERE user_id=13 AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status)
SELECT 18, id, '2026-05-27', '2026-05-29', 3.0, '가족 여행',          'PENDING'
  FROM leave_balances WHERE user_id=18 AND leave_type_id=1 AND year=2026;

INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 7,  id, '2026-05-15', '2026-05-15', 1.0, '경조사', 'APPROVED', 5, '2026-05-13 14:00'
  FROM leave_balances WHERE user_id=7  AND leave_type_id=1 AND year=2026;
UPDATE leave_balances SET used_days = used_days + 1.0 WHERE user_id=7  AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 17, id, '2026-05-12', '2026-05-13', 2.0, '개인 사정', 'APPROVED', 1, '2026-05-10 11:00'
  FROM leave_balances WHERE user_id=17 AND leave_type_id=1 AND year=2026;
UPDATE leave_balances SET used_days = used_days + 2.0 WHERE user_id=17 AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 19, id, '2026-05-14', '2026-05-14', 1.0, '취소된 일정', 'CANCELLED', 17, '2026-05-12 09:00'
  FROM leave_balances WHERE user_id=19 AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, hold_reason)
SELECT 22, id, '2026-05-30', '2026-06-01', 1.0, '가족 행사', 'ON_HOLD', 17, '월말 마감 시기 — 일정 재조정 필요'
  FROM leave_balances WHERE user_id=22 AND leave_type_id=1 AND year=2026;

INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -1.0, 20.0, 19.0, 5
  FROM leave_requests lr WHERE lr.user_id=7 AND lr.start_date='2026-05-15';
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -2.0, 20.0, 18.0, 1
  FROM leave_requests lr WHERE lr.user_id=17 AND lr.start_date='2026-05-12';
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'USE', -1.0, 20.0, 19.0, 17
  FROM leave_requests lr WHERE lr.user_id=19 AND lr.start_date='2026-05-14';
INSERT INTO leave_histories (leave_balance_id, leave_request_id, change_type, change_days, before_days, after_days, changed_by)
SELECT lr.leave_balance_id, lr.id, 'CANCEL', 1.0, 19.0, 20.0, 19
  FROM leave_requests lr WHERE lr.user_id=19 AND lr.start_date='2026-05-14';

INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT u.id, 'HR_CHANGE_REQUESTED', 'HR', r.id,
       '새 인사 변경 요청('|| r.change_type ||')이 결재 대기 중입니다'
  FROM users u, hr_change_requests r
 WHERE u.role IN ('CEO','VP') AND r.status='PENDING';
INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT la.approver_id, 'LEAVE_REQUESTED', 'LEAVE', lr.id,
       '새 휴가 신청이 결재 대기 중입니다'
  FROM leave_requests lr
  JOIN users u ON u.id = lr.user_id
  JOIN leave_approvers la ON la.department_id = u.department_id
 WHERE lr.status='PENDING';
INSERT INTO notifications (user_id, type, ref_type, ref_id, message, is_read)
SELECT lr.user_id, 'LEAVE_APPROVED', 'LEAVE', lr.id, '휴가 신청이 승인되었습니다', TRUE
  FROM leave_requests lr WHERE lr.status='APPROVED';
INSERT INTO notifications (user_id, type, ref_type, ref_id, message)
SELECT lr.user_id, 'LEAVE_ON_HOLD', 'LEAVE', lr.id,
       '휴가 신청이 보류되었습니다: '||lr.hold_reason
  FROM leave_requests lr WHERE lr.status='ON_HOLD';

INSERT INTO approvals (type, ref_id, requester_id, approver_id, status, department_id)
SELECT 'LEAVE', lr.id, lr.user_id,
       CASE WHEN la.is_absent AND la.delegate_id IS NOT NULL THEN la.delegate_id ELSE la.approver_id END,
       'PENDING', u.department_id
  FROM leave_requests lr
  JOIN users u ON u.id = lr.user_id
  JOIN leave_approvers la ON la.department_id = u.department_id
 WHERE lr.status = 'PENDING'
ON CONFLICT (type, ref_id) DO NOTHING;
INSERT INTO approvals (type, ref_id, requester_id, approver_id, status, department_id)
SELECT 'ATTENDANCE', ot.id, ot.user_id, d.manager_id, 'PENDING', u.department_id
  FROM overtime_requests ot
  JOIN users u ON u.id = ot.user_id
  JOIN departments d ON d.id = u.department_id
 WHERE ot.status = 'PENDING'
ON CONFLICT (type, ref_id) DO NOTHING;
INSERT INTO approvals (type, ref_id, requester_id, approver_id, status, department_id)
SELECT 'HR', h.id, h.requested_by, NULL, 'PENDING', u.department_id
  FROM hr_change_requests h
  JOIN users u ON u.id = h.requested_by
 WHERE h.status = 'PENDING' AND u.department_id IS NOT NULL
ON CONFLICT (type, ref_id) DO NOTHING;

INSERT INTO onboarding_tutorial_progress (user_id, tutorial_code, status, current_step, last_shown_at)
VALUES (5, 'MANAGER_TOUR', 'IN_PROGRESS', 2, '2026-05-22 10:00');

UPDATE hr_change_requests SET created_at = NOW() - INTERVAL '4 days'
 WHERE status='PENDING' AND change_type='CREATE';
UPDATE leave_requests SET created_at = NOW() - INTERVAL '5 days'
 WHERE status='PENDING' AND user_id = 6;
UPDATE overtime_requests SET created_at = NOW() - INTERVAL '3 days'
 WHERE status='PENDING' AND user_id = 9;

-- ============================================================ 3) 0601 추가 (seed_0601.sql)
INSERT INTO schedules (user_id, date, start_time, end_time, title, kind)
SELECT u.id, CURRENT_DATE + t.day_offset, t.start_time, t.end_time, t.title, t.kind
FROM users u
CROSS JOIN (VALUES
    (0, TIME '09:30', TIME '10:00', '팀 데일리 스탠드업', 'MEETING'),
    (0, TIME '15:00', TIME '16:00', '업무 협의', 'MEETING'),
    (1, TIME '11:00', TIME '12:00', '1:1 면담', 'MEETING'),
    (2, TIME '14:00', TIME '15:30', '월간 업무 리뷰', 'MEETING')
) AS t(day_offset, start_time, end_time, title, kind)
WHERE u.status = 'ACTIVE';

INSERT INTO salaries (user_id, base_pay, meal_allowance, transport_allowance, position_allowance, pay_day)
SELECT u.id,
       CASE u.role
            WHEN 'CEO' THEN 8000000 WHEN 'VP' THEN 6500000
            WHEN 'HR_MANAGER' THEN 5000000 WHEN 'MANAGER' THEN 4500000
            ELSE 3200000 END,
       200000, 100000,
       CASE u.role
            WHEN 'CEO' THEN 1000000 WHEN 'VP' THEN 700000
            WHEN 'HR_MANAGER' THEN 300000 WHEN 'MANAGER' THEN 300000
            ELSE 0 END,
       25
FROM users u
WHERE u.status = 'ACTIVE'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO daily_work_records (user_id, date, clock_in_at, clock_out_at, overtime_minutes, status)
SELECT u.id, g.d::date, g.d + TIME '09:00', g.d + TIME '18:00', 0, 'NORMAL'
FROM users u
CROSS JOIN generate_series(DATE '2026-06-01', DATE '2026-06-26', INTERVAL '1 day') AS g(d)
WHERE u.status = 'ACTIVE'
  AND EXTRACT(ISODOW FROM g.d) < 6
ON CONFLICT (user_id, date) DO NOTHING;

UPDATE daily_work_records
SET clock_in_at = DATE '2026-06-10' + TIME '09:14', status = 'ANOMALY'
WHERE date = DATE '2026-06-10'
  AND user_id IN (SELECT id FROM users WHERE status = 'ACTIVE');
INSERT INTO work_anomalies (daily_work_record_id, anomaly_type)
SELECT r.id, 'LATE'
FROM daily_work_records r
WHERE r.date = DATE '2026-06-10'
  AND r.user_id IN (SELECT id FROM users WHERE status = 'ACTIVE')
  AND NOT EXISTS (
        SELECT 1 FROM work_anomalies a WHERE a.daily_work_record_id = r.id AND a.anomaly_type = 'LATE');

UPDATE daily_work_records SET overtime_minutes = 120, clock_out_at = DATE '2026-06-17' + TIME '20:00'
WHERE date = DATE '2026-06-17' AND user_id IN (SELECT id FROM users WHERE status = 'ACTIVE');
UPDATE daily_work_records SET overtime_minutes = 80, clock_out_at = DATE '2026-06-24' + TIME '19:20'
WHERE date = DATE '2026-06-24' AND user_id IN (SELECT id FROM users WHERE status = 'ACTIVE');

-- ============================================================ 4) 시연 시나리오: 개발팀장·기획팀장 오늘 휴가
-- 최현준(5, 개발팀장) / 한소희(17, 기획팀장)을 오늘부터 승인 휴가 → 두 팀 사원의 휴가 신청이
-- 대행자(경영지원팀장 박지수, id 3)에게 라우팅되는 것을 시연.
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 5,  id, CURRENT_DATE, CURRENT_DATE + 2, 3.0, '연차 (시연용 — 팀장 부재)', 'APPROVED', 1, NOW()
  FROM leave_balances WHERE user_id=5  AND leave_type_id=1 AND year=2026;
INSERT INTO leave_requests (user_id, leave_balance_id, start_date, end_date, days_used, reason, status, approver_id, approved_at)
SELECT 17, id, CURRENT_DATE, CURRENT_DATE + 2, 3.0, '연차 (시연용 — 팀장 부재)', 'APPROVED', 1, NOW()
  FROM leave_balances WHERE user_id=17 AND leave_type_id=1 AND year=2026;

UPDATE leave_balances SET used_days = used_days + 3.0 WHERE user_id IN (5, 17) AND leave_type_id=1 AND year=2026;
