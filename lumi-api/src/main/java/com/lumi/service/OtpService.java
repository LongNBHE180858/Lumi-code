package com.lumi.service;

import com.lumi.domain.EmailOtp;
import com.lumi.domain.enumeration.OtpPurpose;
import com.lumi.repository.EmailOtpRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing OTP sending, cooldown, and verification.
 */
@Service
@Transactional
public class OtpService {

    private static final Logger LOG = LoggerFactory.getLogger(OtpService.class);
    private static final String ENTITY_NAME = "emailOtp";

    private static final int OTP_LENGTH = 6;
    private static final long COOLDOWN_SECONDS = 60;
    private static final long EXPIRATION_MINUTES = 5;

    private final EmailOtpRepository emailOtpRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final org.springframework.cache.CacheManager cacheManager;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
        EmailOtpRepository emailOtpRepository,
        UserRepository userRepository,
        MailService mailService,
        org.springframework.cache.CacheManager cacheManager
    ) {
        this.emailOtpRepository = emailOtpRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.cacheManager = cacheManager;
    }

    public OtpResponseDTO sendOtp(SendOtpRequestDTO request, String ipAddress) {
        String email = request.getEmail().trim().toLowerCase();
        OtpPurpose purpose = request.getPurpose();

        validatePurposeWithEmail(email, purpose);

        Instant now = Instant.now();

        // kiểm tra xem hiện tại có otp với purpose chưa
        Optional<EmailOtp> latestOtpOpt = emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, purpose);
        if (latestOtpOpt.isPresent()) {
            EmailOtp latestOtp = latestOtpOpt.orElseThrow();
            long secondsSinceLastSend = Duration.between(latestOtp.getCreatedAt(), now).getSeconds();
            if (secondsSinceLastSend < COOLDOWN_SECONDS) {
                long waitTime = COOLDOWN_SECONDS - secondsSinceLastSend;
                throw new BadRequestAlertException(
                    "Vui lòng đợi " + waitTime + " giây trước khi yêu cầu gửi lại mã OTP.",
                    ENTITY_NAME,
                    "otpCooldown"
                );
            }
        }

        String otpCode = generateNumericOtp(OTP_LENGTH);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtpCode(otpCode);
        emailOtp.setPurpose(purpose);
        emailOtp.setCreatedAt(now);
        emailOtp.setExpiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        emailOtp.setIsUsed(false);
        emailOtp.setIpAddress(ipAddress);

        emailOtpRepository.save(emailOtp);

        // lưu otp thì gửi mail
        String purposeDescription = resolvePurposeDescription(purpose);
        mailService.sendOtpMail(email, otpCode, purposeDescription);

        LOG.info("OTP sent to {} for purpose {}", email, purpose);

        return OtpResponseDTO.sent("Mã OTP đã được gửi thành công đến email của bạn.", COOLDOWN_SECONDS, EXPIRATION_MINUTES * 60);
    }

    public OtpResponseDTO verifyOtp(VerifyOtpRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        OtpPurpose purpose = request.getPurpose();
        String inputOtp = request.getOtpCode().trim();

        EmailOtp otp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(email, purpose)
            .orElseThrow(() -> new BadRequestAlertException("Mã OTP không hợp lệ hoặc đã được sử dụng.", ENTITY_NAME, "otpInvalid"));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestAlertException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.", ENTITY_NAME, "otpExpired");
        }

        if (!otp.getOtpCode().equals(inputOtp)) {
            throw new BadRequestAlertException("Mã OTP không chính xác. Vui lòng kiểm tra lại.", ENTITY_NAME, "otpIncorrect");
        }

        otp.setIsUsed(true);
        otp.setVerifiedAt(Instant.now());
        emailOtpRepository.save(otp);

        LOG.info("OTP verified successfully for email {} and purpose {}", email, purpose);

        // Nếu mục đích là đăng ký hoặc xác thực email -> kích hoạt tài khoản
        if (purpose == OtpPurpose.REGISTER || purpose == OtpPurpose.EMAIL_VERIFICATION) {
            userRepository.findOneByEmailIgnoreCase(email).ifPresent(user -> {
                if (!user.isActivated()) {
                    user.setActivated(true);
                    userRepository.save(user);
                    clearUserCaches(user);
                    LOG.info("User {} successfully activated via OTP verification", user.getLogin());
                }
            });
            return OtpResponseDTO.verified("Xác thực email và kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        }

        return OtpResponseDTO.verified("Xác thực mã OTP thành công.");
    }

    @Transactional(readOnly = true)
    public boolean isOtpRecentlyVerified(String email, OtpPurpose purpose, int validWithinMinutes) {
        return emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email.toLowerCase(), purpose)
            .filter(otp -> Boolean.TRUE.equals(otp.getIsUsed()))
            .filter(otp -> otp.getVerifiedAt() != null)
            .filter(otp -> Duration.between(otp.getVerifiedAt(), Instant.now()).toMinutes() <= validWithinMinutes)
            .isPresent();
    }

    private void validatePurposeWithEmail(String email, OtpPurpose purpose) {
        if (purpose == OtpPurpose.FORGOT_PASSWORD) {
            boolean exists = userRepository.findOneByEmailIgnoreCase(email).isPresent();
            if (!exists) {
                throw new BadRequestAlertException("Email không tồn tại trong hệ thống.", ENTITY_NAME, "emailNotFound");
            }
        } else if (purpose == OtpPurpose.REGISTER || purpose == OtpPurpose.EMAIL_VERIFICATION) {
            boolean alreadyActivated = userRepository
                .findOneByEmailIgnoreCase(email)
                .filter(u -> Boolean.TRUE.equals(u.isActivated()))
                .isPresent();
            if (alreadyActivated) {
                throw new BadRequestAlertException(
                    "Email này đã được sử dụng và kích hoạt cho một tài khoản khác.",
                    ENTITY_NAME,
                    "emailAlreadyUsed"
                );
            }
        } else if (purpose == OtpPurpose.LOGIN) {
            boolean exists = userRepository.findOneByEmailIgnoreCase(email).filter(com.lumi.domain.User::isActivated).isPresent();
            if (!exists) {
                throw new BadRequestAlertException(
                    "Email không tồn tại hoặc tài khoản chưa được kích hoạt.",
                    ENTITY_NAME,
                    "emailNotFoundOrInactive"
                );
            }
        }
    }

    private void clearUserCaches(com.lumi.domain.User user) {
        if (cacheManager != null) {
            if (cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE) != null) {
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
            }
            if (user.getEmail() != null && cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE) != null) {
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
            }
        }
    }

    private String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    private String resolvePurposeDescription(OtpPurpose purpose) {
        return switch (purpose) {
            case
                REGISTER,
                EMAIL_VERIFICATION -> "Bạn đang thực hiện xác thực và kích hoạt tài khoản trên Lumi. Vui lòng sử dụng mã OTP dưới đây để hoàn tất:";
            case FORGOT_PASSWORD -> "Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản Lumi. Vui lòng nhập mã OTP dưới đây:";
            case LOGIN -> "Bạn đang thực hiện đăng nhập vào tài khoản Lumi. Vui lòng nhập mã OTP để xác nhận danh tính:";
            case STATUS2FA -> "Bạn đang thực hiện chức năng bảo mật 2 lớp. Vui lòng nhập mã OTP:";
        };
    }
}
