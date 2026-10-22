package com.lumi.web.rest;

import static com.lumi.domain.StudentNoteAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Lesson;
import com.lumi.domain.StudentNote;
import com.lumi.domain.User;
import com.lumi.repository.StudentNoteRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.StudentNoteService;
import com.lumi.service.dto.StudentNoteDTO;
import com.lumi.service.mapper.StudentNoteMapper;
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
 * Integration tests for the {@link StudentNoteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentNoteResourceIT {

    private static final Integer DEFAULT_VIDEO_TIMESTAMP = 0;
    private static final Integer UPDATED_VIDEO_TIMESTAMP = 1;
    private static final Integer SMALLER_VIDEO_TIMESTAMP = 0 - 1;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/student-notes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentNoteRepository studentNoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StudentNoteRepository studentNoteRepositoryMock;

    @Autowired
    private StudentNoteMapper studentNoteMapper;

    @Mock
    private StudentNoteService studentNoteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentNoteMockMvc;

    private StudentNote studentNote;

    private StudentNote insertedStudentNote;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentNote createEntity(EntityManager em) {
        StudentNote studentNote = new StudentNote()
            .videoTimestamp(DEFAULT_VIDEO_TIMESTAMP)
            .content(DEFAULT_CONTENT)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        studentNote.setStudent(user);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        studentNote.setLesson(lesson);
        return studentNote;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentNote createUpdatedEntity(EntityManager em) {
        StudentNote updatedStudentNote = new StudentNote()
            .videoTimestamp(UPDATED_VIDEO_TIMESTAMP)
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedStudentNote.setStudent(user);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        updatedStudentNote.setLesson(lesson);
        return updatedStudentNote;
    }

    @BeforeEach
    void initTest() {
        studentNote = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentNote != null) {
            studentNoteRepository.delete(insertedStudentNote);
            insertedStudentNote = null;
        }
    }

    @Test
    @Transactional
    void createStudentNote() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);
        var returnedStudentNoteDTO = om.readValue(
            restStudentNoteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentNoteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentNoteDTO.class
        );

        // Validate the StudentNote in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudentNote = studentNoteMapper.toEntity(returnedStudentNoteDTO);
        assertStudentNoteUpdatableFieldsEquals(returnedStudentNote, getPersistedStudentNote(returnedStudentNote));

        insertedStudentNote = returnedStudentNote;
    }

    @Test
    @Transactional
    void createStudentNoteWithExistingId() throws Exception {
        // Create the StudentNote with an existing ID
        studentNote.setId(1L);
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentNoteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentNote.setContent(null);

        // Create the StudentNote, which fails.
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        restStudentNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentNoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentNotes() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentNote.getId().intValue())))
            .andExpect(jsonPath("$.[*].videoTimestamp").value(hasItem(DEFAULT_VIDEO_TIMESTAMP)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentNotesWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentNoteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentNoteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentNoteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentNotesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentNoteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentNoteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentNoteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentNote() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get the studentNote
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL_ID, studentNote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentNote.getId().intValue()))
            .andExpect(jsonPath("$.videoTimestamp").value(DEFAULT_VIDEO_TIMESTAMP))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getStudentNotesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        Long id = studentNote.getId();

        defaultStudentNoteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStudentNoteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStudentNoteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp equals to
        defaultStudentNoteFiltering("videoTimestamp.equals=" + DEFAULT_VIDEO_TIMESTAMP, "videoTimestamp.equals=" + UPDATED_VIDEO_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp in
        defaultStudentNoteFiltering(
            "videoTimestamp.in=" + DEFAULT_VIDEO_TIMESTAMP + "," + UPDATED_VIDEO_TIMESTAMP,
            "videoTimestamp.in=" + UPDATED_VIDEO_TIMESTAMP
        );
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp is not null
        defaultStudentNoteFiltering("videoTimestamp.specified=true", "videoTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp is greater than or equal to
        defaultStudentNoteFiltering(
            "videoTimestamp.greaterThanOrEqual=" + DEFAULT_VIDEO_TIMESTAMP,
            "videoTimestamp.greaterThanOrEqual=" + UPDATED_VIDEO_TIMESTAMP
        );
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp is less than or equal to
        defaultStudentNoteFiltering(
            "videoTimestamp.lessThanOrEqual=" + DEFAULT_VIDEO_TIMESTAMP,
            "videoTimestamp.lessThanOrEqual=" + SMALLER_VIDEO_TIMESTAMP
        );
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp is less than
        defaultStudentNoteFiltering(
            "videoTimestamp.lessThan=" + UPDATED_VIDEO_TIMESTAMP,
            "videoTimestamp.lessThan=" + DEFAULT_VIDEO_TIMESTAMP
        );
    }

    @Test
    @Transactional
    void getAllStudentNotesByVideoTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where videoTimestamp is greater than
        defaultStudentNoteFiltering(
            "videoTimestamp.greaterThan=" + SMALLER_VIDEO_TIMESTAMP,
            "videoTimestamp.greaterThan=" + DEFAULT_VIDEO_TIMESTAMP
        );
    }

    @Test
    @Transactional
    void getAllStudentNotesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where content equals to
        defaultStudentNoteFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where content in
        defaultStudentNoteFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where content is not null
        defaultStudentNoteFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentNotesByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where content contains
        defaultStudentNoteFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where content does not contain
        defaultStudentNoteFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where createdAt equals to
        defaultStudentNoteFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where createdAt in
        defaultStudentNoteFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStudentNotesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        // Get all the studentNoteList where createdAt is not null
        defaultStudentNoteFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentNotesByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            studentNoteRepository.saveAndFlush(studentNote);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        studentNote.setStudent(student);
        studentNoteRepository.saveAndFlush(studentNote);
        Long studentId = student.getId();
        // Get all the studentNoteList where student equals to studentId
        defaultStudentNoteShouldBeFound("studentId.equals=" + studentId);

        // Get all the studentNoteList where student equals to (studentId + 1)
        defaultStudentNoteShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllStudentNotesByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            studentNoteRepository.saveAndFlush(studentNote);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        studentNote.setLesson(lesson);
        studentNoteRepository.saveAndFlush(studentNote);
        Long lessonId = lesson.getId();
        // Get all the studentNoteList where lesson equals to lessonId
        defaultStudentNoteShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the studentNoteList where lesson equals to (lessonId + 1)
        defaultStudentNoteShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultStudentNoteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStudentNoteShouldBeFound(shouldBeFound);
        defaultStudentNoteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStudentNoteShouldBeFound(String filter) throws Exception {
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentNote.getId().intValue())))
            .andExpect(jsonPath("$.[*].videoTimestamp").value(hasItem(DEFAULT_VIDEO_TIMESTAMP)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStudentNoteShouldNotBeFound(String filter) throws Exception {
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStudentNoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStudentNote() throws Exception {
        // Get the studentNote
        restStudentNoteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentNote() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentNote
        StudentNote updatedStudentNote = studentNoteRepository.findById(studentNote.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudentNote are not directly saved in db
        em.detach(updatedStudentNote);
        updatedStudentNote.videoTimestamp(UPDATED_VIDEO_TIMESTAMP).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(updatedStudentNote);

        restStudentNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentNoteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentNoteDTO))
            )
            .andExpect(status().isOk());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentNoteToMatchAllProperties(updatedStudentNote);
    }

    @Test
    @Transactional
    void putNonExistingStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentNoteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentNoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentNoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentNoteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentNoteWithPatch() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentNote using partial update
        StudentNote partialUpdatedStudentNote = new StudentNote();
        partialUpdatedStudentNote.setId(studentNote.getId());

        partialUpdatedStudentNote.content(UPDATED_CONTENT);

        restStudentNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentNote.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentNote))
            )
            .andExpect(status().isOk());

        // Validate the StudentNote in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentNoteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentNote, studentNote),
            getPersistedStudentNote(studentNote)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentNoteWithPatch() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentNote using partial update
        StudentNote partialUpdatedStudentNote = new StudentNote();
        partialUpdatedStudentNote.setId(studentNote.getId());

        partialUpdatedStudentNote.videoTimestamp(UPDATED_VIDEO_TIMESTAMP).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restStudentNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentNote.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentNote))
            )
            .andExpect(status().isOk());

        // Validate the StudentNote in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentNoteUpdatableFieldsEquals(partialUpdatedStudentNote, getPersistedStudentNote(partialUpdatedStudentNote));
    }

    @Test
    @Transactional
    void patchNonExistingStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentNoteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentNoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentNoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentNote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentNote.setId(longCount.incrementAndGet());

        // Create the StudentNote
        StudentNoteDTO studentNoteDTO = studentNoteMapper.toDto(studentNote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentNoteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentNoteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentNote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentNote() throws Exception {
        // Initialize the database
        insertedStudentNote = studentNoteRepository.saveAndFlush(studentNote);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentNote
        restStudentNoteMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentNote.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentNoteRepository.count();
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

    protected StudentNote getPersistedStudentNote(StudentNote studentNote) {
        return studentNoteRepository.findById(studentNote.getId()).orElseThrow();
    }

    protected void assertPersistedStudentNoteToMatchAllProperties(StudentNote expectedStudentNote) {
        assertStudentNoteAllPropertiesEquals(expectedStudentNote, getPersistedStudentNote(expectedStudentNote));
    }

    protected void assertPersistedStudentNoteToMatchUpdatableProperties(StudentNote expectedStudentNote) {
        assertStudentNoteAllUpdatablePropertiesEquals(expectedStudentNote, getPersistedStudentNote(expectedStudentNote));
    }
}
