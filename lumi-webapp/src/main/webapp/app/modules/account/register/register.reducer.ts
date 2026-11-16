import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { apiRegister, apiRegisterStudent, apiRegisterInstructor, apiRegisterParents } from 'app/shared/api/auth.api';

import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  birthDate?: string;
  phoneNumber?: string;
  role?: string;
  schoolName?: string;
  gradeLevel?: number;
  childGradeLevel?: number;
  studentCode?: string;
  subjects?: string[];
  yearsOfExperience?: number;
  bio?: string;
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
    const res = await apiRegister({ ...data, login: data.username });
    return { data: res.data, email: data.email };
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

export const handleRegisterInstructor = createAsyncThunk(
  'register/create_teacher_account',
  async (data: RegisterPayload) => {
    const res = await apiRegisterInstructor({ ...data, login: data.username });
    return { data: res.data, email: data.email };
  },
  { serializeError: serializeAxiosError },
);

export const handleRegisterParents = createAsyncThunk(
  'register/create_parent_account',
  async (data: RegisterPayload) => {
    const res = await apiRegisterParents({ ...data, login: data.username });
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
      .addCase(handleRegisterInstructor.pending, state => {
        state.loading = true;
        state.registrationFailure = false;
        state.errorMessage = null;
      })
      .addCase(handleRegisterInstructor.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.errorMessage = action.error.message || 'Đăng ký tài khoản Giáo viên thất bại';
      })
      .addCase(handleRegisterInstructor.fulfilled, (state, action) => {
        state.loading = false;
        state.registrationSuccess = true;
        state.registeredEmail = action.payload.email;
        state.successMessage = 'Đăng ký tài khoản Giáo viên thành công! Vui lòng xác thực tài khoản.';
      })
      .addCase(handleRegisterParents.pending, state => {
        state.loading = true;
        state.registrationFailure = false;
        state.errorMessage = null;
      })
      .addCase(handleRegisterParents.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.errorMessage = action.error.message || 'Đăng ký tài khoản Phụ huynh thất bại';
      })
      .addCase(handleRegisterParents.fulfilled, (state, action) => {
        state.loading = false;
        state.registrationSuccess = true;
        state.registeredEmail = action.payload.email;
        state.successMessage = 'Đăng ký tài khoản Phụ huynh thành công! Vui lòng xác thực tài khoản.';
      });
  },
});

export const { reset } = RegisterSlice.actions;

// Reducer
export default RegisterSlice.reducer;
