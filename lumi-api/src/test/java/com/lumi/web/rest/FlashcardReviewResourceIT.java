package com.lumi.web.rest;

import static com.lumi.domain.FlashcardReviewAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Flashcard;
import com.lumi.domain.FlashcardReview;
import com.lumi.domain.User;
import com.lumi.repository.FlashcardReviewRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.FlashcardReviewService;
import com.lumi.service.dto.FlashcardReviewDTO;
import com.lumi.service.mapper.FlashcardReviewMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link FlashcardReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlashcardReviewResourceIT {

    private static final Instant DEFAULT_NEXT_REVIEW_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_NEXT_REVIEW_AT = Instant.ofEpochMilli(1791835009362L);

    private static final BigDecimal DEFAULT_INTERVAL_DAYS = new BigDecimal(0);
    private static final BigDecimal UPDATED_INTERVAL_DAYS = new BigDecimal(1);
    private static final BigDecimal SMALLER_INTERVAL_DAYS = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/flashcard-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlashcardReviewRepository flashcardReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private FlashcardReviewRepository flashcardReviewRepositoryMock;

    @Autowired
    private FlashcardReviewMapper flashcardReviewMapper;

    @Mock
    private FlashcardReviewService flashcardReviewServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlashcardReviewMockMvc;

    private FlashcardReview flashcardReview;

    private FlashcardReview insertedFlashcardReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashcardReview createEntity(EntityManager em) {
        FlashcardReview flashcardReview = new FlashcardReview().nextReviewAt(DEFAULT_NEXT_REVIEW_AT).intervalDays(DEFAULT_INTERVAL_DAYS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        flashcardReview.setStudent(user);
        // Add required entity
        Flashcard flashcard;
        if (TestUtil.findAll(em, Flashcard.class).isEmpty()) {
            flashcard = FlashcardResourceIT.createEntity(em);
            em.persist(flashcard);
            em.flush();
        } else {
            flashcard = TestUtil.findAll(em, Flashcard.class).getFirst();
        }
        flashcardReview.setFlashcard(flashcard);
        return flashcardReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashcardReview createUpdatedEntity(EntityManager em) {
        FlashcardReview updatedFlashcardReview = new FlashcardReview()
            .nextReviewAt(UPDATED_NEXT_REVIEW_AT)
            .intervalDays(UPDATED_INTERVAL_DAYS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedFlashcardReview.setStudent(user);
        // Add required entity
        Flashcard flashcard;
        if (TestUtil.findAll(em, Flashcard.class).isEmpty()) {
            flashcard = FlashcardResourceIT.createUpdatedEntity(em);
            em.persist(flashcard);
            em.flush();
        } else {
            flashcard = TestUtil.findAll(em, Flashcard.class).getFirst();
        }
        updatedFlashcardReview.setFlashcard(flashcard);
        return updatedFlashcardReview;
    }

    @BeforeEach
    void initTest() {
        flashcardReview = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFlashcardReview != null) {
            flashcardReviewRepository.delete(insertedFlashcardReview);
            insertedFlashcardReview = null;
        }
    }

    @Test
    @Transactional
    void createFlashcardReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);
        var returnedFlashcardReviewDTO = om.readValue(
            restFlashcardReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardReviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlashcardReviewDTO.class
        );

        // Validate the FlashcardReview in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlashcardReview = flashcardReviewMapper.toEntity(returnedFlashcardReviewDTO);
        assertFlashcardReviewUpdatableFieldsEquals(returnedFlashcardReview, getPersistedFlashcardReview(returnedFlashcardReview));

        insertedFlashcardReview = returnedFlashcardReview;
    }

    @Test
    @Transactional
    void createFlashcardReviewWithExistingId() throws Exception {
        // Create the FlashcardReview with an existing ID
        flashcardReview.setId(1L);
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlashcardReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardReviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFlashcardReviews() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcardReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].nextReviewAt").value(hasItem(DEFAULT_NEXT_REVIEW_AT.toString())))
            .andExpect(jsonPath("$.[*].intervalDays").value(hasItem(sameNumber(DEFAULT_INTERVAL_DAYS))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardReviewsWithEagerRelationshipsIsEnabled() throws Exception {
        when(flashcardReviewServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restFlashcardReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flashcardReviewServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashcardReviewsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flashcardReviewServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restFlashcardReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flashcardReviewRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlashcardReview() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get the flashcardReview
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, flashcardReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flashcardReview.getId().intValue()))
            .andExpect(jsonPath("$.nextReviewAt").value(DEFAULT_NEXT_REVIEW_AT.toString()))
            .andExpect(jsonPath("$.intervalDays").value(sameNumber(DEFAULT_INTERVAL_DAYS)));
    }

    @Test
    @Transactional
    void getFlashcardReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        Long id = flashcardReview.getId();

        defaultFlashcardReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFlashcardReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFlashcardReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByNextReviewAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where nextReviewAt equals to
        defaultFlashcardReviewFiltering("nextReviewAt.equals=" + DEFAULT_NEXT_REVIEW_AT, "nextReviewAt.equals=" + UPDATED_NEXT_REVIEW_AT);
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByNextReviewAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where nextReviewAt in
        defaultFlashcardReviewFiltering(
            "nextReviewAt.in=" + DEFAULT_NEXT_REVIEW_AT + "," + UPDATED_NEXT_REVIEW_AT,
            "nextReviewAt.in=" + UPDATED_NEXT_REVIEW_AT
        );
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByNextReviewAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where nextReviewAt is not null
        defaultFlashcardReviewFiltering("nextReviewAt.specified=true", "nextReviewAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays equals to
        defaultFlashcardReviewFiltering("intervalDays.equals=" + DEFAULT_INTERVAL_DAYS, "intervalDays.equals=" + UPDATED_INTERVAL_DAYS);
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays in
        defaultFlashcardReviewFiltering(
            "intervalDays.in=" + DEFAULT_INTERVAL_DAYS + "," + UPDATED_INTERVAL_DAYS,
            "intervalDays.in=" + UPDATED_INTERVAL_DAYS
        );
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays is not null
        defaultFlashcardReviewFiltering("intervalDays.specified=true", "intervalDays.specified=false");
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays is greater than or equal to
        defaultFlashcardReviewFiltering(
            "intervalDays.greaterThanOrEqual=" + DEFAULT_INTERVAL_DAYS,
            "intervalDays.greaterThanOrEqual=" + UPDATED_INTERVAL_DAYS
        );
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays is less than or equal to
        defaultFlashcardReviewFiltering(
            "intervalDays.lessThanOrEqual=" + DEFAULT_INTERVAL_DAYS,
            "intervalDays.lessThanOrEqual=" + SMALLER_INTERVAL_DAYS
        );
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays is less than
        defaultFlashcardReviewFiltering("intervalDays.lessThan=" + UPDATED_INTERVAL_DAYS, "intervalDays.lessThan=" + DEFAULT_INTERVAL_DAYS);
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByIntervalDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        // Get all the flashcardReviewList where intervalDays is greater than
        defaultFlashcardReviewFiltering(
            "intervalDays.greaterThan=" + SMALLER_INTERVAL_DAYS,
            "intervalDays.greaterThan=" + DEFAULT_INTERVAL_DAYS
        );
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            flashcardReviewRepository.saveAndFlush(flashcardReview);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        flashcardReview.setStudent(student);
        flashcardReviewRepository.saveAndFlush(flashcardReview);
        Long studentId = student.getId();
        // Get all the flashcardReviewList where student equals to studentId
        defaultFlashcardReviewShouldBeFound("studentId.equals=" + studentId);

        // Get all the flashcardReviewList where student equals to (studentId + 1)
        defaultFlashcardReviewShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllFlashcardReviewsByFlashcardIsEqualToSomething() throws Exception {
        Flashcard flashcard;
        if (TestUtil.findAll(em, Flashcard.class).isEmpty()) {
            flashcardReviewRepository.saveAndFlush(flashcardReview);
            flashcard = FlashcardResourceIT.createEntity(em);
        } else {
            flashcard = TestUtil.findAll(em, Flashcard.class).getFirst();
        }
        em.persist(flashcard);
        em.flush();
        flashcardReview.setFlashcard(flashcard);
        flashcardReviewRepository.saveAndFlush(flashcardReview);
        Long flashcardId = flashcard.getId();
        // Get all the flashcardReviewList where flashcard equals to flashcardId
        defaultFlashcardReviewShouldBeFound("flashcardId.equals=" + flashcardId);

        // Get all the flashcardReviewList where flashcard equals to (flashcardId + 1)
        defaultFlashcardReviewShouldNotBeFound("flashcardId.equals=" + (flashcardId + 1));
    }

    private void defaultFlashcardReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFlashcardReviewShouldBeFound(shouldBeFound);
        defaultFlashcardReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFlashcardReviewShouldBeFound(String filter) throws Exception {
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashcardReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].nextReviewAt").value(hasItem(DEFAULT_NEXT_REVIEW_AT.toString())))
            .andExpect(jsonPath("$.[*].intervalDays").value(hasItem(sameNumber(DEFAULT_INTERVAL_DAYS))));

        // Check, that the count call also returns 1
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFlashcardReviewShouldNotBeFound(String filter) throws Exception {
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFlashcardReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFlashcardReview() throws Exception {
        // Get the flashcardReview
        restFlashcardReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlashcardReview() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardReview
        FlashcardReview updatedFlashcardReview = flashcardReviewRepository.findById(flashcardReview.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlashcardReview are not directly saved in db
        em.detach(updatedFlashcardReview);
        updatedFlashcardReview.nextReviewAt(UPDATED_NEXT_REVIEW_AT).intervalDays(UPDATED_INTERVAL_DAYS);
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(updatedFlashcardReview);

        restFlashcardReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardReviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlashcardReviewToMatchAllProperties(updatedFlashcardReview);
    }

    @Test
    @Transactional
    void putNonExistingFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashcardReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashcardReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashcardReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlashcardReviewWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardReview using partial update
        FlashcardReview partialUpdatedFlashcardReview = new FlashcardReview();
        partialUpdatedFlashcardReview.setId(flashcardReview.getId());

        restFlashcardReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcardReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcardReview))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardReviewUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlashcardReview, flashcardReview),
            getPersistedFlashcardReview(flashcardReview)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlashcardReviewWithPatch() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashcardReview using partial update
        FlashcardReview partialUpdatedFlashcardReview = new FlashcardReview();
        partialUpdatedFlashcardReview.setId(flashcardReview.getId());

        partialUpdatedFlashcardReview.nextReviewAt(UPDATED_NEXT_REVIEW_AT).intervalDays(UPDATED_INTERVAL_DAYS);

        restFlashcardReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashcardReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashcardReview))
            )
            .andExpect(status().isOk());

        // Validate the FlashcardReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashcardReviewUpdatableFieldsEquals(
            partialUpdatedFlashcardReview,
            getPersistedFlashcardReview(partialUpdatedFlashcardReview)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flashcardReviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashcardReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlashcardReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashcardReview.setId(longCount.incrementAndGet());

        // Create the FlashcardReview
        FlashcardReviewDTO flashcardReviewDTO = flashcardReviewMapper.toDto(flashcardReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashcardReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flashcardReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashcardReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlashcardReview() throws Exception {
        // Initialize the database
        insertedFlashcardReview = flashcardReviewRepository.saveAndFlush(flashcardReview);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flashcardReview
        restFlashcardReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, flashcardReview.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flashcardReviewRepository.count();
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

    protected FlashcardReview getPersistedFlashcardReview(FlashcardReview flashcardReview) {
        return flashcardReviewRepository.findById(flashcardReview.getId()).orElseThrow();
    }

    protected void assertPersistedFlashcardReviewToMatchAllProperties(FlashcardReview expectedFlashcardReview) {
        assertFlashcardReviewAllPropertiesEquals(expectedFlashcardReview, getPersistedFlashcardReview(expectedFlashcardReview));
    }

    protected void assertPersistedFlashcardReviewToMatchUpdatableProperties(FlashcardReview expectedFlashcardReview) {
        assertFlashcardReviewAllUpdatablePropertiesEquals(expectedFlashcardReview, getPersistedFlashcardReview(expectedFlashcardReview));
    }
}
