import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { IGrade, defaultValue } from 'app/shared/model/grade.model';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState: EntityState<IGrade> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export const getEntities = createAsyncThunk(
  'grade/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `api/grades?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${Date.now()}`;
    return axios.get<IGrade[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const GradeSlice = createEntitySlice({
  name: 'grade',
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

export default GradeSlice.reducer;
