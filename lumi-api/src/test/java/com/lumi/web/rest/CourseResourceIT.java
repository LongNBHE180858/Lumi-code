package com.lumi.web.rest;

import static com.lumi.domain.CourseAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Course;
import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CourseService;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.mapper.CourseMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMBNAIL_URL = "AAAAAAAAAA";
    private static final String UPDATED_THUMBNAIL_URL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_BOOK_SERIES = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_SERIES = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_AUDIENCE = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_AUDIENCE = "BBBBBBBBBB";

    private static final String DEFAULT_PREREQUISITES = "AAAAAAAAAA";
    private static final String UPDATED_PREREQUISITES = "BBBBBBBBBB";

    private static final java.util.List<String> DEFAULT_LEARNING_OBJECTIVES = java.util.List.of("AAAAAAAAAA");
    private static final java.util.List<String> UPDATED_LEARNING_OBJECTIVES = java.util.List.of("BBBBBBBBBB");

    private static final String DEFAULT_INTRO_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_INTRO_VIDEO_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_VALID_DURATION_DAYS = 1;
    private static final Integer UPDATED_VALID_DURATION_DAYS = 2;
    private static final Integer SMALLER_VALID_DURATION_DAYS = 1 - 1;

    private static final Integer DEFAULT_ACCESS_DURATION_VALUE = 1;
    private static final Integer UPDATED_ACCESS_DURATION_VALUE = 2;
    private static final Integer SMALLER_ACCESS_DURATION_VALUE = 1 - 1;

    private static final String DEFAULT_ACCESS_DURATION_UNIT = "DAY";
    private static final String UPDATED_ACCESS_DURATION_UNIT = "MONTH";

    private static final Long DEFAULT_VERSION = 1L;
    private static final Long UPDATED_VERSION = 2L;
    private static final Long SMALLER_VERSION = 1L - 1L;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Long DEFAULT_STORAGE_LIMIT_BYTES = 0L;
    private static final Long UPDATED_STORAGE_LIMIT_BYTES = 1L;
    private static final Long SMALLER_STORAGE_LIMIT_BYTES = 0L - 1L;

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepositoryMock;

    @Autowired
    private CourseMapper courseMapper;

    @Mock
    private CourseService courseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    private Course insertedCourse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .thumbnailUrl(DEFAULT_THUMBNAIL_URL)
            .price(DEFAULT_PRICE)
            .shortDescription(DEFAULT_SHORT_DESCRIPTION)
            .bookSeries(DEFAULT_BOOK_SERIES)
            .targetAudience(DEFAULT_TARGET_AUDIENCE)
            .prerequisites(DEFAULT_PREREQUISITES)
            .learningObjectives(DEFAULT_LEARNING_OBJECTIVES)
            .introVideoUrl(DEFAULT_INTRO_VIDEO_URL)
            .validDurationDays(DEFAULT_VALID_DURATION_DAYS)
            .accessDurationValue(DEFAULT_ACCESS_DURATION_VALUE)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.valueOf(DEFAULT_ACCESS_DURATION_UNIT))
            .version(DEFAULT_VERSION)
            .status(DEFAULT_STATUS)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .storageLimitBytes(DEFAULT_STORAGE_LIMIT_BYTES);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        course.setAuthor(user);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course updatedCourse = new Course()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .bookSeries(UPDATED_BOOK_SERIES)
            .targetAudience(UPDATED_TARGET_AUDIENCE)
            .prerequisites(UPDATED_PREREQUISITES)
            .learningObjectives(UPDATED_LEARNING_OBJECTIVES)
            .introVideoUrl(UPDATED_INTRO_VIDEO_URL)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .accessDurationValue(UPDATED_ACCESS_DURATION_VALUE)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.valueOf(UPDATED_ACCESS_DURATION_UNIT))
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .storageLimitBytes(UPDATED_STORAGE_LIMIT_BYTES);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourse.setAuthor(user);
        return updatedCourse;
    }

    @BeforeEach
    void initTest() {
        course = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourse != null) {
            courseRepository.delete(insertedCourse);
            insertedCourse = null;
        }
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);
        var returnedCourseDTO = om.readValue(
            restCourseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseDTO.class
        );

        // Validate the Course in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourse = courseMapper.toEntity(returnedCourseDTO);
        assertCourseUpdatableFieldsEquals(returnedCourse, getPersistedCourse(returnedCourse));

        insertedCourse = returnedCourse;
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);
        CourseDTO courseDTO = courseMapper.toDto(course);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setTitle(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setVersion(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailUrl").value(hasItem(DEFAULT_THUMBNAIL_URL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].bookSeries").value(hasItem(DEFAULT_BOOK_SERIES)))
            .andExpect(jsonPath("$.[*].targetAudience").value(hasItem(DEFAULT_TARGET_AUDIENCE)))
            .andExpect(jsonPath("$.[*].prerequisites").value(hasItem(DEFAULT_PREREQUISITES)))
            .andExpect(jsonPath("$.[*].learningObjectives").value(hasItem(DEFAULT_LEARNING_OBJECTIVES)))
            .andExpect(jsonPath("$.[*].introVideoUrl").value(hasItem(DEFAULT_INTRO_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].validDurationDays").value(hasItem(DEFAULT_VALID_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].accessDurationValue").value(hasItem(DEFAULT_ACCESS_DURATION_VALUE)))
            .andExpect(jsonPath("$.[*].accessDurationUnit").value(hasItem(DEFAULT_ACCESS_DURATION_UNIT)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].storageLimitBytes").value(hasItem(DEFAULT_STORAGE_LIMIT_BYTES.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailUrl").value(DEFAULT_THUMBNAIL_URL))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION))
            .andExpect(jsonPath("$.bookSeries").value(DEFAULT_BOOK_SERIES))
            .andExpect(jsonPath("$.targetAudience").value(DEFAULT_TARGET_AUDIENCE))
            .andExpect(jsonPath("$.prerequisites").value(DEFAULT_PREREQUISITES))
            .andExpect(jsonPath("$.learningObjectives").value(DEFAULT_LEARNING_OBJECTIVES))
            .andExpect(jsonPath("$.introVideoUrl").value(DEFAULT_INTRO_VIDEO_URL))
            .andExpect(jsonPath("$.validDurationDays").value(DEFAULT_VALID_DURATION_DAYS))
            .andExpect(jsonPath("$.accessDurationValue").value(DEFAULT_ACCESS_DURATION_VALUE))
            .andExpect(jsonPath("$.accessDurationUnit").value(DEFAULT_ACCESS_DURATION_UNIT))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.storageLimitBytes").value(DEFAULT_STORAGE_LIMIT_BYTES.intValue()));
    }

    @Test
    @Transactional
    void getCoursesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        Long id = course.getId();

        defaultCourseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title equals to
        defaultCourseFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title in
        defaultCourseFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title is not null
        defaultCourseFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title contains
        defaultCourseFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title does not contain
        defaultCourseFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl equals to
        defaultCourseFiltering("thumbnailUrl.equals=" + DEFAULT_THUMBNAIL_URL, "thumbnailUrl.equals=" + UPDATED_THUMBNAIL_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl in
        defaultCourseFiltering(
            "thumbnailUrl.in=" + DEFAULT_THUMBNAIL_URL + "," + UPDATED_THUMBNAIL_URL,
            "thumbnailUrl.in=" + UPDATED_THUMBNAIL_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl is not null
        defaultCourseFiltering("thumbnailUrl.specified=true", "thumbnailUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl contains
        defaultCourseFiltering("thumbnailUrl.contains=" + DEFAULT_THUMBNAIL_URL, "thumbnailUrl.contains=" + UPDATED_THUMBNAIL_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl does not contain
        defaultCourseFiltering(
            "thumbnailUrl.doesNotContain=" + UPDATED_THUMBNAIL_URL,
            "thumbnailUrl.doesNotContain=" + DEFAULT_THUMBNAIL_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price equals to
        defaultCourseFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price in
        defaultCourseFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is not null
        defaultCourseFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is greater than or equal to
        defaultCourseFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is less than or equal to
        defaultCourseFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is less than
        defaultCourseFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is greater than
        defaultCourseFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription equals to
        defaultCourseFiltering(
            "shortDescription.equals=" + DEFAULT_SHORT_DESCRIPTION,
            "shortDescription.equals=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription in
        defaultCourseFiltering(
            "shortDescription.in=" + DEFAULT_SHORT_DESCRIPTION + "," + UPDATED_SHORT_DESCRIPTION,
            "shortDescription.in=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription is not null
        defaultCourseFiltering("shortDescription.specified=true", "shortDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription contains
        defaultCourseFiltering(
            "shortDescription.contains=" + DEFAULT_SHORT_DESCRIPTION,
            "shortDescription.contains=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription does not contain
        defaultCourseFiltering(
            "shortDescription.doesNotContain=" + UPDATED_SHORT_DESCRIPTION,
            "shortDescription.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByBookSeriesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where bookSeries equals to
        defaultCourseFiltering("bookSeries.equals=" + DEFAULT_BOOK_SERIES, "bookSeries.equals=" + UPDATED_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllCoursesByBookSeriesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where bookSeries in
        defaultCourseFiltering("bookSeries.in=" + DEFAULT_BOOK_SERIES + "," + UPDATED_BOOK_SERIES, "bookSeries.in=" + UPDATED_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllCoursesByBookSeriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where bookSeries is not null
        defaultCourseFiltering("bookSeries.specified=true", "bookSeries.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByBookSeriesContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where bookSeries contains
        defaultCourseFiltering("bookSeries.contains=" + DEFAULT_BOOK_SERIES, "bookSeries.contains=" + UPDATED_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllCoursesByBookSeriesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where bookSeries does not contain
        defaultCourseFiltering("bookSeries.doesNotContain=" + UPDATED_BOOK_SERIES, "bookSeries.doesNotContain=" + DEFAULT_BOOK_SERIES);
    }

    @Test
    @Transactional
    void getAllCoursesByTargetAudienceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where targetAudience equals to
        defaultCourseFiltering("targetAudience.equals=" + DEFAULT_TARGET_AUDIENCE, "targetAudience.equals=" + UPDATED_TARGET_AUDIENCE);
    }

    @Test
    @Transactional
    void getAllCoursesByTargetAudienceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where targetAudience in
        defaultCourseFiltering(
            "targetAudience.in=" + DEFAULT_TARGET_AUDIENCE + "," + UPDATED_TARGET_AUDIENCE,
            "targetAudience.in=" + UPDATED_TARGET_AUDIENCE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByTargetAudienceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where targetAudience is not null
        defaultCourseFiltering("targetAudience.specified=true", "targetAudience.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByTargetAudienceContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where targetAudience contains
        defaultCourseFiltering("targetAudience.contains=" + DEFAULT_TARGET_AUDIENCE, "targetAudience.contains=" + UPDATED_TARGET_AUDIENCE);
    }

    @Test
    @Transactional
    void getAllCoursesByTargetAudienceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where targetAudience does not contain
        defaultCourseFiltering(
            "targetAudience.doesNotContain=" + UPDATED_TARGET_AUDIENCE,
            "targetAudience.doesNotContain=" + DEFAULT_TARGET_AUDIENCE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByPrerequisitesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where prerequisites equals to
        defaultCourseFiltering("prerequisites.equals=" + DEFAULT_PREREQUISITES, "prerequisites.equals=" + UPDATED_PREREQUISITES);
    }

    @Test
    @Transactional
    void getAllCoursesByPrerequisitesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where prerequisites in
        defaultCourseFiltering(
            "prerequisites.in=" + DEFAULT_PREREQUISITES + "," + UPDATED_PREREQUISITES,
            "prerequisites.in=" + UPDATED_PREREQUISITES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByPrerequisitesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where prerequisites is not null
        defaultCourseFiltering("prerequisites.specified=true", "prerequisites.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByPrerequisitesContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where prerequisites contains
        defaultCourseFiltering("prerequisites.contains=" + DEFAULT_PREREQUISITES, "prerequisites.contains=" + UPDATED_PREREQUISITES);
    }

    @Test
    @Transactional
    void getAllCoursesByPrerequisitesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where prerequisites does not contain
        defaultCourseFiltering(
            "prerequisites.doesNotContain=" + UPDATED_PREREQUISITES,
            "prerequisites.doesNotContain=" + DEFAULT_PREREQUISITES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByIntroVideoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where introVideoUrl equals to
        defaultCourseFiltering("introVideoUrl.equals=" + DEFAULT_INTRO_VIDEO_URL, "introVideoUrl.equals=" + UPDATED_INTRO_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByIntroVideoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where introVideoUrl in
        defaultCourseFiltering(
            "introVideoUrl.in=" + DEFAULT_INTRO_VIDEO_URL + "," + UPDATED_INTRO_VIDEO_URL,
            "introVideoUrl.in=" + UPDATED_INTRO_VIDEO_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByIntroVideoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where introVideoUrl is not null
        defaultCourseFiltering("introVideoUrl.specified=true", "introVideoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByIntroVideoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where introVideoUrl contains
        defaultCourseFiltering("introVideoUrl.contains=" + DEFAULT_INTRO_VIDEO_URL, "introVideoUrl.contains=" + UPDATED_INTRO_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByIntroVideoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where introVideoUrl does not contain
        defaultCourseFiltering(
            "introVideoUrl.doesNotContain=" + UPDATED_INTRO_VIDEO_URL,
            "introVideoUrl.doesNotContain=" + DEFAULT_INTRO_VIDEO_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays equals to
        defaultCourseFiltering(
            "validDurationDays.equals=" + DEFAULT_VALID_DURATION_DAYS,
            "validDurationDays.equals=" + UPDATED_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays in
        defaultCourseFiltering(
            "validDurationDays.in=" + DEFAULT_VALID_DURATION_DAYS + "," + UPDATED_VALID_DURATION_DAYS,
            "validDurationDays.in=" + UPDATED_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays is not null
        defaultCourseFiltering("validDurationDays.specified=true", "validDurationDays.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays is greater than or equal to
        defaultCourseFiltering(
            "validDurationDays.greaterThanOrEqual=" + DEFAULT_VALID_DURATION_DAYS,
            "validDurationDays.greaterThanOrEqual=" + UPDATED_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays is less than or equal to
        defaultCourseFiltering(
            "validDurationDays.lessThanOrEqual=" + DEFAULT_VALID_DURATION_DAYS,
            "validDurationDays.lessThanOrEqual=" + SMALLER_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays is less than
        defaultCourseFiltering(
            "validDurationDays.lessThan=" + UPDATED_VALID_DURATION_DAYS,
            "validDurationDays.lessThan=" + DEFAULT_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByValidDurationDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where validDurationDays is greater than
        defaultCourseFiltering(
            "validDurationDays.greaterThan=" + SMALLER_VALID_DURATION_DAYS,
            "validDurationDays.greaterThan=" + DEFAULT_VALID_DURATION_DAYS
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue equals to
        defaultCourseFiltering(
            "accessDurationValue.equals=" + DEFAULT_ACCESS_DURATION_VALUE,
            "accessDurationValue.equals=" + UPDATED_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue in
        defaultCourseFiltering(
            "accessDurationValue.in=" + DEFAULT_ACCESS_DURATION_VALUE + "," + UPDATED_ACCESS_DURATION_VALUE,
            "accessDurationValue.in=" + UPDATED_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue is not null
        defaultCourseFiltering("accessDurationValue.specified=true", "accessDurationValue.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue is greater than or equal to
        defaultCourseFiltering(
            "accessDurationValue.greaterThanOrEqual=" + DEFAULT_ACCESS_DURATION_VALUE,
            "accessDurationValue.greaterThanOrEqual=" + UPDATED_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue is less than or equal to
        defaultCourseFiltering(
            "accessDurationValue.lessThanOrEqual=" + DEFAULT_ACCESS_DURATION_VALUE,
            "accessDurationValue.lessThanOrEqual=" + SMALLER_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue is less than
        defaultCourseFiltering(
            "accessDurationValue.lessThan=" + UPDATED_ACCESS_DURATION_VALUE,
            "accessDurationValue.lessThan=" + DEFAULT_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationValue is greater than
        defaultCourseFiltering(
            "accessDurationValue.greaterThan=" + SMALLER_ACCESS_DURATION_VALUE,
            "accessDurationValue.greaterThan=" + DEFAULT_ACCESS_DURATION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationUnit equals to
        defaultCourseFiltering(
            "accessDurationUnit.equals=" + DEFAULT_ACCESS_DURATION_UNIT,
            "accessDurationUnit.equals=" + UPDATED_ACCESS_DURATION_UNIT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationUnit in
        defaultCourseFiltering(
            "accessDurationUnit.in=" + DEFAULT_ACCESS_DURATION_UNIT + "," + UPDATED_ACCESS_DURATION_UNIT,
            "accessDurationUnit.in=" + UPDATED_ACCESS_DURATION_UNIT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationUnit is not null
        defaultCourseFiltering("accessDurationUnit.specified=true", "accessDurationUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationUnit contains
        defaultCourseFiltering(
            "accessDurationUnit.contains=" + DEFAULT_ACCESS_DURATION_UNIT,
            "accessDurationUnit.contains=" + UPDATED_ACCESS_DURATION_UNIT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAccessDurationUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where accessDurationUnit does not contain
        defaultCourseFiltering(
            "accessDurationUnit.doesNotContain=" + UPDATED_ACCESS_DURATION_UNIT,
            "accessDurationUnit.doesNotContain=" + DEFAULT_ACCESS_DURATION_UNIT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version equals to
        defaultCourseFiltering("version.equals=" + DEFAULT_VERSION, "version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version in
        defaultCourseFiltering("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION, "version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version is not null
        defaultCourseFiltering("version.specified=true", "version.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version is greater than or equal to
        defaultCourseFiltering("version.greaterThanOrEqual=" + DEFAULT_VERSION, "version.greaterThanOrEqual=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version is less than or equal to
        defaultCourseFiltering("version.lessThanOrEqual=" + DEFAULT_VERSION, "version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version is less than
        defaultCourseFiltering("version.lessThan=" + UPDATED_VERSION, "version.lessThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where version is greater than
        defaultCourseFiltering("version.greaterThan=" + SMALLER_VERSION, "version.greaterThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status equals to
        defaultCourseFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status in
        defaultCourseFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status is not null
        defaultCourseFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isDeleted equals to
        defaultCourseFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllCoursesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isDeleted in
        defaultCourseFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllCoursesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isDeleted is not null
        defaultCourseFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt equals to
        defaultCourseFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt in
        defaultCourseFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt is not null
        defaultCourseFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where updatedAt equals to
        defaultCourseFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where updatedAt in
        defaultCourseFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where updatedAt is not null
        defaultCourseFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes equals to
        defaultCourseFiltering(
            "storageLimitBytes.equals=" + DEFAULT_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.equals=" + UPDATED_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes in
        defaultCourseFiltering(
            "storageLimitBytes.in=" + DEFAULT_STORAGE_LIMIT_BYTES + "," + UPDATED_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.in=" + UPDATED_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes is not null
        defaultCourseFiltering("storageLimitBytes.specified=true", "storageLimitBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes is greater than or equal to
        defaultCourseFiltering(
            "storageLimitBytes.greaterThanOrEqual=" + DEFAULT_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.greaterThanOrEqual=" + UPDATED_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes is less than or equal to
        defaultCourseFiltering(
            "storageLimitBytes.lessThanOrEqual=" + DEFAULT_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.lessThanOrEqual=" + SMALLER_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes is less than
        defaultCourseFiltering(
            "storageLimitBytes.lessThan=" + UPDATED_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.lessThan=" + DEFAULT_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByStorageLimitBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where storageLimitBytes is greater than
        defaultCourseFiltering(
            "storageLimitBytes.greaterThan=" + SMALLER_STORAGE_LIMIT_BYTES,
            "storageLimitBytes.greaterThan=" + DEFAULT_STORAGE_LIMIT_BYTES
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAuthorIsEqualToSomething() throws Exception {
        User author;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            author = UserResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(author);
        em.flush();
        course.setAuthor(author);
        courseRepository.saveAndFlush(course);
        Long authorId = author.getId();
        // Get all the courseList where author equals to authorId
        defaultCourseShouldBeFound("authorId.equals=" + authorId);

        // Get all the courseList where author equals to (authorId + 1)
        defaultCourseShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    @Test
    @Transactional
    void getAllCoursesBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            subject = SubjectResourceIT.createEntity();
        } else {
            subject = TestUtil.findAll(em, Subject.class).getFirst();
        }
        em.persist(subject);
        em.flush();
        course.setSubject(subject);
        courseRepository.saveAndFlush(course);
        Long subjectId = subject.getId();
        // Get all the courseList where subject equals to subjectId
        defaultCourseShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the courseList where subject equals to (subjectId + 1)
        defaultCourseShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    @Test
    @Transactional
    void getAllCoursesByGradeIsEqualToSomething() throws Exception {
        Grade grade;
        if (TestUtil.findAll(em, Grade.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            grade = GradeResourceIT.createEntity();
        } else {
            grade = TestUtil.findAll(em, Grade.class).getFirst();
        }
        em.persist(grade);
        em.flush();
        course.setGrade(grade);
        courseRepository.saveAndFlush(course);
        Long gradeId = grade.getId();
        // Get all the courseList where grade equals to gradeId
        defaultCourseShouldBeFound("gradeId.equals=" + gradeId);

        // Get all the courseList where grade equals to (gradeId + 1)
        defaultCourseShouldNotBeFound("gradeId.equals=" + (gradeId + 1));
    }

    private void defaultCourseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseShouldBeFound(shouldBeFound);
        defaultCourseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseShouldBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailUrl").value(hasItem(DEFAULT_THUMBNAIL_URL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].bookSeries").value(hasItem(DEFAULT_BOOK_SERIES)))
            .andExpect(jsonPath("$.[*].targetAudience").value(hasItem(DEFAULT_TARGET_AUDIENCE)))
            .andExpect(jsonPath("$.[*].prerequisites").value(hasItem(DEFAULT_PREREQUISITES)))
            .andExpect(jsonPath("$.[*].learningObjectives").value(hasItem(DEFAULT_LEARNING_OBJECTIVES)))
            .andExpect(jsonPath("$.[*].introVideoUrl").value(hasItem(DEFAULT_INTRO_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].validDurationDays").value(hasItem(DEFAULT_VALID_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].accessDurationValue").value(hasItem(DEFAULT_ACCESS_DURATION_VALUE)))
            .andExpect(jsonPath("$.[*].accessDurationUnit").value(hasItem(DEFAULT_ACCESS_DURATION_UNIT)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].storageLimitBytes").value(hasItem(DEFAULT_STORAGE_LIMIT_BYTES.intValue())));

        // Check, that the count call also returns 1
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseShouldNotBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .bookSeries(UPDATED_BOOK_SERIES)
            .targetAudience(UPDATED_TARGET_AUDIENCE)
            .prerequisites(UPDATED_PREREQUISITES)
            .learningObjectives(UPDATED_LEARNING_OBJECTIVES)
            .introVideoUrl(UPDATED_INTRO_VIDEO_URL)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .accessDurationValue(UPDATED_ACCESS_DURATION_VALUE)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.valueOf(UPDATED_ACCESS_DURATION_UNIT))
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .storageLimitBytes(UPDATED_STORAGE_LIMIT_BYTES);
        CourseDTO courseDTO = courseMapper.toDto(updatedCourse);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseToMatchAllProperties(updatedCourse);
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .title(UPDATED_TITLE)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .targetAudience(UPDATED_TARGET_AUDIENCE)
            .prerequisites(UPDATED_PREREQUISITES)
            .learningObjectives(UPDATED_LEARNING_OBJECTIVES)
            .introVideoUrl(UPDATED_INTRO_VIDEO_URL)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.valueOf(UPDATED_ACCESS_DURATION_UNIT))
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCourse, course), getPersistedCourse(course));
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .bookSeries(UPDATED_BOOK_SERIES)
            .targetAudience(UPDATED_TARGET_AUDIENCE)
            .prerequisites(UPDATED_PREREQUISITES)
            .learningObjectives(UPDATED_LEARNING_OBJECTIVES)
            .introVideoUrl(UPDATED_INTRO_VIDEO_URL)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .accessDurationValue(UPDATED_ACCESS_DURATION_VALUE)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.valueOf(UPDATED_ACCESS_DURATION_UNIT))
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .storageLimitBytes(UPDATED_STORAGE_LIMIT_BYTES);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(partialUpdatedCourse, getPersistedCourse(partialUpdatedCourse));
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseRepository.count();
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

    protected Course getPersistedCourse(Course course) {
        return courseRepository.findById(course.getId()).orElseThrow();
    }

    protected void assertPersistedCourseToMatchAllProperties(Course expectedCourse) {
        assertCourseAllPropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }

    protected void assertPersistedCourseToMatchUpdatableProperties(Course expectedCourse) {
        assertCourseAllUpdatablePropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }
}
