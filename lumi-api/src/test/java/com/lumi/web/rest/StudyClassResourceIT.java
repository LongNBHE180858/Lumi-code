package com.lumi.web.rest;

import static com.lumi.domain.StudyClassAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Grade;
import com.lumi.domain.StudyClass;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.StudyClassService;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.mapper.StudyClassMapper;
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
 * Integration tests for the {@link StudyClassResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudyClassResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_JOIN_CODE = "AAAAAAAAAA";
    private static final String UPDATED_JOIN_CODE = "BBBBBBBBBB";

    private static final ClassJoinMode DEFAULT_JOIN_MODE = ClassJoinMode.INVITE_ONLY;
    private static final ClassJoinMode UPDATED_JOIN_MODE = ClassJoinMode.CODE_APPROVAL;

    private static final Integer DEFAULT_MAX_STUDENTS = 1;
    private static final Integer UPDATED_MAX_STUDENTS = 2;
    private static final Integer SMALLER_MAX_STUDENTS = 1 - 1;

    private static final Integer DEFAULT_CURRENT_STUDENTS = 0;
    private static final Integer UPDATED_CURRENT_STUDENTS = 1;
    private static final Integer SMALLER_CURRENT_STUDENTS = 0 - 1;

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.DRAFT;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.PENDING;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/study-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudyClassRepository studyClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StudyClassRepository studyClassRepositoryMock;

    @Autowired
    private StudyClassMapper studyClassMapper;

    @Mock
    private StudyClassService studyClassServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudyClassMockMvc;

    private StudyClass studyClass;

    private StudyClass insertedStudyClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudyClass createEntity(EntityManager em) {
        StudyClass studyClass = new StudyClass()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .joinCode(DEFAULT_JOIN_CODE)
            .joinMode(DEFAULT_JOIN_MODE)
            .maxStudents(DEFAULT_MAX_STUDENTS)
            .currentStudents(DEFAULT_CURRENT_STUDENTS)
            .status(DEFAULT_STATUS)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        studyClass.setInstructor(user);
        return studyClass;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudyClass createUpdatedEntity(EntityManager em) {
        StudyClass updatedStudyClass = new StudyClass()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .joinCode(UPDATED_JOIN_CODE)
            .joinMode(UPDATED_JOIN_MODE)
            .maxStudents(UPDATED_MAX_STUDENTS)
            .currentStudents(UPDATED_CURRENT_STUDENTS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedStudyClass.setInstructor(user);
        return updatedStudyClass;
    }

    @BeforeEach
    void initTest() {
        studyClass = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStudyClass != null) {
            studyClassRepository.delete(insertedStudyClass);
            insertedStudyClass = null;
        }
    }

    @Test
    @Transactional
    void createStudyClass() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);
        var returnedStudyClassDTO = om.readValue(
            restStudyClassMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studyClassDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudyClassDTO.class
        );

        // Validate the StudyClass in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudyClass = studyClassMapper.toEntity(returnedStudyClassDTO);
        assertStudyClassUpdatableFieldsEquals(returnedStudyClass, getPersistedStudyClass(returnedStudyClass));

        insertedStudyClass = returnedStudyClass;
    }

    @Test
    @Transactional
    void createStudyClassWithExistingId() throws Exception {
        // Create the StudyClass with an existing ID
        studyClass.setId(1L);
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudyClassMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studyClassDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studyClass.setName(null);

        // Create the StudyClass, which fails.
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        restStudyClassMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studyClassDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudyClasses() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studyClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].joinCode").value(hasItem(DEFAULT_JOIN_CODE)))
            .andExpect(jsonPath("$.[*].joinMode").value(hasItem(DEFAULT_JOIN_MODE.toString())))
            .andExpect(jsonPath("$.[*].maxStudents").value(hasItem(DEFAULT_MAX_STUDENTS)))
            .andExpect(jsonPath("$.[*].currentStudents").value(hasItem(DEFAULT_CURRENT_STUDENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudyClassesWithEagerRelationshipsIsEnabled() throws Exception {
        when(studyClassServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudyClassMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studyClassServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudyClassesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studyClassServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudyClassMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studyClassRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudyClass() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get the studyClass
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL_ID, studyClass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studyClass.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.joinCode").value(DEFAULT_JOIN_CODE))
            .andExpect(jsonPath("$.joinMode").value(DEFAULT_JOIN_MODE.toString()))
            .andExpect(jsonPath("$.maxStudents").value(DEFAULT_MAX_STUDENTS))
            .andExpect(jsonPath("$.currentStudents").value(DEFAULT_CURRENT_STUDENTS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getStudyClassesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        Long id = studyClass.getId();

        defaultStudyClassFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStudyClassFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStudyClassFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStudyClassesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where name equals to
        defaultStudyClassFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStudyClassesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where name in
        defaultStudyClassFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStudyClassesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where name is not null
        defaultStudyClassFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where name contains
        defaultStudyClassFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStudyClassesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where name does not contain
        defaultStudyClassFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStudyClassesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where description equals to
        defaultStudyClassFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStudyClassesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where description in
        defaultStudyClassFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where description is not null
        defaultStudyClassFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where description contains
        defaultStudyClassFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStudyClassesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where description does not contain
        defaultStudyClassFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinCode equals to
        defaultStudyClassFiltering("joinCode.equals=" + DEFAULT_JOIN_CODE, "joinCode.equals=" + UPDATED_JOIN_CODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinCode in
        defaultStudyClassFiltering("joinCode.in=" + DEFAULT_JOIN_CODE + "," + UPDATED_JOIN_CODE, "joinCode.in=" + UPDATED_JOIN_CODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinCode is not null
        defaultStudyClassFiltering("joinCode.specified=true", "joinCode.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinCode contains
        defaultStudyClassFiltering("joinCode.contains=" + DEFAULT_JOIN_CODE, "joinCode.contains=" + UPDATED_JOIN_CODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinCode does not contain
        defaultStudyClassFiltering("joinCode.doesNotContain=" + UPDATED_JOIN_CODE, "joinCode.doesNotContain=" + DEFAULT_JOIN_CODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinMode equals to
        defaultStudyClassFiltering("joinMode.equals=" + DEFAULT_JOIN_MODE, "joinMode.equals=" + UPDATED_JOIN_MODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinMode in
        defaultStudyClassFiltering("joinMode.in=" + DEFAULT_JOIN_MODE + "," + UPDATED_JOIN_MODE, "joinMode.in=" + UPDATED_JOIN_MODE);
    }

    @Test
    @Transactional
    void getAllStudyClassesByJoinModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where joinMode is not null
        defaultStudyClassFiltering("joinMode.specified=true", "joinMode.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents equals to
        defaultStudyClassFiltering("maxStudents.equals=" + DEFAULT_MAX_STUDENTS, "maxStudents.equals=" + UPDATED_MAX_STUDENTS);
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents in
        defaultStudyClassFiltering(
            "maxStudents.in=" + DEFAULT_MAX_STUDENTS + "," + UPDATED_MAX_STUDENTS,
            "maxStudents.in=" + UPDATED_MAX_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents is not null
        defaultStudyClassFiltering("maxStudents.specified=true", "maxStudents.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents is greater than or equal to
        defaultStudyClassFiltering(
            "maxStudents.greaterThanOrEqual=" + DEFAULT_MAX_STUDENTS,
            "maxStudents.greaterThanOrEqual=" + UPDATED_MAX_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents is less than or equal to
        defaultStudyClassFiltering(
            "maxStudents.lessThanOrEqual=" + DEFAULT_MAX_STUDENTS,
            "maxStudents.lessThanOrEqual=" + SMALLER_MAX_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents is less than
        defaultStudyClassFiltering("maxStudents.lessThan=" + UPDATED_MAX_STUDENTS, "maxStudents.lessThan=" + DEFAULT_MAX_STUDENTS);
    }

    @Test
    @Transactional
    void getAllStudyClassesByMaxStudentsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where maxStudents is greater than
        defaultStudyClassFiltering("maxStudents.greaterThan=" + SMALLER_MAX_STUDENTS, "maxStudents.greaterThan=" + DEFAULT_MAX_STUDENTS);
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents equals to
        defaultStudyClassFiltering(
            "currentStudents.equals=" + DEFAULT_CURRENT_STUDENTS,
            "currentStudents.equals=" + UPDATED_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents in
        defaultStudyClassFiltering(
            "currentStudents.in=" + DEFAULT_CURRENT_STUDENTS + "," + UPDATED_CURRENT_STUDENTS,
            "currentStudents.in=" + UPDATED_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents is not null
        defaultStudyClassFiltering("currentStudents.specified=true", "currentStudents.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents is greater than or equal to
        defaultStudyClassFiltering(
            "currentStudents.greaterThanOrEqual=" + DEFAULT_CURRENT_STUDENTS,
            "currentStudents.greaterThanOrEqual=" + UPDATED_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents is less than or equal to
        defaultStudyClassFiltering(
            "currentStudents.lessThanOrEqual=" + DEFAULT_CURRENT_STUDENTS,
            "currentStudents.lessThanOrEqual=" + SMALLER_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents is less than
        defaultStudyClassFiltering(
            "currentStudents.lessThan=" + UPDATED_CURRENT_STUDENTS,
            "currentStudents.lessThan=" + DEFAULT_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByCurrentStudentsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where currentStudents is greater than
        defaultStudyClassFiltering(
            "currentStudents.greaterThan=" + SMALLER_CURRENT_STUDENTS,
            "currentStudents.greaterThan=" + DEFAULT_CURRENT_STUDENTS
        );
    }

    @Test
    @Transactional
    void getAllStudyClassesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where status equals to
        defaultStudyClassFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStudyClassesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where status in
        defaultStudyClassFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStudyClassesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where status is not null
        defaultStudyClassFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where isDeleted equals to
        defaultStudyClassFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStudyClassesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where isDeleted in
        defaultStudyClassFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStudyClassesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where isDeleted is not null
        defaultStudyClassFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where createdAt equals to
        defaultStudyClassFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStudyClassesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where createdAt in
        defaultStudyClassFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStudyClassesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        // Get all the studyClassList where createdAt is not null
        defaultStudyClassFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStudyClassesByInstructorIsEqualToSomething() throws Exception {
        User instructor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            studyClassRepository.saveAndFlush(studyClass);
            instructor = UserResourceIT.createEntity();
        } else {
            instructor = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(instructor);
        em.flush();
        studyClass.setInstructor(instructor);
        studyClassRepository.saveAndFlush(studyClass);
        Long instructorId = instructor.getId();
        // Get all the studyClassList where instructor equals to instructorId
        defaultStudyClassShouldBeFound("instructorId.equals=" + instructorId);

        // Get all the studyClassList where instructor equals to (instructorId + 1)
        defaultStudyClassShouldNotBeFound("instructorId.equals=" + (instructorId + 1));
    }

    @Test
    @Transactional
    void getAllStudyClassesBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            studyClassRepository.saveAndFlush(studyClass);
            subject = SubjectResourceIT.createEntity();
        } else {
            subject = TestUtil.findAll(em, Subject.class).getFirst();
        }
        em.persist(subject);
        em.flush();
        studyClass.setSubject(subject);
        studyClassRepository.saveAndFlush(studyClass);
        Long subjectId = subject.getId();
        // Get all the studyClassList where subject equals to subjectId
        defaultStudyClassShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the studyClassList where subject equals to (subjectId + 1)
        defaultStudyClassShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    @Test
    @Transactional
    void getAllStudyClassesByGradeIsEqualToSomething() throws Exception {
        Grade grade;
        if (TestUtil.findAll(em, Grade.class).isEmpty()) {
            studyClassRepository.saveAndFlush(studyClass);
            grade = GradeResourceIT.createEntity();
        } else {
            grade = TestUtil.findAll(em, Grade.class).getFirst();
        }
        em.persist(grade);
        em.flush();
        studyClass.setGrade(grade);
        studyClassRepository.saveAndFlush(studyClass);
        Long gradeId = grade.getId();
        // Get all the studyClassList where grade equals to gradeId
        defaultStudyClassShouldBeFound("gradeId.equals=" + gradeId);

        // Get all the studyClassList where grade equals to (gradeId + 1)
        defaultStudyClassShouldNotBeFound("gradeId.equals=" + (gradeId + 1));
    }

    private void defaultStudyClassFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStudyClassShouldBeFound(shouldBeFound);
        defaultStudyClassShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStudyClassShouldBeFound(String filter) throws Exception {
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studyClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].joinCode").value(hasItem(DEFAULT_JOIN_CODE)))
            .andExpect(jsonPath("$.[*].joinMode").value(hasItem(DEFAULT_JOIN_MODE.toString())))
            .andExpect(jsonPath("$.[*].maxStudents").value(hasItem(DEFAULT_MAX_STUDENTS)))
            .andExpect(jsonPath("$.[*].currentStudents").value(hasItem(DEFAULT_CURRENT_STUDENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStudyClassShouldNotBeFound(String filter) throws Exception {
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStudyClassMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStudyClass() throws Exception {
        // Get the studyClass
        restStudyClassMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudyClass() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studyClass
        StudyClass updatedStudyClass = studyClassRepository.findById(studyClass.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudyClass are not directly saved in db
        em.detach(updatedStudyClass);
        updatedStudyClass
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .joinCode(UPDATED_JOIN_CODE)
            .joinMode(UPDATED_JOIN_MODE)
            .maxStudents(UPDATED_MAX_STUDENTS)
            .currentStudents(UPDATED_CURRENT_STUDENTS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT);
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(updatedStudyClass);

        restStudyClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studyClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studyClassDTO))
            )
            .andExpect(status().isOk());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudyClassToMatchAllProperties(updatedStudyClass);
    }

    @Test
    @Transactional
    void putNonExistingStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studyClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studyClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studyClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studyClassDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudyClassWithPatch() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studyClass using partial update
        StudyClass partialUpdatedStudyClass = new StudyClass();
        partialUpdatedStudyClass.setId(studyClass.getId());

        partialUpdatedStudyClass
            .joinCode(UPDATED_JOIN_CODE)
            .joinMode(UPDATED_JOIN_MODE)
            .maxStudents(UPDATED_MAX_STUDENTS)
            .currentStudents(UPDATED_CURRENT_STUDENTS)
            .createdAt(UPDATED_CREATED_AT);

        restStudyClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudyClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudyClass))
            )
            .andExpect(status().isOk());

        // Validate the StudyClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudyClassUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudyClass, studyClass),
            getPersistedStudyClass(studyClass)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudyClassWithPatch() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studyClass using partial update
        StudyClass partialUpdatedStudyClass = new StudyClass();
        partialUpdatedStudyClass.setId(studyClass.getId());

        partialUpdatedStudyClass
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .joinCode(UPDATED_JOIN_CODE)
            .joinMode(UPDATED_JOIN_MODE)
            .maxStudents(UPDATED_MAX_STUDENTS)
            .currentStudents(UPDATED_CURRENT_STUDENTS)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT);

        restStudyClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudyClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudyClass))
            )
            .andExpect(status().isOk());

        // Validate the StudyClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudyClassUpdatableFieldsEquals(partialUpdatedStudyClass, getPersistedStudyClass(partialUpdatedStudyClass));
    }

    @Test
    @Transactional
    void patchNonExistingStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studyClassDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studyClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studyClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudyClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studyClass.setId(longCount.incrementAndGet());

        // Create the StudyClass
        StudyClassDTO studyClassDTO = studyClassMapper.toDto(studyClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudyClassMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studyClassDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudyClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudyClass() throws Exception {
        // Initialize the database
        insertedStudyClass = studyClassRepository.saveAndFlush(studyClass);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studyClass
        restStudyClassMockMvc
            .perform(delete(ENTITY_API_URL_ID, studyClass.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studyClassRepository.count();
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

    protected StudyClass getPersistedStudyClass(StudyClass studyClass) {
        return studyClassRepository.findById(studyClass.getId()).orElseThrow();
    }

    protected void assertPersistedStudyClassToMatchAllProperties(StudyClass expectedStudyClass) {
        assertStudyClassAllPropertiesEquals(expectedStudyClass, getPersistedStudyClass(expectedStudyClass));
    }

    protected void assertPersistedStudyClassToMatchUpdatableProperties(StudyClass expectedStudyClass) {
        assertStudyClassAllUpdatablePropertiesEquals(expectedStudyClass, getPersistedStudyClass(expectedStudyClass));
    }
}
