package com.lumi.web.rest;

import static com.lumi.domain.LessonProgressAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.CourseEnrollment;
import com.lumi.domain.Lesson;
import com.lumi.domain.LessonProgress;
import com.lumi.repository.LessonProgressRepository;
import com.lumi.service.dto.LessonProgressDTO;
import com.lumi.service.mapper.LessonProgressMapper;
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
 * Integration tests for the {@link LessonProgressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonProgressResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LAST_POSITION_SECONDS = new BigDecimal(0);
    private static final BigDecimal UPDATED_LAST_POSITION_SECONDS = new BigDecimal(1);
    private static final BigDecimal SMALLER_LAST_POSITION_SECONDS = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_LAST_ACCESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_ACCESSED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Long DEFAULT_ROW_VERSION = 1L;
    private static final Long UPDATED_ROW_VERSION = 2L;
    private static final Long SMALLER_ROW_VERSION = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/lesson-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private LessonProgressMapper lessonProgressMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonProgressMockMvc;

    private LessonProgress lessonProgress;

    private LessonProgress insertedLessonProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonProgress createEntity(EntityManager em) {
        LessonProgress lessonProgress = new LessonProgress()
            .status(DEFAULT_STATUS)
            .lastPositionSeconds(DEFAULT_LAST_POSITION_SECONDS)
            .lastAccessedAt(DEFAULT_LAST_ACCESSED_AT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .rowVersion(DEFAULT_ROW_VERSION);
        // Add required entity
        CourseEnrollment courseEnrollment;
        if (TestUtil.findAll(em, CourseEnrollment.class).isEmpty()) {
            courseEnrollment = CourseEnrollmentResourceIT.createEntity(em);
            em.persist(courseEnrollment);
            em.flush();
        } else {
            courseEnrollment = TestUtil.findAll(em, CourseEnrollment.class).getFirst();
        }
        lessonProgress.setEnrollment(courseEnrollment);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        lessonProgress.setLesson(lesson);
        return lessonProgress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonProgress createUpdatedEntity(EntityManager em) {
        LessonProgress updatedLessonProgress = new LessonProgress()
            .status(UPDATED_STATUS)
            .lastPositionSeconds(UPDATED_LAST_POSITION_SECONDS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .rowVersion(UPDATED_ROW_VERSION);
        // Add required entity
        CourseEnrollment courseEnrollment;
        if (TestUtil.findAll(em, CourseEnrollment.class).isEmpty()) {
            courseEnrollment = CourseEnrollmentResourceIT.createUpdatedEntity(em);
            em.persist(courseEnrollment);
            em.flush();
        } else {
            courseEnrollment = TestUtil.findAll(em, CourseEnrollment.class).getFirst();
        }
        updatedLessonProgress.setEnrollment(courseEnrollment);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        updatedLessonProgress.setLesson(lesson);
        return updatedLessonProgress;
    }

    @BeforeEach
    void initTest() {
        lessonProgress = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLessonProgress != null) {
            lessonProgressRepository.delete(insertedLessonProgress);
            insertedLessonProgress = null;
        }
    }

    @Test
    @Transactional
    void createLessonProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);
        var returnedLessonProgressDTO = om.readValue(
            restLessonProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LessonProgressDTO.class
        );

        // Validate the LessonProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLessonProgress = lessonProgressMapper.toEntity(returnedLessonProgressDTO);
        assertLessonProgressUpdatableFieldsEquals(returnedLessonProgress, getPersistedLessonProgress(returnedLessonProgress));

        insertedLessonProgress = returnedLessonProgress;
    }

    @Test
    @Transactional
    void createLessonProgressWithExistingId() throws Exception {
        // Create the LessonProgress with an existing ID
        lessonProgress.setId(1L);
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setStatus(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastPositionSecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setLastPositionSeconds(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRowVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setRowVersion(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLessonProgresses() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].lastPositionSeconds").value(hasItem(sameNumber(DEFAULT_LAST_POSITION_SECONDS))))
            .andExpect(jsonPath("$.[*].lastAccessedAt").value(hasItem(DEFAULT_LAST_ACCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));
    }

    @Test
    @Transactional
    void getLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get the lessonProgress
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, lessonProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonProgress.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.lastPositionSeconds").value(sameNumber(DEFAULT_LAST_POSITION_SECONDS)))
            .andExpect(jsonPath("$.lastAccessedAt").value(DEFAULT_LAST_ACCESSED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.rowVersion").value(DEFAULT_ROW_VERSION.intValue()));
    }

    @Test
    @Transactional
    void getLessonProgressesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        Long id = lessonProgress.getId();

        defaultLessonProgressFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLessonProgressFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLessonProgressFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where status equals to
        defaultLessonProgressFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where status in
        defaultLessonProgressFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where status is not null
        defaultLessonProgressFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonProgressesByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where status contains
        defaultLessonProgressFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where status does not contain
        defaultLessonProgressFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds equals to
        defaultLessonProgressFiltering(
            "lastPositionSeconds.equals=" + DEFAULT_LAST_POSITION_SECONDS,
            "lastPositionSeconds.equals=" + UPDATED_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds in
        defaultLessonProgressFiltering(
            "lastPositionSeconds.in=" + DEFAULT_LAST_POSITION_SECONDS + "," + UPDATED_LAST_POSITION_SECONDS,
            "lastPositionSeconds.in=" + UPDATED_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds is not null
        defaultLessonProgressFiltering("lastPositionSeconds.specified=true", "lastPositionSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds is greater than or equal to
        defaultLessonProgressFiltering(
            "lastPositionSeconds.greaterThanOrEqual=" + DEFAULT_LAST_POSITION_SECONDS,
            "lastPositionSeconds.greaterThanOrEqual=" + UPDATED_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds is less than or equal to
        defaultLessonProgressFiltering(
            "lastPositionSeconds.lessThanOrEqual=" + DEFAULT_LAST_POSITION_SECONDS,
            "lastPositionSeconds.lessThanOrEqual=" + SMALLER_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds is less than
        defaultLessonProgressFiltering(
            "lastPositionSeconds.lessThan=" + UPDATED_LAST_POSITION_SECONDS,
            "lastPositionSeconds.lessThan=" + DEFAULT_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastPositionSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastPositionSeconds is greater than
        defaultLessonProgressFiltering(
            "lastPositionSeconds.greaterThan=" + SMALLER_LAST_POSITION_SECONDS,
            "lastPositionSeconds.greaterThan=" + DEFAULT_LAST_POSITION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastAccessedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastAccessedAt equals to
        defaultLessonProgressFiltering(
            "lastAccessedAt.equals=" + DEFAULT_LAST_ACCESSED_AT,
            "lastAccessedAt.equals=" + UPDATED_LAST_ACCESSED_AT
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastAccessedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastAccessedAt in
        defaultLessonProgressFiltering(
            "lastAccessedAt.in=" + DEFAULT_LAST_ACCESSED_AT + "," + UPDATED_LAST_ACCESSED_AT,
            "lastAccessedAt.in=" + UPDATED_LAST_ACCESSED_AT
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLastAccessedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where lastAccessedAt is not null
        defaultLessonProgressFiltering("lastAccessedAt.specified=true", "lastAccessedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonProgressesByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where completedAt equals to
        defaultLessonProgressFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where completedAt in
        defaultLessonProgressFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where completedAt is not null
        defaultLessonProgressFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion equals to
        defaultLessonProgressFiltering("rowVersion.equals=" + DEFAULT_ROW_VERSION, "rowVersion.equals=" + UPDATED_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion in
        defaultLessonProgressFiltering(
            "rowVersion.in=" + DEFAULT_ROW_VERSION + "," + UPDATED_ROW_VERSION,
            "rowVersion.in=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion is not null
        defaultLessonProgressFiltering("rowVersion.specified=true", "rowVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion is greater than or equal to
        defaultLessonProgressFiltering(
            "rowVersion.greaterThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.greaterThanOrEqual=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion is less than or equal to
        defaultLessonProgressFiltering(
            "rowVersion.lessThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.lessThanOrEqual=" + SMALLER_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion is less than
        defaultLessonProgressFiltering("rowVersion.lessThan=" + UPDATED_ROW_VERSION, "rowVersion.lessThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByRowVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList where rowVersion is greater than
        defaultLessonProgressFiltering("rowVersion.greaterThan=" + SMALLER_ROW_VERSION, "rowVersion.greaterThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllLessonProgressesByEnrollmentIsEqualToSomething() throws Exception {
        CourseEnrollment enrollment;
        if (TestUtil.findAll(em, CourseEnrollment.class).isEmpty()) {
            lessonProgressRepository.saveAndFlush(lessonProgress);
            enrollment = CourseEnrollmentResourceIT.createEntity(em);
        } else {
            enrollment = TestUtil.findAll(em, CourseEnrollment.class).getFirst();
        }
        em.persist(enrollment);
        em.flush();
        lessonProgress.setEnrollment(enrollment);
        lessonProgressRepository.saveAndFlush(lessonProgress);
        Long enrollmentId = enrollment.getId();
        // Get all the lessonProgressList where enrollment equals to enrollmentId
        defaultLessonProgressShouldBeFound("enrollmentId.equals=" + enrollmentId);

        // Get all the lessonProgressList where enrollment equals to (enrollmentId + 1)
        defaultLessonProgressShouldNotBeFound("enrollmentId.equals=" + (enrollmentId + 1));
    }

    @Test
    @Transactional
    void getAllLessonProgressesByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lessonProgressRepository.saveAndFlush(lessonProgress);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        lessonProgress.setLesson(lesson);
        lessonProgressRepository.saveAndFlush(lessonProgress);
        Long lessonId = lesson.getId();
        // Get all the lessonProgressList where lesson equals to lessonId
        defaultLessonProgressShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the lessonProgressList where lesson equals to (lessonId + 1)
        defaultLessonProgressShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultLessonProgressFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLessonProgressShouldBeFound(shouldBeFound);
        defaultLessonProgressShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLessonProgressShouldBeFound(String filter) throws Exception {
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].lastPositionSeconds").value(hasItem(sameNumber(DEFAULT_LAST_POSITION_SECONDS))))
            .andExpect(jsonPath("$.[*].lastAccessedAt").value(hasItem(DEFAULT_LAST_ACCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));

        // Check, that the count call also returns 1
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLessonProgressShouldNotBeFound(String filter) throws Exception {
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLessonProgress() throws Exception {
        // Get the lessonProgress
        restLessonProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress
        LessonProgress updatedLessonProgress = lessonProgressRepository.findById(lessonProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLessonProgress are not directly saved in db
        em.detach(updatedLessonProgress);
        updatedLessonProgress
            .status(UPDATED_STATUS)
            .lastPositionSeconds(UPDATED_LAST_POSITION_SECONDS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .rowVersion(UPDATED_ROW_VERSION);
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(updatedLessonProgress);

        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLessonProgressToMatchAllProperties(updatedLessonProgress);
    }

    @Test
    @Transactional
    void putNonExistingLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonProgressWithPatch() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress using partial update
        LessonProgress partialUpdatedLessonProgress = new LessonProgress();
        partialUpdatedLessonProgress.setId(lessonProgress.getId());

        partialUpdatedLessonProgress.lastPositionSeconds(UPDATED_LAST_POSITION_SECONDS).completedAt(UPDATED_COMPLETED_AT);

        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLessonProgress))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLessonProgress, lessonProgress),
            getPersistedLessonProgress(lessonProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateLessonProgressWithPatch() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress using partial update
        LessonProgress partialUpdatedLessonProgress = new LessonProgress();
        partialUpdatedLessonProgress.setId(lessonProgress.getId());

        partialUpdatedLessonProgress
            .status(UPDATED_STATUS)
            .lastPositionSeconds(UPDATED_LAST_POSITION_SECONDS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .rowVersion(UPDATED_ROW_VERSION);

        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLessonProgress))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonProgressUpdatableFieldsEquals(partialUpdatedLessonProgress, getPersistedLessonProgress(partialUpdatedLessonProgress));
    }

    @Test
    @Transactional
    void patchNonExistingLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(lessonProgressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lessonProgress
        restLessonProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, lessonProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lessonProgressRepository.count();
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

    protected LessonProgress getPersistedLessonProgress(LessonProgress lessonProgress) {
        return lessonProgressRepository.findById(lessonProgress.getId()).orElseThrow();
    }

    protected void assertPersistedLessonProgressToMatchAllProperties(LessonProgress expectedLessonProgress) {
        assertLessonProgressAllPropertiesEquals(expectedLessonProgress, getPersistedLessonProgress(expectedLessonProgress));
    }

    protected void assertPersistedLessonProgressToMatchUpdatableProperties(LessonProgress expectedLessonProgress) {
        assertLessonProgressAllUpdatablePropertiesEquals(expectedLessonProgress, getPersistedLessonProgress(expectedLessonProgress));
    }
}
