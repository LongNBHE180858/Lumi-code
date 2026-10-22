package com.lumi.web.rest;

import static com.lumi.domain.InstructorWalletAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.InstructorWallet;
import com.lumi.domain.User;
import com.lumi.repository.InstructorWalletRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.InstructorWalletService;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.mapper.InstructorWalletMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InstructorWalletResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InstructorWalletResourceIT {

    private static final BigDecimal DEFAULT_PENDING_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_PENDING_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_PENDING_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_AVAILABLE_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AVAILABLE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_AVAILABLE_AMOUNT = new BigDecimal(0 - 1);

    private static final Long DEFAULT_ROW_VERSION = 0L;
    private static final Long UPDATED_ROW_VERSION = 1L;
    private static final Long SMALLER_ROW_VERSION = 0L - 1L;

    private static final String ENTITY_API_URL = "/api/instructor-wallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstructorWalletRepository instructorWalletRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private InstructorWalletRepository instructorWalletRepositoryMock;

    @Autowired
    private InstructorWalletMapper instructorWalletMapper;

    @Mock
    private InstructorWalletService instructorWalletServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstructorWalletMockMvc;

    private InstructorWallet instructorWallet;

    private InstructorWallet insertedInstructorWallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstructorWallet createEntity(EntityManager em) {
        InstructorWallet instructorWallet = new InstructorWallet()
            .pendingAmount(DEFAULT_PENDING_AMOUNT)
            .availableAmount(DEFAULT_AVAILABLE_AMOUNT)
            .rowVersion(DEFAULT_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        instructorWallet.setUser(user);
        return instructorWallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstructorWallet createUpdatedEntity(EntityManager em) {
        InstructorWallet updatedInstructorWallet = new InstructorWallet()
            .pendingAmount(UPDATED_PENDING_AMOUNT)
            .availableAmount(UPDATED_AVAILABLE_AMOUNT)
            .rowVersion(UPDATED_ROW_VERSION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedInstructorWallet.setUser(user);
        return updatedInstructorWallet;
    }

    @BeforeEach
    void initTest() {
        instructorWallet = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstructorWallet != null) {
            instructorWalletRepository.delete(insertedInstructorWallet);
            insertedInstructorWallet = null;
        }
    }

    @Test
    @Transactional
    void createInstructorWallet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);
        var returnedInstructorWalletDTO = om.readValue(
            restInstructorWalletMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorWalletDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstructorWalletDTO.class
        );

        // Validate the InstructorWallet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstructorWallet = instructorWalletMapper.toEntity(returnedInstructorWalletDTO);
        assertInstructorWalletUpdatableFieldsEquals(returnedInstructorWallet, getPersistedInstructorWallet(returnedInstructorWallet));

        insertedInstructorWallet = returnedInstructorWallet;
    }

    @Test
    @Transactional
    void createInstructorWalletWithExistingId() throws Exception {
        // Create the InstructorWallet with an existing ID
        instructorWallet.setId(1L);
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstructorWalletMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorWalletDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInstructorWallets() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instructorWallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].pendingAmount").value(hasItem(sameNumber(DEFAULT_PENDING_AMOUNT))))
            .andExpect(jsonPath("$.[*].availableAmount").value(hasItem(sameNumber(DEFAULT_AVAILABLE_AMOUNT))))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstructorWalletsWithEagerRelationshipsIsEnabled() throws Exception {
        when(instructorWalletServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restInstructorWalletMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(instructorWalletServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstructorWalletsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(instructorWalletServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restInstructorWalletMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(instructorWalletRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInstructorWallet() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get the instructorWallet
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL_ID, instructorWallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instructorWallet.getId().intValue()))
            .andExpect(jsonPath("$.pendingAmount").value(sameNumber(DEFAULT_PENDING_AMOUNT)))
            .andExpect(jsonPath("$.availableAmount").value(sameNumber(DEFAULT_AVAILABLE_AMOUNT)))
            .andExpect(jsonPath("$.rowVersion").value(DEFAULT_ROW_VERSION.intValue()));
    }

    @Test
    @Transactional
    void getInstructorWalletsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        Long id = instructorWallet.getId();

        defaultInstructorWalletFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstructorWalletFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstructorWalletFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount equals to
        defaultInstructorWalletFiltering(
            "pendingAmount.equals=" + DEFAULT_PENDING_AMOUNT,
            "pendingAmount.equals=" + UPDATED_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount in
        defaultInstructorWalletFiltering(
            "pendingAmount.in=" + DEFAULT_PENDING_AMOUNT + "," + UPDATED_PENDING_AMOUNT,
            "pendingAmount.in=" + UPDATED_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount is not null
        defaultInstructorWalletFiltering("pendingAmount.specified=true", "pendingAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount is greater than or equal to
        defaultInstructorWalletFiltering(
            "pendingAmount.greaterThanOrEqual=" + DEFAULT_PENDING_AMOUNT,
            "pendingAmount.greaterThanOrEqual=" + UPDATED_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount is less than or equal to
        defaultInstructorWalletFiltering(
            "pendingAmount.lessThanOrEqual=" + DEFAULT_PENDING_AMOUNT,
            "pendingAmount.lessThanOrEqual=" + SMALLER_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount is less than
        defaultInstructorWalletFiltering(
            "pendingAmount.lessThan=" + UPDATED_PENDING_AMOUNT,
            "pendingAmount.lessThan=" + DEFAULT_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByPendingAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where pendingAmount is greater than
        defaultInstructorWalletFiltering(
            "pendingAmount.greaterThan=" + SMALLER_PENDING_AMOUNT,
            "pendingAmount.greaterThan=" + DEFAULT_PENDING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount equals to
        defaultInstructorWalletFiltering(
            "availableAmount.equals=" + DEFAULT_AVAILABLE_AMOUNT,
            "availableAmount.equals=" + UPDATED_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount in
        defaultInstructorWalletFiltering(
            "availableAmount.in=" + DEFAULT_AVAILABLE_AMOUNT + "," + UPDATED_AVAILABLE_AMOUNT,
            "availableAmount.in=" + UPDATED_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount is not null
        defaultInstructorWalletFiltering("availableAmount.specified=true", "availableAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount is greater than or equal to
        defaultInstructorWalletFiltering(
            "availableAmount.greaterThanOrEqual=" + DEFAULT_AVAILABLE_AMOUNT,
            "availableAmount.greaterThanOrEqual=" + UPDATED_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount is less than or equal to
        defaultInstructorWalletFiltering(
            "availableAmount.lessThanOrEqual=" + DEFAULT_AVAILABLE_AMOUNT,
            "availableAmount.lessThanOrEqual=" + SMALLER_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount is less than
        defaultInstructorWalletFiltering(
            "availableAmount.lessThan=" + UPDATED_AVAILABLE_AMOUNT,
            "availableAmount.lessThan=" + DEFAULT_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByAvailableAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where availableAmount is greater than
        defaultInstructorWalletFiltering(
            "availableAmount.greaterThan=" + SMALLER_AVAILABLE_AMOUNT,
            "availableAmount.greaterThan=" + DEFAULT_AVAILABLE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion equals to
        defaultInstructorWalletFiltering("rowVersion.equals=" + DEFAULT_ROW_VERSION, "rowVersion.equals=" + UPDATED_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion in
        defaultInstructorWalletFiltering(
            "rowVersion.in=" + DEFAULT_ROW_VERSION + "," + UPDATED_ROW_VERSION,
            "rowVersion.in=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion is not null
        defaultInstructorWalletFiltering("rowVersion.specified=true", "rowVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion is greater than or equal to
        defaultInstructorWalletFiltering(
            "rowVersion.greaterThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.greaterThanOrEqual=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion is less than or equal to
        defaultInstructorWalletFiltering(
            "rowVersion.lessThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.lessThanOrEqual=" + SMALLER_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion is less than
        defaultInstructorWalletFiltering("rowVersion.lessThan=" + UPDATED_ROW_VERSION, "rowVersion.lessThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByRowVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        // Get all the instructorWalletList where rowVersion is greater than
        defaultInstructorWalletFiltering("rowVersion.greaterThan=" + SMALLER_ROW_VERSION, "rowVersion.greaterThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllInstructorWalletsByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = instructorWallet.getUser();
        instructorWalletRepository.saveAndFlush(instructorWallet);
        Long userId = user.getId();
        // Get all the instructorWalletList where user equals to userId
        defaultInstructorWalletShouldBeFound("userId.equals=" + userId);

        // Get all the instructorWalletList where user equals to (userId + 1)
        defaultInstructorWalletShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultInstructorWalletFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInstructorWalletShouldBeFound(shouldBeFound);
        defaultInstructorWalletShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstructorWalletShouldBeFound(String filter) throws Exception {
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instructorWallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].pendingAmount").value(hasItem(sameNumber(DEFAULT_PENDING_AMOUNT))))
            .andExpect(jsonPath("$.[*].availableAmount").value(hasItem(sameNumber(DEFAULT_AVAILABLE_AMOUNT))))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())));

        // Check, that the count call also returns 1
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstructorWalletShouldNotBeFound(String filter) throws Exception {
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInstructorWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInstructorWallet() throws Exception {
        // Get the instructorWallet
        restInstructorWalletMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstructorWallet() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorWallet
        InstructorWallet updatedInstructorWallet = instructorWalletRepository.findById(instructorWallet.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstructorWallet are not directly saved in db
        em.detach(updatedInstructorWallet);
        updatedInstructorWallet
            .pendingAmount(UPDATED_PENDING_AMOUNT)
            .availableAmount(UPDATED_AVAILABLE_AMOUNT)
            .rowVersion(UPDATED_ROW_VERSION);
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(updatedInstructorWallet);

        restInstructorWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instructorWalletDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructorWalletDTO))
            )
            .andExpect(status().isOk());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstructorWalletToMatchAllProperties(updatedInstructorWallet);
    }

    @Test
    @Transactional
    void putNonExistingInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instructorWalletDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructorWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructorWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorWalletDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstructorWalletWithPatch() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorWallet using partial update
        InstructorWallet partialUpdatedInstructorWallet = new InstructorWallet();
        partialUpdatedInstructorWallet.setId(instructorWallet.getId());

        restInstructorWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructorWallet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructorWallet))
            )
            .andExpect(status().isOk());

        // Validate the InstructorWallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorWalletUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstructorWallet, instructorWallet),
            getPersistedInstructorWallet(instructorWallet)
        );
    }

    @Test
    @Transactional
    void fullUpdateInstructorWalletWithPatch() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorWallet using partial update
        InstructorWallet partialUpdatedInstructorWallet = new InstructorWallet();
        partialUpdatedInstructorWallet.setId(instructorWallet.getId());

        partialUpdatedInstructorWallet
            .pendingAmount(UPDATED_PENDING_AMOUNT)
            .availableAmount(UPDATED_AVAILABLE_AMOUNT)
            .rowVersion(UPDATED_ROW_VERSION);

        restInstructorWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructorWallet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructorWallet))
            )
            .andExpect(status().isOk());

        // Validate the InstructorWallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorWalletUpdatableFieldsEquals(
            partialUpdatedInstructorWallet,
            getPersistedInstructorWallet(partialUpdatedInstructorWallet)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instructorWalletDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructorWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructorWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstructorWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorWallet.setId(longCount.incrementAndGet());

        // Create the InstructorWallet
        InstructorWalletDTO instructorWalletDTO = instructorWalletMapper.toDto(instructorWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorWalletMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(instructorWalletDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstructorWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstructorWallet() throws Exception {
        // Initialize the database
        insertedInstructorWallet = instructorWalletRepository.saveAndFlush(instructorWallet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instructorWallet
        restInstructorWalletMockMvc
            .perform(delete(ENTITY_API_URL_ID, instructorWallet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return instructorWalletRepository.count();
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

    protected InstructorWallet getPersistedInstructorWallet(InstructorWallet instructorWallet) {
        return instructorWalletRepository.findById(instructorWallet.getId()).orElseThrow();
    }

    protected void assertPersistedInstructorWalletToMatchAllProperties(InstructorWallet expectedInstructorWallet) {
        assertInstructorWalletAllPropertiesEquals(expectedInstructorWallet, getPersistedInstructorWallet(expectedInstructorWallet));
    }

    protected void assertPersistedInstructorWalletToMatchUpdatableProperties(InstructorWallet expectedInstructorWallet) {
        assertInstructorWalletAllUpdatablePropertiesEquals(
            expectedInstructorWallet,
            getPersistedInstructorWallet(expectedInstructorWallet)
        );
    }
}
