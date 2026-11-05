package com.lumi.web.rest;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.Payment;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;

/** Test data shared by the remaining resource integration tests. */
final class PaymentTestFixtures {

    private PaymentTestFixtures() {}

    private static final String DEFAULT_GATEWAY = "AAAAAAAAAA";

    private static final String UPDATED_GATEWAY = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSACTION_NO = "AAAAAAAAAA";

    private static final String UPDATED_TRANSACTION_NO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);

    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";

    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_PAID_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_PAID_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_PAYMENT_REFERENCE = "AAAAAAAAAA";

    private static final String UPDATED_PAYMENT_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_PROVIDER_EVENT_ID = "AAAAAAAAAA";

    private static final String UPDATED_PROVIDER_EVENT_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECEIVED_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_RECEIVED_AT = Instant.ofEpochMilli(1791835009362L);

    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .gateway(DEFAULT_GATEWAY)
            .transactionNo(DEFAULT_TRANSACTION_NO)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS)
            .paidAt(DEFAULT_PAID_AT)
            .paymentReference(DEFAULT_PAYMENT_REFERENCE)
            .providerEventId(DEFAULT_PROVIDER_EVENT_ID)
            .receivedAt(DEFAULT_RECEIVED_AT);
        // Add required entity
        CourseOrder courseOrder;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            courseOrder = CourseOrderTestFixtures.createEntity(em);
            em.persist(courseOrder);
            em.flush();
        } else {
            courseOrder = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        payment.setOrder(courseOrder);
        return payment;
    }

    public static Payment createUpdatedEntity(EntityManager em) {
        Payment updatedPayment = new Payment()
            .gateway(UPDATED_GATEWAY)
            .transactionNo(UPDATED_TRANSACTION_NO)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .paidAt(UPDATED_PAID_AT)
            .paymentReference(UPDATED_PAYMENT_REFERENCE)
            .providerEventId(UPDATED_PROVIDER_EVENT_ID)
            .receivedAt(UPDATED_RECEIVED_AT);
        // Add required entity
        CourseOrder courseOrder;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            courseOrder = CourseOrderTestFixtures.createUpdatedEntity(em);
            em.persist(courseOrder);
            em.flush();
        } else {
            courseOrder = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        updatedPayment.setOrder(courseOrder);
        return updatedPayment;
    }
}
