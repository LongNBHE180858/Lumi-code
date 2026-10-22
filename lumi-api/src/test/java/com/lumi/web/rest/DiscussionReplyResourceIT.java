package com.lumi.web.rest;

import static com.lumi.domain.DiscussionReplyAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Discussion;
import com.lumi.domain.DiscussionReply;
import com.lumi.domain.DiscussionReply;
import com.lumi.domain.User;
import com.lumi.repository.DiscussionReplyRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.DiscussionReplyService;
import com.lumi.service.dto.DiscussionReplyDTO;
import com.lumi.service.mapper.DiscussionReplyMapper;
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
 * Integration tests for the {@link DiscussionReplyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DiscussionReplyResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/discussion-replies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiscussionReplyRepository discussionReplyRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DiscussionReplyRepository discussionReplyRepositoryMock;

    @Autowired
    private DiscussionReplyMapper discussionReplyMapper;

    @Mock
    private DiscussionReplyService discussionReplyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiscussionReplyMockMvc;

    private DiscussionReply discussionReply;

    private DiscussionReply insertedDiscussionReply;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiscussionReply createEntity(EntityManager em) {
        DiscussionReply discussionReply = new DiscussionReply().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        discussionReply.setUser(user);
        // Add required entity
        Discussion discussion;
        if (TestUtil.findAll(em, Discussion.class).isEmpty()) {
            discussion = DiscussionResourceIT.createEntity(em);
            em.persist(discussion);
            em.flush();
        } else {
            discussion = TestUtil.findAll(em, Discussion.class).getFirst();
        }
        discussionReply.setDiscussion(discussion);
        return discussionReply;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiscussionReply createUpdatedEntity(EntityManager em) {
        DiscussionReply updatedDiscussionReply = new DiscussionReply().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDiscussionReply.setUser(user);
        // Add required entity
        Discussion discussion;
        if (TestUtil.findAll(em, Discussion.class).isEmpty()) {
            discussion = DiscussionResourceIT.createUpdatedEntity(em);
            em.persist(discussion);
            em.flush();
        } else {
            discussion = TestUtil.findAll(em, Discussion.class).getFirst();
        }
        updatedDiscussionReply.setDiscussion(discussion);
        return updatedDiscussionReply;
    }

    @BeforeEach
    void initTest() {
        discussionReply = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDiscussionReply != null) {
            discussionReplyRepository.delete(insertedDiscussionReply);
            insertedDiscussionReply = null;
        }
    }

    @Test
    @Transactional
    void createDiscussionReply() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);
        var returnedDiscussionReplyDTO = om.readValue(
            restDiscussionReplyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionReplyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DiscussionReplyDTO.class
        );

        // Validate the DiscussionReply in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDiscussionReply = discussionReplyMapper.toEntity(returnedDiscussionReplyDTO);
        assertDiscussionReplyUpdatableFieldsEquals(returnedDiscussionReply, getPersistedDiscussionReply(returnedDiscussionReply));

        insertedDiscussionReply = returnedDiscussionReply;
    }

    @Test
    @Transactional
    void createDiscussionReplyWithExistingId() throws Exception {
        // Create the DiscussionReply with an existing ID
        discussionReply.setId(1L);
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiscussionReplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionReplyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discussionReply.setContent(null);

        // Create the DiscussionReply, which fails.
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        restDiscussionReplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionReplyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDiscussionReplies() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discussionReply.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiscussionRepliesWithEagerRelationshipsIsEnabled() throws Exception {
        when(discussionReplyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiscussionReplyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(discussionReplyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiscussionRepliesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(discussionReplyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiscussionReplyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(discussionReplyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDiscussionReply() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get the discussionReply
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL_ID, discussionReply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(discussionReply.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getDiscussionRepliesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        Long id = discussionReply.getId();

        defaultDiscussionReplyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDiscussionReplyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDiscussionReplyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where content equals to
        defaultDiscussionReplyFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where content in
        defaultDiscussionReplyFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where content is not null
        defaultDiscussionReplyFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where content contains
        defaultDiscussionReplyFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where content does not contain
        defaultDiscussionReplyFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where createdAt equals to
        defaultDiscussionReplyFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where createdAt in
        defaultDiscussionReplyFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        // Get all the discussionReplyList where createdAt is not null
        defaultDiscussionReplyFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            discussionReplyRepository.saveAndFlush(discussionReply);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(user);
        em.flush();
        discussionReply.setUser(user);
        discussionReplyRepository.saveAndFlush(discussionReply);
        Long userId = user.getId();
        // Get all the discussionReplyList where user equals to userId
        defaultDiscussionReplyShouldBeFound("userId.equals=" + userId);

        // Get all the discussionReplyList where user equals to (userId + 1)
        defaultDiscussionReplyShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByDiscussionIsEqualToSomething() throws Exception {
        Discussion discussion;
        if (TestUtil.findAll(em, Discussion.class).isEmpty()) {
            discussionReplyRepository.saveAndFlush(discussionReply);
            discussion = DiscussionResourceIT.createEntity(em);
        } else {
            discussion = TestUtil.findAll(em, Discussion.class).getFirst();
        }
        em.persist(discussion);
        em.flush();
        discussionReply.setDiscussion(discussion);
        discussionReplyRepository.saveAndFlush(discussionReply);
        Long discussionId = discussion.getId();
        // Get all the discussionReplyList where discussion equals to discussionId
        defaultDiscussionReplyShouldBeFound("discussionId.equals=" + discussionId);

        // Get all the discussionReplyList where discussion equals to (discussionId + 1)
        defaultDiscussionReplyShouldNotBeFound("discussionId.equals=" + (discussionId + 1));
    }

    @Test
    @Transactional
    void getAllDiscussionRepliesByParentIsEqualToSomething() throws Exception {
        DiscussionReply parent;
        if (TestUtil.findAll(em, DiscussionReply.class).isEmpty()) {
            discussionReplyRepository.saveAndFlush(discussionReply);
            parent = DiscussionReplyResourceIT.createEntity(em);
        } else {
            parent = TestUtil.findAll(em, DiscussionReply.class).getFirst();
        }
        em.persist(parent);
        em.flush();
        discussionReply.setParent(parent);
        discussionReplyRepository.saveAndFlush(discussionReply);
        Long parentId = parent.getId();
        // Get all the discussionReplyList where parent equals to parentId
        defaultDiscussionReplyShouldBeFound("parentId.equals=" + parentId);

        // Get all the discussionReplyList where parent equals to (parentId + 1)
        defaultDiscussionReplyShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    private void defaultDiscussionReplyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDiscussionReplyShouldBeFound(shouldBeFound);
        defaultDiscussionReplyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDiscussionReplyShouldBeFound(String filter) throws Exception {
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discussionReply.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDiscussionReplyShouldNotBeFound(String filter) throws Exception {
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDiscussionReplyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDiscussionReply() throws Exception {
        // Get the discussionReply
        restDiscussionReplyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiscussionReply() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussionReply
        DiscussionReply updatedDiscussionReply = discussionReplyRepository.findById(discussionReply.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiscussionReply are not directly saved in db
        em.detach(updatedDiscussionReply);
        updatedDiscussionReply.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(updatedDiscussionReply);

        restDiscussionReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discussionReplyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionReplyDTO))
            )
            .andExpect(status().isOk());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiscussionReplyToMatchAllProperties(updatedDiscussionReply);
    }

    @Test
    @Transactional
    void putNonExistingDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discussionReplyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionReplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discussionReplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discussionReplyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDiscussionReplyWithPatch() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussionReply using partial update
        DiscussionReply partialUpdatedDiscussionReply = new DiscussionReply();
        partialUpdatedDiscussionReply.setId(discussionReply.getId());

        partialUpdatedDiscussionReply.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restDiscussionReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscussionReply.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscussionReply))
            )
            .andExpect(status().isOk());

        // Validate the DiscussionReply in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscussionReplyUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDiscussionReply, discussionReply),
            getPersistedDiscussionReply(discussionReply)
        );
    }

    @Test
    @Transactional
    void fullUpdateDiscussionReplyWithPatch() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discussionReply using partial update
        DiscussionReply partialUpdatedDiscussionReply = new DiscussionReply();
        partialUpdatedDiscussionReply.setId(discussionReply.getId());

        partialUpdatedDiscussionReply.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restDiscussionReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscussionReply.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscussionReply))
            )
            .andExpect(status().isOk());

        // Validate the DiscussionReply in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscussionReplyUpdatableFieldsEquals(
            partialUpdatedDiscussionReply,
            getPersistedDiscussionReply(partialUpdatedDiscussionReply)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, discussionReplyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discussionReplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discussionReplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiscussionReply() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discussionReply.setId(longCount.incrementAndGet());

        // Create the DiscussionReply
        DiscussionReplyDTO discussionReplyDTO = discussionReplyMapper.toDto(discussionReply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscussionReplyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(discussionReplyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiscussionReply in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiscussionReply() throws Exception {
        // Initialize the database
        insertedDiscussionReply = discussionReplyRepository.saveAndFlush(discussionReply);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the discussionReply
        restDiscussionReplyMockMvc
            .perform(delete(ENTITY_API_URL_ID, discussionReply.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return discussionReplyRepository.count();
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

    protected DiscussionReply getPersistedDiscussionReply(DiscussionReply discussionReply) {
        return discussionReplyRepository.findById(discussionReply.getId()).orElseThrow();
    }

    protected void assertPersistedDiscussionReplyToMatchAllProperties(DiscussionReply expectedDiscussionReply) {
        assertDiscussionReplyAllPropertiesEquals(expectedDiscussionReply, getPersistedDiscussionReply(expectedDiscussionReply));
    }

    protected void assertPersistedDiscussionReplyToMatchUpdatableProperties(DiscussionReply expectedDiscussionReply) {
        assertDiscussionReplyAllUpdatablePropertiesEquals(expectedDiscussionReply, getPersistedDiscussionReply(expectedDiscussionReply));
    }
}
