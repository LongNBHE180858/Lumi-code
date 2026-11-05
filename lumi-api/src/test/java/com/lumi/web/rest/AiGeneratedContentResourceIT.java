package com.lumi.web.rest;

import static com.lumi.domain.AiGeneratedContentAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.AiGeneratedContent;
import com.lumi.domain.Course;
import com.lumi.domain.LearningDocument;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.repository.AiGeneratedContentRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.AiGeneratedContentService;
import com.lumi.service.dto.AiGeneratedContentDTO;
import com.lumi.service.mapper.AiGeneratedContentMapper;
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
 * Integration tests for the {@link AiGeneratedContentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AiGeneratedContentResourceIT {

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_HASH = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATION_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_GENERATION_CONFIG = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROMPT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_PROMPT_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_GENERATION_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_VISIBILITY = "AAAAAAAAAA";
    private static final String UPDATED_VISIBILITY = "BBBBBBBBBB";

    private static final Long DEFAULT_VERSION = 1L;
    private static final Long UPDATED_VERSION = 2L;
    private static final Long SMALLER_VERSION = 1L - 1L;

    private static final String DEFAULT_REVIEW_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_REVIEWED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVIEWED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_REVIEWER_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_REVIEWER_FEEDBACK = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Long DEFAULT_SOURCE_QUESTION_ID = 1L;
    private static final Long UPDATED_SOURCE_QUESTION_ID = 2L;
    private static final Long SMALLER_SOURCE_QUESTION_ID = 1L - 1L;

    private static final String DEFAULT_CONTENT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_HASH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ai-generated-contents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AiGeneratedContentRepository aiGeneratedContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AiGeneratedContentRepository aiGeneratedContentRepositoryMock;

    @Autowired
    private AiGeneratedContentMapper aiGeneratedContentMapper;

    @Mock
    private AiGeneratedContentService aiGeneratedContentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAiGeneratedContentMockMvc;

    private AiGeneratedContent aiGeneratedContent;

    private AiGeneratedContent insertedAiGeneratedContent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiGeneratedContent createEntity(EntityManager em) {
        AiGeneratedContent aiGeneratedContent = new AiGeneratedContent()
            .contentType(DEFAULT_CONTENT_TYPE)
            .requestHash(DEFAULT_REQUEST_HASH)
            .generationConfig(DEFAULT_GENERATION_CONFIG)
            .sourceVersion(DEFAULT_SOURCE_VERSION)
            .modelName(DEFAULT_MODEL_NAME)
            .promptVersion(DEFAULT_PROMPT_VERSION)
            .content(DEFAULT_CONTENT)
            .generationStatus(DEFAULT_GENERATION_STATUS)
            .visibility(DEFAULT_VISIBILITY)
            .version(DEFAULT_VERSION)
            .reviewStatus(DEFAULT_REVIEW_STATUS)
            .reviewedAt(DEFAULT_REVIEWED_AT)
            .reviewerFeedback(DEFAULT_REVIEWER_FEEDBACK)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .sourceQuestionId(DEFAULT_SOURCE_QUESTION_ID)
            .contentHash(DEFAULT_CONTENT_HASH);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        aiGeneratedContent.setCreatedBy(user);
        return aiGeneratedContent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiGeneratedContent createUpdatedEntity(EntityManager em) {
        AiGeneratedContent updatedAiGeneratedContent = new AiGeneratedContent()
            .contentType(UPDATED_CONTENT_TYPE)
            .requestHash(UPDATED_REQUEST_HASH)
            .generationConfig(UPDATED_GENERATION_CONFIG)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .modelName(UPDATED_MODEL_NAME)
            .promptVersion(UPDATED_PROMPT_VERSION)
            .content(UPDATED_CONTENT)
            .generationStatus(UPDATED_GENERATION_STATUS)
            .visibility(UPDATED_VISIBILITY)
            .version(UPDATED_VERSION)
            .reviewStatus(UPDATED_REVIEW_STATUS)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .sourceQuestionId(UPDATED_SOURCE_QUESTION_ID)
            .contentHash(UPDATED_CONTENT_HASH);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAiGeneratedContent.setCreatedBy(user);
        return updatedAiGeneratedContent;
    }

    @BeforeEach
    void initTest() {
        aiGeneratedContent = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAiGeneratedContent != null) {
            aiGeneratedContentRepository.delete(insertedAiGeneratedContent);
            insertedAiGeneratedContent = null;
        }
    }

    @Test
    @Transactional
    void createAiGeneratedContent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);
        var returnedAiGeneratedContentDTO = om.readValue(
            restAiGeneratedContentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AiGeneratedContentDTO.class
        );

        // Validate the AiGeneratedContent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAiGeneratedContent = aiGeneratedContentMapper.toEntity(returnedAiGeneratedContentDTO);
        assertAiGeneratedContentUpdatableFieldsEquals(
            returnedAiGeneratedContent,
            getPersistedAiGeneratedContent(returnedAiGeneratedContent)
        );

        insertedAiGeneratedContent = returnedAiGeneratedContent;
    }

    @Test
    @Transactional
    void createAiGeneratedContentWithExistingId() throws Exception {
        // Create the AiGeneratedContent with an existing ID
        aiGeneratedContent.setId(1L);
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setContentType(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGenerationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setGenerationStatus(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVisibilityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setVisibility(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setVersion(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReviewStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setReviewStatus(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setCreatedAt(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiGeneratedContent.setUpdatedAt(null);

        // Create the AiGeneratedContent, which fails.
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContents() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiGeneratedContent.getId().intValue())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].requestHash").value(hasItem(DEFAULT_REQUEST_HASH)))
            .andExpect(jsonPath("$.[*].generationConfig").value(hasItem(DEFAULT_GENERATION_CONFIG)))
            .andExpect(jsonPath("$.[*].sourceVersion").value(hasItem(DEFAULT_SOURCE_VERSION)))
            .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME)))
            .andExpect(jsonPath("$.[*].promptVersion").value(hasItem(DEFAULT_PROMPT_VERSION)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].generationStatus").value(hasItem(DEFAULT_GENERATION_STATUS)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].reviewStatus").value(hasItem(DEFAULT_REVIEW_STATUS)))
            .andExpect(jsonPath("$.[*].reviewedAt").value(hasItem(DEFAULT_REVIEWED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewerFeedback").value(hasItem(DEFAULT_REVIEWER_FEEDBACK)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sourceQuestionId").value(hasItem(DEFAULT_SOURCE_QUESTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].contentHash").value(hasItem(DEFAULT_CONTENT_HASH)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAiGeneratedContentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(aiGeneratedContentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAiGeneratedContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(aiGeneratedContentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAiGeneratedContentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(aiGeneratedContentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAiGeneratedContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(aiGeneratedContentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAiGeneratedContent() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get the aiGeneratedContent
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL_ID, aiGeneratedContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aiGeneratedContent.getId().intValue()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.requestHash").value(DEFAULT_REQUEST_HASH))
            .andExpect(jsonPath("$.generationConfig").value(DEFAULT_GENERATION_CONFIG))
            .andExpect(jsonPath("$.sourceVersion").value(DEFAULT_SOURCE_VERSION))
            .andExpect(jsonPath("$.modelName").value(DEFAULT_MODEL_NAME))
            .andExpect(jsonPath("$.promptVersion").value(DEFAULT_PROMPT_VERSION))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.generationStatus").value(DEFAULT_GENERATION_STATUS))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
            .andExpect(jsonPath("$.reviewStatus").value(DEFAULT_REVIEW_STATUS))
            .andExpect(jsonPath("$.reviewedAt").value(DEFAULT_REVIEWED_AT.toString()))
            .andExpect(jsonPath("$.reviewerFeedback").value(DEFAULT_REVIEWER_FEEDBACK))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.sourceQuestionId").value(DEFAULT_SOURCE_QUESTION_ID.intValue()))
            .andExpect(jsonPath("$.contentHash").value(DEFAULT_CONTENT_HASH));
    }

    @Test
    @Transactional
    void getAiGeneratedContentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        Long id = aiGeneratedContent.getId();

        defaultAiGeneratedContentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAiGeneratedContentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAiGeneratedContentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentType equals to
        defaultAiGeneratedContentFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentType in
        defaultAiGeneratedContentFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentType is not null
        defaultAiGeneratedContentFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentType contains
        defaultAiGeneratedContentFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentType does not contain
        defaultAiGeneratedContentFiltering(
            "contentType.doesNotContain=" + UPDATED_CONTENT_TYPE,
            "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByRequestHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where requestHash equals to
        defaultAiGeneratedContentFiltering("requestHash.equals=" + DEFAULT_REQUEST_HASH, "requestHash.equals=" + UPDATED_REQUEST_HASH);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByRequestHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where requestHash in
        defaultAiGeneratedContentFiltering(
            "requestHash.in=" + DEFAULT_REQUEST_HASH + "," + UPDATED_REQUEST_HASH,
            "requestHash.in=" + UPDATED_REQUEST_HASH
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByRequestHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where requestHash is not null
        defaultAiGeneratedContentFiltering("requestHash.specified=true", "requestHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByRequestHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where requestHash contains
        defaultAiGeneratedContentFiltering("requestHash.contains=" + DEFAULT_REQUEST_HASH, "requestHash.contains=" + UPDATED_REQUEST_HASH);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByRequestHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where requestHash does not contain
        defaultAiGeneratedContentFiltering(
            "requestHash.doesNotContain=" + UPDATED_REQUEST_HASH,
            "requestHash.doesNotContain=" + DEFAULT_REQUEST_HASH
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceVersion equals to
        defaultAiGeneratedContentFiltering(
            "sourceVersion.equals=" + DEFAULT_SOURCE_VERSION,
            "sourceVersion.equals=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceVersion in
        defaultAiGeneratedContentFiltering(
            "sourceVersion.in=" + DEFAULT_SOURCE_VERSION + "," + UPDATED_SOURCE_VERSION,
            "sourceVersion.in=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceVersion is not null
        defaultAiGeneratedContentFiltering("sourceVersion.specified=true", "sourceVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceVersionContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceVersion contains
        defaultAiGeneratedContentFiltering(
            "sourceVersion.contains=" + DEFAULT_SOURCE_VERSION,
            "sourceVersion.contains=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceVersionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceVersion does not contain
        defaultAiGeneratedContentFiltering(
            "sourceVersion.doesNotContain=" + UPDATED_SOURCE_VERSION,
            "sourceVersion.doesNotContain=" + DEFAULT_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByModelNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where modelName equals to
        defaultAiGeneratedContentFiltering("modelName.equals=" + DEFAULT_MODEL_NAME, "modelName.equals=" + UPDATED_MODEL_NAME);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByModelNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where modelName in
        defaultAiGeneratedContentFiltering(
            "modelName.in=" + DEFAULT_MODEL_NAME + "," + UPDATED_MODEL_NAME,
            "modelName.in=" + UPDATED_MODEL_NAME
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByModelNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where modelName is not null
        defaultAiGeneratedContentFiltering("modelName.specified=true", "modelName.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByModelNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where modelName contains
        defaultAiGeneratedContentFiltering("modelName.contains=" + DEFAULT_MODEL_NAME, "modelName.contains=" + UPDATED_MODEL_NAME);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByModelNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where modelName does not contain
        defaultAiGeneratedContentFiltering(
            "modelName.doesNotContain=" + UPDATED_MODEL_NAME,
            "modelName.doesNotContain=" + DEFAULT_MODEL_NAME
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByPromptVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where promptVersion equals to
        defaultAiGeneratedContentFiltering(
            "promptVersion.equals=" + DEFAULT_PROMPT_VERSION,
            "promptVersion.equals=" + UPDATED_PROMPT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByPromptVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where promptVersion in
        defaultAiGeneratedContentFiltering(
            "promptVersion.in=" + DEFAULT_PROMPT_VERSION + "," + UPDATED_PROMPT_VERSION,
            "promptVersion.in=" + UPDATED_PROMPT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByPromptVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where promptVersion is not null
        defaultAiGeneratedContentFiltering("promptVersion.specified=true", "promptVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByPromptVersionContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where promptVersion contains
        defaultAiGeneratedContentFiltering(
            "promptVersion.contains=" + DEFAULT_PROMPT_VERSION,
            "promptVersion.contains=" + UPDATED_PROMPT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByPromptVersionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where promptVersion does not contain
        defaultAiGeneratedContentFiltering(
            "promptVersion.doesNotContain=" + UPDATED_PROMPT_VERSION,
            "promptVersion.doesNotContain=" + DEFAULT_PROMPT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByGenerationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where generationStatus equals to
        defaultAiGeneratedContentFiltering(
            "generationStatus.equals=" + DEFAULT_GENERATION_STATUS,
            "generationStatus.equals=" + UPDATED_GENERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByGenerationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where generationStatus in
        defaultAiGeneratedContentFiltering(
            "generationStatus.in=" + DEFAULT_GENERATION_STATUS + "," + UPDATED_GENERATION_STATUS,
            "generationStatus.in=" + UPDATED_GENERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByGenerationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where generationStatus is not null
        defaultAiGeneratedContentFiltering("generationStatus.specified=true", "generationStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByGenerationStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where generationStatus contains
        defaultAiGeneratedContentFiltering(
            "generationStatus.contains=" + DEFAULT_GENERATION_STATUS,
            "generationStatus.contains=" + UPDATED_GENERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByGenerationStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where generationStatus does not contain
        defaultAiGeneratedContentFiltering(
            "generationStatus.doesNotContain=" + UPDATED_GENERATION_STATUS,
            "generationStatus.doesNotContain=" + DEFAULT_GENERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where visibility equals to
        defaultAiGeneratedContentFiltering("visibility.equals=" + DEFAULT_VISIBILITY, "visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where visibility in
        defaultAiGeneratedContentFiltering(
            "visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY,
            "visibility.in=" + UPDATED_VISIBILITY
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where visibility is not null
        defaultAiGeneratedContentFiltering("visibility.specified=true", "visibility.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVisibilityContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where visibility contains
        defaultAiGeneratedContentFiltering("visibility.contains=" + DEFAULT_VISIBILITY, "visibility.contains=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVisibilityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where visibility does not contain
        defaultAiGeneratedContentFiltering(
            "visibility.doesNotContain=" + UPDATED_VISIBILITY,
            "visibility.doesNotContain=" + DEFAULT_VISIBILITY
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version equals to
        defaultAiGeneratedContentFiltering("version.equals=" + DEFAULT_VERSION, "version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version in
        defaultAiGeneratedContentFiltering("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION, "version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version is not null
        defaultAiGeneratedContentFiltering("version.specified=true", "version.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version is greater than or equal to
        defaultAiGeneratedContentFiltering(
            "version.greaterThanOrEqual=" + DEFAULT_VERSION,
            "version.greaterThanOrEqual=" + UPDATED_VERSION
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version is less than or equal to
        defaultAiGeneratedContentFiltering("version.lessThanOrEqual=" + DEFAULT_VERSION, "version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version is less than
        defaultAiGeneratedContentFiltering("version.lessThan=" + UPDATED_VERSION, "version.lessThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where version is greater than
        defaultAiGeneratedContentFiltering("version.greaterThan=" + SMALLER_VERSION, "version.greaterThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewStatus equals to
        defaultAiGeneratedContentFiltering("reviewStatus.equals=" + DEFAULT_REVIEW_STATUS, "reviewStatus.equals=" + UPDATED_REVIEW_STATUS);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewStatus in
        defaultAiGeneratedContentFiltering(
            "reviewStatus.in=" + DEFAULT_REVIEW_STATUS + "," + UPDATED_REVIEW_STATUS,
            "reviewStatus.in=" + UPDATED_REVIEW_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewStatus is not null
        defaultAiGeneratedContentFiltering("reviewStatus.specified=true", "reviewStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewStatus contains
        defaultAiGeneratedContentFiltering(
            "reviewStatus.contains=" + DEFAULT_REVIEW_STATUS,
            "reviewStatus.contains=" + UPDATED_REVIEW_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewStatus does not contain
        defaultAiGeneratedContentFiltering(
            "reviewStatus.doesNotContain=" + UPDATED_REVIEW_STATUS,
            "reviewStatus.doesNotContain=" + DEFAULT_REVIEW_STATUS
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewedAt equals to
        defaultAiGeneratedContentFiltering("reviewedAt.equals=" + DEFAULT_REVIEWED_AT, "reviewedAt.equals=" + UPDATED_REVIEWED_AT);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewedAt in
        defaultAiGeneratedContentFiltering(
            "reviewedAt.in=" + DEFAULT_REVIEWED_AT + "," + UPDATED_REVIEWED_AT,
            "reviewedAt.in=" + UPDATED_REVIEWED_AT
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where reviewedAt is not null
        defaultAiGeneratedContentFiltering("reviewedAt.specified=true", "reviewedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where createdAt equals to
        defaultAiGeneratedContentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where createdAt in
        defaultAiGeneratedContentFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where createdAt is not null
        defaultAiGeneratedContentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where updatedAt equals to
        defaultAiGeneratedContentFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where updatedAt in
        defaultAiGeneratedContentFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where updatedAt is not null
        defaultAiGeneratedContentFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId equals to
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.equals=" + DEFAULT_SOURCE_QUESTION_ID,
            "sourceQuestionId.equals=" + UPDATED_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId in
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.in=" + DEFAULT_SOURCE_QUESTION_ID + "," + UPDATED_SOURCE_QUESTION_ID,
            "sourceQuestionId.in=" + UPDATED_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId is not null
        defaultAiGeneratedContentFiltering("sourceQuestionId.specified=true", "sourceQuestionId.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId is greater than or equal to
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.greaterThanOrEqual=" + DEFAULT_SOURCE_QUESTION_ID,
            "sourceQuestionId.greaterThanOrEqual=" + UPDATED_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId is less than or equal to
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.lessThanOrEqual=" + DEFAULT_SOURCE_QUESTION_ID,
            "sourceQuestionId.lessThanOrEqual=" + SMALLER_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId is less than
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.lessThan=" + UPDATED_SOURCE_QUESTION_ID,
            "sourceQuestionId.lessThan=" + DEFAULT_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsBySourceQuestionIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where sourceQuestionId is greater than
        defaultAiGeneratedContentFiltering(
            "sourceQuestionId.greaterThan=" + SMALLER_SOURCE_QUESTION_ID,
            "sourceQuestionId.greaterThan=" + DEFAULT_SOURCE_QUESTION_ID
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentHash equals to
        defaultAiGeneratedContentFiltering("contentHash.equals=" + DEFAULT_CONTENT_HASH, "contentHash.equals=" + UPDATED_CONTENT_HASH);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentHash in
        defaultAiGeneratedContentFiltering(
            "contentHash.in=" + DEFAULT_CONTENT_HASH + "," + UPDATED_CONTENT_HASH,
            "contentHash.in=" + UPDATED_CONTENT_HASH
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentHash is not null
        defaultAiGeneratedContentFiltering("contentHash.specified=true", "contentHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentHash contains
        defaultAiGeneratedContentFiltering("contentHash.contains=" + DEFAULT_CONTENT_HASH, "contentHash.contains=" + UPDATED_CONTENT_HASH);
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByContentHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        // Get all the aiGeneratedContentList where contentHash does not contain
        defaultAiGeneratedContentFiltering(
            "contentHash.doesNotContain=" + UPDATED_CONTENT_HASH,
            "contentHash.doesNotContain=" + DEFAULT_CONTENT_HASH
        );
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
            createdBy = UserResourceIT.createEntity();
        } else {
            createdBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(createdBy);
        em.flush();
        aiGeneratedContent.setCreatedBy(createdBy);
        aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
        Long createdById = createdBy.getId();
        // Get all the aiGeneratedContentList where createdBy equals to createdById
        defaultAiGeneratedContentShouldBeFound("createdById.equals=" + createdById);

        // Get all the aiGeneratedContentList where createdBy equals to (createdById + 1)
        defaultAiGeneratedContentShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByDocumentIsEqualToSomething() throws Exception {
        LearningDocument document;
        if (TestUtil.findAll(em, LearningDocument.class).isEmpty()) {
            aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
            document = LearningDocumentResourceIT.createEntity(em);
        } else {
            document = TestUtil.findAll(em, LearningDocument.class).getFirst();
        }
        em.persist(document);
        em.flush();
        aiGeneratedContent.setDocument(document);
        aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
        Long documentId = document.getId();
        // Get all the aiGeneratedContentList where document equals to documentId
        defaultAiGeneratedContentShouldBeFound("documentId.equals=" + documentId);

        // Get all the aiGeneratedContentList where document equals to (documentId + 1)
        defaultAiGeneratedContentShouldNotBeFound("documentId.equals=" + (documentId + 1));
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        aiGeneratedContent.setLesson(lesson);
        aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
        Long lessonId = lesson.getId();
        // Get all the aiGeneratedContentList where lesson equals to lessonId
        defaultAiGeneratedContentShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the aiGeneratedContentList where lesson equals to (lessonId + 1)
        defaultAiGeneratedContentShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByReviewedByIsEqualToSomething() throws Exception {
        User reviewedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
            reviewedBy = UserResourceIT.createEntity();
        } else {
            reviewedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(reviewedBy);
        em.flush();
        aiGeneratedContent.setReviewedBy(reviewedBy);
        aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
        Long reviewedById = reviewedBy.getId();
        // Get all the aiGeneratedContentList where reviewedBy equals to reviewedById
        defaultAiGeneratedContentShouldBeFound("reviewedById.equals=" + reviewedById);

        // Get all the aiGeneratedContentList where reviewedBy equals to (reviewedById + 1)
        defaultAiGeneratedContentShouldNotBeFound("reviewedById.equals=" + (reviewedById + 1));
    }

    @Test
    @Transactional
    void getAllAiGeneratedContentsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        aiGeneratedContent.setCourse(course);
        aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);
        Long courseId = course.getId();
        // Get all the aiGeneratedContentList where course equals to courseId
        defaultAiGeneratedContentShouldBeFound("courseId.equals=" + courseId);

        // Get all the aiGeneratedContentList where course equals to (courseId + 1)
        defaultAiGeneratedContentShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    private void defaultAiGeneratedContentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAiGeneratedContentShouldBeFound(shouldBeFound);
        defaultAiGeneratedContentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAiGeneratedContentShouldBeFound(String filter) throws Exception {
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiGeneratedContent.getId().intValue())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].requestHash").value(hasItem(DEFAULT_REQUEST_HASH)))
            .andExpect(jsonPath("$.[*].generationConfig").value(hasItem(DEFAULT_GENERATION_CONFIG)))
            .andExpect(jsonPath("$.[*].sourceVersion").value(hasItem(DEFAULT_SOURCE_VERSION)))
            .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME)))
            .andExpect(jsonPath("$.[*].promptVersion").value(hasItem(DEFAULT_PROMPT_VERSION)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].generationStatus").value(hasItem(DEFAULT_GENERATION_STATUS)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].reviewStatus").value(hasItem(DEFAULT_REVIEW_STATUS)))
            .andExpect(jsonPath("$.[*].reviewedAt").value(hasItem(DEFAULT_REVIEWED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewerFeedback").value(hasItem(DEFAULT_REVIEWER_FEEDBACK)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sourceQuestionId").value(hasItem(DEFAULT_SOURCE_QUESTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].contentHash").value(hasItem(DEFAULT_CONTENT_HASH)));

        // Check, that the count call also returns 1
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAiGeneratedContentShouldNotBeFound(String filter) throws Exception {
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAiGeneratedContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAiGeneratedContent() throws Exception {
        // Get the aiGeneratedContent
        restAiGeneratedContentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAiGeneratedContent() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiGeneratedContent
        AiGeneratedContent updatedAiGeneratedContent = aiGeneratedContentRepository.findById(aiGeneratedContent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAiGeneratedContent are not directly saved in db
        em.detach(updatedAiGeneratedContent);
        updatedAiGeneratedContent
            .contentType(UPDATED_CONTENT_TYPE)
            .requestHash(UPDATED_REQUEST_HASH)
            .generationConfig(UPDATED_GENERATION_CONFIG)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .modelName(UPDATED_MODEL_NAME)
            .promptVersion(UPDATED_PROMPT_VERSION)
            .content(UPDATED_CONTENT)
            .generationStatus(UPDATED_GENERATION_STATUS)
            .visibility(UPDATED_VISIBILITY)
            .version(UPDATED_VERSION)
            .reviewStatus(UPDATED_REVIEW_STATUS)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .sourceQuestionId(UPDATED_SOURCE_QUESTION_ID)
            .contentHash(UPDATED_CONTENT_HASH);
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(updatedAiGeneratedContent);

        restAiGeneratedContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiGeneratedContentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiGeneratedContentDTO))
            )
            .andExpect(status().isOk());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAiGeneratedContentToMatchAllProperties(updatedAiGeneratedContent);
    }

    @Test
    @Transactional
    void putNonExistingAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiGeneratedContentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiGeneratedContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiGeneratedContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAiGeneratedContentWithPatch() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiGeneratedContent using partial update
        AiGeneratedContent partialUpdatedAiGeneratedContent = new AiGeneratedContent();
        partialUpdatedAiGeneratedContent.setId(aiGeneratedContent.getId());

        partialUpdatedAiGeneratedContent
            .contentType(UPDATED_CONTENT_TYPE)
            .modelName(UPDATED_MODEL_NAME)
            .promptVersion(UPDATED_PROMPT_VERSION)
            .version(UPDATED_VERSION)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .sourceQuestionId(UPDATED_SOURCE_QUESTION_ID)
            .contentHash(UPDATED_CONTENT_HASH);

        restAiGeneratedContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiGeneratedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiGeneratedContent))
            )
            .andExpect(status().isOk());

        // Validate the AiGeneratedContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiGeneratedContentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAiGeneratedContent, aiGeneratedContent),
            getPersistedAiGeneratedContent(aiGeneratedContent)
        );
    }

    @Test
    @Transactional
    void fullUpdateAiGeneratedContentWithPatch() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiGeneratedContent using partial update
        AiGeneratedContent partialUpdatedAiGeneratedContent = new AiGeneratedContent();
        partialUpdatedAiGeneratedContent.setId(aiGeneratedContent.getId());

        partialUpdatedAiGeneratedContent
            .contentType(UPDATED_CONTENT_TYPE)
            .requestHash(UPDATED_REQUEST_HASH)
            .generationConfig(UPDATED_GENERATION_CONFIG)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .modelName(UPDATED_MODEL_NAME)
            .promptVersion(UPDATED_PROMPT_VERSION)
            .content(UPDATED_CONTENT)
            .generationStatus(UPDATED_GENERATION_STATUS)
            .visibility(UPDATED_VISIBILITY)
            .version(UPDATED_VERSION)
            .reviewStatus(UPDATED_REVIEW_STATUS)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .sourceQuestionId(UPDATED_SOURCE_QUESTION_ID)
            .contentHash(UPDATED_CONTENT_HASH);

        restAiGeneratedContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiGeneratedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiGeneratedContent))
            )
            .andExpect(status().isOk());

        // Validate the AiGeneratedContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiGeneratedContentUpdatableFieldsEquals(
            partialUpdatedAiGeneratedContent,
            getPersistedAiGeneratedContent(partialUpdatedAiGeneratedContent)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aiGeneratedContentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiGeneratedContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiGeneratedContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAiGeneratedContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiGeneratedContent.setId(longCount.incrementAndGet());

        // Create the AiGeneratedContent
        AiGeneratedContentDTO aiGeneratedContentDTO = aiGeneratedContentMapper.toDto(aiGeneratedContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiGeneratedContentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aiGeneratedContentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiGeneratedContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAiGeneratedContent() throws Exception {
        // Initialize the database
        insertedAiGeneratedContent = aiGeneratedContentRepository.saveAndFlush(aiGeneratedContent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aiGeneratedContent
        restAiGeneratedContentMockMvc
            .perform(delete(ENTITY_API_URL_ID, aiGeneratedContent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return aiGeneratedContentRepository.count();
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

    protected AiGeneratedContent getPersistedAiGeneratedContent(AiGeneratedContent aiGeneratedContent) {
        return aiGeneratedContentRepository.findById(aiGeneratedContent.getId()).orElseThrow();
    }

    protected void assertPersistedAiGeneratedContentToMatchAllProperties(AiGeneratedContent expectedAiGeneratedContent) {
        assertAiGeneratedContentAllPropertiesEquals(expectedAiGeneratedContent, getPersistedAiGeneratedContent(expectedAiGeneratedContent));
    }

    protected void assertPersistedAiGeneratedContentToMatchUpdatableProperties(AiGeneratedContent expectedAiGeneratedContent) {
        assertAiGeneratedContentAllUpdatablePropertiesEquals(
            expectedAiGeneratedContent,
            getPersistedAiGeneratedContent(expectedAiGeneratedContent)
        );
    }
}
