package com.lumi.web.rest;

import static com.lumi.domain.AssignmentSubmissionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentSubmission;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.AssignmentSubmissionService;
import com.lumi.service.dto.AssignmentSubmissionDTO;
import com.lumi.service.mapper.AssignmentSubmissionMapper;
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
 * Integration tests for the {@link AssignmentSubmissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssignmentSubmissionResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SCORE = new BigDecimal(0 - 1);

    private static final String DEFAULT_TEACHER_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_TEACHER_FEEDBACK = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/assignment-submissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AssignmentSubmissionRepository assignmentSubmissionRepositoryMock;

    @Autowired
    private AssignmentSubmissionMapper assignmentSubmissionMapper;

    @Mock
    private AssignmentSubmissionService assignmentSubmissionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentSubmissionMockMvc;

    private AssignmentSubmission assignmentSubmission;

    private AssignmentSubmission insertedAssignmentSubmission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentSubmission createEntity(EntityManager em) {
        AssignmentSubmission assignmentSubmission = new AssignmentSubmission()
            .content(DEFAULT_CONTENT)
            .score(DEFAULT_SCORE)
            .teacherFeedback(DEFAULT_TEACHER_FEEDBACK)
            .status(DEFAULT_STATUS)
            .submittedAt(DEFAULT_SUBMITTED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        assignmentSubmission.setStudent(user);
        // Add required entity
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignment = AssignmentResourceIT.createEntity(em);
            em.persist(assignment);
            em.flush();
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        assignmentSubmission.setAssignment(assignment);
        return assignmentSubmission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentSubmission createUpdatedEntity(EntityManager em) {
        AssignmentSubmission updatedAssignmentSubmission = new AssignmentSubmission()
            .content(UPDATED_CONTENT)
            .score(UPDATED_SCORE)
            .teacherFeedback(UPDATED_TEACHER_FEEDBACK)
            .status(UPDATED_STATUS)
            .submittedAt(UPDATED_SUBMITTED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAssignmentSubmission.setStudent(user);
        // Add required entity
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignment = AssignmentResourceIT.createUpdatedEntity(em);
            em.persist(assignment);
            em.flush();
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        updatedAssignmentSubmission.setAssignment(assignment);
        return updatedAssignmentSubmission;
    }

    @BeforeEach
    void initTest() {
        assignmentSubmission = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignmentSubmission != null) {
            assignmentSubmissionRepository.delete(insertedAssignmentSubmission);
            insertedAssignmentSubmission = null;
        }
    }

    @Test
    @Transactional
    void createAssignmentSubmission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);
        var returnedAssignmentSubmissionDTO = om.readValue(
            restAssignmentSubmissionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignmentSubmissionDTO.class
        );

        // Validate the AssignmentSubmission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssignmentSubmission = assignmentSubmissionMapper.toEntity(returnedAssignmentSubmissionDTO);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            returnedAssignmentSubmission,
            getPersistedAssignmentSubmission(returnedAssignmentSubmission)
        );

        insertedAssignmentSubmission = returnedAssignmentSubmission;
    }

    @Test
    @Transactional
    void createAssignmentSubmissionWithExistingId() throws Exception {
        // Create the AssignmentSubmission with an existing ID
        assignmentSubmission.setId(1L);
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentSubmissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissions() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentSubmission.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))))
            .andExpect(jsonPath("$.[*].teacherFeedback").value(hasItem(DEFAULT_TEACHER_FEEDBACK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentSubmissionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assignmentSubmissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assignmentSubmissionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentSubmissionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assignmentSubmissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assignmentSubmissionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get the assignmentSubmission
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL_ID, assignmentSubmission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignmentSubmission.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.score").value(sameNumber(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.teacherFeedback").value(DEFAULT_TEACHER_FEEDBACK))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()));
    }

    @Test
    @Transactional
    void getAssignmentSubmissionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        Long id = assignmentSubmission.getId();

        defaultAssignmentSubmissionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssignmentSubmissionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssignmentSubmissionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where content equals to
        defaultAssignmentSubmissionFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where content in
        defaultAssignmentSubmissionFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where content is not null
        defaultAssignmentSubmissionFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where content contains
        defaultAssignmentSubmissionFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where content does not contain
        defaultAssignmentSubmissionFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score equals to
        defaultAssignmentSubmissionFiltering("score.equals=" + DEFAULT_SCORE, "score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score in
        defaultAssignmentSubmissionFiltering("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE, "score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score is not null
        defaultAssignmentSubmissionFiltering("score.specified=true", "score.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score is greater than or equal to
        defaultAssignmentSubmissionFiltering("score.greaterThanOrEqual=" + DEFAULT_SCORE, "score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score is less than or equal to
        defaultAssignmentSubmissionFiltering("score.lessThanOrEqual=" + DEFAULT_SCORE, "score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score is less than
        defaultAssignmentSubmissionFiltering("score.lessThan=" + UPDATED_SCORE, "score.lessThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where score is greater than
        defaultAssignmentSubmissionFiltering("score.greaterThan=" + SMALLER_SCORE, "score.greaterThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByTeacherFeedbackIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where teacherFeedback equals to
        defaultAssignmentSubmissionFiltering(
            "teacherFeedback.equals=" + DEFAULT_TEACHER_FEEDBACK,
            "teacherFeedback.equals=" + UPDATED_TEACHER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByTeacherFeedbackIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where teacherFeedback in
        defaultAssignmentSubmissionFiltering(
            "teacherFeedback.in=" + DEFAULT_TEACHER_FEEDBACK + "," + UPDATED_TEACHER_FEEDBACK,
            "teacherFeedback.in=" + UPDATED_TEACHER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByTeacherFeedbackIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where teacherFeedback is not null
        defaultAssignmentSubmissionFiltering("teacherFeedback.specified=true", "teacherFeedback.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByTeacherFeedbackContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where teacherFeedback contains
        defaultAssignmentSubmissionFiltering(
            "teacherFeedback.contains=" + DEFAULT_TEACHER_FEEDBACK,
            "teacherFeedback.contains=" + UPDATED_TEACHER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByTeacherFeedbackNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where teacherFeedback does not contain
        defaultAssignmentSubmissionFiltering(
            "teacherFeedback.doesNotContain=" + UPDATED_TEACHER_FEEDBACK,
            "teacherFeedback.doesNotContain=" + DEFAULT_TEACHER_FEEDBACK
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status equals to
        defaultAssignmentSubmissionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status in
        defaultAssignmentSubmissionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status is not null
        defaultAssignmentSubmissionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status contains
        defaultAssignmentSubmissionFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status does not contain
        defaultAssignmentSubmissionFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt equals to
        defaultAssignmentSubmissionFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt in
        defaultAssignmentSubmissionFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt is not null
        defaultAssignmentSubmissionFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        assignmentSubmission.setStudent(student);
        assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
        Long studentId = student.getId();
        // Get all the assignmentSubmissionList where student equals to studentId
        defaultAssignmentSubmissionShouldBeFound("studentId.equals=" + studentId);

        // Get all the assignmentSubmissionList where student equals to (studentId + 1)
        defaultAssignmentSubmissionShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByAssignmentIsEqualToSomething() throws Exception {
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
            assignment = AssignmentResourceIT.createEntity(em);
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        em.persist(assignment);
        em.flush();
        assignmentSubmission.setAssignment(assignment);
        assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
        Long assignmentId = assignment.getId();
        // Get all the assignmentSubmissionList where assignment equals to assignmentId
        defaultAssignmentSubmissionShouldBeFound("assignmentId.equals=" + assignmentId);

        // Get all the assignmentSubmissionList where assignment equals to (assignmentId + 1)
        defaultAssignmentSubmissionShouldNotBeFound("assignmentId.equals=" + (assignmentId + 1));
    }

    private void defaultAssignmentSubmissionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssignmentSubmissionShouldBeFound(shouldBeFound);
        defaultAssignmentSubmissionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentSubmissionShouldBeFound(String filter) throws Exception {
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentSubmission.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(sameNumber(DEFAULT_SCORE))))
            .andExpect(jsonPath("$.[*].teacherFeedback").value(hasItem(DEFAULT_TEACHER_FEEDBACK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())));

        // Check, that the count call also returns 1
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentSubmissionShouldNotBeFound(String filter) throws Exception {
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssignmentSubmission() throws Exception {
        // Get the assignmentSubmission
        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission
        AssignmentSubmission updatedAssignmentSubmission = assignmentSubmissionRepository
            .findById(assignmentSubmission.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedAssignmentSubmission are not directly saved in db
        em.detach(updatedAssignmentSubmission);
        updatedAssignmentSubmission
            .content(UPDATED_CONTENT)
            .score(UPDATED_SCORE)
            .teacherFeedback(UPDATED_TEACHER_FEEDBACK)
            .status(UPDATED_STATUS)
            .submittedAt(UPDATED_SUBMITTED_AT);
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(updatedAssignmentSubmission);

        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignmentSubmissionToMatchAllProperties(updatedAssignmentSubmission);
    }

    @Test
    @Transactional
    void putNonExistingAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignmentSubmissionWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission using partial update
        AssignmentSubmission partialUpdatedAssignmentSubmission = new AssignmentSubmission();
        partialUpdatedAssignmentSubmission.setId(assignmentSubmission.getId());

        partialUpdatedAssignmentSubmission.score(UPDATED_SCORE).teacherFeedback(UPDATED_TEACHER_FEEDBACK).status(UPDATED_STATUS);

        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentSubmission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentSubmission))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignmentSubmission, assignmentSubmission),
            getPersistedAssignmentSubmission(assignmentSubmission)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignmentSubmissionWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission using partial update
        AssignmentSubmission partialUpdatedAssignmentSubmission = new AssignmentSubmission();
        partialUpdatedAssignmentSubmission.setId(assignmentSubmission.getId());

        partialUpdatedAssignmentSubmission
            .content(UPDATED_CONTENT)
            .score(UPDATED_SCORE)
            .teacherFeedback(UPDATED_TEACHER_FEEDBACK)
            .status(UPDATED_STATUS)
            .submittedAt(UPDATED_SUBMITTED_AT);

        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentSubmission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentSubmission))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            partialUpdatedAssignmentSubmission,
            getPersistedAssignmentSubmission(partialUpdatedAssignmentSubmission)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignmentSubmission
        restAssignmentSubmissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignmentSubmission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignmentSubmissionRepository.count();
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

    protected AssignmentSubmission getPersistedAssignmentSubmission(AssignmentSubmission assignmentSubmission) {
        return assignmentSubmissionRepository.findById(assignmentSubmission.getId()).orElseThrow();
    }

    protected void assertPersistedAssignmentSubmissionToMatchAllProperties(AssignmentSubmission expectedAssignmentSubmission) {
        assertAssignmentSubmissionAllPropertiesEquals(
            expectedAssignmentSubmission,
            getPersistedAssignmentSubmission(expectedAssignmentSubmission)
        );
    }

    protected void assertPersistedAssignmentSubmissionToMatchUpdatableProperties(AssignmentSubmission expectedAssignmentSubmission) {
        assertAssignmentSubmissionAllUpdatablePropertiesEquals(
            expectedAssignmentSubmission,
            getPersistedAssignmentSubmission(expectedAssignmentSubmission)
        );
    }
}
