package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.Authority;
import com.lumi.domain.EmailOtp;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.OtpPurpose;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.EmailOtpRepository;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.auth.RegisterRequestDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration Tests for Student Registration Feature (Phase 3.2 Scope).
 * Covers TC-REG-STU-IT-001 through IT-012 per Baseline Workbook v2.2.2.
 */
@AutoConfigureMockMvc
@IntegrationTest
class StudentRegistrationIT {

    private static final Logger LOG = LoggerFactory.getLogger(StudentRegistrationIT.class);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private EmailOtpRepository emailOtpRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @BeforeEach
    void setupMocks() {
        reset(javaMailSender);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
    }

    private RegisterRequestDTO createValidStudentVM(String suffix) {
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setUsername("student_" + suffix);
        vm.setEmail("student_" + suffix + "@lumi.edu.vn");
        vm.setPassword("Lumi@2026Password");
        vm.setFirstName("Hanoi");
        vm.setLastName("Student");
        vm.setPhoneNumber("098" + suffix.replaceAll("[^0-9]", "1234567").substring(0, 7));
        //        vm.setSchoolName("THCS Chu Van An");
        //        vm.setGradeLevel(10);
        return vm;
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-001: Register new Student with valid data persists User, UserProfile, and EmailOtp in Database")
    @Tag("IT-001")
    void IT001_registerStudent_validData_shouldPersistUserAndProfileAndOtp() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it001");

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.code").value(1000))
            .andExpect(jsonPath("$.message").value(containsString("Đăng ký tài khoản Học sinh thành công")))
            .andExpect(jsonPath("$.data.user.email").value(vm.getEmail().toLowerCase()))
            .andExpect(jsonPath("$.data.user.authorities.[*]").value(org.hamcrest.Matchers.hasItem(AuthoritiesConstants.STUDENT)));

        // 1. Verify User persistence
        Optional<User> userOpt = userRepository.findOneByEmailIgnoreCase(vm.getEmail());
        assertThat(userOpt).isPresent();
        User savedUser = userOpt.get();
        assertThat(savedUser.isActivated()).isFalse();
        assertThat(savedUser.getLogin()).isEqualTo(vm.getUsername());
        assertThat(passwordEncoder.matches("Lumi@2026Password", savedUser.getPassword())).isTrue();

        Set<String> authorities = savedUser.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
        assertThat(authorities).contains(AuthoritiesConstants.STUDENT, AuthoritiesConstants.USER);

        // 2. Verify UserProfile persistence
        Optional<UserProfile> profileOpt = userProfileRepository.findOneByUserId(savedUser.getId());
        assertThat(profileOpt).isPresent();
        UserProfile profile = profileOpt.get();
        assertThat(profile.getSchoolName()).isEqualTo("THCS Chu Van An");
        assertThat(profile.getGradeLevel()).isEqualTo(10);

        // 3. Verify EmailOtp persistence
        Optional<EmailOtp> otpOpt = emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(
            vm.getEmail(),
            OtpPurpose.REGISTER
        );
        assertThat(otpOpt).isPresent();
        EmailOtp otp = otpOpt.get();
        assertThat(otp.getOtpCode()).matches("^[0-9]{6}$");
        assertThat(otp.getIsUsed()).isFalse();
        assertThat(Duration.between(otp.getCreatedAt(), otp.getExpiresAt()).getSeconds()).isEqualTo(300L);
    }

