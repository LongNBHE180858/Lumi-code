package com.lumi.web.rest;

import static com.lumi.domain.ChapterAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Chapter;
import com.lumi.domain.Course;
import com.lumi.repository.ChapterRepository;
import com.lumi.service.ChapterService;
import com.lumi.service.dto.ChapterDTO;
import com.lumi.service.mapper.ChapterMapper;
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
 * Integration tests for the {@link ChapterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class ChapterResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER_INDEX = 0;
    private static final Integer UPDATED_ORDER_INDEX = 1;
    private static final Integer SMALLER_ORDER_INDEX = 0 - 1;

    private static final String ENTITY_API_URL = "/api/chapters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterRepository chapterRepository;

    @Mock
    private ChapterRepository chapterRepositoryMock;

    @Autowired
    private ChapterMapper chapterMapper;

    @Mock
    private ChapterService chapterServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterMockMvc;

    private Chapter chapter;

    private Chapter insertedChapter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createEntity(EntityManager em) {
        Chapter chapter = new Chapter().title(DEFAULT_TITLE).orderIndex(DEFAULT_ORDER_INDEX);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        chapter.setCourse(course);
        return chapter;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createUpdatedEntity(EntityManager em) {
        Chapter updatedChapter = new Chapter().title(UPDATED_TITLE).orderIndex(UPDATED_ORDER_INDEX);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        updatedChapter.setCourse(course);
        return updatedChapter;
    }

    @BeforeEach
    void initTest() {
        chapter = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedChapter != null) {
            chapterRepository.deleteById(insertedChapter.getId());
            insertedChapter = null;
        }
    }

    @Test
    @Transactional
    void createChapter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);
        var returnedChapterDTO = om.readValue(
            restChapterMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterDTO.class
        );

        // Validate the Chapter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapter = chapterMapper.toEntity(returnedChapterDTO);
        assertChapterUpdatableFieldsEquals(returnedChapter, getPersistedChapter(returnedChapter));

        insertedChapter = returnedChapter;
    }

    @Test
    @Transactional
    void createChapterWithExistingId() throws Exception {
        // Create the Chapter with an existing ID
        chapter.setId(1L);
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setTitle(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setOrderIndex(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapters() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapter.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chapterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chapterRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get the chapter
        restChapterMockMvc
            .perform(get(ENTITY_API_URL_ID, chapter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapter.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX));
    }

    @Test
    @Transactional
    void getChaptersByIdFiltering() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        Long id = chapter.getId();

        defaultChapterFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultChapterFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultChapterFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChaptersByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where title equals to
        defaultChapterFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where title in
        defaultChapterFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where title is not null
        defaultChapterFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where title contains
        defaultChapterFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where title does not contain
        defaultChapterFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex equals to
        defaultChapterFiltering("orderIndex.equals=" + DEFAULT_ORDER_INDEX, "orderIndex.equals=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex in
        defaultChapterFiltering("orderIndex.in=" + DEFAULT_ORDER_INDEX + "," + UPDATED_ORDER_INDEX, "orderIndex.in=" + UPDATED_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex is not null
        defaultChapterFiltering("orderIndex.specified=true", "orderIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex is greater than or equal to
        defaultChapterFiltering(
            "orderIndex.greaterThanOrEqual=" + DEFAULT_ORDER_INDEX,
            "orderIndex.greaterThanOrEqual=" + UPDATED_ORDER_INDEX
        );
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex is less than or equal to
        defaultChapterFiltering("orderIndex.lessThanOrEqual=" + DEFAULT_ORDER_INDEX, "orderIndex.lessThanOrEqual=" + SMALLER_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex is less than
        defaultChapterFiltering("orderIndex.lessThan=" + UPDATED_ORDER_INDEX, "orderIndex.lessThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllChaptersByOrderIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where orderIndex is greater than
        defaultChapterFiltering("orderIndex.greaterThan=" + SMALLER_ORDER_INDEX, "orderIndex.greaterThan=" + DEFAULT_ORDER_INDEX);
    }

    @Test
    @Transactional
    void getAllChaptersByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            chapterRepository.saveAndFlush(chapter);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        chapter.setCourse(course);
        chapterRepository.saveAndFlush(chapter);
        Long courseId = course.getId();
        // Get all the chapterList where course equals to courseId
        defaultChapterShouldBeFound("courseId.equals=" + courseId);

        // Get all the chapterList where course equals to (courseId + 1)
        defaultChapterShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    private void defaultChapterFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultChapterShouldBeFound(shouldBeFound);
        defaultChapterShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChapterShouldBeFound(String filter) throws Exception {
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapter.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)));

        // Check, that the count call also returns 1
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChapterShouldNotBeFound(String filter) throws Exception {
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingChapter() throws Exception {
        // Get the chapter
        restChapterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter
        Chapter updatedChapter = chapterRepository.findById(chapter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapter are not directly saved in db
        em.detach(updatedChapter);
        updatedChapter.title(UPDATED_TITLE).orderIndex(UPDATED_ORDER_INDEX);
        ChapterDTO chapterDTO = chapterMapper.toDto(updatedChapter);

        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterToMatchAllProperties(updatedChapter);
    }

    @Test
    @Transactional
    void putNonExistingChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter.orderIndex(UPDATED_ORDER_INDEX);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChapter, chapter), getPersistedChapter(chapter));
    }

    @Test
    @Transactional
    void fullUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter.title(UPDATED_TITLE).orderIndex(UPDATED_ORDER_INDEX);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterUpdatableFieldsEquals(partialUpdatedChapter, getPersistedChapter(partialUpdatedChapter));
    }

    @Test
    @Transactional
    void patchNonExistingChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapter
        restChapterMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterRepository.count();
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

    protected Chapter getPersistedChapter(Chapter chapter) {
        return chapterRepository.findById(chapter.getId()).orElseThrow();
    }

    protected void assertPersistedChapterToMatchAllProperties(Chapter expectedChapter) {
        assertChapterAllPropertiesEquals(expectedChapter, getPersistedChapter(expectedChapter));
    }

    protected void assertPersistedChapterToMatchUpdatableProperties(Chapter expectedChapter) {
        assertChapterAllUpdatablePropertiesEquals(expectedChapter, getPersistedChapter(expectedChapter));
    }
}
