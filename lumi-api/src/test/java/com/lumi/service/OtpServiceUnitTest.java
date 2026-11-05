package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.lumi.domain.EmailOtp;
import com.lumi.domain.enumeration.OtpPurpose;
import com.lumi.repository.EmailOtpRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

/**
 * Unit Test for {@link OtpService}.
 * Covers Phase 3.1 Unit Testing scope for Student Registration:
 * TC-REG-STU-UT-022, UT-023, UT-025.
 */
@ExtendWith(MockitoExtension.class)
class OtpServiceUnitTest {

    @Mock
    private EmailOtpRepository emailOtpRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private CacheManager cacheManager;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(emailOtpRepository, userRepository, mailService, cacheManager);
    }

    @Test
    @DisplayName("TC-REG-STU-UT-022: Verify OTP generator produces exactly 6 numeric digits")
    @Tag("UT-022")
    void UT022_otpGeneration_shouldProduceExactSixNumericDigits() {
        // Arrange
        String testEmail = "student.hanoi@lumi.edu.vn";
        SendOtpRequestDTO request = new SendOtpRequestDTO();
        request.setEmail(testEmail);
        request.setPurpose(OtpPurpose.REGISTER);

        when(userRepository.findOneByEmailIgnoreCase(testEmail)).thenReturn(Optional.empty());
        when(emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(eq(testEmail), eq(OtpPurpose.REGISTER)))
            .thenReturn(Optional.empty());

        ArgumentCaptor<EmailOtp> otpCaptor = ArgumentCaptor.forClass(EmailOtp.class);

        // Act
        OtpResponseDTO response = otpService.sendOtp(request, "127.0.0.1");

        // Assert
        assertThat(response).isNotNull();
        verify(emailOtpRepository, times(1)).save(otpCaptor.capture());

        EmailOtp savedOtp = otpCaptor.getValue();
        assertThat(savedOtp.getOtpCode())
            .as("OTP code must be exactly 6 characters per SRS GB-05")
            .hasSize(6);

        assertThat(savedOtp.getOtpCode())
            .as("OTP code must contain only numeric digits per SRS GB-05")
            .matches("^[0-9]{6}$");

        verify(mailService, times(1)).sendOtpMail(eq(testEmail), eq(savedOtp.getOtpCode()), anyString());
    }

    @Test
    @DisplayName("TC-REG-STU-UT-023: Verify OTP expiration time calculated as exactly 300 seconds (5 minutes)")
    @Tag("UT-023")
    void UT023_otpExpiration_shouldBeExact300Seconds() {
        // Arrange
        String testEmail = "student.hanoi@lumi.edu.vn";
        SendOtpRequestDTO request = new SendOtpRequestDTO();
        request.setEmail(testEmail);
        request.setPurpose(OtpPurpose.REGISTER);

        when(userRepository.findOneByEmailIgnoreCase(testEmail)).thenReturn(Optional.empty());
        when(emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(eq(testEmail), eq(OtpPurpose.REGISTER)))
            .thenReturn(Optional.empty());

        ArgumentCaptor<EmailOtp> otpCaptor = ArgumentCaptor.forClass(EmailOtp.class);

        // Act
        otpService.sendOtp(request, "127.0.0.1");

        // Assert
        verify(emailOtpRepository, times(1)).save(otpCaptor.capture());
        EmailOtp savedOtp = otpCaptor.getValue();

        Instant createdAt = savedOtp.getCreatedAt();
        Instant expiresAt = savedOtp.getExpiresAt();

        assertThat(createdAt).isNotNull();
        assertThat(expiresAt).isNotNull();

        long diffSeconds = Duration.between(createdAt, expiresAt).getSeconds();
        assertThat(diffSeconds)
            .as("OTP duration between createdAt and expiresAt must be exactly 300 seconds (5 minutes) per SRS GB-05")
            .isEqualTo(300L);

        assertThat(expiresAt)
            .as("expiresAt must equal createdAt plus 5 ChronoUnit.MINUTES")
            .isEqualTo(createdAt.plus(5, ChronoUnit.MINUTES));
    }

    @Test
    @DisplayName("TC-REG-STU-UT-025: Verify technical 60s cooldown rejects immediate OTP resend request")
    @Tag("UT-025")
    void UT025_resendWithinCooldown_shouldThrowCooldownException() {
        // Arrange
        String testEmail = "student.hanoi@lumi.edu.vn";
        SendOtpRequestDTO request = new SendOtpRequestDTO();
        request.setEmail(testEmail);
        request.setPurpose(OtpPurpose.REGISTER);

        when(userRepository.findOneByEmailIgnoreCase(testEmail)).thenReturn(Optional.empty());

        // Simulate an existing OTP created 30 seconds ago (cooldown is 60s)
        EmailOtp recentOtp = new EmailOtp();
        recentOtp.setEmail(testEmail);
        recentOtp.setPurpose(OtpPurpose.REGISTER);
        recentOtp.setCreatedAt(Instant.now().minusSeconds(30));

        when(emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(eq(testEmail), eq(OtpPurpose.REGISTER)))
            .thenReturn(Optional.of(recentOtp));

        // Act & Assert
        assertThatThrownBy(() -> otpService.sendOtp(request, "127.0.0.1"))
            .isInstanceOf(BadRequestAlertException.class)
            .satisfies(ex -> {
                BadRequestAlertException badRequest = (BadRequestAlertException) ex;
                assertThat(badRequest.getErrorKey())
                    .as("Error key must indicate otpCooldown")
                    .isEqualTo("otpCooldown");
                assertThat(badRequest.getMessage())
                    .as("Message must instruct user to wait before resending")
                    .contains("giây trước khi yêu cầu gửi lại mã OTP");
            });

        // Verify no new OTP is saved or mailed
        verify(emailOtpRepository, never()).save(any(EmailOtp.class));
        verify(mailService, never()).sendOtpMail(anyString(), anyString(), anyString());
    }
}