    @Test
    @Transactional
    @DisplayName(
        "TC-REG-STU-IT-002: Verify registration rejected when email already registered and activated (Duplicate Email in Same Role)"
    )
    @Tag("IT-002")
    void IT002_registerStudent_duplicateEmailSameRole_shouldReturnBadRequest() throws Exception {
        RegisterRequestDTO initialVM = createValidStudentVM("it002_init");

        // First registration
        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(initialVM)))
            .andExpect(status().isCreated());

        // Activate the initial user
        User existingUser = userRepository.findOneByEmailIgnoreCase(initialVM.getEmail()).orElseThrow();
        existingUser.setActivated(true);
        userRepository.saveAndFlush(existingUser);

        // Attempt duplicate registration with same email
        RegisterRequestDTO duplicateVM = createValidStudentVM("it002_dup");
        duplicateVM.setEmail(initialVM.getEmail()); // Same email

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(duplicateVM)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value(notNullValue()));

        // Verify only 1 user exists
        long count = userRepository
            .findAll()
            .stream()
            .filter(u -> initialVM.getEmail().equalsIgnoreCase(u.getEmail()))
            .count();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-003: Verify registration behavior when email exists under a different role (Teacher vs Student - NC-01)")
    @Tag("IT-003")
    void IT003_registerStudent_crossRoleEmail_shouldEnforceUniqueConstraint() throws Exception {
        // Pre-seed an activated Teacher
        User teacher = new User();
        teacher.setLogin("teacher_it003");
        teacher.setEmail("teacher_cross@lumi.edu.vn");
        teacher.setPassword(passwordEncoder.encode("Password@123"));
        teacher.setActivated(true);
        Authority teacherAuth = authorityRepository.findById(AuthoritiesConstants.INSTRUCTOR).orElseThrow();
        teacher.setAuthorities(new HashSet<>(Collections.singletonList(teacherAuth)));
        userRepository.saveAndFlush(teacher);

        // Attempt to register Student with teacher's email
        RegisterRequestDTO studentVM = createValidStudentVM("it003_stu");
        studentVM.setEmail("teacher_cross@lumi.edu.vn"); // Same email as teacher

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentVM)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value(notNullValue()));
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-004: Verify re-registration cleans up previous non-activated user and permits new registration")
    @Tag("IT-004")
    void IT004_registerStudent_reRegisterNonActivatedUser_shouldCleanOldAndCreateNew() throws Exception {
        RegisterRequestDTO initialVM = createValidStudentVM("it004_pending");

        // 1. Initial registration
        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(initialVM)))
            .andExpect(status().isCreated());

        User initialUser = userRepository.findOneByEmailIgnoreCase(initialVM.getEmail()).orElseThrow();
        assertThat(initialUser.isActivated()).isFalse();

        // 2. Re-register with same email and new username
        RegisterRequestDTO reRegisterRequestDTO = createValidStudentVM("it004_new");
        reRegisterRequestDTO.setEmail(initialVM.getEmail()); // Same email
        reRegisterRequestDTO.setFirstName("UpdatedName");

        mockMvc
            .perform(
                post("/api/auth/student/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reRegisterRequestDTO))
            )
            .andExpect(status().isCreated());

        // 3. Verify old user was replaced
        Optional<User> currentUserOpt = userRepository.findOneByEmailIgnoreCase(initialVM.getEmail());
        assertThat(currentUserOpt).isPresent();
        User currentUser = currentUserOpt.get();
        assertThat(currentUser.getLogin()).isEqualTo(reRegisterRequestDTO.getUsername());
        assertThat(currentUser.getFirstName()).isEqualTo("UpdatedName");
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-005: Verify OTP email dispatch with exact 6-digit code via JavaMailSender")
    @Tag("IT-005")
    void IT005_registerStudent_shouldDispatchOtpEmailWithSixDigits() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it005_mail");

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        // Verify JavaMailSender was called to send email
        verify(javaMailSender, atLeastOnce()).send(any(MimeMessage.class));

        // Query created OTP
        EmailOtp otp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(vm.getEmail(), OtpPurpose.REGISTER)
            .orElseThrow();
        assertThat(otp.getOtpCode()).matches("^[0-9]{6}$");
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-006: Verify student account activation lifecycle after successful OTP verification")
    @Tag("IT-006")
    void IT006_verifyOtp_validOtp_shouldActivateAccount() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it006_verify");

        // 1. Register student
        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        User user = userRepository.findOneByEmailIgnoreCase(vm.getEmail()).orElseThrow();
        assertThat(user.isActivated()).isFalse();

        // 2. Retrieve generated OTP
        EmailOtp otp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(vm.getEmail(), OtpPurpose.REGISTER)
            .orElseThrow();

        // 3. Submit valid OTP
        VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO();
        verifyRequest.setEmail(vm.getEmail());
        verifyRequest.setOtpCode(otp.getOtpCode());
        verifyRequest.setPurpose(OtpPurpose.REGISTER);

        mockMvc
            .perform(post("/api/auth/otp/verify").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(verifyRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1000))
            .andExpect(jsonPath("$.data.verified").value(true));

        // 4. Assert User is now activated in DB
        User activatedUser = userRepository.findOneByEmailIgnoreCase(vm.getEmail()).orElseThrow();
        assertThat(activatedUser.isActivated()).isTrue();

        // 5. Assert EmailOtp is marked as used
        EmailOtp updatedOtp = emailOtpRepository.findById(otp.getId()).orElseThrow();
        assertThat(updatedOtp.getIsUsed()).isTrue();
        assertThat(updatedOtp.getVerifiedAt()).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-007: Verify OTP verification rejected when incorrect 6-digit code submitted")
    @Tag("IT-007")
    void IT007_verifyOtp_incorrectOtp_shouldReject() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it007_wrong");

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO();
        verifyRequest.setEmail(vm.getEmail());
        verifyRequest.setOtpCode("000000"); // Incorrect OTP
        verifyRequest.setPurpose(OtpPurpose.REGISTER);

        mockMvc
            .perform(post("/api/auth/otp/verify").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(verifyRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(containsString("Mã OTP không chính xác")));

        // Verify user remains unactivated
        User user = userRepository.findOneByEmailIgnoreCase(vm.getEmail()).orElseThrow();
        assertThat(user.isActivated()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-008: Verify expired OTP is rejected with otpExpired error")
    @Tag("IT-008")
    void IT008_verifyOtp_expiredOtp_shouldReject() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it008_expired");

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        EmailOtp otp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(vm.getEmail(), OtpPurpose.REGISTER)
            .orElseThrow();

        // Force OTP expiration in DB
        otp.setExpiresAt(Instant.now().minusSeconds(10));
        emailOtpRepository.saveAndFlush(otp);

        VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO();
        verifyRequest.setEmail(vm.getEmail());
        verifyRequest.setOtpCode(otp.getOtpCode());
        verifyRequest.setPurpose(OtpPurpose.REGISTER);

        mockMvc
            .perform(post("/api/auth/otp/verify").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(verifyRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(containsString("Mã OTP đã hết hạn")));

        User user = userRepository.findOneByEmailIgnoreCase(vm.getEmail()).orElseThrow();
        assertThat(user.isActivated()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-009: Verify technical OTP resend cooldown rejects second send request within 60 seconds")
    @Tag("IT-009")
    void IT009_resendOtp_withinCooldown_shouldReject() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it009_cooldown");

        // 1. Initial registration generates OTP (createdAt = now)
        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        // 2. Immediate resend attempt within 60 seconds
        SendOtpRequestDTO sendRequest = new SendOtpRequestDTO();
        sendRequest.setEmail(vm.getEmail());
        sendRequest.setPurpose(OtpPurpose.REGISTER);

        mockMvc
            .perform(post("/api/auth/otp/send").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sendRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(containsString("giây trước khi yêu cầu gửi lại mã OTP")));
    }

    @Test
    @Transactional
    @DisplayName("TC-REG-STU-IT-010: Verify Grade level and School Name persist correctly in UserProfile")
    @Tag("IT-010")
    void IT010_registerStudent_shouldPersistGradeAndSchoolInProfile() throws Exception {
        RegisterRequestDTO vm = createValidStudentVM("it010_profile");
        //        vm.setSchoolName("THCS Luong The Vinh");
        //        vm.setGradeLevel(8);

        mockMvc
            .perform(post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm)))
            .andExpect(status().isCreated());

        User user = userRepository.findOneByEmailIgnoreCase(vm.getEmail()).orElseThrow();
        UserProfile profile = userProfileRepository.findOneByUserId(user.getId()).orElseThrow();

        assertThat(profile.getSchoolName()).isEqualTo("THCS Luong The Vinh");
        assertThat(profile.getGradeLevel()).isEqualTo(8);
    }

    @Test
    @DisplayName("TC-REG-STU-IT-012: Verify Concurrency: Simultaneous duplicate student registration requests handled safely")
    @Tag("IT-012")
    void IT012_concurrentRegistration_identicalEmail_shouldHandleRaceConditionSafely() throws Exception {
        int threadsCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);

        String commonEmail = "concurrent_stu@lumi.edu.vn";
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadsCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Synchronize all threads at the starting line
                    RegisterRequestDTO vm = new RegisterRequestDTO();
                    vm.setUsername("concurrent_user_" + index);
                    vm.setEmail(commonEmail);
                    vm.setPassword("Lumi@2026Password");
                    vm.setFirstName("Concurrent");
                    vm.setLastName("Student");
                    vm.setPhoneNumber("098111222" + index);
                    //                    vm.setSchoolName("THCS Chu Van An");
                    //                    vm.setGradeLevel(9);

                    MvcResult result = mockMvc
                        .perform(
                            post("/api/auth/student/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vm))
                        )
                        .andReturn();

                    int statusCode = result.getResponse().getStatus();
                    LOG.info("Concurrent request {} finished with HTTP status {}", index, statusCode);
                    statusCodes.add(statusCode);
                } catch (Exception e) {
                    LOG.warn("Concurrent request {} encountered exception: {}", index, e.getMessage());
                    statusCodes.add(400); // Handled as error/rejection
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Unleash all threads simultaneously
        startLatch.countDown();
        boolean completed = finishLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        assertThat(statusCodes).hasSize(threadsCount);

        long createdCount = statusCodes
            .stream()
            .filter(s -> s == 201)
            .count();
        long rejectedCount = statusCodes
            .stream()
            .filter(s -> s != 201)
            .count();

        // Exactly 1 thread must win and create account; others must be rejected
        assertThat(createdCount).as("Exactly 1 concurrent registration must succeed with HTTP 201 Created").isEqualTo(1L);

        assertThat(rejectedCount)
            .as("All other concurrent registrations must fail/be rejected")
            .isEqualTo(threadsCount - 1L);

        // Verify in DB only 1 record exists
        long dbCount = userRepository
            .findAll()
            .stream()
            .filter(u -> commonEmail.equalsIgnoreCase(u.getEmail()))
            .count();
        assertThat(dbCount).isEqualTo(1L);

        // Cleanup concurrent test user
        userRepository.findOneByEmailIgnoreCase(commonEmail).ifPresent(u -> {
            userProfileRepository.findOneByUserId(u.getId()).ifPresent(userProfileRepository::delete);
            userRepository.delete(u);
        });
    }
}
