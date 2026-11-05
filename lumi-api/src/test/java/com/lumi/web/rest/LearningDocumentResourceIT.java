package com.lumi.web.rest;

import static com.lumi.domain.LearningDocumentAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ClassMaterial;
import com.lumi.domain.CourseMaterial;
import com.lumi.domain.Grade;
import com.lumi.domain.LearningDocument;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.LearningDocumentService;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.mapper.LearningDocumentMapper;
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
 * Integration tests for the {@link LearningDocumentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LearningDocumentResourceIT {

    private static final String DEFAULT_SOURCE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_FILE_OBJECT_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_HASH = "BBBBBBBBBB";

    private static final Integer DEFAULT_SOURCE_VERSION = 1;
    private static final Integer UPDATED_SOURCE_VERSION = 2;
    private static final Integer SMALLER_SOURCE_VERSION = 1 - 1;

    private static final String DEFAULT_PROCESSING_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PROCESSING_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_INDEX_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_INDEX_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_EXTRACTED_TEXT_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_EXTRACTED_TEXT_OBJECT_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_EMBEDDING_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_EMBEDDING_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_BOOK_SERIES = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_SERIES = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/learning-documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LearningDocumentRepository learningDocumentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private LearningDocumentRepository learningDocumentRepositoryMock;

    @Autowired
    private LearningDocumentMapper learningDocumentMapper;

    @Mock
    private LearningDocumentService learningDocumentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLearningDocumentMockMvc;

    private LearningDocument learningDocument;

    private LearningDocument insertedLearningDocument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LearningDocument createEntity(EntityManager em) {
        LearningDocument learningDocument = new LearningDocument()
            .sourceType(DEFAULT_SOURCE_TYPE)
            .title(DEFAULT_TITLE)
            .fileObjectKey(DEFAULT_FILE_OBJECT_KEY)
            .contentHash(DEFAULT_CONTENT_HASH)
            .sourceVersion(DEFAULT_SOURCE_VERSION)
            .processingStatus(DEFAULT_PROCESSING_STATUS)
            .indexStatus(DEFAULT_INDEX_STATUS)
            .extractedTextObjectKey(DEFAULT_EXTRACTED_TEXT_OBJECT_KEY)
            .embeddingModel(DEFAULT_EMBEDDING_MODEL)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .bookSeries(DEFAULT_BOOK_SERIES)
            .sourceMetadata(DEFAULT_SOURCE_METADATA);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        learningDocument.setUploadedBy(user);
        return learningDocument;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LearningDocument createUpdatedEntity(EntityManager em) {
        LearningDocument updatedLearningDocument = new LearningDocument()
            .sourceType(UPDATED_SOURCE_TYPE)
            .title(UPDATED_TITLE)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .contentHash(UPDATED_CONTENT_HASH)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .processingStatus(UPDATED_PROCESSING_STATUS)
            .indexStatus(UPDATED_INDEX_STATUS)
            .extractedTextObjectKey(UPDATED_EXTRACTED_TEXT_OBJECT_KEY)
            .embeddingModel(UPDATED_EMBEDDING_MODEL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .bookSeries(UPDATED_BOOK_SERIES)
            .sourceMetadata(UPDATED_SOURCE_METADATA);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedLearningDocument.setUploadedBy(user);
        return updatedLearningDocument;
    }

    @BeforeEach
    void initTest() {
        learningDocument = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLearningDocument != null) {
            learningDocumentRepository.delete(insertedLearningDocument);
            insertedLearningDocument = null;
        }
    }

    @Test
    @Transactional
    void createLearningDocument() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);
        var returnedLearningDocumentDTO = om.readValue(
            restLearningDocumentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LearningDocumentDTO.class
        );

        // Validate the LearningDocument in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLearningDocument = learningDocumentMapper.toEntity(returnedLearningDocumentDTO);
        assertLearningDocumentUpdatableFieldsEquals(returnedLearningDocument, getPersistedLearningDocument(returnedLearningDocument));

        insertedLearningDocument = returnedLearningDocument;
    }

    @Test
    @Transactional
    void createLearningDocumentWithExistingId() throws Exception {
        // Create the LearningDocument with an existing ID
        learningDocument.setId(1L);
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSourceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setSourceType(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setTitle(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileObjectKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setFileObjectKey(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSourceVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setSourceVersion(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProcessingStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setProcessingStatus(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIndexStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setIndexStatus(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setCreatedAt(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        learningDocument.setUpdatedAt(null);

        // Create the LearningDocument, which fails.
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        restLearningDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLearningDocuments() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(learningDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceType").value(hasItem(DEFAULT_SOURCE_TYPE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].contentHash").value(hasItem(DEFAULT_CONTENT_HASH)))
            .andExpect(jsonPath("$.[*].sourceVersion").value(hasItem(DEFAULT_SOURCE_VERSION)))
            .andExpect(jsonPath("$.[*].processingStatus").value(hasItem(DEFAULT_PROCESSING_STATUS)))
            .andExpect(jsonPath("$.[*].indexStatus").value(hasItem(DEFAULT_INDEX_STATUS)))
            .andExpect(jsonPath("$.[*].extractedTextObjectKey").value(hasItem(DEFAULT_EXTRACTED_TEXT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].embeddingModel").value(hasItem(DEFAULT_EMBEDDING_MODEL)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].bookSeries").value(hasItem(DEFAULT_BOOK_SERIES)))
            .andExpect(jsonPath("$.[*].sourceMetadata").value(hasItem(DEFAULT_SOURCE_METADATA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLearningDocumentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(learningDocumentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLearningDocumentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(learningDocumentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLearningDocumentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(learningDocumentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLearningDocumentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(learningDocumentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLearningDocument() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get the learningDocument
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, learningDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(learningDocument.getId().intValue()))
            .andExpect(jsonPath("$.sourceType").value(DEFAULT_SOURCE_TYPE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.fileObjectKey").value(DEFAULT_FILE_OBJECT_KEY))
            .andExpect(jsonPath("$.contentHash").value(DEFAULT_CONTENT_HASH))
            .andExpect(jsonPath("$.sourceVersion").value(DEFAULT_SOURCE_VERSION))
            .andExpect(jsonPath("$.processingStatus").value(DEFAULT_PROCESSING_STATUS))
            .andExpect(jsonPath("$.indexStatus").value(DEFAULT_INDEX_STATUS))
            .andExpect(jsonPath("$.extractedTextObjectKey").value(DEFAULT_EXTRACTED_TEXT_OBJECT_KEY))
            .andExpect(jsonPath("$.embeddingModel").value(DEFAULT_EMBEDDING_MODEL))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.bookSeries").value(DEFAULT_BOOK_SERIES))
            .andExpect(jsonPath("$.sourceMetadata").value(DEFAULT_SOURCE_METADATA));
    }

    @Test
    @Transactional
    void getLearningDocumentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        Long id = learningDocument.getId();

        defaultLearningDocumentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLearningDocumentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLearningDocumentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceType equals to
        defaultLearningDocumentFiltering("sourceType.equals=" + DEFAULT_SOURCE_TYPE, "sourceType.equals=" + UPDATED_SOURCE_TYPE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceType in
        defaultLearningDocumentFiltering(
            "sourceType.in=" + DEFAULT_SOURCE_TYPE + "," + UPDATED_SOURCE_TYPE,
            "sourceType.in=" + UPDATED_SOURCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceType is not null
        defaultLearningDocumentFiltering("sourceType.specified=true", "sourceType.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceType contains
        defaultLearningDocumentFiltering("sourceType.contains=" + DEFAULT_SOURCE_TYPE, "sourceType.contains=" + UPDATED_SOURCE_TYPE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceType does not contain
        defaultLearningDocumentFiltering(
            "sourceType.doesNotContain=" + UPDATED_SOURCE_TYPE,
            "sourceType.doesNotContain=" + DEFAULT_SOURCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where title equals to
        defaultLearningDocumentFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where title in
        defaultLearningDocumentFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where title is not null
        defaultLearningDocumentFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where title contains
        defaultLearningDocumentFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where title does not contain
        defaultLearningDocumentFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByFileObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where fileObjectKey equals to
        defaultLearningDocumentFiltering(
            "fileObjectKey.equals=" + DEFAULT_FILE_OBJECT_KEY,
            "fileObjectKey.equals=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByFileObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where fileObjectKey in
        defaultLearningDocumentFiltering(
            "fileObjectKey.in=" + DEFAULT_FILE_OBJECT_KEY + "," + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.in=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByFileObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where fileObjectKey is not null
        defaultLearningDocumentFiltering("fileObjectKey.specified=true", "fileObjectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByFileObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where fileObjectKey contains
        defaultLearningDocumentFiltering(
            "fileObjectKey.contains=" + DEFAULT_FILE_OBJECT_KEY,
            "fileObjectKey.contains=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByFileObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where fileObjectKey does not contain
        defaultLearningDocumentFiltering(
            "fileObjectKey.doesNotContain=" + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.doesNotContain=" + DEFAULT_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByContentHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where contentHash equals to
        defaultLearningDocumentFiltering("contentHash.equals=" + DEFAULT_CONTENT_HASH, "contentHash.equals=" + UPDATED_CONTENT_HASH);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByContentHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where contentHash in
        defaultLearningDocumentFiltering(
            "contentHash.in=" + DEFAULT_CONTENT_HASH + "," + UPDATED_CONTENT_HASH,
            "contentHash.in=" + UPDATED_CONTENT_HASH
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByContentHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where contentHash is not null
        defaultLearningDocumentFiltering("contentHash.specified=true", "contentHash.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByContentHashContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where contentHash contains
        defaultLearningDocumentFiltering("contentHash.contains=" + DEFAULT_CONTENT_HASH, "contentHash.contains=" + UPDATED_CONTENT_HASH);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByContentHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where contentHash does not contain
        defaultLearningDocumentFiltering(
            "contentHash.doesNotContain=" + UPDATED_CONTENT_HASH,
            "contentHash.doesNotContain=" + DEFAULT_CONTENT_HASH
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion equals to
        defaultLearningDocumentFiltering(
            "sourceVersion.equals=" + DEFAULT_SOURCE_VERSION,
            "sourceVersion.equals=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion in
        defaultLearningDocumentFiltering(
            "sourceVersion.in=" + DEFAULT_SOURCE_VERSION + "," + UPDATED_SOURCE_VERSION,
            "sourceVersion.in=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion is not null
        defaultLearningDocumentFiltering("sourceVersion.specified=true", "sourceVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion is greater than or equal to
        defaultLearningDocumentFiltering(
            "sourceVersion.greaterThanOrEqual=" + DEFAULT_SOURCE_VERSION,
            "sourceVersion.greaterThanOrEqual=" + UPDATED_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion is less than or equal to
        defaultLearningDocumentFiltering(
            "sourceVersion.lessThanOrEqual=" + DEFAULT_SOURCE_VERSION,
            "sourceVersion.lessThanOrEqual=" + SMALLER_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion is less than
        defaultLearningDocumentFiltering(
            "sourceVersion.lessThan=" + UPDATED_SOURCE_VERSION,
            "sourceVersion.lessThan=" + DEFAULT_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySourceVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where sourceVersion is greater than
        defaultLearningDocumentFiltering(
            "sourceVersion.greaterThan=" + SMALLER_SOURCE_VERSION,
            "sourceVersion.greaterThan=" + DEFAULT_SOURCE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByProcessingStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where processingStatus equals to
        defaultLearningDocumentFiltering(
            "processingStatus.equals=" + DEFAULT_PROCESSING_STATUS,
            "processingStatus.equals=" + UPDATED_PROCESSING_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByProcessingStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where processingStatus in
        defaultLearningDocumentFiltering(
            "processingStatus.in=" + DEFAULT_PROCESSING_STATUS + "," + UPDATED_PROCESSING_STATUS,
            "processingStatus.in=" + UPDATED_PROCESSING_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByProcessingStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where processingStatus is not null
        defaultLearningDocumentFiltering("processingStatus.specified=true", "processingStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByProcessingStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where processingStatus contains
        defaultLearningDocumentFiltering(
            "processingStatus.contains=" + DEFAULT_PROCESSING_STATUS,
            "processingStatus.contains=" + UPDATED_PROCESSING_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByProcessingStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where processingStatus does not contain
        defaultLearningDocumentFiltering(
            "processingStatus.doesNotContain=" + UPDATED_PROCESSING_STATUS,
            "processingStatus.doesNotContain=" + DEFAULT_PROCESSING_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByIndexStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where indexStatus equals to
        defaultLearningDocumentFiltering("indexStatus.equals=" + DEFAULT_INDEX_STATUS, "indexStatus.equals=" + UPDATED_INDEX_STATUS);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByIndexStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where indexStatus in
        defaultLearningDocumentFiltering(
            "indexStatus.in=" + DEFAULT_INDEX_STATUS + "," + UPDATED_INDEX_STATUS,
            "indexStatus.in=" + UPDATED_INDEX_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByIndexStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where indexStatus is not null
        defaultLearningDocumentFiltering("indexStatus.specified=true", "indexStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByIndexStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where indexStatus contains
        defaultLearningDocumentFiltering("indexStatus.contains=" + DEFAULT_INDEX_STATUS, "indexStatus.contains=" + UPDATED_INDEX_STATUS);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByIndexStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where indexStatus does not contain
        defaultLearningDocumentFiltering(
            "indexStatus.doesNotContain=" + UPDATED_INDEX_STATUS,
            "indexStatus.doesNotContain=" + DEFAULT_INDEX_STATUS
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByExtractedTextObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where extractedTextObjectKey equals to
        defaultLearningDocumentFiltering(
            "extractedTextObjectKey.equals=" + DEFAULT_EXTRACTED_TEXT_OBJECT_KEY,
            "extractedTextObjectKey.equals=" + UPDATED_EXTRACTED_TEXT_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByExtractedTextObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where extractedTextObjectKey in
        defaultLearningDocumentFiltering(
            "extractedTextObjectKey.in=" + DEFAULT_EXTRACTED_TEXT_OBJECT_KEY + "," + UPDATED_EXTRACTED_TEXT_OBJECT_KEY,
            "extractedTextObjectKey.in=" + UPDATED_EXTRACTED_TEXT_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByExtractedTextObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where extractedTextObjectKey is not null
        defaultLearningDocumentFiltering("extractedTextObjectKey.specified=true", "extractedTextObjectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByExtractedTextObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where extractedTextObjectKey contains
        defaultLearningDocumentFiltering(
            "extractedTextObjectKey.contains=" + DEFAULT_EXTRACTED_TEXT_OBJECT_KEY,
            "extractedTextObjectKey.contains=" + UPDATED_EXTRACTED_TEXT_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByExtractedTextObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where extractedTextObjectKey does not contain
        defaultLearningDocumentFiltering(
            "extractedTextObjectKey.doesNotContain=" + UPDATED_EXTRACTED_TEXT_OBJECT_KEY,
            "extractedTextObjectKey.doesNotContain=" + DEFAULT_EXTRACTED_TEXT_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByEmbeddingModelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where embeddingModel equals to
        defaultLearningDocumentFiltering(
            "embeddingModel.equals=" + DEFAULT_EMBEDDING_MODEL,
            "embeddingModel.equals=" + UPDATED_EMBEDDING_MODEL
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByEmbeddingModelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where embeddingModel in
        defaultLearningDocumentFiltering(
            "embeddingModel.in=" + DEFAULT_EMBEDDING_MODEL + "," + UPDATED_EMBEDDING_MODEL,
            "embeddingModel.in=" + UPDATED_EMBEDDING_MODEL
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByEmbeddingModelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where embeddingModel is not null
        defaultLearningDocumentFiltering("embeddingModel.specified=true", "embeddingModel.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByEmbeddingModelContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where embeddingModel contains
        defaultLearningDocumentFiltering(
            "embeddingModel.contains=" + DEFAULT_EMBEDDING_MODEL,
            "embeddingModel.contains=" + UPDATED_EMBEDDING_MODEL
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByEmbeddingModelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where embeddingModel does not contain
        defaultLearningDocumentFiltering(
            "embeddingModel.doesNotContain=" + UPDATED_EMBEDDING_MODEL,
            "embeddingModel.doesNotContain=" + DEFAULT_EMBEDDING_MODEL
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where createdAt equals to
        defaultLearningDocumentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where createdAt in
        defaultLearningDocumentFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where createdAt is not null
        defaultLearningDocumentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where updatedAt equals to
        defaultLearningDocumentFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where updatedAt in
        defaultLearningDocumentFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where updatedAt is not null
        defaultLearningDocumentFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByBookSeriesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where bookSeries equals to
        defaultLearningDocumentFiltering("bookSeries.equals=" + DEFAULT_BOOK_SERIES, "bookSeries.equals=" + UPDATED_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByBookSeriesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where bookSeries in
        defaultLearningDocumentFiltering(
            "bookSeries.in=" + DEFAULT_BOOK_SERIES + "," + UPDATED_BOOK_SERIES,
            "bookSeries.in=" + UPDATED_BOOK_SERIES
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByBookSeriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where bookSeries is not null
        defaultLearningDocumentFiltering("bookSeries.specified=true", "bookSeries.specified=false");
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByBookSeriesContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where bookSeries contains
        defaultLearningDocumentFiltering("bookSeries.contains=" + DEFAULT_BOOK_SERIES, "bookSeries.contains=" + UPDATED_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByBookSeriesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        // Get all the learningDocumentList where bookSeries does not contain
        defaultLearningDocumentFiltering(
            "bookSeries.doesNotContain=" + UPDATED_BOOK_SERIES,
            "bookSeries.doesNotContain=" + DEFAULT_BOOK_SERIES
        );
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByCourseMaterialIsEqualToSomething() throws Exception {
        CourseMaterial courseMaterial;
        if (TestUtil.findAll(em, CourseMaterial.class).isEmpty()) {
            learningDocumentRepository.saveAndFlush(learningDocument);
            courseMaterial = CourseMaterialResourceIT.createEntity(em);
        } else {
            courseMaterial = TestUtil.findAll(em, CourseMaterial.class).getFirst();
        }
        em.persist(courseMaterial);
        em.flush();
        learningDocument.setCourseMaterial(courseMaterial);
        learningDocumentRepository.saveAndFlush(learningDocument);
        Long courseMaterialId = courseMaterial.getId();
        // Get all the learningDocumentList where courseMaterial equals to courseMaterialId
        defaultLearningDocumentShouldBeFound("courseMaterialId.equals=" + courseMaterialId);

        // Get all the learningDocumentList where courseMaterial equals to (courseMaterialId + 1)
        defaultLearningDocumentShouldNotBeFound("courseMaterialId.equals=" + (courseMaterialId + 1));
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByClassMaterialIsEqualToSomething() throws Exception {
        ClassMaterial classMaterial;
        if (TestUtil.findAll(em, ClassMaterial.class).isEmpty()) {
            learningDocumentRepository.saveAndFlush(learningDocument);
            classMaterial = ClassMaterialResourceIT.createEntity(em);
        } else {
            classMaterial = TestUtil.findAll(em, ClassMaterial.class).getFirst();
        }
        em.persist(classMaterial);
        em.flush();
        learningDocument.setClassMaterial(classMaterial);
        learningDocumentRepository.saveAndFlush(learningDocument);
        Long classMaterialId = classMaterial.getId();
        // Get all the learningDocumentList where classMaterial equals to classMaterialId
        defaultLearningDocumentShouldBeFound("classMaterialId.equals=" + classMaterialId);

        // Get all the learningDocumentList where classMaterial equals to (classMaterialId + 1)
        defaultLearningDocumentShouldNotBeFound("classMaterialId.equals=" + (classMaterialId + 1));
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByUploadedByIsEqualToSomething() throws Exception {
        User uploadedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            learningDocumentRepository.saveAndFlush(learningDocument);
            uploadedBy = UserResourceIT.createEntity();
        } else {
            uploadedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(uploadedBy);
        em.flush();
        learningDocument.setUploadedBy(uploadedBy);
        learningDocumentRepository.saveAndFlush(learningDocument);
        Long uploadedById = uploadedBy.getId();
        // Get all the learningDocumentList where uploadedBy equals to uploadedById
        defaultLearningDocumentShouldBeFound("uploadedById.equals=" + uploadedById);

        // Get all the learningDocumentList where uploadedBy equals to (uploadedById + 1)
        defaultLearningDocumentShouldNotBeFound("uploadedById.equals=" + (uploadedById + 1));
    }

    @Test
    @Transactional
    void getAllLearningDocumentsBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            learningDocumentRepository.saveAndFlush(learningDocument);
            subject = SubjectResourceIT.createEntity();
        } else {
            subject = TestUtil.findAll(em, Subject.class).getFirst();
        }
        em.persist(subject);
        em.flush();
        learningDocument.setSubject(subject);
        learningDocumentRepository.saveAndFlush(learningDocument);
        Long subjectId = subject.getId();
        // Get all the learningDocumentList where subject equals to subjectId
        defaultLearningDocumentShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the learningDocumentList where subject equals to (subjectId + 1)
        defaultLearningDocumentShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    @Test
    @Transactional
    void getAllLearningDocumentsByGradeIsEqualToSomething() throws Exception {
        Grade grade;
        if (TestUtil.findAll(em, Grade.class).isEmpty()) {
            learningDocumentRepository.saveAndFlush(learningDocument);
            grade = GradeResourceIT.createEntity();
        } else {
            grade = TestUtil.findAll(em, Grade.class).getFirst();
        }
        em.persist(grade);
        em.flush();
        learningDocument.setGrade(grade);
        learningDocumentRepository.saveAndFlush(learningDocument);
        Long gradeId = grade.getId();
        // Get all the learningDocumentList where grade equals to gradeId
        defaultLearningDocumentShouldBeFound("gradeId.equals=" + gradeId);

        // Get all the learningDocumentList where grade equals to (gradeId + 1)
        defaultLearningDocumentShouldNotBeFound("gradeId.equals=" + (gradeId + 1));
    }

    private void defaultLearningDocumentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLearningDocumentShouldBeFound(shouldBeFound);
        defaultLearningDocumentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLearningDocumentShouldBeFound(String filter) throws Exception {
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(learningDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceType").value(hasItem(DEFAULT_SOURCE_TYPE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].contentHash").value(hasItem(DEFAULT_CONTENT_HASH)))
            .andExpect(jsonPath("$.[*].sourceVersion").value(hasItem(DEFAULT_SOURCE_VERSION)))
            .andExpect(jsonPath("$.[*].processingStatus").value(hasItem(DEFAULT_PROCESSING_STATUS)))
            .andExpect(jsonPath("$.[*].indexStatus").value(hasItem(DEFAULT_INDEX_STATUS)))
            .andExpect(jsonPath("$.[*].extractedTextObjectKey").value(hasItem(DEFAULT_EXTRACTED_TEXT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].embeddingModel").value(hasItem(DEFAULT_EMBEDDING_MODEL)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].bookSeries").value(hasItem(DEFAULT_BOOK_SERIES)))
            .andExpect(jsonPath("$.[*].sourceMetadata").value(hasItem(DEFAULT_SOURCE_METADATA)));

        // Check, that the count call also returns 1
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLearningDocumentShouldNotBeFound(String filter) throws Exception {
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLearningDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLearningDocument() throws Exception {
        // Get the learningDocument
        restLearningDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLearningDocument() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the learningDocument
        LearningDocument updatedLearningDocument = learningDocumentRepository.findById(learningDocument.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLearningDocument are not directly saved in db
        em.detach(updatedLearningDocument);
        updatedLearningDocument
            .sourceType(UPDATED_SOURCE_TYPE)
            .title(UPDATED_TITLE)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .contentHash(UPDATED_CONTENT_HASH)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .processingStatus(UPDATED_PROCESSING_STATUS)
            .indexStatus(UPDATED_INDEX_STATUS)
            .extractedTextObjectKey(UPDATED_EXTRACTED_TEXT_OBJECT_KEY)
            .embeddingModel(UPDATED_EMBEDDING_MODEL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .bookSeries(UPDATED_BOOK_SERIES)
            .sourceMetadata(UPDATED_SOURCE_METADATA);
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(updatedLearningDocument);

        restLearningDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, learningDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(learningDocumentDTO))
            )
            .andExpect(status().isOk());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLearningDocumentToMatchAllProperties(updatedLearningDocument);
    }

    @Test
    @Transactional
    void putNonExistingLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, learningDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(learningDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(learningDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLearningDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the learningDocument using partial update
        LearningDocument partialUpdatedLearningDocument = new LearningDocument();
        partialUpdatedLearningDocument.setId(learningDocument.getId());

        partialUpdatedLearningDocument
            .sourceType(UPDATED_SOURCE_TYPE)
            .title(UPDATED_TITLE)
            .contentHash(UPDATED_CONTENT_HASH)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .processingStatus(UPDATED_PROCESSING_STATUS)
            .extractedTextObjectKey(UPDATED_EXTRACTED_TEXT_OBJECT_KEY)
            .embeddingModel(UPDATED_EMBEDDING_MODEL)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .bookSeries(UPDATED_BOOK_SERIES);

        restLearningDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLearningDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLearningDocument))
            )
            .andExpect(status().isOk());

        // Validate the LearningDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLearningDocumentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLearningDocument, learningDocument),
            getPersistedLearningDocument(learningDocument)
        );
    }

    @Test
    @Transactional
    void fullUpdateLearningDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the learningDocument using partial update
        LearningDocument partialUpdatedLearningDocument = new LearningDocument();
        partialUpdatedLearningDocument.setId(learningDocument.getId());

        partialUpdatedLearningDocument
            .sourceType(UPDATED_SOURCE_TYPE)
            .title(UPDATED_TITLE)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .contentHash(UPDATED_CONTENT_HASH)
            .sourceVersion(UPDATED_SOURCE_VERSION)
            .processingStatus(UPDATED_PROCESSING_STATUS)
            .indexStatus(UPDATED_INDEX_STATUS)
            .extractedTextObjectKey(UPDATED_EXTRACTED_TEXT_OBJECT_KEY)
            .embeddingModel(UPDATED_EMBEDDING_MODEL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .bookSeries(UPDATED_BOOK_SERIES)
            .sourceMetadata(UPDATED_SOURCE_METADATA);

        restLearningDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLearningDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLearningDocument))
            )
            .andExpect(status().isOk());

        // Validate the LearningDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLearningDocumentUpdatableFieldsEquals(
            partialUpdatedLearningDocument,
            getPersistedLearningDocument(partialUpdatedLearningDocument)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, learningDocumentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(learningDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(learningDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLearningDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        learningDocument.setId(longCount.incrementAndGet());

        // Create the LearningDocument
        LearningDocumentDTO learningDocumentDTO = learningDocumentMapper.toDto(learningDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLearningDocumentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(learningDocumentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LearningDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLearningDocument() throws Exception {
        // Initialize the database
        insertedLearningDocument = learningDocumentRepository.saveAndFlush(learningDocument);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the learningDocument
        restLearningDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, learningDocument.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return learningDocumentRepository.count();
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

    protected LearningDocument getPersistedLearningDocument(LearningDocument learningDocument) {
        return learningDocumentRepository.findById(learningDocument.getId()).orElseThrow();
    }

    protected void assertPersistedLearningDocumentToMatchAllProperties(LearningDocument expectedLearningDocument) {
        assertLearningDocumentAllPropertiesEquals(expectedLearningDocument, getPersistedLearningDocument(expectedLearningDocument));
    }

    protected void assertPersistedLearningDocumentToMatchUpdatableProperties(LearningDocument expectedLearningDocument) {
        assertLearningDocumentAllUpdatablePropertiesEquals(
            expectedLearningDocument,
            getPersistedLearningDocument(expectedLearningDocument)
        );
    }
}
