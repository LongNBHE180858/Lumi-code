import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';
import reducer, { createDraft, IBuilderAggregate } from '../course-builder.reducer';
import LessonMedia from './lesson-media';
import * as durationReader from './video-duration';

vi.mock('axios', () => ({ default: { request: vi.fn(), get: vi.fn(), put: vi.fn(), delete: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

const setup = (extraMaterial = false) => {
  const data: IBuilderAggregate = {
    course: { id: 1, version: 1, title: 'Khóa học', status: 'DRAFT' },
    chapters: [
      {
        chapter: { id: 10 },
        lessons: [
          {
            lesson: { id: 101, title: 'Bài 1', videoUrl: 'stored/old.mp4', videoFileName: 'old.mp4' },
            materials: [{ id: 20, fileName: 'old.pdf', fileType: 'PDF', fileUrl: 'stored/old.pdf' }],
          },
        ],
      },
    ],
    totalLessons: 1,
    totalVideos: 1,
    totalMaterials: 1,
  };
  if (extraMaterial) {
    data.chapters[0].lessons[0].materials.push({ id: 21, fileName: 'second.pdf' });
    data.totalMaterials++;
  }
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(data, 'initial', undefined));
  const Host = () => {
    // Phản ánh saving của Redux như LessonEditor thực tế.
    const saving = useMediaSaving();
    return <LessonMedia lessonId={101} disabled={saving} />;
  };
  render(
    <Provider store={store}>
      <Host />
    </Provider>,
  );
  return store;
};

// Dùng selector thật để kiểm tra khóa thao tác trong lúc upload.
import { useAppSelector } from 'app/config/store';
const useMediaSaving = () => useAppSelector(state => state.courseBuilder.saving);

beforeEach(() => {
  vi.clearAllMocks();
  vi.spyOn(durationReader, 'readVideoDuration').mockResolvedValue(125);
});
afterEach(() => {
  cleanup();
  vi.restoreAllMocks();
});

describe('Media bài học', () => {
  it('đọc và lưu bổ sung thời lượng khi phát video cũ', async () => {
    const store = setup();
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.course.version = 2;
    result.chapters[0].lessons[0].lesson.durationSeconds = 125;
    vi.mocked(axios.put).mockResolvedValueOnce({ data: result });
    vi.mocked(axios.get).mockResolvedValueOnce({ data: { url: 'https://storage.example/video' } });
    fireEvent.click(screen.getByRole('button', { name: 'Xem / tải lại video' }));
    const video = await screen.findByLabelText('Video bài học');
    Object.defineProperty(video, 'duration', { value: 125.1 });
    fireEvent.loadedMetadata(video);
    await waitFor(() => expect(store.getState().courseBuilder.data!.course.version).toBe(2));
    expect(axios.put).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/video/duration', { version: 1, durationSeconds: 125 });
    expect(await screen.findByText(/2 phút 5 giây/)).toBeTruthy();
  });

  it('sắp xếp tài liệu qua materials/order và cập nhật thứ tự từ Builder', async () => {
    const store = setup(true);
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.course.version = 2;
    result.chapters[0].lessons[0].materials.reverse();
    vi.mocked(axios.put).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getByRole('button', { name: 'Đưa old.pdf xuống' }));
    await waitFor(() => expect(store.getState().courseBuilder.data!.course.version).toBe(2));
    expect(axios.put).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/materials/order', { version: 1, materialIds: [21, 20] });
    expect(store.getState().courseBuilder.data!.chapters[0].lessons[0].materials.map(item => item.id)).toEqual([21, 20]);
  });

  it('báo định dạng sai ngay dưới phần chọn tệp và không gửi API', async () => {
    setup();
    fireEvent.change(screen.getByLabelText('Tải tài liệu PDF / DOCX'), { target: { files: [new File(['text'], 'wrong.txt')] } });
    expect(await screen.findByText('Vui lòng chọn tài liệu PDF hoặc DOCX.')).toBeTruthy();
    expect(axios.request).not.toHaveBeenCalled();
  });

  it('gỡ tài liệu qua đúng API sau khi xác nhận', async () => {
    const store = setup();
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.chapters[0].lessons[0].materials = [];
    result.totalMaterials = 0;
    vi.mocked(axios.delete).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getByRole('button', { name: 'Gỡ', exact: true }));
    await screen.findByRole('dialog');
    fireEvent.click(screen.getByRole('button', { name: 'Xác nhận' }));
    await screen.findByText('Chưa có tài liệu đính kèm.');
    expect(axios.delete).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/materials/20', { params: { version: 1 } });
  });

  it('cho thử lại khi lấy URL phát lỗi', async () => {
    setup();
    vi.mocked(axios.get)
      .mockRejectedValueOnce(new Error('503'))
      .mockResolvedValueOnce({ data: { url: 'https://storage.example/retry' } });
    fireEvent.click(screen.getByRole('button', { name: 'Xem / tải lại video' }));
    await screen.findByRole('alert');
    fireEvent.click(screen.getByRole('button', { name: 'Xem / tải lại video' }));
    expect((await screen.findByLabelText('Video bài học')).getAttribute('src')).toBe('https://storage.example/retry');
  });

  it('upload video với tiến độ thật, chỉ cập nhật video sau khi BE thành công', async () => {
    const store = setup();
    let finish!: (value: any) => void;
    vi.mocked(axios.request).mockImplementation(config => {
      config.onUploadProgress?.({ loaded: 25, total: 100 } as any);
      return new Promise(resolve => {
        finish = resolve;
      });
    });
    const file = new File(['video'], 'new.mp4', { type: 'video/mp4' });
    fireEvent.change(screen.getByLabelText('Thay thế video MP4'), { target: { files: [file] } });
    expect(await screen.findByText('25%')).toBeTruthy();
    expect(store.getState().courseBuilder.data!.chapters[0].lessons[0].lesson.videoFileName).toBe('old.mp4');
    const config = vi.mocked(axios.request).mock.calls[0][0];
    expect(config.method).toBe('POST');
    expect(config.url).toBe('/api/instructor/courses/1/lessons/101/video');
    expect(config.params).toEqual({ version: 1, durationSeconds: 125 });
    expect((config.data as FormData).get('file')).toBe(file);
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.course.version = 2;
    result.chapters[0].lessons[0].lesson.videoFileName = 'new.mp4';
    result.chapters[0].lessons[0].lesson.videoUrl = 'stored/new.mp4';
    finish({ data: result });
    await screen.findByText(/new.mp4 •/);
    expect(store.getState().courseBuilder.saving).toBe(false);
  });

  it('thay thế tài liệu lỗi giữ file cũ và thử lại bằng đúng file đã chọn', async () => {
    const store = setup();
    vi.mocked(axios.request).mockRejectedValueOnce(new Error('Network error'));
    const file = new File(['pdf'], 'new.pdf');
    fireEvent.change(screen.getByLabelText('old.pdf'), { target: { files: [file] } });
    await screen.findByRole('alert');
    expect(store.getState().courseBuilder.data!.chapters[0].lessons[0].materials[0].fileName).toBe('old.pdf');
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.chapters[0].lessons[0].materials[0].fileName = 'new.pdf';
    result.course.version = 2;
    vi.mocked(axios.request).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getByRole('button', { name: 'Thử lại' }));
    await screen.findAllByText('new.pdf');
    const config = vi.mocked(axios.request).mock.calls[1][0];
    expect(config.method).toBe('PUT');
    expect(config.url).toBe('/api/instructor/courses/1/lessons/101/materials/20/file');
    expect((config.data as FormData).get('file')).toBe(file);
  });

  it('thêm tài liệu qua POST và đổi tên qua API bằng version mới', async () => {
    const store = setup();
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.course.version = 2;
    result.chapters[0].lessons[0].materials.push({ id: 21, fileName: 'new.docx' });
    vi.mocked(axios.request).mockResolvedValueOnce({ data: result });
    fireEvent.change(screen.getByLabelText('Tải tài liệu PDF / DOCX'), { target: { files: [new File(['docx'], 'new.docx')] } });
    await screen.findAllByText('new.docx');
    expect(axios.request).toHaveBeenCalledWith(
      expect.objectContaining({ method: 'POST', url: '/api/instructor/courses/1/lessons/101/materials' }),
    );
    fireEvent.click(screen.getAllByRole('button', { name: 'Đổi tên' })[0]);
    fireEvent.change(screen.getByLabelText('Tên tài liệu'), { target: { value: 'Bài tập' } });
    const renamed = structuredClone(result);
    renamed.course.version = 3;
    renamed.chapters[0].lessons[0].materials[0].displayName = 'Bài tập';
    vi.mocked(axios.put).mockResolvedValueOnce({ data: renamed });
    fireEvent.click(screen.getByRole('button', { name: 'Xác nhận' }));
    await screen.findAllByText('Bài tập');
    expect(axios.put).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/materials/20', { version: 2, displayName: 'Bài tập' });
  });

  it('lấy URL phát qua API và lấy URL tải riêng cho video, tài liệu', async () => {
    setup();
    vi.mocked(axios.get).mockResolvedValue({ data: { url: 'https://storage.example/signed', expiresAt: '2099-01-01' } });
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
    fireEvent.click(screen.getByRole('button', { name: 'Xem / tải lại video' }));
    await screen.findByLabelText('Video bài học');
    expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/video/playback');
    fireEvent.click(screen.getByRole('button', { name: 'Tải video xuống' }));
    await waitFor(() => expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/video/download'));
    fireEvent.click(screen.getByRole('button', { name: 'Tải xuống', exact: true }));
    await waitFor(() => expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/materials/20/download'));
  });

  it('gỡ video chỉ sau khi xác nhận', async () => {
    const store = setup();
    fireEvent.click(screen.getByRole('button', { name: 'Gỡ video' }));
    await screen.findByRole('dialog');
    expect(axios.delete).not.toHaveBeenCalled();
    const result = structuredClone(store.getState().courseBuilder.data!);
    result.chapters[0].lessons[0].lesson.videoUrl = null;
    result.chapters[0].lessons[0].lesson.videoFileName = null;
    vi.mocked(axios.delete).mockResolvedValueOnce({ data: result });
    fireEvent.click(screen.getByRole('button', { name: 'Xác nhận' }));
    await screen.findByText('Chưa có video cho bài học này.');
    expect(axios.delete).toHaveBeenCalledWith('/api/instructor/courses/1/lessons/101/video', { params: { version: 1 } });
  });
});
