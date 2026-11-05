package com.lumi.web.rest;

import static com.lumi.domain.PdfChunkAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.LearningDocument;
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

    private static final Integer DEFAULT_CHUNK_INDEX = 0;
    private static final Integer UPDATED_CHUNK_INDEX = 1;
    private static final Integer SMALLER_CHUNK_INDEX = 0 - 1;

    private static final Integer DEFAULT_INDEX_VERSION = 1;
    private static final Integer UPDATED_INDEX_VERSION = 2;
    private static final Integer SMALLER_INDEX_VERSION = 1 - 1;

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
        PdfChunk pdfChunk = new PdfChunk()
            .pageNumber(DEFAULT_PAGE_NUMBER)
            .chunkText(DEFAULT_CHUNK_TEXT)
            .embedding(DEFAULT_EMBEDDING)
            .chunkIndex(DEFAULT_CHUNK_INDEX)
            .indexVersion(DEFAULT_INDEX_VERSION);
        // Add required entity
        LearningDocument learningDocument;
        if (TestUtil.findAll(em, LearningDocument.class).isEmpty()) {
            learningDocument = LearningDocumentResourceIT.createEntity(em);
            em.persist(learningDocument);
            em.flush();
        } else {
            learningDocument = TestUtil.findAll(em, LearningDocument.class).getFirst();
        }
        pdfChunk.setMaterial(learningDocument);
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
            .embedding(UPDATED_EMBEDDING)
            .chunkIndex(UPDATED_CHUNK_INDEX)
            .indexVersion(UPDATED_INDEX_VERSION);
        // Add required entity
        LearningDocument learningDocument;
        if (TestUtil.findAll(em, LearningDocument.class).isEmpty()) {
            learningDocument = LearningDocumentResourceIT.createUpdatedEntity(em);
            em.persist(learningDocument);
            em.flush();
        } else {
            learningDocument = TestUtil.findAll(em, LearningDocument.class).getFirst();
        }
        updatedPdfChunk.setMaterial(learningDocument);
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
    void checkChunkIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pdfChunk.setChunkIndex(null);

        // Create the PdfChunk, which fails.
        PdfChunkDTO pdfChunkDTO = pdfChunkMapper.toDto(pdfChunk);

        restPdfChunkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pdfChunkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIndexVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pdfChunk.setIndexVersion(null);

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
            .andExpect(jsonPath("$.[*].embedding").value(hasItem(DEFAULT_EMBEDDING)))
            .andExpect(jsonPath("$.[*].chunkIndex").value(hasItem(DEFAULT_CHUNK_INDEX)))
            .andExpect(jsonPath("$.[*].indexVersion").value(hasItem(DEFAULT_INDEX_VERSION)));
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
            .andExpect(jsonPath("$.embedding").value(DEFAULT_EMBEDDING))
            .andExpect(jsonPath("$.chunkIndex").value(DEFAULT_CHUNK_INDEX))
            .andExpect(jsonPath("$.indexVersion").value(DEFAULT_INDEX_VERSION));
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
    void getAllPdfChunksByChunkIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex equals to
        defaultPdfChunkFiltering("chunkIndex.equals=" + DEFAULT_CHUNK_INDEX, "chunkIndex.equals=" + UPDATED_CHUNK_INDEX);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex in
        defaultPdfChunkFiltering(
            "chunkIndex.in=" + DEFAULT_CHUNK_INDEX + "," + UPDATED_CHUNK_INDEX,
            "chunkIndex.in=" + UPDATED_CHUNK_INDEX
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex is not null
        defaultPdfChunkFiltering("chunkIndex.specified=true", "chunkIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex is greater than or equal to
        defaultPdfChunkFiltering(
            "chunkIndex.greaterThanOrEqual=" + DEFAULT_CHUNK_INDEX,
            "chunkIndex.greaterThanOrEqual=" + UPDATED_CHUNK_INDEX
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex is less than or equal to
        defaultPdfChunkFiltering("chunkIndex.lessThanOrEqual=" + DEFAULT_CHUNK_INDEX, "chunkIndex.lessThanOrEqual=" + SMALLER_CHUNK_INDEX);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex is less than
        defaultPdfChunkFiltering("chunkIndex.lessThan=" + UPDATED_CHUNK_INDEX, "chunkIndex.lessThan=" + DEFAULT_CHUNK_INDEX);
    }

    @Test
    @Transactional
    void getAllPdfChunksByChunkIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where chunkIndex is greater than
        defaultPdfChunkFiltering("chunkIndex.greaterThan=" + SMALLER_CHUNK_INDEX, "chunkIndex.greaterThan=" + DEFAULT_CHUNK_INDEX);
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion equals to
        defaultPdfChunkFiltering("indexVersion.equals=" + DEFAULT_INDEX_VERSION, "indexVersion.equals=" + UPDATED_INDEX_VERSION);
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion in
        defaultPdfChunkFiltering(
            "indexVersion.in=" + DEFAULT_INDEX_VERSION + "," + UPDATED_INDEX_VERSION,
            "indexVersion.in=" + UPDATED_INDEX_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion is not null
        defaultPdfChunkFiltering("indexVersion.specified=true", "indexVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion is greater than or equal to
        defaultPdfChunkFiltering(
            "indexVersion.greaterThanOrEqual=" + DEFAULT_INDEX_VERSION,
            "indexVersion.greaterThanOrEqual=" + UPDATED_INDEX_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion is less than or equal to
        defaultPdfChunkFiltering(
            "indexVersion.lessThanOrEqual=" + DEFAULT_INDEX_VERSION,
            "indexVersion.lessThanOrEqual=" + SMALLER_INDEX_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion is less than
        defaultPdfChunkFiltering("indexVersion.lessThan=" + UPDATED_INDEX_VERSION, "indexVersion.lessThan=" + DEFAULT_INDEX_VERSION);
    }

    @Test
    @Transactional
    void getAllPdfChunksByIndexVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPdfChunk = pdfChunkRepository.saveAndFlush(pdfChunk);

        // Get all the pdfChunkList where indexVersion is greater than
        defaultPdfChunkFiltering("indexVersion.greaterThan=" + SMALLER_INDEX_VERSION, "indexVersion.greaterThan=" + DEFAULT_INDEX_VERSION);
    }

    @Test
    @Transactional
    void getAllPdfChunksByMaterialIsEqualToSomething() throws Exception {
        LearningDocument material;
        if (TestUtil.findAll(em, LearningDocument.class).isEmpty()) {
            pdfChunkRepository.saveAndFlush(pdfChunk);
            material = LearningDocumentResourceIT.createEntity(em);
        } else {
            material = TestUtil.findAll(em, LearningDocument.class).getFirst();
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
            .andExpect(jsonPath("$.[*].embedding").value(hasItem(DEFAULT_EMBEDDING)))
            .andExpect(jsonPath("$.[*].chunkIndex").value(hasItem(DEFAULT_CHUNK_INDEX)))
            .andExpect(jsonPath("$.[*].indexVersion").value(hasItem(DEFAULT_INDEX_VERSION)));

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
        updatedPdfChunk
            .pageNumber(UPDATED_PAGE_NUMBER)
            .chunkText(UPDATED_CHUNK_TEXT)
            .embedding(UPDATED_EMBEDDING)
            .chunkIndex(UPDATED_CHUNK_INDEX)
            .indexVersion(UPDATED_INDEX_VERSION);
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

        partialUpdatedPdfChunk.chunkText(UPDATED_CHUNK_TEXT).indexVersion(UPDATED_INDEX_VERSION);

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

        partialUpdatedPdfChunk
            .pageNumber(UPDATED_PAGE_NUMBER)
            .chunkText(UPDATED_CHUNK_TEXT)
            .embedding(UPDATED_EMBEDDING)
            .chunkIndex(UPDATED_CHUNK_INDEX)
            .indexVersion(UPDATED_INDEX_VERSION);

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
