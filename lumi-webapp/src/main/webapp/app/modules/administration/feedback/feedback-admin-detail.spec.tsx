import { beforeAll, describe, expect, it, vi } from 'vitest';
import React from 'react';
import { TranslatorContext } from 'react-jhipster';
import { MemoryRouter, Route, Routes } from 'react-router';
import { configureStore } from '@reduxjs/toolkit';
import { act, render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import axios from 'axios';

import feedbackReducer from 'app/entities/feedback/feedback.reducer';
import { FeedbackStatus, FeedbackType, IFeedback } from 'app/shared/model/feedback.model';
import { FeedbackAdminDetail, getAvailableTransitions } from './feedback-admin-detail';

describe('FeedbackAdminDetail - State Transitions', () => {
  beforeAll(() => {
    TranslatorContext.setDefaultLocale('vi');
    TranslatorContext.setLocale('vi');
    TranslatorContext.registerTranslations('vi', {
      'lumiApp.feedback.detail.sectionBasic': 'Thông tin phản hồi',
      'lumiApp.feedback.detail.sectionContext': 'Ngữ cảnh học tập',
      'lumiApp.feedback.detail.sectionProcessing': 'Xử lý & Quản trị',
      'lumiApp.feedback.statuses.NEW': 'Mới tiếp nhận',
      'lumiApp.feedback.statuses.IN_REVIEW': 'Đang xử lý',
      'lumiApp.feedback.statuses.RESOLVED': 'Đã giải quyết',
      'lumiApp.feedback.statuses.DISMISSED': 'Đã đóng',
      'lumiApp.feedback.types.BUG_REPORT': 'Báo lỗi',
      'lumiApp.feedback.types.FEATURE_REQUEST': 'Đề xuất tính năng',
      'lumiApp.feedback.types.UI_UX': 'Góp ý giao diện',
      'lumiApp.feedback.types.GENERAL': 'Góp ý chung',
    });
  });

  describe('getAvailableTransitions unit tests', () => {
    it('should return only [IN_REVIEW] when current status is NEW', () => {
      const transitions = getAvailableTransitions(FeedbackStatus.NEW);
      expect(transitions).toEqual([FeedbackStatus.IN_REVIEW]);
      expect(transitions).not.toContain(FeedbackStatus.NEW);
      expect(transitions).not.toContain(FeedbackStatus.RESOLVED);
      expect(transitions).not.toContain(FeedbackStatus.DISMISSED);
    });

    it('should return [RESOLVED, DISMISSED] when current status is IN_REVIEW', () => {
      const transitions = getAvailableTransitions(FeedbackStatus.IN_REVIEW);
      expect(transitions).toEqual([FeedbackStatus.RESOLVED, FeedbackStatus.DISMISSED]);
      expect(transitions).not.toContain(FeedbackStatus.NEW);
      expect(transitions).not.toContain(FeedbackStatus.IN_REVIEW);
    });

    it('should return [IN_REVIEW, DISMISSED] when current status is RESOLVED', () => {
      const transitions = getAvailableTransitions(FeedbackStatus.RESOLVED);
      expect(transitions).toEqual([FeedbackStatus.IN_REVIEW, FeedbackStatus.DISMISSED]);
      expect(transitions).not.toContain(FeedbackStatus.NEW);
      expect(transitions).not.toContain(FeedbackStatus.RESOLVED);
    });

    it('should return [IN_REVIEW, RESOLVED] when current status is DISMISSED', () => {
      const transitions = getAvailableTransitions(FeedbackStatus.DISMISSED);
      expect(transitions).toEqual([FeedbackStatus.IN_REVIEW, FeedbackStatus.RESOLVED]);
      expect(transitions).not.toContain(FeedbackStatus.NEW);
      expect(transitions).not.toContain(FeedbackStatus.DISMISSED);
    });

    it('should return empty array when status is undefined', () => {
      expect(getAvailableTransitions(undefined)).toEqual([]);
    });
  });

  describe('UI dropdown rendering for each status', () => {
    const createMockFeedback = (status: FeedbackStatus): IFeedback => ({
      id: 101,
      title: 'Phản hồi kiểm thử',
      type: FeedbackType.BUG_REPORT,
      content: 'Nội dung phản hồi hợp lệ có ít nhất 10 ký tự',
      contactEmail: 'user@example.com',
      status,
      senderIdentity: 'user1',
      adminNotes: null,
      createdAt: '2026-10-15T10:00:00Z',
      updatedAt: '2026-10-15T10:00:00Z',
    });

    const renderWithFeedback = (feedback: IFeedback) => {
      axios.get = vi.fn().mockImplementation((url: string) => {
        if (url.includes('api/admin/feedbacks/101')) {
          return Promise.resolve({ data: feedback });
        }
        return Promise.resolve({ data: feedback });
      });

      const store = configureStore({
        reducer: {
          feedback: feedbackReducer,
        },
      });

      let utils: any;
      act(() => {
        utils = render(
          <Provider store={store}>
            <MemoryRouter initialEntries={['/admin/feedback/101']}>
              <Routes>
                <Route path="/admin/feedback/:id" element={<FeedbackAdminDetail />} />
              </Routes>
            </MemoryRouter>
          </Provider>,
        );
      });

      return utils;
    };

    it('should only show IN_REVIEW option and no DISMISSED/RESOLVED options when feedback is NEW', async () => {
      const { container } = renderWithFeedback(createMockFeedback(FeedbackStatus.NEW));

      await waitFor(() => {
        const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
        expect(select).not.toBeNull();
      });

      const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
      const options = Array.from(select.options).map(opt => opt.value);
      expect(options).toEqual(['', FeedbackStatus.IN_REVIEW]);
      expect(options).not.toContain(FeedbackStatus.DISMISSED);
      expect(options).not.toContain(FeedbackStatus.RESOLVED);
      expect(options).not.toContain(FeedbackStatus.NEW);
    });

    it('should show RESOLVED and DISMISSED options when feedback is IN_REVIEW', async () => {
      const { container } = renderWithFeedback(createMockFeedback(FeedbackStatus.IN_REVIEW));

      await waitFor(() => {
        const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
        expect(select).not.toBeNull();
      });

      const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
      const options = Array.from(select.options).map(opt => opt.value);
      expect(options).toEqual(['', FeedbackStatus.RESOLVED, FeedbackStatus.DISMISSED]);
      expect(options).not.toContain(FeedbackStatus.NEW);
      expect(options).not.toContain(FeedbackStatus.IN_REVIEW);
    });

    it('should show IN_REVIEW and DISMISSED options when feedback is RESOLVED', async () => {
      const { container } = renderWithFeedback(createMockFeedback(FeedbackStatus.RESOLVED));

      await waitFor(() => {
        const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
        expect(select).not.toBeNull();
      });

      const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
      const options = Array.from(select.options).map(opt => opt.value);
      expect(options).toEqual(['', FeedbackStatus.IN_REVIEW, FeedbackStatus.DISMISSED]);
      expect(options).not.toContain(FeedbackStatus.NEW);
      expect(options).not.toContain(FeedbackStatus.RESOLVED);
    });

    it('should show IN_REVIEW and RESOLVED options when feedback is DISMISSED', async () => {
      const { container } = renderWithFeedback(createMockFeedback(FeedbackStatus.DISMISSED));

      await waitFor(() => {
        const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
        expect(select).not.toBeNull();
      });

      const select = container.querySelector('[data-cy="nextStatusSelect"]') as HTMLSelectElement;
      const options = Array.from(select.options).map(opt => opt.value);
      expect(options).toEqual(['', FeedbackStatus.IN_REVIEW, FeedbackStatus.RESOLVED]);
      expect(options).not.toContain(FeedbackStatus.NEW);
      expect(options).not.toContain(FeedbackStatus.DISMISSED);
    });
  });
});
