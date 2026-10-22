package com.lumi.web.rest;

import static com.lumi.domain.AssignmentAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Assignment;
import com.lumi.domain.StudyClass;
import com.lumi.repository.AssignmentRepository;
import com.lumi.service.AssignmentService;
import com.lumi.service.dto.AssignmentDTO;
import com.lumi.service.mapper.AssignmentMapper;
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
 * Integration tests for the {@link AssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssignmentResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTIONS = "BBBBBBBBBB";

    private static final Instant DEFAULT_DUE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_AT = Instant.ofEpochMilli(1791835009362L);

    private static final BigDecimal DEFAULT_MAX_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MAX_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MAX_SCORE = new BigDecimal(0 - 1);

    private static final Boolean DEFAULT_ALLOW_LATE = false;
    private static final Boolean UPDATED_ALLOW_LATE = true;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String ENTITY_API_URL = "/api/assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssignmentRepository assignmentRepositoryMock;

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Mock
    private AssignmentService assignmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentMockMvc;

    private Assignment assignment;

    private Assignment insertedAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createEntity(EntityManager em) {
        Assignment assignment = new Assignment()
            .title(DEFAULT_TITLE)
            .instructions(DEFAULT_INSTRUCTIONS)
            .dueAt(DEFAULT_DUE_AT)
            .maxScore(DEFAULT_MAX_SCORE)
            .allowLate(DEFAULT_ALLOW_LATE)
            .isDeleted(DEFAULT_IS_DELETED);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        assignment.setStudyClass(studyClass);
        return assignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createUpdatedEntity(EntityManager em) {
        Assignment updatedAssignment = new Assignment()
            .title(UPDATED_TITLE)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueAt(UPDATED_DUE_AT)
            .maxScore(UPDATED_MAX_SCORE)
            .allowLate(UPDATED_ALLOW_LATE)
            .isDeleted(UPDATED_IS_DELETED);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createUpdatedEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        updatedAssignment.setStudyClass(studyClass);
        return updatedAssignment;
    }

    @BeforeEach
    void initTest() {
        assignment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignment != null) {
            assignmentRepository.delete(insertedAssignment);
            insertedAssignment = null;
        }
    }

    @Test
    @Transactional
    void createAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);
        var returnedAssignmentDTO = om.readValue(
            restAssignmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignmentDTO.class
        );

        // Validate the Assignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssignment = assignmentMapper.toEntity(returnedAssignmentDTO);
        assertAssignmentUpdatableFieldsEquals(returnedAssignment, getPersistedAssignment(returnedAssignment));

        insertedAssignment = returnedAssignment;
    }

    @Test
    @Transactional
    void createAssignmentWithExistingId() throws Exception {
        // Create the Assignment with an existing ID
        assignment.setId(1L);
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assignment.setTitle(null);

        // Create the Assignment, which fails.
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        restAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssignments() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].dueAt").value(hasItem(DEFAULT_DUE_AT.toString())))
            .andExpect(jsonPath("$.[*].maxScore").value(hasItem(sameNumber(DEFAULT_MAX_SCORE))))
            .andExpect(jsonPath("$.[*].allowLate").value(hasItem(DEFAULT_ALLOW_LATE)))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assignmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get the assignment
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, assignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignment.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.instructions").value(DEFAULT_INSTRUCTIONS))
            .andExpect(jsonPath("$.dueAt").value(DEFAULT_DUE_AT.toString()))
            .andExpect(jsonPath("$.maxScore").value(sameNumber(DEFAULT_MAX_SCORE)))
            .andExpect(jsonPath("$.allowLate").value(DEFAULT_ALLOW_LATE))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED));
    }

    @Test
    @Transactional
    void getAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        Long id = assignment.getId();

        defaultAssignmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssignmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssignmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title equals to
        defaultAssignmentFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title in
        defaultAssignmentFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title is not null
        defaultAssignmentFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title contains
        defaultAssignmentFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title does not contain
        defaultAssignmentFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByInstructionsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where instructions equals to
        defaultAssignmentFiltering("instructions.equals=" + DEFAULT_INSTRUCTIONS, "instructions.equals=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByInstructionsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where instructions in
        defaultAssignmentFiltering(
            "instructions.in=" + DEFAULT_INSTRUCTIONS + "," + UPDATED_INSTRUCTIONS,
            "instructions.in=" + UPDATED_INSTRUCTIONS
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByInstructionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where instructions is not null
        defaultAssignmentFiltering("instructions.specified=true", "instructions.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByInstructionsContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where instructions contains
        defaultAssignmentFiltering("instructions.contains=" + DEFAULT_INSTRUCTIONS, "instructions.contains=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByInstructionsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where instructions does not contain
        defaultAssignmentFiltering(
            "instructions.doesNotContain=" + UPDATED_INSTRUCTIONS,
            "instructions.doesNotContain=" + DEFAULT_INSTRUCTIONS
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueAt equals to
        defaultAssignmentFiltering("dueAt.equals=" + DEFAULT_DUE_AT, "dueAt.equals=" + UPDATED_DUE_AT);
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueAt in
        defaultAssignmentFiltering("dueAt.in=" + DEFAULT_DUE_AT + "," + UPDATED_DUE_AT, "dueAt.in=" + UPDATED_DUE_AT);
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueAt is not null
        defaultAssignmentFiltering("dueAt.specified=true", "dueAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore equals to
        defaultAssignmentFiltering("maxScore.equals=" + DEFAULT_MAX_SCORE, "maxScore.equals=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore in
        defaultAssignmentFiltering("maxScore.in=" + DEFAULT_MAX_SCORE + "," + UPDATED_MAX_SCORE, "maxScore.in=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore is not null
        defaultAssignmentFiltering("maxScore.specified=true", "maxScore.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore is greater than or equal to
        defaultAssignmentFiltering("maxScore.greaterThanOrEqual=" + DEFAULT_MAX_SCORE, "maxScore.greaterThanOrEqual=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore is less than or equal to
        defaultAssignmentFiltering("maxScore.lessThanOrEqual=" + DEFAULT_MAX_SCORE, "maxScore.lessThanOrEqual=" + SMALLER_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore is less than
        defaultAssignmentFiltering("maxScore.lessThan=" + UPDATED_MAX_SCORE, "maxScore.lessThan=" + DEFAULT_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxScore is greater than
        defaultAssignmentFiltering("maxScore.greaterThan=" + SMALLER_MAX_SCORE, "maxScore.greaterThan=" + DEFAULT_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLate equals to
        defaultAssignmentFiltering("allowLate.equals=" + DEFAULT_ALLOW_LATE, "allowLate.equals=" + UPDATED_ALLOW_LATE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLate in
        defaultAssignmentFiltering("allowLate.in=" + DEFAULT_ALLOW_LATE + "," + UPDATED_ALLOW_LATE, "allowLate.in=" + UPDATED_ALLOW_LATE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLate is not null
        defaultAssignmentFiltering("allowLate.specified=true", "allowLate.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isDeleted equals to
        defaultAssignmentFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isDeleted in
        defaultAssignmentFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isDeleted is not null
        defaultAssignmentFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByStudyClassIsEqualToSomething() throws Exception {
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            assignmentRepository.saveAndFlush(assignment);
            studyClass = StudyClassResourceIT.createEntity(em);
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        em.persist(studyClass);
        em.flush();
        assignment.setStudyClass(studyClass);
        assignmentRepository.saveAndFlush(assignment);
        Long studyClassId = studyClass.getId();
        // Get all the assignmentList where studyClass equals to studyClassId
        defaultAssignmentShouldBeFound("studyClassId.equals=" + studyClassId);

        // Get all the assignmentList where studyClass equals to (studyClassId + 1)
        defaultAssignmentShouldNotBeFound("studyClassId.equals=" + (studyClassId + 1));
    }

    private void defaultAssignmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssignmentShouldBeFound(shouldBeFound);
        defaultAssignmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentShouldBeFound(String filter) throws Exception {
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].dueAt").value(hasItem(DEFAULT_DUE_AT.toString())))
            .andExpect(jsonPath("$.[*].maxScore").value(hasItem(sameNumber(DEFAULT_MAX_SCORE))))
            .andExpect(jsonPath("$.[*].allowLate").value(hasItem(DEFAULT_ALLOW_LATE)))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)));

        // Check, that the count call also returns 1
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentShouldNotBeFound(String filter) throws Exception {
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssignment() throws Exception {
        // Get the assignment
        restAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment
        Assignment updatedAssignment = assignmentRepository.findById(assignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssignment are not directly saved in db
        em.detach(updatedAssignment);
        updatedAssignment
            .title(UPDATED_TITLE)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueAt(UPDATED_DUE_AT)
            .maxScore(UPDATED_MAX_SCORE)
            .allowLate(UPDATED_ALLOW_LATE)
            .isDeleted(UPDATED_IS_DELETED);
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(updatedAssignment);

        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignmentToMatchAllProperties(updatedAssignment);
    }

    @Test
    @Transactional
    void putNonExistingAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment using partial update
        Assignment partialUpdatedAssignment = new Assignment();
        partialUpdatedAssignment.setId(assignment.getId());

        partialUpdatedAssignment
            .title(UPDATED_TITLE)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueAt(UPDATED_DUE_AT)
            .maxScore(UPDATED_MAX_SCORE)
            .isDeleted(UPDATED_IS_DELETED);

        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignment))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignment, assignment),
            getPersistedAssignment(assignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment using partial update
        Assignment partialUpdatedAssignment = new Assignment();
        partialUpdatedAssignment.setId(assignment.getId());

        partialUpdatedAssignment
            .title(UPDATED_TITLE)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueAt(UPDATED_DUE_AT)
            .maxScore(UPDATED_MAX_SCORE)
            .allowLate(UPDATED_ALLOW_LATE)
            .isDeleted(UPDATED_IS_DELETED);

        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignment))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentUpdatableFieldsEquals(partialUpdatedAssignment, getPersistedAssignment(partialUpdatedAssignment));
    }

    @Test
    @Transactional
    void patchNonExistingAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignment
        restAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignmentRepository.count();
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

    protected Assignment getPersistedAssignment(Assignment assignment) {
        return assignmentRepository.findById(assignment.getId()).orElseThrow();
    }

    protected void assertPersistedAssignmentToMatchAllProperties(Assignment expectedAssignment) {
        assertAssignmentAllPropertiesEquals(expectedAssignment, getPersistedAssignment(expectedAssignment));
    }

    protected void assertPersistedAssignmentToMatchUpdatableProperties(Assignment expectedAssignment) {
        assertAssignmentAllUpdatablePropertiesEquals(expectedAssignment, getPersistedAssignment(expectedAssignment));
    }
}
