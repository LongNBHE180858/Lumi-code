package com.lumi.web.rest;

import static com.lumi.domain.ContentReportAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ContentReport;
import com.lumi.domain.User;
import com.lumi.repository.ContentReportRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.ContentReportService;
import com.lumi.service.dto.ContentReportDTO;
import com.lumi.service.mapper.ContentReportMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link ContentReportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ContentReportResourceIT {

    private static final String DEFAULT_TARGET_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_TARGET_ID = 1L;
    private static final Long UPDATED_TARGET_ID = 2L;
    private static final Long SMALLER_TARGET_ID = 1L - 1L;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/content-reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContentReportRepository contentReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ContentReportRepository contentReportRepositoryMock;

    @Autowired
    private ContentReportMapper contentReportMapper;

    @Mock
    private ContentReportService contentReportServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContentReportMockMvc;

    private ContentReport contentReport;

    private ContentReport insertedContentReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContentReport createEntity(EntityManager em) {
        ContentReport contentReport = new ContentReport()
            .targetType(DEFAULT_TARGET_TYPE)
            .targetId(DEFAULT_TARGET_ID)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        contentReport.setReporter(user);
        return contentReport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContentReport createUpdatedEntity(EntityManager em) {
        ContentReport updatedContentReport = new ContentReport()
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedContentReport.setReporter(user);
        return updatedContentReport;
    }

    @BeforeEach
    void initTest() {
        contentReport = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedContentReport != null) {
            contentReportRepository.delete(insertedContentReport);
            insertedContentReport = null;
        }
    }

    @Test
    @Transactional
    void createContentReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);
        var returnedContentReportDTO = om.readValue(
            restContentReportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContentReportDTO.class
        );

        // Validate the ContentReport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContentReport = contentReportMapper.toEntity(returnedContentReportDTO);
        assertContentReportUpdatableFieldsEquals(returnedContentReport, getPersistedContentReport(returnedContentReport));

        insertedContentReport = returnedContentReport;
    }

    @Test
    @Transactional
    void createContentReportWithExistingId() throws Exception {
        // Create the ContentReport with an existing ID
        contentReport.setId(1L);
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContentReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTargetTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contentReport.setTargetType(null);

        // Create the ContentReport, which fails.
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        restContentReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contentReport.setTargetId(null);

        // Create the ContentReport, which fails.
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        restContentReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contentReport.setReason(null);

        // Create the ContentReport, which fails.
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        restContentReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContentReports() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contentReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE)))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContentReportsWithEagerRelationshipsIsEnabled() throws Exception {
        when(contentReportServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContentReportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(contentReportServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContentReportsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(contentReportServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContentReportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(contentReportRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getContentReport() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get the contentReport
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL_ID, contentReport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contentReport.getId().intValue()))
            .andExpect(jsonPath("$.targetType").value(DEFAULT_TARGET_TYPE))
            .andExpect(jsonPath("$.targetId").value(DEFAULT_TARGET_ID.intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getContentReportsByIdFiltering() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        Long id = contentReport.getId();

        defaultContentReportFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultContentReportFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultContentReportFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetType equals to
        defaultContentReportFiltering("targetType.equals=" + DEFAULT_TARGET_TYPE, "targetType.equals=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetType in
        defaultContentReportFiltering(
            "targetType.in=" + DEFAULT_TARGET_TYPE + "," + UPDATED_TARGET_TYPE,
            "targetType.in=" + UPDATED_TARGET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetType is not null
        defaultContentReportFiltering("targetType.specified=true", "targetType.specified=false");
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetType contains
        defaultContentReportFiltering("targetType.contains=" + DEFAULT_TARGET_TYPE, "targetType.contains=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetType does not contain
        defaultContentReportFiltering(
            "targetType.doesNotContain=" + UPDATED_TARGET_TYPE,
            "targetType.doesNotContain=" + DEFAULT_TARGET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId equals to
        defaultContentReportFiltering("targetId.equals=" + DEFAULT_TARGET_ID, "targetId.equals=" + UPDATED_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId in
        defaultContentReportFiltering("targetId.in=" + DEFAULT_TARGET_ID + "," + UPDATED_TARGET_ID, "targetId.in=" + UPDATED_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId is not null
        defaultContentReportFiltering("targetId.specified=true", "targetId.specified=false");
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId is greater than or equal to
        defaultContentReportFiltering(
            "targetId.greaterThanOrEqual=" + DEFAULT_TARGET_ID,
            "targetId.greaterThanOrEqual=" + UPDATED_TARGET_ID
        );
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId is less than or equal to
        defaultContentReportFiltering("targetId.lessThanOrEqual=" + DEFAULT_TARGET_ID, "targetId.lessThanOrEqual=" + SMALLER_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId is less than
        defaultContentReportFiltering("targetId.lessThan=" + UPDATED_TARGET_ID, "targetId.lessThan=" + DEFAULT_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllContentReportsByTargetIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where targetId is greater than
        defaultContentReportFiltering("targetId.greaterThan=" + SMALLER_TARGET_ID, "targetId.greaterThan=" + DEFAULT_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllContentReportsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where reason equals to
        defaultContentReportFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllContentReportsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where reason in
        defaultContentReportFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllContentReportsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where reason is not null
        defaultContentReportFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllContentReportsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where reason contains
        defaultContentReportFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllContentReportsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where reason does not contain
        defaultContentReportFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllContentReportsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where status equals to
        defaultContentReportFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllContentReportsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where status in
        defaultContentReportFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllContentReportsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where status is not null
        defaultContentReportFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllContentReportsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where status contains
        defaultContentReportFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllContentReportsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where status does not contain
        defaultContentReportFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllContentReportsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where createdAt equals to
        defaultContentReportFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllContentReportsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where createdAt in
        defaultContentReportFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllContentReportsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        // Get all the contentReportList where createdAt is not null
        defaultContentReportFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllContentReportsByReporterIsEqualToSomething() throws Exception {
        User reporter;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            contentReportRepository.saveAndFlush(contentReport);
            reporter = UserResourceIT.createEntity();
        } else {
            reporter = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(reporter);
        em.flush();
        contentReport.setReporter(reporter);
        contentReportRepository.saveAndFlush(contentReport);
        Long reporterId = reporter.getId();
        // Get all the contentReportList where reporter equals to reporterId
        defaultContentReportShouldBeFound("reporterId.equals=" + reporterId);

        // Get all the contentReportList where reporter equals to (reporterId + 1)
        defaultContentReportShouldNotBeFound("reporterId.equals=" + (reporterId + 1));
    }

    private void defaultContentReportFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultContentReportShouldBeFound(shouldBeFound);
        defaultContentReportShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContentReportShouldBeFound(String filter) throws Exception {
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contentReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE)))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID.intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContentReportShouldNotBeFound(String filter) throws Exception {
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContentReportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingContentReport() throws Exception {
        // Get the contentReport
        restContentReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContentReport() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contentReport
        ContentReport updatedContentReport = contentReportRepository.findById(contentReport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContentReport are not directly saved in db
        em.detach(updatedContentReport);
        updatedContentReport
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(updatedContentReport);

        restContentReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentReportDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contentReportDTO))
            )
            .andExpect(status().isOk());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContentReportToMatchAllProperties(updatedContentReport);
    }

    @Test
    @Transactional
    void putNonExistingContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentReportDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contentReportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contentReportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContentReportWithPatch() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contentReport using partial update
        ContentReport partialUpdatedContentReport = new ContentReport();
        partialUpdatedContentReport.setId(contentReport.getId());

        partialUpdatedContentReport.targetType(UPDATED_TARGET_TYPE).createdAt(UPDATED_CREATED_AT);

        restContentReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContentReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContentReport))
            )
            .andExpect(status().isOk());

        // Validate the ContentReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContentReportUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContentReport, contentReport),
            getPersistedContentReport(contentReport)
        );
    }

    @Test
    @Transactional
    void fullUpdateContentReportWithPatch() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contentReport using partial update
        ContentReport partialUpdatedContentReport = new ContentReport();
        partialUpdatedContentReport.setId(contentReport.getId());

        partialUpdatedContentReport
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restContentReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContentReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContentReport))
            )
            .andExpect(status().isOk());

        // Validate the ContentReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContentReportUpdatableFieldsEquals(partialUpdatedContentReport, getPersistedContentReport(partialUpdatedContentReport));
    }

    @Test
    @Transactional
    void patchNonExistingContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contentReportDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contentReportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contentReportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContentReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contentReport.setId(longCount.incrementAndGet());

        // Create the ContentReport
        ContentReportDTO contentReportDTO = contentReportMapper.toDto(contentReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentReportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contentReportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContentReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContentReport() throws Exception {
        // Initialize the database
        insertedContentReport = contentReportRepository.saveAndFlush(contentReport);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contentReport
        restContentReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, contentReport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contentReportRepository.count();
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

    protected ContentReport getPersistedContentReport(ContentReport contentReport) {
        return contentReportRepository.findById(contentReport.getId()).orElseThrow();
    }

    protected void assertPersistedContentReportToMatchAllProperties(ContentReport expectedContentReport) {
        assertContentReportAllPropertiesEquals(expectedContentReport, getPersistedContentReport(expectedContentReport));
    }

    protected void assertPersistedContentReportToMatchUpdatableProperties(ContentReport expectedContentReport) {
        assertContentReportAllUpdatablePropertiesEquals(expectedContentReport, getPersistedContentReport(expectedContentReport));
    }
}
