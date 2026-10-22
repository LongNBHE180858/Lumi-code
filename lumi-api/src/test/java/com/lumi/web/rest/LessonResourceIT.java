package com.lumi.web.rest;

import static com.lumi.domain.LessonAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Chapter;
import com.lumi.domain.Lesson;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.LessonRepository;
import com.lumi.service.LessonService;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.mapper.LessonMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link LessonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class LessonResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER_INDEX = 0;
    private static final Integer UPDATED_ORDER_INDEX = 1;
    private static final Integer SMALLER_ORDER_INDEX = 0 - 1;

    private static final Boolean DEFAULT_IS_PREVIEW = false;
    private static final Boolean UPDATED_IS_PREVIEW = true;

    private static final Integer DEFAULT_DURATION_MINUTES = 0;
    private static final Integer UPDATED_DURATION_MINUTES = 1;
    private static final Integer SMALLER_DURATION_MINUTES = 0 - 1;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final String ENTITY_API_URL = "/api/lessons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LessonRepository lessonRepository;

    @Mock
    private LessonRepository lessonRepositoryMock;

    @Autowired
    private LessonMapper lessonMapper;

    @Mock
    private LessonService lessonServiceMock;

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
            .contentType(DEFAULT_CONTENT_TYPE)
            .orderIndex(DEFAULT_ORDER_INDEX)
            .isPreview(DEFAULT_IS_PREVIEW)
            .durationMinutes(DEFAULT_DURATION_MINUTES)
            .status(DEFAULT_STATUS);
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
            .contentType(UPDATED_CONTENT_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS);
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
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].isPreview").value(hasItem(DEFAULT_IS_PREVIEW)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLessonsWithEagerRelationshipsIsEnabled() throws Exception {
        when(lessonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLessonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(lessonServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLessonsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(lessonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLessonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(lessonRepositoryMock, times(1)).findAll(any(Pageable.class));
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
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX))
            .andExpect(jsonPath("$.isPreview").value(DEFAULT_IS_PREVIEW))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
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
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)))
            .andExpect(jsonPath("$.[*].isPreview").value(hasItem(DEFAULT_IS_PREVIEW)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

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
            .contentType(UPDATED_CONTENT_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS);
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
            .contentType(UPDATED_CONTENT_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES);

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
            .contentType(UPDATED_CONTENT_TYPE)
            .orderIndex(UPDATED_ORDER_INDEX)
            .isPreview(UPDATED_IS_PREVIEW)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .status(UPDATED_STATUS);

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
