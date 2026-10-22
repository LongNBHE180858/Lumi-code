package com.lumi.web.rest;

import com.lumi.service.AuthService;
import com.lumi.service.OtpService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.PasswordChangeDTO;
import com.lumi.service.dto.auth.AuthResponseDTO;
import com.lumi.service.dto.auth.RefreshTokenRequestVM;
import com.lumi.service.dto.auth.RegisterVM;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.otp.SendOtpRequestDTO;
import com.lumi.service.dto.auth.otp.VerifyOtpRequestDTO;
import com.lumi.service.dto.auth.session.UserSessionDTO;
import com.lumi.service.dto.auth.twofa.TwoFaInitRequestDTO;
import com.lumi.service.dto.auth.twofa.TwoFaResponseDTO;
import com.lumi.service.dto.auth.twofa.TwoFactorConfirmRequestDTO;
import com.lumi.web.rest.vm.LoginVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Gửi hoặc gửi lại mã OTP qua email (có đếm 60s)")
    public ResponseEntity<APIResponse<OtpResponseDTO>> sendOtp(
        @Valid @RequestBody SendOtpRequestDTO request,
        HttpServletRequest httpRequest
    ) {
        LOG.debug("REST request to send OTP to email: {} for purpose: {}", request.getEmail(), request.getPurpose());
        String ipAddress = extractClientIp(httpRequest);
        OtpResponseDTO response = otpService.sendOtp(request, ipAddress);
        return ResponseEntity.ok(
            APIResponse.<OtpResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message(response.getMessage())
                .data(response)
                .build()
        );
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Xác thực mã OTP 6 chữ số")
    public ResponseEntity<APIResponse<OtpResponseDTO>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        LOG.debug("REST request to verify OTP for email: {} and purpose: {}", request.getEmail(), request.getPurpose());
        OtpResponseDTO response = otpService.verifyOtp(request);
        return ResponseEntity.ok(
            APIResponse.<OtpResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message(response.getMessage())
                .data(response)
                .build()
        );
    }

    @PostMapping("/forgot-password/init")
    @Operation(summary = "Quên mật khẩu bước 1: Gửi mã OTP xác nhận về email")
    public ResponseEntity<APIResponse<OtpResponseDTO>> forgotPasswordInit(
        @Valid @RequestBody com.lumi.service.dto.auth.password.ForgotPasswordInitRequestDTO request,
        HttpServletRequest httpRequest
    ) {
        LOG.debug("REST request to init forgot password for email: {}", request.getEmail());
        String ipAddress = extractClientIp(httpRequest);
        OtpResponseDTO response = authService.forgotPasswordInit(request, ipAddress);
        return ResponseEntity.ok(
            APIResponse.<OtpResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message(response.getMessage())
                .data(response)
                .build()
        );
    }

    @PostMapping("/forgot-password/finish")
    @Operation(summary = "Quên mật khẩu bước 2: Xác thực mã OTP và đặt mật khẩu mới")
    public ResponseEntity<APIResponse<Void>> forgotPasswordFinish(
        @Valid @RequestBody com.lumi.service.dto.auth.password.ForgotPasswordFinishRequestDTO request
    ) {
        LOG.debug("REST request to finish forgot password for email: {}", request.getEmail());
        authService.forgotPasswordFinish(request);
        return ResponseEntity.ok(
            APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại với mật khẩu mới.")
                .build()
        );
    }

    @PostMapping(path = { "/password/change", "/change-password" })
    @Operation(summary = "Đổi mật khẩu tài khoản (yêu cầu người dùng đã đăng nhập)")
    public ResponseEntity<APIResponse<Void>> changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        LOG.debug("REST request to change password");
        authService.changePassword(passwordChangeDto);
        return ResponseEntity.ok(
            APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đổi mật khẩu thành công. Vui lòng sử dụng mật khẩu mới cho các lần đăng nhập tiếp theo.")
                .build()
        );
    }

    @PostMapping("/student/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Đăng ký tài khoản dành cho Student")
    public ResponseEntity<APIResponse<AuthResponseDTO>> registerStudent(
        @Valid @RequestBody RegisterVM registerVM,
        HttpServletRequest request
    ) {
        LOG.debug("REST request to register student: {}", registerVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        AuthResponseDTO response = authService.registerStudent(registerVM, clientDeviceInfo);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(httpHeaders)
            .body(
                APIResponse.<AuthResponseDTO>builder()
                    .status(HttpStatus.CREATED.value())
                    .code(1000)
                    .message("Đăng ký tài khoản Học sinh thành công. Vui lòng xác thực mã OTP gửi về email để kích hoạt tài khoản.")
                    .data(response)
                    .build()
            );
    }

    @PostMapping("/teacher/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Đăng ký tài khoản dành cho Teacher")
    public ResponseEntity<APIResponse<AuthResponseDTO>> registerTeacher(
        @Valid @RequestBody RegisterVM registerVM,
        HttpServletRequest request
    ) {
        LOG.debug("REST request to register teacher: {}", registerVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        AuthResponseDTO response = authService.registerTeacher(registerVM, clientDeviceInfo);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(httpHeaders)
            .body(
                APIResponse.<AuthResponseDTO>builder()
                    .status(HttpStatus.CREATED.value())
                    .code(1000)
                    .message("Đăng ký tài khoản Giáo viên thành công. Vui lòng xác thực mã OTP gửi về email để kích hoạt tài khoản.")
                    .data(response)
                    .build()
            );
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Đăng ký tài khoản chung (mặc định Student hoặc truyền role trong body)")
    public ResponseEntity<APIResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterVM registerVM, HttpServletRequest request) {
        LOG.debug("REST request to register: {}", registerVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        AuthResponseDTO response = authService.register(registerVM, clientDeviceInfo);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(httpHeaders)
            .body(
                APIResponse.<AuthResponseDTO>builder()
                    .status(HttpStatus.CREATED.value())
                    .code(1000)
                    .message("Đăng ký tài khoản thành công. Vui lòng xác thực mã OTP gửi về email để kích hoạt tài khoản.")
                    .data(response)
                    .build()
            );
    }

    @PostMapping("/student/login")
    @Operation(summary = "Đăng nhập dành cho Student")
    public ResponseEntity<APIResponse<AuthResponseDTO>> loginStudent(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        LOG.debug("REST request to login student: {}", loginVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        String clientIp = extractClientIp(request);
        AuthResponseDTO response = authService.loginStudent(loginVM, clientDeviceInfo, clientIp);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        String message = Boolean.TRUE.equals(response.getIs2faRequired())
            ? "Tài khoản đang bật bảo mật 2 lớp (2FA). Vui lòng nhập mã OTP đã gửi về email."
            : "Đăng nhập Học sinh thành công";
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(APIResponse.<AuthResponseDTO>builder().status(HttpStatus.OK.value()).code(1000).message(message).data(response).build());
    }

    @PostMapping("/teacher/login")
    @Operation(summary = "Đăng nhập dành cho Teacher")
    public ResponseEntity<APIResponse<AuthResponseDTO>> loginTeacher(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        LOG.debug("REST request to login teacher: {}", loginVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        String clientIp = extractClientIp(request);
        AuthResponseDTO response = authService.loginTeacher(loginVM, clientDeviceInfo, clientIp);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        String message = Boolean.TRUE.equals(response.getIs2faRequired())
            ? "Tài khoản đang bật bảo mật 2 lớp (2FA). Vui lòng nhập mã OTP đã gửi về email."
            : "Đăng nhập Giáo viên thành công";
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(APIResponse.<AuthResponseDTO>builder().status(HttpStatus.OK.value()).code(1000).message(message).data(response).build());
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập chung")
    public ResponseEntity<APIResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        LOG.debug("REST request to login: {}", loginVM.getUsername());
        String clientDeviceInfo = extractDeviceInfo(request);
        String clientIp = extractClientIp(request);
        AuthResponseDTO response = authService.login(loginVM, clientDeviceInfo, clientIp);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        String message = Boolean.TRUE.equals(response.getIs2faRequired())
            ? "Tài khoản đang bật bảo mật 2 lớp (2FA). Vui lòng nhập mã OTP đã gửi về email."
            : "Đăng nhập thành công";
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(APIResponse.<AuthResponseDTO>builder().status(HttpStatus.OK.value()).code(1000).message(message).data(response).build());
    }

    @PostMapping("/login/2fa")
    @Operation(summary = "Xác thực 2FA để hoàn tất đăng nhập")
    public ResponseEntity<APIResponse<AuthResponseDTO>> login2fa(
        @Valid @RequestBody com.lumi.web.rest.vm.Login2faVM login2faVM,
        HttpServletRequest request
    ) {
        LOG.debug("REST request to complete 2FA login for email: {}", login2faVM.getEmail());
        String clientDeviceInfo = extractDeviceInfo(request);
        AuthResponseDTO response = authService.login2fa(login2faVM, clientDeviceInfo);

        var httpHeaders = new HttpHeaders();
        if (response.getAccessToken() != null) {
            httpHeaders.setBearerAuth(response.getAccessToken());
        }
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(
                APIResponse.<AuthResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .code(1000)
                    .message("Đăng nhập 2FA thành công")
                    .data(response)
                    .build()
            );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<APIResponse<AuthResponseDTO>> refreshToken(
        @Valid @RequestBody RefreshTokenRequestVM refreshTokenRequest,
        HttpServletRequest request
    ) {
        LOG.debug("REST request to refresh access token");
        String clientDeviceInfo = extractDeviceInfo(request);
        AuthResponseDTO response = authService.refreshToken(refreshTokenRequest, clientDeviceInfo);

        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(response.getAccessToken());
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(
                APIResponse.<AuthResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .code(1000)
                    .message("Làm mới access token thành công")
                    .data(response)
                    .build()
            );
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất")
    public ResponseEntity<APIResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequestVM refreshTokenRequest) {
        LOG.debug("REST request to logout");
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null) {
            authService.logout(refreshTokenRequest.getRefreshToken());
        }
        return ResponseEntity.ok(
            APIResponse.<Void>builder().status(HttpStatus.OK.value()).code(1000).message("Đăng xuất thành công").build()
        );
    }

    @GetMapping("/2fa/status")
    @Operation(summary = "Trạng thái 2fa")
    public ResponseEntity<APIResponse<TwoFaResponseDTO>> status2fa() {
        LOG.debug("REST request to get 2fa status of current user");
        TwoFaResponseDTO response = authService.get2faStatus();
        return ResponseEntity.ok(
            APIResponse.<TwoFaResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Lấy trạng thái 2FA thành công")
                .data(response)
                .build()
        );
    }

    @PostMapping("/2fa/init")
    @Operation(summary = "Gửi mã OTP qua email để bật/tắt 2FA")
    public ResponseEntity<APIResponse<Void>> init2fa(
        @RequestBody(required = false) TwoFaInitRequestDTO request,
        HttpServletRequest httpRequest
    ) {
        LOG.debug("REST request to initiate 2fa update for current user");
        String ipAddress = extractClientIp(httpRequest);
        authService.update2faStatus(request, ipAddress);
        return ResponseEntity.ok(
            APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Mã xác thực OTP đã được gửi về email của bạn.")
                .build()
        );
    }

    @PostMapping("/2fa/confirm")
    @Operation(summary = "Xác nhận mã OTP để bật/tắt 2FA")
    public ResponseEntity<APIResponse<TwoFaResponseDTO>> confirm2fa(@RequestBody TwoFactorConfirmRequestDTO request) {
        LOG.debug("REST request to confirm 2fa status of current user");
        TwoFaResponseDTO response = authService.confirm2faStatus(request);
        return ResponseEntity.ok(
            APIResponse.<TwoFaResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Cập nhật trạng thái 2FA thành công")
                .data(response)
                .build()
        );
    }

    @GetMapping("/account/sessions")
    @Operation(summary = "Lấy danh sách các phiên đăng nhập của tài khoản hiện tại")
    public List<UserSessionDTO> getCurrentUserSessions() {
        LOG.debug("REST request to get sessions of current user");
        return authService.getCurrentUserSessions();
    }

    @DeleteMapping("/account/sessions/{id}")
    @Operation(summary = "Đăng xuất từ xa / thu hồi một phiên đăng nhập cụ thể")
    public ResponseEntity<Void> revokeSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to revoke session: {}", id);
        authService.revokeSession(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account/sessions/other")
    @Operation(summary = "Đăng xuất tất cả các thiết bị khác (trừ thiết bị hiện tại nếu truyền currentSessionId)")
    public ResponseEntity<Void> revokeOtherSessions(@RequestParam(value = "currentSessionId", required = false) Long currentSessionId) {
        LOG.debug("REST request to revoke all other sessions with currentSessionId: {}", currentSessionId);
        authService.revokeOtherSessions(currentSessionId);
        return ResponseEntity.noContent().build();
    }

    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && !userAgent.isBlank() ? userAgent : "Unknown Device";
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
