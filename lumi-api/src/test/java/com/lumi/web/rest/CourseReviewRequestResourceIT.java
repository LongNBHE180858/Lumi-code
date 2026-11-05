package com.lumi.web.rest;

import static com.lumi.domain.CourseReviewRequestAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Course;
import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.User;
import com.lumi.repository.CourseReviewRequestRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CourseReviewRequestService;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.mapper.CourseReviewRequestMapper;
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
 * Integration tests for the {@link CourseReviewRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseReviewRequestResourceIT {

    private static final Long DEFAULT_COURSE_VERSION = 1L;
    private static final Long UPDATED_COURSE_VERSION = 2L;
    private static final Long SMALLER_COURSE_VERSION = 1L - 1L;

    private static final String DEFAULT_STATUS = "PENDING";
    private static final String UPDATED_STATUS = "APPROVED";

    private static final String DEFAULT_SUBMISSION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_SUBMISSION_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_RIGHTS_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RIGHTS_CONFIRMED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_REVIEWED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVIEWED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_REVIEWER_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_REVIEWER_FEEDBACK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/course-review-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseReviewRequestRepository courseReviewRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseReviewRequestRepository courseReviewRequestRepositoryMock;

    @Autowired
    private CourseReviewRequestMapper courseReviewRequestMapper;

    @Mock
    private CourseReviewRequestService courseReviewRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseReviewRequestMockMvc;

    private CourseReviewRequest courseReviewRequest;

    private CourseReviewRequest insertedCourseReviewRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseReviewRequest createEntity(EntityManager em) {
        CourseReviewRequest courseReviewRequest = new CourseReviewRequest()
            .courseVersion(DEFAULT_COURSE_VERSION)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.valueOf(DEFAULT_STATUS))
            .submissionNote(DEFAULT_SUBMISSION_NOTE)
            .rightsConfirmedAt(DEFAULT_RIGHTS_CONFIRMED_AT)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .reviewedAt(DEFAULT_REVIEWED_AT)
            .reviewerFeedback(DEFAULT_REVIEWER_FEEDBACK);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        courseReviewRequest.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        courseReviewRequest.setSubmittedBy(user);
        return courseReviewRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseReviewRequest createUpdatedEntity(EntityManager em) {
        CourseReviewRequest updatedCourseReviewRequest = new CourseReviewRequest()
            .courseVersion(UPDATED_COURSE_VERSION)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.valueOf(UPDATED_STATUS))
            .submissionNote(UPDATED_SUBMISSION_NOTE)
            .rightsConfirmedAt(UPDATED_RIGHTS_CONFIRMED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        updatedCourseReviewRequest.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourseReviewRequest.setSubmittedBy(user);
        return updatedCourseReviewRequest;
    }

    @BeforeEach
    void initTest() {
        courseReviewRequest = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseReviewRequest != null) {
            courseReviewRequestRepository.delete(insertedCourseReviewRequest);
            insertedCourseReviewRequest = null;
        }
    }

    @Test
    @Transactional
    void createCourseReviewRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);
        var returnedCourseReviewRequestDTO = om.readValue(
            restCourseReviewRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseReviewRequestDTO.class
        );

        // Validate the CourseReviewRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseReviewRequest = courseReviewRequestMapper.toEntity(returnedCourseReviewRequestDTO);
        assertCourseReviewRequestUpdatableFieldsEquals(
            returnedCourseReviewRequest,
            getPersistedCourseReviewRequest(returnedCourseReviewRequest)
        );

        insertedCourseReviewRequest = returnedCourseReviewRequest;
    }

    @Test
    @Transactional
    void createCourseReviewRequestWithExistingId() throws Exception {
        // Create the CourseReviewRequest with an existing ID
        courseReviewRequest.setId(1L);
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseReviewRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCourseVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseReviewRequest.setCourseVersion(null);

        // Create the CourseReviewRequest, which fails.
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        restCourseReviewRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseReviewRequest.setStatus(null);

        // Create the CourseReviewRequest, which fails.
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        restCourseReviewRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRightsConfirmedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseReviewRequest.setRightsConfirmedAt(null);

        // Create the CourseReviewRequest, which fails.
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        restCourseReviewRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubmittedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseReviewRequest.setSubmittedAt(null);

        // Create the CourseReviewRequest, which fails.
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        restCourseReviewRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequests() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseReviewRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseVersion").value(hasItem(DEFAULT_COURSE_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].submissionNote").value(hasItem(DEFAULT_SUBMISSION_NOTE)))
            .andExpect(jsonPath("$.[*].rightsConfirmedAt").value(hasItem(DEFAULT_RIGHTS_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewedAt").value(hasItem(DEFAULT_REVIEWED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewerFeedback").value(hasItem(DEFAULT_REVIEWER_FEEDBACK)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseReviewRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseReviewRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseReviewRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseReviewRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseReviewRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseReviewRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseReviewRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseReviewRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseReviewRequest() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get the courseReviewRequest
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, courseReviewRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseReviewRequest.getId().intValue()))
            .andExpect(jsonPath("$.courseVersion").value(DEFAULT_COURSE_VERSION.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.submissionNote").value(DEFAULT_SUBMISSION_NOTE))
            .andExpect(jsonPath("$.rightsConfirmedAt").value(DEFAULT_RIGHTS_CONFIRMED_AT.toString()))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.reviewedAt").value(DEFAULT_REVIEWED_AT.toString()))
            .andExpect(jsonPath("$.reviewerFeedback").value(DEFAULT_REVIEWER_FEEDBACK));
    }

    @Test
    @Transactional
    void getCourseReviewRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        Long id = courseReviewRequest.getId();

        defaultCourseReviewRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseReviewRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseReviewRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion equals to
        defaultCourseReviewRequestFiltering(
            "courseVersion.equals=" + DEFAULT_COURSE_VERSION,
            "courseVersion.equals=" + UPDATED_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion in
        defaultCourseReviewRequestFiltering(
            "courseVersion.in=" + DEFAULT_COURSE_VERSION + "," + UPDATED_COURSE_VERSION,
            "courseVersion.in=" + UPDATED_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion is not null
        defaultCourseReviewRequestFiltering("courseVersion.specified=true", "courseVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion is greater than or equal to
        defaultCourseReviewRequestFiltering(
            "courseVersion.greaterThanOrEqual=" + DEFAULT_COURSE_VERSION,
            "courseVersion.greaterThanOrEqual=" + UPDATED_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion is less than or equal to
        defaultCourseReviewRequestFiltering(
            "courseVersion.lessThanOrEqual=" + DEFAULT_COURSE_VERSION,
            "courseVersion.lessThanOrEqual=" + SMALLER_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion is less than
        defaultCourseReviewRequestFiltering(
            "courseVersion.lessThan=" + UPDATED_COURSE_VERSION,
            "courseVersion.lessThan=" + DEFAULT_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where courseVersion is greater than
        defaultCourseReviewRequestFiltering(
            "courseVersion.greaterThan=" + SMALLER_COURSE_VERSION,
            "courseVersion.greaterThan=" + DEFAULT_COURSE_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where status equals to
        defaultCourseReviewRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where status in
        defaultCourseReviewRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where status is not null
        defaultCourseReviewRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where status contains
        defaultCourseReviewRequestFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where status does not contain
        defaultCourseReviewRequestFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmissionNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submissionNote equals to
        defaultCourseReviewRequestFiltering(
            "submissionNote.equals=" + DEFAULT_SUBMISSION_NOTE,
            "submissionNote.equals=" + UPDATED_SUBMISSION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmissionNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submissionNote in
        defaultCourseReviewRequestFiltering(
            "submissionNote.in=" + DEFAULT_SUBMISSION_NOTE + "," + UPDATED_SUBMISSION_NOTE,
            "submissionNote.in=" + UPDATED_SUBMISSION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmissionNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submissionNote is not null
        defaultCourseReviewRequestFiltering("submissionNote.specified=true", "submissionNote.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmissionNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submissionNote contains
        defaultCourseReviewRequestFiltering(
            "submissionNote.contains=" + DEFAULT_SUBMISSION_NOTE,
            "submissionNote.contains=" + UPDATED_SUBMISSION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmissionNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submissionNote does not contain
        defaultCourseReviewRequestFiltering(
            "submissionNote.doesNotContain=" + UPDATED_SUBMISSION_NOTE,
            "submissionNote.doesNotContain=" + DEFAULT_SUBMISSION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByRightsConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where rightsConfirmedAt equals to
        defaultCourseReviewRequestFiltering(
            "rightsConfirmedAt.equals=" + DEFAULT_RIGHTS_CONFIRMED_AT,
            "rightsConfirmedAt.equals=" + UPDATED_RIGHTS_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByRightsConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where rightsConfirmedAt in
        defaultCourseReviewRequestFiltering(
            "rightsConfirmedAt.in=" + DEFAULT_RIGHTS_CONFIRMED_AT + "," + UPDATED_RIGHTS_CONFIRMED_AT,
            "rightsConfirmedAt.in=" + UPDATED_RIGHTS_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByRightsConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where rightsConfirmedAt is not null
        defaultCourseReviewRequestFiltering("rightsConfirmedAt.specified=true", "rightsConfirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submittedAt equals to
        defaultCourseReviewRequestFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submittedAt in
        defaultCourseReviewRequestFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where submittedAt is not null
        defaultCourseReviewRequestFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewedAt equals to
        defaultCourseReviewRequestFiltering("reviewedAt.equals=" + DEFAULT_REVIEWED_AT, "reviewedAt.equals=" + UPDATED_REVIEWED_AT);
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewedAt in
        defaultCourseReviewRequestFiltering(
            "reviewedAt.in=" + DEFAULT_REVIEWED_AT + "," + UPDATED_REVIEWED_AT,
            "reviewedAt.in=" + UPDATED_REVIEWED_AT
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewedAt is not null
        defaultCourseReviewRequestFiltering("reviewedAt.specified=true", "reviewedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewerFeedbackIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewerFeedback equals to
        defaultCourseReviewRequestFiltering(
            "reviewerFeedback.equals=" + DEFAULT_REVIEWER_FEEDBACK,
            "reviewerFeedback.equals=" + UPDATED_REVIEWER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewerFeedbackIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewerFeedback in
        defaultCourseReviewRequestFiltering(
            "reviewerFeedback.in=" + DEFAULT_REVIEWER_FEEDBACK + "," + UPDATED_REVIEWER_FEEDBACK,
            "reviewerFeedback.in=" + UPDATED_REVIEWER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewerFeedbackIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewerFeedback is not null
        defaultCourseReviewRequestFiltering("reviewerFeedback.specified=true", "reviewerFeedback.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewerFeedbackContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewerFeedback contains
        defaultCourseReviewRequestFiltering(
            "reviewerFeedback.contains=" + DEFAULT_REVIEWER_FEEDBACK,
            "reviewerFeedback.contains=" + UPDATED_REVIEWER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewerFeedbackNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        // Get all the courseReviewRequestList where reviewerFeedback does not contain
        defaultCourseReviewRequestFiltering(
            "reviewerFeedback.doesNotContain=" + UPDATED_REVIEWER_FEEDBACK,
            "reviewerFeedback.doesNotContain=" + DEFAULT_REVIEWER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        courseReviewRequest.setCourse(course);
        courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
        Long courseId = course.getId();
        // Get all the courseReviewRequestList where course equals to courseId
        defaultCourseReviewRequestShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseReviewRequestList where course equals to (courseId + 1)
        defaultCourseReviewRequestShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsBySubmittedByIsEqualToSomething() throws Exception {
        User submittedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
            submittedBy = UserResourceIT.createEntity();
        } else {
            submittedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(submittedBy);
        em.flush();
        courseReviewRequest.setSubmittedBy(submittedBy);
        courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
        Long submittedById = submittedBy.getId();
        // Get all the courseReviewRequestList where submittedBy equals to submittedById
        defaultCourseReviewRequestShouldBeFound("submittedById.equals=" + submittedById);

        // Get all the courseReviewRequestList where submittedBy equals to (submittedById + 1)
        defaultCourseReviewRequestShouldNotBeFound("submittedById.equals=" + (submittedById + 1));
    }

    @Test
    @Transactional
    void getAllCourseReviewRequestsByReviewedByIsEqualToSomething() throws Exception {
        User reviewedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
            reviewedBy = UserResourceIT.createEntity();
        } else {
            reviewedBy = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(reviewedBy);
        em.flush();
        courseReviewRequest.setReviewedBy(reviewedBy);
        courseReviewRequestRepository.saveAndFlush(courseReviewRequest);
        Long reviewedById = reviewedBy.getId();
        // Get all the courseReviewRequestList where reviewedBy equals to reviewedById
        defaultCourseReviewRequestShouldBeFound("reviewedById.equals=" + reviewedById);

        // Get all the courseReviewRequestList where reviewedBy equals to (reviewedById + 1)
        defaultCourseReviewRequestShouldNotBeFound("reviewedById.equals=" + (reviewedById + 1));
    }

    private void defaultCourseReviewRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseReviewRequestShouldBeFound(shouldBeFound);
        defaultCourseReviewRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseReviewRequestShouldBeFound(String filter) throws Exception {
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseReviewRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseVersion").value(hasItem(DEFAULT_COURSE_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].submissionNote").value(hasItem(DEFAULT_SUBMISSION_NOTE)))
            .andExpect(jsonPath("$.[*].rightsConfirmedAt").value(hasItem(DEFAULT_RIGHTS_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewedAt").value(hasItem(DEFAULT_REVIEWED_AT.toString())))
            .andExpect(jsonPath("$.[*].reviewerFeedback").value(hasItem(DEFAULT_REVIEWER_FEEDBACK)));

        // Check, that the count call also returns 1
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseReviewRequestShouldNotBeFound(String filter) throws Exception {
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseReviewRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourseReviewRequest() throws Exception {
        // Get the courseReviewRequest
        restCourseReviewRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseReviewRequest() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReviewRequest
        CourseReviewRequest updatedCourseReviewRequest = courseReviewRequestRepository.findById(courseReviewRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseReviewRequest are not directly saved in db
        em.detach(updatedCourseReviewRequest);
        updatedCourseReviewRequest
            .courseVersion(UPDATED_COURSE_VERSION)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.valueOf(UPDATED_STATUS))
            .submissionNote(UPDATED_SUBMISSION_NOTE)
            .rightsConfirmedAt(UPDATED_RIGHTS_CONFIRMED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK);
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(updatedCourseReviewRequest);

        restCourseReviewRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseReviewRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseReviewRequestToMatchAllProperties(updatedCourseReviewRequest);
    }

    @Test
    @Transactional
    void putNonExistingCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseReviewRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseReviewRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseReviewRequestWithPatch() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReviewRequest using partial update
        CourseReviewRequest partialUpdatedCourseReviewRequest = new CourseReviewRequest();
        partialUpdatedCourseReviewRequest.setId(courseReviewRequest.getId());

        partialUpdatedCourseReviewRequest
            .courseVersion(UPDATED_COURSE_VERSION)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.valueOf(UPDATED_STATUS))
            .submittedAt(UPDATED_SUBMITTED_AT)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK);

        restCourseReviewRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseReviewRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseReviewRequest))
            )
            .andExpect(status().isOk());

        // Validate the CourseReviewRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseReviewRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseReviewRequest, courseReviewRequest),
            getPersistedCourseReviewRequest(courseReviewRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseReviewRequestWithPatch() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseReviewRequest using partial update
        CourseReviewRequest partialUpdatedCourseReviewRequest = new CourseReviewRequest();
        partialUpdatedCourseReviewRequest.setId(courseReviewRequest.getId());

        partialUpdatedCourseReviewRequest
            .courseVersion(UPDATED_COURSE_VERSION)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.valueOf(UPDATED_STATUS))
            .submissionNote(UPDATED_SUBMISSION_NOTE)
            .rightsConfirmedAt(UPDATED_RIGHTS_CONFIRMED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .reviewedAt(UPDATED_REVIEWED_AT)
            .reviewerFeedback(UPDATED_REVIEWER_FEEDBACK);

        restCourseReviewRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseReviewRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseReviewRequest))
            )
            .andExpect(status().isOk());

        // Validate the CourseReviewRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseReviewRequestUpdatableFieldsEquals(
            partialUpdatedCourseReviewRequest,
            getPersistedCourseReviewRequest(partialUpdatedCourseReviewRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseReviewRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseReviewRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseReviewRequest.setId(longCount.incrementAndGet());

        // Create the CourseReviewRequest
        CourseReviewRequestDTO courseReviewRequestDTO = courseReviewRequestMapper.toDto(courseReviewRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseReviewRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseReviewRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseReviewRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseReviewRequest() throws Exception {
        // Initialize the database
        insertedCourseReviewRequest = courseReviewRequestRepository.saveAndFlush(courseReviewRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseReviewRequest
        restCourseReviewRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseReviewRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseReviewRequestRepository.count();
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

    protected CourseReviewRequest getPersistedCourseReviewRequest(CourseReviewRequest courseReviewRequest) {
        return courseReviewRequestRepository.findById(courseReviewRequest.getId()).orElseThrow();
    }

    protected void assertPersistedCourseReviewRequestToMatchAllProperties(CourseReviewRequest expectedCourseReviewRequest) {
        assertCourseReviewRequestAllPropertiesEquals(
            expectedCourseReviewRequest,
            getPersistedCourseReviewRequest(expectedCourseReviewRequest)
        );
    }

    protected void assertPersistedCourseReviewRequestToMatchUpdatableProperties(CourseReviewRequest expectedCourseReviewRequest) {
        assertCourseReviewRequestAllUpdatablePropertiesEquals(
            expectedCourseReviewRequest,
            getPersistedCourseReviewRequest(expectedCourseReviewRequest)
        );
    }
}
