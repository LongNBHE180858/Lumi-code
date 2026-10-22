import { createAsyncThunk, createSlice, isPending, isRejected } from '@reduxjs/toolkit';
import { apiForgotPasswordFinish, apiForgotPasswordInit } from 'app/shared/api/auth.api';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState = {
  loading: false,
  resetPasswordSuccess: false,
  resetPasswordFailure: false,
  successMessage: null as string | null,
  errorMessage: null as string | null,
};

export type PasswordResetState = Readonly<typeof initialState>;

// Actions

export const handlePasswordResetInit = createAsyncThunk(
  'passwordReset/reset_password_init',
  async (email: string) => apiForgotPasswordInit(email),
  { serializeError: serializeAxiosError },
);

export const handlePasswordResetFinish = createAsyncThunk(
  'passwordReset/reset_password_finish',
  async (data: { email: string; otpCode?: string; newPassword: string; key?: string }) =>
    apiForgotPasswordFinish(data.email, data.otpCode || data.key || '', data.newPassword),
  { serializeError: serializeAxiosError },
);

export const PasswordResetSlice = createSlice({
  name: 'passwordReset',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(handlePasswordResetInit.fulfilled, () => ({
        ...initialState,
        loading: false,
        resetPasswordSuccess: true,
        successMessage: 'reset.request.messages.success',
      }))
      .addCase(handlePasswordResetFinish.fulfilled, () => ({
        ...initialState,
        loading: false,
        resetPasswordSuccess: true,
        successMessage: 'reset.finish.messages.success',
      }))
      .addMatcher(isPending(handlePasswordResetInit, handlePasswordResetFinish), state => {
        state.loading = true;
        state.errorMessage = null;
      })
      .addMatcher(isRejected(handlePasswordResetInit, handlePasswordResetFinish), (state, action) => ({
        ...initialState,
        loading: false,
        resetPasswordFailure: true,
        errorMessage: action.error.message || 'Thao tác thất bại!',
      }));
  },
});

export const { reset } = PasswordResetSlice.actions;

// Reducer
export default PasswordResetSlice.reducer;
