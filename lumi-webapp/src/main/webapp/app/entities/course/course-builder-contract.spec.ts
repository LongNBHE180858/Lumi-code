import { afterEach, describe, expect, it, vi } from 'vitest';
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducer, { updateEntity } from './course.reducer';
import { ICourse } from 'app/shared/model/course.model';

describe('course builder DTO contract', () => {
  afterEach(() => vi.restoreAllMocks());
  it('sends objectives and version, then retains the updated server version', async () => {
    const draft: ICourse = {
      id: 7,
      version: 2,
      learningObjectives: ['Read PDF', 'Read DOCX'],
      accessDurationValue: 12,
      accessDurationUnit: 'MONTH',
      validDurationDays: null,
      price: 0,
    };
    const saved = { ...draft, version: 3 };
    const put = vi.spyOn(axios, 'put').mockResolvedValue({ data: saved });
    vi.spyOn(axios, 'get').mockResolvedValue({ data: [], headers: {} });
    const store = configureStore({ reducer: { course: reducer } });
    await store.dispatch(updateEntity(draft));
    expect(put).toHaveBeenCalledWith('api/courses/7', draft);
    expect(store.getState().course.entity).toEqual(saved);
  });
});
