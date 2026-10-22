package com.lumi.web.rest;

import static com.lumi.domain.AssignmentStudentAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentStudent;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.AssignmentStudentService;
import com.lumi.service.dto.AssignmentStudentDTO;
import com.lumi.service.mapper.AssignmentStudentMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AssignmentStudentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssignmentStudentResourceIT {

    private static final String ENTITY_API_URL = "/api/assignment-students";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignmentStudentRepository assignmentStudentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AssignmentStudentRepository assignmentStudentRepositoryMock;

    @Autowired
    private AssignmentStudentMapper assignmentStudentMapper;

    @Mock
    private AssignmentStudentService assignmentStudentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentStudentMockMvc;

    private AssignmentStudent assignmentStudent;

    private AssignmentStudent insertedAssignmentStudent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentStudent createEntity(EntityManager em) {
        AssignmentStudent assignmentStudent = new AssignmentStudent();
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        assignmentStudent.setStudent(user);
        // Add required entity
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignment = AssignmentResourceIT.createEntity(em);
            em.persist(assignment);
            em.flush();
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        assignmentStudent.setAssignment(assignment);
        return assignmentStudent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentStudent createUpdatedEntity(EntityManager em) {
        AssignmentStudent updatedAssignmentStudent = new AssignmentStudent();
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAssignmentStudent.setStudent(user);
        // Add required entity
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignment = AssignmentResourceIT.createUpdatedEntity(em);
            em.persist(assignment);
            em.flush();
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        updatedAssignmentStudent.setAssignment(assignment);
        return updatedAssignmentStudent;
    }

    @BeforeEach
    void initTest() {
        assignmentStudent = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignmentStudent != null) {
            assignmentStudentRepository.delete(insertedAssignmentStudent);
            insertedAssignmentStudent = null;
        }
    }

    @Test
    @Transactional
    void createAssignmentStudent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);
        var returnedAssignmentStudentDTO = om.readValue(
            restAssignmentStudentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentStudentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignmentStudentDTO.class
        );

        // Validate the AssignmentStudent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssignmentStudent = assignmentStudentMapper.toEntity(returnedAssignmentStudentDTO);
        assertAssignmentStudentUpdatableFieldsEquals(returnedAssignmentStudent, getPersistedAssignmentStudent(returnedAssignmentStudent));

        insertedAssignmentStudent = returnedAssignmentStudent;
    }

    @Test
    @Transactional
    void createAssignmentStudentWithExistingId() throws Exception {
        // Create the AssignmentStudent with an existing ID
        assignmentStudent.setId(1L);
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentStudentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentStudentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssignmentStudents() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        // Get all the assignmentStudentList
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentStudent.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentStudentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assignmentStudentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentStudentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assignmentStudentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentStudentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assignmentStudentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentStudentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assignmentStudentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssignmentStudent() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        // Get the assignmentStudent
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL_ID, assignmentStudent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignmentStudent.getId().intValue()));
    }

    @Test
    @Transactional
    void getAssignmentStudentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        Long id = assignmentStudent.getId();

        defaultAssignmentStudentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssignmentStudentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssignmentStudentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssignmentStudentsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            assignmentStudentRepository.saveAndFlush(assignmentStudent);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        assignmentStudent.setStudent(student);
        assignmentStudentRepository.saveAndFlush(assignmentStudent);
        Long studentId = student.getId();
        // Get all the assignmentStudentList where student equals to studentId
        defaultAssignmentStudentShouldBeFound("studentId.equals=" + studentId);

        // Get all the assignmentStudentList where student equals to (studentId + 1)
        defaultAssignmentStudentShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllAssignmentStudentsByAssignmentIsEqualToSomething() throws Exception {
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignmentStudentRepository.saveAndFlush(assignmentStudent);
            assignment = AssignmentResourceIT.createEntity(em);
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).getFirst();
        }
        em.persist(assignment);
        em.flush();
        assignmentStudent.setAssignment(assignment);
        assignmentStudentRepository.saveAndFlush(assignmentStudent);
        Long assignmentId = assignment.getId();
        // Get all the assignmentStudentList where assignment equals to assignmentId
        defaultAssignmentStudentShouldBeFound("assignmentId.equals=" + assignmentId);

        // Get all the assignmentStudentList where assignment equals to (assignmentId + 1)
        defaultAssignmentStudentShouldNotBeFound("assignmentId.equals=" + (assignmentId + 1));
    }

    private void defaultAssignmentStudentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssignmentStudentShouldBeFound(shouldBeFound);
        defaultAssignmentStudentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentStudentShouldBeFound(String filter) throws Exception {
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentStudent.getId().intValue())));

        // Check, that the count call also returns 1
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentStudentShouldNotBeFound(String filter) throws Exception {
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentStudentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssignmentStudent() throws Exception {
        // Get the assignmentStudent
        restAssignmentStudentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignmentStudent() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentStudent
        AssignmentStudent updatedAssignmentStudent = assignmentStudentRepository.findById(assignmentStudent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssignmentStudent are not directly saved in db
        em.detach(updatedAssignmentStudent);
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(updatedAssignmentStudent);

        restAssignmentStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentStudentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentStudentDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignmentStudentToMatchAllProperties(updatedAssignmentStudent);
    }

    @Test
    @Transactional
    void putNonExistingAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentStudentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentStudentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentStudentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentStudentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignmentStudentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentStudent using partial update
        AssignmentStudent partialUpdatedAssignmentStudent = new AssignmentStudent();
        partialUpdatedAssignmentStudent.setId(assignmentStudent.getId());

        restAssignmentStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentStudent))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentStudent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentStudentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignmentStudent, assignmentStudent),
            getPersistedAssignmentStudent(assignmentStudent)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignmentStudentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentStudent using partial update
        AssignmentStudent partialUpdatedAssignmentStudent = new AssignmentStudent();
        partialUpdatedAssignmentStudent.setId(assignmentStudent.getId());

        restAssignmentStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentStudent))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentStudent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentStudentUpdatableFieldsEquals(
            partialUpdatedAssignmentStudent,
            getPersistedAssignmentStudent(partialUpdatedAssignmentStudent)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignmentStudentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentStudentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentStudentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignmentStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentStudent.setId(longCount.incrementAndGet());

        // Create the AssignmentStudent
        AssignmentStudentDTO assignmentStudentDTO = assignmentStudentMapper.toDto(assignmentStudent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentStudentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignmentStudentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentStudent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignmentStudent() throws Exception {
        // Initialize the database
        insertedAssignmentStudent = assignmentStudentRepository.saveAndFlush(assignmentStudent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignmentStudent
        restAssignmentStudentMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignmentStudent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignmentStudentRepository.count();
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

    protected AssignmentStudent getPersistedAssignmentStudent(AssignmentStudent assignmentStudent) {
        return assignmentStudentRepository.findById(assignmentStudent.getId()).orElseThrow();
    }

    protected void assertPersistedAssignmentStudentToMatchAllProperties(AssignmentStudent expectedAssignmentStudent) {
        assertAssignmentStudentAllPropertiesEquals(expectedAssignmentStudent, getPersistedAssignmentStudent(expectedAssignmentStudent));
    }

    protected void assertPersistedAssignmentStudentToMatchUpdatableProperties(AssignmentStudent expectedAssignmentStudent) {
        assertAssignmentStudentAllUpdatablePropertiesEquals(
            expectedAssignmentStudent,
            getPersistedAssignmentStudent(expectedAssignmentStudent)
        );
    }
}
