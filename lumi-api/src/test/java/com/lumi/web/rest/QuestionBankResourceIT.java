package com.lumi.web.rest;

import static com.lumi.domain.QuestionBankAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.QuestionBank;
import com.lumi.domain.User;
import com.lumi.repository.QuestionBankRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.QuestionBankService;
import com.lumi.service.dto.QuestionBankDTO;
import com.lumi.service.mapper.QuestionBankMapper;
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
 * Integration tests for the {@link QuestionBankResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuestionBankResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/question-banks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private QuestionBankRepository questionBankRepositoryMock;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Mock
    private QuestionBankService questionBankServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionBankMockMvc;

    private QuestionBank questionBank;

    private QuestionBank insertedQuestionBank;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionBank createEntity(EntityManager em) {
        QuestionBank questionBank = new QuestionBank().name(DEFAULT_NAME);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        questionBank.setInstructor(user);
        return questionBank;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionBank createUpdatedEntity(EntityManager em) {
        QuestionBank updatedQuestionBank = new QuestionBank().name(UPDATED_NAME);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedQuestionBank.setInstructor(user);
        return updatedQuestionBank;
    }

    @BeforeEach
    void initTest() {
        questionBank = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestionBank != null) {
            questionBankRepository.delete(insertedQuestionBank);
            insertedQuestionBank = null;
        }
    }

    @Test
    @Transactional
    void createQuestionBank() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);
        var returnedQuestionBankDTO = om.readValue(
            restQuestionBankMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionBankDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionBankDTO.class
        );

        // Validate the QuestionBank in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestionBank = questionBankMapper.toEntity(returnedQuestionBankDTO);
        assertQuestionBankUpdatableFieldsEquals(returnedQuestionBank, getPersistedQuestionBank(returnedQuestionBank));

        insertedQuestionBank = returnedQuestionBank;
    }

    @Test
    @Transactional
    void createQuestionBankWithExistingId() throws Exception {
        // Create the QuestionBank with an existing ID
        questionBank.setId(1L);
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionBankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionBankDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        questionBank.setName(null);

        // Create the QuestionBank, which fails.
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        restQuestionBankMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionBankDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuestionBanks() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionBank.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionBanksWithEagerRelationshipsIsEnabled() throws Exception {
        when(questionBankServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionBankMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(questionBankServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionBanksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(questionBankServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionBankMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(questionBankRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuestionBank() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get the questionBank
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL_ID, questionBank.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionBank.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getQuestionBanksByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        Long id = questionBank.getId();

        defaultQuestionBankFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuestionBankFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuestionBankFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuestionBanksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList where name equals to
        defaultQuestionBankFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuestionBanksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList where name in
        defaultQuestionBankFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuestionBanksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList where name is not null
        defaultQuestionBankFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllQuestionBanksByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList where name contains
        defaultQuestionBankFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuestionBanksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        // Get all the questionBankList where name does not contain
        defaultQuestionBankFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllQuestionBanksByInstructorIsEqualToSomething() throws Exception {
        User instructor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            questionBankRepository.saveAndFlush(questionBank);
            instructor = UserResourceIT.createEntity();
        } else {
            instructor = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(instructor);
        em.flush();
        questionBank.setInstructor(instructor);
        questionBankRepository.saveAndFlush(questionBank);
        Long instructorId = instructor.getId();
        // Get all the questionBankList where instructor equals to instructorId
        defaultQuestionBankShouldBeFound("instructorId.equals=" + instructorId);

        // Get all the questionBankList where instructor equals to (instructorId + 1)
        defaultQuestionBankShouldNotBeFound("instructorId.equals=" + (instructorId + 1));
    }

    private void defaultQuestionBankFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuestionBankShouldBeFound(shouldBeFound);
        defaultQuestionBankShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuestionBankShouldBeFound(String filter) throws Exception {
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionBank.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuestionBankShouldNotBeFound(String filter) throws Exception {
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuestionBankMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuestionBank() throws Exception {
        // Get the questionBank
        restQuestionBankMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestionBank() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionBank
        QuestionBank updatedQuestionBank = questionBankRepository.findById(questionBank.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestionBank are not directly saved in db
        em.detach(updatedQuestionBank);
        updatedQuestionBank.name(UPDATED_NAME);
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(updatedQuestionBank);

        restQuestionBankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionBankDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionBankDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionBankToMatchAllProperties(updatedQuestionBank);
    }

    @Test
    @Transactional
    void putNonExistingQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionBankDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionBankDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionBankDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionBankDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionBankWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionBank using partial update
        QuestionBank partialUpdatedQuestionBank = new QuestionBank();
        partialUpdatedQuestionBank.setId(questionBank.getId());

        partialUpdatedQuestionBank.name(UPDATED_NAME);

        restQuestionBankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionBank.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionBank))
            )
            .andExpect(status().isOk());

        // Validate the QuestionBank in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionBankUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuestionBank, questionBank),
            getPersistedQuestionBank(questionBank)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuestionBankWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionBank using partial update
        QuestionBank partialUpdatedQuestionBank = new QuestionBank();
        partialUpdatedQuestionBank.setId(questionBank.getId());

        partialUpdatedQuestionBank.name(UPDATED_NAME);

        restQuestionBankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionBank.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionBank))
            )
            .andExpect(status().isOk());

        // Validate the QuestionBank in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionBankUpdatableFieldsEquals(partialUpdatedQuestionBank, getPersistedQuestionBank(partialUpdatedQuestionBank));
    }

    @Test
    @Transactional
    void patchNonExistingQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionBankDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionBankDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionBankDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestionBank() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionBank.setId(longCount.incrementAndGet());

        // Create the QuestionBank
        QuestionBankDTO questionBankDTO = questionBankMapper.toDto(questionBank);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionBankMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionBankDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionBank in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestionBank() throws Exception {
        // Initialize the database
        insertedQuestionBank = questionBankRepository.saveAndFlush(questionBank);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the questionBank
        restQuestionBankMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionBank.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionBankRepository.count();
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

    protected QuestionBank getPersistedQuestionBank(QuestionBank questionBank) {
        return questionBankRepository.findById(questionBank.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionBankToMatchAllProperties(QuestionBank expectedQuestionBank) {
        assertQuestionBankAllPropertiesEquals(expectedQuestionBank, getPersistedQuestionBank(expectedQuestionBank));
    }

    protected void assertPersistedQuestionBankToMatchUpdatableProperties(QuestionBank expectedQuestionBank) {
        assertQuestionBankAllUpdatablePropertiesEquals(expectedQuestionBank, getPersistedQuestionBank(expectedQuestionBank));
    }
}
