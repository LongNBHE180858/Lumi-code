package com.lumi.web.rest;

import static com.lumi.domain.ExamAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Exam;
import com.lumi.domain.StudyClass;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.ExamRepository;
import com.lumi.service.ExamService;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.mapper.ExamMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ExamResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION_MINUTES = 1;
    private static final Integer UPDATED_DURATION_MINUTES = 2;
    private static final Integer SMALLER_DURATION_MINUTES = 1 - 1;

    private static final BigDecimal DEFAULT_PASS_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PASS_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PASS_SCORE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_MAX_ATTEMPTS = 1;
    private static final Integer UPDATED_MAX_ATTEMPTS = 2;
    private static final Integer SMALLER_MAX_ATTEMPTS = 1 - 1;

    private static final Boolean DEFAULT_SHUFFLE_QUESTIONS = false;
    private static final Boolean UPDATED_SHUFFLE_QUESTIONS = true;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String ENTITY_API_URL = "/api/exams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamRepository examRepository;

    @Mock
    private ExamRepository examRepositoryMock;

    @Autowired
    private ExamMapper examMapper;

    @Mock
    private ExamService examServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamMockMvc;

    private Exam exam;

    private Exam insertedExam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createEntity() {
        return new Exam()
            .title(DEFAULT_TITLE)
            .durationMinutes(DEFAULT_DURATION_MINUTES)
            .passScore(DEFAULT_PASS_SCORE)
            .maxAttempts(DEFAULT_MAX_ATTEMPTS)
            .shuffleQuestions(DEFAULT_SHUFFLE_QUESTIONS)
            .status(DEFAULT_STATUS)
            .isDeleted(DEFAULT_IS_DELETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createUpdatedEntity() {
        return new Exam()
            .title(UPDATED_TITLE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .passScore(UPDATED_PASS_SCORE)
            .maxAttempts(UPDATED_MAX_ATTEMPTS)
            .shuffleQuestions(UPDATED_SHUFFLE_QUESTIONS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED);
    }

    @BeforeEach
    void initTest() {
        exam = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedExam != null) {
            examRepository.delete(insertedExam);
            insertedExam = null;
        }
    }

    @Test
    @Transactional
    void createExam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);
        var returnedExamDTO = om.readValue(
            restExamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamDTO.class
        );

        // Validate the Exam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExam = examMapper.toEntity(returnedExamDTO);
        assertExamUpdatableFieldsEquals(returnedExam, getPersistedExam(returnedExam));

        insertedExam = returnedExam;
    }

    @Test
    @Transactional
    void createExamWithExistingId() throws Exception {
        // Create the Exam with an existing ID
        exam.setId(1L);
        ExamDTO examDTO = examMapper.toDto(exam);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setTitle(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationMinutesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setDurationMinutes(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPassScoreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setPassScore(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExams() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].passScore").value(hasItem(sameNumber(DEFAULT_PASS_SCORE))))
            .andExpect(jsonPath("$.[*].maxAttempts").value(hasItem(DEFAULT_MAX_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].shuffleQuestions").value(hasItem(DEFAULT_SHUFFLE_QUESTIONS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamsWithEagerRelationshipsIsEnabled() throws Exception {
        when(examServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get the exam
        restExamMockMvc
            .perform(get(ENTITY_API_URL_ID, exam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.passScore").value(sameNumber(DEFAULT_PASS_SCORE)))
            .andExpect(jsonPath("$.maxAttempts").value(DEFAULT_MAX_ATTEMPTS))
            .andExpect(jsonPath("$.shuffleQuestions").value(DEFAULT_SHUFFLE_QUESTIONS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED));
    }

    @Test
    @Transactional
    void getExamsByIdFiltering() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        Long id = exam.getId();

        defaultExamFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultExamFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultExamFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExamsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where title equals to
        defaultExamFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllExamsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where title in
        defaultExamFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllExamsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where title is not null
        defaultExamFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where title contains
        defaultExamFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllExamsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where title does not contain
        defaultExamFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes equals to
        defaultExamFiltering("durationMinutes.equals=" + DEFAULT_DURATION_MINUTES, "durationMinutes.equals=" + UPDATED_DURATION_MINUTES);
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes in
        defaultExamFiltering(
            "durationMinutes.in=" + DEFAULT_DURATION_MINUTES + "," + UPDATED_DURATION_MINUTES,
            "durationMinutes.in=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes is not null
        defaultExamFiltering("durationMinutes.specified=true", "durationMinutes.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes is greater than or equal to
        defaultExamFiltering(
            "durationMinutes.greaterThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.greaterThanOrEqual=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes is less than or equal to
        defaultExamFiltering(
            "durationMinutes.lessThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.lessThanOrEqual=" + SMALLER_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes is less than
        defaultExamFiltering(
            "durationMinutes.lessThan=" + UPDATED_DURATION_MINUTES,
            "durationMinutes.lessThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllExamsByDurationMinutesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where durationMinutes is greater than
        defaultExamFiltering(
            "durationMinutes.greaterThan=" + SMALLER_DURATION_MINUTES,
            "durationMinutes.greaterThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore equals to
        defaultExamFiltering("passScore.equals=" + DEFAULT_PASS_SCORE, "passScore.equals=" + UPDATED_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore in
        defaultExamFiltering("passScore.in=" + DEFAULT_PASS_SCORE + "," + UPDATED_PASS_SCORE, "passScore.in=" + UPDATED_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore is not null
        defaultExamFiltering("passScore.specified=true", "passScore.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore is greater than or equal to
        defaultExamFiltering("passScore.greaterThanOrEqual=" + DEFAULT_PASS_SCORE, "passScore.greaterThanOrEqual=" + UPDATED_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore is less than or equal to
        defaultExamFiltering("passScore.lessThanOrEqual=" + DEFAULT_PASS_SCORE, "passScore.lessThanOrEqual=" + SMALLER_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore is less than
        defaultExamFiltering("passScore.lessThan=" + UPDATED_PASS_SCORE, "passScore.lessThan=" + DEFAULT_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByPassScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where passScore is greater than
        defaultExamFiltering("passScore.greaterThan=" + SMALLER_PASS_SCORE, "passScore.greaterThan=" + DEFAULT_PASS_SCORE);
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts equals to
        defaultExamFiltering("maxAttempts.equals=" + DEFAULT_MAX_ATTEMPTS, "maxAttempts.equals=" + UPDATED_MAX_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts in
        defaultExamFiltering(
            "maxAttempts.in=" + DEFAULT_MAX_ATTEMPTS + "," + UPDATED_MAX_ATTEMPTS,
            "maxAttempts.in=" + UPDATED_MAX_ATTEMPTS
        );
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts is not null
        defaultExamFiltering("maxAttempts.specified=true", "maxAttempts.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts is greater than or equal to
        defaultExamFiltering(
            "maxAttempts.greaterThanOrEqual=" + DEFAULT_MAX_ATTEMPTS,
            "maxAttempts.greaterThanOrEqual=" + UPDATED_MAX_ATTEMPTS
        );
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts is less than or equal to
        defaultExamFiltering("maxAttempts.lessThanOrEqual=" + DEFAULT_MAX_ATTEMPTS, "maxAttempts.lessThanOrEqual=" + SMALLER_MAX_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts is less than
        defaultExamFiltering("maxAttempts.lessThan=" + UPDATED_MAX_ATTEMPTS, "maxAttempts.lessThan=" + DEFAULT_MAX_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllExamsByMaxAttemptsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where maxAttempts is greater than
        defaultExamFiltering("maxAttempts.greaterThan=" + SMALLER_MAX_ATTEMPTS, "maxAttempts.greaterThan=" + DEFAULT_MAX_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllExamsByShuffleQuestionsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where shuffleQuestions equals to
        defaultExamFiltering(
            "shuffleQuestions.equals=" + DEFAULT_SHUFFLE_QUESTIONS,
            "shuffleQuestions.equals=" + UPDATED_SHUFFLE_QUESTIONS
        );
    }

    @Test
    @Transactional
    void getAllExamsByShuffleQuestionsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where shuffleQuestions in
        defaultExamFiltering(
            "shuffleQuestions.in=" + DEFAULT_SHUFFLE_QUESTIONS + "," + UPDATED_SHUFFLE_QUESTIONS,
            "shuffleQuestions.in=" + UPDATED_SHUFFLE_QUESTIONS
        );
    }

    @Test
    @Transactional
    void getAllExamsByShuffleQuestionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where shuffleQuestions is not null
        defaultExamFiltering("shuffleQuestions.specified=true", "shuffleQuestions.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where status equals to
        defaultExamFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllExamsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where status in
        defaultExamFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllExamsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where status is not null
        defaultExamFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where isDeleted equals to
        defaultExamFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllExamsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where isDeleted in
        defaultExamFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllExamsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList where isDeleted is not null
        defaultExamFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllExamsByStudyClassIsEqualToSomething() throws Exception {
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            examRepository.saveAndFlush(exam);
            studyClass = StudyClassResourceIT.createEntity(em);
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        em.persist(studyClass);
        em.flush();
        exam.setStudyClass(studyClass);
        examRepository.saveAndFlush(exam);
        Long studyClassId = studyClass.getId();
        // Get all the examList where studyClass equals to studyClassId
        defaultExamShouldBeFound("studyClassId.equals=" + studyClassId);

        // Get all the examList where studyClass equals to (studyClassId + 1)
        defaultExamShouldNotBeFound("studyClassId.equals=" + (studyClassId + 1));
    }

    private void defaultExamFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultExamShouldBeFound(shouldBeFound);
        defaultExamShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExamShouldBeFound(String filter) throws Exception {
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].passScore").value(hasItem(sameNumber(DEFAULT_PASS_SCORE))))
            .andExpect(jsonPath("$.[*].maxAttempts").value(hasItem(DEFAULT_MAX_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].shuffleQuestions").value(hasItem(DEFAULT_SHUFFLE_QUESTIONS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));

        // Check, that the count call also returns 1
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExamShouldNotBeFound(String filter) throws Exception {
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExam() throws Exception {
        // Get the exam
        restExamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam
        Exam updatedExam = examRepository.findById(exam.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExam are not directly saved in db
        em.detach(updatedExam);
        updatedExam
            .title(UPDATED_TITLE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .passScore(UPDATED_PASS_SCORE)
            .maxAttempts(UPDATED_MAX_ATTEMPTS)
            .shuffleQuestions(UPDATED_SHUFFLE_QUESTIONS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED);
        ExamDTO examDTO = examMapper.toDto(updatedExam);

        restExamMockMvc
            .perform(put(ENTITY_API_URL_ID, examDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isOk());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamToMatchAllProperties(updatedExam);
    }

    @Test
    @Transactional
    void putNonExistingExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL_ID, examDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamWithPatch() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .maxAttempts(UPDATED_MAX_ATTEMPTS)
            .shuffleQuestions(UPDATED_SHUFFLE_QUESTIONS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedExam, exam), getPersistedExam(exam));
    }

    @Test
    @Transactional
    void fullUpdateExamWithPatch() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .title(UPDATED_TITLE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .passScore(UPDATED_PASS_SCORE)
            .maxAttempts(UPDATED_MAX_ATTEMPTS)
            .shuffleQuestions(UPDATED_SHUFFLE_QUESTIONS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamUpdatableFieldsEquals(partialUpdatedExam, getPersistedExam(partialUpdatedExam));
    }

    @Test
    @Transactional
    void patchNonExistingExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the exam
        restExamMockMvc
            .perform(delete(ENTITY_API_URL_ID, exam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examRepository.count();
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

    protected Exam getPersistedExam(Exam exam) {
        return examRepository.findById(exam.getId()).orElseThrow();
    }

    protected void assertPersistedExamToMatchAllProperties(Exam expectedExam) {
        assertExamAllPropertiesEquals(expectedExam, getPersistedExam(expectedExam));
    }

    protected void assertPersistedExamToMatchUpdatableProperties(Exam expectedExam) {
        assertExamAllUpdatablePropertiesEquals(expectedExam, getPersistedExam(expectedExam));
    }
}
