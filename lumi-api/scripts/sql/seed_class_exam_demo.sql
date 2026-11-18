-- Demo data for class 2552. Safe to rerun: the marker bank prevents duplicate inserts.
-- Uses existing dev_instructor (2000) and hieu (1551) accounts.
BEGIN;

DO $$
DECLARE
    v_class_id bigint := 2552;
    v_instructor_id bigint := 2000;
    v_student_id bigint := 1551;
    v_bank_id bigint;
    v_topic_id bigint;
    v_exam_id bigint;
    v_practice_id bigint;
    v_attempt_id bigint;
    v_question_ids bigint[] := ARRAY[]::bigint[];
    v_question_id bigint;
    v_option_id bigint;
    v_question_text text[] := ARRAY[
        'Giải phương trình 2x + 3 = 11. Giá trị của x là bao nhiêu?',
        'Phương trình 3x - 6 = 0 có nghiệm là bao nhiêu?',
        'Với x = 2, giá trị của biểu thức x² + 3x là bao nhiêu?',
        'Giải phương trình 5(x - 1) = 15. Giá trị của x là bao nhiêu?'
    ];
    v_explanations text[] := ARRAY[
        '2x = 8 nên x = 4.',
        '3x = 6 nên x = 2.',
        '2² + 3 × 2 = 4 + 6 = 10.',
        'x - 1 = 3 nên x = 4.'
    ];
    v_options text[][] := ARRAY[
        ARRAY['2', '3', '4', '5'],
        ARRAY['1', '2', '3', '6'],
        ARRAY['8', '10', '12', '14'],
        ARRAY['2', '3', '4', '5']
    ];
    v_correct_indexes integer[] := ARRAY[3, 2, 2, 3];
    v_student_indexes integer[] := ARRAY[3, 2, 1, 3];
    i integer;
    j integer;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM jhi_user WHERE id = v_instructor_id AND login = 'dev_instructor'
    ) OR NOT EXISTS (
        SELECT 1 FROM jhi_user WHERE id = v_student_id AND login = 'hieu'
    ) OR NOT EXISTS (
        SELECT 1 FROM classes WHERE id = v_class_id AND instructor_id = v_instructor_id AND status = 'ACTIVE'
    ) OR NOT EXISTS (
        SELECT 1 FROM class_members
        WHERE study_class_id = v_class_id AND student_id = v_student_id AND status = 'ACTIVE'
    ) THEN
        RAISE EXCEPTION 'Required demo accounts or class membership are missing';
    END IF;

    IF EXISTS (
        SELECT 1 FROM question_banks
        WHERE name = '[DEMO] Toán 9 - Phương trình' AND instructor_id = v_instructor_id AND class_id = v_class_id
    ) THEN
        RAISE NOTICE 'Demo exam data already exists; no rows inserted';
        RETURN;
    END IF;

    v_bank_id := nextval('sequence_generator');
    INSERT INTO question_banks (id, name, visibility, created_at, updated_at, instructor_id, class_id)
    VALUES (v_bank_id, '[DEMO] Toán 9 - Phương trình', 'PRIVATE', now(), now(), v_instructor_id, v_class_id);

    v_topic_id := nextval('sequence_generator');
    INSERT INTO knowledge_nodes (id, title, type)
    VALUES (v_topic_id, '[DEMO] Phương trình và biểu thức Toán 9', 'TOPIC');

    FOR i IN 1..4 LOOP
        v_question_id := nextval('sequence_generator');
        v_question_ids := array_append(v_question_ids, v_question_id);
        INSERT INTO questions (
            id, content, type, difficulty, explanation, is_deleted, source_type,
            visibility, version, created_at, updated_at, bank_id, created_by_id
        ) VALUES (
            v_question_id, v_question_text[i], 'SINGLE_CHOICE', CASE WHEN i <= 2 THEN 1 ELSE 2 END,
            v_explanations[i], false, 'INSTRUCTOR', 'PRIVATE', 1, now(), now(), v_bank_id, v_instructor_id
        );

        INSERT INTO question_knowledge_links (id, weight, question_id, knowledge_node_id)
        VALUES (nextval('sequence_generator'), 1, v_question_id, v_topic_id);

        FOR j IN 1..4 LOOP
            INSERT INTO question_options (id, content, is_correct, order_index, question_id)
            VALUES (nextval('sequence_generator'), v_options[i][j], j = v_correct_indexes[i], j - 1, v_question_id);
        END LOOP;
    END LOOP;

    v_exam_id := nextval('sequence_generator');
    INSERT INTO exams (
        id, title, duration_minutes, pass_score, max_attempts, shuffle_questions, status,
        is_deleted, opens_at, closes_at, result_release_mode, answer_release_mode,
        version, created_at, assessment_type, assessment_config, security_config,
        study_class_id, created_by_id
    ) VALUES (
        v_exam_id, '[DEMO] Kiểm tra phương trình Toán 9', 20, 50, 2, false, 'ACTIVE',
        false, now() - interval '2 days', now() + interval '14 days', 'IMMEDIATE', 'AFTER_SUBMIT',
        1, now(), 'EXAM', '{"matrix":[{"topic":"[DEMO] Phương trình và biểu thức Toán 9","difficulty":1,"count":2},{"topic":"[DEMO] Phương trình và biểu thức Toán 9","difficulty":2,"count":2}]}',
        '{}', v_class_id, v_instructor_id
    );

    v_practice_id := nextval('sequence_generator');
    INSERT INTO exams (
        id, title, duration_minutes, pass_score, max_attempts, shuffle_questions, status,
        is_deleted, opens_at, closes_at, result_release_mode, answer_release_mode,
        version, created_at, assessment_type, assessment_config, security_config,
        study_class_id, created_by_id
    ) VALUES (
        v_practice_id, '[DEMO] Luyện tập phương trình Toán 9', 20, 50, NULL, false, 'ACTIVE',
        false, now() - interval '1 day', now() + interval '14 days', 'IMMEDIATE', 'AFTER_SUBMIT',
        1, now(), 'PRACTICE', '{"matrix":[{"topic":"[DEMO] Phương trình và biểu thức Toán 9","count":4}]}',
        '{}', v_class_id, v_instructor_id
    );

    FOR i IN 1..4 LOOP
        INSERT INTO exam_questions (id, order_index, score, exam_id, question_id)
        VALUES (nextval('sequence_generator'), i - 1, 1, v_exam_id, v_question_ids[i]);
        INSERT INTO exam_questions (id, order_index, score, exam_id, question_id)
        VALUES (nextval('sequence_generator'), i - 1, 1, v_practice_id, v_question_ids[i]);
    END LOOP;

    v_attempt_id := nextval('sequence_generator');
    INSERT INTO exam_attempts (
        id, start_time, end_time, score, status, row_version, deadline_at,
        last_saved_at, submitted_at, monitoring_status, exam_id, student_id
    ) VALUES (
        v_attempt_id, now() - interval '1 day 10 minutes', now() - interval '1 day',
        75, 'SUBMITTED', 0, now() - interval '1 day' + interval '10 minutes',
        now() - interval '1 day', now() - interval '1 day', 'NONE', v_exam_id, v_student_id
    );

    FOR i IN 1..4 LOOP
        SELECT id INTO STRICT v_option_id FROM question_options
        WHERE question_id = v_question_ids[i] AND order_index = v_student_indexes[i] - 1;
        INSERT INTO exam_answers (
            id, is_correct, score, updated_at, row_version, attempt_id, question_id, option_id
        ) VALUES (
            nextval('sequence_generator'), v_student_indexes[i] = v_correct_indexes[i],
            CASE WHEN v_student_indexes[i] = v_correct_indexes[i] THEN 1 ELSE 0 END,
            now() - interval '1 day', 0, v_attempt_id, v_question_ids[i], v_option_id
        );
    END LOOP;

    RAISE NOTICE 'Inserted demo bank %, exam %, practice %, submitted attempt %',
        v_bank_id, v_exam_id, v_practice_id, v_attempt_id;
END $$;

COMMIT;
