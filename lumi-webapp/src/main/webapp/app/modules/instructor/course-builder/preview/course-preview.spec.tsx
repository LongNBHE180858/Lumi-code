import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { act, cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import axios from 'axios';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import reducer, { createDraft, type IBuilderAggregate } from '../course-builder.reducer';
import CoursePreview from './course-preview';
import { PreviewMedia } from './preview-media';

vi.mock('axios', () => ({ default: { get: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { error: vi.fn() } }));
const data = (): IBuilderAggregate => ({
  course: {
    id: 7,
    version: 1,
    title: 'Vật lý 9',
    status: 'DRAFT',
    subject: { id: 1, name: 'Vật lý' },
    grade: { id: 9, name: 'Lớp 9' },
    bookSeries: 'Bộ sách đã lưu',
    shortDescription: 'Tóm tắt đã lưu',
    description: 'Mô tả đầy đủ đã lưu',
    targetAudience: 'Học sinh yêu thích vật lý',
    prerequisites: 'Kiến thức lớp 8',
    learningObjectives: ['Hiểu định luật Ohm', 'Đo cường độ dòng điện'],
    author: { id: 1, firstName: 'Lan', lastName: 'Nguyen', login: 'teacher' },
    thumbnailUrl: 'private/cover',
    introVideoUrl: 'private/intro',
    price: 450000,
    accessDurationValue: 3,
    accessDurationUnit: 'MONTH',
  },
  chapters: [
    {
      chapter: { id: 20, title: 'Điện học', orderIndex: 0 },
      lessons: [
        {
          lesson: {
            id: 101,
            title: 'Dòng điện',
            description: 'Mô tả bài đã lưu',
            isPreview: true,
            videoUrl: 'private/video',
            durationSeconds: 120,
          },
          materials: [{ id: 55, displayName: 'Bài tập PDF', fileName: 'exercise.pdf', fileUrl: 'private/pdf' }],
        },
        { lesson: { id: 102, title: 'Điện trở', isPreview: false, videoUrl: 'private/locked' }, materials: [] },
        { lesson: { id: 103, title: 'Bài đọc thêm', isPreview: true }, materials: [] },
      ],
    },
  ],
  totalLessons: 3,
  totalMaterials: 1,
  totalVideos: 2,
});
const access = (url = 'https://signed.example/file', expiresAt = new Date(Date.now() + 300000).toISOString()) => ({
  data: { url, expiresAt },
});
const setup = (initial = data()) => {
  const store = configureStore({ reducer: { courseBuilder: reducer } });
  store.dispatch(createDraft.fulfilled(initial, 'initial', undefined));
  const onNavigateTab = vi.fn();
  const onBack = vi.fn();
  const onNext = vi.fn();
  render(
    <Provider store={store}>
      <CoursePreview onNavigateTab={onNavigateTab} onBack={onBack} onNext={onNext} />
    </Provider>,
  );
  return { store, onNavigateTab, onBack, onNext };
};
beforeEach(() => {
  vi.clearAllMocks();
  vi.mocked(axios.get).mockResolvedValue(access());
});
afterEach(() => {
  cleanup();
  vi.useRealTimers();
});

it('renders saved course information, objectives and signed cover/intro', async () => {
  setup();
  for (const text of [
    'Vật lý 9',
    'Vật lý',
    'Lớp 9',
    'Bộ sách đã lưu',
    'Tóm tắt đã lưu',
    'Mô tả đầy đủ đã lưu',
    'Học sinh yêu thích vật lý',
    'Kiến thức lớp 8',
    'Hiểu định luật Ohm',
  ])
    expect(screen.getByText(text)).toBeTruthy();
  expect(screen.getByText('Giảng viên: Lan Nguyen')).toBeTruthy();
  expect(screen.getByText('450.000 ₫')).toBeTruthy();
  await waitFor(() => expect(screen.getByAltText('Ảnh bìa Vật lý 9').getAttribute('src')).toBe('https://signed.example/file'));
  expect(screen.getByLabelText('Video giới thiệu').getAttribute('src')).toBe('https://signed.example/file');
  expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/7/thumbnail/access');
  expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/7/intro-video/playback');
  expect(screen.queryByText(/Toán 6/)).toBeNull();
  expect((screen.getByRole('button', { name: 'Đăng ký học' }) as HTMLButtonElement).disabled).toBe(true);
});

it('expands real chapters and shows missing video duration without treating it as zero', async () => {
  setup();
  await screen.findByLabelText('Video giới thiệu');
  const chapter = screen.getByRole('button', { name: /Điện học • 3 bài học/ });
  fireEvent.click(chapter);
  expect(chapter.getAttribute('aria-expanded')).toBe('true');
  expect(screen.getAllByText('Đã xác định 2 phút • Còn 1 video chưa có thời lượng').length).toBeGreaterThan(0);
  expect(screen.getByText('Điện trở')).toBeTruthy();
  expect(screen.queryByRole('button', { name: /Xem thử: Điện trở/ })).toBeNull();
  expect(screen.getByText('Chưa bật xem thử')).toBeTruthy();
  fireEvent.click(chapter);
  expect(chapter.getAttribute('aria-expanded')).toBe('false');
});

it('hides preview data and media when reloading a course that is no longer accessible', async () => {
  setup();
  await screen.findByLabelText('Video giới thiệu');
  vi.mocked(axios.get).mockRejectedValueOnce({ response: { status: 404 }, message: 'Not found' });
  fireEvent.click(screen.getByRole('button', { name: 'Tải lại bản xem trước' }));
  expect(await screen.findByText('Không thể truy cập khóa học. Khóa học không còn tồn tại hoặc bạn không có quyền xem.')).toBeTruthy();
  expect(screen.queryByText('Vật lý 9')).toBeNull();
  expect(screen.queryByLabelText('Video giới thiệu')).toBeNull();
});

it('opens only preview lessons and requests authorized video/material links on demand', async () => {
  setup();
  fireEvent.click(screen.getByRole('button', { name: 'Xem bài học: Dòng điện' }));
  const dialog = within(await screen.findByRole('dialog'));
  expect(dialog.getByText('Mô tả bài đã lưu')).toBeTruthy();
  await waitFor(() => expect(dialog.getByLabelText('Video Dòng điện').getAttribute('src')).toBe('https://signed.example/file'));
  expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/7/lessons/101/video/playback');
  expect(axios.get).not.toHaveBeenCalledWith('/api/instructor/courses/7/lessons/101/materials/55/download');
  fireEvent.click(dialog.getByRole('button', { name: 'Xem tài liệu: Bài tập PDF' }));
  const link = await dialog.findByRole('link', { name: 'Mở/tải tài liệu: Bài tập PDF' });
  expect(link.getAttribute('href')).toBe('https://signed.example/file');
  expect(link.getAttribute('rel')).toContain('noopener');
  expect(axios.get).toHaveBeenCalledWith('/api/instructor/courses/7/lessons/101/materials/55/download');
  fireEvent.click(dialog.getByRole('button', { name: 'Đóng bài xem thử' }));
  await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
  fireEvent.click(screen.getByRole('button', { name: 'Xem bài học: Bài đọc thêm' }));
  expect(within(await screen.findByRole('dialog')).getByText('Bài học chưa có video.')).toBeTruthy();
});

it('provides empty states without requesting media for an empty course', () => {
  const empty: IBuilderAggregate = {
    course: { id: 7, version: 1, title: '', status: 'DRAFT' },
    chapters: [],
    totalLessons: 0,
    totalVideos: 0,
    totalMaterials: 0,
  };
  setup(empty);
  for (const text of [
    'Chưa có ảnh bìa.',
    'Chưa có video giới thiệu.',
    'Chưa có mục tiêu học tập.',
    'Chưa có chương hoặc bài học.',
    'Chưa có bài học bật xem thử.',
  ])
    expect(screen.getByText(text)).toBeTruthy();
  expect(axios.get).not.toHaveBeenCalled();
});

it('routes edit/back/review buttons and reloads the newest saved aggregate', async () => {
  const { onNavigateTab, onBack, onNext } = setup();
  fireEvent.click(screen.getByRole('button', { name: 'Chỉnh sửa → Thông tin khóa học' }));
  fireEvent.click(screen.getByRole('button', { name: 'Chỉnh sửa → Học phí' }));
  fireEvent.click(screen.getByRole('button', { name: 'Chỉnh sửa → Nội dung bài học' }));
  expect(onNavigateTab.mock.calls).toEqual([['basic-info'], ['pricing'], ['curriculum']]);
  fireEvent.click(screen.getByRole('button', { name: '← Học phí' }));
  fireEvent.click(screen.getByRole('button', { name: 'Tiếp: Gửi duyệt →' }));
  expect(onBack).toHaveBeenCalledOnce();
  expect(onNext).toHaveBeenCalledOnce();
  const saved = data();
  saved.course.title = 'Tên khóa học mới';
  saved.course.price = 0;
  vi.mocked(axios.get).mockImplementation(url => Promise.resolve(url.toString().endsWith('/builder') ? { data: saved } : access()));
  fireEvent.click(screen.getByRole('button', { name: 'Tải lại bản xem trước' }));
  expect(await screen.findByText('Tên khóa học mới')).toBeTruthy();
  expect(screen.getByText('Miễn phí')).toBeTruthy();
});

it.each([403, 404])('does not use raw media URLs when access returns %s', async status => {
  vi.mocked(axios.get).mockRejectedValue({ response: { status } });
  setup();
  await waitFor(() => expect(screen.getAllByRole('button', { name: /Lấy lại liên kết/ }).length).toBe(2));
  expect(screen.queryByAltText('Ảnh bìa Vật lý 9')).toBeNull();
  expect(screen.queryByLabelText('Video giới thiệu')).toBeNull();
  expect(document.querySelector('[src^="private/"]')).toBeNull();
});

it('requests a new signed URL after a player error', async () => {
  render(<PreviewMedia target={{ courseId: 7, kind: 'intro', source: 'private/intro' }} label="Intro" />);
  const video = await screen.findByLabelText('Intro');
  fireEvent.error(video);
  expect(screen.queryByLabelText('Intro')).toBeNull();
  vi.mocked(axios.get).mockResolvedValueOnce(access('https://signed.example/renewed'));
  fireEvent.click(screen.getByRole('button', { name: 'Lấy lại liên kết: Intro' }));
  await waitFor(() => expect(screen.getByLabelText('Intro').getAttribute('src')).toBe('https://signed.example/renewed'));
  expect(axios.get).toHaveBeenCalledTimes(2);
});

it('expires signed links and allows explicit renewal', async () => {
  vi.useFakeTimers();
  vi.mocked(axios.get).mockResolvedValueOnce(access('https://signed.example/short', new Date(Date.now() + 1000).toISOString()));
  render(<PreviewMedia target={{ courseId: 7, kind: 'intro', source: 'private/intro' }} label="Intro" />);
  await act(async () => {
    await Promise.resolve();
  });
  expect(screen.getByLabelText('Intro')).toBeTruthy();
  await act(async () => {
    await vi.advanceTimersByTimeAsync(1001);
  });
  expect(screen.queryByLabelText('Intro')).toBeNull();
  expect(screen.getByText('Liên kết đã hết hạn. Hãy lấy lại liên kết để tiếp tục.')).toBeTruthy();
  vi.mocked(axios.get).mockResolvedValueOnce(access('https://signed.example/renewed'));
  await act(async () => {
    fireEvent.click(screen.getByRole('button', { name: 'Lấy lại liên kết: Intro' }));
    await Promise.resolve();
  });
  expect(screen.getByLabelText('Intro').getAttribute('src')).toBe('https://signed.example/renewed');
});

it('ignores an old access response after changing course', async () => {
  let finish: (value: unknown) => void;
  vi.mocked(axios.get).mockImplementationOnce(
    () =>
      new Promise(resolve => {
        finish = resolve;
      }),
  );
  const view = render(<PreviewMedia target={{ courseId: 7, kind: 'intro', source: 'old' }} label="Intro" />);
  vi.mocked(axios.get).mockResolvedValueOnce(access('https://signed.example/new'));
  view.rerender(<PreviewMedia target={{ courseId: 8, kind: 'intro', source: 'new' }} label="Intro" />);
  await waitFor(() => expect(screen.getByLabelText('Intro').getAttribute('src')).toBe('https://signed.example/new'));
  await act(async () => {
    finish!(access('https://signed.example/old'));
    await Promise.resolve();
  });
  expect(screen.getByLabelText('Intro').getAttribute('src')).toBe('https://signed.example/new');
});

it('toggles between review mode and student mode to hide edit shortcuts', async () => {
  setup();
  expect(screen.getByRole('button', { name: 'Chỉnh sửa → Thông tin khóa học' })).toBeTruthy();
  await act(async () => {
    fireEvent.click(screen.getByRole('button', { name: /Học viên/ }));
    await Promise.resolve();
  });
  expect(screen.queryByRole('button', { name: 'Chỉnh sửa → Thông tin khóa học' })).toBeNull();
  expect(screen.queryByRole('button', { name: 'Chỉnh sửa → Học phí' })).toBeNull();
  await act(async () => {
    fireEvent.click(screen.getByRole('button', { name: /Soát lỗi/ }));
    await Promise.resolve();
  });
  expect(screen.getByRole('button', { name: 'Chỉnh sửa → Thông tin khóa học' })).toBeTruthy();
});

it('supports navigating between previous and next trial lessons inside modal', async () => {
  setup();
  fireEvent.click(screen.getByRole('button', { name: 'Xem bài học: Dòng điện' }));
  const dialog = within(await screen.findByRole('dialog'));
  expect(dialog.getByText('Dòng điện')).toBeTruthy();
  expect(dialog.getByRole('button', { name: /Bài tiếp theo/ })).toBeTruthy();

  fireEvent.click(dialog.getByRole('button', { name: /Bài tiếp theo/ }));
  const nextDialog = within(await screen.findByRole('dialog'));
  expect(nextDialog.getByText('Bài đọc thêm')).toBeTruthy();
  expect(nextDialog.getByRole('button', { name: /Bài trước/ })).toBeTruthy();

  fireEvent.click(nextDialog.getByRole('button', { name: /Bài trước/ }));
  const prevDialog = within(await screen.findByRole('dialog'));
  expect(prevDialog.getByText('Dòng điện')).toBeTruthy();
});
