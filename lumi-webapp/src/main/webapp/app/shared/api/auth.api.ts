import axios from 'axios';
import { Storage } from 'react-jhipster';
import type { ApiResponse } from 'app/shared/model/api-response.model';

const AUTH_API = 'api/auth';
export const REFRESH_TOKEN_KEY = 'lumi-refresh-token';
export const ACCESS_TOKEN_KEY = 'jhi-authenticationToken'; // giữ cũ để axios interceptor vẫn hoạt động

// --- Token helpers ---
export const saveTokens = (accessToken: string, refreshToken: string, rememberMe: boolean) => {
  if (rememberMe) {
    Storage.local.set(ACCESS_TOKEN_KEY, accessToken);
    Storage.local.set(REFRESH_TOKEN_KEY, refreshToken);
  } else {
    Storage.session.set(ACCESS_TOKEN_KEY, accessToken);
    Storage.session.set(REFRESH_TOKEN_KEY, refreshToken);
  }
};

export const getRefreshToken = () => Storage.local.get(REFRESH_TOKEN_KEY) || Storage.session.get(REFRESH_TOKEN_KEY);

export const clearTokens = () => {
  [ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY].forEach(key => {
    Storage.local.remove(key);
    Storage.session.remove(key);
  });
};

// --- API calls ---
export const apiLogin = (username: string, password: string, rememberMe = false) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/login`, { username, password, rememberMe });

export const apiLogin2fa = (email: string, otpCode: string, rememberMe = false) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/login/2fa`, { email, otpCode, rememberMe });

export const apiSendOtp = (email: string, purpose: string) => axios.post<ApiResponse<any>>(`${AUTH_API}/otp/send`, { email, purpose });

export const apiVerifyOtp = (email: string, otpCode: string, purpose: string) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/otp/verify`, { email, otpCode, purpose });

export const apiLogout = (refreshToken: string) => axios.post<ApiResponse<void>>(`${AUTH_API}/logout`, { refreshToken });

export const apiForgotPasswordInit = (email: string) => axios.post<ApiResponse<any>>(`${AUTH_API}/forgot-password/init`, { email });

export const apiForgotPasswordFinish = (email: string, otpCode: string, newPassword: string) =>
  axios.post<ApiResponse<void>>(`${AUTH_API}/forgot-password/finish`, { email, otpCode, newPassword });

export const apiChangePassword = (currentPassword: string, newPassword: string) =>
  axios.post<ApiResponse<void>>(`${AUTH_API}/password/change`, { currentPassword, newPassword });

export const apiGetAccount = () => axios.get<any>('api/account');

// --- Registration APIs ---
export const apiRegister = (data: any) => axios.post<ApiResponse<any>>(`${AUTH_API}/register`, data);
export const apiRegisterStudent = (data: any) => axios.post<ApiResponse<any>>(`${AUTH_API}/student/register`, data);
export const apiRegisterTeacher = (data: any) => axios.post<ApiResponse<any>>(`${AUTH_API}/teacher/register`, data);

// --- Role-specific Login APIs ---
export const apiLoginStudent = (username: string, password: string, rememberMe = false) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/student/login`, { username, password, rememberMe });
export const apiLoginTeacher = (username: string, password: string, rememberMe = false) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/teacher/login`, { username, password, rememberMe });

// --- Token Refresh ---
export const apiRefreshToken = (refreshToken: string, deviceInfo?: string) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/refresh`, { refreshToken, deviceInfo });

// --- Session Management APIs ---
export const apiGetSessions = () => axios.get<any[]>(`${AUTH_API}/account/sessions`);
export const apiRevokeSession = (sessionId: number | string) => axios.delete<void>(`${AUTH_API}/account/sessions/${sessionId}`);
export const apiRevokeOtherSessions = (currentSessionId?: number | string) =>
  axios.delete<void>(`${AUTH_API}/account/sessions/other`, { params: { currentSessionId } });

// --- 2FA Management APIs ---
export const apiGet2faStatus = () => axios.get<ApiResponse<any>>(`${AUTH_API}/2fa/status`);
export const apiInit2fa = (enable?: boolean) => axios.post<ApiResponse<void>>(`${AUTH_API}/2fa/init`, { enable });
export const apiConfirm2fa = (otpCode: string, enable?: boolean) =>
  axios.post<ApiResponse<any>>(`${AUTH_API}/2fa/confirm`, { otpCode, enable });
