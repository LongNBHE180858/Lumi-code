BEGIN;

DO $dashboard_demo$
DECLARE
    v_grade_id bigint;
    v_student_1 bigint;
    v_student_2 bigint;
    v_parent bigint;
    v_instructor_active bigint;
    v_instructor_pending bigint;
    v_user_only bigint;
    v_active_course_1 bigint;
    v_draft_course bigint;
BEGIN
    IF EXISTS (SELECT 1 FROM jhi_user WHERE left(login, 15) = 'dashboard_demo_')
        OR EXISTS (SELECT 1 FROM courses WHERE title LIKE '[Dashboard demo] %')
        OR EXISTS (SELECT 1 FROM support_tickets WHERE subject LIKE '[Dashboard demo] %') THEN
        RAISE EXCEPTION 'Dashboard demo data already exists; no rows were added';
    END IF;

    IF (SELECT count(*) FROM jhi_authority WHERE name IN ('ROLE_STUDENT', 'ROLE_PARENT', 'ROLE_INSTRUCTOR', 'ROLE_USER')) <> 4 THEN
        RAISE EXCEPTION 'Required authorities are missing';
    END IF;

    SELECT id INTO v_grade_id FROM grades WHERE level BETWEEN 1 AND 9 ORDER BY level LIMIT 1;
    IF v_grade_id IS NULL THEN
        RAISE EXCEPTION 'A grade with level 1-9 is required';
    END IF;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_student_1', repeat('x', 60), 'Dashboard', 'Student 1', true, 'vi', 'dashboard_demo', current_timestamp - interval '4 minutes')
    RETURNING id INTO v_student_1;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_student_2', repeat('x', 60), 'Dashboard', 'Student 2', true, 'vi', 'dashboard_demo', current_timestamp - interval '5 minutes')
    RETURNING id INTO v_student_2;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_parent', repeat('x', 60), 'Dashboard', 'Parent', true, 'vi', 'dashboard_demo', current_timestamp - interval '6 minutes')
    RETURNING id INTO v_parent;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_instructor_active', repeat('x', 60), 'Dashboard', 'Instructor Active', true, 'vi', 'dashboard_demo', current_timestamp - interval '7 minutes')
    RETURNING id INTO v_instructor_active;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_instructor_pending', repeat('x', 60), 'Dashboard', 'Instructor Pending', true, 'vi', 'dashboard_demo', current_timestamp - interval '8 minutes')
    RETURNING id INTO v_instructor_pending;

    INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, activated, lang_key, created_by, created_date)
    VALUES (nextval('sequence_generator'), 'dashboard_demo_user_only', repeat('x', 60), 'Dashboard', 'User Only', true, 'vi', 'dashboard_demo', current_timestamp - interval '9 minutes')
    RETURNING id INTO v_user_only;

    INSERT INTO jhi_user_authority (user_id, authority_name)
    VALUES
        (v_student_1, 'ROLE_STUDENT'),
        (v_student_2, 'ROLE_STUDENT'),
        (v_parent, 'ROLE_PARENT'),
        (v_instructor_active, 'ROLE_INSTRUCTOR'),
        (v_instructor_pending, 'ROLE_INSTRUCTOR'),
        (v_user_only, 'ROLE_USER');

    INSERT INTO user_profiles (id, user_id, instructor_status, instructor_reviewed_at, updated_at)
    VALUES
        (nextval('sequence_generator'), v_instructor_active, 'ACTIVE', current_timestamp - interval '1 minute', current_timestamp),
        (nextval('sequence_generator'), v_instructor_pending, 'PENDING', NULL, current_timestamp);

    INSERT INTO courses (id, title, version, status, is_deleted, created_at, updated_at, author_id, grade_id)
    VALUES (nextval('sequence_generator'), '[Dashboard demo] Khóa học đang hiển thị 1', 1, 'ACTIVE', false, current_timestamp - interval '3 minutes', current_timestamp, v_instructor_active, v_grade_id)
    RETURNING id INTO v_active_course_1;

    INSERT INTO courses (id, title, version, status, is_deleted, created_at, updated_at, author_id, grade_id)
    VALUES (nextval('sequence_generator'), '[Dashboard demo] Khóa học đang hiển thị 2', 1, 'ACTIVE', false, current_timestamp - interval '10 minutes', current_timestamp, v_instructor_active, v_grade_id);

    INSERT INTO courses (id, title, version, status, is_deleted, created_at, updated_at, author_id, grade_id)
    VALUES (nextval('sequence_generator'), '[Dashboard demo] Khóa học chờ duyệt', 1, 'DRAFT', false, current_timestamp - interval '11 minutes', current_timestamp, v_instructor_active, v_grade_id)
    RETURNING id INTO v_draft_course;

    INSERT INTO course_review_requests (id, course_version, status, rights_confirmed_at, submitted_at, reviewed_at, course_id, submitted_by_id)
    VALUES
        (nextval('sequence_generator'), 1, 'APPROVED', current_timestamp - interval '3 minutes', current_timestamp - interval '3 minutes', current_timestamp - interval '2 minutes', v_active_course_1, v_instructor_active),
        (nextval('sequence_generator'), 1, 'PENDING', current_timestamp - interval '10 minutes', current_timestamp - interval '10 minutes', NULL, v_draft_course, v_instructor_active);

    INSERT INTO support_tickets (id, type, subject, status, created_at, updated_at, user_id)
    VALUES (nextval('sequence_generator'), 'GENERAL', '[Dashboard demo] Yêu cầu hỗ trợ đang mở', 'OPEN', current_timestamp - interval '12 minutes', current_timestamp - interval '12 minutes', v_student_1);
END;
$dashboard_demo$;

COMMIT;

SELECT u.login, a.authority_name
FROM jhi_user u
JOIN jhi_user_authority a ON a.user_id = u.id
WHERE left(u.login, 15) = 'dashboard_demo_'
ORDER BY u.login;

SELECT c.title, c.status, g.level AS grade_level
FROM courses c
JOIN grades g ON g.id = c.grade_id
WHERE c.title LIKE '[Dashboard demo] %'
ORDER BY c.title;

SELECT
    (SELECT count(*) FROM jhi_user WHERE left(login, 15) = 'dashboard_demo_') AS demo_accounts,
    (SELECT count(*) FROM courses WHERE title LIKE '[Dashboard demo] %' AND status = 'ACTIVE') AS demo_active_courses,
    (SELECT count(*) FROM user_profiles p JOIN jhi_user u ON u.id = p.user_id WHERE u.login = 'dashboard_demo_instructor_pending' AND p.instructor_status = 'PENDING') AS demo_pending_instructor_applications,
    (SELECT count(*) FROM course_review_requests r JOIN courses c ON c.id = r.course_id WHERE c.title LIKE '[Dashboard demo] %' AND r.status = 'PENDING') AS demo_pending_course_reviews,
    (SELECT count(*) FROM support_tickets WHERE subject LIKE '[Dashboard demo] %' AND status = 'OPEN') AS demo_open_support_tickets;
