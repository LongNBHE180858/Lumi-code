package com.lumi.web.rest;

import static com.lumi.domain.FlashcardAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Flashcard;
import com.lumi.domain.FlashcardDeck;
import com.lumi.repository.FlashcardRepository;
import com.lumi.service.FlashcardService;
import com.lumi.service.dto.FlashcardDTO;
import com.lumi.service.mapper.FlashcardMapper;
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
 * Integration tests for the {@link FlashcardResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlashcardResourceIT {

    private static final String DEFAULT_FRONT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_FRONT_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_BACK_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_BACK_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/flashcards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Mock
    private FlashcardRepository flashcardRepositoryMock;

    @Autowired
    private FlashcardMapper flashcardMapper;

    @Mock
    private FlashcardService flashcardServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlashcardMockMvc;

    private Flashcard flashcard;

    private Flashcard insertedFlashcard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flashcard createEntity(EntityManager em) {
        Flashcard flashcard = new Flashcard().frontText(DEFAULT_FRONT_TEXT).backText(DEFAULT_BACK_TEXT);
        // Add required entity
        FlashcardDeck flashcardDeck;
        if (TestUtil.findAll(em, FlashcardDeck.class).isEmpty()) {
            flashcardDeck = FlashcardDeckResourceIT.createEntity(em);
            em.persist(flashcardDeck);
            em.flush();
        } else {
            flashcardDeck = TestUtil.findAll(em, FlashcardDeck.class).getFirst();
        }
        flashcard.setDeck(flashcardDeck);
        return flashcard;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flashcard createUpdatedEntity(EntityManager em) {
        Flashcard updatedFlashcard = new Flashcard().frontText(UPDATED_FRONT_TEXT).backText(UPDATED_BACK_TEXT);
        // Add required entity
        FlashcardDeck flashcardDeck;
        if (TestUtil.findAll(em, FlashcardDeck.class).isEmpty()) {
            flashcardDeck = FlashcardDeckResourceIT.createUpdatedEntity(em);
            em.persist(flashcardDeck);
            em.flush();
        } else {
            flashcardDeck = TestUtil.findAll(em, FlashcardDeck.class).getFirst();
        }
        updatedFlashcard.setDeck(flashcardDeck);
        return updatedFlashcard;
    }

    @BeforeEach
    void initTest() {
        flashcard = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFlashcard != null) {
            flashcardRepository.delete(insertedFlashcard);
            insertedFlashcard = null;
        }
    }

    @Test
    @Transactional
    void createFlashcard() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);
        var returnedFlashcardDTO = om.readValue(
            restFlashcardMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlashcardDTO.class
        );

        // Validate the Flashcard in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlashcard = flashcardMapper.toEntity(returnedFlashcardDTO);
        assertFlashcardUpdatableFieldsEquals(returnedFlashcard, getPersistedFlashcard(returnedFlashcard));

        insertedFlashcard = returnedFlashcard;
    }

    @Test
    @Transactional
    void createFlashcardWithExistingId() throws Exception {
        // Create the Flashcard with an existing ID
        flashcard.setId(1L);
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlashcardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFrontTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashcard.setFrontText(null);

        // Create the Flashcard, which fails.
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        restFlashcardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBackTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashcard.setBackText(null);

        // Create the Flashcard, which fails.
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        restFlashcardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlashcards() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcard.getId().intValue())))
            .andExpect(jsonPath("$.[*].frontText").value(hasItem(DEFAULT_FRONT_TEXT)))
            .andExpect(jsonPath("$.[*].backText").value(hasItem(DEFAULT_BACK_TEXT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardsWithEagerRelationshipsIsEnabled() throws Exception {
        when(flashcardServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashcardMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flashcardServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flashcardServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashcardMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flashcardRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlashcard() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get the flashcard
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL_ID, flashcard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flashcard.getId().intValue()))
            .andExpect(jsonPath("$.frontText").value(DEFAULT_FRONT_TEXT))
            .andExpect(jsonPath("$.backText").value(DEFAULT_BACK_TEXT));
    }

    @Test
    @Transactional
    void getFlashcardsByIdFiltering() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        Long id = flashcard.getId();

        defaultFlashcardFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFlashcardFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFlashcardFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFlashcardsByFrontTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where frontText equals to
        defaultFlashcardFiltering("frontText.equals=" + DEFAULT_FRONT_TEXT, "frontText.equals=" + UPDATED_FRONT_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByFrontTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where frontText in
        defaultFlashcardFiltering("frontText.in=" + DEFAULT_FRONT_TEXT + "," + UPDATED_FRONT_TEXT, "frontText.in=" + UPDATED_FRONT_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByFrontTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where frontText is not null
        defaultFlashcardFiltering("frontText.specified=true", "frontText.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardsByFrontTextContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where frontText contains
        defaultFlashcardFiltering("frontText.contains=" + DEFAULT_FRONT_TEXT, "frontText.contains=" + UPDATED_FRONT_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByFrontTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where frontText does not contain
        defaultFlashcardFiltering("frontText.doesNotContain=" + UPDATED_FRONT_TEXT, "frontText.doesNotContain=" + DEFAULT_FRONT_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByBackTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where backText equals to
        defaultFlashcardFiltering("backText.equals=" + DEFAULT_BACK_TEXT, "backText.equals=" + UPDATED_BACK_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByBackTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where backText in
        defaultFlashcardFiltering("backText.in=" + DEFAULT_BACK_TEXT + "," + UPDATED_BACK_TEXT, "backText.in=" + UPDATED_BACK_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByBackTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where backText is not null
        defaultFlashcardFiltering("backText.specified=true", "backText.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardsByBackTextContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where backText contains
        defaultFlashcardFiltering("backText.contains=" + DEFAULT_BACK_TEXT, "backText.contains=" + UPDATED_BACK_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByBackTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        // Get all the flashcardList where backText does not contain
        defaultFlashcardFiltering("backText.doesNotContain=" + UPDATED_BACK_TEXT, "backText.doesNotContain=" + DEFAULT_BACK_TEXT);
    }

    @Test
    @Transactional
    void getAllFlashcardsByDeckIsEqualToSomething() throws Exception {
        FlashcardDeck deck;
        if (TestUtil.findAll(em, FlashcardDeck.class).isEmpty()) {
            flashcardRepository.saveAndFlush(flashcard);
            deck = FlashcardDeckResourceIT.createEntity(em);
        } else {
            deck = TestUtil.findAll(em, FlashcardDeck.class).getFirst();
        }
        em.persist(deck);
        em.flush();
        flashcard.setDeck(deck);
        flashcardRepository.saveAndFlush(flashcard);
        Long deckId = deck.getId();
        // Get all the flashcardList where deck equals to deckId
        defaultFlashcardShouldBeFound("deckId.equals=" + deckId);

        // Get all the flashcardList where deck equals to (deckId + 1)
        defaultFlashcardShouldNotBeFound("deckId.equals=" + (deckId + 1));
    }

    private void defaultFlashcardFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFlashcardShouldBeFound(shouldBeFound);
        defaultFlashcardShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFlashcardShouldBeFound(String filter) throws Exception {
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcard.getId().intValue())))
            .andExpect(jsonPath("$.[*].frontText").value(hasItem(DEFAULT_FRONT_TEXT)))
            .andExpect(jsonPath("$.[*].backText").value(hasItem(DEFAULT_BACK_TEXT)));

        // Check, that the count call also returns 1
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFlashcardShouldNotBeFound(String filter) throws Exception {
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFlashcardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFlashcard() throws Exception {
        // Get the flashcard
        restFlashcardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlashcard() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcard
        Flashcard updatedFlashcard = flashcardRepository.findById(flashcard.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlashcard are not directly saved in db
        em.detach(updatedFlashcard);
        updatedFlashcard.frontText(UPDATED_FRONT_TEXT).backText(UPDATED_BACK_TEXT);
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(updatedFlashcard);

        restFlashcardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDTO))
            )
            .andExpect(status().isOk());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlashcardToMatchAllProperties(updatedFlashcard);
    }

    @Test
    @Transactional
    void putNonExistingFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlashcardWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcard using partial update
        Flashcard partialUpdatedFlashcard = new Flashcard();
        partialUpdatedFlashcard.setId(flashcard.getId());

        restFlashcardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcard.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcard))
            )
            .andExpect(status().isOk());

        // Validate the Flashcard in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlashcard, flashcard),
            getPersistedFlashcard(flashcard)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlashcardWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcard using partial update
        Flashcard partialUpdatedFlashcard = new Flashcard();
        partialUpdatedFlashcard.setId(flashcard.getId());

        partialUpdatedFlashcard.frontText(UPDATED_FRONT_TEXT).backText(UPDATED_BACK_TEXT);

        restFlashcardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcard.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcard))
            )
            .andExpect(status().isOk());

        // Validate the Flashcard in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardUpdatableFieldsEquals(partialUpdatedFlashcard, getPersistedFlashcard(partialUpdatedFlashcard));
    }

    @Test
    @Transactional
    void patchNonExistingFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flashcardDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlashcard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcard.setId(longCount.incrementAndGet());

        // Create the Flashcard
        FlashcardDTO flashcardDTO = flashcardMapper.toDto(flashcard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flashcardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Flashcard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlashcard() throws Exception {
        // Initialize the database
        insertedFlashcard = flashcardRepository.saveAndFlush(flashcard);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flashcard
        restFlashcardMockMvc
            .perform(delete(ENTITY_API_URL_ID, flashcard.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flashcardRepository.count();
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

    protected Flashcard getPersistedFlashcard(Flashcard flashcard) {
        return flashcardRepository.findById(flashcard.getId()).orElseThrow();
    }

    protected void assertPersistedFlashcardToMatchAllProperties(Flashcard expectedFlashcard) {
        assertFlashcardAllPropertiesEquals(expectedFlashcard, getPersistedFlashcard(expectedFlashcard));
    }

    protected void assertPersistedFlashcardToMatchUpdatableProperties(Flashcard expectedFlashcard) {
        assertFlashcardAllUpdatablePropertiesEquals(expectedFlashcard, getPersistedFlashcard(expectedFlashcard));
    }
}
