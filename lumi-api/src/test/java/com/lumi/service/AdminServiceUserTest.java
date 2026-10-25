package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Authority;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.Role;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.AdminUserDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminServiceUserTest {

    @Mock
    private UserRepository users;

    @Mock
    private AuthorityRepository authorities;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService service;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("reviewer", "n/a"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createsStudentWhenOnlyBasicRoleIsRequested() {
        AdminUserDTO request = new AdminUserDTO();
        request.setLogin("learner");
        request.setEmail("learner@example.com");
        request.setAuthorities(Set.of(AuthoritiesConstants.USER));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-password");
        when(authorities.findById(AuthoritiesConstants.USER)).thenReturn(Optional.of(new Authority().name(AuthoritiesConstants.USER)));
        when(authorities.findById(AuthoritiesConstants.STUDENT)).thenReturn(
            Optional.of(new Authority().name(AuthoritiesConstants.STUDENT))
        );
        when(users.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User created = service.createUser(request);

        assertThat(created.getAuthorities())
            .extracting(Authority::getName)
            .containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.STUDENT);
        assertThat(created.isActivated()).isTrue();
        assertThat(created.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void rejectsMoreThanOneBusinessRole() {
        AdminUserDTO request = new AdminUserDTO();
        request.setLogin("learner");
        request.setAuthorities(Set.of(AuthoritiesConstants.STUDENT, AuthoritiesConstants.INSTRUCTOR));

        assertThatThrownBy(() -> service.createUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void rejectsCreationOfAnotherAdmin() {
        AdminUserDTO request = new AdminUserDTO();
        request.setLogin("another-admin");
        request.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        assertThatThrownBy(() -> service.createUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void onlyOffersNonAdminRolesForCreation() {
        assertThat(service.getManagedRoles()).containsExactly(
            AuthoritiesConstants.INSTRUCTOR,
            AuthoritiesConstants.STUDENT,
            AuthoritiesConstants.PARENT
        );
    }

    @Test
    void cannotDeactivateAdminAccount() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "n/a"));
        User admin = user(1L, "admin", AuthoritiesConstants.ADMIN);
        AdminUserDTO request = new AdminUserDTO(admin);
        request.setActivated(false);
        when(users.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.updateUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void cannotRemoveAdminRole() {
        User admin = user(2L, "other-admin", AuthoritiesConstants.ADMIN);
        AdminUserDTO request = new AdminUserDTO(admin);
        request.setAuthorities(Set.of(AuthoritiesConstants.STUDENT));
        when(users.findById(2L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.updateUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void cannotPromoteAnotherAccountToAdmin() {
        User learner = user(3L, "learner", AuthoritiesConstants.STUDENT);
        AdminUserDTO request = new AdminUserDTO(learner);
        request.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));
        when(users.findById(3L)).thenReturn(Optional.of(learner));

        assertThatThrownBy(() -> service.updateUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void canTemporarilyLockAnOrdinaryAccount() {
        User learner = user(3L, "learner", AuthoritiesConstants.STUDENT);
        when(users.findOneWithAuthoritiesByLogin("learner")).thenReturn(Optional.of(learner));
        when(users.save(learner)).thenReturn(learner);

        AdminUserDTO updated = service.setUserActivation("learner", false);

        assertThat(updated.isActivated()).isFalse();
        verify(users).save(learner);
        verify(userService, times(2)).clearUserCaches(learner);
    }

    @Test
    void canReactivateAnOrdinaryAccount() {
        User learner = user(3L, "learner", AuthoritiesConstants.STUDENT);
        learner.setActivated(false);
        when(users.findOneWithAuthoritiesByLogin("learner")).thenReturn(Optional.of(learner));
        when(users.save(learner)).thenReturn(learner);

        assertThat(service.setUserActivation("learner", true).isActivated()).isTrue();
    }

    @Test
    void cannotChangeAdminActivation() {
        User admin = user(1L, "admin", AuthoritiesConstants.ADMIN);
        when(users.findOneWithAuthoritiesByLogin("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.setUserActivation("admin", false)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void cannotChangeMissingAccountActivation() {
        assertThatThrownBy(() -> service.setUserActivation("missing", false)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void searchesUsersWithRoleAndEscapedCaseInsensitiveTerm() {
        User learner = user(3L, "learner", AuthoritiesConstants.STUDENT);
        PageRequest pageable = PageRequest.of(0, 10);
        when(users.findAllByManagedRoleAndSearch("ROLE_STUDENT", "%lucia!%!_%", pageable)).thenReturn(new PageImpl<>(List.of(learner)));

        assertThat(service.getAllUsers(Role.ROLE_STUDENT, "  LuCiA%_  ", pageable).getContent())
            .extracting(AdminUserDTO::getLogin)
            .containsExactly("learner");
    }

    @Test
    void cannotDeleteAdminAccount() {
        User admin = user(2L, "other-admin", AuthoritiesConstants.ADMIN);
        when(users.findOneByLogin("other-admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.deleteUser("other-admin")).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).delete(any(User.class));
    }

    @Test
    void deletesAnUnprotectedAccount() {
        User learner = user(3L, "learner", AuthoritiesConstants.STUDENT);
        when(users.findOneByLogin("learner")).thenReturn(Optional.of(learner));

        service.deleteUser("learner");

        verify(users).delete(learner);
        verify(userService).clearUserCaches(learner);
    }

    private User user(Long id, String login, String role) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setActivated(true);
        user.setAuthorities(Set.of(new Authority().name(role)));
        return user;
    }
}
