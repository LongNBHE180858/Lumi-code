package com.lumi.web.rest;

import static com.lumi.domain.TeacherVerificationAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.TeacherVerification;
import com.lumi.domain.User;
import com.lumi.repository.TeacherVerificationRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.TeacherVerificationService;
import com.lumi.service.dto.TeacherVerificationDTO;
import com.lumi.service.mapper.TeacherVerificationMapper;
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
 * Integration tests for the {@link TeacherVerificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class TeacherVerificationResourceIT {

    private static final String DEFAULT_IDENTITY_DOCUMENT_URL = "AAAAAAAAAA";
    private static final String UPDATED_IDENTITY_DOCUMENT_URL = "BBBBBBBBBB";

    private static final String DEFAULT_VERIFICATION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_VERIFICATION_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_APPROVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_APPROVED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/teacher-verifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeacherVerificationRepository teacherVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TeacherVerificationRepository teacherVerificationRepositoryMock;

    @Autowired
    private TeacherVerificationMapper teacherVerificationMapper;

    @Mock
    private TeacherVerificationService teacherVerificationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeacherVerificationMockMvc;

    private TeacherVerification teacherVerification;

    private TeacherVerification insertedTeacherVerification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherVerification createEntity(EntityManager em) {
        TeacherVerification teacherVerification = new TeacherVerification()
            .identityDocumentUrl(DEFAULT_IDENTITY_DOCUMENT_URL)
            .verificationStatus(DEFAULT_VERIFICATION_STATUS)
            .notes(DEFAULT_NOTES)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .approvedAt(DEFAULT_APPROVED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        teacherVerification.setUser(user);
        return teacherVerification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherVerification createUpdatedEntity(EntityManager em) {
        TeacherVerification updatedTeacherVerification = new TeacherVerification()
            .identityDocumentUrl(UPDATED_IDENTITY_DOCUMENT_URL)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .notes(UPDATED_NOTES)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .approvedAt(UPDATED_APPROVED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTeacherVerification.setUser(user);
        return updatedTeacherVerification;
    }

    @BeforeEach
    void initTest() {
        teacherVerification = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTeacherVerification != null) {
            teacherVerificationRepository.delete(insertedTeacherVerification);
            insertedTeacherVerification = null;
        }
    }

    @Test
    @Transactional
    void createTeacherVerification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);
        var returnedTeacherVerificationDTO = om.readValue(
            restTeacherVerificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherVerificationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeacherVerificationDTO.class
        );

        // Validate the TeacherVerification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeacherVerification = teacherVerificationMapper.toEntity(returnedTeacherVerificationDTO);
        assertTeacherVerificationUpdatableFieldsEquals(
            returnedTeacherVerification,
            getPersistedTeacherVerification(returnedTeacherVerification)
        );

        insertedTeacherVerification = returnedTeacherVerification;
    }

    @Test
    @Transactional
    void createTeacherVerificationWithExistingId() throws Exception {
        // Create the TeacherVerification with an existing ID
        teacherVerification.setId(1L);
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeacherVerificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherVerificationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIdentityDocumentUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teacherVerification.setIdentityDocumentUrl(null);

        // Create the TeacherVerification, which fails.
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        restTeacherVerificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherVerificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeacherVerifications() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherVerification.getId().intValue())))
            .andExpect(jsonPath("$.[*].identityDocumentUrl").value(hasItem(DEFAULT_IDENTITY_DOCUMENT_URL)))
            .andExpect(jsonPath("$.[*].verificationStatus").value(hasItem(DEFAULT_VERIFICATION_STATUS)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].approvedAt").value(hasItem(DEFAULT_APPROVED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeacherVerificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(teacherVerificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeacherVerificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teacherVerificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeacherVerificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teacherVerificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeacherVerificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teacherVerificationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeacherVerification() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get the teacherVerification
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL_ID, teacherVerification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teacherVerification.getId().intValue()))
            .andExpect(jsonPath("$.identityDocumentUrl").value(DEFAULT_IDENTITY_DOCUMENT_URL))
            .andExpect(jsonPath("$.verificationStatus").value(DEFAULT_VERIFICATION_STATUS))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.approvedAt").value(DEFAULT_APPROVED_AT.toString()));
    }

    @Test
    @Transactional
    void getTeacherVerificationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        Long id = teacherVerification.getId();

        defaultTeacherVerificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTeacherVerificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTeacherVerificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByIdentityDocumentUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where identityDocumentUrl equals to
        defaultTeacherVerificationFiltering(
            "identityDocumentUrl.equals=" + DEFAULT_IDENTITY_DOCUMENT_URL,
            "identityDocumentUrl.equals=" + UPDATED_IDENTITY_DOCUMENT_URL
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByIdentityDocumentUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where identityDocumentUrl in
        defaultTeacherVerificationFiltering(
            "identityDocumentUrl.in=" + DEFAULT_IDENTITY_DOCUMENT_URL + "," + UPDATED_IDENTITY_DOCUMENT_URL,
            "identityDocumentUrl.in=" + UPDATED_IDENTITY_DOCUMENT_URL
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByIdentityDocumentUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where identityDocumentUrl is not null
        defaultTeacherVerificationFiltering("identityDocumentUrl.specified=true", "identityDocumentUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByIdentityDocumentUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where identityDocumentUrl contains
        defaultTeacherVerificationFiltering(
            "identityDocumentUrl.contains=" + DEFAULT_IDENTITY_DOCUMENT_URL,
            "identityDocumentUrl.contains=" + UPDATED_IDENTITY_DOCUMENT_URL
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByIdentityDocumentUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where identityDocumentUrl does not contain
        defaultTeacherVerificationFiltering(
            "identityDocumentUrl.doesNotContain=" + UPDATED_IDENTITY_DOCUMENT_URL,
            "identityDocumentUrl.doesNotContain=" + DEFAULT_IDENTITY_DOCUMENT_URL
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByVerificationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where verificationStatus equals to
        defaultTeacherVerificationFiltering(
            "verificationStatus.equals=" + DEFAULT_VERIFICATION_STATUS,
            "verificationStatus.equals=" + UPDATED_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByVerificationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where verificationStatus in
        defaultTeacherVerificationFiltering(
            "verificationStatus.in=" + DEFAULT_VERIFICATION_STATUS + "," + UPDATED_VERIFICATION_STATUS,
            "verificationStatus.in=" + UPDATED_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByVerificationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where verificationStatus is not null
        defaultTeacherVerificationFiltering("verificationStatus.specified=true", "verificationStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByVerificationStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where verificationStatus contains
        defaultTeacherVerificationFiltering(
            "verificationStatus.contains=" + DEFAULT_VERIFICATION_STATUS,
            "verificationStatus.contains=" + UPDATED_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByVerificationStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where verificationStatus does not contain
        defaultTeacherVerificationFiltering(
            "verificationStatus.doesNotContain=" + UPDATED_VERIFICATION_STATUS,
            "verificationStatus.doesNotContain=" + DEFAULT_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where notes equals to
        defaultTeacherVerificationFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where notes in
        defaultTeacherVerificationFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where notes is not null
        defaultTeacherVerificationFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where notes contains
        defaultTeacherVerificationFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where notes does not contain
        defaultTeacherVerificationFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where submittedAt equals to
        defaultTeacherVerificationFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where submittedAt in
        defaultTeacherVerificationFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where submittedAt is not null
        defaultTeacherVerificationFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByApprovedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where approvedAt equals to
        defaultTeacherVerificationFiltering("approvedAt.equals=" + DEFAULT_APPROVED_AT, "approvedAt.equals=" + UPDATED_APPROVED_AT);
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByApprovedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where approvedAt in
        defaultTeacherVerificationFiltering(
            "approvedAt.in=" + DEFAULT_APPROVED_AT + "," + UPDATED_APPROVED_AT,
            "approvedAt.in=" + UPDATED_APPROVED_AT
        );
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByApprovedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        // Get all the teacherVerificationList where approvedAt is not null
        defaultTeacherVerificationFiltering("approvedAt.specified=true", "approvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTeacherVerificationsByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = teacherVerification.getUser();
        teacherVerificationRepository.saveAndFlush(teacherVerification);
        Long userId = user.getId();
        // Get all the teacherVerificationList where user equals to userId
        defaultTeacherVerificationShouldBeFound("userId.equals=" + userId);

        // Get all the teacherVerificationList where user equals to (userId + 1)
        defaultTeacherVerificationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultTeacherVerificationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTeacherVerificationShouldBeFound(shouldBeFound);
        defaultTeacherVerificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeacherVerificationShouldBeFound(String filter) throws Exception {
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherVerification.getId().intValue())))
            .andExpect(jsonPath("$.[*].identityDocumentUrl").value(hasItem(DEFAULT_IDENTITY_DOCUMENT_URL)))
            .andExpect(jsonPath("$.[*].verificationStatus").value(hasItem(DEFAULT_VERIFICATION_STATUS)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].approvedAt").value(hasItem(DEFAULT_APPROVED_AT.toString())));

        // Check, that the count call also returns 1
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeacherVerificationShouldNotBeFound(String filter) throws Exception {
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeacherVerificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeacherVerification() throws Exception {
        // Get the teacherVerification
        restTeacherVerificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeacherVerification() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherVerification
        TeacherVerification updatedTeacherVerification = teacherVerificationRepository.findById(teacherVerification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeacherVerification are not directly saved in db
        em.detach(updatedTeacherVerification);
        updatedTeacherVerification
            .identityDocumentUrl(UPDATED_IDENTITY_DOCUMENT_URL)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .notes(UPDATED_NOTES)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .approvedAt(UPDATED_APPROVED_AT);
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(updatedTeacherVerification);

        restTeacherVerificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherVerificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeacherVerificationToMatchAllProperties(updatedTeacherVerification);
    }

    @Test
    @Transactional
    void putNonExistingTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherVerificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teacherVerificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeacherVerificationWithPatch() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherVerification using partial update
        TeacherVerification partialUpdatedTeacherVerification = new TeacherVerification();
        partialUpdatedTeacherVerification.setId(teacherVerification.getId());

        partialUpdatedTeacherVerification
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .approvedAt(UPDATED_APPROVED_AT);

        restTeacherVerificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherVerification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeacherVerification))
            )
            .andExpect(status().isOk());

        // Validate the TeacherVerification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherVerificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeacherVerification, teacherVerification),
            getPersistedTeacherVerification(teacherVerification)
        );
    }

    @Test
    @Transactional
    void fullUpdateTeacherVerificationWithPatch() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacherVerification using partial update
        TeacherVerification partialUpdatedTeacherVerification = new TeacherVerification();
        partialUpdatedTeacherVerification.setId(teacherVerification.getId());

        partialUpdatedTeacherVerification
            .identityDocumentUrl(UPDATED_IDENTITY_DOCUMENT_URL)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .notes(UPDATED_NOTES)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .approvedAt(UPDATED_APPROVED_AT);

        restTeacherVerificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherVerification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeacherVerification))
            )
            .andExpect(status().isOk());

        // Validate the TeacherVerification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherVerificationUpdatableFieldsEquals(
            partialUpdatedTeacherVerification,
            getPersistedTeacherVerification(partialUpdatedTeacherVerification)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teacherVerificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeacherVerification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacherVerification.setId(longCount.incrementAndGet());

        // Create the TeacherVerification
        TeacherVerificationDTO teacherVerificationDTO = teacherVerificationMapper.toDto(teacherVerification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherVerificationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teacherVerificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherVerification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeacherVerification() throws Exception {
        // Initialize the database
        insertedTeacherVerification = teacherVerificationRepository.saveAndFlush(teacherVerification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teacherVerification
        restTeacherVerificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, teacherVerification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teacherVerificationRepository.count();
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

    protected TeacherVerification getPersistedTeacherVerification(TeacherVerification teacherVerification) {
        return teacherVerificationRepository.findById(teacherVerification.getId()).orElseThrow();
    }

    protected void assertPersistedTeacherVerificationToMatchAllProperties(TeacherVerification expectedTeacherVerification) {
        assertTeacherVerificationAllPropertiesEquals(
            expectedTeacherVerification,
            getPersistedTeacherVerification(expectedTeacherVerification)
        );
    }

    protected void assertPersistedTeacherVerificationToMatchUpdatableProperties(TeacherVerification expectedTeacherVerification) {
        assertTeacherVerificationAllUpdatablePropertiesEquals(
            expectedTeacherVerification,
            getPersistedTeacherVerification(expectedTeacherVerification)
        );
    }
}
