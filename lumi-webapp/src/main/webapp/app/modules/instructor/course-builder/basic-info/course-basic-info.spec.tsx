import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import reducer, { createDraft, IBuilderAggregate } from '../course-builder.reducer';
import { CourseBasicInfo } from './course-basic-info';

vi.mock('axios', () => ({ default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

const initialData = (): IBuilderAggregate => ({
  course: {
    id: 1,
    version: 1,
    title: 'Toán học 9',
    bookSeries: 'Kết nối tri thức với cuộc sống',
    shortDescription: 'Mô tả ngắn',
    description: 'Mô tả chi tiết',
    targetAudience: 'Học sinh lớp 9',
    prerequisites: 'Hoàn thành Toán 8',
    learningObjectives: ['Hàm số bậc nhất', 'Hệ hai phương trình'],
    status: 'DRAFT',
  },
  chapters: [],
  totalLessons: 0,
  totalMaterials: 0,
  totalVideos: 0,
});

const setup = (data: IBuilderAggregate | null = initialData()) => {
  const store = configureStore({
    reducer: {
      courseBuilder: reducer,
      authentication: (state = { account: { firstName: 'Hùng', lastName: 'Văn', email: 'hung@lumi.vn', login: 'hungvan' } }) => state,
    },
  });
  if (data) {
    store.dispatch(createDraft.fulfilled(data, 'initial', undefined));
  }
  const onSaved = vi.fn();
  const onDirtyChange = vi.fn();
  render(
    <Provider store={store}>
      <CourseBasicInfo onSaved={onSaved} onDirtyChange={onDirtyChange} />
    </Provider>,
  );
  return { store, onSaved, onDirtyChange };
};

beforeEach(() => {
  vi.clearAllMocks();
  vi.mocked(axios.get).mockImplementation((url: string) => {
    if (url.includes('/api/subjects')) return Promise.resolve({ data: [{ id: 10, name: 'Toán học' }] });
    if (url.includes('/api/grades')) return Promise.resolve({ data: [{ id: 9, name: 'Lớp 9' }] });
    return Promise.resolve({ data: {} });
  });
});

afterEach(cleanup);

describe('CourseBasicInfo Component', () => {
  it('hiển thị đầy đủ thông tin cơ bản của khóa học từ Redux store', async () => {
    setup();

    expect((screen.getByPlaceholderText('Nhập tên khóa học...') as HTMLInputElement).value).toBe('Toán học 9');
    expect((screen.getByPlaceholderText('Tóm tắt ngắn gọn khóa học...') as HTMLTextAreaElement).value).toBe('Mô tả ngắn');
    expect((screen.getByPlaceholderText('Giới thiệu đầy đủ nội dung...') as HTMLTextAreaElement).value).toBe('Mô tả chi tiết');
    expect((screen.getByDisplayValue('Học sinh lớp 9') as HTMLTextAreaElement).value).toBe('Học sinh lớp 9');
    expect((screen.getByDisplayValue('Hoàn thành Toán 8') as HTMLInputElement).value).toBe('Hoàn thành Toán 8');
  });

  it('báo lỗi toast khi tiêu đề bị để trống và không dispatch lưu', async () => {
    setup();
    const titleInput = screen.getByPlaceholderText('Nhập tên khóa học...');
    fireEvent.change(titleInput, { target: { value: '   ' } });

    const form = document.getElementById('course-basic-info-form')!;
    fireEvent.submit(form);

    expect(toast.error).toHaveBeenCalledWith('Vui lòng nhập tên khóa học.');
    expect(axios.put).not.toHaveBeenCalled();
  });

  it('lưu thành công thông tin cơ bản và gọi callback onSaved', async () => {
    const { onSaved } = setup();

    const titleInput = screen.getByPlaceholderText('Nhập tên khóa học...');
    fireEvent.change(titleInput, { target: { value: 'Toán học 9 Nâng Cao' } });

    const updatedData = initialData();
    updatedData.course.title = 'Toán học 9 Nâng Cao';
    updatedData.course.version = 2;
    vi.mocked(axios.put).mockResolvedValueOnce({ data: updatedData });

    const form = document.getElementById('course-basic-info-form')!;
    fireEvent.submit(form);

    await waitFor(() => {
      expect(axios.put).toHaveBeenCalledWith(
        '/api/instructor/courses/1/basic-info',
        expect.objectContaining({
          title: 'Toán học 9 Nâng Cao',
          version: 1,
        }),
      );
      expect(toast.success).toHaveBeenCalledWith('Lưu thông tin khóa học thành công.');
      expect(onSaved).toHaveBeenCalledWith(1, false);
    });
  });

  it('xử lý lỗi API khi lưu mà không làm mất nội dung đang nhập của người dùng', async () => {
    setup();

    const titleInput = screen.getByPlaceholderText('Nhập tên khóa học...') as HTMLInputElement;
    fireEvent.change(titleInput, { target: { value: 'Bản nháp mới' } });

    vi.mocked(axios.put).mockRejectedValueOnce({ response: { status: 500, data: { message: 'Server Error' } } });

    const form = document.getElementById('course-basic-info-form')!;
    fireEvent.submit(form);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalled();
      expect(titleInput.value).toBe('Bản nháp mới');
    });
  });
});
