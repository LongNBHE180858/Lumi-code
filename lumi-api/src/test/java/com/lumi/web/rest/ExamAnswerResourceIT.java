package com.lumi.web.rest;

import static com.lumi.domain.ExamAnswerAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ExamAnswer;
import com.lumi.domain.ExamAttempt;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionOption;
import com.lumi.repository.ExamAnswerRepository;
import com.lumi.service.dto.ExamAnswerDTO;
import com.lumi.service.mapper.ExamAnswerMapper;
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
 * Integration tests for the {@link ExamAnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamAnswerResourceIT {

    private static final String DEFAULT_TEXT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_TEXT_ANSWER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_CORRECT = false;
    private static final Boolean UPDATED_IS_CORRECT = true;

    private static final BigDecimal DEFAULT_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SCORE = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/exam-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamAnswerRepository examAnswerRepository;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamAnswerMockMvc;

    private ExamAnswer examAnswer;

    private ExamAnswer insertedExamAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAnswer createEntity(EntityManager em) {
        ExamAnswer examAnswer = new ExamAnswer().textAnswer(DEFAULT_TEXT_ANSWER).isCorrect(DEFAULT_IS_CORRECT).score(DEFAULT_SCORE);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).getFirst();
        }
        examAnswer.setAttempt(examAttempt);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        examAnswer.setQuestion(question);
        return examAnswer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAnswer createUpdatedEntity(EntityManager em) {
        ExamAnswer updatedExamAnswer = new ExamAnswer().textAnswer(UPDATED_TEXT_ANSWER).isCorrect(UPDATED_IS_CORRECT).score(UPDATED_SCORE);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createUpdatedEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).getFirst();
        }
        updatedExamAnswer.setAttempt(examAttempt);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createUpdatedEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        updatedExamAnswer.setQuestion(question);
        return updatedExamAnswer;
    }

    @BeforeEach
    void initTest() {
        examAnswer = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExamAnswer != null) {
            examAnswerRepository.delete(insertedExamAnswer);
            insertedExamAnswer = null;
        }
    }

    @Test
    @Transactional
    void createExamAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);
        var returnedExamAnswerDTO = om.readValue(
            restExamAnswerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAnswerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamAnswerDTO.class
        );

        // Validate the ExamAnswer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExamAnswer = examAnswerMapper.toEntity(returnedExamAnswerDTO);
        assertExamAnswerUpdatableFieldsEquals(returnedExamAnswer, getPersistedExamAnswer(returnedExamAnswer));

        insertedExamAnswer = returnedExamAnswer;
    }

    @Test
    @Transactional
    void createExamAnswerWithExistingId() throws Exception {
        // Create the ExamAnswer with an existing ID
        examAnswer.setId(1L);
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAnswerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExamAnswers() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].textAnswer").value(hasItem(DEFAULT_TEXT_ANSWER)))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))));
    }

    @Test
    @Transactional
    void getExamAnswer() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get the examAnswer
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, examAnswer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examAnswer.getId().intValue()))
            .andExpect(jsonPath("$.textAnswer").value(DEFAULT_TEXT_ANSWER))
            .andExpect(jsonPath("$.isCorrect").value(DEFAULT_IS_CORRECT))
            .andExpect(jsonPath("$.score").value(sameNumber(DEFAULT_SCORE)));
    }

    @Test
    @Transactional
    void getExamAnswersByIdFiltering() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        Long id = examAnswer.getId();

        defaultExamAnswerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultExamAnswerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultExamAnswerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExamAnswersByTextAnswerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where textAnswer equals to
        defaultExamAnswerFiltering("textAnswer.equals=" + DEFAULT_TEXT_ANSWER, "textAnswer.equals=" + UPDATED_TEXT_ANSWER);
    }

    @Test
    @Transactional
    void getAllExamAnswersByTextAnswerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where textAnswer in
        defaultExamAnswerFiltering(
            "textAnswer.in=" + DEFAULT_TEXT_ANSWER + "," + UPDATED_TEXT_ANSWER,
            "textAnswer.in=" + UPDATED_TEXT_ANSWER
        );
    }

    @Test
    @Transactional
    void getAllExamAnswersByTextAnswerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where textAnswer is not null
        defaultExamAnswerFiltering("textAnswer.specified=true", "textAnswer.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAnswersByTextAnswerContainsSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where textAnswer contains
        defaultExamAnswerFiltering("textAnswer.contains=" + DEFAULT_TEXT_ANSWER, "textAnswer.contains=" + UPDATED_TEXT_ANSWER);
    }

    @Test
    @Transactional
    void getAllExamAnswersByTextAnswerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where textAnswer does not contain
        defaultExamAnswerFiltering("textAnswer.doesNotContain=" + UPDATED_TEXT_ANSWER, "textAnswer.doesNotContain=" + DEFAULT_TEXT_ANSWER);
    }

    @Test
    @Transactional
    void getAllExamAnswersByIsCorrectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where isCorrect equals to
        defaultExamAnswerFiltering("isCorrect.equals=" + DEFAULT_IS_CORRECT, "isCorrect.equals=" + UPDATED_IS_CORRECT);
    }

    @Test
    @Transactional
    void getAllExamAnswersByIsCorrectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where isCorrect in
        defaultExamAnswerFiltering("isCorrect.in=" + DEFAULT_IS_CORRECT + "," + UPDATED_IS_CORRECT, "isCorrect.in=" + UPDATED_IS_CORRECT);
    }

    @Test
    @Transactional
    void getAllExamAnswersByIsCorrectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where isCorrect is not null
        defaultExamAnswerFiltering("isCorrect.specified=true", "isCorrect.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score equals to
        defaultExamAnswerFiltering("score.equals=" + DEFAULT_SCORE, "score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score in
        defaultExamAnswerFiltering("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE, "score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score is not null
        defaultExamAnswerFiltering("score.specified=true", "score.specified=false");
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score is greater than or equal to
        defaultExamAnswerFiltering("score.greaterThanOrEqual=" + DEFAULT_SCORE, "score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score is less than or equal to
        defaultExamAnswerFiltering("score.lessThanOrEqual=" + DEFAULT_SCORE, "score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score is less than
        defaultExamAnswerFiltering("score.lessThan=" + UPDATED_SCORE, "score.lessThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        // Get all the examAnswerList where score is greater than
        defaultExamAnswerFiltering("score.greaterThan=" + SMALLER_SCORE, "score.greaterThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllExamAnswersByAttemptIsEqualToSomething() throws Exception {
        ExamAttempt attempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAnswerRepository.saveAndFlush(examAnswer);
            attempt = ExamAttemptResourceIT.createEntity(em);
        } else {
            attempt = TestUtil.findAll(em, ExamAttempt.class).getFirst();
        }
        em.persist(attempt);
        em.flush();
        examAnswer.setAttempt(attempt);
        examAnswerRepository.saveAndFlush(examAnswer);
        Long attemptId = attempt.getId();
        // Get all the examAnswerList where attempt equals to attemptId
        defaultExamAnswerShouldBeFound("attemptId.equals=" + attemptId);

        // Get all the examAnswerList where attempt equals to (attemptId + 1)
        defaultExamAnswerShouldNotBeFound("attemptId.equals=" + (attemptId + 1));
    }

    @Test
    @Transactional
    void getAllExamAnswersByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            examAnswerRepository.saveAndFlush(examAnswer);
            question = QuestionResourceIT.createEntity();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        em.persist(question);
        em.flush();
        examAnswer.setQuestion(question);
        examAnswerRepository.saveAndFlush(examAnswer);
        Long questionId = question.getId();
        // Get all the examAnswerList where question equals to questionId
        defaultExamAnswerShouldBeFound("questionId.equals=" + questionId);

        // Get all the examAnswerList where question equals to (questionId + 1)
        defaultExamAnswerShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    @Test
    @Transactional
    void getAllExamAnswersByOptionIsEqualToSomething() throws Exception {
        QuestionOption option;
        if (TestUtil.findAll(em, QuestionOption.class).isEmpty()) {
            examAnswerRepository.saveAndFlush(examAnswer);
            option = QuestionOptionResourceIT.createEntity(em);
        } else {
            option = TestUtil.findAll(em, QuestionOption.class).getFirst();
        }
        em.persist(option);
        em.flush();
        examAnswer.setOption(option);
        examAnswerRepository.saveAndFlush(examAnswer);
        Long optionId = option.getId();
        // Get all the examAnswerList where option equals to optionId
        defaultExamAnswerShouldBeFound("optionId.equals=" + optionId);

        // Get all the examAnswerList where option equals to (optionId + 1)
        defaultExamAnswerShouldNotBeFound("optionId.equals=" + (optionId + 1));
    }

    private void defaultExamAnswerFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultExamAnswerShouldBeFound(shouldBeFound);
        defaultExamAnswerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExamAnswerShouldBeFound(String filter) throws Exception {
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].textAnswer").value(hasItem(DEFAULT_TEXT_ANSWER)))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))));

        // Check, that the count call also returns 1
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExamAnswerShouldNotBeFound(String filter) throws Exception {
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExamAnswerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExamAnswer() throws Exception {
        // Get the examAnswer
        restExamAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamAnswer() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAnswer
        ExamAnswer updatedExamAnswer = examAnswerRepository.findById(examAnswer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExamAnswer are not directly saved in db
        em.detach(updatedExamAnswer);
        updatedExamAnswer.textAnswer(UPDATED_TEXT_ANSWER).isCorrect(UPDATED_IS_CORRECT).score(UPDATED_SCORE);
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(updatedExamAnswer);

        restExamAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAnswerDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamAnswerToMatchAllProperties(updatedExamAnswer);
    }

    @Test
    @Transactional
    void putNonExistingExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAnswer using partial update
        ExamAnswer partialUpdatedExamAnswer = new ExamAnswer();
        partialUpdatedExamAnswer.setId(examAnswer.getId());

        restExamAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAnswer))
            )
            .andExpect(status().isOk());

        // Validate the ExamAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAnswerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExamAnswer, examAnswer),
            getPersistedExamAnswer(examAnswer)
        );
    }

    @Test
    @Transactional
    void fullUpdateExamAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAnswer using partial update
        ExamAnswer partialUpdatedExamAnswer = new ExamAnswer();
        partialUpdatedExamAnswer.setId(examAnswer.getId());

        partialUpdatedExamAnswer.textAnswer(UPDATED_TEXT_ANSWER).isCorrect(UPDATED_IS_CORRECT).score(UPDATED_SCORE);

        restExamAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAnswer))
            )
            .andExpect(status().isOk());

        // Validate the ExamAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAnswerUpdatableFieldsEquals(partialUpdatedExamAnswer, getPersistedExamAnswer(partialUpdatedExamAnswer));
    }

    @Test
    @Transactional
    void patchNonExistingExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examAnswerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAnswer.setId(longCount.incrementAndGet());

        // Create the ExamAnswer
        ExamAnswerDTO examAnswerDTO = examAnswerMapper.toDto(examAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAnswerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamAnswer() throws Exception {
        // Initialize the database
        insertedExamAnswer = examAnswerRepository.saveAndFlush(examAnswer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the examAnswer
        restExamAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, examAnswer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examAnswerRepository.count();
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

    protected ExamAnswer getPersistedExamAnswer(ExamAnswer examAnswer) {
        return examAnswerRepository.findById(examAnswer.getId()).orElseThrow();
    }

    protected void assertPersistedExamAnswerToMatchAllProperties(ExamAnswer expectedExamAnswer) {
        assertExamAnswerAllPropertiesEquals(expectedExamAnswer, getPersistedExamAnswer(expectedExamAnswer));
    }

    protected void assertPersistedExamAnswerToMatchUpdatableProperties(ExamAnswer expectedExamAnswer) {
        assertExamAnswerAllUpdatablePropertiesEquals(expectedExamAnswer, getPersistedExamAnswer(expectedExamAnswer));
    }
}
