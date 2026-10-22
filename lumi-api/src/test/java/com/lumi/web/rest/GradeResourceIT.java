package com.lumi.web.rest;

import static com.lumi.domain.GradeAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Grade;
import com.lumi.repository.GradeRepository;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.mapper.GradeMapper;
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
 * Integration tests for the {@link GradeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GradeResourceIT {

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;
    private static final Integer SMALLER_LEVEL = 1 - 1;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/grades";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGradeMockMvc;

    private Grade grade;

    private Grade insertedGrade;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grade createEntity() {
        return new Grade().level(DEFAULT_LEVEL).name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grade createUpdatedEntity() {
        return new Grade().level(UPDATED_LEVEL).name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        grade = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGrade != null) {
            gradeRepository.delete(insertedGrade);
            insertedGrade = null;
        }
    }

    @Test
    @Transactional
    void createGrade() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);
        var returnedGradeDTO = om.readValue(
            restGradeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GradeDTO.class
        );

        // Validate the Grade in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGrade = gradeMapper.toEntity(returnedGradeDTO);
        assertGradeUpdatableFieldsEquals(returnedGrade, getPersistedGrade(returnedGrade));

        insertedGrade = returnedGrade;
    }

    @Test
    @Transactional
    void createGradeWithExistingId() throws Exception {
        // Create the Grade with an existing ID
        grade.setId(1L);
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        grade.setLevel(null);

        // Create the Grade, which fails.
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        restGradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        grade.setName(null);

        // Create the Grade, which fails.
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        restGradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGrades() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList
        restGradeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grade.getId().intValue())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getGrade() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get the grade
        restGradeMockMvc
            .perform(get(ENTITY_API_URL_ID, grade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(grade.getId().intValue()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getGradesByIdFiltering() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        Long id = grade.getId();

        defaultGradeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultGradeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultGradeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level equals to
        defaultGradeFiltering("level.equals=" + DEFAULT_LEVEL, "level.equals=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level in
        defaultGradeFiltering("level.in=" + DEFAULT_LEVEL + "," + UPDATED_LEVEL, "level.in=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level is not null
        defaultGradeFiltering("level.specified=true", "level.specified=false");
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level is greater than or equal to
        defaultGradeFiltering("level.greaterThanOrEqual=" + DEFAULT_LEVEL, "level.greaterThanOrEqual=" + (DEFAULT_LEVEL + 1));
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level is less than or equal to
        defaultGradeFiltering("level.lessThanOrEqual=" + DEFAULT_LEVEL, "level.lessThanOrEqual=" + SMALLER_LEVEL);
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level is less than
        defaultGradeFiltering("level.lessThan=" + (DEFAULT_LEVEL + 1), "level.lessThan=" + DEFAULT_LEVEL);
    }

    @Test
    @Transactional
    void getAllGradesByLevelIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where level is greater than
        defaultGradeFiltering("level.greaterThan=" + SMALLER_LEVEL, "level.greaterThan=" + DEFAULT_LEVEL);
    }

    @Test
    @Transactional
    void getAllGradesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where name equals to
        defaultGradeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGradesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where name in
        defaultGradeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGradesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where name is not null
        defaultGradeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllGradesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where name contains
        defaultGradeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGradesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        // Get all the gradeList where name does not contain
        defaultGradeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    private void defaultGradeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultGradeShouldBeFound(shouldBeFound);
        defaultGradeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGradeShouldBeFound(String filter) throws Exception {
        restGradeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grade.getId().intValue())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restGradeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGradeShouldNotBeFound(String filter) throws Exception {
        restGradeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGradeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGrade() throws Exception {
        // Get the grade
        restGradeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGrade() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grade
        Grade updatedGrade = gradeRepository.findById(grade.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGrade are not directly saved in db
        em.detach(updatedGrade);
        updatedGrade.level(UPDATED_LEVEL).name(UPDATED_NAME);
        GradeDTO gradeDTO = gradeMapper.toDto(updatedGrade);

        restGradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gradeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGradeToMatchAllProperties(updatedGrade);
    }

    @Test
    @Transactional
    void putNonExistingGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gradeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gradeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGradeWithPatch() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grade using partial update
        Grade partialUpdatedGrade = new Grade();
        partialUpdatedGrade.setId(grade.getId());

        restGradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrade.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGrade))
            )
            .andExpect(status().isOk());

        // Validate the Grade in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGradeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGrade, grade), getPersistedGrade(grade));
    }

    @Test
    @Transactional
    void fullUpdateGradeWithPatch() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grade using partial update
        Grade partialUpdatedGrade = new Grade();
        partialUpdatedGrade.setId(grade.getId());

        partialUpdatedGrade.level(UPDATED_LEVEL).name(UPDATED_NAME);

        restGradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrade.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGrade))
            )
            .andExpect(status().isOk());

        // Validate the Grade in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGradeUpdatableFieldsEquals(partialUpdatedGrade, getPersistedGrade(partialUpdatedGrade));
    }

    @Test
    @Transactional
    void patchNonExistingGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gradeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grade.setId(longCount.incrementAndGet());

        // Create the Grade
        GradeDTO gradeDTO = gradeMapper.toDto(grade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gradeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGrade() throws Exception {
        // Initialize the database
        insertedGrade = gradeRepository.saveAndFlush(grade);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the grade
        restGradeMockMvc
            .perform(delete(ENTITY_API_URL_ID, grade.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gradeRepository.count();
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

    protected Grade getPersistedGrade(Grade grade) {
        return gradeRepository.findById(grade.getId()).orElseThrow();
    }

    protected void assertPersistedGradeToMatchAllProperties(Grade expectedGrade) {
        assertGradeAllPropertiesEquals(expectedGrade, getPersistedGrade(expectedGrade));
    }

    protected void assertPersistedGradeToMatchUpdatableProperties(Grade expectedGrade) {
        assertGradeAllUpdatablePropertiesEquals(expectedGrade, getPersistedGrade(expectedGrade));
    }
}
