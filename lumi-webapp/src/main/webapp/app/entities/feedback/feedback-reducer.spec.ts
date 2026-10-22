import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import { FeedbackStatus, FeedbackType, IFeedback } from 'app/shared/model/feedback.model';
import { EntityState } from 'app/shared/reducers/reducer.utils';
import reducer, {
  clearEntity,
  createFeedback,
  deleteFeedback,
  getFeedback,
  getFeedbacks,
  reset,
  updateFeedbackNotes,
  updateFeedbackStatus,
} from './feedback.reducer';

describe('Feedback reducer tests', () => {
  const initialState: EntityState<IFeedback> = {
    loading: false,
    errorMessage: null,
    entities: [],
    entity: {},
    updating: false,
    totalItems: 0,
    updateSuccess: false,
  };

  function testInitialState(state) {
    expect(state).toMatchObject({
      loading: false,
      errorMessage: null,
      updating: false,
      updateSuccess: false,
      totalItems: 0,
    });
    expect(state.entities).toEqual([]);
    expect(state.entity).toEqual({});
  }

  function testMultipleTypes(types, payload, testFunction, error?) {
    types.forEach(e => {
      testFunction(reducer(undefined, { type: e, payload, error }));
    });
  }

  describe('Common', () => {
    it('should return the initial state', () => {
      testInitialState(reducer(undefined, { type: '' }));
    });

    it('should reset the state', () => {
      expect(reducer({ ...initialState, loading: true }, reset())).toEqual({
        ...initialState,
      });
    });

    it('should clear entity', () => {
      expect(reducer({ ...initialState, entity: { id: 1 } }, clearEntity())).toEqual({
        ...initialState,
        entity: {},
      });
    });
  });

  describe('Requests', () => {
    it('should set state to loading for fetch actions', () => {
      testMultipleTypes([getFeedbacks.pending.type, getFeedback.pending.type], {}, state => {
        expect(state).toMatchObject({
          errorMessage: null,
          updateSuccess: false,
          loading: true,
        });
      });
    });

    it('should set state to updating for mutation actions', () => {
      testMultipleTypes(
        [createFeedback.pending.type, updateFeedbackStatus.pending.type, updateFeedbackNotes.pending.type, deleteFeedback.pending.type],
        {},
        state => {
          expect(state).toMatchObject({
            errorMessage: null,
            updateSuccess: false,
            updating: true,
          });
        },
      );
    });
  });

  describe('Successes', () => {
    it('should fetch all feedbacks', () => {
      const payload = {
        data: [
          { id: 1, title: 'Feedback 1', type: FeedbackType.BUG_REPORT, status: FeedbackStatus.NEW },
          { id: 2, title: 'Feedback 2', type: FeedbackType.GENERAL, status: FeedbackStatus.RESOLVED },
        ],
        headers: { 'x-total-count': '2' },
      };
      expect(
        reducer(undefined, {
          type: getFeedbacks.fulfilled.type,
          payload,
        }),
      ).toEqual({
        ...initialState,
        loading: false,
        entities: payload.data,
        totalItems: 2,
      });
    });

    it('should fetch a single feedback', () => {
      const payload = {
        data: { id: 1, title: 'Feedback 1', type: FeedbackType.BUG_REPORT, status: FeedbackStatus.NEW },
      };
      expect(
        reducer(undefined, {
          type: getFeedback.fulfilled.type,
          payload,
        }),
      ).toEqual({
        ...initialState,
        loading: false,
        entity: payload.data,
      });
    });

    it('should handle create feedback success', () => {
      const payload = {
        data: { id: 10, title: 'New feedback', type: FeedbackType.GENERAL, status: FeedbackStatus.NEW },
      };
      expect(
        reducer(undefined, {
          type: createFeedback.fulfilled.type,
          payload,
        }),
      ).toEqual({
        ...initialState,
        updating: false,
        updateSuccess: true,
        entity: payload.data,
      });
    });

    it('should clear the submitting state and record create feedback errors', () => {
      const state = reducer(
        { ...initialState, updating: true },
        createFeedback.rejected(new Error('Request failed'), 'request-id', {
          title: 'Title',
          content: 'Content long enough',
          type: FeedbackType.GENERAL,
          contactEmail: 'user@test.com',
        }),
      );

      expect(state).toMatchObject({
        updating: false,
        updateSuccess: false,
        errorMessage: 'Request failed',
      });
    });

    it('should handle update status success', () => {
      const payload = {
        data: { id: 1, status: FeedbackStatus.IN_REVIEW },
      };
      expect(
        reducer(undefined, {
          type: updateFeedbackStatus.fulfilled.type,
          payload,
        }),
      ).toEqual({
        ...initialState,
        updating: false,
        updateSuccess: true,
        entity: payload.data,
      });
    });

    it('should handle update notes success', () => {
      const payload = {
        data: { id: 1, adminNotes: 'Internal note' },
      };
      expect(
        reducer(undefined, {
          type: updateFeedbackNotes.fulfilled.type,
          payload,
        }),
      ).toEqual({
        ...initialState,
        updating: false,
        updateSuccess: true,
        entity: payload.data,
      });
    });

    it('should handle delete feedback success', () => {
      const toTest = reducer(
        { ...initialState, entity: { id: 1 } },
        {
          type: deleteFeedback.fulfilled.type,
        },
      );
      expect(toTest).toMatchObject({
        updating: false,
        updateSuccess: true,
        entity: {},
      });
    });
  });

  describe('Actions', () => {
    const resolvedObject = { data: { id: 1 }, headers: { 'x-total-count': '1' } };
    const getState = vi.fn();
    const dispatch = vi.fn();
    const extra = {};

    beforeEach(() => {
      axios.get = vi.fn().mockResolvedValue(resolvedObject);
      axios.post = vi.fn().mockResolvedValue(resolvedObject);
      axios.patch = vi.fn().mockResolvedValue(resolvedObject);
      axios.delete = vi.fn().mockResolvedValue(resolvedObject);
    });

    it('dispatches getFeedbacks actions', async () => {
      const result = await getFeedbacks({ page: 0, size: 10, sort: 'createdAt,desc' })(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getFeedbacks.pending.type,
        }),
      );
      expect(getFeedbacks.fulfilled.match(result)).toBe(true);
    });

    it('dispatches getFeedback actions', async () => {
      const result = await getFeedback(1)(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getFeedback.pending.type,
        }),
      );
      expect(getFeedback.fulfilled.match(result)).toBe(true);
    });

    it('dispatches createFeedback actions', async () => {
      const newFeedback = {
        title: 'Title',
        content: 'Content',
        type: FeedbackType.GENERAL,
        contactEmail: 'user@test.com',
      };
      const result = await createFeedback(newFeedback)(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: createFeedback.pending.type,
        }),
      );
      expect(createFeedback.fulfilled.match(result)).toBe(true);
    });

    it('dispatches updateFeedbackStatus actions', async () => {
      const result = await updateFeedbackStatus({ id: 1, status: FeedbackStatus.IN_REVIEW })(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: updateFeedbackStatus.pending.type,
        }),
      );
      expect(updateFeedbackStatus.fulfilled.match(result)).toBe(true);
    });

    it('dispatches updateFeedbackNotes actions', async () => {
      const result = await updateFeedbackNotes({ id: 1, adminNotes: 'Note' })(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: updateFeedbackNotes.pending.type,
        }),
      );
      expect(updateFeedbackNotes.fulfilled.match(result)).toBe(true);
    });

    it('dispatches deleteFeedback actions', async () => {
      const result = await deleteFeedback(1)(dispatch, getState, extra);
      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: deleteFeedback.pending.type,
        }),
      );
      expect(deleteFeedback.fulfilled.match(result)).toBe(true);
    });
  });
});
