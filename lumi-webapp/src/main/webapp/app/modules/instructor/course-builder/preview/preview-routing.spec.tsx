import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import axios from 'axios';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import reducer, { type IBuilderAggregate } from '../course-builder.reducer';
import CourseBuilder from '../course-builder';

vi.mock('axios', () => ({ default: { get: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { error: vi.fn() } }));
vi.mock('../basic-info/course-basic-info', () => ({ default: () => <div>Biên tập thông tin</div> }));
vi.mock('../curriculum/curriculum-tab', () => ({
  default: ({ onNext }: { onNext: () => void }) => <button onClick={onNext}>Tiếp từ nội dung</button>,
}));
vi.mock('../pricing/course-pricing', () => ({
  default: ({ onNext }: { onNext: () => void }) => <button onClick={onNext}>Tiếp từ học phí</button>,
}));
vi.mock('../review/course-review-submit', () => ({
  default: ({ onBack }: { onBack: () => void }) => <button onClick={onBack}>Quay lại Preview</button>,
}));

const saved: IBuilderAggregate = {
  course: {
    id: 7,
    version: 5,
    title: 'Khóa học đã lưu',
    status: 'DRAFT',
    shortDescription: 'Mô tả đã cập nhật',
    price: 0,
    accessDurationValue: 1,
    accessDurationUnit: 'YEAR',
  },
  chapters: [],
  totalLessons: 0,
  totalMaterials: 0,
  totalVideos: 0,
};
const Location = () => <output aria-label="Địa chỉ">{useLocation().search}</output>;
const setup = (url: string) => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  render(
    <Provider store={store}>
      <MemoryRouter initialEntries={[url]}>
        <Location />
        <Routes>
          <Route path="/instructor/courses/:id/builder" element={<CourseBuilder />} />
          <Route path="/instructor/courses" element={<div>Danh sách khóa học</div>} />
        </Routes>
      </MemoryRouter>
    </Provider>,
  );
};
beforeEach(() => {
  vi.clearAllMocks();
  vi.mocked(axios.get).mockResolvedValue({ data: saved });
});
afterEach(cleanup);

it('loads a saved preview from its URL and keeps preview on a fresh mount', async () => {
  const url = '/instructor/courses/7/builder?step=preview&from=courses';
  setup(url);
  expect(await screen.findByText('Mô tả đã cập nhật')).toBeTruthy();
  expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/7/builder');
  expect(screen.getByLabelText('Địa chỉ').textContent).toBe('?step=preview&from=courses');
  cleanup();
  setup(url);
  expect(await screen.findByText('Mô tả đã cập nhật')).toBeTruthy();
  expect(screen.getByText('Truy cập 1 năm')).toBeTruthy();
});

it('updates the URL when navigating from Preview to edit, back to Preview, and review', async () => {
  setup('/instructor/courses/7/builder?step=preview');
  await screen.findByText('Mô tả đã cập nhật');
  fireEvent.click(screen.getByRole('button', { name: 'Chỉnh sửa → Học phí' }));
  await waitFor(() => expect(screen.getByLabelText('Địa chỉ').textContent).toBe('?step=pricing'));
  fireEvent.click(screen.getByRole('button', { name: 'Tiếp từ học phí' }));
  await waitFor(() => expect(screen.getByLabelText('Địa chỉ').textContent).toBe('?step=preview'));
  fireEvent.click(screen.getByRole('button', { name: 'Tiếp: Gửi duyệt →' }));
  await waitFor(() => expect(screen.getByLabelText('Địa chỉ').textContent).toBe('?step=review'));
  fireEvent.click(screen.getByRole('button', { name: 'Quay lại Preview' }));
  expect(await screen.findByText('Mô tả đã cập nhật')).toBeTruthy();
});

it('does not display another user course when the builder API rejects access', async () => {
  vi.mocked(axios.get).mockRejectedValueOnce({ response: { status: 404 }, message: 'Not found' });
  setup('/instructor/courses/7/builder?step=preview');
  expect(await screen.findByText('Danh sách khóa học')).toBeTruthy();
  expect(screen.queryByText('Mô tả đã cập nhật')).toBeNull();
});
