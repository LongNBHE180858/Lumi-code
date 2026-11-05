package com.lumi.web.rest;

import static com.lumi.domain.ParentStudentLinkAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ParentStudentLink;
import com.lumi.domain.User;
import com.lumi.repository.ParentStudentLinkRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.ParentStudentLinkService;
import com.lumi.service.dto.ParentStudentLinkDTO;
import com.lumi.service.mapper.ParentStudentLinkMapper;
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
 * Integration tests for the {@link ParentStudentLinkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ParentStudentLinkResourceIT {

    private static final String DEFAULT_LINK_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LINK_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_LINKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LINKED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_CODE_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CODE_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_REVOKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVOKED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_RELATIONSHIP_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RELATIONSHIP_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_GUARDIAN_VERIFICATION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_GUARDIAN_VERIFICATION_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_GUARDIAN_VERIFIED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_GUARDIAN_VERIFIED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_GUARDIAN_VERIFICATION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_GUARDIAN_VERIFICATION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parent-student-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParentStudentLinkRepository parentStudentLinkRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ParentStudentLinkRepository parentStudentLinkRepositoryMock;

    @Autowired
    private ParentStudentLinkMapper parentStudentLinkMapper;

    @Mock
    private ParentStudentLinkService parentStudentLinkServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParentStudentLinkMockMvc;

    private ParentStudentLink parentStudentLink;

    private ParentStudentLink insertedParentStudentLink;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParentStudentLink createEntity(EntityManager em) {
        ParentStudentLink parentStudentLink = new ParentStudentLink()
            .linkCode(DEFAULT_LINK_CODE)
            .status(DEFAULT_STATUS)
            .linkedAt(DEFAULT_LINKED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .codeExpiresAt(DEFAULT_CODE_EXPIRES_AT)
            .confirmedAt(DEFAULT_CONFIRMED_AT)
            .revokedAt(DEFAULT_REVOKED_AT)
            .relationshipType(DEFAULT_RELATIONSHIP_TYPE)
            .guardianVerificationStatus(DEFAULT_GUARDIAN_VERIFICATION_STATUS)
            .guardianVerifiedAt(DEFAULT_GUARDIAN_VERIFIED_AT)
            .guardianVerificationNote(DEFAULT_GUARDIAN_VERIFICATION_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        parentStudentLink.setParent(user);
        // Add required entity
        parentStudentLink.setStudent(user);
        return parentStudentLink;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParentStudentLink createUpdatedEntity(EntityManager em) {
        ParentStudentLink updatedParentStudentLink = new ParentStudentLink()
            .linkCode(UPDATED_LINK_CODE)
            .status(UPDATED_STATUS)
            .linkedAt(UPDATED_LINKED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .codeExpiresAt(UPDATED_CODE_EXPIRES_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .relationshipType(UPDATED_RELATIONSHIP_TYPE)
            .guardianVerificationStatus(UPDATED_GUARDIAN_VERIFICATION_STATUS)
            .guardianVerifiedAt(UPDATED_GUARDIAN_VERIFIED_AT)
            .guardianVerificationNote(UPDATED_GUARDIAN_VERIFICATION_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedParentStudentLink.setParent(user);
        // Add required entity
        updatedParentStudentLink.setStudent(user);
        return updatedParentStudentLink;
    }

    @BeforeEach
    void initTest() {
        parentStudentLink = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedParentStudentLink != null) {
            parentStudentLinkRepository.delete(insertedParentStudentLink);
            insertedParentStudentLink = null;
        }
    }

    @Test
    @Transactional
    void createParentStudentLink() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);
        var returnedParentStudentLinkDTO = om.readValue(
            restParentStudentLinkMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParentStudentLinkDTO.class
        );

        // Validate the ParentStudentLink in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParentStudentLink = parentStudentLinkMapper.toEntity(returnedParentStudentLinkDTO);
        assertParentStudentLinkUpdatableFieldsEquals(returnedParentStudentLink, getPersistedParentStudentLink(returnedParentStudentLink));

        insertedParentStudentLink = returnedParentStudentLink;
    }

    @Test
    @Transactional
    void createParentStudentLinkWithExistingId() throws Exception {
        // Create the ParentStudentLink with an existing ID
        parentStudentLink.setId(1L);
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParentStudentLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLinkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parentStudentLink.setLinkCode(null);

        // Create the ParentStudentLink, which fails.
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        restParentStudentLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parentStudentLink.setCreatedAt(null);

        // Create the ParentStudentLink, which fails.
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        restParentStudentLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parentStudentLink.setCodeExpiresAt(null);

        // Create the ParentStudentLink, which fails.
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        restParentStudentLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGuardianVerificationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parentStudentLink.setGuardianVerificationStatus(null);

        // Create the ParentStudentLink, which fails.
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        restParentStudentLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParentStudentLinks() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parentStudentLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].linkCode").value(hasItem(DEFAULT_LINK_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].linkedAt").value(hasItem(DEFAULT_LINKED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].codeExpiresAt").value(hasItem(DEFAULT_CODE_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].revokedAt").value(hasItem(DEFAULT_REVOKED_AT.toString())))
            .andExpect(jsonPath("$.[*].relationshipType").value(hasItem(DEFAULT_RELATIONSHIP_TYPE)))
            .andExpect(jsonPath("$.[*].guardianVerificationStatus").value(hasItem(DEFAULT_GUARDIAN_VERIFICATION_STATUS)))
            .andExpect(jsonPath("$.[*].guardianVerifiedAt").value(hasItem(DEFAULT_GUARDIAN_VERIFIED_AT.toString())))
            .andExpect(jsonPath("$.[*].guardianVerificationNote").value(hasItem(DEFAULT_GUARDIAN_VERIFICATION_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParentStudentLinksWithEagerRelationshipsIsEnabled() throws Exception {
        when(parentStudentLinkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restParentStudentLinkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(parentStudentLinkServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParentStudentLinksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(parentStudentLinkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restParentStudentLinkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(parentStudentLinkRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getParentStudentLink() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get the parentStudentLink
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL_ID, parentStudentLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parentStudentLink.getId().intValue()))
            .andExpect(jsonPath("$.linkCode").value(DEFAULT_LINK_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.linkedAt").value(DEFAULT_LINKED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.codeExpiresAt").value(DEFAULT_CODE_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()))
            .andExpect(jsonPath("$.revokedAt").value(DEFAULT_REVOKED_AT.toString()))
            .andExpect(jsonPath("$.relationshipType").value(DEFAULT_RELATIONSHIP_TYPE))
            .andExpect(jsonPath("$.guardianVerificationStatus").value(DEFAULT_GUARDIAN_VERIFICATION_STATUS))
            .andExpect(jsonPath("$.guardianVerifiedAt").value(DEFAULT_GUARDIAN_VERIFIED_AT.toString()))
            .andExpect(jsonPath("$.guardianVerificationNote").value(DEFAULT_GUARDIAN_VERIFICATION_NOTE));
    }

    @Test
    @Transactional
    void getParentStudentLinksByIdFiltering() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        Long id = parentStudentLink.getId();

        defaultParentStudentLinkFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParentStudentLinkFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParentStudentLinkFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkCode equals to
        defaultParentStudentLinkFiltering("linkCode.equals=" + DEFAULT_LINK_CODE, "linkCode.equals=" + UPDATED_LINK_CODE);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkCode in
        defaultParentStudentLinkFiltering("linkCode.in=" + DEFAULT_LINK_CODE + "," + UPDATED_LINK_CODE, "linkCode.in=" + UPDATED_LINK_CODE);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkCode is not null
        defaultParentStudentLinkFiltering("linkCode.specified=true", "linkCode.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkCode contains
        defaultParentStudentLinkFiltering("linkCode.contains=" + DEFAULT_LINK_CODE, "linkCode.contains=" + UPDATED_LINK_CODE);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkCode does not contain
        defaultParentStudentLinkFiltering("linkCode.doesNotContain=" + UPDATED_LINK_CODE, "linkCode.doesNotContain=" + DEFAULT_LINK_CODE);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where status equals to
        defaultParentStudentLinkFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where status in
        defaultParentStudentLinkFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where status is not null
        defaultParentStudentLinkFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where status contains
        defaultParentStudentLinkFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where status does not contain
        defaultParentStudentLinkFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkedAt equals to
        defaultParentStudentLinkFiltering("linkedAt.equals=" + DEFAULT_LINKED_AT, "linkedAt.equals=" + UPDATED_LINKED_AT);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkedAt in
        defaultParentStudentLinkFiltering("linkedAt.in=" + DEFAULT_LINKED_AT + "," + UPDATED_LINKED_AT, "linkedAt.in=" + UPDATED_LINKED_AT);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByLinkedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where linkedAt is not null
        defaultParentStudentLinkFiltering("linkedAt.specified=true", "linkedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where createdAt equals to
        defaultParentStudentLinkFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where createdAt in
        defaultParentStudentLinkFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where createdAt is not null
        defaultParentStudentLinkFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCodeExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where codeExpiresAt equals to
        defaultParentStudentLinkFiltering(
            "codeExpiresAt.equals=" + DEFAULT_CODE_EXPIRES_AT,
            "codeExpiresAt.equals=" + UPDATED_CODE_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCodeExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where codeExpiresAt in
        defaultParentStudentLinkFiltering(
            "codeExpiresAt.in=" + DEFAULT_CODE_EXPIRES_AT + "," + UPDATED_CODE_EXPIRES_AT,
            "codeExpiresAt.in=" + UPDATED_CODE_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByCodeExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where codeExpiresAt is not null
        defaultParentStudentLinkFiltering("codeExpiresAt.specified=true", "codeExpiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where confirmedAt equals to
        defaultParentStudentLinkFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT, "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where confirmedAt in
        defaultParentStudentLinkFiltering(
            "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
            "confirmedAt.in=" + UPDATED_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where confirmedAt is not null
        defaultParentStudentLinkFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRevokedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where revokedAt equals to
        defaultParentStudentLinkFiltering("revokedAt.equals=" + DEFAULT_REVOKED_AT, "revokedAt.equals=" + UPDATED_REVOKED_AT);
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRevokedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where revokedAt in
        defaultParentStudentLinkFiltering(
            "revokedAt.in=" + DEFAULT_REVOKED_AT + "," + UPDATED_REVOKED_AT,
            "revokedAt.in=" + UPDATED_REVOKED_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRevokedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where revokedAt is not null
        defaultParentStudentLinkFiltering("revokedAt.specified=true", "revokedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRelationshipTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where relationshipType equals to
        defaultParentStudentLinkFiltering(
            "relationshipType.equals=" + DEFAULT_RELATIONSHIP_TYPE,
            "relationshipType.equals=" + UPDATED_RELATIONSHIP_TYPE
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRelationshipTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where relationshipType in
        defaultParentStudentLinkFiltering(
            "relationshipType.in=" + DEFAULT_RELATIONSHIP_TYPE + "," + UPDATED_RELATIONSHIP_TYPE,
            "relationshipType.in=" + UPDATED_RELATIONSHIP_TYPE
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRelationshipTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where relationshipType is not null
        defaultParentStudentLinkFiltering("relationshipType.specified=true", "relationshipType.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRelationshipTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where relationshipType contains
        defaultParentStudentLinkFiltering(
            "relationshipType.contains=" + DEFAULT_RELATIONSHIP_TYPE,
            "relationshipType.contains=" + UPDATED_RELATIONSHIP_TYPE
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByRelationshipTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where relationshipType does not contain
        defaultParentStudentLinkFiltering(
            "relationshipType.doesNotContain=" + UPDATED_RELATIONSHIP_TYPE,
            "relationshipType.doesNotContain=" + DEFAULT_RELATIONSHIP_TYPE
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerificationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerificationStatus equals to
        defaultParentStudentLinkFiltering(
            "guardianVerificationStatus.equals=" + DEFAULT_GUARDIAN_VERIFICATION_STATUS,
            "guardianVerificationStatus.equals=" + UPDATED_GUARDIAN_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerificationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerificationStatus in
        defaultParentStudentLinkFiltering(
            "guardianVerificationStatus.in=" + DEFAULT_GUARDIAN_VERIFICATION_STATUS + "," + UPDATED_GUARDIAN_VERIFICATION_STATUS,
            "guardianVerificationStatus.in=" + UPDATED_GUARDIAN_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerificationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerificationStatus is not null
        defaultParentStudentLinkFiltering("guardianVerificationStatus.specified=true", "guardianVerificationStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerificationStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerificationStatus contains
        defaultParentStudentLinkFiltering(
            "guardianVerificationStatus.contains=" + DEFAULT_GUARDIAN_VERIFICATION_STATUS,
            "guardianVerificationStatus.contains=" + UPDATED_GUARDIAN_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerificationStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerificationStatus does not contain
        defaultParentStudentLinkFiltering(
            "guardianVerificationStatus.doesNotContain=" + UPDATED_GUARDIAN_VERIFICATION_STATUS,
            "guardianVerificationStatus.doesNotContain=" + DEFAULT_GUARDIAN_VERIFICATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerifiedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerifiedAt equals to
        defaultParentStudentLinkFiltering(
            "guardianVerifiedAt.equals=" + DEFAULT_GUARDIAN_VERIFIED_AT,
            "guardianVerifiedAt.equals=" + UPDATED_GUARDIAN_VERIFIED_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerifiedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerifiedAt in
        defaultParentStudentLinkFiltering(
            "guardianVerifiedAt.in=" + DEFAULT_GUARDIAN_VERIFIED_AT + "," + UPDATED_GUARDIAN_VERIFIED_AT,
            "guardianVerifiedAt.in=" + UPDATED_GUARDIAN_VERIFIED_AT
        );
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerifiedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        // Get all the parentStudentLinkList where guardianVerifiedAt is not null
        defaultParentStudentLinkFiltering("guardianVerifiedAt.specified=true", "guardianVerifiedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByParentIsEqualToSomething() throws Exception {
        User parent;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            parentStudentLinkRepository.saveAndFlush(parentStudentLink);
            parent = UserResourceIT.createEntity();
        } else {
            parent = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(parent);
        em.flush();
        parentStudentLink.setParent(parent);
        parentStudentLinkRepository.saveAndFlush(parentStudentLink);
        Long parentId = parent.getId();
        // Get all the parentStudentLinkList where parent equals to parentId
        defaultParentStudentLinkShouldBeFound("parentId.equals=" + parentId);

        // Get all the parentStudentLinkList where parent equals to (parentId + 1)
        defaultParentStudentLinkShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            parentStudentLinkRepository.saveAndFlush(parentStudentLink);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        parentStudentLink.setStudent(student);
        parentStudentLinkRepository.saveAndFlush(parentStudentLink);
        Long studentId = student.getId();
        // Get all the parentStudentLinkList where student equals to studentId
        defaultParentStudentLinkShouldBeFound("studentId.equals=" + studentId);

        // Get all the parentStudentLinkList where student equals to (studentId + 1)
        defaultParentStudentLinkShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByConfirmedByIsEqualToSomething() throws Exception {
        User confirmedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            parentStudentLinkRepository.saveAndFlush(parentStudentLink);
            confirmedBy = UserResourceIT.createEntity();
        } else {
            confirmedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(confirmedBy);
        em.flush();
        parentStudentLink.setConfirmedBy(confirmedBy);
        parentStudentLinkRepository.saveAndFlush(parentStudentLink);
        Long confirmedById = confirmedBy.getId();
        // Get all the parentStudentLinkList where confirmedBy equals to confirmedById
        defaultParentStudentLinkShouldBeFound("confirmedById.equals=" + confirmedById);

        // Get all the parentStudentLinkList where confirmedBy equals to (confirmedById + 1)
        defaultParentStudentLinkShouldNotBeFound("confirmedById.equals=" + (confirmedById + 1));
    }

    @Test
    @Transactional
    void getAllParentStudentLinksByGuardianVerifiedByIsEqualToSomething() throws Exception {
        User guardianVerifiedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            parentStudentLinkRepository.saveAndFlush(parentStudentLink);
            guardianVerifiedBy = UserResourceIT.createEntity();
        } else {
            guardianVerifiedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(guardianVerifiedBy);
        em.flush();
        parentStudentLink.setGuardianVerifiedBy(guardianVerifiedBy);
        parentStudentLinkRepository.saveAndFlush(parentStudentLink);
        Long guardianVerifiedById = guardianVerifiedBy.getId();
        // Get all the parentStudentLinkList where guardianVerifiedBy equals to guardianVerifiedById
        defaultParentStudentLinkShouldBeFound("guardianVerifiedById.equals=" + guardianVerifiedById);

        // Get all the parentStudentLinkList where guardianVerifiedBy equals to (guardianVerifiedById + 1)
        defaultParentStudentLinkShouldNotBeFound("guardianVerifiedById.equals=" + (guardianVerifiedById + 1));
    }

    private void defaultParentStudentLinkFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParentStudentLinkShouldBeFound(shouldBeFound);
        defaultParentStudentLinkShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParentStudentLinkShouldBeFound(String filter) throws Exception {
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parentStudentLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].linkCode").value(hasItem(DEFAULT_LINK_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].linkedAt").value(hasItem(DEFAULT_LINKED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].codeExpiresAt").value(hasItem(DEFAULT_CODE_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].revokedAt").value(hasItem(DEFAULT_REVOKED_AT.toString())))
            .andExpect(jsonPath("$.[*].relationshipType").value(hasItem(DEFAULT_RELATIONSHIP_TYPE)))
            .andExpect(jsonPath("$.[*].guardianVerificationStatus").value(hasItem(DEFAULT_GUARDIAN_VERIFICATION_STATUS)))
            .andExpect(jsonPath("$.[*].guardianVerifiedAt").value(hasItem(DEFAULT_GUARDIAN_VERIFIED_AT.toString())))
            .andExpect(jsonPath("$.[*].guardianVerificationNote").value(hasItem(DEFAULT_GUARDIAN_VERIFICATION_NOTE)));

        // Check, that the count call also returns 1
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParentStudentLinkShouldNotBeFound(String filter) throws Exception {
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParentStudentLinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParentStudentLink() throws Exception {
        // Get the parentStudentLink
        restParentStudentLinkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParentStudentLink() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parentStudentLink
        ParentStudentLink updatedParentStudentLink = parentStudentLinkRepository.findById(parentStudentLink.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParentStudentLink are not directly saved in db
        em.detach(updatedParentStudentLink);
        updatedParentStudentLink
            .linkCode(UPDATED_LINK_CODE)
            .status(UPDATED_STATUS)
            .linkedAt(UPDATED_LINKED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .codeExpiresAt(UPDATED_CODE_EXPIRES_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .relationshipType(UPDATED_RELATIONSHIP_TYPE)
            .guardianVerificationStatus(UPDATED_GUARDIAN_VERIFICATION_STATUS)
            .guardianVerifiedAt(UPDATED_GUARDIAN_VERIFIED_AT)
            .guardianVerificationNote(UPDATED_GUARDIAN_VERIFICATION_NOTE);
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(updatedParentStudentLink);

        restParentStudentLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parentStudentLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parentStudentLinkDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParentStudentLinkToMatchAllProperties(updatedParentStudentLink);
    }

    @Test
    @Transactional
    void putNonExistingParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parentStudentLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parentStudentLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parentStudentLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParentStudentLinkWithPatch() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parentStudentLink using partial update
        ParentStudentLink partialUpdatedParentStudentLink = new ParentStudentLink();
        partialUpdatedParentStudentLink.setId(parentStudentLink.getId());

        partialUpdatedParentStudentLink
            .linkedAt(UPDATED_LINKED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .relationshipType(UPDATED_RELATIONSHIP_TYPE)
            .guardianVerifiedAt(UPDATED_GUARDIAN_VERIFIED_AT);

        restParentStudentLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParentStudentLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParentStudentLink))
            )
            .andExpect(status().isOk());

        // Validate the ParentStudentLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParentStudentLinkUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParentStudentLink, parentStudentLink),
            getPersistedParentStudentLink(parentStudentLink)
        );
    }

    @Test
    @Transactional
    void fullUpdateParentStudentLinkWithPatch() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parentStudentLink using partial update
        ParentStudentLink partialUpdatedParentStudentLink = new ParentStudentLink();
        partialUpdatedParentStudentLink.setId(parentStudentLink.getId());

        partialUpdatedParentStudentLink
            .linkCode(UPDATED_LINK_CODE)
            .status(UPDATED_STATUS)
            .linkedAt(UPDATED_LINKED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .codeExpiresAt(UPDATED_CODE_EXPIRES_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .relationshipType(UPDATED_RELATIONSHIP_TYPE)
            .guardianVerificationStatus(UPDATED_GUARDIAN_VERIFICATION_STATUS)
            .guardianVerifiedAt(UPDATED_GUARDIAN_VERIFIED_AT)
            .guardianVerificationNote(UPDATED_GUARDIAN_VERIFICATION_NOTE);

        restParentStudentLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParentStudentLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParentStudentLink))
            )
            .andExpect(status().isOk());

        // Validate the ParentStudentLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParentStudentLinkUpdatableFieldsEquals(
            partialUpdatedParentStudentLink,
            getPersistedParentStudentLink(partialUpdatedParentStudentLink)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parentStudentLinkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parentStudentLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parentStudentLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParentStudentLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parentStudentLink.setId(longCount.incrementAndGet());

        // Create the ParentStudentLink
        ParentStudentLinkDTO parentStudentLinkDTO = parentStudentLinkMapper.toDto(parentStudentLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParentStudentLinkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parentStudentLinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParentStudentLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParentStudentLink() throws Exception {
        // Initialize the database
        insertedParentStudentLink = parentStudentLinkRepository.saveAndFlush(parentStudentLink);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parentStudentLink
        restParentStudentLinkMockMvc
            .perform(delete(ENTITY_API_URL_ID, parentStudentLink.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parentStudentLinkRepository.count();
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

    protected ParentStudentLink getPersistedParentStudentLink(ParentStudentLink parentStudentLink) {
        return parentStudentLinkRepository.findById(parentStudentLink.getId()).orElseThrow();
    }

    protected void assertPersistedParentStudentLinkToMatchAllProperties(ParentStudentLink expectedParentStudentLink) {
        assertParentStudentLinkAllPropertiesEquals(expectedParentStudentLink, getPersistedParentStudentLink(expectedParentStudentLink));
    }

    protected void assertPersistedParentStudentLinkToMatchUpdatableProperties(ParentStudentLink expectedParentStudentLink) {
        assertParentStudentLinkAllUpdatablePropertiesEquals(
            expectedParentStudentLink,
            getPersistedParentStudentLink(expectedParentStudentLink)
        );
    }
}
