package com.lumi.web.rest;

import static com.lumi.domain.ProctorSessionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ExamAttempt;
import com.lumi.domain.ProctorSession;
import com.lumi.repository.ProctorSessionRepository;
import com.lumi.service.dto.ProctorSessionDTO;
import com.lumi.service.mapper.ProctorSessionMapper;
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
 * Integration tests for the {@link ProctorSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProctorSessionResourceIT {

    private static final Boolean DEFAULT_FACE_VERIFIED = false;
    private static final Boolean UPDATED_FACE_VERIFIED = true;

    private static final Integer DEFAULT_WARNING_COUNT = 0;
    private static final Integer UPDATED_WARNING_COUNT = 1;
    private static final Integer SMALLER_WARNING_COUNT = 0 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/proctor-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProctorSessionRepository proctorSessionRepository;

    @Autowired
    private ProctorSessionMapper proctorSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProctorSessionMockMvc;

    private ProctorSession proctorSession;

    private ProctorSession insertedProctorSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorSession createEntity(EntityManager em) {
        ProctorSession proctorSession = new ProctorSession()
            .faceVerified(DEFAULT_FACE_VERIFIED)
            .warningCount(DEFAULT_WARNING_COUNT)
            .status(DEFAULT_STATUS);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).getFirst();
        }
        proctorSession.setAttempt(examAttempt);
        return proctorSession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorSession createUpdatedEntity(EntityManager em) {
        ProctorSession updatedProctorSession = new ProctorSession()
            .faceVerified(UPDATED_FACE_VERIFIED)
            .warningCount(UPDATED_WARNING_COUNT)
            .status(UPDATED_STATUS);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createUpdatedEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).getFirst();
        }
        updatedProctorSession.setAttempt(examAttempt);
        return updatedProctorSession;
    }

    @BeforeEach
    void initTest() {
        proctorSession = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProctorSession != null) {
            proctorSessionRepository.delete(insertedProctorSession);
            insertedProctorSession = null;
        }
    }

    @Test
    @Transactional
    void createProctorSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);
        var returnedProctorSessionDTO = om.readValue(
            restProctorSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorSessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProctorSessionDTO.class
        );

        // Validate the ProctorSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProctorSession = proctorSessionMapper.toEntity(returnedProctorSessionDTO);
        assertProctorSessionUpdatableFieldsEquals(returnedProctorSession, getPersistedProctorSession(returnedProctorSession));

        insertedProctorSession = returnedProctorSession;
    }

    @Test
    @Transactional
    void createProctorSessionWithExistingId() throws Exception {
        // Create the ProctorSession with an existing ID
        proctorSession.setId(1L);
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProctorSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorSessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProctorSessions() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proctorSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].faceVerified").value(hasItem(DEFAULT_FACE_VERIFIED)))
            .andExpect(jsonPath("$.[*].warningCount").value(hasItem(DEFAULT_WARNING_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getProctorSession() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get the proctorSession
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, proctorSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proctorSession.getId().intValue()))
            .andExpect(jsonPath("$.faceVerified").value(DEFAULT_FACE_VERIFIED))
            .andExpect(jsonPath("$.warningCount").value(DEFAULT_WARNING_COUNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getProctorSessionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        Long id = proctorSession.getId();

        defaultProctorSessionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProctorSessionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProctorSessionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByFaceVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where faceVerified equals to
        defaultProctorSessionFiltering("faceVerified.equals=" + DEFAULT_FACE_VERIFIED, "faceVerified.equals=" + UPDATED_FACE_VERIFIED);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByFaceVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where faceVerified in
        defaultProctorSessionFiltering(
            "faceVerified.in=" + DEFAULT_FACE_VERIFIED + "," + UPDATED_FACE_VERIFIED,
            "faceVerified.in=" + UPDATED_FACE_VERIFIED
        );
    }

    @Test
    @Transactional
    void getAllProctorSessionsByFaceVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where faceVerified is not null
        defaultProctorSessionFiltering("faceVerified.specified=true", "faceVerified.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount equals to
        defaultProctorSessionFiltering("warningCount.equals=" + DEFAULT_WARNING_COUNT, "warningCount.equals=" + UPDATED_WARNING_COUNT);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount in
        defaultProctorSessionFiltering(
            "warningCount.in=" + DEFAULT_WARNING_COUNT + "," + UPDATED_WARNING_COUNT,
            "warningCount.in=" + UPDATED_WARNING_COUNT
        );
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount is not null
        defaultProctorSessionFiltering("warningCount.specified=true", "warningCount.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount is greater than or equal to
        defaultProctorSessionFiltering(
            "warningCount.greaterThanOrEqual=" + DEFAULT_WARNING_COUNT,
            "warningCount.greaterThanOrEqual=" + UPDATED_WARNING_COUNT
        );
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount is less than or equal to
        defaultProctorSessionFiltering(
            "warningCount.lessThanOrEqual=" + DEFAULT_WARNING_COUNT,
            "warningCount.lessThanOrEqual=" + SMALLER_WARNING_COUNT
        );
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount is less than
        defaultProctorSessionFiltering("warningCount.lessThan=" + UPDATED_WARNING_COUNT, "warningCount.lessThan=" + DEFAULT_WARNING_COUNT);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByWarningCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where warningCount is greater than
        defaultProctorSessionFiltering(
            "warningCount.greaterThan=" + SMALLER_WARNING_COUNT,
            "warningCount.greaterThan=" + DEFAULT_WARNING_COUNT
        );
    }

    @Test
    @Transactional
    void getAllProctorSessionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where status equals to
        defaultProctorSessionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where status in
        defaultProctorSessionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where status is not null
        defaultProctorSessionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllProctorSessionsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where status contains
        defaultProctorSessionFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        // Get all the proctorSessionList where status does not contain
        defaultProctorSessionFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllProctorSessionsByAttemptIsEqualToSomething() throws Exception {
        // Get already existing entity
        ExamAttempt attempt = proctorSession.getAttempt();
        proctorSessionRepository.saveAndFlush(proctorSession);
        Long attemptId = attempt.getId();
        // Get all the proctorSessionList where attempt equals to attemptId
        defaultProctorSessionShouldBeFound("attemptId.equals=" + attemptId);

        // Get all the proctorSessionList where attempt equals to (attemptId + 1)
        defaultProctorSessionShouldNotBeFound("attemptId.equals=" + (attemptId + 1));
    }

    private void defaultProctorSessionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProctorSessionShouldBeFound(shouldBeFound);
        defaultProctorSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProctorSessionShouldBeFound(String filter) throws Exception {
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proctorSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].faceVerified").value(hasItem(DEFAULT_FACE_VERIFIED)))
            .andExpect(jsonPath("$.[*].warningCount").value(hasItem(DEFAULT_WARNING_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProctorSessionShouldNotBeFound(String filter) throws Exception {
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProctorSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProctorSession() throws Exception {
        // Get the proctorSession
        restProctorSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProctorSession() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorSession
        ProctorSession updatedProctorSession = proctorSessionRepository.findById(proctorSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProctorSession are not directly saved in db
        em.detach(updatedProctorSession);
        updatedProctorSession.faceVerified(UPDATED_FACE_VERIFIED).warningCount(UPDATED_WARNING_COUNT).status(UPDATED_STATUS);
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(updatedProctorSession);

        restProctorSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proctorSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProctorSessionToMatchAllProperties(updatedProctorSession);
    }

    @Test
    @Transactional
    void putNonExistingProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proctorSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProctorSessionWithPatch() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorSession using partial update
        ProctorSession partialUpdatedProctorSession = new ProctorSession();
        partialUpdatedProctorSession.setId(proctorSession.getId());

        partialUpdatedProctorSession.faceVerified(UPDATED_FACE_VERIFIED).status(UPDATED_STATUS);

        restProctorSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorSession))
            )
            .andExpect(status().isOk());

        // Validate the ProctorSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProctorSession, proctorSession),
            getPersistedProctorSession(proctorSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateProctorSessionWithPatch() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorSession using partial update
        ProctorSession partialUpdatedProctorSession = new ProctorSession();
        partialUpdatedProctorSession.setId(proctorSession.getId());

        partialUpdatedProctorSession.faceVerified(UPDATED_FACE_VERIFIED).warningCount(UPDATED_WARNING_COUNT).status(UPDATED_STATUS);

        restProctorSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorSession))
            )
            .andExpect(status().isOk());

        // Validate the ProctorSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorSessionUpdatableFieldsEquals(partialUpdatedProctorSession, getPersistedProctorSession(partialUpdatedProctorSession));
    }

    @Test
    @Transactional
    void patchNonExistingProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proctorSessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProctorSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorSession.setId(longCount.incrementAndGet());

        // Create the ProctorSession
        ProctorSessionDTO proctorSessionDTO = proctorSessionMapper.toDto(proctorSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proctorSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProctorSession() throws Exception {
        // Initialize the database
        insertedProctorSession = proctorSessionRepository.saveAndFlush(proctorSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the proctorSession
        restProctorSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, proctorSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return proctorSessionRepository.count();
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

    protected ProctorSession getPersistedProctorSession(ProctorSession proctorSession) {
        return proctorSessionRepository.findById(proctorSession.getId()).orElseThrow();
    }

    protected void assertPersistedProctorSessionToMatchAllProperties(ProctorSession expectedProctorSession) {
        assertProctorSessionAllPropertiesEquals(expectedProctorSession, getPersistedProctorSession(expectedProctorSession));
    }

    protected void assertPersistedProctorSessionToMatchUpdatableProperties(ProctorSession expectedProctorSession) {
        assertProctorSessionAllUpdatablePropertiesEquals(expectedProctorSession, getPersistedProctorSession(expectedProctorSession));
    }
}
