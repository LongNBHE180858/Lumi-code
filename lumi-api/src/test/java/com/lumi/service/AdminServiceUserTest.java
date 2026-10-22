package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Authority;
import com.lumi.domain.User;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.AdminUserDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void cannotDeactivateOwnAdminAccount() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "n/a"));
        User admin = user(1L, "admin", AuthoritiesConstants.ADMIN);
        AdminUserDTO request = new AdminUserDTO(admin);
        request.setActivated(false);
        when(users.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.updateUser(request)).isInstanceOf(BadRequestAlertException.class);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void cannotDeleteLastAdmin() {
        User admin = user(2L, "other-admin", AuthoritiesConstants.ADMIN);
        when(users.findOneByLogin("other-admin")).thenReturn(Optional.of(admin));
        when(authorities.lockByName(AuthoritiesConstants.ADMIN)).thenReturn(Optional.of(new Authority().name(AuthoritiesConstants.ADMIN)));
        when(users.countUsersWithAuthority(AuthoritiesConstants.ADMIN)).thenReturn(1L);

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
