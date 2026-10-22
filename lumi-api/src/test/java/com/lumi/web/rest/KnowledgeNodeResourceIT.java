package com.lumi.web.rest;

import static com.lumi.domain.KnowledgeNodeAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Subject;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.service.KnowledgeNodeService;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.mapper.KnowledgeNodeMapper;
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
 * Integration tests for the {@link KnowledgeNodeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KnowledgeNodeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/knowledge-nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KnowledgeNodeRepository knowledgeNodeRepository;

    @Mock
    private KnowledgeNodeRepository knowledgeNodeRepositoryMock;

    @Autowired
    private KnowledgeNodeMapper knowledgeNodeMapper;

    @Mock
    private KnowledgeNodeService knowledgeNodeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeNodeMockMvc;

    private KnowledgeNode knowledgeNode;

    private KnowledgeNode insertedKnowledgeNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeNode createEntity() {
        return new KnowledgeNode().title(DEFAULT_TITLE).type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeNode createUpdatedEntity() {
        return new KnowledgeNode().title(UPDATED_TITLE).type(UPDATED_TYPE);
    }

    @BeforeEach
    void initTest() {
        knowledgeNode = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedKnowledgeNode != null) {
            knowledgeNodeRepository.delete(insertedKnowledgeNode);
            insertedKnowledgeNode = null;
        }
    }

    @Test
    @Transactional
    void createKnowledgeNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);
        var returnedKnowledgeNodeDTO = om.readValue(
            restKnowledgeNodeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeNodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KnowledgeNodeDTO.class
        );

        // Validate the KnowledgeNode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKnowledgeNode = knowledgeNodeMapper.toEntity(returnedKnowledgeNodeDTO);
        assertKnowledgeNodeUpdatableFieldsEquals(returnedKnowledgeNode, getPersistedKnowledgeNode(returnedKnowledgeNode));

        insertedKnowledgeNode = returnedKnowledgeNode;
    }

    @Test
    @Transactional
    void createKnowledgeNodeWithExistingId() throws Exception {
        // Create the KnowledgeNode with an existing ID
        knowledgeNode.setId(1L);
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKnowledgeNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeNodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        knowledgeNode.setTitle(null);

        // Create the KnowledgeNode, which fails.
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        restKnowledgeNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodes() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeNode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeNodesWithEagerRelationshipsIsEnabled() throws Exception {
        when(knowledgeNodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeNodeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(knowledgeNodeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeNodesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(knowledgeNodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeNodeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(knowledgeNodeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKnowledgeNode() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get the knowledgeNode
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, knowledgeNode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(knowledgeNode.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getKnowledgeNodesByIdFiltering() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        Long id = knowledgeNode.getId();

        defaultKnowledgeNodeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKnowledgeNodeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKnowledgeNodeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where title equals to
        defaultKnowledgeNodeFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where title in
        defaultKnowledgeNodeFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where title is not null
        defaultKnowledgeNodeFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where title contains
        defaultKnowledgeNodeFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where title does not contain
        defaultKnowledgeNodeFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where type equals to
        defaultKnowledgeNodeFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where type in
        defaultKnowledgeNodeFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where type is not null
        defaultKnowledgeNodeFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where type contains
        defaultKnowledgeNodeFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        // Get all the knowledgeNodeList where type does not contain
        defaultKnowledgeNodeFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeNodesBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            knowledgeNodeRepository.saveAndFlush(knowledgeNode);
            subject = SubjectResourceIT.createEntity();
        } else {
            subject = TestUtil.findAll(em, Subject.class).getFirst();
        }
        em.persist(subject);
        em.flush();
        knowledgeNode.setSubject(subject);
        knowledgeNodeRepository.saveAndFlush(knowledgeNode);
        Long subjectId = subject.getId();
        // Get all the knowledgeNodeList where subject equals to subjectId
        defaultKnowledgeNodeShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the knowledgeNodeList where subject equals to (subjectId + 1)
        defaultKnowledgeNodeShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    private void defaultKnowledgeNodeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKnowledgeNodeShouldBeFound(shouldBeFound);
        defaultKnowledgeNodeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKnowledgeNodeShouldBeFound(String filter) throws Exception {
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeNode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKnowledgeNodeShouldNotBeFound(String filter) throws Exception {
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKnowledgeNodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKnowledgeNode() throws Exception {
        // Get the knowledgeNode
        restKnowledgeNodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKnowledgeNode() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeNode
        KnowledgeNode updatedKnowledgeNode = knowledgeNodeRepository.findById(knowledgeNode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKnowledgeNode are not directly saved in db
        em.detach(updatedKnowledgeNode);
        updatedKnowledgeNode.title(UPDATED_TITLE).type(UPDATED_TYPE);
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(updatedKnowledgeNode);

        restKnowledgeNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeNodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKnowledgeNodeToMatchAllProperties(updatedKnowledgeNode);
    }

    @Test
    @Transactional
    void putNonExistingKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKnowledgeNodeWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeNode using partial update
        KnowledgeNode partialUpdatedKnowledgeNode = new KnowledgeNode();
        partialUpdatedKnowledgeNode.setId(knowledgeNode.getId());

        partialUpdatedKnowledgeNode.title(UPDATED_TITLE).type(UPDATED_TYPE);

        restKnowledgeNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeNode))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeNodeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKnowledgeNode, knowledgeNode),
            getPersistedKnowledgeNode(knowledgeNode)
        );
    }

    @Test
    @Transactional
    void fullUpdateKnowledgeNodeWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeNode using partial update
        KnowledgeNode partialUpdatedKnowledgeNode = new KnowledgeNode();
        partialUpdatedKnowledgeNode.setId(knowledgeNode.getId());

        partialUpdatedKnowledgeNode.title(UPDATED_TITLE).type(UPDATED_TYPE);

        restKnowledgeNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeNode))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeNodeUpdatableFieldsEquals(partialUpdatedKnowledgeNode, getPersistedKnowledgeNode(partialUpdatedKnowledgeNode));
    }

    @Test
    @Transactional
    void patchNonExistingKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, knowledgeNodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKnowledgeNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeNode.setId(longCount.incrementAndGet());

        // Create the KnowledgeNode
        KnowledgeNodeDTO knowledgeNodeDTO = knowledgeNodeMapper.toDto(knowledgeNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeNodeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(knowledgeNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKnowledgeNode() throws Exception {
        // Initialize the database
        insertedKnowledgeNode = knowledgeNodeRepository.saveAndFlush(knowledgeNode);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the knowledgeNode
        restKnowledgeNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, knowledgeNode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return knowledgeNodeRepository.count();
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

    protected KnowledgeNode getPersistedKnowledgeNode(KnowledgeNode knowledgeNode) {
        return knowledgeNodeRepository.findById(knowledgeNode.getId()).orElseThrow();
    }

    protected void assertPersistedKnowledgeNodeToMatchAllProperties(KnowledgeNode expectedKnowledgeNode) {
        assertKnowledgeNodeAllPropertiesEquals(expectedKnowledgeNode, getPersistedKnowledgeNode(expectedKnowledgeNode));
    }

    protected void assertPersistedKnowledgeNodeToMatchUpdatableProperties(KnowledgeNode expectedKnowledgeNode) {
        assertKnowledgeNodeAllUpdatablePropertiesEquals(expectedKnowledgeNode, getPersistedKnowledgeNode(expectedKnowledgeNode));
    }
}
