package com.lumi.web.rest;

import static com.lumi.domain.PayoutRequestAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.InstructorWallet;
import com.lumi.domain.PayoutRequest;
import com.lumi.repository.PayoutRequestRepository;
import com.lumi.service.dto.PayoutRequestDTO;
import com.lumi.service.mapper.PayoutRequestMapper;
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
 * Integration tests for the {@link PayoutRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PayoutRequestResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(0 - 1);

    private static final String DEFAULT_BANK_CODE = "AAAAAAAAAA";
    private static final String UPDATED_BANK_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_BANK_ACCOUNT = "AAAAAAAAAA";
    private static final String UPDATED_BANK_ACCOUNT = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/payout-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PayoutRequestRepository payoutRequestRepository;

    @Autowired
    private PayoutRequestMapper payoutRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPayoutRequestMockMvc;

    private PayoutRequest payoutRequest;

    private PayoutRequest insertedPayoutRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PayoutRequest createEntity(EntityManager em) {
        PayoutRequest payoutRequest = new PayoutRequest()
            .amount(DEFAULT_AMOUNT)
            .bankCode(DEFAULT_BANK_CODE)
            .bankAccount(DEFAULT_BANK_ACCOUNT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        InstructorWallet instructorWallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            instructorWallet = InstructorWalletResourceIT.createEntity(em);
            em.persist(instructorWallet);
            em.flush();
        } else {
            instructorWallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        payoutRequest.setWallet(instructorWallet);
        return payoutRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PayoutRequest createUpdatedEntity(EntityManager em) {
        PayoutRequest updatedPayoutRequest = new PayoutRequest()
            .amount(UPDATED_AMOUNT)
            .bankCode(UPDATED_BANK_CODE)
            .bankAccount(UPDATED_BANK_ACCOUNT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        InstructorWallet instructorWallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            instructorWallet = InstructorWalletResourceIT.createUpdatedEntity(em);
            em.persist(instructorWallet);
            em.flush();
        } else {
            instructorWallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        updatedPayoutRequest.setWallet(instructorWallet);
        return updatedPayoutRequest;
    }

    @BeforeEach
    void initTest() {
        payoutRequest = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPayoutRequest != null) {
            payoutRequestRepository.delete(insertedPayoutRequest);
            insertedPayoutRequest = null;
        }
    }

    @Test
    @Transactional
    void createPayoutRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);
        var returnedPayoutRequestDTO = om.readValue(
            restPayoutRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PayoutRequestDTO.class
        );

        // Validate the PayoutRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPayoutRequest = payoutRequestMapper.toEntity(returnedPayoutRequestDTO);
        assertPayoutRequestUpdatableFieldsEquals(returnedPayoutRequest, getPersistedPayoutRequest(returnedPayoutRequest));

        insertedPayoutRequest = returnedPayoutRequest;
    }

    @Test
    @Transactional
    void createPayoutRequestWithExistingId() throws Exception {
        // Create the PayoutRequest with an existing ID
        payoutRequest.setId(1L);
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPayoutRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutRequest.setAmount(null);

        // Create the PayoutRequest, which fails.
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        restPayoutRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBankCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutRequest.setBankCode(null);

        // Create the PayoutRequest, which fails.
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        restPayoutRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBankAccountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutRequest.setBankAccount(null);

        // Create the PayoutRequest, which fails.
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        restPayoutRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPayoutRequests() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payoutRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].bankCode").value(hasItem(DEFAULT_BANK_CODE)))
            .andExpect(jsonPath("$.[*].bankAccount").value(hasItem(DEFAULT_BANK_ACCOUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPayoutRequest() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get the payoutRequest
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, payoutRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payoutRequest.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.bankCode").value(DEFAULT_BANK_CODE))
            .andExpect(jsonPath("$.bankAccount").value(DEFAULT_BANK_ACCOUNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getPayoutRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        Long id = payoutRequest.getId();

        defaultPayoutRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPayoutRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPayoutRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount equals to
        defaultPayoutRequestFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount in
        defaultPayoutRequestFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount is not null
        defaultPayoutRequestFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount is greater than or equal to
        defaultPayoutRequestFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount is less than or equal to
        defaultPayoutRequestFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount is less than
        defaultPayoutRequestFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where amount is greater than
        defaultPayoutRequestFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankCode equals to
        defaultPayoutRequestFiltering("bankCode.equals=" + DEFAULT_BANK_CODE, "bankCode.equals=" + UPDATED_BANK_CODE);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankCode in
        defaultPayoutRequestFiltering("bankCode.in=" + DEFAULT_BANK_CODE + "," + UPDATED_BANK_CODE, "bankCode.in=" + UPDATED_BANK_CODE);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankCode is not null
        defaultPayoutRequestFiltering("bankCode.specified=true", "bankCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankCode contains
        defaultPayoutRequestFiltering("bankCode.contains=" + DEFAULT_BANK_CODE, "bankCode.contains=" + UPDATED_BANK_CODE);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankCode does not contain
        defaultPayoutRequestFiltering("bankCode.doesNotContain=" + UPDATED_BANK_CODE, "bankCode.doesNotContain=" + DEFAULT_BANK_CODE);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankAccount equals to
        defaultPayoutRequestFiltering("bankAccount.equals=" + DEFAULT_BANK_ACCOUNT, "bankAccount.equals=" + UPDATED_BANK_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankAccountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankAccount in
        defaultPayoutRequestFiltering(
            "bankAccount.in=" + DEFAULT_BANK_ACCOUNT + "," + UPDATED_BANK_ACCOUNT,
            "bankAccount.in=" + UPDATED_BANK_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankAccountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankAccount is not null
        defaultPayoutRequestFiltering("bankAccount.specified=true", "bankAccount.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankAccountContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankAccount contains
        defaultPayoutRequestFiltering("bankAccount.contains=" + DEFAULT_BANK_ACCOUNT, "bankAccount.contains=" + UPDATED_BANK_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByBankAccountNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where bankAccount does not contain
        defaultPayoutRequestFiltering(
            "bankAccount.doesNotContain=" + UPDATED_BANK_ACCOUNT,
            "bankAccount.doesNotContain=" + DEFAULT_BANK_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where status equals to
        defaultPayoutRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where status in
        defaultPayoutRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where status is not null
        defaultPayoutRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where status contains
        defaultPayoutRequestFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where status does not contain
        defaultPayoutRequestFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where createdAt equals to
        defaultPayoutRequestFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where createdAt in
        defaultPayoutRequestFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        // Get all the payoutRequestList where createdAt is not null
        defaultPayoutRequestFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutRequestsByWalletIsEqualToSomething() throws Exception {
        InstructorWallet wallet;
        if (TestUtil.findAll(em, InstructorWallet.class).isEmpty()) {
            payoutRequestRepository.saveAndFlush(payoutRequest);
            wallet = InstructorWalletResourceIT.createEntity(em);
        } else {
            wallet = TestUtil.findAll(em, InstructorWallet.class).getFirst();
        }
        em.persist(wallet);
        em.flush();
        payoutRequest.setWallet(wallet);
        payoutRequestRepository.saveAndFlush(payoutRequest);
        Long walletId = wallet.getId();
        // Get all the payoutRequestList where wallet equals to walletId
        defaultPayoutRequestShouldBeFound("walletId.equals=" + walletId);

        // Get all the payoutRequestList where wallet equals to (walletId + 1)
        defaultPayoutRequestShouldNotBeFound("walletId.equals=" + (walletId + 1));
    }

    private void defaultPayoutRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPayoutRequestShouldBeFound(shouldBeFound);
        defaultPayoutRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPayoutRequestShouldBeFound(String filter) throws Exception {
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payoutRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].bankCode").value(hasItem(DEFAULT_BANK_CODE)))
            .andExpect(jsonPath("$.[*].bankAccount").value(hasItem(DEFAULT_BANK_ACCOUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPayoutRequestShouldNotBeFound(String filter) throws Exception {
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPayoutRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPayoutRequest() throws Exception {
        // Get the payoutRequest
        restPayoutRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayoutRequest() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutRequest
        PayoutRequest updatedPayoutRequest = payoutRequestRepository.findById(payoutRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPayoutRequest are not directly saved in db
        em.detach(updatedPayoutRequest);
        updatedPayoutRequest
            .amount(UPDATED_AMOUNT)
            .bankCode(UPDATED_BANK_CODE)
            .bankAccount(UPDATED_BANK_ACCOUNT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(updatedPayoutRequest);

        restPayoutRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, payoutRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPayoutRequestToMatchAllProperties(updatedPayoutRequest);
    }

    @Test
    @Transactional
    void putNonExistingPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, payoutRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePayoutRequestWithPatch() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutRequest using partial update
        PayoutRequest partialUpdatedPayoutRequest = new PayoutRequest();
        partialUpdatedPayoutRequest.setId(payoutRequest.getId());

        partialUpdatedPayoutRequest
            .amount(UPDATED_AMOUNT)
            .bankCode(UPDATED_BANK_CODE)
            .bankAccount(UPDATED_BANK_ACCOUNT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restPayoutRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayoutRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayoutRequest))
            )
            .andExpect(status().isOk());

        // Validate the PayoutRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPayoutRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPayoutRequest, payoutRequest),
            getPersistedPayoutRequest(payoutRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdatePayoutRequestWithPatch() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutRequest using partial update
        PayoutRequest partialUpdatedPayoutRequest = new PayoutRequest();
        partialUpdatedPayoutRequest.setId(payoutRequest.getId());

        partialUpdatedPayoutRequest
            .amount(UPDATED_AMOUNT)
            .bankCode(UPDATED_BANK_CODE)
            .bankAccount(UPDATED_BANK_ACCOUNT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restPayoutRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayoutRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayoutRequest))
            )
            .andExpect(status().isOk());

        // Validate the PayoutRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPayoutRequestUpdatableFieldsEquals(partialUpdatedPayoutRequest, getPersistedPayoutRequest(partialUpdatedPayoutRequest));
    }

    @Test
    @Transactional
    void patchNonExistingPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, payoutRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(payoutRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(payoutRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayoutRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutRequest.setId(longCount.incrementAndGet());

        // Create the PayoutRequest
        PayoutRequestDTO payoutRequestDTO = payoutRequestMapper.toDto(payoutRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(payoutRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PayoutRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePayoutRequest() throws Exception {
        // Initialize the database
        insertedPayoutRequest = payoutRequestRepository.saveAndFlush(payoutRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the payoutRequest
        restPayoutRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, payoutRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return payoutRequestRepository.count();
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

    protected PayoutRequest getPersistedPayoutRequest(PayoutRequest payoutRequest) {
        return payoutRequestRepository.findById(payoutRequest.getId()).orElseThrow();
    }

    protected void assertPersistedPayoutRequestToMatchAllProperties(PayoutRequest expectedPayoutRequest) {
        assertPayoutRequestAllPropertiesEquals(expectedPayoutRequest, getPersistedPayoutRequest(expectedPayoutRequest));
    }

    protected void assertPersistedPayoutRequestToMatchUpdatableProperties(PayoutRequest expectedPayoutRequest) {
        assertPayoutRequestAllUpdatablePropertiesEquals(expectedPayoutRequest, getPersistedPayoutRequest(expectedPayoutRequest));
    }
}
