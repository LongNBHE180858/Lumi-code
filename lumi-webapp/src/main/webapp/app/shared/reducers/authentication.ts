import { Storage } from 'react-jhipster';

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { AxiosResponse } from 'axios';

import { AppThunk } from 'app/config/store';
import { AUTHENTICATION_TOKEN_KEY } from 'app/shared/jhipster/constants';
import { setLocale } from 'app/shared/reducers/locale';

import {
  saveTokens,
  clearTokens,
  getRefreshToken,
  apiLogout,
  apiLogin,
  apiLogin2fa,
  apiLoginStudent,
  apiLoginTeacher,
  apiRefreshToken,
  apiGetAccount,
  apiGetSessions,
  apiRevokeSession,
  apiRevokeOtherSessions,
} from 'app/shared/api/auth.api';
import { serializeAxiosError } from './reducer.utils';

export const initialState = {
  loading: false,
  isAuthenticated: false,
  loginSuccess: false,
  loginError: false, // Errors returned from server side
  showModalLogin: false,
  account: {} as any,
  errorMessage: null as unknown as string, // Errors returned from server side
  redirectMessage: null as unknown as string,
  sessionHasBeenFetched: false,
  logoutUrl: null as unknown as string,
  sessions: [] as any[],
  twoFactorRequired: false,
  twoFactorEmail: null as string | null,
};

export type AuthenticationState = Readonly<typeof initialState>;

// Actions

export const getSession = (): AppThunk => async (dispatch, getState) => {
  await dispatch(getAccount());

  const { account } = getState().authentication;
  if (account?.langKey) {
    const langKey = Storage.session.get('locale', account.langKey);
    await dispatch(setLocale(langKey));
  }
};

export const getAccount = createAsyncThunk('authentication/get_account', async () => apiGetAccount(), {
  serializeError: serializeAxiosError, // hỗ trợ chuyển thành json
});

interface IAuthParams {
  username: string;
  password: string;
  rememberMe?: boolean;
}

export const authenticate = createAsyncThunk(
  'authentication/login',
  async (auth: IAuthParams) => apiLogin(auth.username, auth.password, auth.rememberMe),
  {
    serializeError: serializeAxiosError,
  },
);

export const authenticateStudent = createAsyncThunk(
  'authentication/login_student',
  async (auth: IAuthParams) => apiLoginStudent(auth.username, auth.password, auth.rememberMe),
  {
    serializeError: serializeAxiosError,
  },
);

export const authenticateTeacher = createAsyncThunk(
  'authentication/login_teacher',
  async (auth: IAuthParams) => apiLoginTeacher(auth.username, auth.password, auth.rememberMe),
  {
    serializeError: serializeAxiosError,
  },
);

export const authenticate2fa = createAsyncThunk(
  'authentication/login_2fa',
  async ({ email, otpCode, rememberMe }: { email: string; otpCode: string; rememberMe?: boolean }) =>
    apiLogin2fa(email, otpCode, rememberMe),
  {
    serializeError: serializeAxiosError,
  },
);

export const login2fa: (email: string, otpCode: string, rememberMe?: boolean) => AppThunk<Promise<any>> =
  (email, otpCode, rememberMe = false) =>
  async dispatch => {
    const result = await dispatch(authenticate2fa({ email, otpCode, rememberMe }));
    const response = result.payload as AxiosResponse;
    const data = response?.data?.data;
    if (data?.access_token) {
      saveTokens(data.access_token, data.refresh_token ?? '', rememberMe);
      dispatch(AuthenticationSlice.actions.resetTwoFactor());
      dispatch(getSession());
    }
    return data;
  };

export const login: (username: string, password: string, rememberMe?: boolean) => AppThunk =
  (username, password, rememberMe = false) =>
  async dispatch => {
    const result = await dispatch(authenticate({ username, password, rememberMe }));
    const response = result.payload as AxiosResponse;
    const data = response?.data?.data;
    if (data?.access_token) {
      saveTokens(data.access_token, data.refresh_token ?? '', rememberMe);
    }
    dispatch(getSession());
  };

export const loginStudent: (username: string, password: string, rememberMe?: boolean) => AppThunk =
  (username, password, rememberMe = false) =>
  async dispatch => {
    const result = await dispatch(authenticateStudent({ username, password, rememberMe }));
    const response = result.payload as AxiosResponse;
    const data = response?.data?.data;
    if (data?.access_token) {
      saveTokens(data.access_token, data.refresh_token ?? '', rememberMe);
    }
    dispatch(getSession());
  };

export const loginTeacher: (username: string, password: string, rememberMe?: boolean) => AppThunk =
  (username, password, rememberMe = false) =>
  async dispatch => {
    const result = await dispatch(authenticateTeacher({ username, password, rememberMe }));
    const response = result.payload as AxiosResponse;
    const data = response?.data?.data;
    if (data?.access_token) {
      saveTokens(data.access_token, data.refresh_token ?? '', rememberMe);
    }
    dispatch(getSession());
  };

export const refreshSession = (): AppThunk => async dispatch => {
  const refreshToken = getRefreshToken();
  if (refreshToken) {
    try {
      const res = await apiRefreshToken(refreshToken);
      const data = res.data?.data;
      if (data?.access_token) {
        saveTokens(data.access_token, data.refresh_token ?? refreshToken, true);
        dispatch(getSession());
      }
    } catch {
      dispatch(logoutSession());
    }
  }
};

export const getActiveSessions = createAsyncThunk('authentication/get_sessions', async () => apiGetSessions(), {
  serializeError: serializeAxiosError,
});

