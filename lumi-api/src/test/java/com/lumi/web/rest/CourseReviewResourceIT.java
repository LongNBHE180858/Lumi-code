package com.lumi.web.rest;

import static com.lumi.domain.CourseReviewAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Course;
import com.lumi.domain.CourseReview;
import com.lumi.domain.User;
import com.lumi.repository.CourseReviewRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CourseReviewService;
import com.lumi.service.dto.CourseReviewDTO;
import com.lumi.service.mapper.CourseReviewMapper;
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
 * Integration tests for the {@link CourseReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseReviewResourceIT {

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/course-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseReviewRepository courseReviewRepositoryMock;

    @Autowired
    private CourseReviewMapper courseReviewMapper;

    @Mock
    private CourseReviewService courseReviewServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseReviewMockMvc;

    private CourseReview courseReview;

    private CourseReview insertedCourseReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseReview createEntity(EntityManager em) {
        CourseReview courseReview = new CourseReview()
            .rating(DEFAULT_RATING)
            .content(DEFAULT_CONTENT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        courseReview.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        courseReview.setStudent(user);
        return courseReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseReview createUpdatedEntity(EntityManager em) {
        CourseReview updatedCourseReview = new CourseReview()
            .rating(UPDATED_RATING)
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        updatedCourseReview.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourseReview.setStudent(user);
        return updatedCourseReview;
    }

    @BeforeEach
    void initTest() {
        courseReview = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseReview != null) {
            courseReviewRepository.delete(insertedCourseReview);
            insertedCourseReview = null;
        }
    }

    @Test
    @Transactional
    void createCourseReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);
        var returnedCourseReviewDTO = om.readValue(
            restCourseReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseReviewDTO.class
        );

        // Validate the CourseReview in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseReview = courseReviewMapper.toEntity(returnedCourseReviewDTO);
        assertCourseReviewUpdatableFieldsEquals(returnedCourseReview, getPersistedCourseReview(returnedCourseReview));

        insertedCourseReview = returnedCourseReview;
    }

    @Test
    @Transactional
    void createCourseReviewWithExistingId() throws Exception {
        // Create the CourseReview with an existing ID
        courseReview.setId(1L);
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseReview.setUpdatedAt(null);

        // Create the CourseReview, which fails.
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        restCourseReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseReviews() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseReviewsWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseReviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseReviewsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseReviewRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseReview() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get the courseReview
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, courseReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseReview.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCourseReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        Long id = courseReview.getId();

        defaultCourseReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating equals to
        defaultCourseReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating in
        defaultCourseReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating is not null
        defaultCourseReviewFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating is greater than or equal to
        defaultCourseReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating is less than or equal to
        defaultCourseReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating is less than
        defaultCourseReviewFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where rating is greater than
        defaultCourseReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where createdAt equals to
        defaultCourseReviewFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where createdAt in
        defaultCourseReviewFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where createdAt is not null
        defaultCourseReviewFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where updatedAt equals to
        defaultCourseReviewFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where updatedAt in
        defaultCourseReviewFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        // Get all the courseReviewList where updatedAt is not null
        defaultCourseReviewFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            courseReviewRepository.saveAndFlush(courseReview);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        courseReview.setCourse(course);
        courseReviewRepository.saveAndFlush(courseReview);
        Long courseId = course.getId();
        // Get all the courseReviewList where course equals to courseId
        defaultCourseReviewShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseReviewList where course equals to (courseId + 1)
        defaultCourseReviewShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllCourseReviewsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseReviewRepository.saveAndFlush(courseReview);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        courseReview.setStudent(student);
        courseReviewRepository.saveAndFlush(courseReview);
        Long studentId = student.getId();
        // Get all the courseReviewList where student equals to studentId
        defaultCourseReviewShouldBeFound("studentId.equals=" + studentId);

        // Get all the courseReviewList where student equals to (studentId + 1)
        defaultCourseReviewShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    private void defaultCourseReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseReviewShouldBeFound(shouldBeFound);
        defaultCourseReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseReviewShouldBeFound(String filter) throws Exception {
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseReviewShouldNotBeFound(String filter) throws Exception {
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourseReview() throws Exception {
        // Get the courseReview
        restCourseReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseReview() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReview
        CourseReview updatedCourseReview = courseReviewRepository.findById(courseReview.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseReview are not directly saved in db
        em.detach(updatedCourseReview);
        updatedCourseReview.rating(UPDATED_RATING).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(updatedCourseReview);

        restCourseReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseReviewToMatchAllProperties(updatedCourseReview);
    }

    @Test
    @Transactional
    void putNonExistingCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseReviewWithPatch() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReview using partial update
        CourseReview partialUpdatedCourseReview = new CourseReview();
        partialUpdatedCourseReview.setId(courseReview.getId());

        partialUpdatedCourseReview.updatedAt(UPDATED_UPDATED_AT);

        restCourseReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseReview))
            )
            .andExpect(status().isOk());

        // Validate the CourseReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseReviewUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseReview, courseReview),
            getPersistedCourseReview(courseReview)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseReviewWithPatch() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReview using partial update
        CourseReview partialUpdatedCourseReview = new CourseReview();
        partialUpdatedCourseReview.setId(courseReview.getId());

        partialUpdatedCourseReview
            .rating(UPDATED_RATING)
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCourseReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseReview))
            )
            .andExpect(status().isOk());

        // Validate the CourseReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseReviewUpdatableFieldsEquals(partialUpdatedCourseReview, getPersistedCourseReview(partialUpdatedCourseReview));
    }

    @Test
    @Transactional
    void patchNonExistingCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseReviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReview.setId(longCount.incrementAndGet());

        // Create the CourseReview
        CourseReviewDTO courseReviewDTO = courseReviewMapper.toDto(courseReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseReview() throws Exception {
        // Initialize the database
        insertedCourseReview = courseReviewRepository.saveAndFlush(courseReview);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseReview
        restCourseReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseReview.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseReviewRepository.count();
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

    protected CourseReview getPersistedCourseReview(CourseReview courseReview) {
        return courseReviewRepository.findById(courseReview.getId()).orElseThrow();
    }

    protected void assertPersistedCourseReviewToMatchAllProperties(CourseReview expectedCourseReview) {
        assertCourseReviewAllPropertiesEquals(expectedCourseReview, getPersistedCourseReview(expectedCourseReview));
    }

    protected void assertPersistedCourseReviewToMatchUpdatableProperties(CourseReview expectedCourseReview) {
        assertCourseReviewAllUpdatablePropertiesEquals(expectedCourseReview, getPersistedCourseReview(expectedCourseReview));
    }
}
