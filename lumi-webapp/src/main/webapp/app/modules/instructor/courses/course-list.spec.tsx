import React from 'react';
import { act, cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import axios from 'axios';
import type { AxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, it, expect, vi } from 'vitest';
import { CourseList } from './course-list';
import { COURSES_API, ICourseListItem } from './course-list.reducer';

vi.mock('axios', () => ({ default: { get: vi.fn(), delete: vi.fn(), post: vi.fn() } }));
vi.mock('./course-list.scss', () => ({}));
const item = (id: number, status: ICourseListItem['status'] = 'DRAFT'): ICourseListItem => ({
  id,
  version: id + 10,
  title: 'Khóa học ' + id,
  status,
  subject: { id: 2, name: 'Vật lý' },
  grade: { id: 9, name: 'Lớp 9' },
  chapterCount: 3,
  videoCount: 4,
  totalDurationHours: 1.5,
  price: 250000,
  accessDurationValue: 2,
  accessDurationUnit: 'YEAR',
  updatedAt: '2026-11-04T03:20:00Z',
});
let rows: ICourseListItem[];
let listError: number | undefined;
const renderPage = () =>
  render(
    <MemoryRouter>
      <CourseList />
    </MemoryRouter>,
  );
const rowFor = (title: string) => screen.getByRole('link', { name: title }).closest('.mc-row') as HTMLElement;
const paramsOf = (config?: AxiosRequestConfig) => (config?.params ?? {}) as Record<string, string | number>;
const httpError = (status: number) => Object.assign(new Error('Request failed'), { response: { status } });
const mainCalls = () => vi.mocked(axios.get).mock.calls.filter(([url, config]) => url === COURSES_API && paramsOf(config).size !== 1);

beforeEach(() => {
  vi.resetAllMocks();
  rows = [item(1), item(2, 'ACTIVE'), item(3, 'PENDING')];
  listError = undefined;
  vi.mocked(axios.get).mockImplementation((url, config) => {
    if (url === '/api/subjects') return Promise.resolve({ data: [{ id: 2, name: 'Vật lý' }] } as any);
    if (url === '/api/grades') return Promise.resolve({ data: [{ id: 9, name: 'Lớp 9' }] } as any);
    if (String(url).endsWith('/thumbnail/access')) return Promise.resolve({ data: { url: 'https://signed.example/cover' } } as any);
    const params = paramsOf(config);
    if (listError && params.size !== 1) return Promise.reject(httpError(listError));
    let result = rows.filter(
      row =>
        (!params['status.equals'] || row.status === params['status.equals']) &&
        (!params['title.contains'] || row.title.includes(String(params['title.contains']))) &&
        (!params['subjectId.equals'] || String(row.subject?.id) === params['subjectId.equals']) &&
        (!params['gradeId.equals'] || String(row.grade?.id) === params['gradeId.equals']),
    );
    const total = result.length;
    result = result.slice(Number(params.page) * Number(params.size), (Number(params.page) + 1) * Number(params.size));
    return Promise.resolve({ data: result, headers: { 'x-total-count': String(total) } } as any);
  });
  vi.mocked(axios.delete).mockImplementation(url => {
    rows = rows.filter(row => String(url) !== COURSES_API + '/' + row.id);
    return Promise.resolve({} as any);
  });
});
afterEach(() => {
  cleanup();
  vi.useRealTimers();
  localStorage.clear();
});

describe('CourseList API integration', () => {
  it('renders backend fields, duration units, signed thumbnails and global totals', async () => {
    rows[0].thumbnailUrl = 'private/cover';
    renderPage();
    await screen.findByRole('link', { name: 'Khóa học 1' });
    const row = rowFor('Khóa học 1');
    expect(within(row).getByText('3 chương')).toBeDefined();
    expect(within(row).getByText('Một lần • 2 năm')).toBeDefined();
    await waitFor(() => expect(within(row).getByRole('img').getAttribute('src')).toBe('https://signed.example/cover'));
    expect(screen.getByRole('button', { name: 'Tất cả 3' })).toBeDefined();
    expect(screen.getByRole('button', { name: 'Bản nháp 1' })).toBeDefined();
    expect(mainCalls()[0][1]?.params).toMatchObject({ page: 0, size: 5, sort: 'updatedAt,desc' });
  });

  it('uses an empty backend response instead of local storage or sample courses', async () => {
    rows = [];
    localStorage.setItem('lumi_instructor_courses', JSON.stringify([{ title: 'Old fake course' }]));
    renderPage();
    await screen.findByText('Không tìm thấy khóa học phù hợp');
    expect(screen.queryByText('Old fake course')).toBeNull();
    expect(screen.queryAllByRole('link', { name: /Khóa học [0-9]/ })).toHaveLength(0);
  });

  it('shows loading and allows retry after a failed list request', async () => {
    listError = 500;
    renderPage();
    expect(screen.getByRole('status').textContent).toContain('Đang tải');
    await screen.findByText('Không thể tải danh sách khóa học. Vui lòng thử lại.');
    listError = undefined;
    fireEvent.click(screen.getByRole('button', { name: 'Thử lại' }));
    await screen.findByRole('link', { name: 'Khóa học 1' });
  });

  it('sends backend status, catalog IDs, search and sorting filters', async () => {
    renderPage();
    await screen.findByRole('link', { name: 'Khóa học 1' });
    fireEvent.click(screen.getByRole('button', { name: 'Bản nháp 1' }));
    await waitFor(() => expect(paramsOf(mainCalls().at(-1)?.[1])['status.equals']).toBe('DRAFT'));
    fireEvent.change(screen.getByLabelText('Môn học'), { target: { value: '2' } });
    fireEvent.change(screen.getByLabelText('Lớp'), { target: { value: '9' } });
    fireEvent.change(screen.getByLabelText('Sắp xếp'), { target: { value: 'price_desc' } });
    fireEvent.change(screen.getByPlaceholderText('Tìm theo tên khóa học...'), { target: { value: 'Khóa học 1' } });
    await waitFor(() =>
      expect(mainCalls().at(-1)?.[1]?.params).toMatchObject({
        'title.contains': 'Khóa học 1',
        'subjectId.equals': '2',
        'gradeId.equals': '9',
        sort: 'price,desc',
        page: 0,
      }),
    );
  });

  it('loads the next backend page and returns to the previous page after deleting its last course', async () => {
    rows = Array.from({ length: 6 }, (_, index) => item(index + 1));
    renderPage();
    await screen.findByRole('link', { name: 'Khóa học 1' });
    fireEvent.click(screen.getByRole('button', { name: 'Sau' }));
    await screen.findByRole('link', { name: 'Khóa học 6' });
    expect(paramsOf(mainCalls().at(-1)?.[1]).page).toBe(1);
    fireEvent.click(within(rowFor('Khóa học 6')).getByRole('button', { name: 'Xóa' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Đồng ý xóa' }));
    await screen.findByRole('link', { name: 'Khóa học 1' });
    expect(paramsOf(mainCalls().at(-1)?.[1]).page).toBe(0);
    expect(axios.delete).toHaveBeenCalledWith(COURSES_API + '/6', { params: { version: 16 } });
  });

  it('retains the course and displays errors when deletion fails', async () => {
    vi.mocked(axios.delete).mockRejectedValue({ response: { status: 500 } });
    renderPage();
    await screen.findByRole('link', { name: 'Khóa học 1' });
    fireEvent.click(within(rowFor('Khóa học 1')).getByRole('button', { name: 'Xóa' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Đồng ý xóa' }));
    await screen.findByText('Không thể xóa khóa học. Vui lòng thử lại.');
    expect(screen.getByRole('link', { name: 'Khóa học 1' })).toBeDefined();
    expect(screen.getByRole('dialog')).toBeDefined();
  });

  it('refreshes after a version conflict and blocks deletion for non-drafts', async () => {
    vi.mocked(axios.delete).mockImplementation(() => {
      rows[0] = { ...rows[0], status: 'PENDING', version: 20 };
      return Promise.reject(httpError(409));
    });
    renderPage();
    await screen.findByRole('link', { name: 'Khóa học 1' });
    expect(within(rowFor('Khóa học 2')).queryByRole('button', { name: 'Xóa' })).toBeNull();
    expect(within(rowFor('Khóa học 3')).queryByRole('button', { name: 'Sửa' })).toBeNull();
    expect(within(rowFor('Khóa học 3')).queryByRole('button', { name: 'Xóa' })).toBeNull();
    fireEvent.click(within(rowFor('Khóa học 1')).getByRole('button', { name: 'Xóa' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Đồng ý xóa' }));
    await screen.findByText(/Khóa học đã thay đổi/);
    await waitFor(() => expect(within(rowFor('Khóa học 1')).queryByRole('button', { name: 'Xóa' })).toBeNull());
  });

  it('ignores an older list response after changing filters', async () => {
    const original = vi.mocked(axios.get).getMockImplementation()!;
    let resolveOld!: (value: any) => void;
    vi.mocked(axios.get).mockImplementation((url, config) => {
      if (url === COURSES_API && paramsOf(config).size === 5 && !paramsOf(config)['status.equals']) {
        return new Promise(resolve => {
          resolveOld = resolve;
        });
      }
      return original(url, config);
    });
    renderPage();
    await screen.findByRole('button', { name: 'Bản nháp 1' });
    fireEvent.click(screen.getByRole('button', { name: 'Bản nháp 1' }));
    await screen.findByRole('link', { name: 'Khóa học 1' });
    await act(async () => {
      resolveOld({ data: [item(99)], headers: { 'x-total-count': '1' } });
      await Promise.resolve();
    });
    expect(screen.queryByRole('link', { name: 'Khóa học 99' })).toBeNull();
  });
});

it('withdraws a pending course from the management list and refreshes its status', async () => {
  renderPage();
  await screen.findByRole('link', { name: 'Khóa học 3' });
  vi.mocked(axios.post).mockImplementation(() => {
    rows[2] = { ...rows[2], status: 'DRAFT', version: 14 };
    return Promise.resolve({ data: {} } as any);
  });
  fireEvent.click(within(rowFor('Khóa học 3')).getByRole('button', { name: 'Rút lại gửi duyệt' }));
  fireEvent.click(await screen.findByRole('button', { name: 'Xác nhận rút duyệt' }));
  await waitFor(() => expect(within(rowFor('Khóa học 3')).queryByRole('button', { name: 'Rút lại gửi duyệt' })).toBeNull());
  expect(axios.post).toHaveBeenCalledWith(COURSES_API + '/3/review-requests/withdraw', { version: 13 });
  expect((within(rowFor('Khóa học 3')).getByRole('button', { name: 'Xóa' }) as HTMLButtonElement).disabled).toBe(false);
});
