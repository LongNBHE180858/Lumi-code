package com.lumi.web.rest;

import static com.lumi.domain.ClassMaterialAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ClassMaterial;
import com.lumi.domain.StudyClass;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.service.dto.ClassMaterialDTO;
import com.lumi.service.mapper.ClassMaterialMapper;
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
 * Integration tests for the {@link ClassMaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClassMaterialResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE_BYTES = 1L;
    private static final Long UPDATED_FILE_SIZE_BYTES = 2L;
    private static final Long SMALLER_FILE_SIZE_BYTES = 1L - 1L;

    private static final Instant DEFAULT_UPLOADED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_FILE_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_FILE_OBJECT_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/class-materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClassMaterialRepository classMaterialRepository;

    @Autowired
    private ClassMaterialMapper classMaterialMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassMaterialMockMvc;

    private ClassMaterial classMaterial;

    private ClassMaterial insertedClassMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassMaterial createEntity(EntityManager em) {
        ClassMaterial classMaterial = new ClassMaterial()
            .title(DEFAULT_TITLE)
            .fileType(DEFAULT_FILE_TYPE)
            .fileUrl(DEFAULT_FILE_URL)
            .fileSizeBytes(DEFAULT_FILE_SIZE_BYTES)
            .uploadedAt(DEFAULT_UPLOADED_AT)
            .fileObjectKey(DEFAULT_FILE_OBJECT_KEY)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        classMaterial.setStudyClass(studyClass);
        return classMaterial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassMaterial createUpdatedEntity(EntityManager em) {
        ClassMaterial updatedClassMaterial = new ClassMaterial()
            .title(UPDATED_TITLE)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createUpdatedEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        updatedClassMaterial.setStudyClass(studyClass);
        return updatedClassMaterial;
    }

    @BeforeEach
    void initTest() {
        classMaterial = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedClassMaterial != null) {
            classMaterialRepository.delete(insertedClassMaterial);
            insertedClassMaterial = null;
        }
    }

    @Test
    @Transactional
    void createClassMaterial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);
        var returnedClassMaterialDTO = om.readValue(
            restClassMaterialMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClassMaterialDTO.class
        );

        // Validate the ClassMaterial in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClassMaterial = classMaterialMapper.toEntity(returnedClassMaterialDTO);
        assertClassMaterialUpdatableFieldsEquals(returnedClassMaterial, getPersistedClassMaterial(returnedClassMaterial));

        insertedClassMaterial = returnedClassMaterial;
    }

    @Test
    @Transactional
    void createClassMaterialWithExistingId() throws Exception {
        // Create the ClassMaterial with an existing ID
        classMaterial.setId(1L);
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classMaterial.setTitle(null);

        // Create the ClassMaterial, which fails.
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classMaterial.setFileType(null);

        // Create the ClassMaterial, which fails.
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classMaterial.setFileUrl(null);

        // Create the ClassMaterial, which fails.
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileSizeBytesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classMaterial.setFileSizeBytes(null);

        // Create the ClassMaterial, which fails.
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classMaterial.setUpdatedAt(null);

        // Create the ClassMaterial, which fails.
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        restClassMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClassMaterials() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getClassMaterial() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get the classMaterial
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, classMaterial.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classMaterial.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE))
            .andExpect(jsonPath("$.fileUrl").value(DEFAULT_FILE_URL))
            .andExpect(jsonPath("$.fileSizeBytes").value(DEFAULT_FILE_SIZE_BYTES.intValue()))
            .andExpect(jsonPath("$.uploadedAt").value(DEFAULT_UPLOADED_AT.toString()))
            .andExpect(jsonPath("$.fileObjectKey").value(DEFAULT_FILE_OBJECT_KEY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getClassMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        Long id = classMaterial.getId();

        defaultClassMaterialFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClassMaterialFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClassMaterialFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where title equals to
        defaultClassMaterialFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where title in
        defaultClassMaterialFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where title is not null
        defaultClassMaterialFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where title contains
        defaultClassMaterialFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where title does not contain
        defaultClassMaterialFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileType equals to
        defaultClassMaterialFiltering("fileType.equals=" + DEFAULT_FILE_TYPE, "fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileType in
        defaultClassMaterialFiltering("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE, "fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileType is not null
        defaultClassMaterialFiltering("fileType.specified=true", "fileType.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileType contains
        defaultClassMaterialFiltering("fileType.contains=" + DEFAULT_FILE_TYPE, "fileType.contains=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileType does not contain
        defaultClassMaterialFiltering("fileType.doesNotContain=" + UPDATED_FILE_TYPE, "fileType.doesNotContain=" + DEFAULT_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileUrl equals to
        defaultClassMaterialFiltering("fileUrl.equals=" + DEFAULT_FILE_URL, "fileUrl.equals=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileUrl in
        defaultClassMaterialFiltering("fileUrl.in=" + DEFAULT_FILE_URL + "," + UPDATED_FILE_URL, "fileUrl.in=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileUrl is not null
        defaultClassMaterialFiltering("fileUrl.specified=true", "fileUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileUrl contains
        defaultClassMaterialFiltering("fileUrl.contains=" + DEFAULT_FILE_URL, "fileUrl.contains=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileUrl does not contain
        defaultClassMaterialFiltering("fileUrl.doesNotContain=" + UPDATED_FILE_URL, "fileUrl.doesNotContain=" + DEFAULT_FILE_URL);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes equals to
        defaultClassMaterialFiltering("fileSizeBytes.equals=" + DEFAULT_FILE_SIZE_BYTES, "fileSizeBytes.equals=" + UPDATED_FILE_SIZE_BYTES);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes in
        defaultClassMaterialFiltering(
            "fileSizeBytes.in=" + DEFAULT_FILE_SIZE_BYTES + "," + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.in=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes is not null
        defaultClassMaterialFiltering("fileSizeBytes.specified=true", "fileSizeBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes is greater than or equal to
        defaultClassMaterialFiltering(
            "fileSizeBytes.greaterThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThanOrEqual=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes is less than or equal to
        defaultClassMaterialFiltering(
            "fileSizeBytes.lessThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThanOrEqual=" + SMALLER_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes is less than
        defaultClassMaterialFiltering(
            "fileSizeBytes.lessThan=" + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileSizeBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileSizeBytes is greater than
        defaultClassMaterialFiltering(
            "fileSizeBytes.greaterThan=" + SMALLER_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUploadedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where uploadedAt equals to
        defaultClassMaterialFiltering("uploadedAt.equals=" + DEFAULT_UPLOADED_AT, "uploadedAt.equals=" + UPDATED_UPLOADED_AT);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUploadedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where uploadedAt in
        defaultClassMaterialFiltering(
            "uploadedAt.in=" + DEFAULT_UPLOADED_AT + "," + UPDATED_UPLOADED_AT,
            "uploadedAt.in=" + UPDATED_UPLOADED_AT
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUploadedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where uploadedAt is not null
        defaultClassMaterialFiltering("uploadedAt.specified=true", "uploadedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileObjectKey equals to
        defaultClassMaterialFiltering("fileObjectKey.equals=" + DEFAULT_FILE_OBJECT_KEY, "fileObjectKey.equals=" + UPDATED_FILE_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileObjectKey in
        defaultClassMaterialFiltering(
            "fileObjectKey.in=" + DEFAULT_FILE_OBJECT_KEY + "," + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.in=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileObjectKey is not null
        defaultClassMaterialFiltering("fileObjectKey.specified=true", "fileObjectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileObjectKey contains
        defaultClassMaterialFiltering(
            "fileObjectKey.contains=" + DEFAULT_FILE_OBJECT_KEY,
            "fileObjectKey.contains=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByFileObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where fileObjectKey does not contain
        defaultClassMaterialFiltering(
            "fileObjectKey.doesNotContain=" + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.doesNotContain=" + DEFAULT_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where updatedAt equals to
        defaultClassMaterialFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where updatedAt in
        defaultClassMaterialFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllClassMaterialsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        // Get all the classMaterialList where updatedAt is not null
        defaultClassMaterialFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMaterialsByStudyClassIsEqualToSomething() throws Exception {
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            classMaterialRepository.saveAndFlush(classMaterial);
            studyClass = StudyClassResourceIT.createEntity(em);
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        em.persist(studyClass);
        em.flush();
        classMaterial.setStudyClass(studyClass);
        classMaterialRepository.saveAndFlush(classMaterial);
        Long studyClassId = studyClass.getId();
        // Get all the classMaterialList where studyClass equals to studyClassId
        defaultClassMaterialShouldBeFound("studyClassId.equals=" + studyClassId);

        // Get all the classMaterialList where studyClass equals to (studyClassId + 1)
        defaultClassMaterialShouldNotBeFound("studyClassId.equals=" + (studyClassId + 1));
    }

    private void defaultClassMaterialFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClassMaterialShouldBeFound(shouldBeFound);
        defaultClassMaterialShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClassMaterialShouldBeFound(String filter) throws Exception {
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClassMaterialShouldNotBeFound(String filter) throws Exception {
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClassMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClassMaterial() throws Exception {
        // Get the classMaterial
        restClassMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClassMaterial() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMaterial
        ClassMaterial updatedClassMaterial = classMaterialRepository.findById(classMaterial.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClassMaterial are not directly saved in db
        em.detach(updatedClassMaterial);
        updatedClassMaterial
            .title(UPDATED_TITLE)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(updatedClassMaterial);

        restClassMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMaterialDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClassMaterialToMatchAllProperties(updatedClassMaterial);
    }

    @Test
    @Transactional
    void putNonExistingClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMaterial using partial update
        ClassMaterial partialUpdatedClassMaterial = new ClassMaterial();
        partialUpdatedClassMaterial.setId(classMaterial.getId());

        partialUpdatedClassMaterial.title(UPDATED_TITLE).fileSizeBytes(UPDATED_FILE_SIZE_BYTES).fileObjectKey(UPDATED_FILE_OBJECT_KEY);

        restClassMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassMaterial))
            )
            .andExpect(status().isOk());

        // Validate the ClassMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassMaterialUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClassMaterial, classMaterial),
            getPersistedClassMaterial(classMaterial)
        );
    }

    @Test
    @Transactional
    void fullUpdateClassMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMaterial using partial update
        ClassMaterial partialUpdatedClassMaterial = new ClassMaterial();
        partialUpdatedClassMaterial.setId(classMaterial.getId());

        partialUpdatedClassMaterial
            .title(UPDATED_TITLE)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);

        restClassMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassMaterial))
            )
            .andExpect(status().isOk());

        // Validate the ClassMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassMaterialUpdatableFieldsEquals(partialUpdatedClassMaterial, getPersistedClassMaterial(partialUpdatedClassMaterial));
    }

    @Test
    @Transactional
    void patchNonExistingClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classMaterialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMaterial.setId(longCount.incrementAndGet());

        // Create the ClassMaterial
        ClassMaterialDTO classMaterialDTO = classMaterialMapper.toDto(classMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMaterialMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(classMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassMaterial() throws Exception {
        // Initialize the database
        insertedClassMaterial = classMaterialRepository.saveAndFlush(classMaterial);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classMaterial
        restClassMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, classMaterial.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classMaterialRepository.count();
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

    protected ClassMaterial getPersistedClassMaterial(ClassMaterial classMaterial) {
        return classMaterialRepository.findById(classMaterial.getId()).orElseThrow();
    }

    protected void assertPersistedClassMaterialToMatchAllProperties(ClassMaterial expectedClassMaterial) {
        assertClassMaterialAllPropertiesEquals(expectedClassMaterial, getPersistedClassMaterial(expectedClassMaterial));
    }

    protected void assertPersistedClassMaterialToMatchUpdatableProperties(ClassMaterial expectedClassMaterial) {
        assertClassMaterialAllUpdatablePropertiesEquals(expectedClassMaterial, getPersistedClassMaterial(expectedClassMaterial));
    }
}
