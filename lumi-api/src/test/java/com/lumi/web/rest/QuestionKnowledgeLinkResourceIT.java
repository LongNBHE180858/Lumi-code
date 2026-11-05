package com.lumi.web.rest;

import static com.lumi.domain.QuestionKnowledgeLinkAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionKnowledgeLink;
import com.lumi.repository.QuestionKnowledgeLinkRepository;
import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import com.lumi.service.mapper.QuestionKnowledgeLinkMapper;
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
 * Integration tests for the {@link QuestionKnowledgeLinkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionKnowledgeLinkResourceIT {

    private static final BigDecimal DEFAULT_WEIGHT = new BigDecimal(1);
    private static final BigDecimal UPDATED_WEIGHT = new BigDecimal(2);
    private static final BigDecimal SMALLER_WEIGHT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/question-knowledge-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository;

    @Autowired
    private QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionKnowledgeLinkMockMvc;

    private QuestionKnowledgeLink questionKnowledgeLink;

    private QuestionKnowledgeLink insertedQuestionKnowledgeLink;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionKnowledgeLink createEntity(EntityManager em) {
        QuestionKnowledgeLink questionKnowledgeLink = new QuestionKnowledgeLink().weight(DEFAULT_WEIGHT);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionTestFixtures.createEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        questionKnowledgeLink.setQuestion(question);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeTestFixtures.createEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        questionKnowledgeLink.setKnowledgeNode(knowledgeNode);
        return questionKnowledgeLink;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionKnowledgeLink createUpdatedEntity(EntityManager em) {
        QuestionKnowledgeLink updatedQuestionKnowledgeLink = new QuestionKnowledgeLink().weight(UPDATED_WEIGHT);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionTestFixtures.createUpdatedEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        updatedQuestionKnowledgeLink.setQuestion(question);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeTestFixtures.createUpdatedEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        updatedQuestionKnowledgeLink.setKnowledgeNode(knowledgeNode);
        return updatedQuestionKnowledgeLink;
    }

    @BeforeEach
    void initTest() {
        questionKnowledgeLink = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestionKnowledgeLink != null) {
            questionKnowledgeLinkRepository.delete(insertedQuestionKnowledgeLink);
            insertedQuestionKnowledgeLink = null;
        }
    }

    @Test
    @Transactional
    void createQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);
        var returnedQuestionKnowledgeLinkDTO = om.readValue(
            restQuestionKnowledgeLinkMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionKnowledgeLinkDTO.class
        );

        // Validate the QuestionKnowledgeLink in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestionKnowledgeLink = questionKnowledgeLinkMapper.toEntity(returnedQuestionKnowledgeLinkDTO);
        assertQuestionKnowledgeLinkUpdatableFieldsEquals(
            returnedQuestionKnowledgeLink,
            getPersistedQuestionKnowledgeLink(returnedQuestionKnowledgeLink)
        );

        insertedQuestionKnowledgeLink = returnedQuestionKnowledgeLink;
    }

    @Test
    @Transactional
    void createQuestionKnowledgeLinkWithExistingId() throws Exception {
        // Create the QuestionKnowledgeLink with an existing ID
        questionKnowledgeLink.setId(1L);
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionKnowledgeLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionKnowledgeLinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWeightIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        questionKnowledgeLink.setWeight(null);

        // Create the QuestionKnowledgeLink, which fails.
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        restQuestionKnowledgeLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionKnowledgeLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinks() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionKnowledgeLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(sameNumber(DEFAULT_WEIGHT))));
    }

    @Test
    @Transactional
    void getQuestionKnowledgeLink() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get the questionKnowledgeLink
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL_ID, questionKnowledgeLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionKnowledgeLink.getId().intValue()))
            .andExpect(jsonPath("$.weight").value(sameNumber(DEFAULT_WEIGHT)));
    }

    @Test
    @Transactional
    void getQuestionKnowledgeLinksByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        Long id = questionKnowledgeLink.getId();

        defaultQuestionKnowledgeLinkFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuestionKnowledgeLinkFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuestionKnowledgeLinkFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight equals to
        defaultQuestionKnowledgeLinkFiltering("weight.equals=" + DEFAULT_WEIGHT, "weight.equals=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight in
        defaultQuestionKnowledgeLinkFiltering("weight.in=" + DEFAULT_WEIGHT + "," + UPDATED_WEIGHT, "weight.in=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight is not null
        defaultQuestionKnowledgeLinkFiltering("weight.specified=true", "weight.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight is greater than or equal to
        defaultQuestionKnowledgeLinkFiltering("weight.greaterThanOrEqual=" + DEFAULT_WEIGHT, "weight.greaterThanOrEqual=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight is less than or equal to
        defaultQuestionKnowledgeLinkFiltering("weight.lessThanOrEqual=" + DEFAULT_WEIGHT, "weight.lessThanOrEqual=" + SMALLER_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight is less than
        defaultQuestionKnowledgeLinkFiltering("weight.lessThan=" + UPDATED_WEIGHT, "weight.lessThan=" + DEFAULT_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByWeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        // Get all the questionKnowledgeLinkList where weight is greater than
        defaultQuestionKnowledgeLinkFiltering("weight.greaterThan=" + SMALLER_WEIGHT, "weight.greaterThan=" + DEFAULT_WEIGHT);
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);
            question = QuestionTestFixtures.createEntity(em);
        } else {
            question = TestUtil.findAll(em, Question.class).getFirst();
        }
        em.persist(question);
        em.flush();
        questionKnowledgeLink.setQuestion(question);
        questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);
        Long questionId = question.getId();
        // Get all the questionKnowledgeLinkList where question equals to questionId
        defaultQuestionKnowledgeLinkShouldBeFound("questionId.equals=" + questionId);

        // Get all the questionKnowledgeLinkList where question equals to (questionId + 1)
        defaultQuestionKnowledgeLinkShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    @Test
    @Transactional
    void getAllQuestionKnowledgeLinksByKnowledgeNodeIsEqualToSomething() throws Exception {
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);
            knowledgeNode = KnowledgeNodeTestFixtures.createEntity();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        em.persist(knowledgeNode);
        em.flush();
        questionKnowledgeLink.setKnowledgeNode(knowledgeNode);
        questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);
        Long knowledgeNodeId = knowledgeNode.getId();
        // Get all the questionKnowledgeLinkList where knowledgeNode equals to knowledgeNodeId
        defaultQuestionKnowledgeLinkShouldBeFound("knowledgeNodeId.equals=" + knowledgeNodeId);

        // Get all the questionKnowledgeLinkList where knowledgeNode equals to (knowledgeNodeId + 1)
        defaultQuestionKnowledgeLinkShouldNotBeFound("knowledgeNodeId.equals=" + (knowledgeNodeId + 1));
    }

    private void defaultQuestionKnowledgeLinkFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuestionKnowledgeLinkShouldBeFound(shouldBeFound);
        defaultQuestionKnowledgeLinkShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuestionKnowledgeLinkShouldBeFound(String filter) throws Exception {
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionKnowledgeLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(sameNumber(DEFAULT_WEIGHT))));

        // Check, that the count call also returns 1
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuestionKnowledgeLinkShouldNotBeFound(String filter) throws Exception {
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuestionKnowledgeLinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuestionKnowledgeLink() throws Exception {
        // Get the questionKnowledgeLink
        restQuestionKnowledgeLinkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestionKnowledgeLink() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionKnowledgeLink
        QuestionKnowledgeLink updatedQuestionKnowledgeLink = questionKnowledgeLinkRepository
            .findById(questionKnowledgeLink.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedQuestionKnowledgeLink are not directly saved in db
        em.detach(updatedQuestionKnowledgeLink);
        updatedQuestionKnowledgeLink.weight(UPDATED_WEIGHT);
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(updatedQuestionKnowledgeLink);

        restQuestionKnowledgeLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionKnowledgeLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionKnowledgeLinkToMatchAllProperties(updatedQuestionKnowledgeLink);
    }

    @Test
    @Transactional
    void putNonExistingQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionKnowledgeLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionKnowledgeLinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionKnowledgeLinkWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionKnowledgeLink using partial update
        QuestionKnowledgeLink partialUpdatedQuestionKnowledgeLink = new QuestionKnowledgeLink();
        partialUpdatedQuestionKnowledgeLink.setId(questionKnowledgeLink.getId());

        restQuestionKnowledgeLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionKnowledgeLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionKnowledgeLink))
            )
            .andExpect(status().isOk());

        // Validate the QuestionKnowledgeLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionKnowledgeLinkUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuestionKnowledgeLink, questionKnowledgeLink),
            getPersistedQuestionKnowledgeLink(questionKnowledgeLink)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuestionKnowledgeLinkWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionKnowledgeLink using partial update
        QuestionKnowledgeLink partialUpdatedQuestionKnowledgeLink = new QuestionKnowledgeLink();
        partialUpdatedQuestionKnowledgeLink.setId(questionKnowledgeLink.getId());

        partialUpdatedQuestionKnowledgeLink.weight(UPDATED_WEIGHT);

        restQuestionKnowledgeLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionKnowledgeLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionKnowledgeLink))
            )
            .andExpect(status().isOk());

        // Validate the QuestionKnowledgeLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionKnowledgeLinkUpdatableFieldsEquals(
            partialUpdatedQuestionKnowledgeLink,
            getPersistedQuestionKnowledgeLink(partialUpdatedQuestionKnowledgeLink)
        );
    }

    @Test
    @Transactional
    void patchNonExistingQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionKnowledgeLinkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestionKnowledgeLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionKnowledgeLink.setId(longCount.incrementAndGet());

        // Create the QuestionKnowledgeLink
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionKnowledgeLinkMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionKnowledgeLinkDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionKnowledgeLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestionKnowledgeLink() throws Exception {
        // Initialize the database
        insertedQuestionKnowledgeLink = questionKnowledgeLinkRepository.saveAndFlush(questionKnowledgeLink);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the questionKnowledgeLink
        restQuestionKnowledgeLinkMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionKnowledgeLink.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionKnowledgeLinkRepository.count();
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

    protected QuestionKnowledgeLink getPersistedQuestionKnowledgeLink(QuestionKnowledgeLink questionKnowledgeLink) {
        return questionKnowledgeLinkRepository.findById(questionKnowledgeLink.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionKnowledgeLinkToMatchAllProperties(QuestionKnowledgeLink expectedQuestionKnowledgeLink) {
        assertQuestionKnowledgeLinkAllPropertiesEquals(
            expectedQuestionKnowledgeLink,
            getPersistedQuestionKnowledgeLink(expectedQuestionKnowledgeLink)
        );
    }

    protected void assertPersistedQuestionKnowledgeLinkToMatchUpdatableProperties(QuestionKnowledgeLink expectedQuestionKnowledgeLink) {
        assertQuestionKnowledgeLinkAllUpdatablePropertiesEquals(
            expectedQuestionKnowledgeLink,
            getPersistedQuestionKnowledgeLink(expectedQuestionKnowledgeLink)
        );
    }
}
