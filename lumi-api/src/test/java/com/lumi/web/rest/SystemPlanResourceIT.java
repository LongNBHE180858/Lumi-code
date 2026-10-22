package com.lumi.web.rest;

import static com.lumi.domain.SystemPlanAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.SystemPlan;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.SystemPlanDTO;
import com.lumi.service.mapper.SystemPlanMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SystemPlanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SystemPlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_DURATION_DAYS = 1;
    private static final Integer UPDATED_DURATION_DAYS = 2;
    private static final Integer SMALLER_DURATION_DAYS = 1 - 1;

    private static final Long DEFAULT_STORAGE_QUOTA_MB = 0L;
    private static final Long UPDATED_STORAGE_QUOTA_MB = 1L;
    private static final Long SMALLER_STORAGE_QUOTA_MB = 0L - 1L;

    private static final Integer DEFAULT_AI_GENERATION_LIMIT = 0;
    private static final Integer UPDATED_AI_GENERATION_LIMIT = 1;
    private static final Integer SMALLER_AI_GENERATION_LIMIT = 0 - 1;

    private static final Integer DEFAULT_MAX_CLASSES = 1;
    private static final Integer UPDATED_MAX_CLASSES = 2;
    private static final Integer SMALLER_MAX_CLASSES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/system-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SystemPlanRepository systemPlanRepository;

    @Autowired
    private SystemPlanMapper systemPlanMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemPlanMockMvc;

    private SystemPlan systemPlan;

    private SystemPlan insertedSystemPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemPlan createEntity() {
        return new SystemPlan()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .durationDays(DEFAULT_DURATION_DAYS)
            .storageQuotaMb(DEFAULT_STORAGE_QUOTA_MB)
            .aiGenerationLimit(DEFAULT_AI_GENERATION_LIMIT)
            .maxClasses(DEFAULT_MAX_CLASSES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemPlan createUpdatedEntity() {
        return new SystemPlan()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .storageQuotaMb(UPDATED_STORAGE_QUOTA_MB)
            .aiGenerationLimit(UPDATED_AI_GENERATION_LIMIT)
            .maxClasses(UPDATED_MAX_CLASSES);
    }

    @BeforeEach
    void initTest() {
        systemPlan = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSystemPlan != null) {
            systemPlanRepository.delete(insertedSystemPlan);
            insertedSystemPlan = null;
        }
    }

    @Test
    @Transactional
    void createSystemPlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);
        var returnedSystemPlanDTO = om.readValue(
            restSystemPlanMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SystemPlanDTO.class
        );

        // Validate the SystemPlan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSystemPlan = systemPlanMapper.toEntity(returnedSystemPlanDTO);
        assertSystemPlanUpdatableFieldsEquals(returnedSystemPlan, getPersistedSystemPlan(returnedSystemPlan));

        insertedSystemPlan = returnedSystemPlan;
    }

    @Test
    @Transactional
    void createSystemPlanWithExistingId() throws Exception {
        // Create the SystemPlan with an existing ID
        systemPlan.setId(1L);
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setName(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setPrice(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationDaysIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setDurationDays(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStorageQuotaMbIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setStorageQuotaMb(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAiGenerationLimitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setAiGenerationLimit(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxClassesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        systemPlan.setMaxClasses(null);

        // Create the SystemPlan, which fails.
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        restSystemPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSystemPlans() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].durationDays").value(hasItem(DEFAULT_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].storageQuotaMb").value(hasItem(DEFAULT_STORAGE_QUOTA_MB.intValue())))
            .andExpect(jsonPath("$.[*].aiGenerationLimit").value(hasItem(DEFAULT_AI_GENERATION_LIMIT)))
            .andExpect(jsonPath("$.[*].maxClasses").value(hasItem(DEFAULT_MAX_CLASSES)));
    }

    @Test
    @Transactional
    void getSystemPlan() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get the systemPlan
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL_ID, systemPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(systemPlan.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.durationDays").value(DEFAULT_DURATION_DAYS))
            .andExpect(jsonPath("$.storageQuotaMb").value(DEFAULT_STORAGE_QUOTA_MB.intValue()))
            .andExpect(jsonPath("$.aiGenerationLimit").value(DEFAULT_AI_GENERATION_LIMIT))
            .andExpect(jsonPath("$.maxClasses").value(DEFAULT_MAX_CLASSES));
    }

    @Test
    @Transactional
    void getSystemPlansByIdFiltering() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        Long id = systemPlan.getId();

        defaultSystemPlanFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSystemPlanFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSystemPlanFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSystemPlansByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where name equals to
        defaultSystemPlanFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSystemPlansByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where name in
        defaultSystemPlanFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSystemPlansByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where name is not null
        defaultSystemPlanFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where name contains
        defaultSystemPlanFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSystemPlansByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where name does not contain
        defaultSystemPlanFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price equals to
        defaultSystemPlanFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price in
        defaultSystemPlanFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price is not null
        defaultSystemPlanFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price is greater than or equal to
        defaultSystemPlanFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price is less than or equal to
        defaultSystemPlanFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price is less than
        defaultSystemPlanFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where price is greater than
        defaultSystemPlanFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays equals to
        defaultSystemPlanFiltering("durationDays.equals=" + DEFAULT_DURATION_DAYS, "durationDays.equals=" + UPDATED_DURATION_DAYS);
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays in
        defaultSystemPlanFiltering(
            "durationDays.in=" + DEFAULT_DURATION_DAYS + "," + UPDATED_DURATION_DAYS,
            "durationDays.in=" + UPDATED_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays is not null
        defaultSystemPlanFiltering("durationDays.specified=true", "durationDays.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays is greater than or equal to
        defaultSystemPlanFiltering(
            "durationDays.greaterThanOrEqual=" + DEFAULT_DURATION_DAYS,
            "durationDays.greaterThanOrEqual=" + UPDATED_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays is less than or equal to
        defaultSystemPlanFiltering(
            "durationDays.lessThanOrEqual=" + DEFAULT_DURATION_DAYS,
            "durationDays.lessThanOrEqual=" + SMALLER_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays is less than
        defaultSystemPlanFiltering("durationDays.lessThan=" + UPDATED_DURATION_DAYS, "durationDays.lessThan=" + DEFAULT_DURATION_DAYS);
    }

    @Test
    @Transactional
    void getAllSystemPlansByDurationDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where durationDays is greater than
        defaultSystemPlanFiltering(
            "durationDays.greaterThan=" + SMALLER_DURATION_DAYS,
            "durationDays.greaterThan=" + DEFAULT_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb equals to
        defaultSystemPlanFiltering(
            "storageQuotaMb.equals=" + DEFAULT_STORAGE_QUOTA_MB,
            "storageQuotaMb.equals=" + UPDATED_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb in
        defaultSystemPlanFiltering(
            "storageQuotaMb.in=" + DEFAULT_STORAGE_QUOTA_MB + "," + UPDATED_STORAGE_QUOTA_MB,
            "storageQuotaMb.in=" + UPDATED_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb is not null
        defaultSystemPlanFiltering("storageQuotaMb.specified=true", "storageQuotaMb.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb is greater than or equal to
        defaultSystemPlanFiltering(
            "storageQuotaMb.greaterThanOrEqual=" + DEFAULT_STORAGE_QUOTA_MB,
            "storageQuotaMb.greaterThanOrEqual=" + UPDATED_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb is less than or equal to
        defaultSystemPlanFiltering(
            "storageQuotaMb.lessThanOrEqual=" + DEFAULT_STORAGE_QUOTA_MB,
            "storageQuotaMb.lessThanOrEqual=" + SMALLER_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb is less than
        defaultSystemPlanFiltering(
            "storageQuotaMb.lessThan=" + UPDATED_STORAGE_QUOTA_MB,
            "storageQuotaMb.lessThan=" + DEFAULT_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByStorageQuotaMbIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where storageQuotaMb is greater than
        defaultSystemPlanFiltering(
            "storageQuotaMb.greaterThan=" + SMALLER_STORAGE_QUOTA_MB,
            "storageQuotaMb.greaterThan=" + DEFAULT_STORAGE_QUOTA_MB
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit equals to
        defaultSystemPlanFiltering(
            "aiGenerationLimit.equals=" + DEFAULT_AI_GENERATION_LIMIT,
            "aiGenerationLimit.equals=" + UPDATED_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit in
        defaultSystemPlanFiltering(
            "aiGenerationLimit.in=" + DEFAULT_AI_GENERATION_LIMIT + "," + UPDATED_AI_GENERATION_LIMIT,
            "aiGenerationLimit.in=" + UPDATED_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit is not null
        defaultSystemPlanFiltering("aiGenerationLimit.specified=true", "aiGenerationLimit.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit is greater than or equal to
        defaultSystemPlanFiltering(
            "aiGenerationLimit.greaterThanOrEqual=" + DEFAULT_AI_GENERATION_LIMIT,
            "aiGenerationLimit.greaterThanOrEqual=" + UPDATED_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit is less than or equal to
        defaultSystemPlanFiltering(
            "aiGenerationLimit.lessThanOrEqual=" + DEFAULT_AI_GENERATION_LIMIT,
            "aiGenerationLimit.lessThanOrEqual=" + SMALLER_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit is less than
        defaultSystemPlanFiltering(
            "aiGenerationLimit.lessThan=" + UPDATED_AI_GENERATION_LIMIT,
            "aiGenerationLimit.lessThan=" + DEFAULT_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByAiGenerationLimitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where aiGenerationLimit is greater than
        defaultSystemPlanFiltering(
            "aiGenerationLimit.greaterThan=" + SMALLER_AI_GENERATION_LIMIT,
            "aiGenerationLimit.greaterThan=" + DEFAULT_AI_GENERATION_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses equals to
        defaultSystemPlanFiltering("maxClasses.equals=" + DEFAULT_MAX_CLASSES, "maxClasses.equals=" + UPDATED_MAX_CLASSES);
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses in
        defaultSystemPlanFiltering(
            "maxClasses.in=" + DEFAULT_MAX_CLASSES + "," + UPDATED_MAX_CLASSES,
            "maxClasses.in=" + UPDATED_MAX_CLASSES
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses is not null
        defaultSystemPlanFiltering("maxClasses.specified=true", "maxClasses.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses is greater than or equal to
        defaultSystemPlanFiltering(
            "maxClasses.greaterThanOrEqual=" + DEFAULT_MAX_CLASSES,
            "maxClasses.greaterThanOrEqual=" + UPDATED_MAX_CLASSES
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses is less than or equal to
        defaultSystemPlanFiltering(
            "maxClasses.lessThanOrEqual=" + DEFAULT_MAX_CLASSES,
            "maxClasses.lessThanOrEqual=" + SMALLER_MAX_CLASSES
        );
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses is less than
        defaultSystemPlanFiltering("maxClasses.lessThan=" + UPDATED_MAX_CLASSES, "maxClasses.lessThan=" + DEFAULT_MAX_CLASSES);
    }

    @Test
    @Transactional
    void getAllSystemPlansByMaxClassesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        // Get all the systemPlanList where maxClasses is greater than
        defaultSystemPlanFiltering("maxClasses.greaterThan=" + SMALLER_MAX_CLASSES, "maxClasses.greaterThan=" + DEFAULT_MAX_CLASSES);
    }

    private void defaultSystemPlanFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSystemPlanShouldBeFound(shouldBeFound);
        defaultSystemPlanShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSystemPlanShouldBeFound(String filter) throws Exception {
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].durationDays").value(hasItem(DEFAULT_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].storageQuotaMb").value(hasItem(DEFAULT_STORAGE_QUOTA_MB.intValue())))
            .andExpect(jsonPath("$.[*].aiGenerationLimit").value(hasItem(DEFAULT_AI_GENERATION_LIMIT)))
            .andExpect(jsonPath("$.[*].maxClasses").value(hasItem(DEFAULT_MAX_CLASSES)));

        // Check, that the count call also returns 1
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSystemPlanShouldNotBeFound(String filter) throws Exception {
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSystemPlanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSystemPlan() throws Exception {
        // Get the systemPlan
        restSystemPlanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSystemPlan() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the systemPlan
        SystemPlan updatedSystemPlan = systemPlanRepository.findById(systemPlan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSystemPlan are not directly saved in db
        em.detach(updatedSystemPlan);
        updatedSystemPlan
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .storageQuotaMb(UPDATED_STORAGE_QUOTA_MB)
            .aiGenerationLimit(UPDATED_AI_GENERATION_LIMIT)
            .maxClasses(UPDATED_MAX_CLASSES);
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(updatedSystemPlan);

        restSystemPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemPlanDTO))
            )
            .andExpect(status().isOk());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSystemPlanToMatchAllProperties(updatedSystemPlan);
    }

    @Test
    @Transactional
    void putNonExistingSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSystemPlanWithPatch() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the systemPlan using partial update
        SystemPlan partialUpdatedSystemPlan = new SystemPlan();
        partialUpdatedSystemPlan.setId(systemPlan.getId());

        partialUpdatedSystemPlan
            .durationDays(UPDATED_DURATION_DAYS)
            .storageQuotaMb(UPDATED_STORAGE_QUOTA_MB)
            .aiGenerationLimit(UPDATED_AI_GENERATION_LIMIT)
            .maxClasses(UPDATED_MAX_CLASSES);

        restSystemPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSystemPlan))
            )
            .andExpect(status().isOk());

        // Validate the SystemPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSystemPlanUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSystemPlan, systemPlan),
            getPersistedSystemPlan(systemPlan)
        );
    }

    @Test
    @Transactional
    void fullUpdateSystemPlanWithPatch() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the systemPlan using partial update
        SystemPlan partialUpdatedSystemPlan = new SystemPlan();
        partialUpdatedSystemPlan.setId(systemPlan.getId());

        partialUpdatedSystemPlan
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .storageQuotaMb(UPDATED_STORAGE_QUOTA_MB)
            .aiGenerationLimit(UPDATED_AI_GENERATION_LIMIT)
            .maxClasses(UPDATED_MAX_CLASSES);

        restSystemPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSystemPlan))
            )
            .andExpect(status().isOk());

        // Validate the SystemPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSystemPlanUpdatableFieldsEquals(partialUpdatedSystemPlan, getPersistedSystemPlan(partialUpdatedSystemPlan));
    }

    @Test
    @Transactional
    void patchNonExistingSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, systemPlanDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(systemPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(systemPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSystemPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        systemPlan.setId(longCount.incrementAndGet());

        // Create the SystemPlan
        SystemPlanDTO systemPlanDTO = systemPlanMapper.toDto(systemPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemPlanMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(systemPlanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSystemPlan() throws Exception {
        // Initialize the database
        insertedSystemPlan = systemPlanRepository.saveAndFlush(systemPlan);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the systemPlan
        restSystemPlanMockMvc
            .perform(delete(ENTITY_API_URL_ID, systemPlan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return systemPlanRepository.count();
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

    protected SystemPlan getPersistedSystemPlan(SystemPlan systemPlan) {
        return systemPlanRepository.findById(systemPlan.getId()).orElseThrow();
    }

    protected void assertPersistedSystemPlanToMatchAllProperties(SystemPlan expectedSystemPlan) {
        assertSystemPlanAllPropertiesEquals(expectedSystemPlan, getPersistedSystemPlan(expectedSystemPlan));
    }

    protected void assertPersistedSystemPlanToMatchUpdatableProperties(SystemPlan expectedSystemPlan) {
        assertSystemPlanAllUpdatablePropertiesEquals(expectedSystemPlan, getPersistedSystemPlan(expectedSystemPlan));
    }
}
