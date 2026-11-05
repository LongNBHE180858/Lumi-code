package com.lumi.web.rest;

import static com.lumi.domain.StudentKnowledgeProgressAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.StudentKnowledgeProgress;
import com.lumi.domain.User;
import com.lumi.repository.StudentKnowledgeProgressRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.StudentKnowledgeProgressService;
import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import com.lumi.service.mapper.StudentKnowledgeProgressMapper;
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
 * Integration tests for the {@link StudentKnowledgeProgressResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentKnowledgeProgressResourceIT {

    private static final BigDecimal DEFAULT_MASTERY_SCORE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MASTERY_SCORE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MASTERY_SCORE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_EVIDENCE_COUNT = 0;
    private static final Integer UPDATED_EVIDENCE_COUNT = 1;
    private static final Integer SMALLER_EVIDENCE_COUNT = 0 - 1;

    private static final String DEFAULT_CALCULATION_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_CALCULATION_VERSION = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/student-knowledge-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentKnowledgeProgressRepository studentKnowledgeProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StudentKnowledgeProgressRepository studentKnowledgeProgressRepositoryMock;

    @Autowired
    private StudentKnowledgeProgressMapper studentKnowledgeProgressMapper;

    @Mock
    private StudentKnowledgeProgressService studentKnowledgeProgressServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentKnowledgeProgressMockMvc;

    private StudentKnowledgeProgress studentKnowledgeProgress;

    private StudentKnowledgeProgress insertedStudentKnowledgeProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentKnowledgeProgress createEntity(EntityManager em) {
        StudentKnowledgeProgress studentKnowledgeProgress = new StudentKnowledgeProgress()
            .masteryScore(DEFAULT_MASTERY_SCORE)
            .evidenceCount(DEFAULT_EVIDENCE_COUNT)
            .calculationVersion(DEFAULT_CALCULATION_VERSION)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        studentKnowledgeProgress.setStudent(user);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeTestFixtures.createEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        studentKnowledgeProgress.setKnowledgeNode(knowledgeNode);
        return studentKnowledgeProgress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentKnowledgeProgress createUpdatedEntity(EntityManager em) {
        StudentKnowledgeProgress updatedStudentKnowledgeProgress = new StudentKnowledgeProgress()
            .masteryScore(UPDATED_MASTERY_SCORE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .calculationVersion(UPDATED_CALCULATION_VERSION)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedStudentKnowledgeProgress.setStudent(user);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeTestFixtures.createUpdatedEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        updatedStudentKnowledgeProgress.setKnowledgeNode(knowledgeNode);
        return updatedStudentKnowledgeProgress;
    }

    @BeforeEach
    void initTest() {
        studentKnowledgeProgress = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentKnowledgeProgress != null) {
            studentKnowledgeProgressRepository.delete(insertedStudentKnowledgeProgress);
            insertedStudentKnowledgeProgress = null;
        }
    }

    @Test
    @Transactional
    void createStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);
        var returnedStudentKnowledgeProgressDTO = om.readValue(
            restStudentKnowledgeProgressMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentKnowledgeProgressDTO.class
        );

        // Validate the StudentKnowledgeProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudentKnowledgeProgress = studentKnowledgeProgressMapper.toEntity(returnedStudentKnowledgeProgressDTO);
        assertStudentKnowledgeProgressUpdatableFieldsEquals(
            returnedStudentKnowledgeProgress,
            getPersistedStudentKnowledgeProgress(returnedStudentKnowledgeProgress)
        );

        insertedStudentKnowledgeProgress = returnedStudentKnowledgeProgress;
    }

    @Test
    @Transactional
    void createStudentKnowledgeProgressWithExistingId() throws Exception {
        // Create the StudentKnowledgeProgress with an existing ID
        studentKnowledgeProgress.setId(1L);
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentKnowledgeProgressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEvidenceCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentKnowledgeProgress.setEvidenceCount(null);

        // Create the StudentKnowledgeProgress, which fails.
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        restStudentKnowledgeProgressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentKnowledgeProgress.setUpdatedAt(null);

        // Create the StudentKnowledgeProgress, which fails.
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        restStudentKnowledgeProgressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgresses() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentKnowledgeProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].masteryScore").value(hasItem(sameNumber(DEFAULT_MASTERY_SCORE))))
            .andExpect(jsonPath("$.[*].evidenceCount").value(hasItem(DEFAULT_EVIDENCE_COUNT)))
            .andExpect(jsonPath("$.[*].calculationVersion").value(hasItem(DEFAULT_CALCULATION_VERSION)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentKnowledgeProgressesWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentKnowledgeProgressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentKnowledgeProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentKnowledgeProgressServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentKnowledgeProgressesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentKnowledgeProgressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentKnowledgeProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentKnowledgeProgressRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentKnowledgeProgress() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get the studentKnowledgeProgress
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, studentKnowledgeProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentKnowledgeProgress.getId().intValue()))
            .andExpect(jsonPath("$.masteryScore").value(sameNumber(DEFAULT_MASTERY_SCORE)))
            .andExpect(jsonPath("$.evidenceCount").value(DEFAULT_EVIDENCE_COUNT))
            .andExpect(jsonPath("$.calculationVersion").value(DEFAULT_CALCULATION_VERSION))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getStudentKnowledgeProgressesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        Long id = studentKnowledgeProgress.getId();

        defaultStudentKnowledgeProgressFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStudentKnowledgeProgressFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStudentKnowledgeProgressFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore equals to
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.equals=" + DEFAULT_MASTERY_SCORE,
            "masteryScore.equals=" + UPDATED_MASTERY_SCORE
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore in
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.in=" + DEFAULT_MASTERY_SCORE + "," + UPDATED_MASTERY_SCORE,
            "masteryScore.in=" + UPDATED_MASTERY_SCORE
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore is not null
        defaultStudentKnowledgeProgressFiltering("masteryScore.specified=true", "masteryScore.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore is greater than or equal to
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.greaterThanOrEqual=" + DEFAULT_MASTERY_SCORE,
            "masteryScore.greaterThanOrEqual=" + DEFAULT_MASTERY_SCORE.add(BigDecimal.ONE)
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore is less than or equal to
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.lessThanOrEqual=" + DEFAULT_MASTERY_SCORE,
            "masteryScore.lessThanOrEqual=" + SMALLER_MASTERY_SCORE
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore is less than
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.lessThan=" + DEFAULT_MASTERY_SCORE.add(BigDecimal.ONE),
            "masteryScore.lessThan=" + DEFAULT_MASTERY_SCORE
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByMasteryScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where masteryScore is greater than
        defaultStudentKnowledgeProgressFiltering(
            "masteryScore.greaterThan=" + SMALLER_MASTERY_SCORE,
            "masteryScore.greaterThan=" + DEFAULT_MASTERY_SCORE
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount equals to
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.equals=" + DEFAULT_EVIDENCE_COUNT,
            "evidenceCount.equals=" + UPDATED_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount in
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.in=" + DEFAULT_EVIDENCE_COUNT + "," + UPDATED_EVIDENCE_COUNT,
            "evidenceCount.in=" + UPDATED_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount is not null
        defaultStudentKnowledgeProgressFiltering("evidenceCount.specified=true", "evidenceCount.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount is greater than or equal to
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.greaterThanOrEqual=" + DEFAULT_EVIDENCE_COUNT,
            "evidenceCount.greaterThanOrEqual=" + UPDATED_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount is less than or equal to
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.lessThanOrEqual=" + DEFAULT_EVIDENCE_COUNT,
            "evidenceCount.lessThanOrEqual=" + SMALLER_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount is less than
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.lessThan=" + UPDATED_EVIDENCE_COUNT,
            "evidenceCount.lessThan=" + DEFAULT_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByEvidenceCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where evidenceCount is greater than
        defaultStudentKnowledgeProgressFiltering(
            "evidenceCount.greaterThan=" + SMALLER_EVIDENCE_COUNT,
            "evidenceCount.greaterThan=" + DEFAULT_EVIDENCE_COUNT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByCalculationVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where calculationVersion equals to
        defaultStudentKnowledgeProgressFiltering(
            "calculationVersion.equals=" + DEFAULT_CALCULATION_VERSION,
            "calculationVersion.equals=" + UPDATED_CALCULATION_VERSION
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByCalculationVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where calculationVersion in
        defaultStudentKnowledgeProgressFiltering(
            "calculationVersion.in=" + DEFAULT_CALCULATION_VERSION + "," + UPDATED_CALCULATION_VERSION,
            "calculationVersion.in=" + UPDATED_CALCULATION_VERSION
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByCalculationVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where calculationVersion is not null
        defaultStudentKnowledgeProgressFiltering("calculationVersion.specified=true", "calculationVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByCalculationVersionContainsSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where calculationVersion contains
        defaultStudentKnowledgeProgressFiltering(
            "calculationVersion.contains=" + DEFAULT_CALCULATION_VERSION,
            "calculationVersion.contains=" + UPDATED_CALCULATION_VERSION
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByCalculationVersionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where calculationVersion does not contain
        defaultStudentKnowledgeProgressFiltering(
            "calculationVersion.doesNotContain=" + UPDATED_CALCULATION_VERSION,
            "calculationVersion.doesNotContain=" + DEFAULT_CALCULATION_VERSION
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where updatedAt equals to
        defaultStudentKnowledgeProgressFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where updatedAt in
        defaultStudentKnowledgeProgressFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        // Get all the studentKnowledgeProgressList where updatedAt is not null
        defaultStudentKnowledgeProgressFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        studentKnowledgeProgress.setStudent(student);
        studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);
        Long studentId = student.getId();
        // Get all the studentKnowledgeProgressList where student equals to studentId
        defaultStudentKnowledgeProgressShouldBeFound("studentId.equals=" + studentId);

        // Get all the studentKnowledgeProgressList where student equals to (studentId + 1)
        defaultStudentKnowledgeProgressShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllStudentKnowledgeProgressesByKnowledgeNodeIsEqualToSomething() throws Exception {
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);
            knowledgeNode = KnowledgeNodeTestFixtures.createEntity();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        em.persist(knowledgeNode);
        em.flush();
        studentKnowledgeProgress.setKnowledgeNode(knowledgeNode);
        studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);
        Long knowledgeNodeId = knowledgeNode.getId();
        // Get all the studentKnowledgeProgressList where knowledgeNode equals to knowledgeNodeId
        defaultStudentKnowledgeProgressShouldBeFound("knowledgeNodeId.equals=" + knowledgeNodeId);

        // Get all the studentKnowledgeProgressList where knowledgeNode equals to (knowledgeNodeId + 1)
        defaultStudentKnowledgeProgressShouldNotBeFound("knowledgeNodeId.equals=" + (knowledgeNodeId + 1));
    }

    private void defaultStudentKnowledgeProgressFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStudentKnowledgeProgressShouldBeFound(shouldBeFound);
        defaultStudentKnowledgeProgressShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStudentKnowledgeProgressShouldBeFound(String filter) throws Exception {
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentKnowledgeProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].masteryScore").value(hasItem(sameNumber(DEFAULT_MASTERY_SCORE))))
            .andExpect(jsonPath("$.[*].evidenceCount").value(hasItem(DEFAULT_EVIDENCE_COUNT)))
            .andExpect(jsonPath("$.[*].calculationVersion").value(hasItem(DEFAULT_CALCULATION_VERSION)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStudentKnowledgeProgressShouldNotBeFound(String filter) throws Exception {
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStudentKnowledgeProgressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStudentKnowledgeProgress() throws Exception {
        // Get the studentKnowledgeProgress
        restStudentKnowledgeProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentKnowledgeProgress() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentKnowledgeProgress
        StudentKnowledgeProgress updatedStudentKnowledgeProgress = studentKnowledgeProgressRepository
            .findById(studentKnowledgeProgress.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedStudentKnowledgeProgress are not directly saved in db
        em.detach(updatedStudentKnowledgeProgress);
        updatedStudentKnowledgeProgress
            .masteryScore(UPDATED_MASTERY_SCORE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .calculationVersion(UPDATED_CALCULATION_VERSION)
            .updatedAt(UPDATED_UPDATED_AT);
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(updatedStudentKnowledgeProgress);

        restStudentKnowledgeProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentKnowledgeProgressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isOk());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentKnowledgeProgressToMatchAllProperties(updatedStudentKnowledgeProgress);
    }

    @Test
    @Transactional
    void putNonExistingStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentKnowledgeProgressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentKnowledgeProgressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentKnowledgeProgressWithPatch() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentKnowledgeProgress using partial update
        StudentKnowledgeProgress partialUpdatedStudentKnowledgeProgress = new StudentKnowledgeProgress();
        partialUpdatedStudentKnowledgeProgress.setId(studentKnowledgeProgress.getId());

        restStudentKnowledgeProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentKnowledgeProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentKnowledgeProgress))
            )
            .andExpect(status().isOk());

        // Validate the StudentKnowledgeProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentKnowledgeProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentKnowledgeProgress, studentKnowledgeProgress),
            getPersistedStudentKnowledgeProgress(studentKnowledgeProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentKnowledgeProgressWithPatch() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentKnowledgeProgress using partial update
        StudentKnowledgeProgress partialUpdatedStudentKnowledgeProgress = new StudentKnowledgeProgress();
        partialUpdatedStudentKnowledgeProgress.setId(studentKnowledgeProgress.getId());

        partialUpdatedStudentKnowledgeProgress
            .masteryScore(UPDATED_MASTERY_SCORE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .calculationVersion(UPDATED_CALCULATION_VERSION)
            .updatedAt(UPDATED_UPDATED_AT);

        restStudentKnowledgeProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentKnowledgeProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentKnowledgeProgress))
            )
            .andExpect(status().isOk());

        // Validate the StudentKnowledgeProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentKnowledgeProgressUpdatableFieldsEquals(
            partialUpdatedStudentKnowledgeProgress,
            getPersistedStudentKnowledgeProgress(partialUpdatedStudentKnowledgeProgress)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentKnowledgeProgressDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentKnowledgeProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentKnowledgeProgress.setId(longCount.incrementAndGet());

        // Create the StudentKnowledgeProgress
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentKnowledgeProgressMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentKnowledgeProgressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentKnowledgeProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentKnowledgeProgress() throws Exception {
        // Initialize the database
        insertedStudentKnowledgeProgress = studentKnowledgeProgressRepository.saveAndFlush(studentKnowledgeProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentKnowledgeProgress
        restStudentKnowledgeProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentKnowledgeProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentKnowledgeProgressRepository.count();
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

    protected StudentKnowledgeProgress getPersistedStudentKnowledgeProgress(StudentKnowledgeProgress studentKnowledgeProgress) {
        return studentKnowledgeProgressRepository.findById(studentKnowledgeProgress.getId()).orElseThrow();
    }

    protected void assertPersistedStudentKnowledgeProgressToMatchAllProperties(StudentKnowledgeProgress expectedStudentKnowledgeProgress) {
        assertStudentKnowledgeProgressAllPropertiesEquals(
            expectedStudentKnowledgeProgress,
            getPersistedStudentKnowledgeProgress(expectedStudentKnowledgeProgress)
        );
    }

    protected void assertPersistedStudentKnowledgeProgressToMatchUpdatableProperties(
        StudentKnowledgeProgress expectedStudentKnowledgeProgress
    ) {
        assertStudentKnowledgeProgressAllUpdatablePropertiesEquals(
            expectedStudentKnowledgeProgress,
            getPersistedStudentKnowledgeProgress(expectedStudentKnowledgeProgress)
        );
    }
}
