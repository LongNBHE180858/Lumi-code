package com.lumi.web.rest;

import static com.lumi.domain.QuestionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionBank;
import com.lumi.repository.QuestionRepository;
import com.lumi.service.QuestionService;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.mapper.QuestionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link QuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuestionResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DIFFICULTY = 1;
    private static final Integer UPDATED_DIFFICULTY = 2;
    private static final Integer SMALLER_DIFFICULTY = 1 - 1;

    private static final String DEFAULT_EXPLANATION = "AAAAAAAAAA";
    private static final String UPDATED_EXPLANATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String ENTITY_API_URL = "/api/questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionRepository questionRepository;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Autowired
    private QuestionMapper questionMapper;

    @Mock
    private QuestionService questionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionMockMvc;

    private Question question;

    private Question insertedQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createEntity() {
        return new Question()
            .content(DEFAULT_CONTENT)
            .type(DEFAULT_TYPE)
            .difficulty(DEFAULT_DIFFICULTY)
            .explanation(DEFAULT_EXPLANATION)
            .isDeleted(DEFAULT_IS_DELETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createUpdatedEntity() {
        return new Question()
            .content(UPDATED_CONTENT)
            .type(UPDATED_TYPE)
            .difficulty(UPDATED_DIFFICULTY)
            .explanation(UPDATED_EXPLANATION)
            .isDeleted(UPDATED_IS_DELETED);
    }

    @BeforeEach
    void initTest() {
        question = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestion != null) {
            questionRepository.delete(insertedQuestion);
            insertedQuestion = null;
        }
    }

    @Test
    @Transactional
    void createQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);
        var returnedQuestionDTO = om.readValue(
            restQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionDTO.class
        );

        // Validate the Question in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestion = questionMapper.toEntity(returnedQuestionDTO);
        assertQuestionUpdatableFieldsEquals(returnedQuestion, getPersistedQuestion(returnedQuestion));

        insertedQuestion = returnedQuestion;
    }

    @Test
    @Transactional
    void createQuestionWithExistingId() throws Exception {
        // Create the Question with an existing ID
        question.setId(1L);
        QuestionDTO questionDTO = questionMapper.toDto(question);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        question.setContent(null);

        // Create the Question, which fails.
        QuestionDTO questionDTO = questionMapper.toDto(question);

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        question.setType(null);

        // Create the Question, which fails.
        QuestionDTO questionDTO = questionMapper.toDto(question);

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuestions() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY)))
            .andExpect(jsonPath("$.[*].explanation").value(hasItem(DEFAULT_EXPLANATION)))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(questionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(questionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(questionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(questionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get the question
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(question.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.difficulty").value(DEFAULT_DIFFICULTY))
            .andExpect(jsonPath("$.explanation").value(DEFAULT_EXPLANATION))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED));
    }

    @Test
    @Transactional
    void getQuestionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        Long id = question.getId();

        defaultQuestionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuestionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuestionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuestionsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where content equals to
        defaultQuestionFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where content in
        defaultQuestionFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where content is not null
        defaultQuestionFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionsByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where content contains
        defaultQuestionFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where content does not contain
        defaultQuestionFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type equals to
        defaultQuestionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllQuestionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type in
        defaultQuestionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllQuestionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type is not null
        defaultQuestionFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type contains
        defaultQuestionFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllQuestionsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type does not contain
        defaultQuestionFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty equals to
        defaultQuestionFiltering("difficulty.equals=" + DEFAULT_DIFFICULTY, "difficulty.equals=" + UPDATED_DIFFICULTY);
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty in
        defaultQuestionFiltering("difficulty.in=" + DEFAULT_DIFFICULTY + "," + UPDATED_DIFFICULTY, "difficulty.in=" + UPDATED_DIFFICULTY);
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty is not null
        defaultQuestionFiltering("difficulty.specified=true", "difficulty.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty is greater than or equal to
        defaultQuestionFiltering(
            "difficulty.greaterThanOrEqual=" + DEFAULT_DIFFICULTY,
            "difficulty.greaterThanOrEqual=" + (DEFAULT_DIFFICULTY + 1)
        );
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty is less than or equal to
        defaultQuestionFiltering("difficulty.lessThanOrEqual=" + DEFAULT_DIFFICULTY, "difficulty.lessThanOrEqual=" + SMALLER_DIFFICULTY);
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty is less than
        defaultQuestionFiltering("difficulty.lessThan=" + (DEFAULT_DIFFICULTY + 1), "difficulty.lessThan=" + DEFAULT_DIFFICULTY);
    }

    @Test
    @Transactional
    void getAllQuestionsByDifficultyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where difficulty is greater than
        defaultQuestionFiltering("difficulty.greaterThan=" + SMALLER_DIFFICULTY, "difficulty.greaterThan=" + DEFAULT_DIFFICULTY);
    }

    @Test
    @Transactional
    void getAllQuestionsByExplanationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where explanation equals to
        defaultQuestionFiltering("explanation.equals=" + DEFAULT_EXPLANATION, "explanation.equals=" + UPDATED_EXPLANATION);
    }

    @Test
    @Transactional
    void getAllQuestionsByExplanationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where explanation in
        defaultQuestionFiltering(
            "explanation.in=" + DEFAULT_EXPLANATION + "," + UPDATED_EXPLANATION,
            "explanation.in=" + UPDATED_EXPLANATION
        );
    }

    @Test
    @Transactional
    void getAllQuestionsByExplanationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where explanation is not null
        defaultQuestionFiltering("explanation.specified=true", "explanation.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionsByExplanationContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where explanation contains
        defaultQuestionFiltering("explanation.contains=" + DEFAULT_EXPLANATION, "explanation.contains=" + UPDATED_EXPLANATION);
    }

    @Test
    @Transactional
    void getAllQuestionsByExplanationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where explanation does not contain
        defaultQuestionFiltering("explanation.doesNotContain=" + UPDATED_EXPLANATION, "explanation.doesNotContain=" + DEFAULT_EXPLANATION);
    }

    @Test
    @Transactional
    void getAllQuestionsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where isDeleted equals to
        defaultQuestionFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllQuestionsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where isDeleted in
        defaultQuestionFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllQuestionsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where isDeleted is not null
        defaultQuestionFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionsByBankIsEqualToSomething() throws Exception {
        QuestionBank bank;
        if (TestUtil.findAll(em, QuestionBank.class).isEmpty()) {
            questionRepository.saveAndFlush(question);
            bank = QuestionBankResourceIT.createEntity(em);
        } else {
            bank = TestUtil.findAll(em, QuestionBank.class).getFirst();
        }
        em.persist(bank);
        em.flush();
        question.setBank(bank);
        questionRepository.saveAndFlush(question);
        Long bankId = bank.getId();
        // Get all the questionList where bank equals to bankId
        defaultQuestionShouldBeFound("bankId.equals=" + bankId);

        // Get all the questionList where bank equals to (bankId + 1)
        defaultQuestionShouldNotBeFound("bankId.equals=" + (bankId + 1));
    }

    private void defaultQuestionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuestionShouldBeFound(shouldBeFound);
        defaultQuestionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuestionShouldBeFound(String filter) throws Exception {
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY)))
            .andExpect(jsonPath("$.[*].explanation").value(hasItem(DEFAULT_EXPLANATION)))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));

        // Check, that the count call also returns 1
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuestionShouldNotBeFound(String filter) throws Exception {
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question
        Question updatedQuestion = questionRepository.findById(question.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestion are not directly saved in db
        em.detach(updatedQuestion);
        updatedQuestion
            .content(UPDATED_CONTENT)
            .type(UPDATED_TYPE)
            .difficulty(UPDATED_DIFFICULTY)
            .explanation(UPDATED_EXPLANATION)
            .isDeleted(UPDATED_IS_DELETED);
        QuestionDTO questionDTO = questionMapper.toDto(updatedQuestion);

        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionToMatchAllProperties(updatedQuestion);
    }

    @Test
    @Transactional
    void putNonExistingQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion.type(UPDATED_TYPE).explanation(UPDATED_EXPLANATION).isDeleted(UPDATED_IS_DELETED);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedQuestion, question), getPersistedQuestion(question));
    }

    @Test
    @Transactional
    void fullUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion
            .content(UPDATED_CONTENT)
            .type(UPDATED_TYPE)
            .difficulty(UPDATED_DIFFICULTY)
            .explanation(UPDATED_EXPLANATION)
            .isDeleted(UPDATED_IS_DELETED);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionUpdatableFieldsEquals(partialUpdatedQuestion, getPersistedQuestion(partialUpdatedQuestion));
    }

    @Test
    @Transactional
    void patchNonExistingQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the question
        restQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, question.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionRepository.count();
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

    protected Question getPersistedQuestion(Question question) {
        return questionRepository.findById(question.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionToMatchAllProperties(Question expectedQuestion) {
        assertQuestionAllPropertiesEquals(expectedQuestion, getPersistedQuestion(expectedQuestion));
    }

    protected void assertPersistedQuestionToMatchUpdatableProperties(Question expectedQuestion) {
        assertQuestionAllUpdatablePropertiesEquals(expectedQuestion, getPersistedQuestion(expectedQuestion));
    }
}
