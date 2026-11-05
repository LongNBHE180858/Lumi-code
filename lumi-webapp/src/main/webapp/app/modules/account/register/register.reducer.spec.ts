import { beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { TranslatorContext } from 'react-jhipster';

import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';

import register, { handleRegister, reset } from './register.reducer';

describe('Creating account tests', () => {
  const initialState = {
    loading: false,
    registrationSuccess: false,
    registrationFailure: false,
    registeredEmail: '',
    errorMessage: null,
    successMessage: null,
  };

  beforeAll(() => {
    TranslatorContext.registerTranslations('vi', {});
  });

  it('should return the initial state', () => {
    expect(register(undefined, { type: '' })).toEqual({
      ...initialState,
    });
  });

  it('should detect a request', () => {
    expect(register(undefined, { type: handleRegister.pending.type })).toEqual({
      ...initialState,
      loading: true,
    });
  });

  it('should handle RESET', () => {
    expect(
      register(
        {
          loading: true,
          registrationSuccess: true,
          registrationFailure: true,
          registeredEmail: 'test@lumi.edu.vn',
          errorMessage: '',
          successMessage: '',
        },
        reset(),
      ),
    ).toEqual({
      ...initialState,
    });
  });

  it('should handle CREATE_ACCOUNT success', () => {
    expect(
      register(undefined, {
        type: handleRegister.fulfilled.type,
        payload: { data: 'fake payload', email: 'test@lumi.edu.vn' },
      }),
    ).toEqual({
      ...initialState,
      registrationSuccess: true,
      registeredEmail: 'test@lumi.edu.vn',
      successMessage: 'Đăng ký thành công! Vui lòng xác thực tài khoản.',
    });
  });

  it('should handle CREATE_ACCOUNT failure', () => {
    const error = { message: 'fake error' };
    expect(
      register(undefined, {
        type: handleRegister.rejected.type,
        error,
      }),
    ).toEqual({
      ...initialState,
      registrationFailure: true,
      errorMessage: error.message,
    });
  });

  describe('Actions', () => {
    let store;

    const resolvedObject = { value: 'whatever' };
    const getState = vi.fn();
    const dispatch = vi.fn();
    const extra = {};
    beforeEach(() => {
      store = configureStore({
        reducer: (state = [], action) => [...state, action],
      });
      axios.post = vi.fn().mockResolvedValue({ data: resolvedObject });
    });

    it('dispatches CREATE_ACCOUNT_PENDING and CREATE_ACCOUNT_FULFILLED actions', async () => {
      const arg = { username: 'testuser', email: 'test@lumi.edu.vn', password: 'Password123!' };

      const result = await handleRegister(arg)(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: handleRegister.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(handleRegister.fulfilled.match(result)).toBe(true);
      expect(result.payload).toEqual({ data: resolvedObject, email: 'test@lumi.edu.vn' });
    });
    it('dispatches RESET actions', async () => {
      await store.dispatch(reset());
      expect(store.getState()).toEqual([expect.any(Object), expect.objectContaining(reset())]);
    });
  });
});
