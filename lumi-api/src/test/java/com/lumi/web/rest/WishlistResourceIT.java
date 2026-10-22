package com.lumi.web.rest;

import static com.lumi.domain.WishlistAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Course;
import com.lumi.domain.User;
import com.lumi.domain.Wishlist;
import com.lumi.repository.UserRepository;
import com.lumi.repository.WishlistRepository;
import com.lumi.service.dto.WishlistDTO;
import com.lumi.service.mapper.WishlistMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link WishlistResource} REST controller.
 *
 * <p>Wishlists are owned by the authenticated user, so the fixtures always attach the current
 * user ({@code "user"}) as the student. A second seeded user ({@code "admin"}) is used to prove
 * that users cannot see or delete each other's records.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(username = "user")
class WishlistResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String USER_LOGIN = "user";
    private static final String OTHER_LOGIN = "admin";

    private static final String ENTITY_API_URL = "/api/wishlists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistMapper wishlistMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishlistMockMvc;

    private Wishlist wishlist;

    private Wishlist insertedWishlist;

    /**
     * Create an entity for this test. The wishlist is owned by the current (mock) user.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createEntity(EntityManager em) {
        Wishlist wishlist = new Wishlist().createdAt(DEFAULT_CREATED_AT);
        wishlist.setStudent(currentUser(em));
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        wishlist.setCourse(course);
        return wishlist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createUpdatedEntity(EntityManager em) {
        Wishlist updatedWishlist = new Wishlist().createdAt(UPDATED_CREATED_AT);
        updatedWishlist.setStudent(currentUser(em));
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).getFirst();
        }
        updatedWishlist.setCourse(course);
        return updatedWishlist;
    }

    private static User currentUser(EntityManager em) {
        return TestUtil.findAll(em, User.class)
            .stream()
            .filter(u -> USER_LOGIN.equals(u.getLogin()))
            .findFirst()
            .orElseGet(() -> {
                User user = UserResourceIT.createEntity();
                user.setLogin(USER_LOGIN);
                user.setEmail(user.getLogin() + "@localhost");
                em.persist(user);
                em.flush();
                return user;
            });
    }

    @BeforeEach
    void initTest() {
        wishlist = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWishlist != null) {
            wishlistRepository.deleteById(insertedWishlist.getId());
            insertedWishlist = null;
        }
    }

    @Test
    @Transactional
    void createWishlist() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);
        var returnedWishlistDTO = om.readValue(
            restWishlistMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-lumi-alert", "lumi.wishlist.created"))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WishlistDTO.class
        );

        // Validate the Wishlist in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWishlist = wishlistMapper.toEntity(returnedWishlistDTO);
        assertWishlistUpdatableFieldsEquals(returnedWishlist, getPersistedWishlist(returnedWishlist));

        insertedWishlist = returnedWishlist;
    }

    @Test
    @Transactional
    void createWishlistWithExistingId() throws Exception {
        // Create the Wishlist with an existing ID
        wishlist.setId(1L);
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createWishlistCannotSpoofStudent() throws Exception {
        User otherUser = userRepository.findOneByLogin(OTHER_LOGIN).orElseThrow();
        wishlist.setStudent(otherUser);
        long databaseSizeBeforeCreate = getRepositoryCount();

        // Client attempts to claim ownership by sending another user's id.
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);
        var returnedWishlistDTO = om.readValue(
            restWishlistMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WishlistDTO.class
        );

        // The persisted record must belong to the authenticated user, not the spoofed id.
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        User currentUser = userRepository.findOneByLogin(USER_LOGIN).orElseThrow();
        Wishlist persisted = getPersistedWishlist(wishlistMapper.toEntity(returnedWishlistDTO));
        assertThat(persisted.getStudent().getLogin()).isEqualTo(USER_LOGIN);
        assertThat(persisted.getStudent().getId()).isEqualTo(currentUser.getId());

        insertedWishlist = persisted;
    }

    @Test
    @Transactional
    void createWishlistDuplicateBlocked() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();

        // First attempt is valid
        restWishlistMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistMapper.toDto(wishlist)))
            )
            .andExpect(status().isCreated());

        // Second attempt for the same course must be rejected
        WishlistDTO duplicate = wishlistMapper.toDto(wishlist);
        duplicate.setId(null);
        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(duplicate)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void createWishlistWithNonExistingCourse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);
        wishlistDTO.getCourse().setId(Long.MAX_VALUE);

        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    // Error integration (C2.5.2 / C2.5.3): the BadRequestAlertException response must expose both the
    // JHipster error headers (X-lumi-error = error.<key>, X-lumi-params = entity name) and a coherent
    // Problem Detail body (message = error.<key>, params = entity name). Spring's
    // ResponseEntityExceptionHandler drops the headers unless ExceptionTranslator.handleErrorResponseException
    // is overridden (C2.5.3) to flow buildHeaders(ex) through. The title is overridden to "Bad Request",
    // so clients localize from `message`, not the title.
    @Test
    @Transactional
    void createWishlistDuplicateReturnsErrorContract() throws Exception {
        // First attempt is valid
        restWishlistMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistMapper.toDto(wishlist)))
            )
            .andExpect(status().isCreated());

        // Second attempt for the same course must be rejected with the error headers and body.
        WishlistDTO duplicate = wishlistMapper.toDto(wishlist);
        duplicate.setId(null);
        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(duplicate)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(header().string("X-lumi-error", "error.duplicate"))
            .andExpect(header().string("X-lumi-params", "wishlist"))
            .andExpect(jsonPath("$.message").value("error.duplicate"))
            .andExpect(jsonPath("$.params").value("wishlist"));
    }

    @Test
    @Transactional
    void createWishlistWithNonExistingCourseReturnsErrorContract() throws Exception {
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);
        wishlistDTO.getCourse().setId(Long.MAX_VALUE);

        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(header().string("X-lumi-error", "error.coursenotfound"))
            .andExpect(header().string("X-lumi-params", "wishlist"))
            .andExpect(jsonPath("$.message").value("error.coursenotfound"))
            .andExpect(jsonPath("$.params").value("wishlist"));
    }

    @Test
    @Transactional
    void updateNonExistingWishlistReturnsIdNotFoundErrorContract() throws Exception {
        // A PUT on a non-existent (or not-owned) id surfaces the idnotfound error.
        wishlist.setId(longCount.incrementAndGet());
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(header().string("X-lumi-error", "error.idnotfound"))
            .andExpect(header().string("X-lumi-params", "wishlist"))
            .andExpect(jsonPath("$.message").value("error.idnotfound"))
            .andExpect(jsonPath("$.params").value("wishlist"));
    }

    @Test
    @Transactional
    void getAllWishlists() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getWishlist() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        // Get the wishlist
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL_ID, wishlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishlist.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getWishlistsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        Long id = wishlist.getId();

        defaultWishlistFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWishlistFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWishlistFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWishlistsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList where createdAt equals to
        defaultWishlistFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWishlistsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList where createdAt in
        defaultWishlistFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWishlistsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList where createdAt is not null
        defaultWishlistFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWishlistsByStudentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        Long currentUserId = userRepository.findOneByLogin(USER_LOGIN).orElseThrow().getId();
        // The list is always scoped to the current user: querying by the own id finds the record.
        defaultWishlistShouldBeFound("studentId.equals=" + currentUserId);
    }

    @Test
    @Transactional
    void getAllWishlistsByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        Long courseId = wishlist.getCourse().getId();
        // Get all the wishlistList where course equals to courseId
        defaultWishlistShouldBeFound("courseId.equals=" + courseId);

        // Get all the wishlistList where course equals to (courseId + 1)
        defaultWishlistShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllWishlistsDoesNotLeakOtherUsers() throws Exception {
        // A wishlist owned by the current user
        User currentUser = userRepository.findOneByLogin(USER_LOGIN).orElseThrow();
        User otherUser = userRepository.findOneByLogin(OTHER_LOGIN).orElseThrow();

        Course courseA = CourseResourceIT.createEntity(em);
        em.persist(courseA);
        em.flush();
        Course courseB = CourseResourceIT.createEntity(em);
        em.persist(courseB);
        em.flush();

        Wishlist mine = new Wishlist().createdAt(DEFAULT_CREATED_AT).student(currentUser).course(courseA);
        Wishlist other = new Wishlist().createdAt(UPDATED_CREATED_AT).student(otherUser).course(courseB);
        wishlistRepository.saveAndFlush(mine);
        wishlistRepository.saveAndFlush(other);
        insertedWishlist = mine;

        // The list only exposes the current user's records, never another user's.
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mine.getId().intValue())))
            .andExpect(jsonPath("$.[*].id").value(not(hasItem(other.getId().intValue()))));

        // Count is also scoped to the current user.
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "/count"))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));

        // The current user cannot read another user's wishlist (404, existence is not disclosed).
        restWishlistMockMvc.perform(get(ENTITY_API_URL_ID, other.getId())).andExpect(status().isNotFound());

        // The current user cannot delete another user's wishlist (404, nothing is deleted).
        restWishlistMockMvc
            .perform(delete(ENTITY_API_URL_ID, other.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
        assertThat(wishlistRepository.findById(other.getId())).isPresent();

        // The current user can read and then delete their own wishlist.
        restWishlistMockMvc.perform(get(ENTITY_API_URL_ID, mine.getId())).andExpect(status().isOk());
        restWishlistMockMvc
            .perform(delete(ENTITY_API_URL_ID, mine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void getNonExistingWishlist() throws Exception {
        // Get the wishlist
        restWishlistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWishlist() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishlist
        Wishlist updatedWishlist = wishlistRepository.findById(wishlist.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWishlist are not directly saved in db
        em.detach(updatedWishlist);
        updatedWishlist.createdAt(UPDATED_CREATED_AT);
        WishlistDTO wishlistDTO = wishlistMapper.toDto(updatedWishlist);

        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        // createdAt is server-managed and preserved on update; ownership is the current user.
        Wishlist persisted = getPersistedWishlist(updatedWishlist);
        assertThat(persisted.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(persisted.getStudent().getLogin()).isEqualTo(USER_LOGIN);
    }

    @Test
    @Transactional
    void putNonExistingWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWishlistWithPatch() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishlist using partial update; a client attempt to change createdAt is ignored.
        Wishlist partialUpdatedWishlist = new Wishlist();
        partialUpdatedWishlist.setId(wishlist.getId());
        partialUpdatedWishlist.createdAt(UPDATED_CREATED_AT);

        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWishlist))
            )
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        Wishlist persisted = getPersistedWishlist(wishlist);
        assertThat(persisted.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(persisted.getStudent().getLogin()).isEqualTo(USER_LOGIN);
    }

    @Test
    @Transactional
    void patchNonExistingWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishlistDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishlist.setId(longCount.incrementAndGet());

        // Create the Wishlist
        WishlistDTO wishlistDTO = wishlistMapper.toDto(wishlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(wishlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wishlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWishlist() throws Exception {
        // Initialize the database
        insertedWishlist = wishlistRepository.saveAndFlush(wishlist);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the wishlist
        restWishlistMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishlist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wishlistRepository.count();
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

    protected Wishlist getPersistedWishlist(Wishlist wishlist) {
        return wishlistRepository.findById(wishlist.getId()).orElseThrow();
    }

    private void defaultWishlistFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWishlistShouldBeFound(shouldBeFound);
        defaultWishlistShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWishlistShouldBeFound(String filter) throws Exception {
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWishlistShouldNotBeFound(String filter) throws Exception {
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    private static Long returnedId(MockMvc mockMvc) {
        throw new UnsupportedOperationException("Placeholder; use the DTO returned from the create call instead.");
    }
}
