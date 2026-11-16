package com.lumi.service;

import com.lumi.domain.EmailOtp;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.OtpPurpose;
import com.lumi.repository.EmailOtpRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.OtpDigest;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final OtpDigest otpDigest;
    private final org.springframework.cache.CacheManager cacheManager;
    private final SecureRandom secureRandom = new SecureRandom();

    public record VerifiedLogin(User user, boolean rememberMe) {}

    public OtpService(
        EmailOtpRepository emailOtpRepository,
        UserRepository userRepository,
        MailService mailService,
        OtpDigest otpDigest,
        org.springframework.cache.CacheManager cacheManager
    ) {
        this.emailOtpRepository = emailOtpRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.otpDigest = otpDigest;
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

        emailOtpRepository.invalidateUnused(email, purpose);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtpCode(otpDigest.hash(otpCode));
        emailOtp.setPurpose(purpose);
        emailOtp.setCreatedAt(now);
        emailOtp.setExpiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        emailOtp.setIsUsed(false);
        emailOtp.setFailedAttempts(0);
        emailOtp.setIpAddress(ipAddress);

        emailOtpRepository.save(emailOtp);
        mailService.sendOtpMail(email, otpCode, resolvePurposeDescription(purpose));

        LOG.info("OTP sent to {} for purpose {}", email, purpose);

        return OtpResponseDTO.sent("Mã OTP đã được gửi thành công đến email của bạn.", COOLDOWN_SECONDS, EXPIRATION_MINUTES * 60);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BadRequestAlertException.class)
    public OtpResponseDTO verifyOtp(VerifyOtpRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        OtpPurpose purpose = request.getPurpose();
        String inputOtp = request.getOtpCode().trim();

        EmailOtp otp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(email, purpose)
            .orElseThrow(() -> new BadRequestAlertException("Mã OTP không hợp lệ hoặc đã sử dụng.", ENTITY_NAME, "otpInvalid"));

        if (!otp.getExpiresAt().isAfter(Instant.now())) {
            otp.setIsUsed(true);
            emailOtpRepository.save(otp);
            throw new BadRequestAlertException("Mã OTP đã hết hạn.", ENTITY_NAME, "otpExpired");
        }

        if (otp.getFailedAttempts() >= 5) {
            otp.setIsUsed(true);
            emailOtpRepository.save(otp);
            throw new BadRequestAlertException("Mã OTP đã bị khóa do nhập sai quá nhiều lần.", ENTITY_NAME, "otpLocked");
        }

        if (!otpDigest.matches(inputOtp, otp.getOtpCode())) {
            int failed = otp.getFailedAttempts() + 1;
            otp.setFailedAttempts(failed);
            if (failed >= 5) otp.setIsUsed(true);
            emailOtpRepository.save(otp);

            throw new BadRequestAlertException("Mã OTP không chính xác.", ENTITY_NAME, "otpIncorrect");
        }

        otp.setIsUsed(true);
        otp.setVerifiedAt(Instant.now());
        emailOtpRepository.save(otp);

        if (purpose == OtpPurpose.REGISTER || purpose == OtpPurpose.EMAIL_VERIFICATION) {
            User user = userRepository
                .findOneByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy tài khoản.", ENTITY_NAME, "userNotFound"));

            if (!user.isActivated()) {
                user.setActivated(true);
                userRepository.save(user);
                clearUserCaches(user);
            }

            return OtpResponseDTO.verified("Xác thực email thành công. Bạn có thể đăng nhập.");
        }

        return OtpResponseDTO.verified("Xác thực OTP thành công.");
    }

    public String startLoginChallenge(User user, boolean rememberMe, String ipAddress) {
        String email = user.getEmail().trim().toLowerCase();
        Instant now = Instant.now();
        var previous = emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.LOGIN);
        if (previous.isPresent() && Duration.between(previous.orElseThrow().getCreatedAt(), now).getSeconds() < COOLDOWN_SECONDS) {
            EmailOtp existing = previous.orElseThrow();
            if (
                existing.getLoginChallengeId() != null && !Boolean.TRUE.equals(existing.getIsUsed()) && existing.getExpiresAt().isAfter(now)
            ) {
                return existing.getLoginChallengeId();
            }
            throw new BadRequestAlertException("Vui lòng đợi trước khi yêu cầu mã OTP mới.", ENTITY_NAME, "otpCooldown");
        }

        emailOtpRepository.invalidateUnused(email, OtpPurpose.LOGIN);
        String code = generateNumericOtp(OTP_LENGTH);
        EmailOtp otp = new EmailOtp();
        otp.setEmail(email);
        otp.setPurpose(OtpPurpose.LOGIN);
        otp.setOtpCode(otpDigest.hash(code));
        otp.setLoginChallengeId(UUID.randomUUID().toString());
        otp.setRememberMe(rememberMe);
        otp.setFailedAttempts(0);
        otp.setIsUsed(false);
        otp.setCreatedAt(now);
        otp.setExpiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        otp.setIpAddress(ipAddress);
        emailOtpRepository.save(otp);
        mailService.sendOtpMail(email, code, resolvePurposeDescription(OtpPurpose.LOGIN));
        return otp.getLoginChallengeId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BadRequestAlertException.class)
    public VerifiedLogin verifyLoginChallenge(String challengeId, String code) {
        EmailOtp otp = emailOtpRepository
            .findChallengeForUpdate(challengeId, OtpPurpose.LOGIN)
            .orElseThrow(() -> new BadRequestAlertException("Yêu cầu đăng nhập không hợp lệ.", ENTITY_NAME, "challengeInvalid"));
        Instant now = Instant.now();
        if (Boolean.TRUE.equals(otp.getIsUsed()) || !otp.getExpiresAt().isAfter(now)) {
            throw new BadRequestAlertException("Yêu cầu đăng nhập đã hết hiệu lực.", ENTITY_NAME, "challengeExpired");
        }
        if (otp.getFailedAttempts() >= 5) {
            throw new BadRequestAlertException("Bạn đã nhập sai quá nhiều lần.", ENTITY_NAME, "challengeLocked");
        }
        if (code == null || !code.matches("\\d{6}") || !otpDigest.matches(code, otp.getOtpCode())) {
            int failed = otp.getFailedAttempts() + 1;
            otp.setFailedAttempts(failed);
            if (failed >= 5) otp.setIsUsed(true);
            emailOtpRepository.save(otp);
            throw new BadRequestAlertException("OTP không chính xác.", ENTITY_NAME, "otpIncorrect");
        }
        User user = userRepository
            .findOneWithAuthoritiesByEmailIgnoreCase(otp.getEmail())
            .filter(User::isActivated)
            .orElseThrow(() -> new BadRequestAlertException("Tài khoản không hợp lệ.", ENTITY_NAME, "userInvalid"));
        otp.setIsUsed(true);
        otp.setVerifiedAt(now);
        emailOtpRepository.save(otp);
        return new VerifiedLogin(user, Boolean.TRUE.equals(otp.getRememberMe()));
    }

    public void resendLoginChallenge(String challengeId) {
        EmailOtp otp = emailOtpRepository
            .findChallengeForUpdate(challengeId, OtpPurpose.LOGIN)
            .orElseThrow(() -> new BadRequestAlertException("Yêu cầu đăng nhập không hợp lệ.", ENTITY_NAME, "challengeInvalid"));
        Instant now = Instant.now();
        if (Boolean.TRUE.equals(otp.getIsUsed()) || !otp.getExpiresAt().isAfter(now) || otp.getFailedAttempts() >= 5) {
            throw new BadRequestAlertException("Yêu cầu đăng nhập đã hết hiệu lực.", ENTITY_NAME, "challengeExpired");
        }
        if (Duration.between(otp.getCreatedAt(), now).getSeconds() < COOLDOWN_SECONDS) {
            throw new BadRequestAlertException("Vui lòng đợi trước khi gửi lại OTP.", ENTITY_NAME, "otpCooldown");
        }
        String code = generateNumericOtp(OTP_LENGTH);
        otp.setOtpCode(otpDigest.hash(code));
        otp.setCreatedAt(now);
        emailOtpRepository.save(otp);
        mailService.sendOtpMail(otp.getEmail(), code, resolvePurposeDescription(OtpPurpose.LOGIN));
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
            User user = userRepository
                .findOneByEmailIgnoreCase(email)
                .orElseThrow(() ->
                    new BadRequestAlertException("Không tìm thấy tài khoản cần xác thực.", ENTITY_NAME, "registrationNotFound")
                );

            if (user.isActivated()) {
                throw new BadRequestAlertException("Tài khoản đã được kích hoạt.", ENTITY_NAME, "accountAlreadyActivated");
            }
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
