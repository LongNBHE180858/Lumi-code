package com.lumi.web.rest;

import static com.lumi.domain.SupportTicketAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.SupportTicket;
import com.lumi.domain.User;
import com.lumi.repository.SupportTicketRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.SupportTicketService;
import com.lumi.service.dto.SupportTicketDTO;
import com.lumi.service.mapper.SupportTicketMapper;
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
 * Integration tests for the {@link SupportTicketResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SupportTicketResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/support-tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SupportTicketRepository supportTicketRepositoryMock;

    @Autowired
    private SupportTicketMapper supportTicketMapper;

    @Mock
    private SupportTicketService supportTicketServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupportTicketMockMvc;

    private SupportTicket supportTicket;

    private SupportTicket insertedSupportTicket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupportTicket createEntity(EntityManager em) {
        SupportTicket supportTicket = new SupportTicket()
            .type(DEFAULT_TYPE)
            .subject(DEFAULT_SUBJECT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        supportTicket.setUser(user);
        return supportTicket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupportTicket createUpdatedEntity(EntityManager em) {
        SupportTicket updatedSupportTicket = new SupportTicket()
            .type(UPDATED_TYPE)
            .subject(UPDATED_SUBJECT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedSupportTicket.setUser(user);
        return updatedSupportTicket;
    }

    @BeforeEach
    void initTest() {
        supportTicket = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSupportTicket != null) {
            supportTicketRepository.delete(insertedSupportTicket);
            insertedSupportTicket = null;
        }
    }

    @Test
    @Transactional
    void createSupportTicket() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);
        var returnedSupportTicketDTO = om.readValue(
            restSupportTicketMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supportTicketDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupportTicketDTO.class
        );

        // Validate the SupportTicket in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSupportTicket = supportTicketMapper.toEntity(returnedSupportTicketDTO);
        assertSupportTicketUpdatableFieldsEquals(returnedSupportTicket, getPersistedSupportTicket(returnedSupportTicket));

        insertedSupportTicket = returnedSupportTicket;
    }

    @Test
    @Transactional
    void createSupportTicketWithExistingId() throws Exception {
        // Create the SupportTicket with an existing ID
        supportTicket.setId(1L);
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupportTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supportTicketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supportTicket.setType(null);

        // Create the SupportTicket, which fails.
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        restSupportTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supportTicketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supportTicket.setSubject(null);

        // Create the SupportTicket, which fails.
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        restSupportTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supportTicketDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSupportTickets() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supportTicket.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupportTicketsWithEagerRelationshipsIsEnabled() throws Exception {
        when(supportTicketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupportTicketMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supportTicketServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupportTicketsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(supportTicketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupportTicketMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(supportTicketRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSupportTicket() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get the supportTicket
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL_ID, supportTicket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supportTicket.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getSupportTicketsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        Long id = supportTicket.getId();

        defaultSupportTicketFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSupportTicketFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSupportTicketFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where type equals to
        defaultSupportTicketFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where type in
        defaultSupportTicketFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where type is not null
        defaultSupportTicketFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllSupportTicketsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where type contains
        defaultSupportTicketFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where type does not contain
        defaultSupportTicketFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllSupportTicketsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where subject equals to
        defaultSupportTicketFiltering("subject.equals=" + DEFAULT_SUBJECT, "subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSupportTicketsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where subject in
        defaultSupportTicketFiltering("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT, "subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSupportTicketsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where subject is not null
        defaultSupportTicketFiltering("subject.specified=true", "subject.specified=false");
    }

    @Test
    @Transactional
    void getAllSupportTicketsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where subject contains
        defaultSupportTicketFiltering("subject.contains=" + DEFAULT_SUBJECT, "subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSupportTicketsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where subject does not contain
        defaultSupportTicketFiltering("subject.doesNotContain=" + UPDATED_SUBJECT, "subject.doesNotContain=" + DEFAULT_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where status equals to
        defaultSupportTicketFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where status in
        defaultSupportTicketFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where status is not null
        defaultSupportTicketFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSupportTicketsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where status contains
        defaultSupportTicketFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where status does not contain
        defaultSupportTicketFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where createdAt equals to
        defaultSupportTicketFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSupportTicketsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where createdAt in
        defaultSupportTicketFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllSupportTicketsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        // Get all the supportTicketList where createdAt is not null
        defaultSupportTicketFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSupportTicketsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            supportTicketRepository.saveAndFlush(supportTicket);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(user);
        em.flush();
        supportTicket.setUser(user);
        supportTicketRepository.saveAndFlush(supportTicket);
        Long userId = user.getId();
        // Get all the supportTicketList where user equals to userId
        defaultSupportTicketShouldBeFound("userId.equals=" + userId);

        // Get all the supportTicketList where user equals to (userId + 1)
        defaultSupportTicketShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultSupportTicketFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSupportTicketShouldBeFound(shouldBeFound);
        defaultSupportTicketShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupportTicketShouldBeFound(String filter) throws Exception {
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supportTicket.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupportTicketShouldNotBeFound(String filter) throws Exception {
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupportTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupportTicket() throws Exception {
        // Get the supportTicket
        restSupportTicketMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupportTicket() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supportTicket
        SupportTicket updatedSupportTicket = supportTicketRepository.findById(supportTicket.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupportTicket are not directly saved in db
        em.detach(updatedSupportTicket);
        updatedSupportTicket.type(UPDATED_TYPE).subject(UPDATED_SUBJECT).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(updatedSupportTicket);

        restSupportTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supportTicketDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supportTicketDTO))
            )
            .andExpect(status().isOk());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupportTicketToMatchAllProperties(updatedSupportTicket);
    }

    @Test
    @Transactional
    void putNonExistingSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supportTicketDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supportTicketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supportTicketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supportTicketDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSupportTicketWithPatch() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supportTicket using partial update
        SupportTicket partialUpdatedSupportTicket = new SupportTicket();
        partialUpdatedSupportTicket.setId(supportTicket.getId());

        restSupportTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupportTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupportTicket))
            )
            .andExpect(status().isOk());

        // Validate the SupportTicket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupportTicketUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSupportTicket, supportTicket),
            getPersistedSupportTicket(supportTicket)
        );
    }

    @Test
    @Transactional
    void fullUpdateSupportTicketWithPatch() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supportTicket using partial update
        SupportTicket partialUpdatedSupportTicket = new SupportTicket();
        partialUpdatedSupportTicket.setId(supportTicket.getId());

        partialUpdatedSupportTicket.type(UPDATED_TYPE).subject(UPDATED_SUBJECT).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restSupportTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupportTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupportTicket))
            )
            .andExpect(status().isOk());

        // Validate the SupportTicket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupportTicketUpdatableFieldsEquals(partialUpdatedSupportTicket, getPersistedSupportTicket(partialUpdatedSupportTicket));
    }

    @Test
    @Transactional
    void patchNonExistingSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supportTicketDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supportTicketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supportTicketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupportTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supportTicket.setId(longCount.incrementAndGet());

        // Create the SupportTicket
        SupportTicketDTO supportTicketDTO = supportTicketMapper.toDto(supportTicket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportTicketMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supportTicketDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupportTicket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupportTicket() throws Exception {
        // Initialize the database
        insertedSupportTicket = supportTicketRepository.saveAndFlush(supportTicket);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the supportTicket
        restSupportTicketMockMvc
            .perform(delete(ENTITY_API_URL_ID, supportTicket.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return supportTicketRepository.count();
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

    protected SupportTicket getPersistedSupportTicket(SupportTicket supportTicket) {
        return supportTicketRepository.findById(supportTicket.getId()).orElseThrow();
    }

    protected void assertPersistedSupportTicketToMatchAllProperties(SupportTicket expectedSupportTicket) {
        assertSupportTicketAllPropertiesEquals(expectedSupportTicket, getPersistedSupportTicket(expectedSupportTicket));
    }

    protected void assertPersistedSupportTicketToMatchUpdatableProperties(SupportTicket expectedSupportTicket) {
        assertSupportTicketAllUpdatablePropertiesEquals(expectedSupportTicket, getPersistedSupportTicket(expectedSupportTicket));
    }
}
