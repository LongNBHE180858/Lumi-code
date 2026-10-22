import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';
import { apiRegister, apiRegisterStudent, apiRegisterTeacher } from 'app/shared/api/auth.api';

import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  role?: string;
  schoolName?: string;
  gradeLevel?: number;
  langKey?: string;
  deviceInfo?: string;
}

const initialState = {
  loading: false,
  registrationSuccess: false,
  registrationFailure: false,
  registeredEmail: '' as string,
  errorMessage: null as string | null,
  successMessage: null as string | null,
};

export type RegisterState = Readonly<typeof initialState>;

// Actions
export const handleRegister = createAsyncThunk(
  'register/create_account',
  async (data: RegisterPayload) => {
    try {
      const res = await apiRegister({
        ...data,
        login: data.username,
      });
      return { data: res.data, email: data.email };
    } catch (err: any) {
      if (err.response && err.response.status === 404) {
        // Fallback to /api/register
        const res = await axios.post<any>('api/register', {
          login: data.username,
          email: data.email,
          password: data.password,
          firstName: data.firstName,
          lastName: data.lastName,
          langKey: data.langKey,
        });
        return { data: res.data, email: data.email };
      }
      throw err;
    }
  },
  { serializeError: serializeAxiosError },
);

export const handleRegisterStudent = createAsyncThunk(
  'register/create_student_account',
  async (data: RegisterPayload) => {
    const res = await apiRegisterStudent({ ...data, login: data.username });
    return { data: res.data, email: data.email };
  },
  { serializeError: serializeAxiosError },
);

export const handleRegisterTeacher = createAsyncThunk(
  'register/create_teacher_account',
  async (data: RegisterPayload) => {
    const res = await apiRegisterTeacher({ ...data, login: data.username });
    return { data: res.data, email: data.email };
  },
  { serializeError: serializeAxiosError },
);

export const RegisterSlice = createSlice({
  name: 'register',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(handleRegister.pending, state => {
        state.loading = true;
        state.registrationFailure = false;
        state.errorMessage = null;
      })
      .addCase(handleRegister.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.errorMessage = action.error.message || 'Đăng ký tài khoản thất bại';
      })
      .addCase(handleRegister.fulfilled, (state, action) => {
        state.loading = false;
        state.registrationSuccess = true;
        state.registeredEmail = action.payload.email;
        state.successMessage = 'Đăng ký thành công! Vui lòng xác thực tài khoản.';
      })
      .addCase(handleRegisterStudent.pending, state => {
        state.loading = true;
        state.registrationFailure = false;
        state.errorMessage = null;
      })
      .addCase(handleRegisterStudent.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.errorMessage = action.error.message || 'Đăng ký tài khoản Học sinh thất bại';
      })
      .addCase(handleRegisterStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.registrationSuccess = true;
        state.registeredEmail = action.payload.email;
        state.successMessage = 'Đăng ký tài khoản Học sinh thành công! Vui lòng xác thực tài khoản.';
      })
      .addCase(handleRegisterTeacher.pending, state => {
        state.loading = true;
        state.registrationFailure = false;
        state.errorMessage = null;
      })
      .addCase(handleRegisterTeacher.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.errorMessage = action.error.message || 'Đăng ký tài khoản Giáo viên thất bại';
      })
      .addCase(handleRegisterTeacher.fulfilled, (state, action) => {
        state.loading = false;
        state.registrationSuccess = true;
        state.registeredEmail = action.payload.email;
        state.successMessage = 'Đăng ký tài khoản Giáo viên thành công! Vui lòng xác thực tài khoản.';
      });
  },
});

export const { reset } = RegisterSlice.actions;

// Reducer
export default RegisterSlice.reducer;
