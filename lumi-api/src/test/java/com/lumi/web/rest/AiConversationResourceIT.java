package com.lumi.web.rest;

import static com.lumi.domain.AiConversationAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.AiConversation;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.repository.AiConversationRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.AiConversationService;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.mapper.AiConversationMapper;
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
 * Integration tests for the {@link AiConversationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AiConversationResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/ai-conversations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AiConversationRepository aiConversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AiConversationRepository aiConversationRepositoryMock;

    @Autowired
    private AiConversationMapper aiConversationMapper;

    @Mock
    private AiConversationService aiConversationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAiConversationMockMvc;

    private AiConversation aiConversation;

    private AiConversation insertedAiConversation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiConversation createEntity(EntityManager em) {
        AiConversation aiConversation = new AiConversation().type(DEFAULT_TYPE).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        aiConversation.setUser(user);
        return aiConversation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiConversation createUpdatedEntity(EntityManager em) {
        AiConversation updatedAiConversation = new AiConversation().type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAiConversation.setUser(user);
        return updatedAiConversation;
    }

    @BeforeEach
    void initTest() {
        aiConversation = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAiConversation != null) {
            aiConversationRepository.delete(insertedAiConversation);
            insertedAiConversation = null;
        }
    }

    @Test
    @Transactional
    void createAiConversation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);
        var returnedAiConversationDTO = om.readValue(
            restAiConversationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiConversationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AiConversationDTO.class
        );

        // Validate the AiConversation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAiConversation = aiConversationMapper.toEntity(returnedAiConversationDTO);
        assertAiConversationUpdatableFieldsEquals(returnedAiConversation, getPersistedAiConversation(returnedAiConversation));

        insertedAiConversation = returnedAiConversation;
    }

    @Test
    @Transactional
    void createAiConversationWithExistingId() throws Exception {
        // Create the AiConversation with an existing ID
        aiConversation.setId(1L);
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAiConversationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiConversationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiConversation.setType(null);

        // Create the AiConversation, which fails.
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        restAiConversationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiConversationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAiConversations() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiConversation.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAiConversationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(aiConversationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAiConversationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(aiConversationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAiConversationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(aiConversationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAiConversationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(aiConversationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAiConversation() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get the aiConversation
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL_ID, aiConversation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aiConversation.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getAiConversationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        Long id = aiConversation.getId();

        defaultAiConversationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAiConversationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAiConversationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAiConversationsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where type equals to
        defaultAiConversationFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllAiConversationsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where type in
        defaultAiConversationFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllAiConversationsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where type is not null
        defaultAiConversationFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllAiConversationsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where type contains
        defaultAiConversationFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllAiConversationsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where type does not contain
        defaultAiConversationFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllAiConversationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where createdAt equals to
        defaultAiConversationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAiConversationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where createdAt in
        defaultAiConversationFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllAiConversationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        // Get all the aiConversationList where createdAt is not null
        defaultAiConversationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAiConversationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            aiConversationRepository.saveAndFlush(aiConversation);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(user);
        em.flush();
        aiConversation.setUser(user);
        aiConversationRepository.saveAndFlush(aiConversation);
        Long userId = user.getId();
        // Get all the aiConversationList where user equals to userId
        defaultAiConversationShouldBeFound("userId.equals=" + userId);

        // Get all the aiConversationList where user equals to (userId + 1)
        defaultAiConversationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllAiConversationsByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            aiConversationRepository.saveAndFlush(aiConversation);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        aiConversation.setLesson(lesson);
        aiConversationRepository.saveAndFlush(aiConversation);
        Long lessonId = lesson.getId();
        // Get all the aiConversationList where lesson equals to lessonId
        defaultAiConversationShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the aiConversationList where lesson equals to (lessonId + 1)
        defaultAiConversationShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultAiConversationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAiConversationShouldBeFound(shouldBeFound);
        defaultAiConversationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAiConversationShouldBeFound(String filter) throws Exception {
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiConversation.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAiConversationShouldNotBeFound(String filter) throws Exception {
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAiConversationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAiConversation() throws Exception {
        // Get the aiConversation
        restAiConversationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAiConversation() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiConversation
        AiConversation updatedAiConversation = aiConversationRepository.findById(aiConversation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAiConversation are not directly saved in db
        em.detach(updatedAiConversation);
        updatedAiConversation.type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(updatedAiConversation);

        restAiConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiConversationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiConversationDTO))
            )
            .andExpect(status().isOk());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAiConversationToMatchAllProperties(updatedAiConversation);
    }

    @Test
    @Transactional
    void putNonExistingAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiConversationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiConversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiConversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiConversationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAiConversationWithPatch() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiConversation using partial update
        AiConversation partialUpdatedAiConversation = new AiConversation();
        partialUpdatedAiConversation.setId(aiConversation.getId());

        partialUpdatedAiConversation.type(UPDATED_TYPE);

        restAiConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiConversation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiConversation))
            )
            .andExpect(status().isOk());

        // Validate the AiConversation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiConversationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAiConversation, aiConversation),
            getPersistedAiConversation(aiConversation)
        );
    }

    @Test
    @Transactional
    void fullUpdateAiConversationWithPatch() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiConversation using partial update
        AiConversation partialUpdatedAiConversation = new AiConversation();
        partialUpdatedAiConversation.setId(aiConversation.getId());

        partialUpdatedAiConversation.type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);

        restAiConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiConversation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiConversation))
            )
            .andExpect(status().isOk());

        // Validate the AiConversation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiConversationUpdatableFieldsEquals(partialUpdatedAiConversation, getPersistedAiConversation(partialUpdatedAiConversation));
    }

    @Test
    @Transactional
    void patchNonExistingAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aiConversationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiConversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiConversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAiConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiConversation.setId(longCount.incrementAndGet());

        // Create the AiConversation
        AiConversationDTO aiConversationDTO = aiConversationMapper.toDto(aiConversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiConversationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aiConversationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiConversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAiConversation() throws Exception {
        // Initialize the database
        insertedAiConversation = aiConversationRepository.saveAndFlush(aiConversation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aiConversation
        restAiConversationMockMvc
            .perform(delete(ENTITY_API_URL_ID, aiConversation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return aiConversationRepository.count();
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

    protected AiConversation getPersistedAiConversation(AiConversation aiConversation) {
        return aiConversationRepository.findById(aiConversation.getId()).orElseThrow();
    }

    protected void assertPersistedAiConversationToMatchAllProperties(AiConversation expectedAiConversation) {
        assertAiConversationAllPropertiesEquals(expectedAiConversation, getPersistedAiConversation(expectedAiConversation));
    }

    protected void assertPersistedAiConversationToMatchUpdatableProperties(AiConversation expectedAiConversation) {
        assertAiConversationAllUpdatablePropertiesEquals(expectedAiConversation, getPersistedAiConversation(expectedAiConversation));
    }
}
