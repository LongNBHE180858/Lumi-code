package com.lumi.web.rest;

import static com.lumi.domain.TeacherQuotaAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.SystemPlan;
import com.lumi.domain.TeacherQuota;
import com.lumi.domain.User;
import com.lumi.repository.TeacherQuotaRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.TeacherQuotaService;
import com.lumi.service.dto.TeacherQuotaDTO;
import com.lumi.service.mapper.TeacherQuotaMapper;
import jakarta.persistence.EntityManager;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link TeacherQuotaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeacherQuotaResourceIT {

    private static final Instant DEFAULT_PLAN_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLAN_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Long DEFAULT_USED_STORAGE_BYTES = 0L;
    private static final Long UPDATED_USED_STORAGE_BYTES = 1L;
    private static final Long SMALLER_USED_STORAGE_BYTES = 0L - 1L;

    private static final Integer DEFAULT_AI_USED_COUNT = 0;
    private static final Integer UPDATED_AI_USED_COUNT = 1;
    private static final Integer SMALLER_AI_USED_COUNT = 0 - 1;

    private static final Long DEFAULT_ROW_VERSION = 0L;
    private static final Long UPDATED_ROW_VERSION = 1L;
    private static final Long SMALLER_ROW_VERSION = 0L - 1L;

    private static final String ENTITY_API_URL = "/api/teacher-quotas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeacherQuotaRepository teacherQuotaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TeacherQuotaRepository teacherQuotaRepositoryMock;

    @Autowired
    private TeacherQuotaMapper teacherQuotaMapper;

    @Mock
    private TeacherQuotaService teacherQuotaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeacherQuotaMockMvc;

    private TeacherQuota teacherQuota;

    private TeacherQuota insertedTeacherQuota;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherQuota createEntity(EntityManager em) {
        TeacherQuota teacherQuota = new TeacherQuota()
            .planExpiresAt(DEFAULT_PLAN_EXPIRES_AT)
            .usedStorageBytes(DEFAULT_USED_STORAGE_BYTES)
            .aiUsedCount(DEFAULT_AI_USED_COUNT)
            .rowVersion(DEFAULT_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        teacherQuota.setUser(user);
        return teacherQuota;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherQuota createUpdatedEntity(EntityManager em) {
        TeacherQuota updatedTeacherQuota = new TeacherQuota()
            .planExpiresAt(UPDATED_PLAN_EXPIRES_AT)
            .usedStorageBytes(UPDATED_USED_STORAGE_BYTES)
            .aiUsedCount(UPDATED_AI_USED_COUNT)
            .rowVersion(UPDATED_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTeacherQuota.setUser(user);
        return updatedTeacherQuota;
    }

    @BeforeEach
    void initTest() {
        teacherQuota = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTeacherQuota != null) {
            teacherQuotaRepository.delete(insertedTeacherQuota);
            insertedTeacherQuota = null;
        }
    }

    @Test
    @Transactional
    void createTeacherQuota() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);
        var returnedTeacherQuotaDTO = om.readValue(
            restTeacherQuotaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherQuotaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeacherQuotaDTO.class
        );

        // Validate the TeacherQuota in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeacherQuota = teacherQuotaMapper.toEntity(returnedTeacherQuotaDTO);
        assertTeacherQuotaUpdatableFieldsEquals(returnedTeacherQuota, getPersistedTeacherQuota(returnedTeacherQuota));

        insertedTeacherQuota = returnedTeacherQuota;
    }

    @Test
    @Transactional
    void createTeacherQuotaWithExistingId() throws Exception {
        // Create the TeacherQuota with an existing ID
        teacherQuota.setId(1L);
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeacherQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherQuotaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeacherQuotas() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherQuota.getId().intValue())))
            .andExpect(jsonPath("$.[*].planExpiresAt").value(hasItem(DEFAULT_PLAN_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].usedStorageBytes").value(hasItem(DEFAULT_USED_STORAGE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].aiUsedCount").value(hasItem(DEFAULT_AI_USED_COUNT)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeacherQuotasWithEagerRelationshipsIsEnabled() throws Exception {
        when(teacherQuotaServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restTeacherQuotaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teacherQuotaServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeacherQuotasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teacherQuotaServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restTeacherQuotaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teacherQuotaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeacherQuota() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get the teacherQuota
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL_ID, teacherQuota.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teacherQuota.getId().intValue()))
            .andExpect(jsonPath("$.planExpiresAt").value(DEFAULT_PLAN_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.usedStorageBytes").value(DEFAULT_USED_STORAGE_BYTES.intValue()))
            .andExpect(jsonPath("$.aiUsedCount").value(DEFAULT_AI_USED_COUNT))
            .andExpect(jsonPath("$.rowVersion").value(DEFAULT_ROW_VERSION.intValue()));
    }

    @Test
    @Transactional
    void getTeacherQuotasByIdFiltering() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        Long id = teacherQuota.getId();

        defaultTeacherQuotaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTeacherQuotaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTeacherQuotaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByPlanExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where planExpiresAt equals to
        defaultTeacherQuotaFiltering("planExpiresAt.equals=" + DEFAULT_PLAN_EXPIRES_AT, "planExpiresAt.equals=" + UPDATED_PLAN_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByPlanExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where planExpiresAt in
        defaultTeacherQuotaFiltering(
            "planExpiresAt.in=" + DEFAULT_PLAN_EXPIRES_AT + "," + UPDATED_PLAN_EXPIRES_AT,
            "planExpiresAt.in=" + UPDATED_PLAN_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByPlanExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where planExpiresAt is not null
        defaultTeacherQuotaFiltering("planExpiresAt.specified=true", "planExpiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes equals to
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.equals=" + DEFAULT_USED_STORAGE_BYTES,
            "usedStorageBytes.equals=" + UPDATED_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes in
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.in=" + DEFAULT_USED_STORAGE_BYTES + "," + UPDATED_USED_STORAGE_BYTES,
            "usedStorageBytes.in=" + UPDATED_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes is not null
        defaultTeacherQuotaFiltering("usedStorageBytes.specified=true", "usedStorageBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes is greater than or equal to
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.greaterThanOrEqual=" + DEFAULT_USED_STORAGE_BYTES,
            "usedStorageBytes.greaterThanOrEqual=" + UPDATED_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes is less than or equal to
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.lessThanOrEqual=" + DEFAULT_USED_STORAGE_BYTES,
            "usedStorageBytes.lessThanOrEqual=" + SMALLER_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes is less than
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.lessThan=" + UPDATED_USED_STORAGE_BYTES,
            "usedStorageBytes.lessThan=" + DEFAULT_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUsedStorageBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where usedStorageBytes is greater than
        defaultTeacherQuotaFiltering(
            "usedStorageBytes.greaterThan=" + SMALLER_USED_STORAGE_BYTES,
            "usedStorageBytes.greaterThan=" + DEFAULT_USED_STORAGE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount equals to
        defaultTeacherQuotaFiltering("aiUsedCount.equals=" + DEFAULT_AI_USED_COUNT, "aiUsedCount.equals=" + UPDATED_AI_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount in
        defaultTeacherQuotaFiltering(
            "aiUsedCount.in=" + DEFAULT_AI_USED_COUNT + "," + UPDATED_AI_USED_COUNT,
            "aiUsedCount.in=" + UPDATED_AI_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount is not null
        defaultTeacherQuotaFiltering("aiUsedCount.specified=true", "aiUsedCount.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount is greater than or equal to
        defaultTeacherQuotaFiltering(
            "aiUsedCount.greaterThanOrEqual=" + DEFAULT_AI_USED_COUNT,
            "aiUsedCount.greaterThanOrEqual=" + UPDATED_AI_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount is less than or equal to
        defaultTeacherQuotaFiltering(
            "aiUsedCount.lessThanOrEqual=" + DEFAULT_AI_USED_COUNT,
            "aiUsedCount.lessThanOrEqual=" + SMALLER_AI_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount is less than
        defaultTeacherQuotaFiltering("aiUsedCount.lessThan=" + UPDATED_AI_USED_COUNT, "aiUsedCount.lessThan=" + DEFAULT_AI_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByAiUsedCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where aiUsedCount is greater than
        defaultTeacherQuotaFiltering(
            "aiUsedCount.greaterThan=" + SMALLER_AI_USED_COUNT,
            "aiUsedCount.greaterThan=" + DEFAULT_AI_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion equals to
        defaultTeacherQuotaFiltering("rowVersion.equals=" + DEFAULT_ROW_VERSION, "rowVersion.equals=" + UPDATED_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion in
        defaultTeacherQuotaFiltering(
            "rowVersion.in=" + DEFAULT_ROW_VERSION + "," + UPDATED_ROW_VERSION,
            "rowVersion.in=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion is not null
        defaultTeacherQuotaFiltering("rowVersion.specified=true", "rowVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion is greater than or equal to
        defaultTeacherQuotaFiltering(
            "rowVersion.greaterThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.greaterThanOrEqual=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion is less than or equal to
        defaultTeacherQuotaFiltering(
            "rowVersion.lessThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.lessThanOrEqual=" + SMALLER_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion is less than
        defaultTeacherQuotaFiltering("rowVersion.lessThan=" + UPDATED_ROW_VERSION, "rowVersion.lessThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByRowVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        // Get all the teacherQuotaList where rowVersion is greater than
        defaultTeacherQuotaFiltering("rowVersion.greaterThan=" + SMALLER_ROW_VERSION, "rowVersion.greaterThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = teacherQuota.getUser();
        teacherQuotaRepository.saveAndFlush(teacherQuota);
        Long userId = user.getId();
        // Get all the teacherQuotaList where user equals to userId
        defaultTeacherQuotaShouldBeFound("userId.equals=" + userId);

        // Get all the teacherQuotaList where user equals to (userId + 1)
        defaultTeacherQuotaShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllTeacherQuotasByCurrentPlanIsEqualToSomething() throws Exception {
        SystemPlan currentPlan;
        if (TestUtil.findAll(em, SystemPlan.class).isEmpty()) {
            teacherQuotaRepository.saveAndFlush(teacherQuota);
            currentPlan = SystemPlanResourceIT.createEntity();
        } else {
            currentPlan = TestUtil.findAll(em, SystemPlan.class).getFirst();
        }
        em.persist(currentPlan);
        em.flush();
        teacherQuota.setCurrentPlan(currentPlan);
        teacherQuotaRepository.saveAndFlush(teacherQuota);
        Long currentPlanId = currentPlan.getId();
        // Get all the teacherQuotaList where currentPlan equals to currentPlanId
        defaultTeacherQuotaShouldBeFound("currentPlanId.equals=" + currentPlanId);

        // Get all the teacherQuotaList where currentPlan equals to (currentPlanId + 1)
        defaultTeacherQuotaShouldNotBeFound("currentPlanId.equals=" + (currentPlanId + 1));
    }

    private void defaultTeacherQuotaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTeacherQuotaShouldBeFound(shouldBeFound);
        defaultTeacherQuotaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeacherQuotaShouldBeFound(String filter) throws Exception {
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherQuota.getId().intValue())))
            .andExpect(jsonPath("$.[*].planExpiresAt").value(hasItem(DEFAULT_PLAN_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].usedStorageBytes").value(hasItem(DEFAULT_USED_STORAGE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].aiUsedCount").value(hasItem(DEFAULT_AI_USED_COUNT)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));

        // Check, that the count call also returns 1
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeacherQuotaShouldNotBeFound(String filter) throws Exception {
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeacherQuotaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeacherQuota() throws Exception {
        // Get the teacherQuota
        restTeacherQuotaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeacherQuota() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherQuota
        TeacherQuota updatedTeacherQuota = teacherQuotaRepository.findById(teacherQuota.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeacherQuota are not directly saved in db
        em.detach(updatedTeacherQuota);
        updatedTeacherQuota
            .planExpiresAt(UPDATED_PLAN_EXPIRES_AT)
            .usedStorageBytes(UPDATED_USED_STORAGE_BYTES)
            .aiUsedCount(UPDATED_AI_USED_COUNT)
            .rowVersion(UPDATED_ROW_VERSION);
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(updatedTeacherQuota);

        restTeacherQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherQuotaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherQuotaDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeacherQuotaToMatchAllProperties(updatedTeacherQuota);
    }

    @Test
    @Transactional
    void putNonExistingTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherQuotaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherQuotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherQuotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherQuotaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeacherQuotaWithPatch() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherQuota using partial update
        TeacherQuota partialUpdatedTeacherQuota = new TeacherQuota();
        partialUpdatedTeacherQuota.setId(teacherQuota.getId());

        partialUpdatedTeacherQuota
            .planExpiresAt(UPDATED_PLAN_EXPIRES_AT)
            .aiUsedCount(UPDATED_AI_USED_COUNT)
            .rowVersion(UPDATED_ROW_VERSION);

        restTeacherQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherQuota.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeacherQuota))
            )
            .andExpect(status().isOk());

        // Validate the TeacherQuota in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherQuotaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeacherQuota, teacherQuota),
            getPersistedTeacherQuota(teacherQuota)
        );
    }

    @Test
    @Transactional
    void fullUpdateTeacherQuotaWithPatch() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherQuota using partial update
        TeacherQuota partialUpdatedTeacherQuota = new TeacherQuota();
        partialUpdatedTeacherQuota.setId(teacherQuota.getId());

        partialUpdatedTeacherQuota
            .planExpiresAt(UPDATED_PLAN_EXPIRES_AT)
            .usedStorageBytes(UPDATED_USED_STORAGE_BYTES)
            .aiUsedCount(UPDATED_AI_USED_COUNT)
            .rowVersion(UPDATED_ROW_VERSION);

        restTeacherQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherQuota.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeacherQuota))
            )
            .andExpect(status().isOk());

        // Validate the TeacherQuota in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherQuotaUpdatableFieldsEquals(partialUpdatedTeacherQuota, getPersistedTeacherQuota(partialUpdatedTeacherQuota));
    }

    @Test
    @Transactional
    void patchNonExistingTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teacherQuotaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teacherQuotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teacherQuotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeacherQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherQuota.setId(longCount.incrementAndGet());

        // Create the TeacherQuota
        TeacherQuotaDTO teacherQuotaDTO = teacherQuotaMapper.toDto(teacherQuota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherQuotaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teacherQuotaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherQuota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeacherQuota() throws Exception {
        // Initialize the database
        insertedTeacherQuota = teacherQuotaRepository.saveAndFlush(teacherQuota);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teacherQuota
        restTeacherQuotaMockMvc
            .perform(delete(ENTITY_API_URL_ID, teacherQuota.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teacherQuotaRepository.count();
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

    protected TeacherQuota getPersistedTeacherQuota(TeacherQuota teacherQuota) {
        return teacherQuotaRepository.findById(teacherQuota.getId()).orElseThrow();
    }

    protected void assertPersistedTeacherQuotaToMatchAllProperties(TeacherQuota expectedTeacherQuota) {
        assertTeacherQuotaAllPropertiesEquals(expectedTeacherQuota, getPersistedTeacherQuota(expectedTeacherQuota));
    }

    protected void assertPersistedTeacherQuotaToMatchUpdatableProperties(TeacherQuota expectedTeacherQuota) {
        assertTeacherQuotaAllUpdatablePropertiesEquals(expectedTeacherQuota, getPersistedTeacherQuota(expectedTeacherQuota));
    }
}
