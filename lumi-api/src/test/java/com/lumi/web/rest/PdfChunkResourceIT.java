package com.lumi.web.rest;

import static com.lumi.domain.PdfChunkAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.CourseMaterial;
import com.lumi.domain.PdfChunk;
import com.lumi.repository.PdfChunkRepository;
import com.lumi.service.dto.PdfChunkDTO;
import com.lumi.service.mapper.PdfChunkMapper;
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
 * Integration tests for the {@link PdfChunkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PdfChunkResourceIT {

    private static final Integer DEFAULT_PAGE_NUMBER = 1;
    private static final Integer UPDATED_PAGE_NUMBER = 2;
    private static final Integer SMALLER_PAGE_NUMBER = 1 - 1;

    private static final String DEFAULT_CHUNK_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_CHUNK_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_EMBEDDING = "AAAAAAAAAA";
    private static final String UPDATED_EMBEDDING = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pdf-chunks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PdfChunkRepository pdfChunkRepository;

    @Autowired
    private PdfChunkMapper pdfChunkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPdfChunkMockMvc;

    private PdfChunk pdfChunk;

    private PdfChunk insertedPdfChunk;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PdfChunk createEntity(EntityManager em) {
        PdfChunk pdfChunk = new PdfChunk().pageNumber(DEFAULT_PAGE_NUMBER).chunkText(DEFAULT_CHUNK_TEXT).embedding(DEFAULT_EMBEDDING);
        // Add required entity
        CourseMaterial courseMaterial;
        if (TestUtil.findAll(em, CourseMaterial.class).isEmpty()) {
            courseMaterial = CourseMaterialResourceIT.createEntity(em);
            em.persist(courseMaterial);
            em.flush();
        } else {
            courseMaterial = TestUtil.findAll(em, CourseMaterial.class).getFirst();
        }
        pdfChunk.setMaterial(courseMaterial);
        return pdfChunk;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PdfChunk createUpdatedEntity(EntityManager em) {
        PdfChunk updatedPdfChunk = new PdfChunk()
            .pageNumber(UPDATED_PAGE_NUMBER)
            .chunkText(UPDATED_CHUNK_TEXT)
            .embedding(UPDATED_EMBEDDING);
        // Add required entity
        CourseMaterial courseMaterial;
        if (TestUtil.findAll(em, CourseMaterial.class).isEmpty()) {
            courseMaterial = CourseMaterialResourceIT.createUpdatedEntity(em);
            em.persist(courseMaterial);
            em.flush();
        } else {
            courseMaterial = TestUtil.findAll(em, CourseMaterial.class).getFirst();
        }
        updatedPdfChunk.setMaterial(courseMaterial);
        return updatedPdfChunk;
    }

    @BeforeEach
    void initTest() {
        pdfChunk = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPdfChunk != null) {
            pdfChunkRepository.delete(insertedPdfChunk);
            insertedPdfChunk = null;
        }
    }

    @Test
    @Transactional
    void createPdfChunk() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);
        var returnedPdfChunkDTO = om.readValue(
            restPdfChunkMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pdfChunkDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PdfChunkDTO.class
        );

        // Validate the PdfChunk in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPdfChunk = pdfChunkMapper.toEntity(returnedPdfChunkDTO);
        assertPdfChunkUpdatableFieldsEquals(returnedPdfChunk, getPersistedPdfChunk(returnedPdfChunk));

        insertedPdfChunk = returnedPdfChunk;
    }

    @Test
    @Transactional
    void createPdfChunkWithExistingId() throws Exception {
        // Create the PdfChunk with an existing ID
        pdfChunk.setId(1L);
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPdfChunkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pdfChunkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkChunkTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pdfChunk.setChunkText(null);

        // Create the PdfChunk, which fails.
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        restPdfChunkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pdfChunkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPdfChunks() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pdfChunk.getId().intValue())))
            .andExpect(jsonPath("$.[*].pageNumber").value(hasItem(DEFAULT_PAGE_NUMBER)))
            .andExpect(jsonPath("$.[*].chunkText").value(hasItem(DEFAULT_CHUNK_TEXT)))
            .andExpect(jsonPath("$.[*].embedding").value(hasItem(DEFAULT_EMBEDDING)));
    }

    @Test
    @Transactional
    void getPdfChunk() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get the pdfChunk
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL_ID, pdfChunk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pdfChunk.getId().intValue()))
            .andExpect(jsonPath("$.pageNumber").value(DEFAULT_PAGE_NUMBER))
            .andExpect(jsonPath("$.chunkText").value(DEFAULT_CHUNK_TEXT))
            .andExpect(jsonPath("$.embedding").value(DEFAULT_EMBEDDING));
    }

    @Test
    @Transactional
    void getPdfChunksByIdFiltering() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        Long id = pdfChunk.getId();

        defaultPdfChunkFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPdfChunkFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPdfChunkFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber equals to
        defaultPdfChunkFiltering("pageNumber.equals=" + DEFAULT_PAGE_NUMBER, "pageNumber.equals=" + UPDATED_PAGE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber in
        defaultPdfChunkFiltering(
            "pageNumber.in=" + DEFAULT_PAGE_NUMBER + "," + UPDATED_PAGE_NUMBER,
            "pageNumber.in=" + UPDATED_PAGE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber is not null
        defaultPdfChunkFiltering("pageNumber.specified=true", "pageNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber is greater than or equal to
        defaultPdfChunkFiltering(
            "pageNumber.greaterThanOrEqual=" + DEFAULT_PAGE_NUMBER,
            "pageNumber.greaterThanOrEqual=" + UPDATED_PAGE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber is less than or equal to
        defaultPdfChunkFiltering("pageNumber.lessThanOrEqual=" + DEFAULT_PAGE_NUMBER, "pageNumber.lessThanOrEqual=" + SMALLER_PAGE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber is less than
        defaultPdfChunkFiltering("pageNumber.lessThan=" + UPDATED_PAGE_NUMBER, "pageNumber.lessThan=" + DEFAULT_PAGE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPdfChunksByPageNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where pageNumber is greater than
        defaultPdfChunkFiltering("pageNumber.greaterThan=" + SMALLER_PAGE_NUMBER, "pageNumber.greaterThan=" + DEFAULT_PAGE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkText equals to
        defaultPdfChunkFiltering("chunkText.equals=" + DEFAULT_CHUNK_TEXT, "chunkText.equals=" + UPDATED_CHUNK_TEXT);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkText in
        defaultPdfChunkFiltering("chunkText.in=" + DEFAULT_CHUNK_TEXT + "," + UPDATED_CHUNK_TEXT, "chunkText.in=" + UPDATED_CHUNK_TEXT);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkText is not null
        defaultPdfChunkFiltering("chunkText.specified=true", "chunkText.specified=false");
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkTextContainsSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkText contains
        defaultPdfChunkFiltering("chunkText.contains=" + DEFAULT_CHUNK_TEXT, "chunkText.contains=" + UPDATED_CHUNK_TEXT);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkText does not contain
        defaultPdfChunkFiltering("chunkText.doesNotContain=" + UPDATED_CHUNK_TEXT, "chunkText.doesNotContain=" + DEFAULT_CHUNK_TEXT);
    }

    @Test
    @Transactional
    void getAllPdfChunksByEmbeddingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where embedding equals to
        defaultPdfChunkFiltering("embedding.equals=" + DEFAULT_EMBEDDING, "embedding.equals=" + UPDATED_EMBEDDING);
    }

    @Test
    @Transactional
    void getAllPdfChunksByEmbeddingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where embedding in
        defaultPdfChunkFiltering("embedding.in=" + DEFAULT_EMBEDDING + "," + UPDATED_EMBEDDING, "embedding.in=" + UPDATED_EMBEDDING);
    }

    @Test
    @Transactional
    void getAllPdfChunksByEmbeddingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where embedding is not null
        defaultPdfChunkFiltering("embedding.specified=true", "embedding.specified=false");
    }

    @Test
    @Transactional
    void getAllPdfChunksByEmbeddingContainsSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where embedding contains
        defaultPdfChunkFiltering("embedding.contains=" + DEFAULT_EMBEDDING, "embedding.contains=" + UPDATED_EMBEDDING);
    }

    @Test
    @Transactional
    void getAllPdfChunksByEmbeddingNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where embedding does not contain
        defaultPdfChunkFiltering("embedding.doesNotContain=" + UPDATED_EMBEDDING, "embedding.doesNotContain=" + DEFAULT_EMBEDDING);
    }

    @Test
    @Transactional
    void getAllPdfChunksByMaterialIsEqualToSomething() throws Exception {
        CourseMaterial material;
        if (TestUtil.findAll(em, CourseMaterial.class).isEmpty()) {
            pdfChunkRepository.saveAndFlush(pdfChunk);
            material = CourseMaterialResourceIT.createEntity(em);
        } else {
            material = TestUtil.findAll(em, CourseMaterial.class).getFirst();
        }
        em.persist(material);
        em.flush();
        pdfChunk.setMaterial(material);
        pdfChunkRepository.saveAndFlush(pdfChunk);
        Long materialId = material.getId();
        // Get all the pdfChunkList where material equals to materialId
        defaultPdfChunkShouldBeFound("materialId.equals=" + materialId);

        // Get all the pdfChunkList where material equals to (materialId + 1)
        defaultPdfChunkShouldNotBeFound("materialId.equals=" + (materialId + 1));
    }

    private void defaultPdfChunkFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPdfChunkShouldBeFound(shouldBeFound);
        defaultPdfChunkShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPdfChunkShouldBeFound(String filter) throws Exception {
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pdfChunk.getId().intValue())))
            .andExpect(jsonPath("$.[*].pageNumber").value(hasItem(DEFAULT_PAGE_NUMBER)))
            .andExpect(jsonPath("$.[*].chunkText").value(hasItem(DEFAULT_CHUNK_TEXT)))
            .andExpect(jsonPath("$.[*].embedding").value(hasItem(DEFAULT_EMBEDDING)));

        // Check, that the count call also returns 1
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPdfChunkShouldNotBeFound(String filter) throws Exception {
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPdfChunkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPdfChunk() throws Exception {
        // Get the pdfChunk
        restPdfChunkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPdfChunk() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pdfChunk
        PdfChunk updatedPdfChunk = pdfChunkRepository.findById(pdfChunk.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPdfChunk are not directly saved in db
        em.detach(updatedPdfChunk);
        updatedPdfChunk.pageNumber(UPDATED_PAGE_NUMBER).chunkText(UPDATED_CHUNK_TEXT).embedding(UPDATED_EMBEDDING);
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(updatedPdfChunk);

        restPdfChunkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pdfChunkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pdfChunkDTO))
            )
            .andExpect(status().isOk());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPdfChunkToMatchAllProperties(updatedPdfChunk);
    }

    @Test
    @Transactional
    void putNonExistingPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pdfChunkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pdfChunkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pdfChunkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pdfChunkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePdfChunkWithPatch() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pdfChunk using partial update
        PdfChunk partialUpdatedPdfChunk = new PdfChunk();
        partialUpdatedPdfChunk.setId(pdfChunk.getId());

        partialUpdatedPdfChunk.chunkText(UPDATED_CHUNK_TEXT);

        restPdfChunkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPdfChunk.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPdfChunk))
            )
            .andExpect(status().isOk());

        // Validate the PdfChunk in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPdfChunkUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPdfChunk, pdfChunk), getPersistedPdfChunk(pdfChunk));
    }

    @Test
    @Transactional
    void fullUpdatePdfChunkWithPatch() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pdfChunk using partial update
        PdfChunk partialUpdatedPdfChunk = new PdfChunk();
        partialUpdatedPdfChunk.setId(pdfChunk.getId());

        partialUpdatedPdfChunk.pageNumber(UPDATED_PAGE_NUMBER).chunkText(UPDATED_CHUNK_TEXT).embedding(UPDATED_EMBEDDING);

        restPdfChunkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPdfChunk.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPdfChunk))
            )
            .andExpect(status().isOk());

        // Validate the PdfChunk in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPdfChunkUpdatableFieldsEquals(partialUpdatedPdfChunk, getPersistedPdfChunk(partialUpdatedPdfChunk));
    }

    @Test
    @Transactional
    void patchNonExistingPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pdfChunkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pdfChunkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pdfChunkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPdfChunk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pdfChunk.setId(longCount.incrementAndGet());

        // Create the PdfChunk
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPdfChunkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pdfChunkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PdfChunk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePdfChunk() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pdfChunk
        restPdfChunkMockMvc
            .perform(delete(ENTITY_API_URL_ID, pdfChunk.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pdfChunkRepository.count();
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

    protected PdfChunk getPersistedPdfChunk(PdfChunk pdfChunk) {
        return pdfChunkRepository.findById(pdfChunk.getId()).orElseThrow();
    }

    protected void assertPersistedPdfChunkToMatchAllProperties(PdfChunk expectedPdfChunk) {
        assertPdfChunkAllPropertiesEquals(expectedPdfChunk, getPersistedPdfChunk(expectedPdfChunk));
    }

    protected void assertPersistedPdfChunkToMatchUpdatableProperties(PdfChunk expectedPdfChunk) {
        assertPdfChunkAllUpdatablePropertiesEquals(expectedPdfChunk, getPersistedPdfChunk(expectedPdfChunk));
    }
}
