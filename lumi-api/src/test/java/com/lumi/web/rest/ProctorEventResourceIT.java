package com.lumi.web.rest;

import static com.lumi.domain.ProctorEventAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ProctorEvent;
import com.lumi.domain.ProctorSession;
import com.lumi.repository.ProctorEventRepository;
import com.lumi.service.dto.ProctorEventDTO;
import com.lumi.service.mapper.ProctorEventMapper;
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
 * Integration tests for the {@link ProctorEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProctorEventResourceIT {

    private static final String DEFAULT_VIOLATION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_VIOLATION_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/proctor-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProctorEventRepository proctorEventRepository;

    @Autowired
    private ProctorEventMapper proctorEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProctorEventMockMvc;

    private ProctorEvent proctorEvent;

    private ProctorEvent insertedProctorEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorEvent createEntity(EntityManager em) {
        ProctorEvent proctorEvent = new ProctorEvent()
            .violationType(DEFAULT_VIOLATION_TYPE)
            .photoUrl(DEFAULT_PHOTO_URL)
            .occurredAt(DEFAULT_OCCURRED_AT);
        // Add required entity
        ProctorSession proctorSession;
        if (TestUtil.findAll(em, ProctorSession.class).isEmpty()) {
            proctorSession = ProctorSessionResourceIT.createEntity(em);
            em.persist(proctorSession);
            em.flush();
        } else {
            proctorSession = TestUtil.findAll(em, ProctorSession.class).getFirst();
        }
        proctorEvent.setSession(proctorSession);
        return proctorEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorEvent createUpdatedEntity(EntityManager em) {
        ProctorEvent updatedProctorEvent = new ProctorEvent()
            .violationType(UPDATED_VIOLATION_TYPE)
            .photoUrl(UPDATED_PHOTO_URL)
            .occurredAt(UPDATED_OCCURRED_AT);
        // Add required entity
        ProctorSession proctorSession;
        if (TestUtil.findAll(em, ProctorSession.class).isEmpty()) {
            proctorSession = ProctorSessionResourceIT.createUpdatedEntity(em);
            em.persist(proctorSession);
            em.flush();
        } else {
            proctorSession = TestUtil.findAll(em, ProctorSession.class).getFirst();
        }
        updatedProctorEvent.setSession(proctorSession);
        return updatedProctorEvent;
    }

    @BeforeEach
    void initTest() {
        proctorEvent = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProctorEvent != null) {
            proctorEventRepository.delete(insertedProctorEvent);
            insertedProctorEvent = null;
        }
    }

    @Test
    @Transactional
    void createProctorEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);
        var returnedProctorEventDTO = om.readValue(
            restProctorEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEventDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProctorEventDTO.class
        );

        // Validate the ProctorEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProctorEvent = proctorEventMapper.toEntity(returnedProctorEventDTO);
        assertProctorEventUpdatableFieldsEquals(returnedProctorEvent, getPersistedProctorEvent(returnedProctorEvent));

        insertedProctorEvent = returnedProctorEvent;
    }

    @Test
    @Transactional
    void createProctorEventWithExistingId() throws Exception {
        // Create the ProctorEvent with an existing ID
        proctorEvent.setId(1L);
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProctorEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkViolationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proctorEvent.setViolationType(null);

        // Create the ProctorEvent, which fails.
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        restProctorEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProctorEvents() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proctorEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].violationType").value(hasItem(DEFAULT_VIOLATION_TYPE)))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));
    }

    @Test
    @Transactional
    void getProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get the proctorEvent
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL_ID, proctorEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proctorEvent.getId().intValue()))
            .andExpect(jsonPath("$.violationType").value(DEFAULT_VIOLATION_TYPE))
            .andExpect(jsonPath("$.photoUrl").value(DEFAULT_PHOTO_URL))
            .andExpect(jsonPath("$.occurredAt").value(DEFAULT_OCCURRED_AT.toString()));
    }

    @Test
    @Transactional
    void getProctorEventsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        Long id = proctorEvent.getId();

        defaultProctorEventFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProctorEventFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProctorEventFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProctorEventsByViolationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where violationType equals to
        defaultProctorEventFiltering("violationType.equals=" + DEFAULT_VIOLATION_TYPE, "violationType.equals=" + UPDATED_VIOLATION_TYPE);
    }

    @Test
    @Transactional
    void getAllProctorEventsByViolationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where violationType in
        defaultProctorEventFiltering(
            "violationType.in=" + DEFAULT_VIOLATION_TYPE + "," + UPDATED_VIOLATION_TYPE,
            "violationType.in=" + UPDATED_VIOLATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllProctorEventsByViolationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where violationType is not null
        defaultProctorEventFiltering("violationType.specified=true", "violationType.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorEventsByViolationTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where violationType contains
        defaultProctorEventFiltering(
            "violationType.contains=" + DEFAULT_VIOLATION_TYPE,
            "violationType.contains=" + UPDATED_VIOLATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllProctorEventsByViolationTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where violationType does not contain
        defaultProctorEventFiltering(
            "violationType.doesNotContain=" + UPDATED_VIOLATION_TYPE,
            "violationType.doesNotContain=" + DEFAULT_VIOLATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllProctorEventsByPhotoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where photoUrl equals to
        defaultProctorEventFiltering("photoUrl.equals=" + DEFAULT_PHOTO_URL, "photoUrl.equals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllProctorEventsByPhotoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where photoUrl in
        defaultProctorEventFiltering("photoUrl.in=" + DEFAULT_PHOTO_URL + "," + UPDATED_PHOTO_URL, "photoUrl.in=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllProctorEventsByPhotoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where photoUrl is not null
        defaultProctorEventFiltering("photoUrl.specified=true", "photoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorEventsByPhotoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where photoUrl contains
        defaultProctorEventFiltering("photoUrl.contains=" + DEFAULT_PHOTO_URL, "photoUrl.contains=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllProctorEventsByPhotoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where photoUrl does not contain
        defaultProctorEventFiltering("photoUrl.doesNotContain=" + UPDATED_PHOTO_URL, "photoUrl.doesNotContain=" + DEFAULT_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllProctorEventsByOccurredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where occurredAt equals to
        defaultProctorEventFiltering("occurredAt.equals=" + DEFAULT_OCCURRED_AT, "occurredAt.equals=" + UPDATED_OCCURRED_AT);
    }

    @Test
    @Transactional
    void getAllProctorEventsByOccurredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where occurredAt in
        defaultProctorEventFiltering(
            "occurredAt.in=" + DEFAULT_OCCURRED_AT + "," + UPDATED_OCCURRED_AT,
            "occurredAt.in=" + UPDATED_OCCURRED_AT
        );
    }

    @Test
    @Transactional
    void getAllProctorEventsByOccurredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList where occurredAt is not null
        defaultProctorEventFiltering("occurredAt.specified=true", "occurredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorEventsBySessionIsEqualToSomething() throws Exception {
        ProctorSession session;
        if (TestUtil.findAll(em, ProctorSession.class).isEmpty()) {
            proctorEventRepository.saveAndFlush(proctorEvent);
            session = ProctorSessionResourceIT.createEntity(em);
        } else {
            session = TestUtil.findAll(em, ProctorSession.class).getFirst();
        }
        em.persist(session);
        em.flush();
        proctorEvent.setSession(session);
        proctorEventRepository.saveAndFlush(proctorEvent);
        Long sessionId = session.getId();
        // Get all the proctorEventList where session equals to sessionId
        defaultProctorEventShouldBeFound("sessionId.equals=" + sessionId);

        // Get all the proctorEventList where session equals to (sessionId + 1)
        defaultProctorEventShouldNotBeFound("sessionId.equals=" + (sessionId + 1));
    }

    private void defaultProctorEventFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProctorEventShouldBeFound(shouldBeFound);
        defaultProctorEventShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProctorEventShouldBeFound(String filter) throws Exception {
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proctorEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].violationType").value(hasItem(DEFAULT_VIOLATION_TYPE)))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));

        // Check, that the count call also returns 1
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProctorEventShouldNotBeFound(String filter) throws Exception {
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProctorEvent() throws Exception {
        // Get the proctorEvent
        restProctorEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent
        ProctorEvent updatedProctorEvent = proctorEventRepository.findById(proctorEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProctorEvent are not directly saved in db
        em.detach(updatedProctorEvent);
        updatedProctorEvent.violationType(UPDATED_VIOLATION_TYPE).photoUrl(UPDATED_PHOTO_URL).occurredAt(UPDATED_OCCURRED_AT);
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(updatedProctorEvent);

        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proctorEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProctorEventToMatchAllProperties(updatedProctorEvent);
    }

    @Test
    @Transactional
    void putNonExistingProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proctorEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProctorEventWithPatch() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent using partial update
        ProctorEvent partialUpdatedProctorEvent = new ProctorEvent();
        partialUpdatedProctorEvent.setId(proctorEvent.getId());

        partialUpdatedProctorEvent.violationType(UPDATED_VIOLATION_TYPE);

        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorEvent))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProctorEvent, proctorEvent),
            getPersistedProctorEvent(proctorEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateProctorEventWithPatch() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent using partial update
        ProctorEvent partialUpdatedProctorEvent = new ProctorEvent();
        partialUpdatedProctorEvent.setId(proctorEvent.getId());

        partialUpdatedProctorEvent.violationType(UPDATED_VIOLATION_TYPE).photoUrl(UPDATED_PHOTO_URL).occurredAt(UPDATED_OCCURRED_AT);

        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorEvent))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorEventUpdatableFieldsEquals(partialUpdatedProctorEvent, getPersistedProctorEvent(partialUpdatedProctorEvent));
    }

    @Test
    @Transactional
    void patchNonExistingProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proctorEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // Create the ProctorEvent
        ProctorEventDTO proctorEventDTO = proctorEventMapper.toDto(proctorEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proctorEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the proctorEvent
        restProctorEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, proctorEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return proctorEventRepository.count();
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

    protected ProctorEvent getPersistedProctorEvent(ProctorEvent proctorEvent) {
        return proctorEventRepository.findById(proctorEvent.getId()).orElseThrow();
    }

    protected void assertPersistedProctorEventToMatchAllProperties(ProctorEvent expectedProctorEvent) {
        assertProctorEventAllPropertiesEquals(expectedProctorEvent, getPersistedProctorEvent(expectedProctorEvent));
    }

    protected void assertPersistedProctorEventToMatchUpdatableProperties(ProctorEvent expectedProctorEvent) {
        assertProctorEventAllUpdatablePropertiesEquals(expectedProctorEvent, getPersistedProctorEvent(expectedProctorEvent));
    }
}
