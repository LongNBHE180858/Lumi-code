package com.lumi.web.rest;

import static com.lumi.domain.TicketMessageAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.SupportTicket;
import com.lumi.domain.TicketMessage;
import com.lumi.domain.User;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.TicketMessageService;
import com.lumi.service.dto.TicketMessageDTO;
import com.lumi.service.mapper.TicketMessageMapper;
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
 * Integration tests for the {@link TicketMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TicketMessageResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/ticket-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TicketMessageRepository ticketMessageRepositoryMock;

    @Autowired
    private TicketMessageMapper ticketMessageMapper;

    @Mock
    private TicketMessageService ticketMessageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketMessageMockMvc;

    private TicketMessage ticketMessage;

    private TicketMessage insertedTicketMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketMessage createEntity(EntityManager em) {
        TicketMessage ticketMessage = new TicketMessage().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        ticketMessage.setSender(user);
        // Add required entity
        SupportTicket supportTicket;
        if (TestUtil.findAll(em, SupportTicket.class).isEmpty()) {
            supportTicket = SupportTicketResourceIT.createEntity(em);
            em.persist(supportTicket);
            em.flush();
        } else {
            supportTicket = TestUtil.findAll(em, SupportTicket.class).getFirst();
        }
        ticketMessage.setTicket(supportTicket);
        return ticketMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketMessage createUpdatedEntity(EntityManager em) {
        TicketMessage updatedTicketMessage = new TicketMessage().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTicketMessage.setSender(user);
        // Add required entity
        SupportTicket supportTicket;
        if (TestUtil.findAll(em, SupportTicket.class).isEmpty()) {
            supportTicket = SupportTicketResourceIT.createUpdatedEntity(em);
            em.persist(supportTicket);
            em.flush();
        } else {
            supportTicket = TestUtil.findAll(em, SupportTicket.class).getFirst();
        }
        updatedTicketMessage.setTicket(supportTicket);
        return updatedTicketMessage;
    }

    @BeforeEach
    void initTest() {
        ticketMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketMessage != null) {
            ticketMessageRepository.delete(insertedTicketMessage);
            insertedTicketMessage = null;
        }
    }

    @Test
    @Transactional
    void createTicketMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);
        var returnedTicketMessageDTO = om.readValue(
            restTicketMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketMessageDTO.class
        );

        // Validate the TicketMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicketMessage = ticketMessageMapper.toEntity(returnedTicketMessageDTO);
        assertTicketMessageUpdatableFieldsEquals(returnedTicketMessage, getPersistedTicketMessage(returnedTicketMessage));

        insertedTicketMessage = returnedTicketMessage;
    }

    @Test
    @Transactional
    void createTicketMessageWithExistingId() throws Exception {
        // Create the TicketMessage with an existing ID
        ticketMessage.setId(1L);
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketMessage.setContent(null);

        // Create the TicketMessage, which fails.
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        restTicketMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTicketMessages() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ticketMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ticketMessageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ticketMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ticketMessageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTicketMessage() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get the ticketMessage
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketMessage.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getTicketMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        Long id = ticketMessage.getId();

        defaultTicketMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTicketMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTicketMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where content equals to
        defaultTicketMessageFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where content in
        defaultTicketMessageFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where content is not null
        defaultTicketMessageFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketMessagesByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where content contains
        defaultTicketMessageFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where content does not contain
        defaultTicketMessageFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where createdAt equals to
        defaultTicketMessageFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTicketMessagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where createdAt in
        defaultTicketMessageFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTicketMessagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        // Get all the ticketMessageList where createdAt is not null
        defaultTicketMessageFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketMessagesBySenderIsEqualToSomething() throws Exception {
        User sender;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            ticketMessageRepository.saveAndFlush(ticketMessage);
            sender = UserResourceIT.createEntity();
        } else {
            sender = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(sender);
        em.flush();
        ticketMessage.setSender(sender);
        ticketMessageRepository.saveAndFlush(ticketMessage);
        Long senderId = sender.getId();
        // Get all the ticketMessageList where sender equals to senderId
        defaultTicketMessageShouldBeFound("senderId.equals=" + senderId);

        // Get all the ticketMessageList where sender equals to (senderId + 1)
        defaultTicketMessageShouldNotBeFound("senderId.equals=" + (senderId + 1));
    }

    @Test
    @Transactional
    void getAllTicketMessagesByTicketIsEqualToSomething() throws Exception {
        SupportTicket ticket;
        if (TestUtil.findAll(em, SupportTicket.class).isEmpty()) {
            ticketMessageRepository.saveAndFlush(ticketMessage);
            ticket = SupportTicketResourceIT.createEntity(em);
        } else {
            ticket = TestUtil.findAll(em, SupportTicket.class).getFirst();
        }
        em.persist(ticket);
        em.flush();
        ticketMessage.setTicket(ticket);
        ticketMessageRepository.saveAndFlush(ticketMessage);
        Long ticketId = ticket.getId();
        // Get all the ticketMessageList where ticket equals to ticketId
        defaultTicketMessageShouldBeFound("ticketId.equals=" + ticketId);

        // Get all the ticketMessageList where ticket equals to (ticketId + 1)
        defaultTicketMessageShouldNotBeFound("ticketId.equals=" + (ticketId + 1));
    }

    private void defaultTicketMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTicketMessageShouldBeFound(shouldBeFound);
        defaultTicketMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketMessageShouldBeFound(String filter) throws Exception {
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketMessageShouldNotBeFound(String filter) throws Exception {
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketMessage() throws Exception {
        // Get the ticketMessage
        restTicketMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketMessage() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketMessage
        TicketMessage updatedTicketMessage = ticketMessageRepository.findById(ticketMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketMessage are not directly saved in db
        em.detach(updatedTicketMessage);
        updatedTicketMessage.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(updatedTicketMessage);

        restTicketMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketMessageToMatchAllProperties(updatedTicketMessage);
    }

    @Test
    @Transactional
    void putNonExistingTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTicketMessageWithPatch() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketMessage using partial update
        TicketMessage partialUpdatedTicketMessage = new TicketMessage();
        partialUpdatedTicketMessage.setId(ticketMessage.getId());

        restTicketMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketMessage))
            )
            .andExpect(status().isOk());

        // Validate the TicketMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketMessage, ticketMessage),
            getPersistedTicketMessage(ticketMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketMessageWithPatch() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketMessage using partial update
        TicketMessage partialUpdatedTicketMessage = new TicketMessage();
        partialUpdatedTicketMessage.setId(ticketMessage.getId());

        partialUpdatedTicketMessage.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restTicketMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketMessage))
            )
            .andExpect(status().isOk());

        // Validate the TicketMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketMessageUpdatableFieldsEquals(partialUpdatedTicketMessage, getPersistedTicketMessage(partialUpdatedTicketMessage));
    }

    @Test
    @Transactional
    void patchNonExistingTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketMessage.setId(longCount.incrementAndGet());

        // Create the TicketMessage
        TicketMessageDTO ticketMessageDTO = ticketMessageMapper.toDto(ticketMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTicketMessage() throws Exception {
        // Initialize the database
        insertedTicketMessage = ticketMessageRepository.saveAndFlush(ticketMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketMessage
        restTicketMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketMessageRepository.count();
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

    protected TicketMessage getPersistedTicketMessage(TicketMessage ticketMessage) {
        return ticketMessageRepository.findById(ticketMessage.getId()).orElseThrow();
    }

    protected void assertPersistedTicketMessageToMatchAllProperties(TicketMessage expectedTicketMessage) {
        assertTicketMessageAllPropertiesEquals(expectedTicketMessage, getPersistedTicketMessage(expectedTicketMessage));
    }

    protected void assertPersistedTicketMessageToMatchUpdatableProperties(TicketMessage expectedTicketMessage) {
        assertTicketMessageAllUpdatablePropertiesEquals(expectedTicketMessage, getPersistedTicketMessage(expectedTicketMessage));
    }
}
