package com.lumi.web.rest;

import static com.lumi.domain.ExamAttemptAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Exam;
import com.lumi.domain.ExamAttempt;
import com.lumi.domain.User;
import com.lumi.repository.ExamAttemptRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.ExamAttemptService;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.mapper.ExamAttemptMapper;
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
 * Integration tests for the {@link ExamAttemptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamAttemptResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.ofEpochMilli(1791835009362L);

    private static final BigDecimal DEFAULT_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SCORE = new BigDecimal(0 - 1);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Long DEFAULT_ROW_VERSION = 0L;
    private static final Long UPDATED_ROW_VERSION = 1L;
    private static final Long SMALLER_ROW_VERSION = 0L - 1L;

    private static final String ENTITY_API_URL = "/api/exam-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ExamAttemptRepository examAttemptRepositoryMock;

    @Autowired
    private ExamAttemptMapper examAttemptMapper;

    @Mock
    private ExamAttemptService examAttemptServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamAttemptMockMvc;

    private ExamAttempt examAttempt;

    private ExamAttempt insertedExamAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAttempt createEntity(EntityManager em) {
        ExamAttempt examAttempt = new ExamAttempt()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .score(DEFAULT_SCORE)
            .status(DEFAULT_STATUS)
            .rowVersion(DEFAULT_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        examAttempt.setStudent(user);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        examAttempt.setExam(exam);
        return examAttempt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAttempt createUpdatedEntity(EntityManager em) {
        ExamAttempt updatedExamAttempt = new ExamAttempt()
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .score(UPDATED_SCORE)
            .status(UPDATED_STATUS)
            .rowVersion(UPDATED_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedExamAttempt.setStudent(user);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createUpdatedEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        updatedExamAttempt.setExam(exam);
        return updatedExamAttempt;
    }

    @BeforeEach
    void initTest() {
        examAttempt = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExamAttempt != null) {
            examAttemptRepository.delete(insertedExamAttempt);
            insertedExamAttempt = null;
        }
    }

    @Test
    @Transactional
    void createExamAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);
        var returnedExamAttemptDTO = om.readValue(
            restExamAttemptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamAttemptDTO.class
        );

        // Validate the ExamAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExamAttempt = examAttemptMapper.toEntity(returnedExamAttemptDTO);
        assertExamAttemptUpdatableFieldsEquals(returnedExamAttempt, getPersistedExamAttempt(returnedExamAttempt));

        insertedExamAttempt = returnedExamAttempt;
    }

    @Test
    @Transactional
    void createExamAttemptWithExistingId() throws Exception {
        // Create the ExamAttempt with an existing ID
        examAttempt.setId(1L);
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examAttempt.setStartTime(null);

        // Create the ExamAttempt, which fails.
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        restExamAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExamAttempts() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamAttemptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(examAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examAttemptServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamAttemptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examAttemptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get the examAttempt
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, examAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examAttempt.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.score").value(sameNumber(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.rowVersion").value(DEFAULT_ROW_VERSION.intValue()));
    }

    @Test
    @Transactional
    void getExamAttemptsByIdFiltering() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        Long id = examAttempt.getId();

        defaultExamAttemptFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultExamAttemptFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultExamAttemptFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where startTime equals to
        defaultExamAttemptFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where startTime in
        defaultExamAttemptFiltering("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME, "startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where startTime is not null
        defaultExamAttemptFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAttemptsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where endTime equals to
        defaultExamAttemptFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where endTime in
        defaultExamAttemptFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where endTime is not null
        defaultExamAttemptFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score equals to
        defaultExamAttemptFiltering("score.equals=" + DEFAULT_SCORE, "score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score in
        defaultExamAttemptFiltering("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE, "score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score is not null
        defaultExamAttemptFiltering("score.specified=true", "score.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score is greater than or equal to
        defaultExamAttemptFiltering("score.greaterThanOrEqual=" + DEFAULT_SCORE, "score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score is less than or equal to
        defaultExamAttemptFiltering("score.lessThanOrEqual=" + DEFAULT_SCORE, "score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score is less than
        defaultExamAttemptFiltering("score.lessThan=" + UPDATED_SCORE, "score.lessThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where score is greater than
        defaultExamAttemptFiltering("score.greaterThan=" + SMALLER_SCORE, "score.greaterThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where status equals to
        defaultExamAttemptFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where status in
        defaultExamAttemptFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where status is not null
        defaultExamAttemptFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where status contains
        defaultExamAttemptFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where status does not contain
        defaultExamAttemptFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion equals to
        defaultExamAttemptFiltering("rowVersion.equals=" + DEFAULT_ROW_VERSION, "rowVersion.equals=" + UPDATED_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion in
        defaultExamAttemptFiltering(
            "rowVersion.in=" + DEFAULT_ROW_VERSION + "," + UPDATED_ROW_VERSION,
            "rowVersion.in=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion is not null
        defaultExamAttemptFiltering("rowVersion.specified=true", "rowVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion is greater than or equal to
        defaultExamAttemptFiltering(
            "rowVersion.greaterThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.greaterThanOrEqual=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion is less than or equal to
        defaultExamAttemptFiltering(
            "rowVersion.lessThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.lessThanOrEqual=" + SMALLER_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion is less than
        defaultExamAttemptFiltering("rowVersion.lessThan=" + UPDATED_ROW_VERSION, "rowVersion.lessThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByRowVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList where rowVersion is greater than
        defaultExamAttemptFiltering("rowVersion.greaterThan=" + SMALLER_ROW_VERSION, "rowVersion.greaterThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllExamAttemptsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            examAttemptRepository.saveAndFlush(examAttempt);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        examAttempt.setStudent(student);
        examAttemptRepository.saveAndFlush(examAttempt);
        Long studentId = student.getId();
        // Get all the examAttemptList where student equals to studentId
        defaultExamAttemptShouldBeFound("studentId.equals=" + studentId);

        // Get all the examAttemptList where student equals to (studentId + 1)
        defaultExamAttemptShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllExamAttemptsByExamIsEqualToSomething() throws Exception {
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            examAttemptRepository.saveAndFlush(examAttempt);
            exam = ExamResourceIT.createEntity();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        em.persist(exam);
        em.flush();
        examAttempt.setExam(exam);
        examAttemptRepository.saveAndFlush(examAttempt);
        Long examId = exam.getId();
        // Get all the examAttemptList where exam equals to examId
        defaultExamAttemptShouldBeFound("examId.equals=" + examId);

        // Get all the examAttemptList where exam equals to (examId + 1)
        defaultExamAttemptShouldNotBeFound("examId.equals=" + (examId + 1));
    }

    private void defaultExamAttemptFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultExamAttemptShouldBeFound(shouldBeFound);
        defaultExamAttemptShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExamAttemptShouldBeFound(String filter) throws Exception {
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));

        // Check, that the count call also returns 1
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExamAttemptShouldNotBeFound(String filter) throws Exception {
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExamAttempt() throws Exception {
        // Get the examAttempt
        restExamAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt
        ExamAttempt updatedExamAttempt = examAttemptRepository.findById(examAttempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExamAttempt are not directly saved in db
        em.detach(updatedExamAttempt);
        updatedExamAttempt
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .score(UPDATED_SCORE)
            .status(UPDATED_STATUS)
            .rowVersion(UPDATED_ROW_VERSION);
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(updatedExamAttempt);

        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamAttemptToMatchAllProperties(updatedExamAttempt);
    }

    @Test
    @Transactional
    void putNonExistingExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt using partial update
        ExamAttempt partialUpdatedExamAttempt = new ExamAttempt();
        partialUpdatedExamAttempt.setId(examAttempt.getId());

        partialUpdatedExamAttempt.score(UPDATED_SCORE).status(UPDATED_STATUS);

        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAttempt))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExamAttempt, examAttempt),
            getPersistedExamAttempt(examAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateExamAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt using partial update
        ExamAttempt partialUpdatedExamAttempt = new ExamAttempt();
        partialUpdatedExamAttempt.setId(examAttempt.getId());

        partialUpdatedExamAttempt
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .score(UPDATED_SCORE)
            .status(UPDATED_STATUS)
            .rowVersion(UPDATED_ROW_VERSION);

        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAttempt))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAttemptUpdatableFieldsEquals(partialUpdatedExamAttempt, getPersistedExamAttempt(partialUpdatedExamAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the examAttempt
        restExamAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, examAttempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examAttemptRepository.count();
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

    protected ExamAttempt getPersistedExamAttempt(ExamAttempt examAttempt) {
        return examAttemptRepository.findById(examAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedExamAttemptToMatchAllProperties(ExamAttempt expectedExamAttempt) {
        assertExamAttemptAllPropertiesEquals(expectedExamAttempt, getPersistedExamAttempt(expectedExamAttempt));
    }

    protected void assertPersistedExamAttemptToMatchUpdatableProperties(ExamAttempt expectedExamAttempt) {
        assertExamAttemptAllUpdatablePropertiesEquals(expectedExamAttempt, getPersistedExamAttempt(expectedExamAttempt));
    }
}
