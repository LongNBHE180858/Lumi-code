import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { act, cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import axios from 'axios';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import reducer, { createDraft, getCourseBuilder, type IBuilderAggregate } from '../course-builder.reducer';
import CoursePricing, { type PricingNavigation } from './course-pricing';

vi.mock('axios', () => ({ default: { get: vi.fn(), put: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
const data = (): IBuilderAggregate => ({
  course: {
    id: 7,
    version: 3,
    title: 'Vật lý 9',
    status: 'DRAFT',
    price: 250000,
    accessDurationValue: 2,
    accessDurationUnit: 'YEAR',
    author: { id: 1, login: 'teacher' },
  },
  chapters: [{ chapter: { id: 10 }, lessons: [{ lesson: { id: 11, videoUrl: 'video', durationSeconds: 120 }, materials: [] }] }],
  totalLessons: 1,
  totalMaterials: 0,
  totalVideos: 1,
});
const setup = (initial = data()) => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(initial, 'initial', undefined));
  const onNext = vi.fn();
  const onBack = vi.fn();
  const navigationRef = React.createRef<PricingNavigation>();
  render(
    <Provider store={store}>
      <CoursePricing onNext={onNext} onBack={onBack} navigationRef={navigationRef} />
    </Provider>,
  );
  return { store, onNext, onBack, navigationRef };
};
beforeEach(() => vi.clearAllMocks());
afterEach(cleanup);

it('loads saved pricing and real summary, with legacy DAY fallback', () => {
  const initial = data();
  initial.course.accessDurationValue = null;
  initial.course.accessDurationUnit = null;
  initial.course.validDurationDays = 60;
  setup(initial);
  expect((screen.getByLabelText(/Học phí trọn khóa/) as HTMLInputElement).value).toBe('250000');
  expect((screen.getByLabelText('Thời gian truy cập *') as HTMLInputElement).value).toBe('60');
  expect((screen.getByLabelText('Đơn vị thời gian') as HTMLSelectElement).value).toBe('DAY');
  expect(screen.getByText('Vật lý 9')).toBeTruthy();
  expect(document.body.textContent).toContain('1 chương • 1 bài học • 1 video');
  expect(screen.getByText(/Truy cập 60 ngày/)).toBeTruthy();
});

it('saves free pricing with latest version and waits for success before preview', async () => {
  const { store, onNext } = setup();
  let resolve: (value: unknown) => void;
  vi.mocked(axios.put).mockImplementationOnce(
    () =>
      new Promise(done => {
        resolve = done;
      }),
  );
  fireEvent.click(screen.getByLabelText('Miễn phí'));
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  expect(onNext).not.toHaveBeenCalled();
  expect(axios.put).toHaveBeenCalledExactlyOnceWith('/api/instructor/courses/7/pricing', {
    version: 3,
    price: 0,
    accessDurationValue: 2,
    accessDurationUnit: 'YEAR',
  });
  const saved = data();
  saved.course.price = 0;
  saved.course.version = 4;
  resolve!({ data: saved });
  await waitFor(() => expect(onNext).toHaveBeenCalledOnce());
  expect(store.getState().courseBuilder.data?.course.version).toBe(4);
  expect(store.getState().courseBuilder.lastSavedTime).toBeTruthy();
});

it('rejects invalid input and preserves draft when save fails', async () => {
  const { onNext } = setup();
  const input = screen.getByLabelText(/Học phí trọn khóa/);
  for (const value of ['', '-1', '1.5', '9007199254740992']) {
    fireEvent.change(input, { target: { value } });
    fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  }
  expect(axios.put).not.toHaveBeenCalled();
  fireEvent.change(input, { target: { value: '300000' } });
  vi.mocked(axios.put).mockRejectedValueOnce({ response: { status: 409 }, message: 'Conflict' });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  await screen.findByText('Dữ liệu hoặc trạng thái khóa học đã thay đổi. Hãy tải lại trước khi lưu tiếp.');
  expect((input as HTMLInputElement).value).toBe('300000');
  expect(onNext).not.toHaveBeenCalled();
});

it('offers stay, discard or save before leaving a dirty pricing tab', async () => {
  const { onBack } = setup();
  fireEvent.change(screen.getByLabelText('Thời gian truy cập *'), { target: { value: '3' } });
  fireEvent.click(screen.getByRole('button', { name: /Nội dung bài học/ }));
  expect(onBack).not.toHaveBeenCalled();
  fireEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Ở lại' }));
  await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
  fireEvent.click(screen.getByRole('button', { name: /Nội dung bài học/ }));
  const saved = data();
  saved.course.version = 4;
  saved.course.accessDurationValue = 3;
  vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
  fireEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Lưu và tiếp tục' }));
  await waitFor(() => expect(onBack).toHaveBeenCalledOnce());
});

it('guards parent navigation and lets the user discard without writing', async () => {
  const { navigationRef } = setup();
  const leave = vi.fn();
  fireEvent.change(screen.getByLabelText('Đơn vị thời gian'), { target: { value: 'MONTH' } });
  act(() => navigationRef.current!.requestLeave(leave));
  expect(leave).not.toHaveBeenCalled();
  fireEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Bỏ thay đổi' }));
  expect(leave).toHaveBeenCalledOnce();
  expect(axios.put).not.toHaveBeenCalled();
});

it('submits the pricing form for save draft and keeps the current tab', async () => {
  const { onNext, store } = setup();
  fireEvent.change(screen.getByLabelText('Thời gian truy cập *'), { target: { value: '0' } });
  fireEvent.submit(document.getElementById('course-pricing-form')!);
  expect(axios.put).not.toHaveBeenCalled();
  fireEvent.change(screen.getByLabelText('Thời gian truy cập *'), { target: { value: '6' } });
  fireEvent.change(screen.getByLabelText('Đơn vị thời gian'), { target: { value: 'MONTH' } });
  const saved = data();
  saved.course.version = 4;
  saved.course.accessDurationValue = 6;
  saved.course.accessDurationUnit = 'MONTH';
  vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
  fireEvent.submit(document.getElementById('course-pricing-form')!);
  await waitFor(() => expect(store.getState().courseBuilder.data?.course.version).toBe(4));
  expect(onNext).not.toHaveBeenCalled();
  expect(screen.queryByText(/Có thay đổi chưa lưu/)).toBeNull();
});

it('saves paid pricing after switching from free and loads it into a fresh store', async () => {
  const initial = data();
  initial.course.price = 0;
  const { onNext } = setup(initial);
  fireEvent.click(screen.getByLabelText('Có học phí'));
  fireEvent.change(screen.getByLabelText(/Học phí trọn khóa/), { target: { value: '450000' } });
  const saved = data();
  saved.course.price = 450000;
  saved.course.version = 4;
  vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  await waitFor(() => expect(onNext).toHaveBeenCalledOnce());
  cleanup();
  const fresh = configureStore({ reducer: { courseBuilder: reducer } });
  vi.mocked(axios.get).mockResolvedValueOnce({ data: saved });
  await fresh.dispatch(getCourseBuilder(7));
  render(
    <Provider store={fresh}>
      <CoursePricing onBack={vi.fn()} onNext={vi.fn()} />
    </Provider>,
  );
  expect((screen.getByLabelText(/Học phí trọn khóa/) as HTMLInputElement).value).toBe('450000');
  expect((screen.getByLabelText('Thời gian truy cập *') as HTMLInputElement).value).toBe('2');
  expect((screen.getByLabelText('Đơn vị thời gian') as HTMLSelectElement).value).toBe('YEAR');
});

it.each([403, 404, 409])('keeps the draft and blocks further saves on %s until reload', async status => {
  const { onNext } = setup();
  fireEvent.change(screen.getByLabelText(/Học phí trọn khóa/), { target: { value: '300000' } });
  vi.mocked(axios.put).mockRejectedValueOnce({ response: { status }, message: 'Rejected' });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  await waitFor(() => expect((screen.getByRole('button', { name: /Tiếp: Xem trước/ }) as HTMLButtonElement).disabled).toBe(true));
  expect((screen.getByLabelText(/Học phí trọn khóa/) as HTMLInputElement).value).toBe('300000');
  expect(onNext).not.toHaveBeenCalled();
  const loaded = data();
  loaded.course.version = 10;
  vi.mocked(axios.get).mockResolvedValueOnce({ data: loaded });
  fireEvent.click(screen.getByRole('button', { name: 'Tải lại dữ liệu' }));
  expect(axios.get).not.toHaveBeenCalled();
  fireEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Bỏ thay đổi' }));
  await waitFor(() => expect((screen.getByLabelText(/Học phí trọn khóa/) as HTMLInputElement).value).toBe('250000'));
  const saved = data();
  saved.course.version = 11;
  vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
  fireEvent.submit(document.getElementById('course-pricing-form')!);
  await waitFor(() => expect(axios.put).toHaveBeenCalledTimes(2));
  expect(axios.put).toHaveBeenLastCalledWith('/api/instructor/courses/7/pricing', expect.objectContaining({ version: 10 }));
});

it('keeps edits on a transient save failure and permits retry', async () => {
  const { store, onNext } = setup();
  fireEvent.change(screen.getByLabelText(/Học phí trọn khóa/), { target: { value: '300000' } });
  vi.mocked(axios.put).mockRejectedValueOnce({ response: { status: 500 }, message: 'Server error' });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  await waitFor(() => expect(store.getState().courseBuilder.errorMessage).toBeTruthy());
  expect(onNext).not.toHaveBeenCalled();
  expect((screen.getByLabelText(/Học phí trọn khóa/) as HTMLInputElement).value).toBe('300000');
  const saved = data();
  saved.course.price = 300000;
  saved.course.version = 4;
  vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp: Xem trước/ }));
  await waitFor(() => expect(onNext).toHaveBeenCalledOnce());
});

it.each(['', '-1', '0', '1.5', '366'])('does not save invalid access duration %j', value => {
  setup();
  fireEvent.change(screen.getByLabelText('Thời gian truy cập *'), { target: { value } });
  fireEvent.submit(document.getElementById('course-pricing-form')!);
  expect(axios.put).not.toHaveBeenCalled();
});
