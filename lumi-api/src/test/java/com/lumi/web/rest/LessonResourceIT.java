package com.lumi.web.rest;

import static com.lumi.domain.LessonAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Chapter;
import com.lumi.domain.Lesson;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.LessonRepository;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.mapper.LessonMapper;
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
 * Integration tests for the {@link LessonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_FILE_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_VIDEO_SIZE_BYTES = 1L;
    private static final Long UPDATED_VIDEO_SIZE_BYTES = 2L;
    private static final Long SMALLER_VIDEO_SIZE_BYTES = 1L - 1L;

    private static final Integer DEFAULT_ORDER_INDEX = 1;
    private static final Integer UPDATED_ORDER_INDEX = 2;
    private static final Integer SMALLER_ORDER_INDEX = 1 - 1;

    private static final Boolean DEFAULT_IS_PREVIEW = false;
    private static final Boolean UPDATED_IS_PREVIEW = true;

    private static final Integer DEFAULT_DURATION_MINUTES = 1;
    private static final Integer UPDATED_DURATION_MINUTES = 2;
    private static final Integer SMALLER_DURATION_MINUTES = 1 - 1;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final Integer DEFAULT_DURATION_SECONDS = 0;
    private static final Integer UPDATED_DURATION_SECONDS = 1;
    private static final Integer SMALLER_DURATION_SECONDS = 0 - 1;

    private static final String DEFAULT_VIDEO_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_OBJECT_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/lessons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMapper lessonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonMockMvc;

    private Lesson lesson;

    private Lesson insertedLesson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createEntity(EntityManager em) {
        Lesson lesson = new Lesson()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .contentType(DEFAULT_CONTENT_TYPE)
            .videoUrl(DEFAULT_VIDEO_URL)
            .videoFileName(DEFAULT_VIDEO_FILE_NAME)
            .videoSizeBytes(DEFAULT_VIDEO_SIZE_BYTES)
            .orderIndex(DEFAULT_ORDER_INDEX)
            .isPreview(DEFAULT_IS_PREVIEW)
            .durationMinutes(DEFAULT_DURATION_MINUTES)
            .status(DEFAULT_STATUS)
            .durationSeconds(DEFAULT_DURATION_SECONDS)
            .videoObjectKey(DEFAULT_VIDEO_OBJECT_KEY)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Chapter chapter;
        if (TestUtil.findAll(em, Chapter.class).isEmpty()) {
            chapter = ChapterResourceIT.createEntity(em);
            em.persist(chapter);
            em.flush();
        } else {
            chapter = TestUtil.findAll(em, Chapter.class).getFirst();
        }
        lesson.setChapter(chapter);
        return lesson;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createUpdatedEntity(EntityManager em) {
        Lesson updatedLesson = new Lesson()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .contentType(UPDATED_CONTENT_TYPE)
            .videoUrl(UPDATED_VIDEO_URL)
            .videoFileName(UPDATED_VIDEO_FILE_NAME)
            .videoSizeBytes(UPDATED_VIDEO_SIZE_BYTES)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .videoObjectKey(UPDATED_VIDEO_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Chapter chapter;
        if (TestUtil.findAll(em, Chapter.class).isEmpty()) {
            chapter = ChapterResourceIT.createUpdatedEntity(em);
            em.persist(chapter);
            em.flush();
        } else {
            chapter = TestUtil.findAll(em, Chapter.class).getFirst();
        }
        updatedLesson.setChapter(chapter);
        return updatedLesson;
    }

    @BeforeEach
    void initTest() {
        lesson = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLesson != null) {
            lessonRepository.delete(insertedLesson);
            insertedLesson = null;
        }
    }

    @Test
    @Transactional
    void createLesson() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);
        var returnedLessonDTO = om.readValue(
            restLessonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LessonDTO.class
        );

        // Validate the Lesson in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLesson = lessonMapper.toEntity(returnedLessonDTO);
        assertLessonUpdatableFieldsEquals(returnedLesson, getPersistedLesson(returnedLesson));

        insertedLesson = returnedLesson;
    }

    @Test
    @Transactional
    void createLessonWithExistingId() throws Exception {
        // Create the Lesson with an existing ID
        lesson.setId(1L);
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setTitle(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setContentType(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setOrderIndex(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setUpdatedAt(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLessons() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].videoFileName").value(hasItem(DEFAULT_VIDEO_FILE_NAME)))
            .andExpect(jsonPath("$.[*].videoSizeBytes").value(hasItem(DEFAULT_VIDEO_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].isPreview").value(hasItem(DEFAULT_IS_PREVIEW)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].durationSeconds").value(hasItem(DEFAULT_DURATION_SECONDS)))
            .andExpect(jsonPath("$.[*].videoObjectKey").value(hasItem(DEFAULT_VIDEO_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get the lesson
        restLessonMockMvc
            .perform(get(ENTITY_API_URL_ID, lesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lesson.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.videoUrl").value(DEFAULT_VIDEO_URL))
            .andExpect(jsonPath("$.videoFileName").value(DEFAULT_VIDEO_FILE_NAME))
            .andExpect(jsonPath("$.videoSizeBytes").value(DEFAULT_VIDEO_SIZE_BYTES.intValue()))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX))
            .andExpect(jsonPath("$.isPreview").value(DEFAULT_IS_PREVIEW))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.durationSeconds").value(DEFAULT_DURATION_SECONDS))
            .andExpect(jsonPath("$.videoObjectKey").value(DEFAULT_VIDEO_OBJECT_KEY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getLessonsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        Long id = lesson.getId();

        defaultLessonFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLessonFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLessonFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title equals to
        defaultLessonFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title in
        defaultLessonFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title is not null
        defaultLessonFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title contains
        defaultLessonFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title does not contain
        defaultLessonFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where contentType equals to
        defaultLessonFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllLessonsByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where contentType in
        defaultLessonFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllLessonsByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where contentType is not null
        defaultLessonFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where contentType contains
        defaultLessonFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllLessonsByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where contentType does not contain
        defaultLessonFiltering("contentType.doesNotContain=" + UPDATED_CONTENT_TYPE, "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl equals to
        defaultLessonFiltering("videoUrl.equals=" + DEFAULT_VIDEO_URL, "videoUrl.equals=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl in
        defaultLessonFiltering("videoUrl.in=" + DEFAULT_VIDEO_URL + "," + UPDATED_VIDEO_URL, "videoUrl.in=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl is not null
        defaultLessonFiltering("videoUrl.specified=true", "videoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl contains
        defaultLessonFiltering("videoUrl.contains=" + DEFAULT_VIDEO_URL, "videoUrl.contains=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl does not contain
        defaultLessonFiltering("videoUrl.doesNotContain=" + UPDATED_VIDEO_URL, "videoUrl.doesNotContain=" + DEFAULT_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoFileName equals to
        defaultLessonFiltering("videoFileName.equals=" + DEFAULT_VIDEO_FILE_NAME, "videoFileName.equals=" + UPDATED_VIDEO_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoFileName in
        defaultLessonFiltering(
            "videoFileName.in=" + DEFAULT_VIDEO_FILE_NAME + "," + UPDATED_VIDEO_FILE_NAME,
            "videoFileName.in=" + UPDATED_VIDEO_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoFileName is not null
        defaultLessonFiltering("videoFileName.specified=true", "videoFileName.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByVideoFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoFileName contains
        defaultLessonFiltering("videoFileName.contains=" + DEFAULT_VIDEO_FILE_NAME, "videoFileName.contains=" + UPDATED_VIDEO_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoFileName does not contain
        defaultLessonFiltering(
            "videoFileName.doesNotContain=" + UPDATED_VIDEO_FILE_NAME,
            "videoFileName.doesNotContain=" + DEFAULT_VIDEO_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes equals to
        defaultLessonFiltering("videoSizeBytes.equals=" + DEFAULT_VIDEO_SIZE_BYTES, "videoSizeBytes.equals=" + UPDATED_VIDEO_SIZE_BYTES);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes in
        defaultLessonFiltering(
            "videoSizeBytes.in=" + DEFAULT_VIDEO_SIZE_BYTES + "," + UPDATED_VIDEO_SIZE_BYTES,
            "videoSizeBytes.in=" + UPDATED_VIDEO_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes is not null
        defaultLessonFiltering("videoSizeBytes.specified=true", "videoSizeBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes is greater than or equal to
        defaultLessonFiltering(
            "videoSizeBytes.greaterThanOrEqual=" + DEFAULT_VIDEO_SIZE_BYTES,
            "videoSizeBytes.greaterThanOrEqual=" + UPDATED_VIDEO_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes is less than or equal to
        defaultLessonFiltering(
            "videoSizeBytes.lessThanOrEqual=" + DEFAULT_VIDEO_SIZE_BYTES,
            "videoSizeBytes.lessThanOrEqual=" + SMALLER_VIDEO_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes is less than
        defaultLessonFiltering(
            "videoSizeBytes.lessThan=" + UPDATED_VIDEO_SIZE_BYTES,
            "videoSizeBytes.lessThan=" + DEFAULT_VIDEO_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoSizeBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoSizeBytes is greater than
        defaultLessonFiltering(
            "videoSizeBytes.greaterThan=" + SMALLER_VIDEO_SIZE_BYTES,
            "videoSizeBytes.greaterThan=" + DEFAULT_VIDEO_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex equals to
        defaultLessonFiltering("orderIndex.equals=" + DEFAULT_ORDER_INDEX, "orderIndex.equals=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex in
        defaultLessonFiltering("orderIndex.in=" + DEFAULT_ORDER_INDEX + "," + UPDATED_ORDER_INDEX, "orderIndex.in=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex is not null
        defaultLessonFiltering("orderIndex.specified=true", "orderIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex is greater than or equal to
        defaultLessonFiltering(
            "orderIndex.greaterThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.greaterThanOrEqual=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex is less than or equal to
        defaultLessonFiltering("orderIndex.lessThanOrEqual=" + DEFAULT_ORDER_INDEX, "orderIndex.lessThanOrEqual=" + SMALLER_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex is less than
        defaultLessonFiltering("orderIndex.lessThan=" + UPDATED_ORDER_INDEX, "orderIndex.lessThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllLessonsByOrderIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where orderIndex is greater than
        defaultLessonFiltering("orderIndex.greaterThan=" + SMALLER_ORDER_INDEX, "orderIndex.greaterThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllLessonsByIsPreviewIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPreview equals to
        defaultLessonFiltering("isPreview.equals=" + DEFAULT_IS_PREVIEW, "isPreview.equals=" + UPDATED_IS_PREVIEW);
    }

    @Test
    @Transactional
    void getAllLessonsByIsPreviewIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPreview in
        defaultLessonFiltering("isPreview.in=" + DEFAULT_IS_PREVIEW + "," + UPDATED_IS_PREVIEW, "isPreview.in=" + UPDATED_IS_PREVIEW);
    }

    @Test
    @Transactional
    void getAllLessonsByIsPreviewIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPreview is not null
        defaultLessonFiltering("isPreview.specified=true", "isPreview.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes equals to
        defaultLessonFiltering("durationMinutes.equals=" + DEFAULT_DURATION_MINUTES, "durationMinutes.equals=" + UPDATED_DURATION_MINUTES);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes in
        defaultLessonFiltering(
            "durationMinutes.in=" + DEFAULT_DURATION_MINUTES + "," + UPDATED_DURATION_MINUTES,
            "durationMinutes.in=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes is not null
        defaultLessonFiltering("durationMinutes.specified=true", "durationMinutes.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes is greater than or equal to
        defaultLessonFiltering(
            "durationMinutes.greaterThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.greaterThanOrEqual=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes is less than or equal to
        defaultLessonFiltering(
            "durationMinutes.lessThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.lessThanOrEqual=" + SMALLER_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes is less than
        defaultLessonFiltering(
            "durationMinutes.lessThan=" + UPDATED_DURATION_MINUTES,
            "durationMinutes.lessThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationMinutesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationMinutes is greater than
        defaultLessonFiltering(
            "durationMinutes.greaterThan=" + SMALLER_DURATION_MINUTES,
            "durationMinutes.greaterThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllLessonsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where status equals to
        defaultLessonFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where status in
        defaultLessonFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLessonsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where status is not null
        defaultLessonFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds equals to
        defaultLessonFiltering("durationSeconds.equals=" + DEFAULT_DURATION_SECONDS, "durationSeconds.equals=" + UPDATED_DURATION_SECONDS);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds in
        defaultLessonFiltering(
            "durationSeconds.in=" + DEFAULT_DURATION_SECONDS + "," + UPDATED_DURATION_SECONDS,
            "durationSeconds.in=" + UPDATED_DURATION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds is not null
        defaultLessonFiltering("durationSeconds.specified=true", "durationSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds is greater than or equal to
        defaultLessonFiltering(
            "durationSeconds.greaterThanOrEqual=" + DEFAULT_DURATION_SECONDS,
            "durationSeconds.greaterThanOrEqual=" + UPDATED_DURATION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds is less than or equal to
        defaultLessonFiltering(
            "durationSeconds.lessThanOrEqual=" + DEFAULT_DURATION_SECONDS,
            "durationSeconds.lessThanOrEqual=" + SMALLER_DURATION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds is less than
        defaultLessonFiltering(
            "durationSeconds.lessThan=" + UPDATED_DURATION_SECONDS,
            "durationSeconds.lessThan=" + DEFAULT_DURATION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonsByDurationSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where durationSeconds is greater than
        defaultLessonFiltering(
            "durationSeconds.greaterThan=" + SMALLER_DURATION_SECONDS,
            "durationSeconds.greaterThan=" + DEFAULT_DURATION_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoObjectKey equals to
        defaultLessonFiltering("videoObjectKey.equals=" + DEFAULT_VIDEO_OBJECT_KEY, "videoObjectKey.equals=" + UPDATED_VIDEO_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoObjectKey in
        defaultLessonFiltering(
            "videoObjectKey.in=" + DEFAULT_VIDEO_OBJECT_KEY + "," + UPDATED_VIDEO_OBJECT_KEY,
            "videoObjectKey.in=" + UPDATED_VIDEO_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoObjectKey is not null
        defaultLessonFiltering("videoObjectKey.specified=true", "videoObjectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByVideoObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoObjectKey contains
        defaultLessonFiltering(
            "videoObjectKey.contains=" + DEFAULT_VIDEO_OBJECT_KEY,
            "videoObjectKey.contains=" + UPDATED_VIDEO_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLessonsByVideoObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoObjectKey does not contain
        defaultLessonFiltering(
            "videoObjectKey.doesNotContain=" + UPDATED_VIDEO_OBJECT_KEY,
            "videoObjectKey.doesNotContain=" + DEFAULT_VIDEO_OBJECT_KEY
        );
    }

    @Test
    @Transactional
    void getAllLessonsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where updatedAt equals to
        defaultLessonFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllLessonsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where updatedAt in
        defaultLessonFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllLessonsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where updatedAt is not null
        defaultLessonFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByChapterIsEqualToSomething() throws Exception {
        Chapter chapter;
        if (TestUtil.findAll(em, Chapter.class).isEmpty()) {
            lessonRepository.saveAndFlush(lesson);
            chapter = ChapterResourceIT.createEntity(em);
        } else {
            chapter = TestUtil.findAll(em, Chapter.class).getFirst();
        }
        em.persist(chapter);
        em.flush();
        lesson.setChapter(chapter);
        lessonRepository.saveAndFlush(lesson);
        Long chapterId = chapter.getId();
        // Get all the lessonList where chapter equals to chapterId
        defaultLessonShouldBeFound("chapterId.equals=" + chapterId);

        // Get all the lessonList where chapter equals to (chapterId + 1)
        defaultLessonShouldNotBeFound("chapterId.equals=" + (chapterId + 1));
    }

    private void defaultLessonFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLessonShouldBeFound(shouldBeFound);
        defaultLessonShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLessonShouldBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].videoFileName").value(hasItem(DEFAULT_VIDEO_FILE_NAME)))
            .andExpect(jsonPath("$.[*].videoSizeBytes").value(hasItem(DEFAULT_VIDEO_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].isPreview").value(hasItem(DEFAULT_IS_PREVIEW)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].durationSeconds").value(hasItem(DEFAULT_DURATION_SECONDS)))
            .andExpect(jsonPath("$.[*].videoObjectKey").value(hasItem(DEFAULT_VIDEO_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLessonShouldNotBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLesson() throws Exception {
        // Get the lesson
        restLessonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson
        Lesson updatedLesson = lessonRepository.findById(lesson.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLesson are not directly saved in db
        em.detach(updatedLesson);
        updatedLesson
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .contentType(UPDATED_CONTENT_TYPE)
            .videoUrl(UPDATED_VIDEO_URL)
            .videoFileName(UPDATED_VIDEO_FILE_NAME)
            .videoSizeBytes(UPDATED_VIDEO_SIZE_BYTES)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .videoObjectKey(UPDATED_VIDEO_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);
        LessonDTO lessonDTO = lessonMapper.toDto(updatedLesson);

        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLessonToMatchAllProperties(updatedLesson);
    }

    @Test
    @Transactional
    void putNonExistingLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .contentType(UPDATED_CONTENT_TYPE)
            .videoUrl(UPDATED_VIDEO_URL)
            .videoFileName(UPDATED_VIDEO_FILE_NAME)
            .orderIndex(UPDATED_ORDER_INDEX)
            .status(UPDATED_STATUS)
            .videoObjectKey(UPDATED_VIDEO_OBJECT_KEY);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLesson, lesson), getPersistedLesson(lesson));
    }

    @Test
    @Transactional
    void fullUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .contentType(UPDATED_CONTENT_TYPE)
            .videoUrl(UPDATED_VIDEO_URL)
            .videoFileName(UPDATED_VIDEO_FILE_NAME)
            .videoSizeBytes(UPDATED_VIDEO_SIZE_BYTES)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .videoObjectKey(UPDATED_VIDEO_OBJECT_KEY)
            .updatedAt(UPDATED_UPDATED_AT);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonUpdatableFieldsEquals(partialUpdatedLesson, getPersistedLesson(partialUpdatedLesson));
    }

    @Test
    @Transactional
    void patchNonExistingLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lesson
        restLessonMockMvc
            .perform(delete(ENTITY_API_URL_ID, lesson.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lessonRepository.count();
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

    protected Lesson getPersistedLesson(Lesson lesson) {
        return lessonRepository.findById(lesson.getId()).orElseThrow();
    }

    protected void assertPersistedLessonToMatchAllProperties(Lesson expectedLesson) {
        assertLessonAllPropertiesEquals(expectedLesson, getPersistedLesson(expectedLesson));
    }

    protected void assertPersistedLessonToMatchUpdatableProperties(Lesson expectedLesson) {
        assertLessonAllUpdatablePropertiesEquals(expectedLesson, getPersistedLesson(expectedLesson));
    }
}
