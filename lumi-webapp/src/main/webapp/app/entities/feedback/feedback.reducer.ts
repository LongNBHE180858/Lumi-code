import { createAsyncThunk, createSlice, isFulfilled, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { IFeedback, IFeedbackCreate, FeedbackStatus, FeedbackType } from 'app/shared/model/feedback.model';
import { EntityState, IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

export interface IFeedbackFilterParams extends IQueryParams {
  status?: FeedbackStatus;
  type?: FeedbackType;
}

const initialState: EntityState<IFeedback> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: {},
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const userApiUrl = 'api/feedbacks';
const adminApiUrl = 'api/admin/feedbacks';

// Async Actions

export const createFeedback = createAsyncThunk(
  'feedback/create_feedback',
  async (feedback: IFeedbackCreate) => {
    return axios.post<IFeedback>(userApiUrl, feedback);
  },
  { serializeError: serializeAxiosError },
);

export const getFeedbacks = createAsyncThunk(
  'feedback/fetch_feedbacks',
  async ({ page, size, sort, status, type }: IFeedbackFilterParams = {}) => {
    let requestUrl = `${adminApiUrl}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}`;
    if (status) {
      requestUrl += `status=${status}&`;
    }
    if (type) {
      requestUrl += `type=${type}&`;
    }
    requestUrl += `cacheBuster=${Date.now()}`;
    return axios.get<IFeedback[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getFeedback = createAsyncThunk(
  'feedback/fetch_feedback',
  async (id: string | number) => {
    const requestUrl = `${adminApiUrl}/${id}`;
    return axios.get<IFeedback>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const updateFeedbackStatus = createAsyncThunk(
  'feedback/update_status',
  async ({ id, status }: { id: number; status: FeedbackStatus }) => {
    const requestUrl = `${adminApiUrl}/${id}/status`;
    return axios.patch<IFeedback>(requestUrl, { status });
  },
  { serializeError: serializeAxiosError },
);

export const updateFeedbackNotes = createAsyncThunk(
  'feedback/update_notes',
  async ({ id, adminNotes }: { id: number; adminNotes: string | null }) => {
    const requestUrl = `${adminApiUrl}/${id}/notes`;
    return axios.patch<IFeedback>(requestUrl, { adminNotes });
  },
  { serializeError: serializeAxiosError },
);

export const deleteFeedback = createAsyncThunk(
  'feedback/delete_feedback',
  async (id: number) => {
    const requestUrl = `${adminApiUrl}/${id}`;
    return axios.delete(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

// Slice
export const FeedbackSlice = createSlice({
  name: 'feedback',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
    clearEntity(state) {
      state.entity = {};
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getFeedback.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteFeedback.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addCase(createFeedback.rejected, (state, action) => {
        state.updating = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message ?? null;
      })
      .addMatcher(isPending(getFeedbacks, getFeedback), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createFeedback, updateFeedbackStatus, updateFeedbackNotes, deleteFeedback), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      })
      .addMatcher(isFulfilled(getFeedbacks), (state, action) => {
        state.loading = false;
        state.entities = action.payload.data;
        state.totalItems = parseInt(action.payload.headers['x-total-count'], 10) || action.payload.data.length;
      })
      .addMatcher(isFulfilled(createFeedback, updateFeedbackStatus, updateFeedbackNotes), (state, action) => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      });
  },
});

export const { reset, clearEntity } = FeedbackSlice.actions;

// Reducer
export default FeedbackSlice.reducer;
