package com.lumi.web.rest;

import static com.lumi.domain.WalletTransactionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.CourseOrder;
import com.lumi.domain.InstructorWallet;
import com.lumi.domain.WalletTransaction;
import com.lumi.domain.enumeration.TransactionType;
import com.lumi.repository.WalletTransactionRepository;
import com.lumi.service.dto.WalletTransactionDTO;
import com.lumi.service.mapper.WalletTransactionMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link WalletTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletTransactionResourceIT {

    private static final TransactionType DEFAULT_TYPE = TransactionType.SALE;
    private static final TransactionType UPDATED_TYPE = TransactionType.REFUND;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_IDEMPOTENCY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_IDEMPOTENCY_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/wallet-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWalletTransactionMockMvc;

    private WalletTransaction walletTransaction;

    private WalletTransaction insertedWalletTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WalletTransaction createEntity(EntityManager em) {
        WalletTransaction walletTransaction = new WalletTransaction()
            .type(DEFAULT_TYPE)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS)
            .idempotencyKey(DEFAULT_IDEMPOTENCY_KEY)
            .occurredAt(DEFAULT_OCCURRED_AT);
        // Add required entity
        InstructorWallet instructorWallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            instructorWallet = InstructorWalletResourceIT.createEntity(em);
            em.persist(instructorWallet);
            em.flush();
        } else {
            instructorWallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        walletTransaction.setWallet(instructorWallet);
        return walletTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WalletTransaction createUpdatedEntity(EntityManager em) {
        WalletTransaction updatedWalletTransaction = new WalletTransaction()
            .type(UPDATED_TYPE)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .occurredAt(UPDATED_OCCURRED_AT);
        // Add required entity
        InstructorWallet instructorWallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            instructorWallet = InstructorWalletResourceIT.createUpdatedEntity(em);
            em.persist(instructorWallet);
            em.flush();
        } else {
            instructorWallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        updatedWalletTransaction.setWallet(instructorWallet);
        return updatedWalletTransaction;
    }

    @BeforeEach
    void initTest() {
        walletTransaction = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWalletTransaction != null) {
            walletTransactionRepository.delete(insertedWalletTransaction);
            insertedWalletTransaction = null;
        }
    }

    @Test
    @Transactional
    void createWalletTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);
        var returnedWalletTransactionDTO = om.readValue(
            restWalletTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WalletTransactionDTO.class
        );

        // Validate the WalletTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWalletTransaction = walletTransactionMapper.toEntity(returnedWalletTransactionDTO);
        assertWalletTransactionUpdatableFieldsEquals(returnedWalletTransaction, getPersistedWalletTransaction(returnedWalletTransaction));

        insertedWalletTransaction = returnedWalletTransaction;
    }

    @Test
    @Transactional
    void createWalletTransactionWithExistingId() throws Exception {
        // Create the WalletTransaction with an existing ID
        walletTransaction.setId(1L);
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        walletTransaction.setType(null);

        // Create the WalletTransaction, which fails.
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        restWalletTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        walletTransaction.setAmount(null);

        // Create the WalletTransaction, which fails.
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        restWalletTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        walletTransaction.setStatus(null);

        // Create the WalletTransaction, which fails.
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        restWalletTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWalletTransactions() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));
    }

    @Test
    @Transactional
    void getWalletTransaction() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get the walletTransaction
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, walletTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(walletTransaction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.idempotencyKey").value(DEFAULT_IDEMPOTENCY_KEY))
            .andExpect(jsonPath("$.occurredAt").value(DEFAULT_OCCURRED_AT.toString()));
    }

    @Test
    @Transactional
    void getWalletTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        Long id = walletTransaction.getId();

        defaultWalletTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWalletTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWalletTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where type equals to
        defaultWalletTransactionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where type in
        defaultWalletTransactionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where type is not null
        defaultWalletTransactionFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount equals to
        defaultWalletTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount in
        defaultWalletTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount is not null
        defaultWalletTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount is greater than or equal to
        defaultWalletTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount is less than or equal to
        defaultWalletTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount is less than
        defaultWalletTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where amount is greater than
        defaultWalletTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where status equals to
        defaultWalletTransactionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where status in
        defaultWalletTransactionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where status is not null
        defaultWalletTransactionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where status contains
        defaultWalletTransactionFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where status does not contain
        defaultWalletTransactionFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByIdempotencyKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where idempotencyKey equals to
        defaultWalletTransactionFiltering(
            "idempotencyKey.equals=" + DEFAULT_IDEMPOTENCY_KEY,
            "idempotencyKey.equals=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByIdempotencyKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where idempotencyKey in
        defaultWalletTransactionFiltering(
            "idempotencyKey.in=" + DEFAULT_IDEMPOTENCY_KEY + "," + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.in=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByIdempotencyKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where idempotencyKey is not null
        defaultWalletTransactionFiltering("idempotencyKey.specified=true", "idempotencyKey.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByIdempotencyKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where idempotencyKey contains
        defaultWalletTransactionFiltering(
            "idempotencyKey.contains=" + DEFAULT_IDEMPOTENCY_KEY,
            "idempotencyKey.contains=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByIdempotencyKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where idempotencyKey does not contain
        defaultWalletTransactionFiltering(
            "idempotencyKey.doesNotContain=" + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.doesNotContain=" + DEFAULT_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByOccurredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where occurredAt equals to
        defaultWalletTransactionFiltering("occurredAt.equals=" + DEFAULT_OCCURRED_AT, "occurredAt.equals=" + UPDATED_OCCURRED_AT);
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByOccurredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where occurredAt in
        defaultWalletTransactionFiltering(
            "occurredAt.in=" + DEFAULT_OCCURRED_AT + "," + UPDATED_OCCURRED_AT,
            "occurredAt.in=" + UPDATED_OCCURRED_AT
        );
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByOccurredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        // Get all the walletTransactionList where occurredAt is not null
        defaultWalletTransactionFiltering("occurredAt.specified=true", "occurredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByWalletIsEqualToSomething() throws Exception {
        InstructorWallet wallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            walletTransactionRepository.saveAndFlush(walletTransaction);
            wallet = InstructorWalletResourceIT.createEntity(em);
        } else {
            wallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        em.persist(wallet);
        em.flush();
        walletTransaction.setWallet(wallet);
        walletTransactionRepository.saveAndFlush(walletTransaction);
        Long walletId = wallet.getId();
        // Get all the walletTransactionList where wallet equals to walletId
        defaultWalletTransactionShouldBeFound("walletId.equals=" + walletId);

        // Get all the walletTransactionList where wallet equals to (walletId + 1)
        defaultWalletTransactionShouldNotBeFound("walletId.equals=" + (walletId + 1));
    }

    @Test
    @Transactional
    void getAllWalletTransactionsByOrderIsEqualToSomething() throws Exception {
        CourseOrder order;
        if (TestUtil.findAll(em, CourseOrder.class).isEmpty()) {
            walletTransactionRepository.saveAndFlush(walletTransaction);
            order = CourseOrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, CourseOrder.class).getFirst();
        }
        em.persist(order);
        em.flush();
        walletTransaction.setOrder(order);
        walletTransactionRepository.saveAndFlush(walletTransaction);
        Long orderId = order.getId();
        // Get all the walletTransactionList where order equals to orderId
        defaultWalletTransactionShouldBeFound("orderId.equals=" + orderId);

        // Get all the walletTransactionList where order equals to (orderId + 1)
        defaultWalletTransactionShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    private void defaultWalletTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWalletTransactionShouldBeFound(shouldBeFound);
        defaultWalletTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWalletTransactionShouldBeFound(String filter) throws Exception {
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));

        // Check, that the count call also returns 1
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWalletTransactionShouldNotBeFound(String filter) throws Exception {
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWalletTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWalletTransaction() throws Exception {
        // Get the walletTransaction
        restWalletTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWalletTransaction() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the walletTransaction
        WalletTransaction updatedWalletTransaction = walletTransactionRepository.findById(walletTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWalletTransaction are not directly saved in db
        em.detach(updatedWalletTransaction);
        updatedWalletTransaction
            .type(UPDATED_TYPE)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .occurredAt(UPDATED_OCCURRED_AT);
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(updatedWalletTransaction);

        restWalletTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(walletTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWalletTransactionToMatchAllProperties(updatedWalletTransaction);
    }

    @Test
    @Transactional
    void putNonExistingWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(walletTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(walletTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWalletTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the walletTransaction using partial update
        WalletTransaction partialUpdatedWalletTransaction = new WalletTransaction();
        partialUpdatedWalletTransaction.setId(walletTransaction.getId());

        partialUpdatedWalletTransaction.type(UPDATED_TYPE).amount(UPDATED_AMOUNT).idempotencyKey(UPDATED_IDEMPOTENCY_KEY);

        restWalletTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWalletTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWalletTransaction))
            )
            .andExpect(status().isOk());

        // Validate the WalletTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWalletTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWalletTransaction, walletTransaction),
            getPersistedWalletTransaction(walletTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateWalletTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the walletTransaction using partial update
        WalletTransaction partialUpdatedWalletTransaction = new WalletTransaction();
        partialUpdatedWalletTransaction.setId(walletTransaction.getId());

        partialUpdatedWalletTransaction
            .type(UPDATED_TYPE)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .occurredAt(UPDATED_OCCURRED_AT);

        restWalletTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWalletTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWalletTransaction))
            )
            .andExpect(status().isOk());

        // Validate the WalletTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWalletTransactionUpdatableFieldsEquals(
            partialUpdatedWalletTransaction,
            getPersistedWalletTransaction(partialUpdatedWalletTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, walletTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(walletTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(walletTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWalletTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        walletTransaction.setId(longCount.incrementAndGet());

        // Create the WalletTransaction
        WalletTransactionDTO walletTransactionDTO = walletTransactionMapper.toDto(walletTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(walletTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WalletTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWalletTransaction() throws Exception {
        // Initialize the database
        insertedWalletTransaction = walletTransactionRepository.saveAndFlush(walletTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the walletTransaction
        restWalletTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, walletTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return walletTransactionRepository.count();
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

    protected WalletTransaction getPersistedWalletTransaction(WalletTransaction walletTransaction) {
        return walletTransactionRepository.findById(walletTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedWalletTransactionToMatchAllProperties(WalletTransaction expectedWalletTransaction) {
        assertWalletTransactionAllPropertiesEquals(expectedWalletTransaction, getPersistedWalletTransaction(expectedWalletTransaction));
    }

    protected void assertPersistedWalletTransactionToMatchUpdatableProperties(WalletTransaction expectedWalletTransaction) {
        assertWalletTransactionAllUpdatablePropertiesEquals(
            expectedWalletTransaction,
            getPersistedWalletTransaction(expectedWalletTransaction)
        );
    }
}
