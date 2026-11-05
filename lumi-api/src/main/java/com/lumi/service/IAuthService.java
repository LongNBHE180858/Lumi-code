package com.lumi.service;

import com.lumi.service.dto.PasswordChangeDTO;
import com.lumi.service.dto.auth.AuthResponseDTO;
import com.lumi.service.dto.auth.InstructorRegisterRequestDTO;
import com.lumi.service.dto.auth.ParentRegisterRequestDTO;
import com.lumi.service.dto.auth.RefreshTokenRequestVM;
import com.lumi.service.dto.auth.RegisterRequestDTO;
import com.lumi.service.dto.auth.StudentRegisterRequestDTO;
import com.lumi.service.dto.auth.otp.OtpResponseDTO;
import com.lumi.service.dto.auth.password.ForgotPasswordFinishRequestDTO;
import com.lumi.service.dto.auth.password.ForgotPasswordInitRequestDTO;
import com.lumi.service.dto.auth.session.UserSessionDTO;
import com.lumi.service.dto.auth.twofa.TwoFaInitRequestDTO;
import com.lumi.service.dto.auth.twofa.TwoFaResponseDTO;
import com.lumi.service.dto.auth.twofa.TwoFactorConfirmRequestDTO;
import com.lumi.web.rest.vm.Login2faVM;
import com.lumi.web.rest.vm.LoginVM;
import java.util.List;

public interface IAuthService {
    AuthResponseDTO register(RegisterRequestDTO request, String clientDeviceInfo);

    AuthResponseDTO registerStudent(StudentRegisterRequestDTO request, String clientDeviceInfo);

    AuthResponseDTO registerInstructor(InstructorRegisterRequestDTO request, String clientDeviceInfo);

    AuthResponseDTO registerParents(ParentRegisterRequestDTO request, String clientDeviceInfo);

    AuthResponseDTO registerWithRole(RegisterRequestDTO request, String targetRole, String clientDeviceInfo);

    AuthResponseDTO login(LoginVM request, String clientDeviceInfo);

    AuthResponseDTO login(LoginVM request, String clientDeviceInfo, String ipAddress);

    AuthResponseDTO loginStudent(LoginVM request, String clientDeviceInfo);

    AuthResponseDTO loginStudent(LoginVM request, String clientDeviceInfo, String ipAddress);

    AuthResponseDTO loginTeacher(LoginVM request, String clientDeviceInfo);

    AuthResponseDTO loginTeacher(LoginVM request, String clientDeviceInfo, String ipAddress);

    AuthResponseDTO login2fa(Login2faVM request, String clientDeviceInfo);

    AuthResponseDTO refreshToken(RefreshTokenRequestVM request, String clientDeviceInfo);

    void logout(String refreshToken);

    List<UserSessionDTO> getCurrentUserSessions();

    void revokeSession(Long sessionId);

    void revokeOtherSessions(Long currentSessionId);

    void changePassword(PasswordChangeDTO request);

    OtpResponseDTO forgotPasswordInit(ForgotPasswordInitRequestDTO request, String ipAddress);

    void forgotPasswordFinish(ForgotPasswordFinishRequestDTO request);

    TwoFaResponseDTO get2faStatus();

    void update2faStatus(TwoFaInitRequestDTO request, String ipAddress);

    TwoFaResponseDTO confirm2faStatus(TwoFactorConfirmRequestDTO request);
}
