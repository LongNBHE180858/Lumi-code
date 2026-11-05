package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lumi.domain.Authority;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.repository.*;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.TokenProvider;
import com.lumi.service.dto.auth.AuthResponseDTO;
import com.lumi.service.dto.auth.RegisterRequestDTO;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit Test for {@link AuthService} technical helper logic.
 * Covers Phase 3.1 Unit Testing scope for Student Registration:
 * TC-REG-STU-UT-026.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private TeacherVerificationRepository teacherVerificationRepository;

    @Mock
    private AuthSessionRepository authSessionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private OtpService otpService;

    @Mock
    private MailService mailService;

    @Mock
    private EmailOtpRepository emailOtpRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
            authenticationManagerBuilder,
            tokenProvider,
            userRepository,
            userProfileRepository,
            teacherVerificationRepository,
            authSessionRepository,
            passwordEncoder,
            authorityRepository,
            cacheManager,
            otpService,
            mailService,
            emailOtpRepository
        );
    }

    @Test
    @DisplayName("TC-REG-STU-UT-026: Verify resolveTargetRole defaults to ROLE_STUDENT when role is null")
    @Tag("UT-026")
    void UT026_nullRole_shouldDefaultToRoleStudent() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setUsername("student_hanoi");
        vm.setEmail("student.hanoi@lumi.edu.vn");
        vm.setPassword("Lumi@2026Platform");
        vm.setFirstName("Hanoi");
        vm.setLastName("Student");
        vm.setRole(null); // Explicitly null role

        when(userRepository.findOneByLogin("student_hanoi")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("student.hanoi@lumi.edu.vn")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        Authority userAuth = new Authority();
        userAuth.setName(AuthoritiesConstants.USER);
        when(authorityRepository.findById(AuthoritiesConstants.USER)).thenReturn(Optional.of(userAuth));

        Authority studentAuth = new Authority();
        studentAuth.setName(AuthoritiesConstants.STUDENT);
        when(authorityRepository.findById(AuthoritiesConstants.STUDENT)).thenReturn(Optional.of(studentAuth));

        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(101L);
            return u;
        });

        when(userProfileRepository.saveAndFlush(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(otpService.sendOtp(any(SendOtpRequestDTO.class), any())).thenReturn(OtpResponseDTO.sent("OTP sent", 60L, 300L));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        AuthResponseDTO response = authService.register(vm, "test-client-device");

        // Assert
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        Set<String> authorityNames = savedUser.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());

        assertThat(authorityNames)
            .as("When role is null, resolveTargetRole must default to ROLE_STUDENT")
            .contains(AuthoritiesConstants.STUDENT, AuthoritiesConstants.USER);
    }

    @Test
    @DisplayName("TC-REG-STU-UT-026: Verify resolveTargetRole defaults to ROLE_STUDENT when role is empty or blank")
    @Tag("UT-026")
    void UT026_emptyOrBlankRole_shouldDefaultToRoleStudent() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setUsername("student_danang");
        vm.setEmail("student.danang@lumi.edu.vn");
        vm.setPassword("Lumi@2026Platform");
        vm.setFirstName("Danang");
        vm.setLastName("Student");
        vm.setRole("   "); // Blank role

        when(userRepository.findOneByLogin("student_danang")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("student.danang@lumi.edu.vn")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        Authority userAuth = new Authority();
        userAuth.setName(AuthoritiesConstants.USER);
        when(authorityRepository.findById(AuthoritiesConstants.USER)).thenReturn(Optional.of(userAuth));

        Authority studentAuth = new Authority();
        studentAuth.setName(AuthoritiesConstants.STUDENT);
        when(authorityRepository.findById(AuthoritiesConstants.STUDENT)).thenReturn(Optional.of(studentAuth));

        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(102L);
            return u;
        });

        when(userProfileRepository.saveAndFlush(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(otpService.sendOtp(any(SendOtpRequestDTO.class), any())).thenReturn(OtpResponseDTO.sent("OTP sent", 60L, 300L));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        AuthResponseDTO response = authService.register(vm, "test-client-device");

        // Assert
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        Set<String> authorityNames = savedUser.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());

        assertThat(authorityNames)
            .as("When role is blank, resolveTargetRole must default to ROLE_STUDENT")
            .contains(AuthoritiesConstants.STUDENT, AuthoritiesConstants.USER);
    }
}
