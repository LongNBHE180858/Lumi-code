package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.lumi.domain.Authority;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class InstructorAccessServiceTest {

    @Mock
    private UserRepository users;

    @Mock
    private UserProfileRepository profiles;

    private InstructorAccessService access;
    private User user;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("instructor", "n/a"));
        access = new InstructorAccessService(users, profiles);
        user = new User();
        user.setId(42L);
        user.setLogin("instructor");
        user.setActivated(true);
        user.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        profile = new UserProfile().user(user);
        when(users.findOneByLogin("instructor")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void pendingAndRejectedApplicationsCannotUseInstructorFeatures() {
        when(profiles.findOneByUserId(42L)).thenReturn(Optional.of(profile));
        profile.setInstructorStatus(InstructorApplicationStatus.PENDING);
        assertThat(access.canUseInstructorFeatures()).isFalse();
        assertThatThrownBy(access::requireActiveInstructor).isInstanceOf(AccessDeniedException.class);
        profile.setInstructorStatus(InstructorApplicationStatus.REJECTED);
        assertThat(access.canUseInstructorFeatures()).isFalse();
    }

    @Test
    void approvedInstructorCanUseFeatures() {
        when(profiles.findOneByUserId(42L)).thenReturn(Optional.of(profile));
        profile.setInstructorStatus(InstructorApplicationStatus.ACTIVE);
        assertThat(access.requireActiveInstructor()).isSameAs(user);
    }

    @Test
    void roleWithoutProfileIsInsufficient() {
        when(profiles.findOneByUserId(42L)).thenReturn(Optional.empty());
        assertThat(access.canUseInstructorFeatures()).isFalse();
    }

    @Test
    void adminRetainsAccess() {
        user.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.ADMIN)));
        assertThat(access.canUseInstructorFeatures()).isTrue();
    }

    @Test
    void inactiveAccountIsDenied() {
        user.setActivated(false);
        assertThat(access.canUseInstructorFeatures()).isFalse();
    }
}
