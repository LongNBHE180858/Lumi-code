package com.lumi.web.rest;

import static com.lumi.domain.KnowledgeEdgeAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.KnowledgeEdge;
import com.lumi.domain.KnowledgeNode;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.service.KnowledgeEdgeService;
import com.lumi.service.dto.KnowledgeEdgeDTO;
import com.lumi.service.mapper.KnowledgeEdgeMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link KnowledgeEdgeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KnowledgeEdgeResourceIT {

    private static final String DEFAULT_RELATION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RELATION_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/knowledge-edges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KnowledgeEdgeRepository knowledgeEdgeRepository;

    @Mock
    private KnowledgeEdgeRepository knowledgeEdgeRepositoryMock;

    @Autowired
    private KnowledgeEdgeMapper knowledgeEdgeMapper;

    @Mock
    private KnowledgeEdgeService knowledgeEdgeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeEdgeMockMvc;

    private KnowledgeEdge knowledgeEdge;

    private KnowledgeEdge insertedKnowledgeEdge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeEdge createEntity(EntityManager em) {
        KnowledgeEdge knowledgeEdge = new KnowledgeEdge().relationType(DEFAULT_RELATION_TYPE);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeResourceIT.createEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        knowledgeEdge.setFromNode(knowledgeNode);
        // Add required entity
        knowledgeEdge.setToNode(knowledgeNode);
        return knowledgeEdge;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeEdge createUpdatedEntity(EntityManager em) {
        KnowledgeEdge updatedKnowledgeEdge = new KnowledgeEdge().relationType(UPDATED_RELATION_TYPE);
        // Add required entity
        KnowledgeNode knowledgeNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeNode = KnowledgeNodeResourceIT.createUpdatedEntity();
            em.persist(knowledgeNode);
            em.flush();
        } else {
            knowledgeNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        updatedKnowledgeEdge.setFromNode(knowledgeNode);
        // Add required entity
        updatedKnowledgeEdge.setToNode(knowledgeNode);
        return updatedKnowledgeEdge;
    }

    @BeforeEach
    void initTest() {
        knowledgeEdge = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedKnowledgeEdge != null) {
            knowledgeEdgeRepository.delete(insertedKnowledgeEdge);
            insertedKnowledgeEdge = null;
        }
    }

    @Test
    @Transactional
    void createKnowledgeEdge() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);
        var returnedKnowledgeEdgeDTO = om.readValue(
            restKnowledgeEdgeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeEdgeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KnowledgeEdgeDTO.class
        );

        // Validate the KnowledgeEdge in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKnowledgeEdge = knowledgeEdgeMapper.toEntity(returnedKnowledgeEdgeDTO);
        assertKnowledgeEdgeUpdatableFieldsEquals(returnedKnowledgeEdge, getPersistedKnowledgeEdge(returnedKnowledgeEdge));

        insertedKnowledgeEdge = returnedKnowledgeEdge;
    }

    @Test
    @Transactional
    void createKnowledgeEdgeWithExistingId() throws Exception {
        // Create the KnowledgeEdge with an existing ID
        knowledgeEdge.setId(1L);
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKnowledgeEdgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeEdgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRelationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        knowledgeEdge.setRelationType(null);

        // Create the KnowledgeEdge, which fails.
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        restKnowledgeEdgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeEdgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllKnowledgeEdges() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeEdge.getId().intValue())))
            .andExpect(jsonPath("$.[*].relationType").value(hasItem(DEFAULT_RELATION_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeEdgesWithEagerRelationshipsIsEnabled() throws Exception {
        when(knowledgeEdgeServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restKnowledgeEdgeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(knowledgeEdgeServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeEdgesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(knowledgeEdgeServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restKnowledgeEdgeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(knowledgeEdgeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKnowledgeEdge() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get the knowledgeEdge
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL_ID, knowledgeEdge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(knowledgeEdge.getId().intValue()))
            .andExpect(jsonPath("$.relationType").value(DEFAULT_RELATION_TYPE));
    }

    @Test
    @Transactional
    void getKnowledgeEdgesByIdFiltering() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        Long id = knowledgeEdge.getId();

        defaultKnowledgeEdgeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKnowledgeEdgeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKnowledgeEdgeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByRelationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList where relationType equals to
        defaultKnowledgeEdgeFiltering("relationType.equals=" + DEFAULT_RELATION_TYPE, "relationType.equals=" + UPDATED_RELATION_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByRelationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList where relationType in
        defaultKnowledgeEdgeFiltering(
            "relationType.in=" + DEFAULT_RELATION_TYPE + "," + UPDATED_RELATION_TYPE,
            "relationType.in=" + UPDATED_RELATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByRelationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList where relationType is not null
        defaultKnowledgeEdgeFiltering("relationType.specified=true", "relationType.specified=false");
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByRelationTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList where relationType contains
        defaultKnowledgeEdgeFiltering("relationType.contains=" + DEFAULT_RELATION_TYPE, "relationType.contains=" + UPDATED_RELATION_TYPE);
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByRelationTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        // Get all the knowledgeEdgeList where relationType does not contain
        defaultKnowledgeEdgeFiltering(
            "relationType.doesNotContain=" + UPDATED_RELATION_TYPE,
            "relationType.doesNotContain=" + DEFAULT_RELATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByFromNodeIsEqualToSomething() throws Exception {
        KnowledgeNode fromNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);
            fromNode = KnowledgeNodeResourceIT.createEntity();
        } else {
            fromNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        em.persist(fromNode);
        em.flush();
        knowledgeEdge.setFromNode(fromNode);
        knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);
        Long fromNodeId = fromNode.getId();
        // Get all the knowledgeEdgeList where fromNode equals to fromNodeId
        defaultKnowledgeEdgeShouldBeFound("fromNodeId.equals=" + fromNodeId);

        // Get all the knowledgeEdgeList where fromNode equals to (fromNodeId + 1)
        defaultKnowledgeEdgeShouldNotBeFound("fromNodeId.equals=" + (fromNodeId + 1));
    }

    @Test
    @Transactional
    void getAllKnowledgeEdgesByToNodeIsEqualToSomething() throws Exception {
        KnowledgeNode toNode;
        if (TestUtil.findAll(em, KnowledgeNode.class).isEmpty()) {
            knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);
            toNode = KnowledgeNodeResourceIT.createEntity();
        } else {
            toNode = TestUtil.findAll(em, KnowledgeNode.class).getFirst();
        }
        em.persist(toNode);
        em.flush();
        knowledgeEdge.setToNode(toNode);
        knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);
        Long toNodeId = toNode.getId();
        // Get all the knowledgeEdgeList where toNode equals to toNodeId
        defaultKnowledgeEdgeShouldBeFound("toNodeId.equals=" + toNodeId);

        // Get all the knowledgeEdgeList where toNode equals to (toNodeId + 1)
        defaultKnowledgeEdgeShouldNotBeFound("toNodeId.equals=" + (toNodeId + 1));
    }

    private void defaultKnowledgeEdgeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKnowledgeEdgeShouldBeFound(shouldBeFound);
        defaultKnowledgeEdgeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKnowledgeEdgeShouldBeFound(String filter) throws Exception {
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeEdge.getId().intValue())))
            .andExpect(jsonPath("$.[*].relationType").value(hasItem(DEFAULT_RELATION_TYPE)));

        // Check, that the count call also returns 1
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKnowledgeEdgeShouldNotBeFound(String filter) throws Exception {
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKnowledgeEdgeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKnowledgeEdge() throws Exception {
        // Get the knowledgeEdge
        restKnowledgeEdgeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKnowledgeEdge() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeEdge
        KnowledgeEdge updatedKnowledgeEdge = knowledgeEdgeRepository.findById(knowledgeEdge.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKnowledgeEdge are not directly saved in db
        em.detach(updatedKnowledgeEdge);
        updatedKnowledgeEdge.relationType(UPDATED_RELATION_TYPE);
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(updatedKnowledgeEdge);

        restKnowledgeEdgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeEdgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeEdgeDTO))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKnowledgeEdgeToMatchAllProperties(updatedKnowledgeEdge);
    }

    @Test
    @Transactional
    void putNonExistingKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeEdgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeEdgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeEdgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeEdgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKnowledgeEdgeWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeEdge using partial update
        KnowledgeEdge partialUpdatedKnowledgeEdge = new KnowledgeEdge();
        partialUpdatedKnowledgeEdge.setId(knowledgeEdge.getId());

        restKnowledgeEdgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeEdge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeEdge))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeEdge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeEdgeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKnowledgeEdge, knowledgeEdge),
            getPersistedKnowledgeEdge(knowledgeEdge)
        );
    }

    @Test
    @Transactional
    void fullUpdateKnowledgeEdgeWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeEdge using partial update
        KnowledgeEdge partialUpdatedKnowledgeEdge = new KnowledgeEdge();
        partialUpdatedKnowledgeEdge.setId(knowledgeEdge.getId());

        partialUpdatedKnowledgeEdge.relationType(UPDATED_RELATION_TYPE);

        restKnowledgeEdgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeEdge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeEdge))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeEdge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeEdgeUpdatableFieldsEquals(partialUpdatedKnowledgeEdge, getPersistedKnowledgeEdge(partialUpdatedKnowledgeEdge));
    }

    @Test
    @Transactional
    void patchNonExistingKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, knowledgeEdgeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeEdgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeEdgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKnowledgeEdge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeEdge.setId(longCount.incrementAndGet());

        // Create the KnowledgeEdge
        KnowledgeEdgeDTO knowledgeEdgeDTO = knowledgeEdgeMapper.toDto(knowledgeEdge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeEdgeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(knowledgeEdgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeEdge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKnowledgeEdge() throws Exception {
        // Initialize the database
        insertedKnowledgeEdge = knowledgeEdgeRepository.saveAndFlush(knowledgeEdge);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the knowledgeEdge
        restKnowledgeEdgeMockMvc
            .perform(delete(ENTITY_API_URL_ID, knowledgeEdge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return knowledgeEdgeRepository.count();
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

    protected KnowledgeEdge getPersistedKnowledgeEdge(KnowledgeEdge knowledgeEdge) {
        return knowledgeEdgeRepository.findById(knowledgeEdge.getId()).orElseThrow();
    }

    protected void assertPersistedKnowledgeEdgeToMatchAllProperties(KnowledgeEdge expectedKnowledgeEdge) {
        assertKnowledgeEdgeAllPropertiesEquals(expectedKnowledgeEdge, getPersistedKnowledgeEdge(expectedKnowledgeEdge));
    }

    protected void assertPersistedKnowledgeEdgeToMatchUpdatableProperties(KnowledgeEdge expectedKnowledgeEdge) {
        assertKnowledgeEdgeAllUpdatablePropertiesEquals(expectedKnowledgeEdge, getPersistedKnowledgeEdge(expectedKnowledgeEdge));
    }
}
