package com.lumi.web.rest;

import static com.lumi.domain.ClassMemberAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.ClassMemberService;
import com.lumi.service.dto.ClassMemberDTO;
import com.lumi.service.mapper.ClassMemberMapper;
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
 * Integration tests for the {@link ClassMemberResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClassMemberResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_JOINED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JOINED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/class-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClassMemberRepository classMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ClassMemberRepository classMemberRepositoryMock;

    @Autowired
    private ClassMemberMapper classMemberMapper;

    @Mock
    private ClassMemberService classMemberServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassMemberMockMvc;

    private ClassMember classMember;

    private ClassMember insertedClassMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassMember createEntity(EntityManager em) {
        ClassMember classMember = new ClassMember().status(DEFAULT_STATUS).joinedAt(DEFAULT_JOINED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        classMember.setStudent(user);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        classMember.setStudyClass(studyClass);
        return classMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassMember createUpdatedEntity(EntityManager em) {
        ClassMember updatedClassMember = new ClassMember().status(UPDATED_STATUS).joinedAt(UPDATED_JOINED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedClassMember.setStudent(user);
        // Add required entity
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            studyClass = StudyClassResourceIT.createUpdatedEntity(em);
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        updatedClassMember.setStudyClass(studyClass);
        return updatedClassMember;
    }

    @BeforeEach
    void initTest() {
        classMember = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedClassMember != null) {
            classMemberRepository.delete(insertedClassMember);
            insertedClassMember = null;
        }
    }

    @Test
    @Transactional
    void createClassMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);
        var returnedClassMemberDTO = om.readValue(
            restClassMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClassMemberDTO.class
        );

        // Validate the ClassMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClassMember = classMemberMapper.toEntity(returnedClassMemberDTO);
        assertClassMemberUpdatableFieldsEquals(returnedClassMember, getPersistedClassMember(returnedClassMember));

        insertedClassMember = returnedClassMember;
    }

    @Test
    @Transactional
    void createClassMemberWithExistingId() throws Exception {
        // Create the ClassMember with an existing ID
        classMember.setId(1L);
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClassMembers() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClassMembersWithEagerRelationshipsIsEnabled() throws Exception {
        when(classMemberServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassMemberMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(classMemberServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClassMembersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(classMemberServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassMemberMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(classMemberRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClassMember() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get the classMember
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, classMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classMember.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.joinedAt").value(DEFAULT_JOINED_AT.toString()));
    }

    @Test
    @Transactional
    void getClassMembersByIdFiltering() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        Long id = classMember.getId();

        defaultClassMemberFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClassMemberFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClassMemberFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClassMembersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where status equals to
        defaultClassMemberFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClassMembersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where status in
        defaultClassMemberFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClassMembersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where status is not null
        defaultClassMemberFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMembersByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where status contains
        defaultClassMemberFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClassMembersByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where status does not contain
        defaultClassMemberFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllClassMembersByJoinedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where joinedAt equals to
        defaultClassMemberFiltering("joinedAt.equals=" + DEFAULT_JOINED_AT, "joinedAt.equals=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllClassMembersByJoinedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where joinedAt in
        defaultClassMemberFiltering("joinedAt.in=" + DEFAULT_JOINED_AT + "," + UPDATED_JOINED_AT, "joinedAt.in=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllClassMembersByJoinedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        // Get all the classMemberList where joinedAt is not null
        defaultClassMemberFiltering("joinedAt.specified=true", "joinedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClassMembersByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            classMemberRepository.saveAndFlush(classMember);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        classMember.setStudent(student);
        classMemberRepository.saveAndFlush(classMember);
        Long studentId = student.getId();
        // Get all the classMemberList where student equals to studentId
        defaultClassMemberShouldBeFound("studentId.equals=" + studentId);

        // Get all the classMemberList where student equals to (studentId + 1)
        defaultClassMemberShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllClassMembersByStudyClassIsEqualToSomething() throws Exception {
        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            classMemberRepository.saveAndFlush(classMember);
            studyClass = StudyClassResourceIT.createEntity(em);
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        em.persist(studyClass);
        em.flush();
        classMember.setStudyClass(studyClass);
        classMemberRepository.saveAndFlush(classMember);
        Long studyClassId = studyClass.getId();
        // Get all the classMemberList where studyClass equals to studyClassId
        defaultClassMemberShouldBeFound("studyClassId.equals=" + studyClassId);

        // Get all the classMemberList where studyClass equals to (studyClassId + 1)
        defaultClassMemberShouldNotBeFound("studyClassId.equals=" + (studyClassId + 1));
    }

    private void defaultClassMemberFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClassMemberShouldBeFound(shouldBeFound);
        defaultClassMemberShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClassMemberShouldBeFound(String filter) throws Exception {
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())));

        // Check, that the count call also returns 1
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClassMemberShouldNotBeFound(String filter) throws Exception {
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClassMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClassMember() throws Exception {
        // Get the classMember
        restClassMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClassMember() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMember
        ClassMember updatedClassMember = classMemberRepository.findById(classMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClassMember are not directly saved in db
        em.detach(updatedClassMember);
        updatedClassMember.status(UPDATED_STATUS).joinedAt(UPDATED_JOINED_AT);
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(updatedClassMember);

        restClassMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClassMemberToMatchAllProperties(updatedClassMember);
    }

    @Test
    @Transactional
    void putNonExistingClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassMemberWithPatch() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMember using partial update
        ClassMember partialUpdatedClassMember = new ClassMember();
        partialUpdatedClassMember.setId(classMember.getId());

        partialUpdatedClassMember.status(UPDATED_STATUS);

        restClassMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassMember))
            )
            .andExpect(status().isOk());

        // Validate the ClassMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClassMember, classMember),
            getPersistedClassMember(classMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateClassMemberWithPatch() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classMember using partial update
        ClassMember partialUpdatedClassMember = new ClassMember();
        partialUpdatedClassMember.setId(classMember.getId());

        partialUpdatedClassMember.status(UPDATED_STATUS).joinedAt(UPDATED_JOINED_AT);

        restClassMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassMember))
            )
            .andExpect(status().isOk());

        // Validate the ClassMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassMemberUpdatableFieldsEquals(partialUpdatedClassMember, getPersistedClassMember(partialUpdatedClassMember));
    }

    @Test
    @Transactional
    void patchNonExistingClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classMember.setId(longCount.incrementAndGet());

        // Create the ClassMember
        ClassMemberDTO classMemberDTO = classMemberMapper.toDto(classMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(classMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassMember() throws Exception {
        // Initialize the database
        insertedClassMember = classMemberRepository.saveAndFlush(classMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classMember
        restClassMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, classMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classMemberRepository.count();
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

    protected ClassMember getPersistedClassMember(ClassMember classMember) {
        return classMemberRepository.findById(classMember.getId()).orElseThrow();
    }

    protected void assertPersistedClassMemberToMatchAllProperties(ClassMember expectedClassMember) {
        assertClassMemberAllPropertiesEquals(expectedClassMember, getPersistedClassMember(expectedClassMember));
    }

    protected void assertPersistedClassMemberToMatchUpdatableProperties(ClassMember expectedClassMember) {
        assertClassMemberAllUpdatablePropertiesEquals(expectedClassMember, getPersistedClassMember(expectedClassMember));
    }
}
