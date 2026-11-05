package com.lumi.web.rest;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.OrderStatus;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;

/** Test data shared by the remaining resource integration tests. */
final class CourseOrderTestFixtures {

    private CourseOrderTestFixtures() {}

    private static final BigDecimal DEFAULT_ORIGINAL_PRICE = new BigDecimal(1);

    private static final BigDecimal UPDATED_ORIGINAL_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);

    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_FINAL_PRICE = new BigDecimal(1);

    private static final BigDecimal UPDATED_FINAL_PRICE = new BigDecimal(2);

    private static final String DEFAULT_REVENUE_SPLIT_RATE = "AAAAAAAAAA";

    private static final String UPDATED_REVENUE_SPLIT_RATE = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDING;

    private static final OrderStatus UPDATED_STATUS = OrderStatus.PAID;

    private static final String DEFAULT_IDEMPOTENCY_KEY = "AAAAAAAAAA";

    private static final String UPDATED_IDEMPOTENCY_KEY = "BBBBBBBBBB";

    private static final Long DEFAULT_ROW_VERSION = 1L;

    private static final Long UPDATED_ROW_VERSION = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final BigDecimal DEFAULT_INSTRUCTOR_SHARE_PERCENT = new BigDecimal(0);

    private static final BigDecimal UPDATED_INSTRUCTOR_SHARE_PERCENT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_INSTRUCTOR_AMOUNT = new BigDecimal(0);

    private static final BigDecimal UPDATED_INSTRUCTOR_AMOUNT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PLATFORM_AMOUNT = new BigDecimal(0);

    private static final BigDecimal UPDATED_PLATFORM_AMOUNT = new BigDecimal(1);

    private static final String DEFAULT_ORDER_TYPE = "AAAAAAAAAA";

    private static final String UPDATED_ORDER_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_PAID_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_PAID_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Integer DEFAULT_ACCESS_DURATION_VALUE = 1;

    private static final Integer UPDATED_ACCESS_DURATION_VALUE = 2;

    private static final String DEFAULT_ACCESS_DURATION_UNIT = "AAAAAAAAAA";

    private static final String UPDATED_ACCESS_DURATION_UNIT = "BBBBBBBBBB";

    private static final Instant DEFAULT_ENTITLEMENT_STARTS_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_ENTITLEMENT_STARTS_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_ENTITLEMENT_EXPIRES_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_ENTITLEMENT_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_ENTITLEMENT_STATUS = "AAAAAAAAAA";

    private static final String UPDATED_ENTITLEMENT_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PLAN_SNAPSHOT = "AAAAAAAAAA";

    private static final String UPDATED_PLAN_SNAPSHOT = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_PRICING_SNAPSHOT = "AAAAAAAAAA";

    private static final String UPDATED_PRICING_SNAPSHOT = "BBBBBBBBBB";

    public static CourseOrder createEntity(EntityManager em) {
        CourseOrder courseOrder = new CourseOrder()
            .originalPrice(DEFAULT_ORIGINAL_PRICE)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .finalPrice(DEFAULT_FINAL_PRICE)
            .revenueSplitRate(DEFAULT_REVENUE_SPLIT_RATE)
            .status(DEFAULT_STATUS)
            .idempotencyKey(DEFAULT_IDEMPOTENCY_KEY)
            .rowVersion(DEFAULT_ROW_VERSION)
            .createdAt(DEFAULT_CREATED_AT)
            .instructorSharePercent(DEFAULT_INSTRUCTOR_SHARE_PERCENT)
            .instructorAmount(DEFAULT_INSTRUCTOR_AMOUNT)
            .platformAmount(DEFAULT_PLATFORM_AMOUNT)
            .orderType(DEFAULT_ORDER_TYPE)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .paidAt(DEFAULT_PAID_AT)
            .accessDurationValue(DEFAULT_ACCESS_DURATION_VALUE)
            .accessDurationUnit(DEFAULT_ACCESS_DURATION_UNIT)
            .entitlementStartsAt(DEFAULT_ENTITLEMENT_STARTS_AT)
            .entitlementExpiresAt(DEFAULT_ENTITLEMENT_EXPIRES_AT)
            .entitlementStatus(DEFAULT_ENTITLEMENT_STATUS)
            .planSnapshot(DEFAULT_PLAN_SNAPSHOT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .pricingSnapshot(DEFAULT_PRICING_SNAPSHOT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        courseOrder.setStudent(user);
        return courseOrder;
    }

    public static CourseOrder createUpdatedEntity(EntityManager em) {
        CourseOrder updatedCourseOrder = new CourseOrder()
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .finalPrice(UPDATED_FINAL_PRICE)
            .revenueSplitRate(UPDATED_REVENUE_SPLIT_RATE)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .rowVersion(UPDATED_ROW_VERSION)
            .createdAt(UPDATED_CREATED_AT)
            .instructorSharePercent(UPDATED_INSTRUCTOR_SHARE_PERCENT)
            .instructorAmount(UPDATED_INSTRUCTOR_AMOUNT)
            .platformAmount(UPDATED_PLATFORM_AMOUNT)
            .orderType(UPDATED_ORDER_TYPE)
            .expiresAt(UPDATED_EXPIRES_AT)
            .paidAt(UPDATED_PAID_AT)
            .accessDurationValue(UPDATED_ACCESS_DURATION_VALUE)
            .accessDurationUnit(UPDATED_ACCESS_DURATION_UNIT)
            .entitlementStartsAt(UPDATED_ENTITLEMENT_STARTS_AT)
            .entitlementExpiresAt(UPDATED_ENTITLEMENT_EXPIRES_AT)
            .entitlementStatus(UPDATED_ENTITLEMENT_STATUS)
            .planSnapshot(UPDATED_PLAN_SNAPSHOT)
            .updatedAt(UPDATED_UPDATED_AT)
            .pricingSnapshot(UPDATED_PRICING_SNAPSHOT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourseOrder.setStudent(user);
        return updatedCourseOrder;
    }
}
