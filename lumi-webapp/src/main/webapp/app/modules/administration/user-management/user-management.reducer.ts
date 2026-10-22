import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { defaultValue } from 'app/shared/model/user.model';
import type { IUser } from 'app/shared/model/user.model';
import { IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

import { userApi } from './user-api';
import type { AdminCounts, AdminUserQueryParams } from './user-api';

const initialState = {
  loading: false,
  errorMessage: null as string | null,
  users: [] as readonly IUser[],
  authorities: [] as string[],
  user: defaultValue,
  updating: false,
  updateSuccess: false,
  totalItems: 0,
  adminCounts: null as AdminCounts | null,
};

// Async Actions

export const getUsers = createAsyncThunk('userManagement/fetch_users', (params: IQueryParams) => userApi.getUsers(params));

export const getUsersAsAdmin = createAsyncThunk('userManagement/fetch_users_as_admin', (params: AdminUserQueryParams) =>
  userApi.getUsersAsAdmin(params),
);
export const getAdminCounts = createAsyncThunk('userManagement/fetch_admin_counts', () => userApi.getAdminCounts());

export const getRoles = createAsyncThunk('userManagement/fetch_roles', () => userApi.getRoles());

export const getUser = createAsyncThunk('userManagement/fetch_user', (login: string) => userApi.getUser(login), {
  serializeError: serializeAxiosError,
});

export const createUser = createAsyncThunk(
  'userManagement/create_user',
  async (user: IUser, thunkAPI) => {
    const result = await userApi.createUser(user);
    thunkAPI.dispatch(getUsersAsAdmin({}));
    thunkAPI.dispatch(getAdminCounts());
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const updateUser = createAsyncThunk(
  'userManagement/update_user',
  async (user: IUser, thunkAPI) => {
    const result = await userApi.updateUser(user);
    thunkAPI.dispatch(getAdminCounts());
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const deleteUser = createAsyncThunk(
  'userManagement/delete_user',
  async (login: string, thunkAPI) => {
    const result = await userApi.deleteUser(login);
    thunkAPI.dispatch(getUsersAsAdmin({}));
    thunkAPI.dispatch(getAdminCounts());
    return result;
  },
  { serializeError: serializeAxiosError },
);

export type UserManagementState = Readonly<typeof initialState>;

export const UserManagementSlice = createSlice({
  name: 'userManagement',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getRoles.fulfilled, (state, action) => {
        state.authorities = action.payload.data;
      })
      .addCase(getRoles.pending, state => {
        state.authorities = [];
      })
      .addCase(getRoles.rejected, state => {
        state.authorities = [];
      })
      .addCase(getAdminCounts.fulfilled, (state, action) => {
        state.adminCounts = action.payload.data;
      })
      .addCase(getAdminCounts.pending, state => {
        state.adminCounts = null;
      })
      .addCase(getAdminCounts.rejected, state => {
        state.adminCounts = null;
      })
      .addCase(getUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.data;
      })
      .addCase(deleteUser.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.user = defaultValue;
      })
      .addMatcher(isFulfilled(getUsers, getUsersAsAdmin), (state, action) => {
        state.loading = false;
        state.users = action.payload.data;
        state.totalItems = Number.parseInt(action.payload.headers['x-total-count'], 10);
      })
      .addMatcher(isFulfilled(createUser, updateUser), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.user = action.payload.data;
      })
      .addMatcher(isPending(getUsers, getUsersAsAdmin, getUser), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createUser, updateUser, deleteUser), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      })
      .addMatcher(isRejected(getUsers, getUsersAsAdmin, getUser, getRoles, createUser, updateUser, deleteUser), (state, action) => {
        state.loading = false;
        state.updating = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message!;
      });
  },
});

export const { reset } = UserManagementSlice.actions;

// Reducer
export default UserManagementSlice.reducer;
