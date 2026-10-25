package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.User;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.AdminService;
import com.lumi.service.dto.AdminUserDTO;
import com.lumi.service.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import java.util.*;
import java.util.function.Consumer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link AdminResource} user endpoints.
 */
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
class UserResourceIT {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String DEFAULT_LASTNAME = "doe";
    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private static final String DEFAULT_LANGKEY = "en";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MockMvc restUserMockMvc;

    private User user;

    private Long numberOfUsers;

    /**
     * Create a User.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which has a required relationship to the User entity.
     */
    public static User createEntity() {
        User persistUser = new User();
        persistUser.setLogin(DEFAULT_LOGIN + RandomStringUtils.insecure().nextAlphabetic(5));
        persistUser.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        persistUser.setActivated(true);
        persistUser.setEmail(RandomStringUtils.insecure().nextAlphabetic(5) + DEFAULT_EMAIL);
        persistUser.setFirstName(DEFAULT_FIRSTNAME);
        persistUser.setLastName(DEFAULT_LASTNAME);
        persistUser.setImageUrl(DEFAULT_IMAGEURL);
        persistUser.setLangKey(DEFAULT_LANGKEY);
        return persistUser;
    }

    /**
     * Setups the database with one user.
     */
    public static User initTestUser() {
        User persistUser = createEntity();
        persistUser.setLogin(DEFAULT_LOGIN);
        persistUser.setEmail(DEFAULT_EMAIL);
        return persistUser;
    }

    @BeforeEach
    void initTest() {
        numberOfUsers = userRepository.count();
        user = initTestUser();
    }

