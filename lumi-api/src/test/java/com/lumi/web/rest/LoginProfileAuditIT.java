package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.repository.AuditLogRepository;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.AuditLogService;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@IntegrationTest
@AutoConfigureMockMvc
class LoginProfileAuditIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoSpyBean
    private AuditLogService auditLogService;

    @Test
    void loginKeepsProfileAndRefreshWorkingWhileRecordingAudit() throws Exception {
        User user = createUser();
        String loginRequest = objectMapper.writeValueAsString(Map.of("username", user.getLogin(), "password", "secret"));

        MvcResult loginResult = mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequest)
                    .header("User-Agent", "Chrome/154.0.0.0 Windows")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.user.login").value(user.getLogin()))
            .andExpect(jsonPath("$.data.user.profile").exists())
            .andReturn();

        var loginData = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("data");
        String accessToken = loginData.get("access_token").asText();
        String refreshToken = loginData.get("refresh_token").asText();

        mockMvc
            .perform(get("/api/profile").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.login").value(user.getLogin()))
            .andExpect(jsonPath("$.data.firstName").value("Regression"));

        mockMvc
            .perform(get("/api/account").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value(user.getLogin()));

        mockMvc
            .perform(
                post("/api/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.access_token").isNotEmpty());

        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() ->
                assertThat(auditLogRepository.findAll())
                    .filteredOn(log -> user.getId().toString().equals(log.getTargetId()) && "LOGIN_SUCCESS".equals(log.getAction()))
                    .hasSize(1)
            );
    }

    @Test
    void incorrectPasswordDoesNotCreateLoginAudit() throws Exception {
        User user = createUser();

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("username", user.getLogin(), "password", "wrong")))
            )
            .andExpect(status().isUnauthorized());

        assertThat(auditLogRepository.findAll())
            .filteredOn(log -> user.getId().toString().equals(log.getTargetId()) && "LOGIN_SUCCESS".equals(log.getAction()))
            .isEmpty();
    }

    @Test
    void auditWriteFailureStillAllowsLoginAndProfile() throws Exception {
        User user = createUser();
        doThrow(new IllegalStateException("audit unavailable")).when(auditLogService).recordSuccessfulLogin(eq(user.getId()), any(), any());

        MvcResult result = mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("username", user.getLogin(), "password", "secret")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.user.login").value(user.getLogin()))
            .andReturn();

        String accessToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("access_token").asText();
        mockMvc
            .perform(get("/api/profile").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.login").value(user.getLogin()));

        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> verify(auditLogService).recordSuccessfulLogin(eq(user.getId()), any(), any()));
    }

    private User createUser() {
        String login = "audit-regression-" + UUID.randomUUID().toString().substring(0, 12);
        User user = new User();
        user.setLogin(login);
        user.setEmail(login + "@example.com");
        user.setFirstName("Regression");
        user.setLastName("User");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("secret"));
        user.getAuthorities().add(authorityRepository.findById(AuthoritiesConstants.USER).orElseThrow());
        user = userRepository.saveAndFlush(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setIs2faEnabled(false);
        userProfileRepository.saveAndFlush(profile);
        return user;
    }
}
