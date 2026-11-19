BEGIN;

DO $finance_demo$
DECLARE
    v_now timestamp := now() AT TIME ZONE 'UTC';
    v_parent_id bigint;
    v_student_id bigint;
    v_instructor_id bigint;
    v_old_course_id bigint := 2050;
    v_refund_course_id bigint := 2100;
    v_old_order_id bigint;
    v_refund_order_id bigint;
    v_old_sale_id bigint;
    v_refund_payment_id bigint;
    v_wallet_id bigint;
    v_payout_id bigint;
    v_refund_id bigint;
BEGIN
    IF EXISTS (SELECT 1 FROM orders WHERE idempotency_key IN ('finance-admin-demo-old-v1', 'finance-admin-demo-refund-v1'))
        OR EXISTS (SELECT 1 FROM payments WHERE transaction_no IN ('FINANCE-DEMO-OLD-V1', 'FINANCE-DEMO-REFUND-V1')) THEN
        RAISE EXCEPTION 'Finance admin demo already exists; no rows were added';
    END IF;

    SELECT u.id INTO v_parent_id
    FROM jhi_user u
    JOIN jhi_user_authority a ON a.user_id = u.id AND a.authority_name = 'ROLE_PARENT'
    WHERE u.login = 'dashboard_demo_parent' AND u.activated = true;

    SELECT u.id INTO v_student_id
    FROM jhi_user u
    JOIN jhi_user_authority a ON a.user_id = u.id AND a.authority_name = 'ROLE_STUDENT'
    WHERE u.login = 'dashboard_demo_student_1' AND u.activated = true;

    SELECT u.id INTO v_instructor_id
    FROM jhi_user u
    JOIN jhi_user_authority a ON a.user_id = u.id AND a.authority_name = 'ROLE_INSTRUCTOR'
    WHERE u.login = 'dashboard_demo_instructor_active' AND u.activated = true;

    IF v_parent_id IS NULL OR v_student_id IS NULL OR v_instructor_id IS NULL THEN
        RAISE EXCEPTION 'Required dashboard demo accounts are missing';
    END IF;

    IF (SELECT count(*) FROM courses WHERE id IN (v_old_course_id, v_refund_course_id) AND author_id = v_instructor_id AND status = 'ACTIVE') <> 2 THEN
        RAISE EXCEPTION 'Required dashboard demo courses are missing or inactive';
    END IF;

    IF EXISTS (SELECT 1 FROM instructor_wallets WHERE instructor_id = v_instructor_id)
        OR EXISTS (SELECT 1 FROM course_enrollments WHERE student_id = v_student_id AND course_id IN (v_old_course_id, v_refund_course_id)) THEN
        RAISE EXCEPTION 'Existing wallet or enrollment needs review before adding finance demo';
    END IF;

    INSERT INTO orders (
        id, original_price, discount_amount, final_price, revenue_split_rate, status,
        idempotency_key, row_version, created_at, instructor_share_percent,
        instructor_amount, platform_amount, order_type, expires_at, paid_at,
        access_duration_value, access_duration_unit, entitlement_starts_at,
        entitlement_expires_at, entitlement_status, updated_at, student_id, course_id, buyer_id
    ) VALUES (
        nextval('sequence_generator'), 100000, 0, 100000, '80/20', 'PAID',
        'finance-admin-demo-old-v1', 0, v_now - interval '8 days', 80,
        80000, 20000, 'COURSE', v_now - interval '8 days' + interval '15 minutes', v_now - interval '8 days',
        180, 'DAY', v_now - interval '8 days',
        v_now + interval '172 days', 'ACTIVE', v_now - interval '8 days', v_student_id, v_old_course_id, v_parent_id
    ) RETURNING id INTO v_old_order_id;

    INSERT INTO orders (
        id, original_price, discount_amount, final_price, revenue_split_rate, status,
        idempotency_key, row_version, created_at, instructor_share_percent,
        instructor_amount, platform_amount, order_type, expires_at, paid_at,
        access_duration_value, access_duration_unit, entitlement_starts_at,
        entitlement_expires_at, entitlement_status, updated_at, student_id, course_id, buyer_id
    ) VALUES (
        nextval('sequence_generator'), 100000, 0, 100000, '80/20', 'PAID',
        'finance-admin-demo-refund-v1', 0, v_now - interval '1 day', 80,
        80000, 20000, 'COURSE', v_now - interval '1 day' + interval '15 minutes', v_now - interval '1 day',
        180, 'DAY', v_now - interval '1 day',
        v_now + interval '179 days', 'ACTIVE', v_now - interval '1 day', v_student_id, v_refund_course_id, v_parent_id
    ) RETURNING id INTO v_refund_order_id;

    INSERT INTO payments (id, gateway, transaction_no, amount, status, paid_at, order_id)
    VALUES (nextval('sequence_generator'), 'TEST', 'FINANCE-DEMO-OLD-V1', 100000, 'SUCCESS', v_now - interval '8 days', v_old_order_id);

    INSERT INTO payments (id, gateway, transaction_no, amount, status, paid_at, order_id)
    VALUES (nextval('sequence_generator'), 'TEST', 'FINANCE-DEMO-REFUND-V1', 100000, 'SUCCESS', v_now - interval '1 day', v_refund_order_id)
    RETURNING id INTO v_refund_payment_id;

    INSERT INTO course_enrollments (id, progress_percent, status, enrolled_at, expires_at, updated_at, course_id, student_id)
    VALUES
        (nextval('sequence_generator'), 0, 'ACTIVE', v_now - interval '8 days', v_now + interval '172 days', v_now - interval '8 days', v_old_course_id, v_student_id),
        (nextval('sequence_generator'), 0, 'ACTIVE', v_now - interval '1 day', v_now + interval '179 days', v_now - interval '1 day', v_refund_course_id, v_student_id);

    INSERT INTO instructor_wallets (id, pending_amount, available_amount, row_version, instructor_id)
    VALUES (nextval('sequence_generator'), 80000, 30000, 0, v_instructor_id)
    RETURNING id INTO v_wallet_id;

    INSERT INTO payout_requests (id, amount, bank_code, bank_account, status, created_at, account_holder_name, updated_at, wallet_id)
    VALUES (nextval('sequence_generator'), 50000, 'TEST', '0000000000', 'PENDING', v_now, 'DEMO INSTRUCTOR', v_now, v_wallet_id)
    RETURNING id INTO v_payout_id;

    INSERT INTO refunds (id, amount, reason, status, created_at, order_id, payment_id)
    VALUES (
        nextval('sequence_generator'), 80000,
        json_build_object(
            'format', 'lumi-refund-details-v1',
            'reason', 'Du lieu thu nghiem duyet hoan tien',
            'bankCode', 'TEST',
            'bankAccount', '0000000000',
            'accountHolderName', 'DEMO PARENT'
        )::text,
        'PENDING', v_now, v_refund_order_id, v_refund_payment_id
    ) RETURNING id INTO v_refund_id;

    INSERT INTO wallet_transactions (
        id, type, amount, status, idempotency_key, occurred_at, balance_bucket, wallet_id, order_id
    ) VALUES (
        nextval('sequence_generator'), 'SALE', 80000, 'RELEASED', 'sale:' || v_old_order_id,
        v_now - interval '8 days', 'PENDING', v_wallet_id, v_old_order_id
    ) RETURNING id INTO v_old_sale_id;

    INSERT INTO wallet_transactions (
        id, type, amount, status, idempotency_key, occurred_at, balance_bucket,
        wallet_id, order_id, payout_request_id
    ) VALUES
        (nextval('sequence_generator'), 'ADJUSTMENT', -80000, 'POSTED', 'release:' || v_old_sale_id || ':pending', v_now - interval '1 hour', 'PENDING', v_wallet_id, v_old_order_id, NULL),
        (nextval('sequence_generator'), 'ADJUSTMENT', 80000, 'POSTED', 'release:' || v_old_sale_id || ':available', v_now - interval '1 hour', 'AVAILABLE', v_wallet_id, v_old_order_id, NULL),
        (nextval('sequence_generator'), 'SALE', 80000, 'PENDING', 'sale:' || v_refund_order_id, v_now - interval '1 day', 'PENDING', v_wallet_id, v_refund_order_id, NULL),
        (nextval('sequence_generator'), 'PAYOUT', -50000, 'RESERVED', 'payout:' || v_payout_id || ':reserve', v_now, 'AVAILABLE', v_wallet_id, NULL, v_payout_id);

    RAISE NOTICE 'Finance demo created: refund id %, payout id %', v_refund_id, v_payout_id;
END;
$finance_demo$;

COMMIT;

SELECT r.id, r.status, r.amount, o.id AS order_id
FROM refunds r
JOIN orders o ON o.id = r.order_id
WHERE o.idempotency_key = 'finance-admin-demo-refund-v1';

SELECT p.id, p.status, p.amount, u.login AS instructor_login
FROM payout_requests p
JOIN instructor_wallets w ON w.id = p.wallet_id
JOIN jhi_user u ON u.id = w.instructor_id
WHERE u.login = 'dashboard_demo_instructor_active';
