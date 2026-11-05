package com.lumi.web.rest;

import static com.lumi.domain.CourseMaterialAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.CourseMaterial;
import com.lumi.domain.Lesson;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.mapper.CourseMaterialMapper;
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
 * Integration tests for the {@link CourseMaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CourseMaterialResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER_INDEX = 1;
    private static final Integer UPDATED_ORDER_INDEX = 2;
    private static final Integer SMALLER_ORDER_INDEX = 1 - 1;

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE_BYTES = 1L;
    private static final Long UPDATED_FILE_SIZE_BYTES = 2L;
    private static final Long SMALLER_FILE_SIZE_BYTES = 1L - 1L;

    private static final String DEFAULT_FILE_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_FILE_OBJECT_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/course-materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseMaterialRepository courseMaterialRepository;

    @Autowired
    private CourseMaterialMapper courseMaterialMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMaterialMockMvc;

    private CourseMaterial courseMaterial;

    private CourseMaterial insertedCourseMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseMaterial createEntity(EntityManager em) {
        CourseMaterial courseMaterial = new CourseMaterial()
            .fileName(DEFAULT_FILE_NAME)
            .displayName(DEFAULT_DISPLAY_NAME)
            .mimeType(DEFAULT_MIME_TYPE)
            .orderIndex(DEFAULT_ORDER_INDEX)
            .fileType(DEFAULT_FILE_TYPE)
            .fileUrl(DEFAULT_FILE_URL)
            .fileSizeBytes(DEFAULT_FILE_SIZE_BYTES)
            .fileObjectKey(DEFAULT_FILE_OBJECT_KEY)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        courseMaterial.setLesson(lesson);
        return courseMaterial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseMaterial createUpdatedEntity(EntityManager em) {
        CourseMaterial updatedCourseMaterial = new CourseMaterial()
            .fileName(UPDATED_FILE_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        updatedCourseMaterial.setLesson(lesson);
        return updatedCourseMaterial;
    }

    @BeforeEach
    void initTest() {
        courseMaterial = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseMaterial != null) {
            courseMaterialRepository.delete(insertedCourseMaterial);
            insertedCourseMaterial = null;
        }
    }

    @Test
    @Transactional
    void createCourseMaterial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);
        var returnedCourseMaterialDTO = om.readValue(
            restCourseMaterialMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseMaterialDTO.class
        );

        // Validate the CourseMaterial in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseMaterial = courseMaterialMapper.toEntity(returnedCourseMaterialDTO);
        assertCourseMaterialUpdatableFieldsEquals(returnedCourseMaterial, getPersistedCourseMaterial(returnedCourseMaterial));

        insertedCourseMaterial = returnedCourseMaterial;
    }

    @Test
    @Transactional
    void createCourseMaterialWithExistingId() throws Exception {
        // Create the CourseMaterial with an existing ID
        courseMaterial.setId(1L);
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFileTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseMaterial.setFileType(null);

        // Create the CourseMaterial, which fails.
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        restCourseMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseMaterial.setFileUrl(null);

        // Create the CourseMaterial, which fails.
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        restCourseMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseMaterial.setUpdatedAt(null);

        // Create the CourseMaterial, which fails.
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        restCourseMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseMaterials() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCourseMaterial() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get the courseMaterial
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, courseMaterial.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseMaterial.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE))
            .andExpect(jsonPath("$.fileUrl").value(DEFAULT_FILE_URL))
            .andExpect(jsonPath("$.fileSizeBytes").value(DEFAULT_FILE_SIZE_BYTES.intValue()))
            .andExpect(jsonPath("$.fileObjectKey").value(DEFAULT_FILE_OBJECT_KEY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCourseMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        Long id = courseMaterial.getId();

        defaultCourseMaterialFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseMaterialFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseMaterialFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileName equals to
        defaultCourseMaterialFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileName in
        defaultCourseMaterialFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileName is not null
        defaultCourseMaterialFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileName contains
        defaultCourseMaterialFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileName does not contain
        defaultCourseMaterialFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByDisplayNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where displayName equals to
        defaultCourseMaterialFiltering("displayName.equals=" + DEFAULT_DISPLAY_NAME, "displayName.equals=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByDisplayNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where displayName in
        defaultCourseMaterialFiltering(
            "displayName.in=" + DEFAULT_DISPLAY_NAME + "," + UPDATED_DISPLAY_NAME,
            "displayName.in=" + UPDATED_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByDisplayNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where displayName is not null
        defaultCourseMaterialFiltering("displayName.specified=true", "displayName.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByDisplayNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where displayName contains
        defaultCourseMaterialFiltering("displayName.contains=" + DEFAULT_DISPLAY_NAME, "displayName.contains=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByDisplayNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where displayName does not contain
        defaultCourseMaterialFiltering(
            "displayName.doesNotContain=" + UPDATED_DISPLAY_NAME,
            "displayName.doesNotContain=" + DEFAULT_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByMimeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where mimeType equals to
        defaultCourseMaterialFiltering("mimeType.equals=" + DEFAULT_MIME_TYPE, "mimeType.equals=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByMimeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where mimeType in
        defaultCourseMaterialFiltering("mimeType.in=" + DEFAULT_MIME_TYPE + "," + UPDATED_MIME_TYPE, "mimeType.in=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByMimeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where mimeType is not null
        defaultCourseMaterialFiltering("mimeType.specified=true", "mimeType.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByMimeTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where mimeType contains
        defaultCourseMaterialFiltering("mimeType.contains=" + DEFAULT_MIME_TYPE, "mimeType.contains=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByMimeTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where mimeType does not contain
        defaultCourseMaterialFiltering("mimeType.doesNotContain=" + UPDATED_MIME_TYPE, "mimeType.doesNotContain=" + DEFAULT_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex equals to
        defaultCourseMaterialFiltering("orderIndex.equals=" + DEFAULT_ORDER_INDEX, "orderIndex.equals=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex in
        defaultCourseMaterialFiltering(
            "orderIndex.in=" + DEFAULT_ORDER_INDEX + "," + UPDATED_ORDER_INDEX,
            "orderIndex.in=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex is not null
        defaultCourseMaterialFiltering("orderIndex.specified=true", "orderIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex is greater than or equal to
        defaultCourseMaterialFiltering(
            "orderIndex.greaterThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.greaterThanOrEqual=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex is less than or equal to
        defaultCourseMaterialFiltering(
            "orderIndex.lessThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.lessThanOrEqual=" + SMALLER_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex is less than
        defaultCourseMaterialFiltering("orderIndex.lessThan=" + UPDATED_ORDER_INDEX, "orderIndex.lessThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByOrderIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where orderIndex is greater than
        defaultCourseMaterialFiltering("orderIndex.greaterThan=" + SMALLER_ORDER_INDEX, "orderIndex.greaterThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileType equals to
        defaultCourseMaterialFiltering("fileType.equals=" + DEFAULT_FILE_TYPE, "fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileType in
        defaultCourseMaterialFiltering("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE, "fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileType is not null
        defaultCourseMaterialFiltering("fileType.specified=true", "fileType.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileType contains
        defaultCourseMaterialFiltering("fileType.contains=" + DEFAULT_FILE_TYPE, "fileType.contains=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileType does not contain
        defaultCourseMaterialFiltering("fileType.doesNotContain=" + UPDATED_FILE_TYPE, "fileType.doesNotContain=" + DEFAULT_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileUrl equals to
        defaultCourseMaterialFiltering("fileUrl.equals=" + DEFAULT_FILE_URL, "fileUrl.equals=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileUrl in
        defaultCourseMaterialFiltering("fileUrl.in=" + DEFAULT_FILE_URL + "," + UPDATED_FILE_URL, "fileUrl.in=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileUrl is not null
        defaultCourseMaterialFiltering("fileUrl.specified=true", "fileUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileUrl contains
        defaultCourseMaterialFiltering("fileUrl.contains=" + DEFAULT_FILE_URL, "fileUrl.contains=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileUrl does not contain
        defaultCourseMaterialFiltering("fileUrl.doesNotContain=" + UPDATED_FILE_URL, "fileUrl.doesNotContain=" + DEFAULT_FILE_URL);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes equals to
        defaultCourseMaterialFiltering(
            "fileSizeBytes.equals=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.equals=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes in
        defaultCourseMaterialFiltering(
            "fileSizeBytes.in=" + DEFAULT_FILE_SIZE_BYTES + "," + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.in=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes is not null
        defaultCourseMaterialFiltering("fileSizeBytes.specified=true", "fileSizeBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes is greater than or equal to
        defaultCourseMaterialFiltering(
            "fileSizeBytes.greaterThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThanOrEqual=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes is less than or equal to
        defaultCourseMaterialFiltering(
            "fileSizeBytes.lessThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThanOrEqual=" + SMALLER_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes is less than
        defaultCourseMaterialFiltering(
            "fileSizeBytes.lessThan=" + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileSizeBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileSizeBytes is greater than
        defaultCourseMaterialFiltering(
            "fileSizeBytes.greaterThan=" + SMALLER_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileObjectKey equals to
        defaultCourseMaterialFiltering(
            "fileObjectKey.equals=" + DEFAULT_FILE_OBJECT_KEY,
            "fileObjectKey.equals=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileObjectKey in
        defaultCourseMaterialFiltering(
            "fileObjectKey.in=" + DEFAULT_FILE_OBJECT_KEY + "," + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.in=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileObjectKey is not null
        defaultCourseMaterialFiltering("fileObjectKey.specified=true", "fileObjectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileObjectKey contains
        defaultCourseMaterialFiltering(
            "fileObjectKey.contains=" + DEFAULT_FILE_OBJECT_KEY,
            "fileObjectKey.contains=" + UPDATED_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByFileObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where fileObjectKey does not contain
        defaultCourseMaterialFiltering(
            "fileObjectKey.doesNotContain=" + UPDATED_FILE_OBJECT_KEY,
            "fileObjectKey.doesNotContain=" + DEFAULT_FILE_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where updatedAt equals to
        defaultCourseMaterialFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where updatedAt in
        defaultCourseMaterialFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        // Get all the courseMaterialList where updatedAt is not null
        defaultCourseMaterialFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseMaterialsByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            courseMaterialRepository.saveAndFlush(courseMaterial);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        courseMaterial.setLesson(lesson);
        courseMaterialRepository.saveAndFlush(courseMaterial);
        Long lessonId = lesson.getId();
        // Get all the courseMaterialList where lesson equals to lessonId
        defaultCourseMaterialShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the courseMaterialList where lesson equals to (lessonId + 1)
        defaultCourseMaterialShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultCourseMaterialFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseMaterialShouldBeFound(shouldBeFound);
        defaultCourseMaterialShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseMaterialShouldBeFound(String filter) throws Exception {
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].fileObjectKey").value(hasItem(DEFAULT_FILE_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseMaterialShouldNotBeFound(String filter) throws Exception {
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourseMaterial() throws Exception {
        // Get the courseMaterial
        restCourseMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseMaterial() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseMaterial
        CourseMaterial updatedCourseMaterial = courseMaterialRepository.findById(courseMaterial.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseMaterial are not directly saved in db
        em.detach(updatedCourseMaterial);
        updatedCourseMaterial
            .fileName(UPDATED_FILE_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(updatedCourseMaterial);

        restCourseMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseMaterialDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseMaterialToMatchAllProperties(updatedCourseMaterial);
    }

    @Test
    @Transactional
    void putNonExistingCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseMaterial using partial update
        CourseMaterial partialUpdatedCourseMaterial = new CourseMaterial();
        partialUpdatedCourseMaterial.setId(courseMaterial.getId());

        partialUpdatedCourseMaterial
            .fileName(UPDATED_FILE_NAME)
            .orderIndex(UPDATED_ORDER_INDEX)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .updatedAt(UPDATED_UPDATED_AT);

        restCourseMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseMaterial))
            )
            .andExpect(status().isOk());

        // Validate the CourseMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseMaterialUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseMaterial, courseMaterial),
            getPersistedCourseMaterial(courseMaterial)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseMaterial using partial update
        CourseMaterial partialUpdatedCourseMaterial = new CourseMaterial();
        partialUpdatedCourseMaterial.setId(courseMaterial.getId());

        partialUpdatedCourseMaterial
            .fileName(UPDATED_FILE_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .fileType(UPDATED_FILE_TYPE)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .fileObjectKey(UPDATED_FILE_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);

        restCourseMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseMaterial))
            )
            .andExpect(status().isOk());

        // Validate the CourseMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseMaterialUpdatableFieldsEquals(partialUpdatedCourseMaterial, getPersistedCourseMaterial(partialUpdatedCourseMaterial));
    }

    @Test
    @Transactional
    void patchNonExistingCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseMaterialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseMaterial.setId(longCount.incrementAndGet());

        // Create the CourseMaterial
        CourseMaterialDTO courseMaterialDTO = courseMaterialMapper.toDto(courseMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMaterialMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseMaterial() throws Exception {
        // Initialize the database
        insertedCourseMaterial = courseMaterialRepository.saveAndFlush(courseMaterial);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseMaterial
        restCourseMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseMaterial.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseMaterialRepository.count();
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

    protected CourseMaterial getPersistedCourseMaterial(CourseMaterial courseMaterial) {
        return courseMaterialRepository.findById(courseMaterial.getId()).orElseThrow();
    }

    protected void assertPersistedCourseMaterialToMatchAllProperties(CourseMaterial expectedCourseMaterial) {
        assertCourseMaterialAllPropertiesEquals(expectedCourseMaterial, getPersistedCourseMaterial(expectedCourseMaterial));
    }

    protected void assertPersistedCourseMaterialToMatchUpdatableProperties(CourseMaterial expectedCourseMaterial) {
        assertCourseMaterialAllUpdatablePropertiesEquals(expectedCourseMaterial, getPersistedCourseMaterial(expectedCourseMaterial));
    }
}
