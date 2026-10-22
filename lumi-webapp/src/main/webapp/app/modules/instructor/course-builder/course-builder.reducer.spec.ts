import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import reducer, { createChapter, createDraft, getCourseBuilder, IBuilderAggregate, updateLesson } from './course-builder.reducer';

vi.mock('axios', () => ({ default: { get: vi.fn(), post: vi.fn(), put: vi.fn() } }));
beforeEach(() => vi.clearAllMocks());

const initialData = (): IBuilderAggregate => ({
  course: { id: 1, version: 1, title: 'Khóa học', status: 'DRAFT' },
  chapters: [{ chapter: { id: 10, title: 'Chương 1' }, lessons: [{ lesson: { id: 101, title: 'Bài 1' }, materials: [] }] }],
  totalLessons: 1,
  totalMaterials: 0,
  totalVideos: 0,
});
const setup = () => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(initialData(), 'initial', undefined));
  return store;
};

describe('Đồng bộ CourseBuilder', () => {
  it('chặn hai thao tác ghi đồng thời trước khi yêu cầu thứ hai tới API', async () => {
    const store = setup();
    let finish!: (value: any) => void;
    vi.mocked(axios.post).mockImplementation(
      () =>
        new Promise(resolve => {
          finish = resolve;
        }),
    );
    const first = store.dispatch(createChapter({ courseId: 1, version: 1, title: 'Chương mới' }));
    const second = await store.dispatch(
      updateLesson({ courseId: 1, lessonId: 101, version: 1, lesson: { title: 'Tên mới', description: '', isPreview: false } }),
    );
    expect(second.meta.requestStatus).toBe('rejected');
    expect(axios.put).not.toHaveBeenCalled();
    expect(store.getState().courseBuilder.saving).toBe(true);
    const result = initialData();
    result.course.version = 2;
    finish({ data: result });
    await first;
    expect(store.getState().courseBuilder.saving).toBe(false);
    expect(store.getState().courseBuilder.data!.course.version).toBe(2);
  });

  it.each([403, 409])('chuẩn hóa lỗi %s và chặn ghi tiếp cho đến khi tải lại dữ liệu', async status => {
    const store = setup();
    vi.mocked(axios.post).mockRejectedValueOnce({ response: { status }, message: 'Rejected' });
    await store.dispatch(createChapter({ courseId: 1, version: 1, title: 'Chương mới' }));
    expect(store.getState().courseBuilder.errorStatus).toBe(status);
    expect(store.getState().courseBuilder.errorMessage).toContain(status === 403 ? 'không có quyền' : 'đã thay đổi');
    await store.dispatch(createChapter({ courseId: 1, version: 1, title: 'Không gửi' }));
    expect(axios.post).toHaveBeenCalledTimes(1);
    const fresh = initialData();
    fresh.course.version = 3;
    vi.mocked(axios.get).mockResolvedValueOnce({ data: fresh });
    await store.dispatch(getCourseBuilder(1));
    expect(store.getState().courseBuilder.errorStatus).toBeNull();
    expect(store.getState().courseBuilder.data!.course.version).toBe(3);
  });

  it('store mới tải lại dữ liệu đã lưu qua API, không phụ thuộc state cũ', async () => {
    const first = setup();
    const persisted = initialData();
    persisted.course.version = 2;
    persisted.chapters.push({ chapter: { id: 20, title: 'Chương đã lưu' }, lessons: [] });
    vi.mocked(axios.post).mockResolvedValueOnce({ data: persisted });
    await first.dispatch(createChapter({ courseId: 1, version: 1, title: 'Chương đã lưu' }));
    const second = configureStore({ reducer: { courseBuilder: reducer } });
    vi.mocked(axios.get).mockResolvedValueOnce({ data: persisted });
    await second.dispatch(getCourseBuilder(1));
    expect(second.getState().courseBuilder.data).toEqual(persisted);
    expect(second.getState().courseBuilder.data).toEqual(first.getState().courseBuilder.data);
  });
});
