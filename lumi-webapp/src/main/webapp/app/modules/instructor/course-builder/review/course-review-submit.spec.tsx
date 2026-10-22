import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import axios from 'axios';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import reducer, { createDraft, updatePricing, type IBuilderAggregate } from '../course-builder.reducer';
import CourseReviewSubmit from './course-review-submit';

vi.mock('axios', () => ({ default: { get: vi.fn(), post: vi.fn(), put: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
const ready = (): IBuilderAggregate => ({
  course: {
    id: 7,
    version: 3,
    status: 'DRAFT',
    title: 'Vật lý 9',
    authorDisplayName: 'Nguyễn Lan',
    subject: { id: 1, name: 'Vật lý' },
    grade: { id: 9, name: 'Lớp 9' },
    price: 0,
    accessDurationValue: 1,
    accessDurationUnit: 'YEAR',
  },
  chapters: [],
  totalLessons: 4,
  totalVideos: 3,
  totalMaterials: 2,
  review: {
    version: 3,
    canSubmit: true,
    checks: [
      { code: 'identity', label: 'Thông tin khóa học', required: true, complete: true, tab: 'basic-info' },
      { code: 'pricing', label: 'Học phí và thời hạn truy cập', required: true, complete: true, tab: 'pricing' },
      { code: 'intro', label: 'Video giới thiệu', required: false, complete: false, tab: 'basic-info' },
    ],
  },
});
const pending = () => {
  const result = ready();
  result.course.status = 'PENDING';
  result.course.version = 4;
  result.review = {
    ...result.review!,
    version: 4,
    canSubmit: false,
    request: {
      id: 50,
      status: 'PENDING',
      courseVersion: 4,
      submittedAt: '2026-10-21T08:00:00Z',
      rightsConfirmedAt: '2026-10-21T08:00:00Z',
      submissionNote: 'Nhờ kiểm tra tài liệu',
    },
  };
  return result;
};
const setup = async (data = ready()) => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(data, 'initial', undefined));
  vi.mocked(axios.get).mockResolvedValue({ data });
  const onNavigateTab = vi.fn();
  const onSubmitSuccess = vi.fn();
  render(
    <Provider store={store}>
      <CourseReviewSubmit onBack={vi.fn()} onNavigateTab={onNavigateTab} onSubmitSuccess={onSubmitSuccess} />
    </Provider>,
  );
  await waitFor(() => expect(store.getState().courseBuilder.loading).toBe(false));
  return { store, onNavigateTab, onSubmitSuccess };
};
const accept = () => fireEvent.click(screen.getByRole('checkbox'));
const openConfirmation = async () => {
  fireEvent.click(screen.getByRole('button', { name: 'Gửi duyệt', exact: true }));
  return within(await screen.findByRole('dialog'));
};
beforeEach(() => {
  vi.clearAllMocks();
  sessionStorage.clear();
});
afterEach(cleanup);

it('uses real data, starts with an empty note and unchecked rights, and allows missing recommendations', async () => {
  await setup();
  expect(screen.getByText('Vật lý 9')).toBeTruthy();
  expect(screen.getByText('Nguyễn Lan')).toBeTruthy();
  expect((screen.getByRole('textbox') as HTMLTextAreaElement).value).toBe('');
  expect((screen.getByRole('checkbox') as HTMLInputElement).checked).toBe(false);
  expect((screen.getByRole('button', { name: 'Gửi duyệt', exact: true }) as HTMLButtonElement).disabled).toBe(true);
  accept();
  expect((screen.getByRole('button', { name: 'Gửi duyệt', exact: true }) as HTMLButtonElement).disabled).toBe(false);
  expect(screen.queryByRole('button', { name: 'Lưu nháp' })).toBeNull();
});

it('only sends after explicit confirmation and cannot submit twice while waiting', async () => {
  const { onSubmitSuccess } = await setup();
  fireEvent.change(screen.getByRole('textbox'), { target: { value: 'Nhờ kiểm tra tài liệu' } });
  accept();
  const dialog = await openConfirmation();
  expect(axios.post).not.toHaveBeenCalled();
  let finish: (value: unknown) => void;
  vi.mocked(axios.post).mockImplementationOnce(
    () =>
      new Promise(resolve => {
        finish = resolve;
      }),
  );
  const submit = dialog.getByRole('button', { name: 'Xác nhận gửi duyệt' });
  fireEvent.click(submit);
  fireEvent.click(submit);
  expect(axios.post).toHaveBeenCalledExactlyOnceWith('/api/instructor/courses/7/review-requests', {
    version: 3,
    note: 'Nhờ kiểm tra tài liệu',
    confirmedRights: true,
  });
  finish!({ data: pending() });
  expect(await screen.findByText('Yêu cầu đang chờ duyệt')).toBeTruthy();
  await waitFor(() => expect(onSubmitSuccess).toHaveBeenCalledOnce());
  expect(screen.queryByRole('checkbox')).toBeNull();
  expect((screen.getByRole('button', { name: 'Đã gửi duyệt' }) as HTMLButtonElement).disabled).toBe(true);
  expect(sessionStorage.getItem('course-review-note-7')).toBeNull();
});

it('blocks missing required items using backend readiness and routes to the correct editor', async () => {
  const data = ready();
  data.review!.canSubmit = false;
  data.review!.checks[1].complete = false;
  const { onNavigateTab } = await setup(data);
  accept();
  expect((screen.getByRole('button', { name: 'Gửi duyệt', exact: true }) as HTMLButtonElement).disabled).toBe(true);
  fireEvent.click(screen.getByRole('button', { name: 'Bổ sung: Học phí và thời hạn truy cập' }));
  expect(onNavigateTab).toHaveBeenCalledWith('pricing');
  expect(axios.post).not.toHaveBeenCalled();
});

it.each([400, 409, 500])('retains note after %s and reloads readiness without assuming success', async status => {
  const { store } = await setup();
  fireEvent.change(screen.getByRole('textbox'), { target: { value: 'Giữ ghi chú này' } });
  accept();
  vi.mocked(axios.post).mockRejectedValueOnce({ response: { status }, message: 'Rejected' });
  const dialog = await openConfirmation();
  fireEvent.click(dialog.getByRole('button', { name: 'Xác nhận gửi duyệt' }));
  await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
  expect((screen.getByRole('textbox') as HTMLTextAreaElement).value).toBe('Giữ ghi chú này');
  expect(sessionStorage.getItem('course-review-note-7')).toBe('Giữ ghi chú này');
  expect(store.getState().courseBuilder.data?.course.status).toBe('DRAFT');
  const fresh = ready();
  fresh.course.version = 10;
  fresh.review!.version = 10;
  vi.mocked(axios.get).mockResolvedValueOnce({ data: fresh });
  fireEvent.click(screen.getByRole('button', { name: 'Tải lại danh sách kiểm tra' }));
  await waitFor(() => expect(store.getState().courseBuilder.data?.course.version).toBe(10));
  expect((screen.getByRole('checkbox') as HTMLInputElement).checked).toBe(false);
});

it('shows persisted request after a fresh mount and blocks writes from another editor', async () => {
  const { store } = await setup(pending());
  expect(screen.getByText('Yêu cầu đang chờ duyệt')).toBeTruthy();
  expect(screen.getByText(/Nhờ kiểm tra tài liệu/)).toBeTruthy();
  await store.dispatch(
    updatePricing({ courseId: 7, data: { version: 4, price: 100000, accessDurationValue: 1, accessDurationUnit: 'YEAR' } }),
  );
  expect(axios.put).not.toHaveBeenCalled();
  expect(axios.post).not.toHaveBeenCalled();
});

it('restores note within the browser session while keeping rights unchecked', async () => {
  sessionStorage.setItem('course-review-note-7', 'Ghi chú đang soạn');
  await setup();
  expect((screen.getByRole('textbox') as HTMLTextAreaElement).value).toBe('Ghi chú đang soạn');
  expect((screen.getByRole('checkbox') as HTMLInputElement).checked).toBe(false);
});
