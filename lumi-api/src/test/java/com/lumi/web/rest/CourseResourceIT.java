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
import com.lumi.domain.Category;
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
@WithMockUser(authorities = "ROLE_ADMIN")
class CourseResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMBNAIL_URL = "AAAAAAAAAA";
    private static final String UPDATED_THUMBNAIL_URL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_VALID_DURATION_DAYS = 1;
    private static final Integer UPDATED_VALID_DURATION_DAYS = 2;
    private static final Integer SMALLER_VALID_DURATION_DAYS = 1 - 1;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

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
            .validDurationDays(DEFAULT_VALID_DURATION_DAYS)
            .status(DEFAULT_STATUS)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
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
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
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
            courseRepository.deleteById(insertedCourse.getId());
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
    void createDraftWithoutDuration() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        course.setValidDurationDays(null);
        CourseDTO courseDTO = courseMapper.toDto(course);
        var result = restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isCreated())
            .andReturn();
        insertedCourse = courseMapper.toEntity(om.readValue(result.getResponse().getContentAsString(), CourseDTO.class));
        assertIncrementedRepositoryCount(databaseSizeBeforeTest);
        assertThat(insertedCourse.getValidDurationDays()).isNull();
    }

    @Test
    @Transactional
    void staleVersionCannotOverwriteCourse() throws Exception {
        insertedCourse = courseRepository.saveAndFlush(course);
        CourseDTO stale = courseMapper.toDto(course);
        CourseDTO current = courseMapper.toDto(course);
        current.setTitle("Updated once");
        var result = restCourseMockMvc
            .perform(put(ENTITY_API_URL_ID, course.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(current)))
            .andExpect(status().isOk())
            .andReturn();
        CourseDTO saved = om.readValue(result.getResponse().getContentAsString(), CourseDTO.class);
        assertThat(saved.getVersion()).isGreaterThan(stale.getVersion());
        stale.setTitle("Stale overwrite");
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, course.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stale))
            )
            .andExpect(status().isConflict());
        assertThat(courseRepository.findById(course.getId()).orElseThrow().getTitle()).isEqualTo("Updated once");
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
            .andExpect(jsonPath("$.[*].validDurationDays").value(hasItem(DEFAULT_VALID_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
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
            .andExpect(jsonPath("$.validDurationDays").value(DEFAULT_VALID_DURATION_DAYS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
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
    void getAllCoursesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where description equals to
        defaultCourseFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where description in
        defaultCourseFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where description is not null
        defaultCourseFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where description contains
        defaultCourseFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where description does not contain
        defaultCourseFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
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
    void getAllCoursesByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            category = CategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, Category.class).getFirst();
        }
        em.persist(category);
        em.flush();
        course.setCategory(category);
        courseRepository.saveAndFlush(course);
        Long categoryId = category.getId();
        // Get all the courseList where category equals to categoryId
        defaultCourseShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the courseList where category equals to (categoryId + 1)
        defaultCourseShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
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
            .andExpect(jsonPath("$.[*].validDurationDays").value(hasItem(DEFAULT_VALID_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

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
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
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
        partialUpdatedCourse.setVersion(course.getVersion());

        partialUpdatedCourse
            .title(UPDATED_TITLE)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .isDeleted(UPDATED_IS_DELETED)
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
        partialUpdatedCourse.setVersion(course.getVersion());

        partialUpdatedCourse
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .validDurationDays(UPDATED_VALID_DURATION_DAYS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
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