export const revokeSessionThunk = createAsyncThunk(
  'authentication/revoke_session',
  async (sessionId: number | string) => {
    await apiRevokeSession(sessionId);
    return sessionId;
  },
  { serializeError: serializeAxiosError },
);

export const revokeOtherSessionsThunk = createAsyncThunk(
  'authentication/revoke_other_sessions',
  async (currentSessionId?: number | string) => {
    await apiRevokeOtherSessions(currentSessionId);
    return currentSessionId;
  },
  { serializeError: serializeAxiosError },
);

export const clearAuthToken = () => {
  if (Storage.local.get(AUTHENTICATION_TOKEN_KEY)) {
    Storage.local.remove(AUTHENTICATION_TOKEN_KEY);
  }
  if (Storage.session.get(AUTHENTICATION_TOKEN_KEY)) {
    Storage.session.remove(AUTHENTICATION_TOKEN_KEY);
  }
};

export const logout: () => AppThunk = () => dispatch => {
  clearTokens();
  clearAuthToken();
  dispatch(logoutSession());
};

export const logoutWithApi = (): AppThunk => async dispatch => {
  const refreshToken = getRefreshToken();
  if (refreshToken) {
    try {
      await apiLogout(refreshToken);
    } catch {
      /**/
    }
  }
  clearTokens();
  clearAuthToken();
  dispatch(logoutSession());
};

export const clearAuthentication = messageKey => dispatch => {
  clearAuthToken();
  dispatch(authError(messageKey));
  dispatch(clearAuth());
};

export const AuthenticationSlice = createSlice({
  name: 'authentication',
  initialState,
  reducers: {
    logoutSession() {
      return {
        ...initialState,
        showModalLogin: true,
      };
    },
    authError(state, action) {
      return {
        ...state,
        showModalLogin: true,
        redirectMessage: action.payload,
      };
    },
    clearAuth(state) {
      return {
        ...state,
        loading: false,
        showModalLogin: true,
        isAuthenticated: false,
      };
    },
    resetTwoFactor(state) {
      state.twoFactorRequired = false;
      state.twoFactorEmail = null;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(authenticate.rejected, (state, action) => ({
        ...initialState,
        errorMessage: action.error.message!,
        showModalLogin: true,
        loginError: true,
      }))
      .addCase(authenticate.fulfilled, (state, action) => {
        const data = action.payload?.data?.data;
        return {
          ...state,
          loading: false,
          loginError: false,
          showModalLogin: false,
          loginSuccess: !data?.is_2fa_required,
          twoFactorRequired: Boolean(data?.is_2fa_required),
          twoFactorEmail: data?.email ?? null,
        };
      })
      .addCase(getAccount.rejected, (state, action) => ({
        ...state,
        loading: false,
        isAuthenticated: false,
        sessionHasBeenFetched: true,
        showModalLogin: true,
        errorMessage: action.error.message!,
      }))
      .addCase(getAccount.fulfilled, (state, action) => {
        const isAuthenticated = action.payload?.data?.activated;
        return {
          ...state,
          isAuthenticated,
          loading: false,
          sessionHasBeenFetched: true,
          account: action.payload.data,
        };
      })
      .addCase(authenticate.pending, state => {
        state.loading = true;
      })
      .addCase(authenticateStudent.pending, state => {
        state.loading = true;
      })
      .addCase(authenticateTeacher.pending, state => {
        state.loading = true;
      })
      .addCase(authenticate2fa.pending, state => {
        state.loading = true;
        state.loginError = false;
        state.errorMessage = null as unknown as string;
      })
      .addCase(authenticateStudent.rejected, (state, action) => ({
        ...initialState,
        errorMessage: action.error.message!,
        showModalLogin: true,
        loginError: true,
      }))
      .addCase(authenticateTeacher.rejected, (state, action) => ({
        ...initialState,
        errorMessage: action.error.message!,
        showModalLogin: true,
        loginError: true,
      }))
      .addCase(authenticate2fa.rejected, (state, action) => ({
        ...state,
        loading: false,
        loginError: true,
        errorMessage: action.error.message!,
      }))
      .addCase(authenticateStudent.fulfilled, (state, action) => {
        const data = action.payload?.data?.data;
        return {
          ...state,
          loading: false,
          loginError: false,
          showModalLogin: false,
          loginSuccess: !data?.is_2fa_required,
          twoFactorRequired: Boolean(data?.is_2fa_required),
          twoFactorEmail: data?.email ?? null,
        };
      })
      .addCase(authenticateTeacher.fulfilled, (state, action) => {
        const data = action.payload?.data?.data;
        return {
          ...state,
          loading: false,
          loginError: false,
          showModalLogin: false,
          loginSuccess: !data?.is_2fa_required,
          twoFactorRequired: Boolean(data?.is_2fa_required),
          twoFactorEmail: data?.email ?? null,
        };
      })
      .addCase(authenticate2fa.fulfilled, state => ({
        ...state,
        loading: false,
        loginError: false,
        showModalLogin: false,
        loginSuccess: true,
        twoFactorRequired: false,
      }))
      .addCase(getAccount.pending, state => {
        state.loading = true;
      })
      .addCase(getActiveSessions.fulfilled, (state, action) => {
        state.sessions = Array.isArray(action.payload?.data) ? action.payload.data : [];
      })
      .addCase(revokeSessionThunk.fulfilled, (state, action) => {
        state.sessions = state.sessions.filter(s => s.id !== action.payload);
      })
      .addCase(revokeOtherSessionsThunk.fulfilled, state => {
        state.sessions = state.sessions.slice(0, 1);
      });
  },
});

export const { logoutSession, authError, clearAuth, resetTwoFactor } = AuthenticationSlice.actions;

// Reducer
export default AuthenticationSlice.reducer;
