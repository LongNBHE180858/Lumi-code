package com.lumi.web.rest;

import static com.lumi.domain.RefundAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.CourseOrder;
import com.lumi.domain.Payment;
import com.lumi.domain.Refund;
import com.lumi.domain.User;
import com.lumi.repository.RefundRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.RefundService;
import com.lumi.service.dto.RefundDTO;
import com.lumi.service.mapper.RefundMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link RefundResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RefundResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_PROCESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROCESSED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_REVIEW_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_REFUND_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFUND_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_ACCESS_REVOKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACCESS_REVOKED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/refunds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RefundRepository refundRepositoryMock;

    @Autowired
    private RefundMapper refundMapper;

    @Mock
    private RefundService refundServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRefundMockMvc;

    private Refund refund;

    private Refund insertedRefund;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createEntity(EntityManager em) {
        Refund refund = new Refund()
            .amount(DEFAULT_AMOUNT)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .processedAt(DEFAULT_PROCESSED_AT)
            .reviewNote(DEFAULT_REVIEW_NOTE)
            .refundReference(DEFAULT_REFUND_REFERENCE)
            .accessRevokedAt(DEFAULT_ACCESS_REVOKED_AT);
        // Add required entity
        CourseOrder courseOrder;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            courseOrder = CourseOrderTestFixtures.createEntity(em);
            em.persist(courseOrder);
            em.flush();
        } else {
            courseOrder = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        refund.setOrder(courseOrder);
        // Add required entity
        Payment payment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            payment = PaymentTestFixtures.createEntity(em);
            em.persist(payment);
            em.flush();
        } else {
            payment = TestUtil.findAll(em, Payment.class).getFirst();
        }
        refund.setPayment(payment);
        return refund;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createUpdatedEntity(EntityManager em) {
        Refund updatedRefund = new Refund()
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .processedAt(UPDATED_PROCESSED_AT)
            .reviewNote(UPDATED_REVIEW_NOTE)
            .refundReference(UPDATED_REFUND_REFERENCE)
            .accessRevokedAt(UPDATED_ACCESS_REVOKED_AT);
        // Add required entity
        CourseOrder courseOrder;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            courseOrder = CourseOrderTestFixtures.createUpdatedEntity(em);
            em.persist(courseOrder);
            em.flush();
        } else {
            courseOrder = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        updatedRefund.setOrder(courseOrder);
        // Add required entity
        Payment payment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            payment = PaymentTestFixtures.createUpdatedEntity(em);
            em.persist(payment);
            em.flush();
        } else {
            payment = TestUtil.findAll(em, Payment.class).getFirst();
        }
        updatedRefund.setPayment(payment);
        return updatedRefund;
    }

    @BeforeEach
    void initTest() {
        refund = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRefund != null) {
            refundRepository.delete(insertedRefund);
            insertedRefund = null;
        }
    }

    @Test
    @Transactional
    void createRefund() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);
        var returnedRefundDTO = om.readValue(
            restRefundMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RefundDTO.class
        );

        // Validate the Refund in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRefund = refundMapper.toEntity(returnedRefundDTO);
        assertRefundUpdatableFieldsEquals(returnedRefund, getPersistedRefund(returnedRefund));

        insertedRefund = returnedRefund;
    }

    @Test
    @Transactional
    void createRefundWithExistingId() throws Exception {
        // Create the Refund with an existing ID
        refund.setId(1L);
        RefundDTO refundDTO = refundMapper.toDto(refund);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setAmount(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRefunds() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].processedAt").value(hasItem(DEFAULT_PROCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewNote").value(hasItem(DEFAULT_REVIEW_NOTE)))
            .andExpect(jsonPath("$.[*].refundReference").value(hasItem(DEFAULT_REFUND_REFERENCE)))
            .andExpect(jsonPath("$.[*].accessRevokedAt").value(hasItem(DEFAULT_ACCESS_REVOKED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRefundsWithEagerRelationshipsIsEnabled() throws Exception {
        when(refundServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRefundMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(refundServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRefundsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(refundServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRefundMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(refundRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get the refund
        restRefundMockMvc
            .perform(get(ENTITY_API_URL_ID, refund.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(refund.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.processedAt").value(DEFAULT_PROCESSED_AT.toString()))
            .andExpect(jsonPath("$.reviewNote").value(DEFAULT_REVIEW_NOTE))
            .andExpect(jsonPath("$.refundReference").value(DEFAULT_REFUND_REFERENCE))
            .andExpect(jsonPath("$.accessRevokedAt").value(DEFAULT_ACCESS_REVOKED_AT.toString()));
    }

    @Test
    @Transactional
    void getRefundsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        Long id = refund.getId();

        defaultRefundFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRefundFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRefundFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount equals to
        defaultRefundFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount in
        defaultRefundFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is not null
        defaultRefundFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than or equal to
        defaultRefundFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than or equal to
        defaultRefundFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than
        defaultRefundFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than
        defaultRefundFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status equals to
        defaultRefundFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status in
        defaultRefundFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status is not null
        defaultRefundFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status contains
        defaultRefundFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status does not contain
        defaultRefundFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt equals to
        defaultRefundFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt in
        defaultRefundFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt is not null
        defaultRefundFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByProcessedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where processedAt equals to
        defaultRefundFiltering("processedAt.equals=" + DEFAULT_PROCESSED_AT, "processedAt.equals=" + UPDATED_PROCESSED_AT);
    }

    @Test
    @Transactional
    void getAllRefundsByProcessedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where processedAt in
        defaultRefundFiltering(
            "processedAt.in=" + DEFAULT_PROCESSED_AT + "," + UPDATED_PROCESSED_AT,
            "processedAt.in=" + UPDATED_PROCESSED_AT
        );
    }

    @Test
    @Transactional
    void getAllRefundsByProcessedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where processedAt is not null
        defaultRefundFiltering("processedAt.specified=true", "processedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByRefundReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundReference equals to
        defaultRefundFiltering("refundReference.equals=" + DEFAULT_REFUND_REFERENCE, "refundReference.equals=" + UPDATED_REFUND_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByRefundReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundReference in
        defaultRefundFiltering(
            "refundReference.in=" + DEFAULT_REFUND_REFERENCE + "," + UPDATED_REFUND_REFERENCE,
            "refundReference.in=" + UPDATED_REFUND_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllRefundsByRefundReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundReference is not null
        defaultRefundFiltering("refundReference.specified=true", "refundReference.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByRefundReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundReference contains
        defaultRefundFiltering(
            "refundReference.contains=" + DEFAULT_REFUND_REFERENCE,
            "refundReference.contains=" + UPDATED_REFUND_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllRefundsByRefundReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundReference does not contain
        defaultRefundFiltering(
            "refundReference.doesNotContain=" + UPDATED_REFUND_REFERENCE,
            "refundReference.doesNotContain=" + DEFAULT_REFUND_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllRefundsByAccessRevokedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where accessRevokedAt equals to
        defaultRefundFiltering(
            "accessRevokedAt.equals=" + DEFAULT_ACCESS_REVOKED_AT,
            "accessRevokedAt.equals=" + UPDATED_ACCESS_REVOKED_AT
        );
    }

    @Test
    @Transactional
    void getAllRefundsByAccessRevokedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where accessRevokedAt in
        defaultRefundFiltering(
            "accessRevokedAt.in=" + DEFAULT_ACCESS_REVOKED_AT + "," + UPDATED_ACCESS_REVOKED_AT,
            "accessRevokedAt.in=" + UPDATED_ACCESS_REVOKED_AT
        );
    }

    @Test
    @Transactional
    void getAllRefundsByAccessRevokedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where accessRevokedAt is not null
        defaultRefundFiltering("accessRevokedAt.specified=true", "accessRevokedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByOrderIsEqualToSomething() throws Exception {
        CourseOrder order;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            order = CourseOrderTestFixtures.createEntity(em);
        } else {
            order = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        em.persist(order);
        em.flush();
        refund.setOrder(order);
        refundRepository.saveAndFlush(refund);
        Long orderId = order.getId();
        // Get all the refundList where order equals to orderId
        defaultRefundShouldBeFound("orderId.equals=" + orderId);

        // Get all the refundList where order equals to (orderId + 1)
        defaultRefundShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllRefundsByPaymentIsEqualToSomething() throws Exception {
        Payment payment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            payment = PaymentTestFixtures.createEntity(em);
        } else {
            payment = TestUtil.findAll(em, Payment.class).getFirst();
        }
        em.persist(payment);
        em.flush();
        refund.setPayment(payment);
        refundRepository.saveAndFlush(refund);
        Long paymentId = payment.getId();
        // Get all the refundList where payment equals to paymentId
        defaultRefundShouldBeFound("paymentId.equals=" + paymentId);

        // Get all the refundList where payment equals to (paymentId + 1)
        defaultRefundShouldNotBeFound("paymentId.equals=" + (paymentId + 1));
    }

    @Test
    @Transactional
    void getAllRefundsByProcessedByIsEqualToSomething() throws Exception {
        User processedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            processedBy = UserResourceIT.createEntity();
        } else {
            processedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(processedBy);
        em.flush();
        refund.setProcessedBy(processedBy);
        refundRepository.saveAndFlush(refund);
        Long processedById = processedBy.getId();
        // Get all the refundList where processedBy equals to processedById
        defaultRefundShouldBeFound("processedById.equals=" + processedById);

        // Get all the refundList where processedBy equals to (processedById + 1)
        defaultRefundShouldNotBeFound("processedById.equals=" + (processedById + 1));
    }

    private void defaultRefundFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRefundShouldBeFound(shouldBeFound);
        defaultRefundShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRefundShouldBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].processedAt").value(hasItem(DEFAULT_PROCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewNote").value(hasItem(DEFAULT_REVIEW_NOTE)))
            .andExpect(jsonPath("$.[*].refundReference").value(hasItem(DEFAULT_REFUND_REFERENCE)))
            .andExpect(jsonPath("$.[*].accessRevokedAt").value(hasItem(DEFAULT_ACCESS_REVOKED_AT.toString())));

        // Check, that the count call also returns 1
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRefundShouldNotBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRefund() throws Exception {
        // Get the refund
        restRefundMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund
        Refund updatedRefund = refundRepository.findById(refund.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRefund are not directly saved in db
        em.detach(updatedRefund);
        updatedRefund
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .processedAt(UPDATED_PROCESSED_AT)
            .reviewNote(UPDATED_REVIEW_NOTE)
            .refundReference(UPDATED_REFUND_REFERENCE)
            .accessRevokedAt(UPDATED_ACCESS_REVOKED_AT);
        RefundDTO refundDTO = refundMapper.toDto(updatedRefund);

        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRefundToMatchAllProperties(updatedRefund);
    }

    @Test
    @Transactional
    void putNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund
            .reason(UPDATED_REASON)
            .reviewNote(UPDATED_REVIEW_NOTE)
            .refundReference(UPDATED_REFUND_REFERENCE)
            .accessRevokedAt(UPDATED_ACCESS_REVOKED_AT);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRefund, refund), getPersistedRefund(refund));
    }

    @Test
    @Transactional
    void fullUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .processedAt(UPDATED_PROCESSED_AT)
            .reviewNote(UPDATED_REVIEW_NOTE)
            .refundReference(UPDATED_REFUND_REFERENCE)
            .accessRevokedAt(UPDATED_ACCESS_REVOKED_AT);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(partialUpdatedRefund, getPersistedRefund(partialUpdatedRefund));
    }

    @Test
    @Transactional
    void patchNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, refundDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the refund
        restRefundMockMvc
            .perform(delete(ENTITY_API_URL_ID, refund.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return refundRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Refund getPersistedRefund(Refund refund) {
        return refundRepository.findById(refund.getId()).orElseThrow();
    }

    protected void assertPersistedRefundToMatchAllProperties(Refund expectedRefund) {
        assertRefundAllPropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }

    protected void assertPersistedRefundToMatchUpdatableProperties(Refund expectedRefund) {
        assertRefundAllUpdatablePropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }
}
