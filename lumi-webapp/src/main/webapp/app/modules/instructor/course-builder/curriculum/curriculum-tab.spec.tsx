import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { cleanup, createEvent, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import axios from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import reducer, { createDraft, IBuilderAggregate } from '../course-builder.reducer';
import CurriculumTab from './curriculum-tab';

vi.mock('axios', () => ({ default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

const initialData = (): IBuilderAggregate => ({
  course: { id: 1, version: 1, title: 'Khóa học', status: 'DRAFT' },
  chapters: [
    {
      chapter: { id: 10, title: 'Chương 1' },
      lessons: [
        { lesson: { id: 101, title: 'Bài 1', description: 'Mô tả 1', isPreview: false }, materials: [] },
        { lesson: { id: 102, title: 'Bài 2', description: 'Mô tả 2', isPreview: true }, materials: [] },
      ],
    },
  ],
  totalLessons: 2,
  totalMaterials: 0,
  totalVideos: 0,
});

const setup = (data = initialData()) => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(data, 'initial', undefined));
  render(
    <Provider store={store}>
      <CurriculumTab onBack={vi.fn()} onNext={vi.fn()} />
    </Provider>,
  );
  return store;
};

const titleInput = () => screen.getAllByRole('textbox')[0] as HTMLInputElement;

beforeEach(() => vi.clearAllMocks());
afterEach(cleanup);

describe('Quản lý bài học', () => {
  it('chuyển bài sang chương khác qua API nguyên tử và chọn bài tại chương đích', async () => {
    const data = initialData();
    data.chapters.push({ chapter: { id: 20, title: 'Chương 2' }, lessons: [] });
    setup(data);
    const result = structuredClone(data);
    result.course.version = 2;
    result.chapters[1].lessons.push(result.chapters[0].lessons.shift()!);
    vi.mocked(axios.put).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getAllByRole('button', { name: 'Chuyển chương' })[0]);
    fireEvent.change(await screen.findByLabelText('Chương đích'), { target: { value: '20' } });
    fireEvent.click(screen.getByRole('button', { name: 'Chuyển bài học', exact: true }));
    await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
    expect(axios.put).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/move', {
      version: 1,
      chapterId: 20,
      title: 'Bài 1',
    });
    expect(titleInput().value).toBe('Bài 1');
    expect(screen.getByText(/Chương 2 • Bài 1/)).toBeTruthy();
  });

  it('hiển thị và xác nhận tên mới khi chương đích có bài trùng tên', async () => {
    const data = initialData();
    data.chapters.push({
      chapter: { id: 20, title: 'Chương 2' },
      lessons: [
        { lesson: { id: 201, title: 'Bài 1' }, materials: [] },
        { lesson: { id: 202, title: 'Bài 1 (1)' }, materials: [] },
      ],
    });
    setup(data);
    fireEvent.click(screen.getAllByRole('button', { name: 'Chuyển chương' })[0]);
    fireEvent.change(await screen.findByLabelText('Chương đích'), { target: { value: '20' } });
    expect(screen.getByRole('status').textContent).toContain('Bài 1 (2)');
    expect(axios.put).not.toHaveBeenCalled();
    const result = structuredClone(data);
    const moved = result.chapters[0].lessons.shift()!;
    moved.lesson.title = 'Bài 1 (2)';
    result.chapters[1].lessons.push(moved);
    result.course.version = 2;
    vi.mocked(axios.put).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getByRole('button', { name: 'Chuyển và đổi tên' }));
    await waitFor(() => expect(titleInput().value).toBe('Bài 1 (2)'));
    expect(axios.put).toHaveBeenCalledExactlyOnceWith('/api/instructor/courses/1/lessons/101/move', {
      version: 1,
      chapterId: 20,
      title: 'Bài 1 (2)',
    });
  });

  it('409: tải lại version mới vẫn giữ bản nháp và có thể lưu lại', async () => {
    setup();
    vi.mocked(axios.put).mockRejectedValueOnce({ response: { status: 409 }, message: 'Conflict' });
    fireEvent.change(titleInput(), { target: { value: 'Bản nháp cần giữ' } });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu bài học' }));
    await screen.findByRole('alert');
    expect((screen.getByRole('button', { name: 'Lưu bài học' }) as HTMLButtonElement).disabled).toBe(true);
    const fresh = initialData();
    fresh.course.version = 5;
    fresh.chapters[0].lessons[0].lesson.description = 'Mô tả từ tab khác';
    vi.mocked(axios.get).mockResolvedValueOnce({ data: fresh });
    fireEvent.click(screen.getByRole('button', { name: 'Tải lại dữ liệu' }));
    await waitFor(() => expect(screen.queryByRole('alert')).toBeNull());
    expect(titleInput().value).toBe('Bản nháp cần giữ');
    const saved = structuredClone(fresh);
    saved.course.version = 6;
    saved.chapters[0].lessons[0].lesson.title = 'Bản nháp cần giữ';
    vi.mocked(axios.put).mockResolvedValueOnce({ data: saved });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu bài học' }));
    await waitFor(() => expect(axios.put).toHaveBeenCalledTimes(2));
    expect(axios.put).toHaveBeenLastCalledWith('/api/instructor/courses/1/lessons/101', expect.objectContaining({ version: 5 }));
  });

  it('kéo bài xuống rồi lên, lưu đúng thứ tự và giữ bản nháp của bài đang chọn', async () => {
    const store = setup();
    vi.mocked(axios.put).mockImplementation((_url, body: any) => {
      const result = structuredClone(store.getState().courseBuilder.data!);
      const lessons = result.chapters[0].lessons;
      result.chapters[0].lessons = body.chapters[0].lessonIds.map((id: number) => lessons.find(item => item.lesson.id === id));
      result.course.version++;
      return Promise.resolve({ data: result });
    });
    fireEvent.change(titleInput(), { target: { value: 'Bản nháp đang sửa' } });
    const dataTransfer = { setData: vi.fn(), effectAllowed: '', dropEffect: '' };
    fireEvent.dragStart(screen.getAllByTitle('Kéo để đổi thứ tự bài học')[0], { dataTransfer });
    fireEvent.drop(screen.getByText('2. Bài 2').closest('div.p-2')!, { dataTransfer, clientY: 10 });
    await screen.findByText('1. Bài 2');
    expect(axios.put).toHaveBeenLastCalledWith('/api/instructor/courses/1/curriculum/order', {
      version: 1,
      chapters: [{ id: 10, lessonIds: [102, 101] }],
    });
    expect(titleInput().value).toBe('Bản nháp đang sửa');

    fireEvent.dragStart(screen.getAllByTitle('Kéo để đổi thứ tự bài học')[1], { dataTransfer });
    const firstRow = screen.getByText('1. Bài 2').closest('div.p-2')!;
    vi.spyOn(firstRow, 'getBoundingClientRect').mockReturnValue({ top: 100, height: 20 } as DOMRect);
    const dropEvent = createEvent.drop(firstRow, { dataTransfer });
    Object.defineProperty(dropEvent, 'clientY', { value: 0 });
    fireEvent(firstRow, dropEvent);
    await screen.findByText('1. Bài 1');
    expect(axios.put).toHaveBeenLastCalledWith('/api/instructor/courses/1/curriculum/order', {
      version: 2,
      chapters: [{ id: 10, lessonIds: [101, 102] }],
    });
    expect(titleInput().value).toBe('Bản nháp đang sửa');
  });

  it('giữ thứ tự cũ và bản nháp khi lưu thứ tự thất bại', async () => {
    setup();
    vi.mocked(axios.put).mockRejectedValueOnce(new Error('Network error'));
    fireEvent.change(titleInput(), { target: { value: 'Bản nháp cần giữ' } });
    const dataTransfer = { setData: vi.fn(), effectAllowed: '', dropEffect: '' };
    fireEvent.dragStart(screen.getAllByTitle('Kéo để đổi thứ tự bài học')[0], { dataTransfer });
    fireEvent.drop(screen.getByText('2. Bài 2').closest('div.p-2')!, { dataTransfer, clientY: 10 });
    await screen.findByRole('alert');
    expect(screen.getByText('1. Bài 1')).toBeTruthy();
    expect(titleInput().value).toBe('Bản nháp cần giữ');
  });

  it('tự thêm hậu tố khi tạo nhiều bài học mới trong cùng chương', async () => {
    const store = setup();
    vi.mocked(axios.post).mockImplementation((_url, body: any) => {
      const result = structuredClone(store.getState().courseBuilder.data!);
      result.course.version++;
      result.chapters[0].lessons.push({ lesson: { id: 103 + result.totalLessons, ...body }, materials: [] });
      result.totalLessons++;
      return Promise.resolve({ data: result });
    });
    for (const title of ['Bài học mới', 'Bài học mới (1)', 'Bài học mới (2)']) {
      fireEvent.click(screen.getByRole('button', { name: /Thêm bài học/ }));
      await waitFor(() => expect(titleInput().value).toBe(title));
      expect(axios.post).toHaveBeenLastCalledWith('/api/instructor/courses/1/chapters/10/lessons', expect.objectContaining({ title }));
    }
  });

  it('đổi tên trùng thành tên có hậu tố, không coi chính bài đang sửa là trùng', async () => {
    const store = setup();
    vi.mocked(axios.put).mockImplementation((_url, body: any) => {
      const result = structuredClone(store.getState().courseBuilder.data!);
      result.course.version++;
      Object.assign(result.chapters[0].lessons[0].lesson, body);
      return Promise.resolve({ data: result });
    });
    fireEvent.change(titleInput(), { target: { value: 'Bài 2' } });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu bài học' }));
    await waitFor(() => expect(titleInput().value).toBe('Bài 2 (1)'));
    fireEvent.change(screen.getAllByRole('textbox')[1], { target: { value: 'Chỉ sửa mô tả' } });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu bài học' }));
    await waitFor(() => expect(axios.put).toHaveBeenCalledTimes(2));
    expect(axios.put).toHaveBeenLastCalledWith('/api/instructor/courses/1/lessons/101', expect.objectContaining({ title: 'Bài 2 (1)' }));
  });

  it('giữ bản nháp khi ở lại và nạp đúng bài sau khi bỏ thay đổi', async () => {
    setup();
    fireEvent.change(titleInput(), { target: { value: 'Tên chưa lưu' } });
    fireEvent.click(screen.getByText('2. Bài 2'));
    fireEvent.click(await screen.findByRole('button', { name: 'Ở lại' }));
    expect(titleInput().value).toBe('Tên chưa lưu');
    fireEvent.click(screen.getByText('2. Bài 2'));
    fireEvent.click(await screen.findByRole('button', { name: 'Bỏ thay đổi' }));
    await waitFor(() => expect(titleInput().value).toBe('Bài 2'));
    expect((screen.getByRole('checkbox') as HTMLInputElement).checked).toBe(true);
    expect(axios.put).not.toHaveBeenCalled();
  });

  it('lưu đủ các trường và dùng version mới khi lưu rồi thêm bài', async () => {
    const store = setup();
    vi.mocked(axios.put).mockImplementation((_url, body: any) => {
      const result = initialData();
      result.course.version = 2;
      Object.assign(result.chapters[0].lessons[0].lesson, body);
      return Promise.resolve({ data: result });
    });
    vi.mocked(axios.post).mockImplementation(() => {
      const result = structuredClone(store.getState().courseBuilder.data!);
      result.course.version = 3;
      result.chapters[0].lessons.push({ lesson: { id: 103, title: 'Bài học mới' }, materials: [] });
      result.totalLessons = 3;
      return Promise.resolve({ data: result });
    });
    fireEvent.change(titleInput(), { target: { value: 'Tên đã sửa' } });
    fireEvent.change(screen.getAllByRole('textbox')[1], { target: { value: 'Mô tả mới' } });
    fireEvent.click(screen.getByRole('checkbox'));
    fireEvent.click(screen.getByRole('button', { name: /Thêm bài học/ }));
    fireEvent.click(await screen.findByRole('button', { name: 'Lưu và tiếp tục' }));
    await waitFor(() => expect(titleInput().value).toBe('Bài học mới'));
    expect(axios.put).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101', {
      version: 1,
      title: 'Tên đã sửa',
      description: 'Mô tả mới',
      isPreview: true,
    });
    expect(axios.post).toHaveBeenCalledWith('/api/instructor/courses/1/chapters/10/lessons', {
      version: 2,
      title: 'Bài học mới',
      description: '',
      isPreview: false,
    });
  });

  it('giữ nội dung và không chuyển bài nếu API lưu thất bại', async () => {
    setup();
    vi.mocked(axios.put).mockRejectedValueOnce(new Error('Network error'));
    fireEvent.change(titleInput(), { target: { value: 'Giữ nội dung này' } });
    fireEvent.click(screen.getByText('2. Bài 2'));
    fireEvent.click(await screen.findByRole('button', { name: 'Lưu và tiếp tục' }));
    await waitFor(() => expect(screen.getByRole('button', { name: 'Lưu và tiếp tục' }).hasAttribute('disabled')).toBe(false));
    expect(titleInput().value).toBe('Giữ nội dung này');
    expect(screen.getByRole('dialog')).toBeTruthy();
  });

  it('chỉ xóa sau khi xác nhận và chọn bài còn lại', async () => {
    setup();
    const result = initialData();
    result.chapters[0].lessons.shift();
    result.totalLessons = 1;
    result.course.version = 2;
    vi.mocked(axios.delete).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getAllByRole('button', { name: 'Xóa', exact: true })[1]);
    const dialog = await screen.findByRole('dialog');
    expect(axios.delete).not.toHaveBeenCalled();
    fireEvent.click(within(dialog).getByRole('button', { name: 'Xóa bài học' }));
    await waitFor(() => expect(titleInput().value).toBe('Bài 2'));
    expect(axios.delete).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101', { params: { version: 1 } });
  });
});
