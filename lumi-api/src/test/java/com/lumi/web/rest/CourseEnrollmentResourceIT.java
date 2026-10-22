package com.lumi.web.rest;

import static com.lumi.domain.CourseEnrollmentAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Course;
import com.lumi.domain.CourseEnrollment;
import com.lumi.domain.User;
import com.lumi.repository.CourseEnrollmentRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CourseEnrollmentService;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.mapper.CourseEnrollmentMapper;
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
 * Integration tests for the {@link CourseEnrollmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseEnrollmentResourceIT {

    private static final BigDecimal DEFAULT_PROGRESS_PERCENT = new BigDecimal(0);
    private static final BigDecimal UPDATED_PROGRESS_PERCENT = new BigDecimal(1);
    private static final BigDecimal SMALLER_PROGRESS_PERCENT = new BigDecimal(0 - 1);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_ENROLLED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENROLLED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/course-enrollments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseEnrollmentRepository courseEnrollmentRepositoryMock;

    @Autowired
    private CourseEnrollmentMapper courseEnrollmentMapper;

    @Mock
    private CourseEnrollmentService courseEnrollmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseEnrollmentMockMvc;

    private CourseEnrollment courseEnrollment;

    private CourseEnrollment insertedCourseEnrollment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseEnrollment createEntity(EntityManager em) {
        CourseEnrollment courseEnrollment = new CourseEnrollment()
            .progressPercent(DEFAULT_PROGRESS_PERCENT)
            .status(DEFAULT_STATUS)
            .enrolledAt(DEFAULT_ENROLLED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        courseEnrollment.setStudent(user);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        courseEnrollment.setCourse(course);
        return courseEnrollment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseEnrollment createUpdatedEntity(EntityManager em) {
        CourseEnrollment updatedCourseEnrollment = new CourseEnrollment()
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .status(UPDATED_STATUS)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourseEnrollment.setStudent(user);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        updatedCourseEnrollment.setCourse(course);
        return updatedCourseEnrollment;
    }

    @BeforeEach
    void initTest() {
        courseEnrollment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseEnrollment != null) {
            courseEnrollmentRepository.deleteById(insertedCourseEnrollment.getId());
            insertedCourseEnrollment = null;
        }
    }

    @Test
    @Transactional
    void createCourseEnrollment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);
        var returnedCourseEnrollmentDTO = om.readValue(
            restCourseEnrollmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEnrollmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseEnrollmentDTO.class
        );

        // Validate the CourseEnrollment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseEnrollment = courseEnrollmentMapper.toEntity(returnedCourseEnrollmentDTO);
        assertCourseEnrollmentUpdatableFieldsEquals(returnedCourseEnrollment, getPersistedCourseEnrollment(returnedCourseEnrollment));

        insertedCourseEnrollment = returnedCourseEnrollment;
    }

    @Test
    @Transactional
    void createCourseEnrollmentWithExistingId() throws Exception {
        // Create the CourseEnrollment with an existing ID
        courseEnrollment.setId(1L);
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEnrollmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseEnrollment.setExpiresAt(null);

        // Create the CourseEnrollment, which fails.
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        restCourseEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEnrollmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseEnrollments() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseEnrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].progressPercent").value(hasItem(sameNumber(DEFAULT_PROGRESS_PERCENT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].enrolledAt").value(hasItem(DEFAULT_ENROLLED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseEnrollmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseEnrollmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseEnrollmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseEnrollmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseEnrollmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseEnrollmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseEnrollmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseEnrollmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseEnrollment() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get the courseEnrollment
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL_ID, courseEnrollment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseEnrollment.getId().intValue()))
            .andExpect(jsonPath("$.progressPercent").value(sameNumber(DEFAULT_PROGRESS_PERCENT)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.enrolledAt").value(DEFAULT_ENROLLED_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getCourseEnrollmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        Long id = courseEnrollment.getId();

        defaultCourseEnrollmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseEnrollmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseEnrollmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent equals to
        defaultCourseEnrollmentFiltering(
            "progressPercent.equals=" + DEFAULT_PROGRESS_PERCENT,
            "progressPercent.equals=" + UPDATED_PROGRESS_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent in
        defaultCourseEnrollmentFiltering(
            "progressPercent.in=" + DEFAULT_PROGRESS_PERCENT + "," + UPDATED_PROGRESS_PERCENT,
            "progressPercent.in=" + UPDATED_PROGRESS_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent is not null
        defaultCourseEnrollmentFiltering("progressPercent.specified=true", "progressPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent is greater than or equal to
        defaultCourseEnrollmentFiltering(
            "progressPercent.greaterThanOrEqual=" + DEFAULT_PROGRESS_PERCENT,
            "progressPercent.greaterThanOrEqual=" + DEFAULT_PROGRESS_PERCENT.add(BigDecimal.ONE)
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent is less than or equal to
        defaultCourseEnrollmentFiltering(
            "progressPercent.lessThanOrEqual=" + DEFAULT_PROGRESS_PERCENT,
            "progressPercent.lessThanOrEqual=" + SMALLER_PROGRESS_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent is less than
        defaultCourseEnrollmentFiltering(
            "progressPercent.lessThan=" + DEFAULT_PROGRESS_PERCENT.add(BigDecimal.ONE),
            "progressPercent.lessThan=" + DEFAULT_PROGRESS_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByProgressPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where progressPercent is greater than
        defaultCourseEnrollmentFiltering(
            "progressPercent.greaterThan=" + SMALLER_PROGRESS_PERCENT,
            "progressPercent.greaterThan=" + DEFAULT_PROGRESS_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where status equals to
        defaultCourseEnrollmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where status in
        defaultCourseEnrollmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where status is not null
        defaultCourseEnrollmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where status contains
        defaultCourseEnrollmentFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where status does not contain
        defaultCourseEnrollmentFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByEnrolledAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where enrolledAt equals to
        defaultCourseEnrollmentFiltering("enrolledAt.equals=" + DEFAULT_ENROLLED_AT, "enrolledAt.equals=" + UPDATED_ENROLLED_AT);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByEnrolledAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where enrolledAt in
        defaultCourseEnrollmentFiltering(
            "enrolledAt.in=" + DEFAULT_ENROLLED_AT + "," + UPDATED_ENROLLED_AT,
            "enrolledAt.in=" + UPDATED_ENROLLED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByEnrolledAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where enrolledAt is not null
        defaultCourseEnrollmentFiltering("enrolledAt.specified=true", "enrolledAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where expiresAt equals to
        defaultCourseEnrollmentFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where expiresAt in
        defaultCourseEnrollmentFiltering(
            "expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT,
            "expiresAt.in=" + UPDATED_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        // Get all the courseEnrollmentList where expiresAt is not null
        defaultCourseEnrollmentFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseEnrollmentRepository.saveAndFlush(courseEnrollment);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        courseEnrollment.setStudent(student);
        courseEnrollmentRepository.saveAndFlush(courseEnrollment);
        Long studentId = student.getId();
        // Get all the courseEnrollmentList where student equals to studentId
        defaultCourseEnrollmentShouldBeFound("studentId.equals=" + studentId);

        // Get all the courseEnrollmentList where student equals to (studentId + 1)
        defaultCourseEnrollmentShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllCourseEnrollmentsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            courseEnrollmentRepository.saveAndFlush(courseEnrollment);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        courseEnrollment.setCourse(course);
        courseEnrollmentRepository.saveAndFlush(courseEnrollment);
        Long courseId = course.getId();
        // Get all the courseEnrollmentList where course equals to courseId
        defaultCourseEnrollmentShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseEnrollmentList where course equals to (courseId + 1)
        defaultCourseEnrollmentShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    private void defaultCourseEnrollmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseEnrollmentShouldBeFound(shouldBeFound);
        defaultCourseEnrollmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseEnrollmentShouldBeFound(String filter) throws Exception {
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseEnrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].progressPercent").value(hasItem(sameNumber(DEFAULT_PROGRESS_PERCENT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].enrolledAt").value(hasItem(DEFAULT_ENROLLED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));

        // Check, that the count call also returns 1
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseEnrollmentShouldNotBeFound(String filter) throws Exception {
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourseEnrollment() throws Exception {
        // Get the courseEnrollment
        restCourseEnrollmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseEnrollment() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEnrollment
        CourseEnrollment updatedCourseEnrollment = courseEnrollmentRepository.findById(courseEnrollment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseEnrollment are not directly saved in db
        em.detach(updatedCourseEnrollment);
        updatedCourseEnrollment
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .status(UPDATED_STATUS)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(updatedCourseEnrollment);

        restCourseEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseEnrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEnrollmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseEnrollmentToMatchAllProperties(updatedCourseEnrollment);
    }

    @Test
    @Transactional
    void putNonExistingCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseEnrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEnrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEnrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEnrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEnrollment using partial update
        CourseEnrollment partialUpdatedCourseEnrollment = new CourseEnrollment();
        partialUpdatedCourseEnrollment.setId(courseEnrollment.getId());

        partialUpdatedCourseEnrollment.progressPercent(UPDATED_PROGRESS_PERCENT).enrolledAt(UPDATED_ENROLLED_AT);

        restCourseEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the CourseEnrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseEnrollmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseEnrollment, courseEnrollment),
            getPersistedCourseEnrollment(courseEnrollment)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEnrollment using partial update
        CourseEnrollment partialUpdatedCourseEnrollment = new CourseEnrollment();
        partialUpdatedCourseEnrollment.setId(courseEnrollment.getId());

        partialUpdatedCourseEnrollment
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .status(UPDATED_STATUS)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);

        restCourseEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the CourseEnrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseEnrollmentUpdatableFieldsEquals(
            partialUpdatedCourseEnrollment,
            getPersistedCourseEnrollment(partialUpdatedCourseEnrollment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseEnrollmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseEnrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseEnrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEnrollment.setId(longCount.incrementAndGet());

        // Create the CourseEnrollment
        CourseEnrollmentDTO courseEnrollmentDTO = courseEnrollmentMapper.toDto(courseEnrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEnrollmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseEnrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseEnrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseEnrollment() throws Exception {
        // Initialize the database
        insertedCourseEnrollment = courseEnrollmentRepository.saveAndFlush(courseEnrollment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseEnrollment
        restCourseEnrollmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseEnrollment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseEnrollmentRepository.count();
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

    protected CourseEnrollment getPersistedCourseEnrollment(CourseEnrollment courseEnrollment) {
        return courseEnrollmentRepository.findById(courseEnrollment.getId()).orElseThrow();
    }

    protected void assertPersistedCourseEnrollmentToMatchAllProperties(CourseEnrollment expectedCourseEnrollment) {
        assertCourseEnrollmentAllPropertiesEquals(expectedCourseEnrollment, getPersistedCourseEnrollment(expectedCourseEnrollment));
    }

    protected void assertPersistedCourseEnrollmentToMatchUpdatableProperties(CourseEnrollment expectedCourseEnrollment) {
        assertCourseEnrollmentAllUpdatablePropertiesEquals(
            expectedCourseEnrollment,
            getPersistedCourseEnrollment(expectedCourseEnrollment)
        );
    }
}
