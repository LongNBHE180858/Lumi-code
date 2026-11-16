import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { ISubject, defaultValue } from 'app/shared/model/subject.model';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState: EntityState<ISubject> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export const getEntities = createAsyncThunk(
  'subject/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `api/subjects?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${Date.now()}`;
    return axios.get<ISubject[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const SubjectSlice = createEntitySlice({
  name: 'subject',
  initialState,
  extraReducers(builder) {
    builder
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;
        state.loading = false;
        state.entities = data;
        state.totalItems = parseInt(headers['x-total-count'], 10);
      })
      .addMatcher(isPending(getEntities), state => {
        state.errorMessage = null;
        state.loading = true;
      });
  },
});

export default SubjectSlice.reducer;