    @AfterEach
    void cleanupAndCheck() {
        adminService.deleteUser(DEFAULT_LOGIN);
        adminService.deleteUser(user.getLogin());
        adminService.deleteUser("anotherlogin");
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);
        numberOfUsers = null;
        cacheManager
            .getCacheNames()
            .stream()
            .map(cacheName -> this.cacheManager.getCache(cacheName))
            .filter(Objects::nonNull)
            .forEach(Cache::invalidate);
    }

    @Test
    @Transactional
    void createUser() throws Exception {
        // Create the User
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.USER));

        var returnedUserDTO = om.readValue(
            restUserMockMvc
                .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AdminUserDTO.class
        );

        User convertedUser = userMapper.userDTOToUser(returnedUserDTO);
        // Validate the returned User
        assertThat(convertedUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(convertedUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(convertedUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(convertedUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(convertedUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(convertedUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
    }

    @Test
    @Transactional
    void adminCanAssignInstructorRole() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.INSTRUCTOR));

        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isCreated());

        Set<String> assignedRoles = new AdminUserDTO(
            userRepository.findOneWithAuthoritiesByLogin(DEFAULT_LOGIN).orElseThrow()
        ).getAuthorities();
        assertThat(assignedRoles).containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.INSTRUCTOR);
    }

    @Test
    @Transactional
    void adminCanAssignParentRole() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.PARENT));

        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isCreated());

        Set<String> assignedRoles = new AdminUserDTO(
            userRepository.findOneWithAuthoritiesByLogin(DEFAULT_LOGIN).orElseThrow()
        ).getAuthorities();
        assertThat(assignedRoles).containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.PARENT);
    }

    @Test
    @Transactional
    void adminCannotCreateAnotherAdmin() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByLogin(DEFAULT_LOGIN)).isEmpty();
    }

    @Test
    @Transactional
    void cannotAssignMultipleBusinessRoles() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.STUDENT, AuthoritiesConstants.INSTRUCTOR));

        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByLogin(DEFAULT_LOGIN)).isEmpty();
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.STUDENT)
    void studentCannotAssignRoles() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListAssignableRoles() throws Exception {
        restUserMockMvc
            .perform(get("/api/admin/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value(AuthoritiesConstants.INSTRUCTOR))
            .andExpect(jsonPath("$[1]").value(AuthoritiesConstants.STUDENT))
            .andExpect(jsonPath("$[2]").value(AuthoritiesConstants.PARENT))
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.INSTRUCTOR)
    void instructorCannotListAssignableRoles() throws Exception {
        restUserMockMvc.perform(get("/api/admin/roles")).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setId(DEFAULT_ID);
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.USER));

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(DEFAULT_LOGIN); // this login should already be used
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail("anothermail@localhost");
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.USER));

        // Create the User
        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin("anotherlogin");
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL); // this email should already be used
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.USER));

        // Create the User
        restUserMockMvc
            .perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the users
        restUserMockMvc
            .perform(get("/api/admin/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGEURL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANGKEY)));
    }

    @Test
    @Transactional
    void filterUsersByDisplayedRoleBeforePagination() throws Exception {
        userRepository.saveAndFlush(user);

        restUserMockMvc
            .perform(get("/api/admin/users?role=ROLE_STUDENT&size=1&sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)));

        restUserMockMvc
            .perform(get("/api/admin/users?role=ROLE_ADMIN&sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].login").value(not(hasItem(DEFAULT_LOGIN))));

        restUserMockMvc.perform(get("/api/admin/users?role=UNKNOWN")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void searchUsersByFullNameOrEmailBeforePagination() throws Exception {
        user.setFirstName("Lucia");
        user.setLastName("Nguyen");
        user.setEmail("lucia.unique@example.com");
        userRepository.saveAndFlush(user);

        User other = createEntity();
        other.setFirstName("Another");
        other.setLastName("Person");
        userRepository.saveAndFlush(other);

        restUserMockMvc
            .perform(get("/api/admin/users").param("search", "lUcIa nGuYeN").param("size", "1").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$.[0].login").value(user.getLogin()));

        restUserMockMvc
            .perform(get("/api/admin/users").param("search", "Nguyen Lucia"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$.[0].login").value(user.getLogin()));

        restUserMockMvc
            .perform(get("/api/admin/users").param("search", "UNIQUE@EXAMPLE").param("role", "ROLE_STUDENT"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$.[0].email").value("lucia.unique@example.com"));
    }

    @Test
    @Transactional
    void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(user.getLogin(), User.class)).isNull();

        // Get the user
        restUserMockMvc
            .perform(get("/api/admin/users/{login}", user.getLogin()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.login").value(user.getLogin()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGEURL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANGKEY));

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(user.getLogin(), User.class)).isNotNull();
    }

    @Test
    @Transactional
    void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/admin/users/unknown")).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void adminCanTemporarilyLockAndReactivateUser() throws Exception {
        userRepository.saveAndFlush(user);

        restUserMockMvc
            .perform(
                patch("/api/admin/users/{login}/activation", user.getLogin())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"activated\":false}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activated").value(false));
        assertThat(userRepository.findOneByLogin(user.getLogin()).orElseThrow().isActivated()).isFalse();

        restUserMockMvc
            .perform(
                patch("/api/admin/users/{login}/activation", user.getLogin())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"activated\":true}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activated").value(true));
        assertThat(userRepository.findOneByLogin(user.getLogin()).orElseThrow().isActivated()).isTrue();
    }

    @Test
    @Transactional
    void adminCannotLockAdminAccount() throws Exception {
        restUserMockMvc
            .perform(patch("/api/admin/users/admin/activation").contentType(MediaType.APPLICATION_JSON).content("{\"activated\":false}"))
            .andExpect(status().isBadRequest());
        assertThat(userRepository.findOneByLogin("admin").orElseThrow().isActivated()).isTrue();
    }

    @Test
    void activationRequestRequiresBooleanState() throws Exception {
        restUserMockMvc
            .perform(patch("/api/admin/users/{login}/activation", DEFAULT_LOGIN).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void adminCannotUpdateUsers() throws Exception {
        userRepository.saveAndFlush(user);
        AdminUserDTO request = new AdminUserDTO(user);
        request.setFirstName("changed");
        request.setActivated(false);
        request.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        restUserMockMvc
            .perform(put("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isMethodNotAllowed());
        restUserMockMvc
            .perform(
                put("/api/admin/users/{login}", user.getLogin())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isMethodNotAllowed());

        User unchanged = userRepository.findOneWithAuthoritiesByLogin(user.getLogin()).orElseThrow();
        assertThat(unchanged.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(unchanged.isActivated()).isTrue();
        assertThat(unchanged.getAuthorities()).noneMatch(authority -> AuthoritiesConstants.ADMIN.equals(authority.getName()));
    }

    @Test
    @Transactional
    void adminCannotDeleteUsers() throws Exception {
        userRepository.saveAndFlush(user);

        restUserMockMvc.perform(delete("/api/admin/users/{login}", user.getLogin())).andExpect(status().isMethodNotAllowed());

        assertThat(userRepository.findOneByLogin(user.getLogin())).isPresent();
    }

    @Test
    void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = new User();
        user1.setId(DEFAULT_ID);
        User user2 = new User();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}
