package com.lumi.web.rest;

import static com.lumi.domain.CouponAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Coupon;
import com.lumi.domain.User;
import com.lumi.repository.CouponRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CouponService;
import com.lumi.service.dto.CouponDTO;
import com.lumi.service.mapper.CouponMapper;
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
 * Integration tests for the {@link CouponResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CouponResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISCOUNT_PERCENT = 1;
    private static final Integer UPDATED_DISCOUNT_PERCENT = 2;
    private static final Integer SMALLER_DISCOUNT_PERCENT = 1 - 1;

    private static final Integer DEFAULT_MAX_USES = 1;
    private static final Integer UPDATED_MAX_USES = 2;
    private static final Integer SMALLER_MAX_USES = 1 - 1;

    private static final Integer DEFAULT_USED_COUNT = 0;
    private static final Integer UPDATED_USED_COUNT = 1;
    private static final Integer SMALLER_USED_COUNT = 0 - 1;

    private static final Instant DEFAULT_VALID_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_TO = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepositoryMock;

    @Autowired
    private CouponMapper couponMapper;

    @Mock
    private CouponService couponServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCouponMockMvc;

    private Coupon coupon;

    private Coupon insertedCoupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createEntity() {
        return new Coupon()
            .code(DEFAULT_CODE)
            .discountPercent(DEFAULT_DISCOUNT_PERCENT)
            .maxUses(DEFAULT_MAX_USES)
            .usedCount(DEFAULT_USED_COUNT)
            .validTo(DEFAULT_VALID_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createUpdatedEntity() {
        return new Coupon()
            .code(UPDATED_CODE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .maxUses(UPDATED_MAX_USES)
            .usedCount(UPDATED_USED_COUNT)
            .validTo(UPDATED_VALID_TO);
    }

    @BeforeEach
    void initTest() {
        coupon = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCoupon != null) {
            couponRepository.delete(insertedCoupon);
            insertedCoupon = null;
        }
    }

    @Test
    @Transactional
    void createCoupon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);
        var returnedCouponDTO = om.readValue(
            restCouponMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CouponDTO.class
        );

        // Validate the Coupon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCoupon = couponMapper.toEntity(returnedCouponDTO);
        assertCouponUpdatableFieldsEquals(returnedCoupon, getPersistedCoupon(returnedCoupon));

        insertedCoupon = returnedCoupon;
    }

    @Test
    @Transactional
    void createCouponWithExistingId() throws Exception {
        // Create the Coupon with an existing ID
        coupon.setId(1L);
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setCode(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCoupons() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.[*].maxUses").value(hasItem(DEFAULT_MAX_USES)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCouponsWithEagerRelationshipsIsEnabled() throws Exception {
        when(couponServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCouponMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(couponServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCouponsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(couponServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCouponMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(couponRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get the coupon
        restCouponMockMvc
            .perform(get(ENTITY_API_URL_ID, coupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coupon.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.discountPercent").value(DEFAULT_DISCOUNT_PERCENT))
            .andExpect(jsonPath("$.maxUses").value(DEFAULT_MAX_USES))
            .andExpect(jsonPath("$.usedCount").value(DEFAULT_USED_COUNT))
            .andExpect(jsonPath("$.validTo").value(DEFAULT_VALID_TO.toString()));
    }

    @Test
    @Transactional
    void getCouponsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        Long id = coupon.getId();

        defaultCouponFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCouponFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCouponFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code equals to
        defaultCouponFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code in
        defaultCouponFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code is not null
        defaultCouponFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code contains
        defaultCouponFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code does not contain
        defaultCouponFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent equals to
        defaultCouponFiltering("discountPercent.equals=" + DEFAULT_DISCOUNT_PERCENT, "discountPercent.equals=" + UPDATED_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent in
        defaultCouponFiltering(
            "discountPercent.in=" + DEFAULT_DISCOUNT_PERCENT + "," + UPDATED_DISCOUNT_PERCENT,
            "discountPercent.in=" + UPDATED_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent is not null
        defaultCouponFiltering("discountPercent.specified=true", "discountPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent is greater than or equal to
        defaultCouponFiltering(
            "discountPercent.greaterThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT,
            "discountPercent.greaterThanOrEqual=" + (DEFAULT_DISCOUNT_PERCENT + 1)
        );
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent is less than or equal to
        defaultCouponFiltering(
            "discountPercent.lessThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT,
            "discountPercent.lessThanOrEqual=" + SMALLER_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent is less than
        defaultCouponFiltering(
            "discountPercent.lessThan=" + (DEFAULT_DISCOUNT_PERCENT + 1),
            "discountPercent.lessThan=" + DEFAULT_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discountPercent is greater than
        defaultCouponFiltering(
            "discountPercent.greaterThan=" + SMALLER_DISCOUNT_PERCENT,
            "discountPercent.greaterThan=" + DEFAULT_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses equals to
        defaultCouponFiltering("maxUses.equals=" + DEFAULT_MAX_USES, "maxUses.equals=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses in
        defaultCouponFiltering("maxUses.in=" + DEFAULT_MAX_USES + "," + UPDATED_MAX_USES, "maxUses.in=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses is not null
        defaultCouponFiltering("maxUses.specified=true", "maxUses.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses is greater than or equal to
        defaultCouponFiltering("maxUses.greaterThanOrEqual=" + DEFAULT_MAX_USES, "maxUses.greaterThanOrEqual=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses is less than or equal to
        defaultCouponFiltering("maxUses.lessThanOrEqual=" + DEFAULT_MAX_USES, "maxUses.lessThanOrEqual=" + SMALLER_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses is less than
        defaultCouponFiltering("maxUses.lessThan=" + UPDATED_MAX_USES, "maxUses.lessThan=" + DEFAULT_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByMaxUsesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where maxUses is greater than
        defaultCouponFiltering("maxUses.greaterThan=" + SMALLER_MAX_USES, "maxUses.greaterThan=" + DEFAULT_MAX_USES);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount equals to
        defaultCouponFiltering("usedCount.equals=" + DEFAULT_USED_COUNT, "usedCount.equals=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount in
        defaultCouponFiltering("usedCount.in=" + DEFAULT_USED_COUNT + "," + UPDATED_USED_COUNT, "usedCount.in=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount is not null
        defaultCouponFiltering("usedCount.specified=true", "usedCount.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount is greater than or equal to
        defaultCouponFiltering("usedCount.greaterThanOrEqual=" + DEFAULT_USED_COUNT, "usedCount.greaterThanOrEqual=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount is less than or equal to
        defaultCouponFiltering("usedCount.lessThanOrEqual=" + DEFAULT_USED_COUNT, "usedCount.lessThanOrEqual=" + SMALLER_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount is less than
        defaultCouponFiltering("usedCount.lessThan=" + UPDATED_USED_COUNT, "usedCount.lessThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByUsedCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where usedCount is greater than
        defaultCouponFiltering("usedCount.greaterThan=" + SMALLER_USED_COUNT, "usedCount.greaterThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByValidToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where validTo equals to
        defaultCouponFiltering("validTo.equals=" + DEFAULT_VALID_TO, "validTo.equals=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllCouponsByValidToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where validTo in
        defaultCouponFiltering("validTo.in=" + DEFAULT_VALID_TO + "," + UPDATED_VALID_TO, "validTo.in=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllCouponsByValidToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where validTo is not null
        defaultCouponFiltering("validTo.specified=true", "validTo.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByInstructorIsEqualToSomething() throws Exception {
        User instructor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            couponRepository.saveAndFlush(coupon);
            instructor = UserResourceIT.createEntity();
        } else {
            instructor = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(instructor);
        em.flush();
        coupon.setInstructor(instructor);
        couponRepository.saveAndFlush(coupon);
        Long instructorId = instructor.getId();
        // Get all the couponList where instructor equals to instructorId
        defaultCouponShouldBeFound("instructorId.equals=" + instructorId);

        // Get all the couponList where instructor equals to (instructorId + 1)
        defaultCouponShouldNotBeFound("instructorId.equals=" + (instructorId + 1));
    }

    private void defaultCouponFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCouponShouldBeFound(shouldBeFound);
        defaultCouponShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCouponShouldBeFound(String filter) throws Exception {
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.[*].maxUses").value(hasItem(DEFAULT_MAX_USES)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())));

        // Check, that the count call also returns 1
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCouponShouldNotBeFound(String filter) throws Exception {
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCoupon() throws Exception {
        // Get the coupon
        restCouponMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCoupon are not directly saved in db
        em.detach(updatedCoupon);
        updatedCoupon
            .code(UPDATED_CODE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .maxUses(UPDATED_MAX_USES)
            .usedCount(UPDATED_USED_COUNT)
            .validTo(UPDATED_VALID_TO);
        CouponDTO couponDTO = couponMapper.toDto(updatedCoupon);

        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCouponToMatchAllProperties(updatedCoupon);
    }

    @Test
    @Transactional
    void putNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon.discountPercent(UPDATED_DISCOUNT_PERCENT).validTo(UPDATED_VALID_TO);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCoupon, coupon), getPersistedCoupon(coupon));
    }

    @Test
    @Transactional
    void fullUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon
            .code(UPDATED_CODE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .maxUses(UPDATED_MAX_USES)
            .usedCount(UPDATED_USED_COUNT)
            .validTo(UPDATED_VALID_TO);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(partialUpdatedCoupon, getPersistedCoupon(partialUpdatedCoupon));
    }

    @Test
    @Transactional
    void patchNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, couponDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the coupon
        restCouponMockMvc
            .perform(delete(ENTITY_API_URL_ID, coupon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return couponRepository.count();
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

    protected Coupon getPersistedCoupon(Coupon coupon) {
        return couponRepository.findById(coupon.getId()).orElseThrow();
    }

    protected void assertPersistedCouponToMatchAllProperties(Coupon expectedCoupon) {
        assertCouponAllPropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }

    protected void assertPersistedCouponToMatchUpdatableProperties(Coupon expectedCoupon) {
        assertCouponAllUpdatablePropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }
}
