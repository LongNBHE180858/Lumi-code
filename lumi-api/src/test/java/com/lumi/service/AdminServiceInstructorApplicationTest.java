package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Authority;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminServiceInstructorApplicationTest {

    @Mock
    private UserRepository users;

    @Mock
    private UserProfileRepository profiles;

    @Mock
    private AuthorityRepository authorities;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    private UserProfile application;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("reviewer", "n/a"));
        User user = new User();
        user.setId(42L);
        user.setLogin("applicant");
        user.setActivated(true);
        user.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        application = new UserProfile().user(user).instructorStatus(InstructorApplicationStatus.PENDING);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void stubSavedApplication() {
        when(profiles.findInstructorApplicationForUpdate(42L)).thenReturn(Optional.of(application));
        when(profiles.saveAndFlush(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void approvalRecordsDecisionAndClearsReason() {
        application.setInstructorRejectionReason("old reason");
        stubSavedApplication();

        var result = adminService.approveInstructorApplication(42L);

        assertThat(result.status()).isEqualTo(InstructorApplicationStatus.ACTIVE);
        assertThat(result.reviewedAt()).isNotNull();
        assertThat(result.rejectionReason()).isNull();
    }

    @Test
    void rejectionRequiresReasonAndRecordsDecision() {
        assertThatThrownBy(() -> adminService.rejectInstructorApplication(42L, "  "))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

        stubSavedApplication();
        var result = adminService.rejectInstructorApplication(42L, "  Missing qualifications  ");

        assertThat(result.status()).isEqualTo(InstructorApplicationStatus.REJECTED);
        assertThat(result.rejectionReason()).isEqualTo("Missing qualifications");
    }

    @Test
    void reviewedApplicationCannotBeReviewedAgain() {
        application.setInstructorStatus(InstructorApplicationStatus.ACTIVE);
        when(profiles.findInstructorApplicationForUpdate(42L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> adminService.rejectInstructorApplication(42L, "No"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void unverifiedEmailCannotBeApproved() {
        application.getUser().setActivated(false);
        when(profiles.findInstructorApplicationForUpdate(42L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> adminService.approveInstructorApplication(42L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void rejectsUnsupportedListStatus() {
        assertThatThrownBy(() ->
            adminService.getInstructorApplications(InstructorApplicationStatus.DRAFT, org.springframework.data.domain.Pageable.unpaged())
        )
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void cannotReviewNonInstructorProfile() {
        application.getUser().setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.USER)));
        when(profiles.findInstructorApplicationForUpdate(42L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> adminService.approveInstructorApplication(42L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(profiles, never()).saveAndFlush(any(UserProfile.class));
    }

    @Test
    void rejectsReasonLongerThanLimit() {
        assertThatThrownBy(() -> adminService.rejectInstructorApplication(42L, "x".repeat(2001)))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(profiles, never()).findInstructorApplicationForUpdate(42L);
    }

    @Test
    void decisionRequiresReviewerIdentity() {
        SecurityContextHolder.clearContext();
        when(profiles.findInstructorApplicationForUpdate(42L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> adminService.approveInstructorApplication(42L)).isInstanceOf(AccessDeniedException.class);
        verify(profiles, never()).saveAndFlush(any(UserProfile.class));
    }
}
