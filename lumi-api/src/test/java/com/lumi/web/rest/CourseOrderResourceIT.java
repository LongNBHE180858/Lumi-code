package com.lumi.web.rest;

import static com.lumi.domain.CourseOrderAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Coupon;
import com.lumi.domain.Course;
import com.lumi.domain.CourseOrder;
import com.lumi.domain.SystemPlan;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.OrderStatus;
import com.lumi.repository.CourseOrderRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.CourseOrderService;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.mapper.CourseOrderMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link CourseOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseOrderResourceIT {

    private static final BigDecimal DEFAULT_ORIGINAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_ORIGINAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_ORIGINAL_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_FINAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_FINAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_FINAL_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_REVENUE_SPLIT_RATE = "AAAAAAAAAA";
    private static final String UPDATED_REVENUE_SPLIT_RATE = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.PAID;

    private static final String DEFAULT_IDEMPOTENCY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_IDEMPOTENCY_KEY = "BBBBBBBBBB";

    private static final Long DEFAULT_ROW_VERSION = 0L;
    private static final Long UPDATED_ROW_VERSION = 1L;
    private static final Long SMALLER_ROW_VERSION = 0L - 1L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String ENTITY_API_URL = "/api/course-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseOrderRepository courseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseOrderRepository courseOrderRepositoryMock;

    @Autowired
    private CourseOrderMapper courseOrderMapper;

    @Mock
    private CourseOrderService courseOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseOrderMockMvc;

    private CourseOrder courseOrder;

    private CourseOrder insertedCourseOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseOrder createEntity(EntityManager em) {
        CourseOrder courseOrder = new CourseOrder()
            .originalPrice(DEFAULT_ORIGINAL_PRICE)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .finalPrice(DEFAULT_FINAL_PRICE)
            .revenueSplitRate(DEFAULT_REVENUE_SPLIT_RATE)
            .status(DEFAULT_STATUS)
            .idempotencyKey(DEFAULT_IDEMPOTENCY_KEY)
            .rowVersion(DEFAULT_ROW_VERSION)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        courseOrder.setStudent(user);
        return courseOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseOrder createUpdatedEntity(EntityManager em) {
        CourseOrder updatedCourseOrder = new CourseOrder()
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .finalPrice(UPDATED_FINAL_PRICE)
            .revenueSplitRate(UPDATED_REVENUE_SPLIT_RATE)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .rowVersion(UPDATED_ROW_VERSION)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCourseOrder.setStudent(user);
        return updatedCourseOrder;
    }

    @BeforeEach
    void initTest() {
        courseOrder = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseOrder != null) {
            courseOrderRepository.deleteById(insertedCourseOrder.getId());
            insertedCourseOrder = null;
        }
    }

    @Test
    @Transactional
    void createCourseOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);
        var returnedCourseOrderDTO = om.readValue(
            restCourseOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseOrderDTO.class
        );

        // Validate the CourseOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseOrder = courseOrderMapper.toEntity(returnedCourseOrderDTO);
        assertCourseOrderUpdatableFieldsEquals(returnedCourseOrder, getPersistedCourseOrder(returnedCourseOrder));

        insertedCourseOrder = returnedCourseOrder;
    }

    @Test
    @Transactional
    void createCourseOrderWithExistingId() throws Exception {
        // Create the CourseOrder with an existing ID
        courseOrder.setId(1L);
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOriginalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseOrder.setOriginalPrice(null);

        // Create the CourseOrder, which fails.
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        restCourseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseOrder.setFinalPrice(null);

        // Create the CourseOrder, which fails.
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        restCourseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseOrders() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].originalPrice").value(hasItem(sameNumber(DEFAULT_ORIGINAL_PRICE))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
            .andExpect(jsonPath("$.[*].revenueSplitRate").value(hasItem(DEFAULT_REVENUE_SPLIT_RATE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseOrder() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get the courseOrder
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, courseOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseOrder.getId().intValue()))
            .andExpect(jsonPath("$.originalPrice").value(sameNumber(DEFAULT_ORIGINAL_PRICE)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.finalPrice").value(sameNumber(DEFAULT_FINAL_PRICE)))
            .andExpect(jsonPath("$.revenueSplitRate").value(DEFAULT_REVENUE_SPLIT_RATE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.idempotencyKey").value(DEFAULT_IDEMPOTENCY_KEY))
            .andExpect(jsonPath("$.rowVersion").value(DEFAULT_ROW_VERSION.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCourseOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        Long id = courseOrder.getId();

        defaultCourseOrderFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice equals to
        defaultCourseOrderFiltering("originalPrice.equals=" + DEFAULT_ORIGINAL_PRICE, "originalPrice.equals=" + UPDATED_ORIGINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice in
        defaultCourseOrderFiltering(
            "originalPrice.in=" + DEFAULT_ORIGINAL_PRICE + "," + UPDATED_ORIGINAL_PRICE,
            "originalPrice.in=" + UPDATED_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice is not null
        defaultCourseOrderFiltering("originalPrice.specified=true", "originalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice is greater than or equal to
        defaultCourseOrderFiltering(
            "originalPrice.greaterThanOrEqual=" + DEFAULT_ORIGINAL_PRICE,
            "originalPrice.greaterThanOrEqual=" + UPDATED_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice is less than or equal to
        defaultCourseOrderFiltering(
            "originalPrice.lessThanOrEqual=" + DEFAULT_ORIGINAL_PRICE,
            "originalPrice.lessThanOrEqual=" + SMALLER_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice is less than
        defaultCourseOrderFiltering("originalPrice.lessThan=" + UPDATED_ORIGINAL_PRICE, "originalPrice.lessThan=" + DEFAULT_ORIGINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByOriginalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where originalPrice is greater than
        defaultCourseOrderFiltering(
            "originalPrice.greaterThan=" + SMALLER_ORIGINAL_PRICE,
            "originalPrice.greaterThan=" + DEFAULT_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount equals to
        defaultCourseOrderFiltering("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT, "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount in
        defaultCourseOrderFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount is not null
        defaultCourseOrderFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount is greater than or equal to
        defaultCourseOrderFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount is less than or equal to
        defaultCourseOrderFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount is less than
        defaultCourseOrderFiltering(
            "discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where discountAmount is greater than
        defaultCourseOrderFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice equals to
        defaultCourseOrderFiltering("finalPrice.equals=" + DEFAULT_FINAL_PRICE, "finalPrice.equals=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice in
        defaultCourseOrderFiltering(
            "finalPrice.in=" + DEFAULT_FINAL_PRICE + "," + UPDATED_FINAL_PRICE,
            "finalPrice.in=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice is not null
        defaultCourseOrderFiltering("finalPrice.specified=true", "finalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice is greater than or equal to
        defaultCourseOrderFiltering(
            "finalPrice.greaterThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.greaterThanOrEqual=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice is less than or equal to
        defaultCourseOrderFiltering(
            "finalPrice.lessThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.lessThanOrEqual=" + SMALLER_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice is less than
        defaultCourseOrderFiltering("finalPrice.lessThan=" + UPDATED_FINAL_PRICE, "finalPrice.lessThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByFinalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where finalPrice is greater than
        defaultCourseOrderFiltering("finalPrice.greaterThan=" + SMALLER_FINAL_PRICE, "finalPrice.greaterThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRevenueSplitRateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where revenueSplitRate equals to
        defaultCourseOrderFiltering(
            "revenueSplitRate.equals=" + DEFAULT_REVENUE_SPLIT_RATE,
            "revenueSplitRate.equals=" + UPDATED_REVENUE_SPLIT_RATE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRevenueSplitRateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where revenueSplitRate in
        defaultCourseOrderFiltering(
            "revenueSplitRate.in=" + DEFAULT_REVENUE_SPLIT_RATE + "," + UPDATED_REVENUE_SPLIT_RATE,
            "revenueSplitRate.in=" + UPDATED_REVENUE_SPLIT_RATE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRevenueSplitRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where revenueSplitRate is not null
        defaultCourseOrderFiltering("revenueSplitRate.specified=true", "revenueSplitRate.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRevenueSplitRateContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where revenueSplitRate contains
        defaultCourseOrderFiltering(
            "revenueSplitRate.contains=" + DEFAULT_REVENUE_SPLIT_RATE,
            "revenueSplitRate.contains=" + UPDATED_REVENUE_SPLIT_RATE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRevenueSplitRateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where revenueSplitRate does not contain
        defaultCourseOrderFiltering(
            "revenueSplitRate.doesNotContain=" + UPDATED_REVENUE_SPLIT_RATE,
            "revenueSplitRate.doesNotContain=" + DEFAULT_REVENUE_SPLIT_RATE
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where status equals to
        defaultCourseOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where status in
        defaultCourseOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where status is not null
        defaultCourseOrderFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByIdempotencyKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where idempotencyKey equals to
        defaultCourseOrderFiltering("idempotencyKey.equals=" + DEFAULT_IDEMPOTENCY_KEY, "idempotencyKey.equals=" + UPDATED_IDEMPOTENCY_KEY);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByIdempotencyKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where idempotencyKey in
        defaultCourseOrderFiltering(
            "idempotencyKey.in=" + DEFAULT_IDEMPOTENCY_KEY + "," + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.in=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByIdempotencyKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where idempotencyKey is not null
        defaultCourseOrderFiltering("idempotencyKey.specified=true", "idempotencyKey.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByIdempotencyKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where idempotencyKey contains
        defaultCourseOrderFiltering(
            "idempotencyKey.contains=" + DEFAULT_IDEMPOTENCY_KEY,
            "idempotencyKey.contains=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByIdempotencyKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where idempotencyKey does not contain
        defaultCourseOrderFiltering(
            "idempotencyKey.doesNotContain=" + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.doesNotContain=" + DEFAULT_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion equals to
        defaultCourseOrderFiltering("rowVersion.equals=" + DEFAULT_ROW_VERSION, "rowVersion.equals=" + UPDATED_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion in
        defaultCourseOrderFiltering(
            "rowVersion.in=" + DEFAULT_ROW_VERSION + "," + UPDATED_ROW_VERSION,
            "rowVersion.in=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion is not null
        defaultCourseOrderFiltering("rowVersion.specified=true", "rowVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion is greater than or equal to
        defaultCourseOrderFiltering(
            "rowVersion.greaterThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.greaterThanOrEqual=" + UPDATED_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion is less than or equal to
        defaultCourseOrderFiltering(
            "rowVersion.lessThanOrEqual=" + DEFAULT_ROW_VERSION,
            "rowVersion.lessThanOrEqual=" + SMALLER_ROW_VERSION
        );
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion is less than
        defaultCourseOrderFiltering("rowVersion.lessThan=" + UPDATED_ROW_VERSION, "rowVersion.lessThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByRowVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where rowVersion is greater than
        defaultCourseOrderFiltering("rowVersion.greaterThan=" + SMALLER_ROW_VERSION, "rowVersion.greaterThan=" + DEFAULT_ROW_VERSION);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where createdAt equals to
        defaultCourseOrderFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where createdAt in
        defaultCourseOrderFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCourseOrdersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        // Get all the courseOrderList where createdAt is not null
        defaultCourseOrderFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCourseOrdersByStudentIsEqualToSomething() throws Exception {
        User student;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            courseOrderRepository.saveAndFlush(courseOrder);
            student = UserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(student);
        em.flush();
        courseOrder.setStudent(student);
        courseOrderRepository.saveAndFlush(courseOrder);
        Long studentId = student.getId();
        // Get all the courseOrderList where student equals to studentId
        defaultCourseOrderShouldBeFound("studentId.equals=" + studentId);

        // Get all the courseOrderList where student equals to (studentId + 1)
        defaultCourseOrderShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    @Test
    @Transactional
    void getAllCourseOrdersByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            courseOrderRepository.saveAndFlush(courseOrder);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        em.persist(course);
        em.flush();
        courseOrder.setCourse(course);
        courseOrderRepository.saveAndFlush(courseOrder);
        Long courseId = course.getId();
        // Get all the courseOrderList where course equals to courseId
        defaultCourseOrderShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseOrderList where course equals to (courseId + 1)
        defaultCourseOrderShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllCourseOrdersByPlanIsEqualToSomething() throws Exception {
        SystemPlan plan;
        if (TestUtil.findAll(em, SystemPlan.class).isEmpty()) {
            courseOrderRepository.saveAndFlush(courseOrder);
            plan = SystemPlanResourceIT.createEntity();
        } else {
            plan = TestUtil.findAll(em, SystemPlan.class).getFirst();
        }
        em.persist(plan);
        em.flush();
        courseOrder.setPlan(plan);
        courseOrderRepository.saveAndFlush(courseOrder);
        Long planId = plan.getId();
        // Get all the courseOrderList where plan equals to planId
        defaultCourseOrderShouldBeFound("planId.equals=" + planId);

        // Get all the courseOrderList where plan equals to (planId + 1)
        defaultCourseOrderShouldNotBeFound("planId.equals=" + (planId + 1));
    }

    @Test
    @Transactional
    void getAllCourseOrdersByCouponIsEqualToSomething() throws Exception {
        Coupon coupon;
        if (TestUtil.findAll(em, Coupon.class).isEmpty()) {
            courseOrderRepository.saveAndFlush(courseOrder);
            coupon = CouponResourceIT.createEntity();
        } else {
            coupon = TestUtil.findAll(em, Coupon.class).getFirst();
        }
        em.persist(coupon);
        em.flush();
        courseOrder.setCoupon(coupon);
        courseOrderRepository.saveAndFlush(courseOrder);
        Long couponId = coupon.getId();
        // Get all the courseOrderList where coupon equals to couponId
        defaultCourseOrderShouldBeFound("couponId.equals=" + couponId);

        // Get all the courseOrderList where coupon equals to (couponId + 1)
        defaultCourseOrderShouldNotBeFound("couponId.equals=" + (couponId + 1));
    }

    private void defaultCourseOrderFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseOrderShouldBeFound(shouldBeFound);
        defaultCourseOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseOrderShouldBeFound(String filter) throws Exception {
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].originalPrice").value(hasItem(sameNumber(DEFAULT_ORIGINAL_PRICE))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
            .andExpect(jsonPath("$.[*].revenueSplitRate").value(hasItem(DEFAULT_REVENUE_SPLIT_RATE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].rowVersion").value(hasItem(DEFAULT_ROW_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseOrderShouldNotBeFound(String filter) throws Exception {
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourseOrder() throws Exception {
        // Get the courseOrder
        restCourseOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseOrder() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseOrder
        CourseOrder updatedCourseOrder = courseOrderRepository.findById(courseOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseOrder are not directly saved in db
        em.detach(updatedCourseOrder);
        updatedCourseOrder
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .finalPrice(UPDATED_FINAL_PRICE)
            .revenueSplitRate(UPDATED_REVENUE_SPLIT_RATE)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .rowVersion(UPDATED_ROW_VERSION)
            .createdAt(UPDATED_CREATED_AT);
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(updatedCourseOrder);

        restCourseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseOrderToMatchAllProperties(updatedCourseOrder);
    }

    @Test
    @Transactional
    void putNonExistingCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseOrder using partial update
        CourseOrder partialUpdatedCourseOrder = new CourseOrder();
        partialUpdatedCourseOrder.setId(courseOrder.getId());

        partialUpdatedCourseOrder
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .finalPrice(UPDATED_FINAL_PRICE)
            .revenueSplitRate(UPDATED_REVENUE_SPLIT_RATE)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY);

        restCourseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseOrder))
            )
            .andExpect(status().isOk());

        // Validate the CourseOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseOrder, courseOrder),
            getPersistedCourseOrder(courseOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseOrder using partial update
        CourseOrder partialUpdatedCourseOrder = new CourseOrder();
        partialUpdatedCourseOrder.setId(courseOrder.getId());

        partialUpdatedCourseOrder
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .finalPrice(UPDATED_FINAL_PRICE)
            .revenueSplitRate(UPDATED_REVENUE_SPLIT_RATE)
            .status(UPDATED_STATUS)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .rowVersion(UPDATED_ROW_VERSION)
            .createdAt(UPDATED_CREATED_AT);

        restCourseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseOrder))
            )
            .andExpect(status().isOk());

        // Validate the CourseOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseOrderUpdatableFieldsEquals(partialUpdatedCourseOrder, getPersistedCourseOrder(partialUpdatedCourseOrder));
    }

    @Test
    @Transactional
    void patchNonExistingCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseOrder.setId(longCount.incrementAndGet());

        // Create the CourseOrder
        CourseOrderDTO courseOrderDTO = courseOrderMapper.toDto(courseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseOrder() throws Exception {
        // Initialize the database
        insertedCourseOrder = courseOrderRepository.saveAndFlush(courseOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseOrder
        restCourseOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseOrderRepository.count();
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

    protected CourseOrder getPersistedCourseOrder(CourseOrder courseOrder) {
        return courseOrderRepository.findById(courseOrder.getId()).orElseThrow();
    }

    protected void assertPersistedCourseOrderToMatchAllProperties(CourseOrder expectedCourseOrder) {
        assertCourseOrderAllPropertiesEquals(expectedCourseOrder, getPersistedCourseOrder(expectedCourseOrder));
    }

    protected void assertPersistedCourseOrderToMatchUpdatableProperties(CourseOrder expectedCourseOrder) {
        assertCourseOrderAllUpdatablePropertiesEquals(expectedCourseOrder, getPersistedCourseOrder(expectedCourseOrder));
    }
}
