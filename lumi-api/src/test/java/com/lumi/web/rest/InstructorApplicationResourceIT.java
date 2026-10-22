package com.lumi.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "reviewer", authorities = AuthoritiesConstants.ADMIN)
class InstructorApplicationResourceIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository users;

    @Autowired
    private UserProfileRepository profiles;

    @Autowired
    private AuthorityRepository authorities;

    private Long createApplication(boolean activated) {
        User user = new User();
        user.setLogin("instructor-review-it");
        user.setPassword("x".repeat(60));
        user.setEmail("instructor-review-it@example.com");
        user.setActivated(activated);
        user.setAuthorities(
            Set.of(
                authorities.findById(AuthoritiesConstants.USER).orElseThrow(),
                authorities.findById(AuthoritiesConstants.INSTRUCTOR).orElseThrow()
            )
        );
        user = users.saveAndFlush(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setSchoolName("LUMI School");
        profile.setInstructorStatus(InstructorApplicationStatus.PENDING);
        profiles.saveAndFlush(profile);
        return user.getId();
    }

    @Test
    void listsAndShowsPendingApplications() throws Exception {
        Long userId = createApplication(true);

        mvc.perform(get("/api/admin/instructor-applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].userId", hasItem(userId.intValue())));
        mvc.perform(get("/api/admin/instructor-applications/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.schoolName").value("LUMI School"));
    }

    @Test
    void approvesOnceAndRecordsReviewer() throws Exception {
        Long userId = createApplication(true);

        mvc.perform(post("/api/admin/instructor-applications/{userId}/approve", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.reviewedBy").value("reviewer"))
            .andExpect(jsonPath("$.reviewedAt").exists());
        mvc.perform(post("/api/admin/instructor-applications/{userId}/approve", userId)).andExpect(status().isConflict());
    }

    @Test
    void rejectsWithReasonAndDoesNotAllowSecondDecision() throws Exception {
        Long userId = createApplication(true);

        mvc.perform(
            post("/api/admin/instructor-applications/{userId}/reject", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"Missing qualifications\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REJECTED"))
            .andExpect(jsonPath("$.rejectionReason").value("Missing qualifications"));
        mvc.perform(post("/api/admin/instructor-applications/{userId}/approve", userId)).andExpect(status().isConflict());
    }

    @Test
    void requiresReasonAndVerifiedEmail() throws Exception {
        Long userId = createApplication(false);

        mvc.perform(
            post("/api/admin/instructor-applications/{userId}/reject", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"   \"}")
        ).andExpect(status().isBadRequest());
        mvc.perform(post("/api/admin/instructor-applications/{userId}/approve", userId)).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void rejectsNonAdmin() throws Exception {
        Long userId = createApplication(true);
        mvc.perform(post("/api/admin/instructor-applications/{userId}/approve", userId)).andExpect(status().isForbidden());
    }
}
