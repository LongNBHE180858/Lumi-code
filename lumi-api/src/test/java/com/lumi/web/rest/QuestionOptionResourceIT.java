package com.lumi.web.rest;

import static com.lumi.domain.QuestionOptionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionOption;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.service.dto.QuestionOptionDTO;
import com.lumi.service.mapper.QuestionOptionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link QuestionOptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionOptionResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_CORRECT = false;
    private static final Boolean UPDATED_IS_CORRECT = true;

    private static final String ENTITY_API_URL = "/api/question-options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionOptionMockMvc;

    private QuestionOption questionOption;

    private QuestionOption insertedQuestionOption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionOption createEntity(EntityManager em) {
        QuestionOption questionOption = new QuestionOption().content(DEFAULT_CONTENT).isCorrect(DEFAULT_IS_CORRECT);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        questionOption.setQuestion(question);
        return questionOption;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionOption createUpdatedEntity(EntityManager em) {
        QuestionOption updatedQuestionOption = new QuestionOption().content(UPDATED_CONTENT).isCorrect(UPDATED_IS_CORRECT);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createUpdatedEntity();
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        updatedQuestionOption.setQuestion(question);
        return updatedQuestionOption;
    }

    @BeforeEach
    void initTest() {
        questionOption = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestionOption != null) {
            questionOptionRepository.delete(insertedQuestionOption);
            insertedQuestionOption = null;
        }
    }

    @Test
    @Transactional
    void createQuestionOption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);
        var returnedQuestionOptionDTO = om.readValue(
            restQuestionOptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionOptionDTO.class
        );

        // Validate the QuestionOption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestionOption = questionOptionMapper.toEntity(returnedQuestionOptionDTO);
        assertQuestionOptionUpdatableFieldsEquals(returnedQuestionOption, getPersistedQuestionOption(returnedQuestionOption));

        insertedQuestionOption = returnedQuestionOption;
    }

    @Test
    @Transactional
    void createQuestionOptionWithExistingId() throws Exception {
        // Create the QuestionOption with an existing ID
        questionOption.setId(1L);
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        questionOption.setContent(null);

        // Create the QuestionOption, which fails.
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        restQuestionOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuestionOptions() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionOption.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT)));
    }

    @Test
    @Transactional
    void getQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get the questionOption
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL_ID, questionOption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionOption.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.isCorrect").value(DEFAULT_IS_CORRECT));
    }

    @Test
    @Transactional
    void getQuestionOptionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        Long id = questionOption.getId();

        defaultQuestionOptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuestionOptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuestionOptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where content equals to
        defaultQuestionOptionFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where content in
        defaultQuestionOptionFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where content is not null
        defaultQuestionOptionFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where content contains
        defaultQuestionOptionFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where content does not contain
        defaultQuestionOptionFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByIsCorrectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where isCorrect equals to
        defaultQuestionOptionFiltering("isCorrect.equals=" + DEFAULT_IS_CORRECT, "isCorrect.equals=" + UPDATED_IS_CORRECT);
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByIsCorrectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where isCorrect in
        defaultQuestionOptionFiltering(
            "isCorrect.in=" + DEFAULT_IS_CORRECT + "," + UPDATED_IS_CORRECT,
            "isCorrect.in=" + UPDATED_IS_CORRECT
        );
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByIsCorrectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList where isCorrect is not null
        defaultQuestionOptionFiltering("isCorrect.specified=true", "isCorrect.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionOptionsByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            questionOptionRepository.saveAndFlush(questionOption);
            question = QuestionResourceIT.createEntity();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        em.persist(question);
        em.flush();
        questionOption.setQuestion(question);
        questionOptionRepository.saveAndFlush(questionOption);
        Long questionId = question.getId();
        // Get all the questionOptionList where question equals to questionId
        defaultQuestionOptionShouldBeFound("questionId.equals=" + questionId);

        // Get all the questionOptionList where question equals to (questionId + 1)
        defaultQuestionOptionShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    private void defaultQuestionOptionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuestionOptionShouldBeFound(shouldBeFound);
        defaultQuestionOptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuestionOptionShouldBeFound(String filter) throws Exception {
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionOption.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT)));

        // Check, that the count call also returns 1
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuestionOptionShouldNotBeFound(String filter) throws Exception {
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuestionOption() throws Exception {
        // Get the questionOption
        restQuestionOptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption
        QuestionOption updatedQuestionOption = questionOptionRepository.findById(questionOption.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestionOption are not directly saved in db
        em.detach(updatedQuestionOption);
        updatedQuestionOption.content(UPDATED_CONTENT).isCorrect(UPDATED_IS_CORRECT);
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(updatedQuestionOption);

        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionOptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionOptionToMatchAllProperties(updatedQuestionOption);
    }

    @Test
    @Transactional
    void putNonExistingQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionOptionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption using partial update
        QuestionOption partialUpdatedQuestionOption = new QuestionOption();
        partialUpdatedQuestionOption.setId(questionOption.getId());

        partialUpdatedQuestionOption.isCorrect(UPDATED_IS_CORRECT);

        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionOption))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionOptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuestionOption, questionOption),
            getPersistedQuestionOption(questionOption)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuestionOptionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption using partial update
        QuestionOption partialUpdatedQuestionOption = new QuestionOption();
        partialUpdatedQuestionOption.setId(questionOption.getId());

        partialUpdatedQuestionOption.content(UPDATED_CONTENT).isCorrect(UPDATED_IS_CORRECT);

        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionOption))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionOptionUpdatableFieldsEquals(partialUpdatedQuestionOption, getPersistedQuestionOption(partialUpdatedQuestionOption));
    }

    @Test
    @Transactional
    void patchNonExistingQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionOptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // Create the QuestionOption
        QuestionOptionDTO questionOptionDTO = questionOptionMapper.toDto(questionOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionOptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the questionOption
        restQuestionOptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionOption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionOptionRepository.count();
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

    protected QuestionOption getPersistedQuestionOption(QuestionOption questionOption) {
        return questionOptionRepository.findById(questionOption.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionOptionToMatchAllProperties(QuestionOption expectedQuestionOption) {
        assertQuestionOptionAllPropertiesEquals(expectedQuestionOption, getPersistedQuestionOption(expectedQuestionOption));
    }

    protected void assertPersistedQuestionOptionToMatchUpdatableProperties(QuestionOption expectedQuestionOption) {
        assertQuestionOptionAllUpdatablePropertiesEquals(expectedQuestionOption, getPersistedQuestionOption(expectedQuestionOption));
    }
}
