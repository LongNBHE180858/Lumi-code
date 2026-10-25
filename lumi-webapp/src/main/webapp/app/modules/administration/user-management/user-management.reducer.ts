import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { defaultValue } from 'app/shared/model/user.model';
import type { IUser } from 'app/shared/model/user.model';
import { IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

import { userApi } from './user-api';
import type { AdminUserQueryParams } from './user-api';

const initialState = {
  loading: false,
  errorMessage: null as string | null,
  users: [] as readonly IUser[],
  authorities: [] as string[],
  user: defaultValue,
  updating: false,
  updateSuccess: false,
  totalItems: 0,
};

// Async Actions

export const getUsers = createAsyncThunk('userManagement/fetch_users', (params: IQueryParams) => userApi.getUsers(params));

export const getUsersAsAdmin = createAsyncThunk('userManagement/fetch_users_as_admin', (params: AdminUserQueryParams, { signal }) =>
  userApi.getUsersAsAdmin(params, signal),
);
export const getRoles = createAsyncThunk('userManagement/fetch_roles', () => userApi.getRoles());

export const getUser = createAsyncThunk('userManagement/fetch_user', (login: string) => userApi.getUser(login), {
  serializeError: serializeAxiosError,
});

export const createUser = createAsyncThunk(
  'userManagement/create_user',
  async (user: IUser, thunkAPI) => {
    const result = await userApi.createUser(user);
    thunkAPI.dispatch(getUsersAsAdmin({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const setUserActivation = createAsyncThunk(
  'userManagement/set_user_activation',
  ({ login, activated }: { login: string; activated: boolean }) => userApi.setUserActivation(login, activated),
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
      .addCase(getUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.data;
      })
      .addCase(setUserActivation.fulfilled, (state, action) => {
        const updatedUser = action.payload.data;
        state.users = state.users.map(user => (user.login === updatedUser.login ? updatedUser : user));
        if (state.user.login === updatedUser.login) {
          state.user = updatedUser;
        }
      })
      .addMatcher(isFulfilled(getUsers, getUsersAsAdmin), (state, action) => {
        state.loading = false;
        state.users = action.payload.data;
        state.totalItems = Number.parseInt(action.payload.headers['x-total-count'], 10);
      })
      .addMatcher(isFulfilled(createUser), (state, action) => {
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
      .addMatcher(isPending(createUser), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      })
      .addMatcher(isRejected(getUsers, getUsersAsAdmin, getUser, getRoles, createUser, setUserActivation), (state, action) => {
        if (action.meta?.aborted) return;
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
