package com.lumi.service;

import com.lumi.config.Constants;
import com.lumi.domain.*;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.OtpPurpose;
import com.lumi.repository.*;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.DomainUserDetailsService;
import com.lumi.security.SecurityUtils;
import com.lumi.security.TokenProvider;
import com.lumi.security.UserNotActivatedException;
import com.lumi.service.dto.PasswordChangeDTO;
import com.lumi.service.dto.auth.*;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO;
import com.lumi.service.dto.auth.session.UserSessionDTO;
import com.lumi.service.dto.auth.twofa.TwoFaInitRequestDTO;
import com.lumi.service.dto.auth.twofa.TwoFaResponseDTO;
import com.lumi.service.dto.auth.twofa.TwoFactorConfirmRequestDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import com.lumi.web.rest.errors.EmailAlreadyUsedException;
import com.lumi.web.rest.errors.LoginAlreadyUsedException;
import com.lumi.web.rest.vm.Login2faVM;
import com.lumi.web.rest.vm.LoginVM;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService implements IAuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private static final int OTP_LENGTH = 6;
    private static final long COOLDOWN_SECONDS = 60;
    private static final long EXPIRATION_MINUTES = 5;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TeacherVerificationRepository teacherVerificationRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final CacheManager cacheManager;
    private final OtpService otpService;
    private final MailService mailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final EmailOtpRepository emailOtpRepository;

    public AuthService(
        AuthenticationManagerBuilder authenticationManagerBuilder,
        TokenProvider tokenProvider,
        UserRepository userRepository,
        UserProfileRepository userProfileRepository,
        TeacherVerificationRepository teacherVerificationRepository,
        AuthSessionRepository authSessionRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        OtpService otpService,
        MailService mailService,
        EmailOtpRepository emailOtpRepository
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.teacherVerificationRepository = teacherVerificationRepository;
        this.authSessionRepository = authSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.otpService = otpService;
        this.mailService = mailService;
        this.emailOtpRepository = emailOtpRepository;
    }

    @Override
    public com.lumi.service.dto.auth.otp.OtpResponseDTO forgotPasswordInit(
        com.lumi.service.dto.auth.password.ForgotPasswordInitRequestDTO request,
        String ipAddress
    ) {
        LOG.debug("Initiating forgot password for email: {}", request.getEmail());
        var sendOtpRequest = new com.lumi.service.dto.auth.otp.SendOtpRequestDTO(
            request.getEmail(),
            com.lumi.domain.enumeration.OtpPurpose.FORGOT_PASSWORD
        );
        return otpService.sendOtp(sendOtpRequest, ipAddress);
    }

    @Override
    public void forgotPasswordFinish(com.lumi.service.dto.auth.password.ForgotPasswordFinishRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        LOG.debug("Finishing forgot password for email: {}", email);

        var verifyRequest = new com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO(
            email,
            com.lumi.domain.enumeration.OtpPurpose.FORGOT_PASSWORD,
            request.getOtpCode()
        );
        otpService.verifyOtp(verifyRequest);

        User user = userRepository
            .findOneByEmailIgnoreCase(email)
            .orElseThrow(() ->
                new com.lumi.web.rest.errors.BadRequestAlertException(
                    "Không tìm thấy tài khoản với email này.",
                    "userManagement",
                    "emailNotFound"
                )
            );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        // xóa thông tin mật khẩu cũ đang ở trên RAM
        clearUserCaches(user);

        var activeSessions = authSessionRepository.findAllByUserAndIsRevokedFalse(user);
        for (var session : activeSessions) {
            session.setIsRevoked(true);
        }
        authSessionRepository.saveAll(activeSessions);

        LOG.info("Password reset successfully for user '{}' and all active sessions revoked", user.getLogin());
    }

    @Override
    public void changePassword(PasswordChangeDTO passwordChangeDto) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa đăng nhập.")
        );

        if (passwordChangeDto.getCurrentPassword() == null || passwordChangeDto.getCurrentPassword().isBlank()) {
            throw new BadRequestAlertException("Vui lòng nhập mật khẩu hiện tại.", "account", "currentPasswordEmpty");
        }

        if (
            passwordChangeDto.getNewPassword() == null ||
            passwordChangeDto.getNewPassword().length() < 4 ||
            passwordChangeDto.getNewPassword().length() > 100
        ) {
            throw new BadRequestAlertException("Mật khẩu mới phải có độ dài từ 4 đến 100 ký tự.", "account", "passwordLengthInvalid");
        }

        if (passwordChangeDto.getCurrentPassword().equals(passwordChangeDto.getNewPassword())) {
            throw new BadRequestAlertException("Mật khẩu mới không được trùng với mật khẩu hiện tại.", "account", "passwordSameAsOld");
        }

        User user = userRepository
            .findOneByLogin(login)
            .or(() -> userRepository.findOneByEmailIgnoreCase(login))
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản người dùng: " + login)
            );

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestAlertException("Mật khẩu hiện tại không chính xác.", "account", "invalidPassword");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
        clearUserCaches(user);

        LOG.info("Password changed successfully for user '{}'", user.getLogin());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserSessionDTO> getCurrentUserSessions() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
        );

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return authSessionRepository
            .findAllByUserAndIsRevokedFalseOrderByCreatedAtDesc(user)
            .stream()
            .filter(s -> s.getExpiresAt() != null && s.getExpiresAt().isAfter(java.time.Instant.now()))
            .map(s ->
                new com.lumi.service.dto.auth.session.UserSessionDTO(
                    s.getId(),
                    s.getDeviceInfo(),
                    s.getCreatedAt(),
                    s.getExpiresAt(),
                    s.getIsRevoked()
                )
            )
            .toList();
    }

    @Override
    public void revokeSession(Long sessionId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
        );

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AuthSession session = authSessionRepository
            .findByIdAndUser(sessionId, user)
            .orElseThrow(() ->
                new BadRequestAlertException(
                    "Không tìm thấy phiên đăng nhập hoặc bạn không có quyền thao tác.",
                    "authSession",
                    "sessionNotFound"
                )
            );

        session.setIsRevoked(true);
        authSessionRepository.save(session);
        LOG.info("Session {} revoked for user {}", sessionId, login);
    }

    @Override
    public void revokeOtherSessions(Long currentSessionId) {
        String login = com.lumi.security.SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
        );

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        java.util.List<com.lumi.domain.AuthSession> otherSessions =
            currentSessionId != null
                ? authSessionRepository.findAllByUserAndIdNotAndIsRevokedFalse(user, currentSessionId)
                : authSessionRepository.findAllByUserAndIsRevokedFalse(user);

        for (var session : otherSessions) {
            session.setIsRevoked(true);
        }
        authSessionRepository.saveAll(otherSessions);
        LOG.info("All other sessions revoked for user {}", login);
    }

    @Override
    public AuthResponseDTO loginStudent(LoginVM loginVM, String clientDeviceInfo) {
        return loginStudent(loginVM, clientDeviceInfo, null);
    }

    @Override
    public AuthResponseDTO loginStudent(LoginVM loginVM, String clientDeviceInfo, String ipAddress) {
        LOG.debug("REST request to login student: {}", loginVM.getUsername());
        Authentication authentication = authenticateCredentials(loginVM.getUsername(), loginVM.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = getUserFromAuthentication(authentication);

        boolean isStudent = user
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getName().equals(AuthoritiesConstants.STUDENT) || a.getName().equals(AuthoritiesConstants.USER));

        if (!isStudent) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản không có quyền truy cập dành cho Học sinh / Học viên.");
        }

        var twoFaOpt = checkAndInitiate2fa(user, ipAddress);
        if (twoFaOpt.isPresent()) {
            return twoFaOpt.orElseThrow();
        }

        return buildAuthResponse(authentication, user, loginVM.isRememberMe(), resolveDeviceInfo(loginVM, clientDeviceInfo));
    }

    @Override
    public AuthResponseDTO loginTeacher(LoginVM loginVM, String clientDeviceInfo) {
        return loginTeacher(loginVM, clientDeviceInfo, null);
    }

    @Override
    public AuthResponseDTO loginTeacher(LoginVM loginVM, String clientDeviceInfo, String ipAddress) {
        LOG.debug("REST request to login teacher: {}", loginVM.getUsername());
        Authentication authentication = authenticateCredentials(loginVM.getUsername(), loginVM.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = getUserFromAuthentication(authentication);

        boolean hasTeacherRole = user
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getName().equals(AuthoritiesConstants.INSTRUCTOR) || a.getName().equals(AuthoritiesConstants.ADMIN));

        Optional<UserProfile> userProfileOpt = userProfileRepository.findOneByUserId(user.getId());
        boolean hasInstructorProfile = userProfileOpt.isPresent() && userProfileOpt.orElseThrow().getInstructorStatus() != null;

        Optional<TeacherVerification> verificationOpt = teacherVerificationRepository.findOneByUserId(user.getId());
        boolean hasTeacherVerification = verificationOpt.isPresent();

        if (!hasTeacherRole && !hasInstructorProfile && !hasTeacherVerification) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Tài khoản không có quyền truy cập dành cho Giáo viên / Giảng viên. Vui lòng đăng ký tài khoản Giáo viên."
            );
        }

        var twoFaOpt = checkAndInitiate2fa(user, ipAddress);
        if (twoFaOpt.isPresent()) {
            return twoFaOpt.orElseThrow();
        }

        return buildAuthResponse(authentication, user, loginVM.isRememberMe(), resolveDeviceInfo(loginVM, clientDeviceInfo));
    }

    @Override
    public AuthResponseDTO login(LoginVM loginVM, String clientDeviceInfo) {
        return login(loginVM, clientDeviceInfo, null);
    }

    @Override
    public AuthResponseDTO login(LoginVM loginVM, String clientDeviceInfo, String ipAddress) {
        LOG.debug("REST request to authenticate user: {}", loginVM.getUsername());
        Authentication authentication = authenticateCredentials(loginVM.getUsername(), loginVM.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = getUserFromAuthentication(authentication);
        var twoFaOpt = checkAndInitiate2fa(user, ipAddress);
        if (twoFaOpt.isPresent()) {
            return twoFaOpt.orElseThrow();
        }

        return buildAuthResponse(authentication, user, loginVM.isRememberMe(), resolveDeviceInfo(loginVM, clientDeviceInfo));
    }

    @Override
    public AuthResponseDTO login2fa(Login2faVM login2faVM, String clientDeviceInfo) {
        String email = login2faVM.getEmail().trim().toLowerCase();
        String otpCode = login2faVM.getOtpCode().trim();
        LOG.debug("REST request to complete 2FA login for email: {}", email);

        var verifyRequest = new VerifyOtpRequestDTO(email, com.lumi.domain.enumeration.OtpPurpose.LOGIN, otpCode);
        otpService.verifyOtp(verifyRequest);

        User user = userRepository
            .findOneWithAuthoritiesByEmailIgnoreCase(email)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản người dùng: " + email)
            );

        if (!user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản chưa được kích hoạt.");
        }

        var userDetails = DomainUserDetailsService.UserWithId.fromUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResponse(
            authentication,
            user,
            login2faVM.isRememberMe(),
            login2faVM.getDeviceInfo() != null && !login2faVM.getDeviceInfo().isBlank() ? login2faVM.getDeviceInfo() : clientDeviceInfo
        );
    }

    private Optional<AuthResponseDTO> checkAndInitiate2fa(User user, String ipAddress) {
        boolean is2faEnabled = userProfileRepository.findOneByUserId(user.getId()).map(UserProfile::getIs2faEnabled).orElse(false);

        if (is2faEnabled) {
            LOG.info("2FA is enabled for user {}. Sending OTP to email.", user.getLogin());
            try {
                var sendOtpRequest = new SendOtpRequestDTO(user.getEmail(), OtpPurpose.LOGIN);
                otpService.sendOtp(sendOtpRequest, ipAddress != null ? ipAddress : "0.0.0.0");
            } catch (BadRequestAlertException ex) {
                if (!"otpCooldown".equals(ex.getErrorKey())) {
                    throw ex;
                }
                LOG.info("2FA OTP cooldown active for user {}, using previously sent OTP", user.getLogin());
            }
            return Optional.of(AuthResponseDTO.require2fa(user.getEmail()));
        }
        return Optional.empty();
    }

    //    public AuthResponseDTO loginWithGoogle(GoogleLoginVM googleLoginVM, String clientDeviceInfo) {
    //        LOG.debug("REST request to login with Google");
    //        if (googleLoginVM == null || googleLoginVM.getIdToken() == null || googleLoginVM.getIdToken().isBlank()) {
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google ID Token không được để trống.");
    //        }
    //
    //        GoogleUserInfo googleUser = verifyGoogleToken(googleLoginVM.getIdToken());
    //        String email = googleUser.email().trim().toLowerCase();
    //
    //        // 1. Kiểm tra tài khoản đã tồn tại theo email hoặc login chưa
    //        Optional<User> existingUserOpt = userRepository
    //            .findOneWithAuthoritiesByEmailIgnoreCase(email)
    //            .or(() -> userRepository.findOneWithAuthoritiesByLogin(email));
    //
    //        User user;
    //        if (existingUserOpt.isPresent()) {
    //            user = existingUserOpt.get();
    //            boolean needSave = false;
    //
    //            // Tự động kích hoạt tài khoản nếu chưa kích hoạt vì email đã được Google xác thực
    //            if (!user.isActivated()) {
    //                user.setActivated(true);
    //                needSave = true;
    //            }
    //
    //            // Cập nhật avatar nếu tài khoản hiện tại chưa có
    //            if ((user.getImageUrl() == null || user.getImageUrl().isBlank()) && googleUser.picture() != null) {
    //                user.setImageUrl(googleUser.picture());
    //                needSave = true;
    //            }
    //
    //            if (needSave) {
    //                user = userRepository.saveAndFlush(user);
    //                clearUserCaches(user);
    //            }
    //        } else {
    //            // 2. Tạo tài khoản mới tự động nếu chưa từng đăng ký
    //            user = new User();
    //            user.setLogin(email);
    //            user.setEmail(email);
    //            user.setFirstName(googleUser.givenName() != null ? googleUser.givenName() : googleUser.name());
    //            user.setLastName(googleUser.familyName() != null ? googleUser.familyName() : "");
    //            user.setImageUrl(googleUser.picture());
    //            user.setActivated(true);
    //            user.setLangKey(Constants.DEFAULT_LANGUAGE);
    //            // Đặt mật khẩu ngẫu nhiên được mã hóa an toàn (ngăn đăng nhập mật khẩu rỗng)
    //            user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
    //
    //            // Gán quyền truy cập (mặc định STUDENT nếu không chỉ định)
    //            String targetRole = resolveTargetRole(googleLoginVM.getRole());
    //            Set<Authority> authorities = new HashSet<>();
    //            authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
    //            authorityRepository.findById(targetRole).ifPresent(authorities::add);
    //            user.setAuthorities(authorities);
    //
    //            user = userRepository.saveAndFlush(user);
    //            clearUserCaches(user);
    //
    //            // Tạo UserProfile tương ứng
    //            UserProfile profile = new UserProfile();
    //            profile.setUser(user);
    //            profile.setUpdatedAt(Instant.now());
    //            if (AuthoritiesConstants.INSTRUCTOR.equals(targetRole)) {
    //                profile.setInstructorStatus(InstructorApplicationStatus.PENDING);
    //            }
    //            userProfileRepository.saveAndFlush(profile);
    //        }
    //
    //        // 3. Tạo Authentication Token & Session
    //        var userDetails = DomainUserDetailsService.UserWithId.fromUser(user);
    //        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    //        SecurityContextHolder.getContext().setAuthentication(authentication);
    //
    //        return buildAuthResponse(
    //            authentication,
    //            user,
    //            googleLoginVM.isRememberMe(),
    //            resolveDeviceInfo(googleLoginVM.getDeviceInfo(), clientDeviceInfo)
    //        );
    //    }

    // private GoogleUserInfo verifyGoogleToken(String idToken) {
    //     try {
    //         RestClient restClient = RestClient.create();
    //         GoogleUserInfo googleUser = restClient
    //             .get()
    //             .uri("https://oauth2.googleapis.com/tokeninfo?id_token={idToken}", idToken.trim())
    //             .retrieve()
    //             .body(GoogleUserInfo.class);

    //         if (googleUser == null || googleUser.email() == null || googleUser.email().isBlank()) {
    //             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Xác thực Google thất bại: Không tìm thấy thông tin email.");
    //         }

    //         if (!"true".equalsIgnoreCase(googleUser.emailVerified())) {
    //             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Google của bạn chưa được xác thực bởi Google.");
    //         }

    //         return googleUser;
    //     } catch (ResponseStatusException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         LOG.error("Failed to verify Google ID Token: {}", e.getMessage());
    //         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID Token không hợp lệ hoặc đã hết hạn.");
    //     }
    // }

    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestVM requestVM, String clientDeviceInfo) {
        String token = requestVM.getRefreshToken();
        AuthSession session = authSessionRepository
            .findOneByRefreshToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ hoặc không tồn tại."));

        if (Boolean.TRUE.equals(session.getIsRevoked())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token đã bị thu hồi.");
        }

        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token đã hết hạn. Vui lòng đăng nhập lại.");
        }

        User user = userRepository.findOneWithAuthoritiesById(session.getUser().getId()).orElseGet(session::getUser);

        if (!user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản người dùng chưa được kích hoạt.");
        }

        session.setIsRevoked(true);
        authSessionRepository.save(session);

        String deviceInfo =
            requestVM.getDeviceInfo() != null && !requestVM.getDeviceInfo().isBlank()
                ? requestVM.getDeviceInfo()
                : clientDeviceInfo != null
                  ? clientDeviceInfo
                  : session.getDeviceInfo();

        String newAccessToken = tokenProvider.createAccessToken(user, false);
        String newRefreshToken = tokenProvider.createRefreshToken(user, deviceInfo);
        long expiresIn = tokenProvider.getTokenValidityInSeconds(false);

        UserAuthDTO userAuthDTO = buildUserAuthDTO(user);
        return new AuthResponseDTO(newAccessToken, newRefreshToken, expiresIn, userAuthDTO);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authSessionRepository.findOneByRefreshToken(refreshToken).ifPresent(session -> {
                session.setIsRevoked(true);
                authSessionRepository.save(session);
            });
        }
    }

    @Override
    public TwoFaResponseDTO get2faStatus() {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa đăng nhập")
        );

        var user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin người dùng"));

        var userProfile = userProfileRepository
            .findOneByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ người dùng"));

        boolean is2fa = Boolean.TRUE.equals(userProfile.getIs2faEnabled());
        return new TwoFaResponseDTO(is2fa, user.getEmail());
    }

    @Override
    public void update2faStatus(TwoFaInitRequestDTO request, String ipAddress) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa đăng nhập")
        );

        var user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin người dùng"));

        OtpPurpose purpose = request != null && request.getPurpose() != null ? request.getPurpose() : OtpPurpose.STATUS2FA;

        Instant now = Instant.now();

        // Kiểm tra cooldown 60s
        emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(user.getEmail(), purpose).ifPresent(latestOtp -> {
            long secondsSinceLast = java.time.Duration.between(latestOtp.getCreatedAt(), now).getSeconds();
            if (secondsSinceLast < COOLDOWN_SECONDS) {
                throw new BadRequestAlertException(
                    "Vui lòng đợi " + (COOLDOWN_SECONDS - secondsSinceLast) + " giây trước khi yêu cầu mã OTP mới.",
                    "emailOtp",
                    "otpCooldown"
                );
            }
        });

        String otpCode = generateNumericOtp(OTP_LENGTH);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(user.getEmail());
        emailOtp.setOtpCode(otpCode);
        emailOtp.setPurpose(purpose);
        emailOtp.setCreatedAt(now);
        emailOtp.setExpiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        emailOtp.setIsUsed(false);
        emailOtp.setIpAddress(ipAddress);
        emailOtpRepository.save(emailOtp);

        String purposeDescription = resolvePurposeDescription(purpose);
        mailService.sendOtpMail(user.getEmail(), otpCode, purposeDescription);
    }

    @Override
    public TwoFaResponseDTO confirm2faStatus(TwoFactorConfirmRequestDTO request) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa đăng nhập")
        );

        var user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin người dùng"));

        String email = user.getEmail().toLowerCase().trim();
        String otp = request.getOtpCode() != null ? request.getOtpCode().trim() : "";
        OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.STATUS2FA;

        EmailOtp emailOtp = emailOtpRepository
            .findTopByEmailIgnoreCaseAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(email, purpose)
            .orElseThrow(() -> new BadRequestAlertException("Mã OTP không hợp lệ hoặc đã được sử dụng.", "emailOtp", "otpInvalid"));

        if (emailOtp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestAlertException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.", "emailOtp", "otpExpired");
        }

        if (!emailOtp.getOtpCode().equals(otp)) {
            throw new BadRequestAlertException("Mã OTP không chính xác. Vui lòng kiểm tra lại.", "emailOtp", "otpIncorrect");
        }

        // Đánh dấu mã OTP đã được xác minh thành công
        emailOtp.setIsUsed(true);
        emailOtp.setVerifiedAt(Instant.now());
        emailOtpRepository.save(emailOtp);

        // Cập nhật trạng thái 2FA vào hồ sơ người dùng
        var userProfile = userProfileRepository
            .findOneByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ người dùng"));

        userProfile.setIs2faEnabled(request.getIs2faEnable());
        userProfile.updatedAt(Instant.now());
        userProfileRepository.save(userProfile);

        return new TwoFaResponseDTO(Boolean.TRUE.equals(userProfile.getIs2faEnabled()), email);
    }

    private String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    //    private Authentication authenticateCredentials(String username, String password) {
    //        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    //        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    //    }
    private Authentication authenticateCredentials(String username, String password) {
        String login = username.trim().toLowerCase();

        User user = userRepository
            .findOneWithAuthoritiesByLogin(login)
            .or(() -> userRepository.findOneWithAuthoritiesByEmailIgnoreCase(login))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác."));

        if (!user.isActivated()) {
            throw new UserNotActivatedException(
                "Tài khoản chưa được kích hoạt. Vui lòng xác thực mã OTP gửi về email trước khi đăng nhập."
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác.");
        }

        //
        var userDetails = DomainUserDetailsService.UserWithId.fromUser(user);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String login = authentication.getName();
        return userRepository
            .findOneWithAuthoritiesByLogin(login.toLowerCase())
            .or(() -> userRepository.findOneWithAuthoritiesByEmailIgnoreCase(login))
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản người dùng: " + login)
            );
    }

    private AuthResponseDTO buildAuthResponse(Authentication authentication, User user, boolean rememberMe, String deviceInfo) {
        String accessToken = tokenProvider.createAccessToken(authentication, rememberMe);
        String refreshToken = tokenProvider.createRefreshToken(user, deviceInfo);
        long expiresIn = tokenProvider.getTokenValidityInSeconds(rememberMe);

        UserAuthDTO userAuthDTO = buildUserAuthDTO(user);
        return new AuthResponseDTO(accessToken, refreshToken, expiresIn, userAuthDTO);
    }

    private UserAuthDTO buildUserAuthDTO(User user) {
        UserAuthDTO dto = new UserAuthDTO(user);
        userProfileRepository.findOneByUserId(user.getId()).ifPresent(profile -> {
            dto.setProfile(new UserProfileAuthDTO(profile));
        });
        teacherVerificationRepository.findOneByUserId(user.getId()).ifPresent(verification -> {
            dto.setTeacherVerification(new TeacherVerificationAuthDTO(verification));
        });
        return dto;
    }

    @Override
    public AuthResponseDTO registerStudent(StudentRegisterRequestDTO registerRequestDTO, String clientDeviceInfo) {
        LOG.debug("REST request to register student: {}", registerRequestDTO.getUsername());
        return registerWithRole(registerRequestDTO, AuthoritiesConstants.STUDENT, clientDeviceInfo);
    }

    @Override
    public AuthResponseDTO registerInstructor(InstructorRegisterRequestDTO registerRequestDTO, String clientDeviceInfo) {
        LOG.debug("REST request to register instructor: {}", registerRequestDTO.getUsername());
        return registerWithRole(registerRequestDTO, AuthoritiesConstants.INSTRUCTOR, clientDeviceInfo);
    }

    @Override
    public AuthResponseDTO registerParents(ParentRegisterRequestDTO registerRequestDTO, String clientDeviceInfo) {
        LOG.debug("REST request to register parent: {}", registerRequestDTO.getUsername());
        return registerWithRole(registerRequestDTO, AuthoritiesConstants.PARENTS, clientDeviceInfo);
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO, String clientDeviceInfo) {
        LOG.debug("REST request to register user: {}", registerRequestDTO.getUsername());
        String targetRole = resolveTargetRole(registerRequestDTO.getRole());
        return registerWithRole(registerRequestDTO, targetRole, clientDeviceInfo);
    }

    @Override
    public AuthResponseDTO registerWithRole(RegisterRequestDTO registerRequestDTO, String targetRole, String clientDeviceInfo) {
        String login = registerRequestDTO.getUsername().toLowerCase().trim();
        String email = registerRequestDTO.getEmail().toLowerCase().trim();

        // Kiểm tra login đã tồn tại hay chưa
        userRepository.findOneByLogin(login).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new LoginAlreadyUsedException();
            }
        });

        // Kiểm tra email đã tồn tại hay chưa
        userRepository.findOneByEmailIgnoreCase(email).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });

        if (registerRequestDTO.getBirthDate() != null) {
            LocalDate now = LocalDate.now();

            // Ngày sinh không được ở tương lai
            if (registerRequestDTO.getBirthDate().isAfter(now)) {
                throw new BadRequestAlertException("Ngày sinh không hợp lệ.", "userManagement", "invalidBirthDate");
            }

            long age = ChronoUnit.YEARS.between(registerRequestDTO.getBirthDate(), now);

            // 1. Kiểm tra đối với Học sinh (lớp 1 đến lớp 9: từ 6 đến 15 tuổi)
            if (AuthoritiesConstants.STUDENT.equals(targetRole)) {
                if (age < 6) {
                    throw new BadRequestAlertException(
                        "Độ tuổi học sinh phải từ lớp 1 trở lên (tối thiểu 6 tuổi).",
                        "userManagement",
                        "ageTooYoungForStudent"
                    );
                }
                if (age > 15) {
                    throw new BadRequestAlertException(
                        "Nền tảng hiện chỉ hỗ trợ học sinh từ lớp 1 đến lớp 9 (tối đa 15 tuổi).",
                        "userManagement",
                        "ageTooOldForStudent"
                    );
                }
            }

            // 2. Kiểm tra đối với Giáo viên và Phụ huynh (phải từ 18 tuổi trở lên)
            if (AuthoritiesConstants.INSTRUCTOR.equals(targetRole) || AuthoritiesConstants.PARENTS.equals(targetRole)) {
                if (age < 18) {
                    throw new BadRequestAlertException(
                        "Giáo viên và Phụ huynh phải từ 18 tuổi trở lên.",
                        "userManagement",
                        "ageTooYoungForAdult"
                    );
                }
            }
        }

        // Kiểm tra số điện thoại
        String rawPhone = registerRequestDTO.getPhoneNumber();
        String phoneNumber = rawPhone != null && !rawPhone.isBlank() ? rawPhone.trim() : null;
        if (phoneNumber != null && userProfileRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã được đăng ký bởi tài khoản khác.");
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        newUser.setFirstName(registerRequestDTO.getFirstName());
        newUser.setLastName(registerRequestDTO.getLastName());
        newUser.setEmail(email);
        newUser.setActivated(false);
        newUser.setLangKey(registerRequestDTO.getLangKey() != null ? registerRequestDTO.getLangKey() : Constants.DEFAULT_LANGUAGE);

        // thêm role vào tài khoản
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        authorities.add(
            authorityRepository.findById(targetRole).orElseThrow(() -> new IllegalStateException("Missing authority: " + targetRole))
        );
        newUser.setAuthorities(authorities);

        User savedUser = userRepository.saveAndFlush(newUser);
        clearUserCaches(savedUser);

        // Tạo UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(savedUser);
        userProfile.setPhoneNumber(phoneNumber);
        userProfile.setBirthDate(registerRequestDTO.getBirthDate());
        userProfile.setUpdatedAt(Instant.now());

        // DÙNG PATTERN MATCHING INSTANCEOF ĐỂ LẤY DỮ LIỆU RIÊNG TỪNG CONs
        if (registerRequestDTO instanceof StudentRegisterRequestDTO studentReq) {
            userProfile.setSchoolName(studentReq.getSchoolName());
            userProfile.setGradeLevel(studentReq.getGradeLevel());
        } else if (registerRequestDTO instanceof InstructorRegisterRequestDTO instructorReq) {
            userProfile.setSchoolName(instructorReq.getSchoolName());
            userProfile.setGradeLevel(instructorReq.getGradeLevel());
            userProfile.setBio(instructorReq.getBio());
            userProfile.setInstructorStatus(InstructorApplicationStatus.PENDING);

            // cần thêm teacher verification nữa (phần admin cua lóng)
        } else if (registerRequestDTO instanceof ParentRegisterRequestDTO parentReq) {
            userProfile.setGradeLevel(parentReq.getChildGradeLevel());
        }
        userProfileRepository.saveAndFlush(userProfile);

        // Tự động gửi mã OTP xác thực tài khoản qua email
        try {
            SendOtpRequestDTO sendOtpRequest = new SendOtpRequestDTO();
            sendOtpRequest.setEmail(email);
            sendOtpRequest.setPurpose(OtpPurpose.REGISTER);
            otpService.sendOtp(sendOtpRequest, null);
            LOG.info("Auto-sent registration activation OTP to email: {}", email);
        } catch (Exception e) {
            LOG.warn("Failed to auto-send registration OTP to {}: {}", email, e.getMessage());
        }

        UserAuthDTO userAuthDTO = buildUserAuthDTO(savedUser);
        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setUser(userAuthDTO);
        return responseDTO;
    }

    private String resolveTargetRole(String role) {
        if (role == null || role.isBlank()) {
            return AuthoritiesConstants.STUDENT;
        }
        String normalized = role.trim().toUpperCase();
        if (normalized.equals("INSTRUCTOR") || normalized.equals(AuthoritiesConstants.INSTRUCTOR)) {
            return AuthoritiesConstants.INSTRUCTOR;
        }
        if (normalized.equals("PARENT") || normalized.equals(AuthoritiesConstants.PARENT)) {
            return AuthoritiesConstants.PARENT;
        }
        if (normalized.equals("STUDENT") || normalized.equals(AuthoritiesConstants.STUDENT)) {
            return AuthoritiesConstants.STUDENT;
        }
        if (normalized.equals("PARENTS") || normalized.equals(AuthoritiesConstants.PARENTS)) {
            return AuthoritiesConstants.PARENTS;
        }
        throw new BadRequestAlertException("Vai trò này hiện chưa được hỗ trợ đăng ký trực tuyến.", "userManagement", "roleNotSupported");
    }

    // Phần này bỏ chuyển giao cho Long
    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userProfileRepository.findOneByUserId(existingUser.getId()).ifPresent(userProfileRepository::delete);
        teacherVerificationRepository.findOneByUserId(existingUser.getId()).ifPresent(teacherVerificationRepository::delete);
        userRepository.delete(existingUser);
        userRepository.flush();
        clearUserCaches(existingUser);
        return true;
    }

    private void clearUserCaches(User user) {
        if (cacheManager != null) {
            if (cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE) != null) {
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
            }
            if (user.getEmail() != null && cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE) != null) {
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
            }
        }
    }

    private String resolveDeviceInfo(LoginVM loginVM, String clientDeviceInfo) {
        return resolveDeviceInfo(loginVM.getDeviceInfo(), clientDeviceInfo);
    }

    private String resolveDeviceInfo(String deviceInfo, String clientDeviceInfo) {
        if (deviceInfo != null && !deviceInfo.isBlank()) {
            return deviceInfo;
        }
        return clientDeviceInfo != null && !clientDeviceInfo.isBlank() ? clientDeviceInfo : "Unknown Device";
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
