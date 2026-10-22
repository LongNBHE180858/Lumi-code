package com.lumi.web.rest;

import static com.lumi.domain.SubjectAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Subject;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.mapper.SubjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link SubjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubjectMockMvc;

    private Subject subject;

    private Subject insertedSubject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createEntity() {
        return new Subject().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createUpdatedEntity() {
        return new Subject().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        subject = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubject != null) {
            subjectRepository.delete(insertedSubject);
            insertedSubject = null;
        }
    }

    @Test
    @Transactional
    void createSubject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);
        var returnedSubjectDTO = om.readValue(
            restSubjectMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubjectDTO.class
        );

        // Validate the Subject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubject = subjectMapper.toEntity(returnedSubjectDTO);
        assertSubjectUpdatableFieldsEquals(returnedSubject, getPersistedSubject(returnedSubject));

        insertedSubject = returnedSubject;
    }

    @Test
    @Transactional
    void createSubjectWithExistingId() throws Exception {
        // Create the Subject with an existing ID
        subject.setId(1L);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subject.setName(null);

        // Create the Subject, which fails.
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        restSubjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubjects() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSubject() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get the subject
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL_ID, subject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subject.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getSubjectsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        Long id = subject.getId();

        defaultSubjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSubjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSubjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name equals to
        defaultSubjectFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name in
        defaultSubjectFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name is not null
        defaultSubjectFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name contains
        defaultSubjectFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name does not contain
        defaultSubjectFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where description equals to
        defaultSubjectFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubjectsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where description in
        defaultSubjectFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSubjectsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where description is not null
        defaultSubjectFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where description contains
        defaultSubjectFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubjectsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where description does not contain
        defaultSubjectFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    private void defaultSubjectFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSubjectShouldBeFound(shouldBeFound);
        defaultSubjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubjectShouldBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSubjectShouldNotBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSubject() throws Exception {
        // Get the subject
        restSubjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubject() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject
        Subject updatedSubject = subjectRepository.findById(subject.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubject are not directly saved in db
        em.detach(updatedSubject);
        updatedSubject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        SubjectDTO subjectDTO = subjectMapper.toDto(updatedSubject);

        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubjectToMatchAllProperties(updatedSubject);
    }

    @Test
    @Transactional
    void putNonExistingSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subjectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSubject, subject), getPersistedSubject(subject));
    }

    @Test
    @Transactional
    void fullUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubjectUpdatableFieldsEquals(partialUpdatedSubject, getPersistedSubject(partialUpdatedSubject));
    }

    @Test
    @Transactional
    void patchNonExistingSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subjectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(subjectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubject() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.saveAndFlush(subject);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subject
        restSubjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, subject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subjectRepository.count();
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

    protected Subject getPersistedSubject(Subject subject) {
        return subjectRepository.findById(subject.getId()).orElseThrow();
    }

    protected void assertPersistedSubjectToMatchAllProperties(Subject expectedSubject) {
        assertSubjectAllPropertiesEquals(expectedSubject, getPersistedSubject(expectedSubject));
    }

    protected void assertPersistedSubjectToMatchUpdatableProperties(Subject expectedSubject) {
        assertSubjectAllUpdatablePropertiesEquals(expectedSubject, getPersistedSubject(expectedSubject));
    }
}
