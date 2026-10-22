package com.lumi.web.rest;

import static com.lumi.domain.StudentStreakAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.StudentStreak;
import com.lumi.domain.User;
import com.lumi.repository.StudentStreakRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.StudentStreakService;
import com.lumi.service.dto.StudentStreakDTO;
import com.lumi.service.mapper.StudentStreakMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link StudentStreakResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentStreakResourceIT {

    private static final Integer DEFAULT_CURRENT_STREAK = 0;
    private static final Integer UPDATED_CURRENT_STREAK = 1;
    private static final Integer SMALLER_CURRENT_STREAK = 0 - 1;

    private static final Integer DEFAULT_LONGEST_STREAK = 0;
    private static final Integer UPDATED_LONGEST_STREAK = 1;
    private static final Integer SMALLER_LONGEST_STREAK = 0 - 1;

    private static final Integer DEFAULT_GEMS_BALANCE = 0;
    private static final Integer UPDATED_GEMS_BALANCE = 1;
    private static final Integer SMALLER_GEMS_BALANCE = 0 - 1;

    private static final LocalDate DEFAULT_LAST_ACTIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_ACTIVE_DATE = LocalDate.parse("2026-10-12");
    private static final LocalDate SMALLER_LAST_ACTIVE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/student-streaks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentStreakRepository studentStreakRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StudentStreakRepository studentStreakRepositoryMock;

    @Autowired
    private StudentStreakMapper studentStreakMapper;

    @Mock
    private StudentStreakService studentStreakServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentStreakMockMvc;

    private StudentStreak studentStreak;

    private StudentStreak insertedStudentStreak;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentStreak createEntity(EntityManager em) {
        StudentStreak studentStreak = new StudentStreak()
            .currentStreak(DEFAULT_CURRENT_STREAK)
            .longestStreak(DEFAULT_LONGEST_STREAK)
            .gemsBalance(DEFAULT_GEMS_BALANCE)
            .lastActiveDate(DEFAULT_LAST_ACTIVE_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        studentStreak.setUser(user);
        return studentStreak;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentStreak createUpdatedEntity(EntityManager em) {
        StudentStreak updatedStudentStreak = new StudentStreak()
            .currentStreak(UPDATED_CURRENT_STREAK)
            .longestStreak(UPDATED_LONGEST_STREAK)
            .gemsBalance(UPDATED_GEMS_BALANCE)
            .lastActiveDate(UPDATED_LAST_ACTIVE_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedStudentStreak.setUser(user);
        return updatedStudentStreak;
    }

    @BeforeEach
    void initTest() {
        studentStreak = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentStreak != null) {
            studentStreakRepository.delete(insertedStudentStreak);
            insertedStudentStreak = null;
        }
    }

    @Test
    @Transactional
    void createStudentStreak() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);
        var returnedStudentStreakDTO = om.readValue(
            restStudentStreakMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentStreakDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentStreakDTO.class
        );

        // Validate the StudentStreak in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudentStreak = studentStreakMapper.toEntity(returnedStudentStreakDTO);
        assertStudentStreakUpdatableFieldsEquals(returnedStudentStreak, getPersistedStudentStreak(returnedStudentStreak));

        insertedStudentStreak = returnedStudentStreak;
    }

    @Test
    @Transactional
    void createStudentStreakWithExistingId() throws Exception {
        // Create the StudentStreak with an existing ID
        studentStreak.setId(1L);
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentStreakMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentStreakDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStudentStreaks() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentStreak.getId().intValue())))
            .andExpect(jsonPath("$.[*].currentStreak").value(hasItem(DEFAULT_CURRENT_STREAK)))
            .andExpect(jsonPath("$.[*].longestStreak").value(hasItem(DEFAULT_LONGEST_STREAK)))
            .andExpect(jsonPath("$.[*].gemsBalance").value(hasItem(DEFAULT_GEMS_BALANCE)))
            .andExpect(jsonPath("$.[*].lastActiveDate").value(hasItem(DEFAULT_LAST_ACTIVE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentStreaksWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentStreakServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restStudentStreakMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentStreakServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentStreaksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentStreakServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restStudentStreakMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentStreakRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentStreak() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get the studentStreak
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL_ID, studentStreak.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentStreak.getId().intValue()))
            .andExpect(jsonPath("$.currentStreak").value(DEFAULT_CURRENT_STREAK))
            .andExpect(jsonPath("$.longestStreak").value(DEFAULT_LONGEST_STREAK))
            .andExpect(jsonPath("$.gemsBalance").value(DEFAULT_GEMS_BALANCE))
            .andExpect(jsonPath("$.lastActiveDate").value(DEFAULT_LAST_ACTIVE_DATE.toString()));
    }

    @Test
    @Transactional
    void getStudentStreaksByIdFiltering() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        Long id = studentStreak.getId();

        defaultStudentStreakFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStudentStreakFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStudentStreakFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak equals to
        defaultStudentStreakFiltering("currentStreak.equals=" + DEFAULT_CURRENT_STREAK, "currentStreak.equals=" + UPDATED_CURRENT_STREAK);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak in
        defaultStudentStreakFiltering(
            "currentStreak.in=" + DEFAULT_CURRENT_STREAK + "," + UPDATED_CURRENT_STREAK,
            "currentStreak.in=" + UPDATED_CURRENT_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak is not null
        defaultStudentStreakFiltering("currentStreak.specified=true", "currentStreak.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak is greater than or equal to
        defaultStudentStreakFiltering(
            "currentStreak.greaterThanOrEqual=" + DEFAULT_CURRENT_STREAK,
            "currentStreak.greaterThanOrEqual=" + UPDATED_CURRENT_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak is less than or equal to
        defaultStudentStreakFiltering(
            "currentStreak.lessThanOrEqual=" + DEFAULT_CURRENT_STREAK,
            "currentStreak.lessThanOrEqual=" + SMALLER_CURRENT_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak is less than
        defaultStudentStreakFiltering(
            "currentStreak.lessThan=" + UPDATED_CURRENT_STREAK,
            "currentStreak.lessThan=" + DEFAULT_CURRENT_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByCurrentStreakIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where currentStreak is greater than
        defaultStudentStreakFiltering(
            "currentStreak.greaterThan=" + SMALLER_CURRENT_STREAK,
            "currentStreak.greaterThan=" + DEFAULT_CURRENT_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak equals to
        defaultStudentStreakFiltering("longestStreak.equals=" + DEFAULT_LONGEST_STREAK, "longestStreak.equals=" + UPDATED_LONGEST_STREAK);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak in
        defaultStudentStreakFiltering(
            "longestStreak.in=" + DEFAULT_LONGEST_STREAK + "," + UPDATED_LONGEST_STREAK,
            "longestStreak.in=" + UPDATED_LONGEST_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak is not null
        defaultStudentStreakFiltering("longestStreak.specified=true", "longestStreak.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak is greater than or equal to
        defaultStudentStreakFiltering(
            "longestStreak.greaterThanOrEqual=" + DEFAULT_LONGEST_STREAK,
            "longestStreak.greaterThanOrEqual=" + UPDATED_LONGEST_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak is less than or equal to
        defaultStudentStreakFiltering(
            "longestStreak.lessThanOrEqual=" + DEFAULT_LONGEST_STREAK,
            "longestStreak.lessThanOrEqual=" + SMALLER_LONGEST_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak is less than
        defaultStudentStreakFiltering(
            "longestStreak.lessThan=" + UPDATED_LONGEST_STREAK,
            "longestStreak.lessThan=" + DEFAULT_LONGEST_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLongestStreakIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where longestStreak is greater than
        defaultStudentStreakFiltering(
            "longestStreak.greaterThan=" + SMALLER_LONGEST_STREAK,
            "longestStreak.greaterThan=" + DEFAULT_LONGEST_STREAK
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance equals to
        defaultStudentStreakFiltering("gemsBalance.equals=" + DEFAULT_GEMS_BALANCE, "gemsBalance.equals=" + UPDATED_GEMS_BALANCE);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance in
        defaultStudentStreakFiltering(
            "gemsBalance.in=" + DEFAULT_GEMS_BALANCE + "," + UPDATED_GEMS_BALANCE,
            "gemsBalance.in=" + UPDATED_GEMS_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance is not null
        defaultStudentStreakFiltering("gemsBalance.specified=true", "gemsBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance is greater than or equal to
        defaultStudentStreakFiltering(
            "gemsBalance.greaterThanOrEqual=" + DEFAULT_GEMS_BALANCE,
            "gemsBalance.greaterThanOrEqual=" + UPDATED_GEMS_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance is less than or equal to
        defaultStudentStreakFiltering(
            "gemsBalance.lessThanOrEqual=" + DEFAULT_GEMS_BALANCE,
            "gemsBalance.lessThanOrEqual=" + SMALLER_GEMS_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance is less than
        defaultStudentStreakFiltering("gemsBalance.lessThan=" + UPDATED_GEMS_BALANCE, "gemsBalance.lessThan=" + DEFAULT_GEMS_BALANCE);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByGemsBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where gemsBalance is greater than
        defaultStudentStreakFiltering("gemsBalance.greaterThan=" + SMALLER_GEMS_BALANCE, "gemsBalance.greaterThan=" + DEFAULT_GEMS_BALANCE);
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate equals to
        defaultStudentStreakFiltering(
            "lastActiveDate.equals=" + DEFAULT_LAST_ACTIVE_DATE,
            "lastActiveDate.equals=" + UPDATED_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate in
        defaultStudentStreakFiltering(
            "lastActiveDate.in=" + DEFAULT_LAST_ACTIVE_DATE + "," + UPDATED_LAST_ACTIVE_DATE,
            "lastActiveDate.in=" + UPDATED_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate is not null
        defaultStudentStreakFiltering("lastActiveDate.specified=true", "lastActiveDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate is greater than or equal to
        defaultStudentStreakFiltering(
            "lastActiveDate.greaterThanOrEqual=" + DEFAULT_LAST_ACTIVE_DATE,
            "lastActiveDate.greaterThanOrEqual=" + UPDATED_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate is less than or equal to
        defaultStudentStreakFiltering(
            "lastActiveDate.lessThanOrEqual=" + DEFAULT_LAST_ACTIVE_DATE,
            "lastActiveDate.lessThanOrEqual=" + SMALLER_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate is less than
        defaultStudentStreakFiltering(
            "lastActiveDate.lessThan=" + UPDATED_LAST_ACTIVE_DATE,
            "lastActiveDate.lessThan=" + DEFAULT_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByLastActiveDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        // Get all the studentStreakList where lastActiveDate is greater than
        defaultStudentStreakFiltering(
            "lastActiveDate.greaterThan=" + SMALLER_LAST_ACTIVE_DATE,
            "lastActiveDate.greaterThan=" + DEFAULT_LAST_ACTIVE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStudentStreaksByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = studentStreak.getUser();
        studentStreakRepository.saveAndFlush(studentStreak);
        Long userId = user.getId();
        // Get all the studentStreakList where user equals to userId
        defaultStudentStreakShouldBeFound("userId.equals=" + userId);

        // Get all the studentStreakList where user equals to (userId + 1)
        defaultStudentStreakShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultStudentStreakFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStudentStreakShouldBeFound(shouldBeFound);
        defaultStudentStreakShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStudentStreakShouldBeFound(String filter) throws Exception {
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentStreak.getId().intValue())))
            .andExpect(jsonPath("$.[*].currentStreak").value(hasItem(DEFAULT_CURRENT_STREAK)))
            .andExpect(jsonPath("$.[*].longestStreak").value(hasItem(DEFAULT_LONGEST_STREAK)))
            .andExpect(jsonPath("$.[*].gemsBalance").value(hasItem(DEFAULT_GEMS_BALANCE)))
            .andExpect(jsonPath("$.[*].lastActiveDate").value(hasItem(DEFAULT_LAST_ACTIVE_DATE.toString())));

        // Check, that the count call also returns 1
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStudentStreakShouldNotBeFound(String filter) throws Exception {
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStudentStreakMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStudentStreak() throws Exception {
        // Get the studentStreak
        restStudentStreakMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentStreak() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentStreak
        StudentStreak updatedStudentStreak = studentStreakRepository.findById(studentStreak.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudentStreak are not directly saved in db
        em.detach(updatedStudentStreak);
        updatedStudentStreak
            .currentStreak(UPDATED_CURRENT_STREAK)
            .longestStreak(UPDATED_LONGEST_STREAK)
            .gemsBalance(UPDATED_GEMS_BALANCE)
            .lastActiveDate(UPDATED_LAST_ACTIVE_DATE);
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(updatedStudentStreak);

        restStudentStreakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentStreakDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentStreakDTO))
            )
            .andExpect(status().isOk());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentStreakToMatchAllProperties(updatedStudentStreak);
    }

    @Test
    @Transactional
    void putNonExistingStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentStreakDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentStreakDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentStreakDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentStreakDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentStreakWithPatch() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentStreak using partial update
        StudentStreak partialUpdatedStudentStreak = new StudentStreak();
        partialUpdatedStudentStreak.setId(studentStreak.getId());

        partialUpdatedStudentStreak.currentStreak(UPDATED_CURRENT_STREAK).longestStreak(UPDATED_LONGEST_STREAK);

        restStudentStreakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentStreak.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentStreak))
            )
            .andExpect(status().isOk());

        // Validate the StudentStreak in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentStreakUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentStreak, studentStreak),
            getPersistedStudentStreak(studentStreak)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentStreakWithPatch() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentStreak using partial update
        StudentStreak partialUpdatedStudentStreak = new StudentStreak();
        partialUpdatedStudentStreak.setId(studentStreak.getId());

        partialUpdatedStudentStreak
            .currentStreak(UPDATED_CURRENT_STREAK)
            .longestStreak(UPDATED_LONGEST_STREAK)
            .gemsBalance(UPDATED_GEMS_BALANCE)
            .lastActiveDate(UPDATED_LAST_ACTIVE_DATE);

        restStudentStreakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentStreak.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentStreak))
            )
            .andExpect(status().isOk());

        // Validate the StudentStreak in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentStreakUpdatableFieldsEquals(partialUpdatedStudentStreak, getPersistedStudentStreak(partialUpdatedStudentStreak));
    }

    @Test
    @Transactional
    void patchNonExistingStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentStreakDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentStreakDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentStreakDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentStreak() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentStreak.setId(longCount.incrementAndGet());

        // Create the StudentStreak
        StudentStreakDTO studentStreakDTO = studentStreakMapper.toDto(studentStreak);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentStreakMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentStreakDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentStreak in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentStreak() throws Exception {
        // Initialize the database
        insertedStudentStreak = studentStreakRepository.saveAndFlush(studentStreak);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentStreak
        restStudentStreakMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentStreak.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentStreakRepository.count();
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

    protected StudentStreak getPersistedStudentStreak(StudentStreak studentStreak) {
        return studentStreakRepository.findById(studentStreak.getId()).orElseThrow();
    }

    protected void assertPersistedStudentStreakToMatchAllProperties(StudentStreak expectedStudentStreak) {
        assertStudentStreakAllPropertiesEquals(expectedStudentStreak, getPersistedStudentStreak(expectedStudentStreak));
    }

    protected void assertPersistedStudentStreakToMatchUpdatableProperties(StudentStreak expectedStudentStreak) {
        assertStudentStreakAllUpdatablePropertiesEquals(expectedStudentStreak, getPersistedStudentStreak(expectedStudentStreak));
    }
}
