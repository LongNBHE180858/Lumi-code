package com.lumi.web.rest;

import static com.lumi.domain.FlashcardDeckAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.FlashcardDeck;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.repository.FlashcardDeckRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.FlashcardDeckService;
import com.lumi.service.dto.FlashcardDeckDTO;
import com.lumi.service.mapper.FlashcardDeckMapper;
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
 * Integration tests for the {@link FlashcardDeckResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlashcardDeckResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String ENTITY_API_URL = "/api/flashcard-decks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlashcardDeckRepository flashcardDeckRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private FlashcardDeckRepository flashcardDeckRepositoryMock;

    @Autowired
    private FlashcardDeckMapper flashcardDeckMapper;

    @Mock
    private FlashcardDeckService flashcardDeckServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlashcardDeckMockMvc;

    private FlashcardDeck flashcardDeck;

    private FlashcardDeck insertedFlashcardDeck;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashcardDeck createEntity(EntityManager em) {
        FlashcardDeck flashcardDeck = new FlashcardDeck().title(DEFAULT_TITLE).isPublic(DEFAULT_IS_PUBLIC);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        flashcardDeck.setOwner(user);
        return flashcardDeck;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashcardDeck createUpdatedEntity(EntityManager em) {
        FlashcardDeck updatedFlashcardDeck = new FlashcardDeck().title(UPDATED_TITLE).isPublic(UPDATED_IS_PUBLIC);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedFlashcardDeck.setOwner(user);
        return updatedFlashcardDeck;
    }

    @BeforeEach
    void initTest() {
        flashcardDeck = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFlashcardDeck != null) {
            flashcardDeckRepository.delete(insertedFlashcardDeck);
            insertedFlashcardDeck = null;
        }
    }

    @Test
    @Transactional
    void createFlashcardDeck() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);
        var returnedFlashcardDeckDTO = om.readValue(
            restFlashcardDeckMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDeckDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlashcardDeckDTO.class
        );

        // Validate the FlashcardDeck in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlashcardDeck = flashcardDeckMapper.toEntity(returnedFlashcardDeckDTO);
        assertFlashcardDeckUpdatableFieldsEquals(returnedFlashcardDeck, getPersistedFlashcardDeck(returnedFlashcardDeck));

        insertedFlashcardDeck = returnedFlashcardDeck;
    }

    @Test
    @Transactional
    void createFlashcardDeckWithExistingId() throws Exception {
        // Create the FlashcardDeck with an existing ID
        flashcardDeck.setId(1L);
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlashcardDeckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDeckDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashcardDeck.setTitle(null);

        // Create the FlashcardDeck, which fails.
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        restFlashcardDeckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDeckDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlashcardDecks() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcardDeck.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardDecksWithEagerRelationshipsIsEnabled() throws Exception {
        when(flashcardDeckServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashcardDeckMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flashcardDeckServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardDecksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flashcardDeckServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashcardDeckMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flashcardDeckRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlashcardDeck() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get the flashcardDeck
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL_ID, flashcardDeck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flashcardDeck.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC));
    }

    @Test
    @Transactional
    void getFlashcardDecksByIdFiltering() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        Long id = flashcardDeck.getId();

        defaultFlashcardDeckFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFlashcardDeckFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFlashcardDeckFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where title equals to
        defaultFlashcardDeckFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where title in
        defaultFlashcardDeckFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where title is not null
        defaultFlashcardDeckFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where title contains
        defaultFlashcardDeckFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where title does not contain
        defaultFlashcardDeckFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByIsPublicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where isPublic equals to
        defaultFlashcardDeckFiltering("isPublic.equals=" + DEFAULT_IS_PUBLIC, "isPublic.equals=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByIsPublicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where isPublic in
        defaultFlashcardDeckFiltering("isPublic.in=" + DEFAULT_IS_PUBLIC + "," + UPDATED_IS_PUBLIC, "isPublic.in=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByIsPublicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        // Get all the flashcardDeckList where isPublic is not null
        defaultFlashcardDeckFiltering("isPublic.specified=true", "isPublic.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardDecksByOwnerIsEqualToSomething() throws Exception {
        User owner;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            flashcardDeckRepository.saveAndFlush(flashcardDeck);
            owner = UserResourceIT.createEntity();
        } else {
            owner = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(owner);
        em.flush();
        flashcardDeck.setOwner(owner);
        flashcardDeckRepository.saveAndFlush(flashcardDeck);
        Long ownerId = owner.getId();
        // Get all the flashcardDeckList where owner equals to ownerId
        defaultFlashcardDeckShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the flashcardDeckList where owner equals to (ownerId + 1)
        defaultFlashcardDeckShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }

    @Test
    @Transactional
    void getAllFlashcardDecksBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            flashcardDeckRepository.saveAndFlush(flashcardDeck);
            subject = SubjectResourceIT.createEntity();
        } else {
            subject = TestUtil.findAll(em, Subject.class).getFirst();
        }
        em.persist(subject);
        em.flush();
        flashcardDeck.setSubject(subject);
        flashcardDeckRepository.saveAndFlush(flashcardDeck);
        Long subjectId = subject.getId();
        // Get all the flashcardDeckList where subject equals to subjectId
        defaultFlashcardDeckShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the flashcardDeckList where subject equals to (subjectId + 1)
        defaultFlashcardDeckShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    private void defaultFlashcardDeckFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFlashcardDeckShouldBeFound(shouldBeFound);
        defaultFlashcardDeckShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFlashcardDeckShouldBeFound(String filter) throws Exception {
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcardDeck.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)));

        // Check, that the count call also returns 1
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFlashcardDeckShouldNotBeFound(String filter) throws Exception {
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFlashcardDeckMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFlashcardDeck() throws Exception {
        // Get the flashcardDeck
        restFlashcardDeckMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlashcardDeck() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardDeck
        FlashcardDeck updatedFlashcardDeck = flashcardDeckRepository.findById(flashcardDeck.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlashcardDeck are not directly saved in db
        em.detach(updatedFlashcardDeck);
        updatedFlashcardDeck.title(UPDATED_TITLE).isPublic(UPDATED_IS_PUBLIC);
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(updatedFlashcardDeck);

        restFlashcardDeckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardDeckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDeckDTO))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlashcardDeckToMatchAllProperties(updatedFlashcardDeck);
    }

    @Test
    @Transactional
    void putNonExistingFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardDeckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDeckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardDeckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardDeckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlashcardDeckWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardDeck using partial update
        FlashcardDeck partialUpdatedFlashcardDeck = new FlashcardDeck();
        partialUpdatedFlashcardDeck.setId(flashcardDeck.getId());

        partialUpdatedFlashcardDeck.title(UPDATED_TITLE);

        restFlashcardDeckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcardDeck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcardDeck))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardDeck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardDeckUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlashcardDeck, flashcardDeck),
            getPersistedFlashcardDeck(flashcardDeck)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlashcardDeckWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardDeck using partial update
        FlashcardDeck partialUpdatedFlashcardDeck = new FlashcardDeck();
        partialUpdatedFlashcardDeck.setId(flashcardDeck.getId());

        partialUpdatedFlashcardDeck.title(UPDATED_TITLE).isPublic(UPDATED_IS_PUBLIC);

        restFlashcardDeckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcardDeck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcardDeck))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardDeck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardDeckUpdatableFieldsEquals(partialUpdatedFlashcardDeck, getPersistedFlashcardDeck(partialUpdatedFlashcardDeck));
    }

    @Test
    @Transactional
    void patchNonExistingFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flashcardDeckDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardDeckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardDeckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlashcardDeck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardDeck.setId(longCount.incrementAndGet());

        // Create the FlashcardDeck
        FlashcardDeckDTO flashcardDeckDTO = flashcardDeckMapper.toDto(flashcardDeck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardDeckMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flashcardDeckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashcardDeck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlashcardDeck() throws Exception {
        // Initialize the database
        insertedFlashcardDeck = flashcardDeckRepository.saveAndFlush(flashcardDeck);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flashcardDeck
        restFlashcardDeckMockMvc
            .perform(delete(ENTITY_API_URL_ID, flashcardDeck.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flashcardDeckRepository.count();
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

    protected FlashcardDeck getPersistedFlashcardDeck(FlashcardDeck flashcardDeck) {
        return flashcardDeckRepository.findById(flashcardDeck.getId()).orElseThrow();
    }

    protected void assertPersistedFlashcardDeckToMatchAllProperties(FlashcardDeck expectedFlashcardDeck) {
        assertFlashcardDeckAllPropertiesEquals(expectedFlashcardDeck, getPersistedFlashcardDeck(expectedFlashcardDeck));
    }

    protected void assertPersistedFlashcardDeckToMatchUpdatableProperties(FlashcardDeck expectedFlashcardDeck) {
        assertFlashcardDeckAllUpdatablePropertiesEquals(expectedFlashcardDeck, getPersistedFlashcardDeck(expectedFlashcardDeck));
    }
}
