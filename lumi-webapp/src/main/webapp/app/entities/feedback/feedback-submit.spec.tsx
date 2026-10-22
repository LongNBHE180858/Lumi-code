import { beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import React from 'react';
import { TranslatorContext } from 'react-jhipster';
import { MemoryRouter } from 'react-router';
import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import axios from 'axios';

import feedbackReducer from './feedback.reducer';
import FeedbackSubmit from './feedback-submit';

describe('FeedbackSubmit component', () => {
  beforeAll(() => {
    TranslatorContext.setDefaultLocale('vi');
    TranslatorContext.setLocale('vi');
    TranslatorContext.registerTranslations('vi', {
      'lumiApp.feedback.submit.title': 'Gửi phản hồi',
      'lumiApp.feedback.submit.subtitle': 'Chia sẻ góp ý hoặc vấn đề bạn gặp khi học cùng LUMI.',
      'lumiApp.feedback.submit.breadcrumbLabel': 'Đường dẫn',
      'lumiApp.feedback.submit.home': 'Trang chủ',
      'lumiApp.feedback.submit.support': 'Hỗ trợ',
      'lumiApp.feedback.submit.back': 'Quay lại',
      'lumiApp.feedback.submit.requiredFields': 'Các trường có dấu * là bắt buộc',
      'lumiApp.feedback.submit.detailsHeading': 'Chi tiết phản hồi',
      'lumiApp.feedback.submit.contactHeading': 'Email liên hệ',
      'lumiApp.feedback.submit.optional': 'tùy chọn',
      'lumiApp.feedback.submit.courseSelectPlaceholder': '-- Chọn khóa học liên quan (tùy chọn) --',
      'lumiApp.feedback.submit.lessonSelectPlaceholder': '-- Chọn bài học (tùy chọn) --',
      'lumiApp.feedback.submit.lessonSelectPrompt': 'Vui lòng chọn khóa học trước',
      'lumiApp.feedback.submit.loadingCourses': 'Đang tải khóa học...',
      'lumiApp.feedback.submit.loadingLessons': 'Đang tải bài học...',
      'lumiApp.feedback.submit.coursesLoadError': 'Không thể tải danh sách khóa học.',
      'lumiApp.feedback.submit.lessonsLoadError': 'Không thể tải danh sách bài học.',
      'lumiApp.feedback.submit.courseLessonHint': 'Có thể bỏ qua nếu phản hồi không liên quan đến một khóa học hoặc bài học.',
      'lumiApp.feedback.submit.titlePlaceholder': 'Tóm tắt điều bạn muốn chia sẻ',
      'lumiApp.feedback.submit.contentPlaceholder': 'Mô tả góp ý hoặc vấn đề bạn gặp.',
      'lumiApp.feedback.submit.contentHint': 'Viết rõ và cụ thể để LUMI hiểu phản hồi của bạn.',
      'lumiApp.feedback.submit.emailPlaceholder': 'example@domain.com',
      'lumiApp.feedback.submit.emailHint': 'Bạn có thể đổi email để nhận trao đổi về phản hồi này.',
      'lumiApp.feedback.submit.confirmEmailCheckbox': 'Tôi xác nhận email liên hệ ở trên là chính xác',
      'lumiApp.feedback.submit.securityNote': 'Không chia sẻ thông tin nhạy cảm trong phản hồi.',
      'lumiApp.feedback.submit.requestFailed': 'Không thể gửi phản hồi. Vui lòng thử lại; nội dung của bạn vẫn được giữ nguyên.',
      'lumiApp.feedback.submit.reviewBeforeSubmit': 'Kiểm tra nội dung và email liên hệ trước khi gửi.',
      'lumiApp.feedback.submit.cancel': 'Hủy',
      'lumiApp.feedback.submit.submitting': 'Đang gửi...',
      'lumiApp.feedback.submit.submitButton': 'Gửi phản hồi',
      'lumiApp.feedback.validation.titleRequired': 'Tiêu đề là bắt buộc',
      'lumiApp.feedback.validation.titleMaxLength': 'Tiêu đề không được vượt quá 255 ký tự',
      'lumiApp.feedback.validation.contentRequired': 'Nội dung phản hồi là bắt buộc',
      'lumiApp.feedback.validation.contentMinLength': 'Nội dung phản hồi phải có ít nhất 10 ký tự',
      'lumiApp.feedback.validation.contentMaxLength': 'Nội dung phản hồi không được vượt quá 5.000 ký tự',
      'lumiApp.feedback.validation.contactEmailRequired': 'Email liên hệ là bắt buộc',
      'lumiApp.feedback.validation.contactEmailInvalid': 'Email liên hệ không hợp lệ hoặc vượt quá 254 ký tự',
      'lumiApp.feedback.validation.confirmEmailRequired': 'Bạn cần xác nhận email liên hệ trước khi gửi',
      'lumiApp.feedback.title': 'Tiêu đề',
      'lumiApp.feedback.content': 'Nội dung phản hồi',
      'lumiApp.feedback.contactEmail': 'Email liên hệ',
      'lumiApp.feedback.course': 'Khóa học',
      'lumiApp.feedback.lesson': 'Bài học',
      'lumiApp.feedback.type': 'Loại phản hồi',
      'lumiApp.feedback.types.GENERAL': 'Góp ý chung',
      'lumiApp.feedback.types.FEATURE_REQUEST': 'Đề xuất tính năng',
      'lumiApp.feedback.types.BUG_REPORT': 'Báo lỗi',
      'lumiApp.feedback.types.UI_UX': 'Góp ý giao diện',
    });
  });

  const renderComponent = (account = { email: 'student@example.com' }) => {
    const store = configureStore({
      reducer: {
        authentication: () => ({ account }),
        feedback: feedbackReducer,
      },
    });

    return render(
      <Provider store={store}>
        <MemoryRouter>
          <FeedbackSubmit />
        </MemoryRouter>
      </Provider>,
    );
  };

  beforeEach(() => {
    axios.get = vi.fn().mockImplementation((url: string) => {
      if (url.includes('api/courses?')) {
        return Promise.resolve({
          data: [
            { id: 1, title: 'Java Basics' },
            { id: 2, title: 'React Mastery' },
          ],
        });
      }
      if (url.includes('/lessons')) {
        return Promise.resolve({
          data: [
            { id: 101, title: 'Lesson 1: Introduction' },
            { id: 102, title: 'Lesson 2: Variables' },
          ],
        });
      }
      return Promise.resolve({ data: [] });
    });
    axios.post = vi.fn().mockResolvedValue({ data: { id: 1 } });
  });

  it('should render the feedback submit form with pre-populated email', () => {
    const { container } = renderComponent({ email: 'john@lumi.edu' });

    expect(container.textContent).toContain('Gửi phản hồi');
    const emailInput = container.querySelector('[data-cy="feedback-contact-email"]') as HTMLInputElement;
    expect(emailInput.value).toBe('john@lumi.edu');
    expect(container.querySelector('[data-cy="feedback-type-GENERAL"] input')).toHaveProperty('checked', true);
  });

  it('should have lesson select disabled initially when no course is selected', () => {
    const { container } = renderComponent();

    const lessonSelect = container.querySelector('[data-cy="feedback-lesson"]') as HTMLSelectElement;
    expect(lessonSelect.disabled).toBe(true);
  });

  it('should enable lesson select when course is selected and populate lessons', async () => {
    const { container } = renderComponent();

    // Wait for courses to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('api/courses?'));
    });

    const courseSelect = container.querySelector('[data-cy="feedback-course"]') as HTMLSelectElement;
    fireEvent.change(courseSelect, { target: { value: '1' } });

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith('api/courses/1/lessons');
    });

    const lessonSelect = container.querySelector('[data-cy="feedback-lesson"]') as HTMLSelectElement;
    expect(lessonSelect.disabled).toBe(false);
  });

  it('should show validation errors when submitted empty', async () => {
    const { container } = renderComponent({ email: '' });

    const submitBtn = container.querySelector('[data-cy="feedback-submit-btn"]') as HTMLButtonElement;
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(container.textContent).toContain('Tiêu đề là bắt buộc');
      expect(container.textContent).toContain('Nội dung phản hồi là bắt buộc');
      expect(container.textContent).toContain('Email liên hệ là bắt buộc');
      expect(container.textContent).toContain('Bạn cần xác nhận email liên hệ trước khi gửi');
    });
  });

  it('should reject whitespace-only content with validation error', async () => {
    const { container } = renderComponent({ email: 'test@example.com' });

    const titleInput = container.querySelector('[data-cy="feedback-title"]') as HTMLInputElement;
    const contentInput = container.querySelector('[data-cy="feedback-content"]') as HTMLTextAreaElement;
    const confirmCheckbox = container.querySelector('[data-cy="feedback-confirm-email"]') as HTMLInputElement;

    fireEvent.change(titleInput, { target: { value: 'Valid Title' } });
    fireEvent.change(contentInput, { target: { value: '          ' } }); // 10 spaces
    fireEvent.click(confirmCheckbox);

    const submitBtn = container.querySelector('[data-cy="feedback-submit-btn"]') as HTMLButtonElement;
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(container.textContent).toContain('Nội dung phản hồi là bắt buộc');
    });
    expect(axios.post).not.toHaveBeenCalled();
  });

  it('should reject content under 10 characters with min-length error', async () => {
    const { container } = renderComponent({ email: 'test@example.com' });

    const titleInput = container.querySelector('[data-cy="feedback-title"]') as HTMLInputElement;
    const contentInput = container.querySelector('[data-cy="feedback-content"]') as HTMLTextAreaElement;
    const confirmCheckbox = container.querySelector('[data-cy="feedback-confirm-email"]') as HTMLInputElement;

    fireEvent.change(titleInput, { target: { value: 'Valid Title' } });
    fireEvent.change(contentInput, { target: { value: '123456789' } }); // 9 chars
    fireEvent.click(confirmCheckbox);

    const submitBtn = container.querySelector('[data-cy="feedback-submit-btn"]') as HTMLButtonElement;
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(container.textContent).toContain('Nội dung phản hồi phải có ít nhất 10 ký tự');
    });
    expect(axios.post).not.toHaveBeenCalled();
  });

  it('should accept valid content with exactly 10 characters', async () => {
    const { container } = renderComponent({ email: 'test@example.com' });

    const titleInput = container.querySelector('[data-cy="feedback-title"]') as HTMLInputElement;
    const contentInput = container.querySelector('[data-cy="feedback-content"]') as HTMLTextAreaElement;
    const confirmCheckbox = container.querySelector('[data-cy="feedback-confirm-email"]') as HTMLInputElement;

    fireEvent.change(titleInput, { target: { value: 'Valid Title' } });
    fireEvent.change(contentInput, { target: { value: '1234567890' } }); // exactly 10 chars
    fireEvent.click(confirmCheckbox);

    const submitBtn = container.querySelector('[data-cy="feedback-submit-btn"]') as HTMLButtonElement;
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        'api/feedbacks',
        expect.objectContaining({
          title: 'Valid Title',
          content: '1234567890',
          contactEmail: 'test@example.com',
        }),
      );
    });
  });

  it('should accept valid content up to 5000 characters', async () => {
    const { container } = renderComponent({ email: 'test@example.com' });

    const titleInput = container.querySelector('[data-cy="feedback-title"]') as HTMLInputElement;
    const contentInput = container.querySelector('[data-cy="feedback-content"]') as HTMLTextAreaElement;
    const confirmCheckbox = container.querySelector('[data-cy="feedback-confirm-email"]') as HTMLInputElement;

    const longContent = 'A'.repeat(5000);
    fireEvent.change(titleInput, { target: { value: 'Valid Title' } });
    fireEvent.change(contentInput, { target: { value: longContent } });
    fireEvent.click(confirmCheckbox);

    const submitBtn = container.querySelector('[data-cy="feedback-submit-btn"]') as HTMLButtonElement;
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        'api/feedbacks',
        expect.objectContaining({
          title: 'Valid Title',
          content: longContent,
          contactEmail: 'test@example.com',
        }),
      );
    });
  });

  it('should preserve the form and show an error when feedback submission fails', async () => {
    axios.post = vi.fn().mockRejectedValue(new Error('Server unavailable'));
    const { container, findByRole } = renderComponent({ email: 'test@example.com' });

    fireEvent.change(container.querySelector('[data-cy="feedback-title"]'), { target: { value: 'Valid Title' } });
    fireEvent.change(container.querySelector('[data-cy="feedback-content"]'), { target: { value: 'Valid content text' } });
    fireEvent.click(container.querySelector('[data-cy="feedback-confirm-email"]'));
    fireEvent.click(container.querySelector('[data-cy="feedback-submit-btn"]'));

    const alert = await findByRole('alert');
    expect(alert.textContent).toContain('Không thể gửi phản hồi');
    expect(container.querySelector('[data-cy="feedback-title"]')).toHaveProperty('value', 'Valid Title');
    expect(container.querySelector('[data-cy="feedback-content"]')).toHaveProperty('value', 'Valid content text');
    expect(container.querySelector('[data-cy="feedback-submit-btn"]')).toHaveProperty('disabled', false);
  });
});
