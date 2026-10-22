package com.lumi.web.rest;

import static com.lumi.domain.ExamQuestionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Exam;
import com.lumi.domain.ExamQuestion;
import com.lumi.domain.Question;
import com.lumi.repository.ExamQuestionRepository;
import com.lumi.service.ExamQuestionService;
import com.lumi.service.dto.ExamQuestionDTO;
import com.lumi.service.mapper.ExamQuestionMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link ExamQuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamQuestionResourceIT {

    private static final Integer DEFAULT_ORDER_INDEX = 0;
    private static final Integer UPDATED_ORDER_INDEX = 1;
    private static final Integer SMALLER_ORDER_INDEX = 0 - 1;

    private static final BigDecimal DEFAULT_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SCORE = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/exam-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Mock
    private ExamQuestionRepository examQuestionRepositoryMock;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Mock
    private ExamQuestionService examQuestionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamQuestionMockMvc;

    private ExamQuestion examQuestion;

    private ExamQuestion insertedExamQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamQuestion createEntity(EntityManager em) {
        ExamQuestion examQuestion = new ExamQuestion().orderIndex(DEFAULT_ORDER_INDEX).score(DEFAULT_SCORE);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        examQuestion.setExam(exam);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        examQuestion.setQuestion(question);
        return examQuestion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamQuestion createUpdatedEntity(EntityManager em) {
        ExamQuestion updatedExamQuestion = new ExamQuestion().orderIndex(UPDATED_ORDER_INDEX).score(UPDATED_SCORE);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createUpdatedEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        updatedExamQuestion.setExam(exam);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createUpdatedEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        updatedExamQuestion.setQuestion(question);
        return updatedExamQuestion;
    }

    @BeforeEach
    void initTest() {
        examQuestion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExamQuestion != null) {
            examQuestionRepository.delete(insertedExamQuestion);
            insertedExamQuestion = null;
        }
    }

    @Test
    @Transactional
    void createExamQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);
        var returnedExamQuestionDTO = om.readValue(
            restExamQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examQuestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamQuestionDTO.class
        );

        // Validate the ExamQuestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExamQuestion = examQuestionMapper.toEntity(returnedExamQuestionDTO);
        assertExamQuestionUpdatableFieldsEquals(returnedExamQuestion, getPersistedExamQuestion(returnedExamQuestion));

        insertedExamQuestion = returnedExamQuestion;
    }

    @Test
    @Transactional
    void createExamQuestionWithExistingId() throws Exception {
        // Create the ExamQuestion with an existing ID
        examQuestion.setId(1L);
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examQuestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examQuestion.setOrderIndex(null);

        // Create the ExamQuestion, which fails.
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        restExamQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScoreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examQuestion.setScore(null);

        // Create the ExamQuestion, which fails.
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        restExamQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExamQuestions() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamQuestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(examQuestionServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restExamQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examQuestionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamQuestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examQuestionServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restExamQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examQuestionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExamQuestion() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get the examQuestion
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, examQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examQuestion.getId().intValue()))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX))
            .andExpect(jsonPath("$.score").value(sameNumber(DEFAULT_SCORE)));
    }

    @Test
    @Transactional
    void getExamQuestionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        Long id = examQuestion.getId();

        defaultExamQuestionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultExamQuestionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultExamQuestionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex equals to
        defaultExamQuestionFiltering("orderIndex.equals=" + DEFAULT_ORDER_INDEX, "orderIndex.equals=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex in
        defaultExamQuestionFiltering(
            "orderIndex.in=" + DEFAULT_ORDER_INDEX + "," + UPDATED_ORDER_INDEX,
            "orderIndex.in=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex is not null
        defaultExamQuestionFiltering("orderIndex.specified=true", "orderIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex is greater than or equal to
        defaultExamQuestionFiltering(
            "orderIndex.greaterThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.greaterThanOrEqual=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex is less than or equal to
        defaultExamQuestionFiltering(
            "orderIndex.lessThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.lessThanOrEqual=" + SMALLER_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex is less than
        defaultExamQuestionFiltering("orderIndex.lessThan=" + UPDATED_ORDER_INDEX, "orderIndex.lessThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByOrderIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where orderIndex is greater than
        defaultExamQuestionFiltering("orderIndex.greaterThan=" + SMALLER_ORDER_INDEX, "orderIndex.greaterThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score equals to
        defaultExamQuestionFiltering("score.equals=" + DEFAULT_SCORE, "score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score in
        defaultExamQuestionFiltering("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE, "score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score is not null
        defaultExamQuestionFiltering("score.specified=true", "score.specified=false");
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score is greater than or equal to
        defaultExamQuestionFiltering("score.greaterThanOrEqual=" + DEFAULT_SCORE, "score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score is less than or equal to
        defaultExamQuestionFiltering("score.lessThanOrEqual=" + DEFAULT_SCORE, "score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score is less than
        defaultExamQuestionFiltering("score.lessThan=" + UPDATED_SCORE, "score.lessThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        // Get all the examQuestionList where score is greater than
        defaultExamQuestionFiltering("score.greaterThan=" + SMALLER_SCORE, "score.greaterThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamQuestionsByExamIsEqualToSomething() throws Exception {
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            examQuestionRepository.saveAndFlush(examQuestion);
            exam = ExamResourceIT.createEntity();
        } else {
            exam = TestUtil.findAll(em, Exam.class).getFirst();
        }
        em.persist(exam);
        em.flush();
        examQuestion.setExam(exam);
        examQuestionRepository.saveAndFlush(examQuestion);
        Long examId = exam.getId();
        // Get all the examQuestionList where exam equals to examId
        defaultExamQuestionShouldBeFound("examId.equals=" + examId);

        // Get all the examQuestionList where exam equals to (examId + 1)
        defaultExamQuestionShouldNotBeFound("examId.equals=" + (examId + 1));
    }

    @Test
    @Transactional
    void getAllExamQuestionsByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            examQuestionRepository.saveAndFlush(examQuestion);
            question = QuestionResourceIT.createEntity();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        em.persist(question);
        em.flush();
        examQuestion.setQuestion(question);
        examQuestionRepository.saveAndFlush(examQuestion);
        Long questionId = question.getId();
        // Get all the examQuestionList where question equals to questionId
        defaultExamQuestionShouldBeFound("questionId.equals=" + questionId);

        // Get all the examQuestionList where question equals to (questionId + 1)
        defaultExamQuestionShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    private void defaultExamQuestionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultExamQuestionShouldBeFound(shouldBeFound);
        defaultExamQuestionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExamQuestionShouldBeFound(String filter) throws Exception {
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))));

        // Check, that the count call also returns 1
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExamQuestionShouldNotBeFound(String filter) throws Exception {
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExamQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExamQuestion() throws Exception {
        // Get the examQuestion
        restExamQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamQuestion() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examQuestion
        ExamQuestion updatedExamQuestion = examQuestionRepository.findById(examQuestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExamQuestion are not directly saved in db
        em.detach(updatedExamQuestion);
        updatedExamQuestion.orderIndex(UPDATED_ORDER_INDEX).score(UPDATED_SCORE);
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(updatedExamQuestion);

        restExamQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examQuestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamQuestionToMatchAllProperties(updatedExamQuestion);
    }

    @Test
    @Transactional
    void putNonExistingExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examQuestion using partial update
        ExamQuestion partialUpdatedExamQuestion = new ExamQuestion();
        partialUpdatedExamQuestion.setId(examQuestion.getId());

        partialUpdatedExamQuestion.orderIndex(UPDATED_ORDER_INDEX);

        restExamQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamQuestion))
            )
            .andExpect(status().isOk());

        // Validate the ExamQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamQuestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExamQuestion, examQuestion),
            getPersistedExamQuestion(examQuestion)
        );
    }

    @Test
    @Transactional
    void fullUpdateExamQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examQuestion using partial update
        ExamQuestion partialUpdatedExamQuestion = new ExamQuestion();
        partialUpdatedExamQuestion.setId(examQuestion.getId());

        partialUpdatedExamQuestion.orderIndex(UPDATED_ORDER_INDEX).score(UPDATED_SCORE);

        restExamQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamQuestion))
            )
            .andExpect(status().isOk());

        // Validate the ExamQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamQuestionUpdatableFieldsEquals(partialUpdatedExamQuestion, getPersistedExamQuestion(partialUpdatedExamQuestion));
    }

    @Test
    @Transactional
    void patchNonExistingExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examQuestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examQuestion.setId(longCount.incrementAndGet());

        // Create the ExamQuestion
        ExamQuestionDTO examQuestionDTO = examQuestionMapper.toDto(examQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamQuestion() throws Exception {
        // Initialize the database
        insertedExamQuestion = examQuestionRepository.saveAndFlush(examQuestion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the examQuestion
        restExamQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, examQuestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examQuestionRepository.count();
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

    protected ExamQuestion getPersistedExamQuestion(ExamQuestion examQuestion) {
        return examQuestionRepository.findById(examQuestion.getId()).orElseThrow();
    }

    protected void assertPersistedExamQuestionToMatchAllProperties(ExamQuestion expectedExamQuestion) {
        assertExamQuestionAllPropertiesEquals(expectedExamQuestion, getPersistedExamQuestion(expectedExamQuestion));
    }

    protected void assertPersistedExamQuestionToMatchUpdatableProperties(ExamQuestion expectedExamQuestion) {
        assertExamQuestionAllUpdatablePropertiesEquals(expectedExamQuestion, getPersistedExamQuestion(expectedExamQuestion));
    }
}
