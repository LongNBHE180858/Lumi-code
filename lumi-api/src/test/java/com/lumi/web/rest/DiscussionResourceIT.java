package com.lumi.web.rest;

import static com.lumi.domain.DiscussionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Discussion;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.repository.DiscussionRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.DiscussionService;
import com.lumi.service.dto.DiscussionDTO;
import com.lumi.service.mapper.DiscussionMapper;
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
 * Integration tests for the {@link DiscussionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DiscussionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/discussions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DiscussionRepository discussionRepositoryMock;

    @Autowired
    private DiscussionMapper discussionMapper;

    @Mock
    private DiscussionService discussionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiscussionMockMvc;

    private Discussion discussion;

    private Discussion insertedDiscussion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discussion createEntity(EntityManager em) {
        Discussion discussion = new Discussion().title(DEFAULT_TITLE).content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        discussion.setStudent(user);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        discussion.setLesson(lesson);
        return discussion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discussion createUpdatedEntity(EntityManager em) {
        Discussion updatedDiscussion = new Discussion().title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDiscussion.setStudent(user);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        updatedDiscussion.setLesson(lesson);
        return updatedDiscussion;
    }

    @BeforeEach
    void initTest() {
        discussion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDiscussion != null) {
            discussionRepository.delete(insertedDiscussion);
            insertedDiscussion = null;
        }
    }

    @Test
    @Transactional
    void createDiscussion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);
        var returnedDiscussionDTO = om.readValue(
            restDiscussionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DiscussionDTO.class
        );

        // Validate the Discussion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDiscussion = discussionMapper.toEntity(returnedDiscussionDTO);
        assertDiscussionUpdatableFieldsEquals(returnedDiscussion, getPersistedDiscussion(returnedDiscussion));

        insertedDiscussion = returnedDiscussion;
    }

    @Test
    @Transactional
    void createDiscussionWithExistingId() throws Exception {
        // Create the Discussion with an existing ID
        discussion.setId(1L);
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiscussionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discussion.setTitle(null);

        // Create the Discussion, which fails.
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        restDiscussionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discussion.setContent(null);

        // Create the Discussion, which fails.
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        restDiscussionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDiscussions() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discussion.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiscussionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(discussionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiscussionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(discussionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiscussionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(discussionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiscussionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(discussionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDiscussion() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get the discussion
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL_ID, discussion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(discussion.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getDiscussionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        Long id = discussion.getId();

        defaultDiscussionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDiscussionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDiscussionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDiscussionsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where title equals to
        defaultDiscussionFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllDiscussionsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where title in
        defaultDiscussionFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllDiscussionsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where title is not null
        defaultDiscussionFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllDiscussionsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where title contains
        defaultDiscussionFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllDiscussionsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where title does not contain
        defaultDiscussionFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllDiscussionsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where content equals to
        defaultDiscussionFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where content in
        defaultDiscussionFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where content is not null
        defaultDiscussionFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllDiscussionsByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where content contains
        defaultDiscussionFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where content does not contain
        defaultDiscussionFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where createdAt equals to
        defaultDiscussionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where createdAt in
        defaultDiscussionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDiscussionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        // Get all the discussionList where createdAt is not null
        defaultDiscussionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDiscussionsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            discussionRepository.saveAndFlush(discussion);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        discussion.setStudent(student);
        discussionRepository.saveAndFlush(discussion);
        Long studentId = student.getId();
        // Get all the discussionList where student equals to studentId
        defaultDiscussionShouldBeFound("studentId.equals=" + studentId);

        // Get all the discussionList where student equals to (studentId + 1)
        defaultDiscussionShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllDiscussionsByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            discussionRepository.saveAndFlush(discussion);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).getFirst();
        }
        em.persist(lesson);
        em.flush();
        discussion.setLesson(lesson);
        discussionRepository.saveAndFlush(discussion);
        Long lessonId = lesson.getId();
        // Get all the discussionList where lesson equals to lessonId
        defaultDiscussionShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the discussionList where lesson equals to (lessonId + 1)
        defaultDiscussionShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultDiscussionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDiscussionShouldBeFound(shouldBeFound);
        defaultDiscussionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDiscussionShouldBeFound(String filter) throws Exception {
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discussion.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDiscussionShouldNotBeFound(String filter) throws Exception {
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDiscussionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDiscussion() throws Exception {
        // Get the discussion
        restDiscussionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiscussion() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussion
        Discussion updatedDiscussion = discussionRepository.findById(discussion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiscussion are not directly saved in db
        em.detach(updatedDiscussion);
        updatedDiscussion.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        DiscussionDTO discussionDTO = discussionMapper.toDto(updatedDiscussion);

        restDiscussionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discussionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiscussionToMatchAllProperties(updatedDiscussion);
    }

    @Test
    @Transactional
    void putNonExistingDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discussionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDiscussionWithPatch() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussion using partial update
        Discussion partialUpdatedDiscussion = new Discussion();
        partialUpdatedDiscussion.setId(discussion.getId());

        partialUpdatedDiscussion.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restDiscussionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscussion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscussion))
            )
            .andExpect(status().isOk());

        // Validate the Discussion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscussionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDiscussion, discussion),
            getPersistedDiscussion(discussion)
        );
    }

    @Test
    @Transactional
    void fullUpdateDiscussionWithPatch() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussion using partial update
        Discussion partialUpdatedDiscussion = new Discussion();
        partialUpdatedDiscussion.setId(discussion.getId());

        partialUpdatedDiscussion.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restDiscussionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscussion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscussion))
            )
            .andExpect(status().isOk());

        // Validate the Discussion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscussionUpdatableFieldsEquals(partialUpdatedDiscussion, getPersistedDiscussion(partialUpdatedDiscussion));
    }

    @Test
    @Transactional
    void patchNonExistingDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, discussionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discussionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discussionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiscussion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussion.setId(longCount.incrementAndGet());

        // Create the Discussion
        DiscussionDTO discussionDTO = discussionMapper.toDto(discussion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(discussionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discussion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiscussion() throws Exception {
        // Initialize the database
        insertedDiscussion = discussionRepository.saveAndFlush(discussion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the discussion
        restDiscussionMockMvc
            .perform(delete(ENTITY_API_URL_ID, discussion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return discussionRepository.count();
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

    protected Discussion getPersistedDiscussion(Discussion discussion) {
        return discussionRepository.findById(discussion.getId()).orElseThrow();
    }

    protected void assertPersistedDiscussionToMatchAllProperties(Discussion expectedDiscussion) {
        assertDiscussionAllPropertiesEquals(expectedDiscussion, getPersistedDiscussion(expectedDiscussion));
    }

    protected void assertPersistedDiscussionToMatchUpdatableProperties(Discussion expectedDiscussion) {
        assertDiscussionAllUpdatablePropertiesEquals(expectedDiscussion, getPersistedDiscussion(expectedDiscussion));
    }
}
