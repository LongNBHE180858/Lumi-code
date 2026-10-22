import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';
import { apiSendOtp, apiVerifyOtp } from 'app/shared/api/auth.api';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState = {
  loading: false,
  activationSuccess: false,
  activationFailure: false,
  otpSentSuccess: false,
  errorMessage: null as string | null,
  successMessage: null as string | null,
};

export type ActivateState = Readonly<typeof initialState>;

// Actions

export const activateAction = createAsyncThunk('activate/activate_account', async (key: string) => axios.get(`api/activate?key=${key}`), {
  serializeError: serializeAxiosError,
});

export const sendOtpAction = createAsyncThunk(
  'activate/send_otp',
  async ({ email, purpose = 'REGISTER' }: { email: string; purpose?: string }) => apiSendOtp(email, purpose),
  { serializeError: serializeAxiosError },
);

export const verifyOtpAction = createAsyncThunk(
  'activate/verify_otp',
  async ({ email, otpCode, purpose = 'REGISTER' }: { email: string; otpCode: string; purpose?: string }) =>
    apiVerifyOtp(email, otpCode, purpose),
  { serializeError: serializeAxiosError },
);

export const ActivateSlice = createSlice({
  name: 'activate',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      // Activate by link key
      .addCase(activateAction.pending, () => ({ ...initialState, loading: true }))
      .addCase(activateAction.rejected, (state, action) => {
        state.loading = false;
        state.activationFailure = true;
        state.errorMessage = action.error.message || 'Kích hoạt thất bại!';
      })
      .addCase(activateAction.fulfilled, state => {
        state.loading = false;
        state.activationSuccess = true;
      })
      // Send OTP
      .addCase(sendOtpAction.pending, state => {
        state.loading = true;
        state.errorMessage = null;
        state.otpSentSuccess = false;
      })
      .addCase(sendOtpAction.rejected, (state, action) => {
        state.loading = false;
        state.otpSentSuccess = false;
        state.errorMessage = action.error.message || 'Gửi OTP thất bại!';
      })
      .addCase(sendOtpAction.fulfilled, state => {
        state.loading = false;
        state.otpSentSuccess = true;
        state.successMessage = 'Đã gửi mã OTP thành công!';
      })
      // Verify OTP
      .addCase(verifyOtpAction.pending, state => {
        state.loading = true;
        state.errorMessage = null;
        state.activationFailure = false;
      })
      .addCase(verifyOtpAction.rejected, (state, action) => {
        state.loading = false;
        state.activationFailure = true;
        state.errorMessage = action.error.message || 'Xác thực OTP thất bại!';
      })
      .addCase(verifyOtpAction.fulfilled, state => {
        state.loading = false;
        state.activationSuccess = true;
        state.successMessage = 'Xác thực tài khoản thành công!';
      });
  },
});

export const { reset } = ActivateSlice.actions;

// Reducer
export default ActivateSlice.reducer;
