package com.lumi.web.rest;

import static com.lumi.domain.AiMessageAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.AiConversation;
import com.lumi.domain.AiMessage;
import com.lumi.repository.AiMessageRepository;
import com.lumi.service.dto.AiMessageDTO;
import com.lumi.service.mapper.AiMessageMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AiMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AiMessageResourceIT {

    private static final String DEFAULT_SENDER_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_ROLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/ai-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AiMessageRepository aiMessageRepository;

    @Autowired
    private AiMessageMapper aiMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAiMessageMockMvc;

    private AiMessage aiMessage;

    private AiMessage insertedAiMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiMessage createEntity(EntityManager em) {
        AiMessage aiMessage = new AiMessage().senderRole(DEFAULT_SENDER_ROLE).content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        AiConversation aiConversation;
        if (TestUtil.findAll(em, AiConversation.class).isEmpty()) {
            aiConversation = AiConversationResourceIT.createEntity(em);
            em.persist(aiConversation);
            em.flush();
        } else {
            aiConversation = TestUtil.findAll(em, AiConversation.class).getFirst();
        }
        aiMessage.setConversation(aiConversation);
        return aiMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiMessage createUpdatedEntity(EntityManager em) {
        AiMessage updatedAiMessage = new AiMessage().senderRole(UPDATED_SENDER_ROLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        AiConversation aiConversation;
        if (TestUtil.findAll(em, AiConversation.class).isEmpty()) {
            aiConversation = AiConversationResourceIT.createUpdatedEntity(em);
            em.persist(aiConversation);
            em.flush();
        } else {
            aiConversation = TestUtil.findAll(em, AiConversation.class).getFirst();
        }
        updatedAiMessage.setConversation(aiConversation);
        return updatedAiMessage;
    }

    @BeforeEach
    void initTest() {
        aiMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAiMessage != null) {
            aiMessageRepository.delete(insertedAiMessage);
            insertedAiMessage = null;
        }
    }

    @Test
    @Transactional
    void createAiMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);
        var returnedAiMessageDTO = om.readValue(
            restAiMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AiMessageDTO.class
        );

        // Validate the AiMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAiMessage = aiMessageMapper.toEntity(returnedAiMessageDTO);
        assertAiMessageUpdatableFieldsEquals(returnedAiMessage, getPersistedAiMessage(returnedAiMessage));

        insertedAiMessage = returnedAiMessage;
    }

    @Test
    @Transactional
    void createAiMessageWithExistingId() throws Exception {
        // Create the AiMessage with an existing ID
        aiMessage.setId(1L);
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAiMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSenderRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiMessage.setSenderRole(null);

        // Create the AiMessage, which fails.
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        restAiMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiMessage.setContent(null);

        // Create the AiMessage, which fails.
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        restAiMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAiMessages() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].senderRole").value(hasItem(DEFAULT_SENDER_ROLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAiMessage() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get the aiMessage
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, aiMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aiMessage.getId().intValue()))
            .andExpect(jsonPath("$.senderRole").value(DEFAULT_SENDER_ROLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getAiMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        Long id = aiMessage.getId();

        defaultAiMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAiMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAiMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAiMessagesBySenderRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where senderRole equals to
        defaultAiMessageFiltering("senderRole.equals=" + DEFAULT_SENDER_ROLE, "senderRole.equals=" + UPDATED_SENDER_ROLE);
    }

    @Test
    @Transactional
    void getAllAiMessagesBySenderRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where senderRole in
        defaultAiMessageFiltering(
            "senderRole.in=" + DEFAULT_SENDER_ROLE + "," + UPDATED_SENDER_ROLE,
            "senderRole.in=" + UPDATED_SENDER_ROLE
        );
    }

    @Test
    @Transactional
    void getAllAiMessagesBySenderRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where senderRole is not null
        defaultAiMessageFiltering("senderRole.specified=true", "senderRole.specified=false");
    }

    @Test
    @Transactional
    void getAllAiMessagesBySenderRoleContainsSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where senderRole contains
        defaultAiMessageFiltering("senderRole.contains=" + DEFAULT_SENDER_ROLE, "senderRole.contains=" + UPDATED_SENDER_ROLE);
    }

    @Test
    @Transactional
    void getAllAiMessagesBySenderRoleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where senderRole does not contain
        defaultAiMessageFiltering("senderRole.doesNotContain=" + UPDATED_SENDER_ROLE, "senderRole.doesNotContain=" + DEFAULT_SENDER_ROLE);
    }

    @Test
    @Transactional
    void getAllAiMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where content equals to
        defaultAiMessageFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where content in
        defaultAiMessageFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where content is not null
        defaultAiMessageFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllAiMessagesByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where content contains
        defaultAiMessageFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where content does not contain
        defaultAiMessageFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where createdAt equals to
        defaultAiMessageFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where createdAt in
        defaultAiMessageFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAiMessagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        // Get all the aiMessageList where createdAt is not null
        defaultAiMessageFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAiMessagesByConversationIsEqualToSomething() throws Exception {
        AiConversation conversation;
        if (TestUtil.findAll(em, AiConversation.class).isEmpty()) {
            aiMessageRepository.saveAndFlush(aiMessage);
            conversation = AiConversationResourceIT.createEntity(em);
        } else {
            conversation = TestUtil.findAll(em, AiConversation.class).getFirst();
        }
        em.persist(conversation);
        em.flush();
        aiMessage.setConversation(conversation);
        aiMessageRepository.saveAndFlush(aiMessage);
        Long conversationId = conversation.getId();
        // Get all the aiMessageList where conversation equals to conversationId
        defaultAiMessageShouldBeFound("conversationId.equals=" + conversationId);

        // Get all the aiMessageList where conversation equals to (conversationId + 1)
        defaultAiMessageShouldNotBeFound("conversationId.equals=" + (conversationId + 1));
    }

    private void defaultAiMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAiMessageShouldBeFound(shouldBeFound);
        defaultAiMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAiMessageShouldBeFound(String filter) throws Exception {
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].senderRole").value(hasItem(DEFAULT_SENDER_ROLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAiMessageShouldNotBeFound(String filter) throws Exception {
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAiMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAiMessage() throws Exception {
        // Get the aiMessage
        restAiMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAiMessage() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiMessage
        AiMessage updatedAiMessage = aiMessageRepository.findById(aiMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAiMessage are not directly saved in db
        em.detach(updatedAiMessage);
        updatedAiMessage.senderRole(UPDATED_SENDER_ROLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(updatedAiMessage);

        restAiMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAiMessageToMatchAllProperties(updatedAiMessage);
    }

    @Test
    @Transactional
    void putNonExistingAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAiMessageWithPatch() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiMessage using partial update
        AiMessage partialUpdatedAiMessage = new AiMessage();
        partialUpdatedAiMessage.setId(aiMessage.getId());

        partialUpdatedAiMessage.senderRole(UPDATED_SENDER_ROLE);

        restAiMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiMessage))
            )
            .andExpect(status().isOk());

        // Validate the AiMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAiMessage, aiMessage),
            getPersistedAiMessage(aiMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateAiMessageWithPatch() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiMessage using partial update
        AiMessage partialUpdatedAiMessage = new AiMessage();
        partialUpdatedAiMessage.setId(aiMessage.getId());

        partialUpdatedAiMessage.senderRole(UPDATED_SENDER_ROLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restAiMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiMessage))
            )
            .andExpect(status().isOk());

        // Validate the AiMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiMessageUpdatableFieldsEquals(partialUpdatedAiMessage, getPersistedAiMessage(partialUpdatedAiMessage));
    }

    @Test
    @Transactional
    void patchNonExistingAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aiMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAiMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiMessage.setId(longCount.incrementAndGet());

        // Create the AiMessage
        AiMessageDTO aiMessageDTO = aiMessageMapper.toDto(aiMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aiMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAiMessage() throws Exception {
        // Initialize the database
        insertedAiMessage = aiMessageRepository.saveAndFlush(aiMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aiMessage
        restAiMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, aiMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return aiMessageRepository.count();
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

    protected AiMessage getPersistedAiMessage(AiMessage aiMessage) {
        return aiMessageRepository.findById(aiMessage.getId()).orElseThrow();
    }

    protected void assertPersistedAiMessageToMatchAllProperties(AiMessage expectedAiMessage) {
        assertAiMessageAllPropertiesEquals(expectedAiMessage, getPersistedAiMessage(expectedAiMessage));
    }

    protected void assertPersistedAiMessageToMatchUpdatableProperties(AiMessage expectedAiMessage) {
        assertAiMessageAllUpdatablePropertiesEquals(expectedAiMessage, getPersistedAiMessage(expectedAiMessage));
    }
}
