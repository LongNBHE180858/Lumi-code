package com.lumi.web.rest;

import static com.lumi.domain.AuthSessionAsserts.*;
import static com.lumi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.AuthSessionService;
import com.lumi.service.dto.AuthSessionDTO;
import com.lumi.service.mapper.AuthSessionMapper;
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
 * Integration tests for the {@link AuthSessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AuthSessionResourceIT {

    private static final String DEFAULT_DEVICE_INFO = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_REFRESH_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_REFRESH_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Boolean DEFAULT_IS_REVOKED = false;
    private static final Boolean UPDATED_IS_REVOKED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final String DEFAULT_REFRESH_TOKEN_HASH = "AAAAAAAAAA";
    private static final String UPDATED_REFRESH_TOKEN_HASH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/auth-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuthSessionRepository authSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AuthSessionRepository authSessionRepositoryMock;

    @Autowired
    private AuthSessionMapper authSessionMapper;

    @Mock
    private AuthSessionService authSessionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthSessionMockMvc;

    private AuthSession authSession;

    private AuthSession insertedAuthSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthSession createEntity(EntityManager em) {
        AuthSession authSession = new AuthSession()
            .deviceInfo(DEFAULT_DEVICE_INFO)
            .refreshToken(DEFAULT_REFRESH_TOKEN)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .isRevoked(DEFAULT_IS_REVOKED)
            .createdAt(DEFAULT_CREATED_AT)
            .refreshTokenHash(DEFAULT_REFRESH_TOKEN_HASH);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        authSession.setUser(user);
        return authSession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthSession createUpdatedEntity(EntityManager em) {
        AuthSession updatedAuthSession = new AuthSession()
            .deviceInfo(UPDATED_DEVICE_INFO)
            .refreshToken(UPDATED_REFRESH_TOKEN)
            .expiresAt(UPDATED_EXPIRES_AT)
            .isRevoked(UPDATED_IS_REVOKED)
            .createdAt(UPDATED_CREATED_AT)
            .refreshTokenHash(UPDATED_REFRESH_TOKEN_HASH);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAuthSession.setUser(user);
        return updatedAuthSession;
    }

    @BeforeEach
    void initTest() {
        authSession = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuthSession != null) {
            authSessionRepository.delete(insertedAuthSession);
            insertedAuthSession = null;
        }
    }

    @Test
    @Transactional
    void createAuthSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);
        var returnedAuthSessionDTO = om.readValue(
            restAuthSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authSessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuthSessionDTO.class
        );

        // Validate the AuthSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuthSession = authSessionMapper.toEntity(returnedAuthSessionDTO);
        assertAuthSessionUpdatableFieldsEquals(returnedAuthSession, getPersistedAuthSession(returnedAuthSession));

        insertedAuthSession = returnedAuthSession;
    }

    @Test
    @Transactional
    void createAuthSessionWithExistingId() throws Exception {
        // Create the AuthSession with an existing ID
        authSession.setId(1L);
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authSessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        authSession.setExpiresAt(null);

        // Create the AuthSession, which fails.
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        restAuthSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAuthSessions() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceInfo").value(hasItem(DEFAULT_DEVICE_INFO)))
            .andExpect(jsonPath("$.[*].refreshToken").value(hasItem(DEFAULT_REFRESH_TOKEN)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].isRevoked").value(hasItem(DEFAULT_IS_REVOKED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].refreshTokenHash").value(hasItem(DEFAULT_REFRESH_TOKEN_HASH)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuthSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(authSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAuthSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(authSessionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuthSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(authSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAuthSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(authSessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAuthSession() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get the authSession
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, authSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(authSession.getId().intValue()))
            .andExpect(jsonPath("$.deviceInfo").value(DEFAULT_DEVICE_INFO))
            .andExpect(jsonPath("$.refreshToken").value(DEFAULT_REFRESH_TOKEN))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.isRevoked").value(DEFAULT_IS_REVOKED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.refreshTokenHash").value(DEFAULT_REFRESH_TOKEN_HASH));
    }

    @Test
    @Transactional
    void getAuthSessionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        Long id = authSession.getId();

        defaultAuthSessionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuthSessionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuthSessionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByDeviceInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where deviceInfo equals to
        defaultAuthSessionFiltering("deviceInfo.equals=" + DEFAULT_DEVICE_INFO, "deviceInfo.equals=" + UPDATED_DEVICE_INFO);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByDeviceInfoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where deviceInfo in
        defaultAuthSessionFiltering(
            "deviceInfo.in=" + DEFAULT_DEVICE_INFO + "," + UPDATED_DEVICE_INFO,
            "deviceInfo.in=" + UPDATED_DEVICE_INFO
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByDeviceInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where deviceInfo is not null
        defaultAuthSessionFiltering("deviceInfo.specified=true", "deviceInfo.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByDeviceInfoContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where deviceInfo contains
        defaultAuthSessionFiltering("deviceInfo.contains=" + DEFAULT_DEVICE_INFO, "deviceInfo.contains=" + UPDATED_DEVICE_INFO);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByDeviceInfoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where deviceInfo does not contain
        defaultAuthSessionFiltering("deviceInfo.doesNotContain=" + UPDATED_DEVICE_INFO, "deviceInfo.doesNotContain=" + DEFAULT_DEVICE_INFO);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshToken equals to
        defaultAuthSessionFiltering("refreshToken.equals=" + DEFAULT_REFRESH_TOKEN, "refreshToken.equals=" + UPDATED_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshToken in
        defaultAuthSessionFiltering(
            "refreshToken.in=" + DEFAULT_REFRESH_TOKEN + "," + UPDATED_REFRESH_TOKEN,
            "refreshToken.in=" + UPDATED_REFRESH_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshToken is not null
        defaultAuthSessionFiltering("refreshToken.specified=true", "refreshToken.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshToken contains
        defaultAuthSessionFiltering("refreshToken.contains=" + DEFAULT_REFRESH_TOKEN, "refreshToken.contains=" + UPDATED_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshToken does not contain
        defaultAuthSessionFiltering(
            "refreshToken.doesNotContain=" + UPDATED_REFRESH_TOKEN,
            "refreshToken.doesNotContain=" + DEFAULT_REFRESH_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where expiresAt equals to
        defaultAuthSessionFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where expiresAt in
        defaultAuthSessionFiltering("expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT, "expiresAt.in=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where expiresAt is not null
        defaultAuthSessionFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByIsRevokedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where isRevoked equals to
        defaultAuthSessionFiltering("isRevoked.equals=" + DEFAULT_IS_REVOKED, "isRevoked.equals=" + UPDATED_IS_REVOKED);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByIsRevokedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where isRevoked in
        defaultAuthSessionFiltering("isRevoked.in=" + DEFAULT_IS_REVOKED + "," + UPDATED_IS_REVOKED, "isRevoked.in=" + UPDATED_IS_REVOKED);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByIsRevokedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where isRevoked is not null
        defaultAuthSessionFiltering("isRevoked.specified=true", "isRevoked.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where createdAt equals to
        defaultAuthSessionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where createdAt in
        defaultAuthSessionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthSessionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where createdAt is not null
        defaultAuthSessionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshTokenHash equals to
        defaultAuthSessionFiltering(
            "refreshTokenHash.equals=" + DEFAULT_REFRESH_TOKEN_HASH,
            "refreshTokenHash.equals=" + UPDATED_REFRESH_TOKEN_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshTokenHash in
        defaultAuthSessionFiltering(
            "refreshTokenHash.in=" + DEFAULT_REFRESH_TOKEN_HASH + "," + UPDATED_REFRESH_TOKEN_HASH,
            "refreshTokenHash.in=" + UPDATED_REFRESH_TOKEN_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshTokenHash is not null
        defaultAuthSessionFiltering("refreshTokenHash.specified=true", "refreshTokenHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshTokenHash contains
        defaultAuthSessionFiltering(
            "refreshTokenHash.contains=" + DEFAULT_REFRESH_TOKEN_HASH,
            "refreshTokenHash.contains=" + UPDATED_REFRESH_TOKEN_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByRefreshTokenHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        // Get all the authSessionList where refreshTokenHash does not contain
        defaultAuthSessionFiltering(
            "refreshTokenHash.doesNotContain=" + UPDATED_REFRESH_TOKEN_HASH,
            "refreshTokenHash.doesNotContain=" + DEFAULT_REFRESH_TOKEN_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuthSessionsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            authSessionRepository.saveAndFlush(authSession);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).getFirst();
        }
        em.persist(user);
        em.flush();
        authSession.setUser(user);
        authSessionRepository.saveAndFlush(authSession);
        Long userId = user.getId();
        // Get all the authSessionList where user equals to userId
        defaultAuthSessionShouldBeFound("userId.equals=" + userId);

        // Get all the authSessionList where user equals to (userId + 1)
        defaultAuthSessionShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultAuthSessionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuthSessionShouldBeFound(shouldBeFound);
        defaultAuthSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuthSessionShouldBeFound(String filter) throws Exception {
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceInfo").value(hasItem(DEFAULT_DEVICE_INFO)))
            .andExpect(jsonPath("$.[*].refreshToken").value(hasItem(DEFAULT_REFRESH_TOKEN)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].isRevoked").value(hasItem(DEFAULT_IS_REVOKED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].refreshTokenHash").value(hasItem(DEFAULT_REFRESH_TOKEN_HASH)));

        // Check, that the count call also returns 1
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuthSessionShouldNotBeFound(String filter) throws Exception {
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuthSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuthSession() throws Exception {
        // Get the authSession
        restAuthSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuthSession() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authSession
        AuthSession updatedAuthSession = authSessionRepository.findById(authSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuthSession are not directly saved in db
        em.detach(updatedAuthSession);
        updatedAuthSession
            .deviceInfo(UPDATED_DEVICE_INFO)
            .refreshToken(UPDATED_REFRESH_TOKEN)
            .expiresAt(UPDATED_EXPIRES_AT)
            .isRevoked(UPDATED_IS_REVOKED)
            .createdAt(UPDATED_CREATED_AT)
            .refreshTokenHash(UPDATED_REFRESH_TOKEN_HASH);
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(updatedAuthSession);

        restAuthSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuthSessionToMatchAllProperties(updatedAuthSession);
    }

    @Test
    @Transactional
    void putNonExistingAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuthSessionWithPatch() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authSession using partial update
        AuthSession partialUpdatedAuthSession = new AuthSession();
        partialUpdatedAuthSession.setId(authSession.getId());

        partialUpdatedAuthSession
            .deviceInfo(UPDATED_DEVICE_INFO)
            .expiresAt(UPDATED_EXPIRES_AT)
            .refreshTokenHash(UPDATED_REFRESH_TOKEN_HASH);

        restAuthSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthSession))
            )
            .andExpect(status().isOk());

        // Validate the AuthSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuthSession, authSession),
            getPersistedAuthSession(authSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateAuthSessionWithPatch() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authSession using partial update
        AuthSession partialUpdatedAuthSession = new AuthSession();
        partialUpdatedAuthSession.setId(authSession.getId());

        partialUpdatedAuthSession
            .deviceInfo(UPDATED_DEVICE_INFO)
            .refreshToken(UPDATED_REFRESH_TOKEN)
            .expiresAt(UPDATED_EXPIRES_AT)
            .isRevoked(UPDATED_IS_REVOKED)
            .createdAt(UPDATED_CREATED_AT)
            .refreshTokenHash(UPDATED_REFRESH_TOKEN_HASH);

        restAuthSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthSession))
            )
            .andExpect(status().isOk());

        // Validate the AuthSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthSessionUpdatableFieldsEquals(partialUpdatedAuthSession, getPersistedAuthSession(partialUpdatedAuthSession));
    }

    @Test
    @Transactional
    void patchNonExistingAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, authSessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuthSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authSession.setId(longCount.incrementAndGet());

        // Create the AuthSession
        AuthSessionDTO authSessionDTO = authSessionMapper.toDto(authSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(authSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuthSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuthSession() throws Exception {
        // Initialize the database
        insertedAuthSession = authSessionRepository.saveAndFlush(authSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the authSession
        restAuthSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, authSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return authSessionRepository.count();
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

    protected AuthSession getPersistedAuthSession(AuthSession authSession) {
        return authSessionRepository.findById(authSession.getId()).orElseThrow();
    }

    protected void assertPersistedAuthSessionToMatchAllProperties(AuthSession expectedAuthSession) {
        assertAuthSessionAllPropertiesEquals(expectedAuthSession, getPersistedAuthSession(expectedAuthSession));
    }

    protected void assertPersistedAuthSessionToMatchUpdatableProperties(AuthSession expectedAuthSession) {
        assertAuthSessionAllUpdatablePropertiesEquals(expectedAuthSession, getPersistedAuthSession(expectedAuthSession));
    }
}
