import React from 'react';
import { MemoryRouter } from 'react-router';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import CourseReviewList from './course-review-list';
import { courseReviewApi, type ICourseReviewRequest } from './course-review-api';

vi.mock('../../layout/admin-layout', () => ({ default: ({ children }: { children: React.ReactNode }) => <div>{children}</div> }));

vi.mock('./course-review-api', async importOriginal => {
  const actual = await importOriginal<typeof import('./course-review-api')>();
  return {
    ...actual,
    courseReviewApi: {
      ...actual.courseReviewApi,
      list: vi.fn(),
      count: vi.fn(),
      approve: vi.fn(),
      reject: vi.fn(),
      getThumbnailUrl: vi.fn().mockResolvedValue(null),
    },
  };
});

const sampleRequest: ICourseReviewRequest = {
  id: 101,
  courseVersion: 2,
  status: 'PENDING',
  submissionNote: 'Nhờ admin kiểm tra video chương 1',
  rightsConfirmedAt: '2026-11-01T10:00:00Z',
  submittedAt: '2026-11-01T10:00:00Z',
  course: {
    id: 50,
    title: 'Toán học nâng cao 10',
    version: 2,
    status: 'PENDING',
    price: 250000,
    accessDurationValue: 12,
    accessDurationUnit: 'MONTH',
    subject: { id: 1, name: 'Toán' },
    grade: { id: 10, name: 'Lớp 10' },
    bookSeries: 'Cánh Diều',
    authorDisplayName: 'Thầy Hùng',
  },
  submittedBy: {
    id: 9,
    login: 'hung_math',
    firstName: 'Hùng',
    lastName: 'Trần',
    email: 'hung@lumi.edu.vn',
  },
};

const approvedRequest: ICourseReviewRequest = {
  ...sampleRequest,
  id: 102,
  status: 'APPROVED',
  course: {
    ...sampleRequest.course,
    id: 51,
    title: 'Vật lý 11 chuyên sâu',
    subject: { id: 2, name: 'Vật lý' },
  },
};

describe('CourseReviewList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(courseReviewApi.count).mockImplementation((status?: string) => {
      if (status === 'PENDING') return Promise.resolve({ data: 1 } as never);
      if (status === 'APPROVED') return Promise.resolve({ data: 1 } as never);
      if (status === 'REJECTED') return Promise.resolve({ data: 0 } as never);
      return Promise.resolve({ data: 2 } as never);
    });

    vi.mocked(courseReviewApi.list).mockResolvedValue({
      data: [sampleRequest],
      headers: { 'x-total-count': '1' },
    } as never);
  });

  it('renders heading, stat cards and course request items', async () => {
    render(
      <MemoryRouter>
        <CourseReviewList />
      </MemoryRouter>,
    );

    expect(await screen.findByText('Duyệt khóa học')).toBeTruthy();
    expect(screen.getByText('Toán học nâng cao 10')).toBeTruthy();
    expect(screen.getByText('Hùng Trần')).toBeTruthy();
    expect(screen.getByText('hung@lumi.edu.vn')).toBeTruthy();
    expect(screen.getByText('v2')).toBeTruthy();
    expect(screen.getAllByText(/Chờ duyệt/).length).toBeGreaterThan(0);
  });

  it('filters by status tab when tab button clicked', async () => {
    render(
      <MemoryRouter>
        <CourseReviewList />
      </MemoryRouter>,
    );

    await screen.findByText('Toán học nâng cao 10');

    vi.mocked(courseReviewApi.list).mockResolvedValueOnce({
      data: [approvedRequest],
      headers: { 'x-total-count': '1' },
    } as never);

    const approvedTab = screen.getByRole('button', { name: /Đã duyệt/ });
    fireEvent.click(approvedTab);

    await waitFor(() => {
      expect(courseReviewApi.list).toHaveBeenCalledWith(
        expect.objectContaining({
          status: 'APPROVED',
        }),
      );
    });
  });

  it('opens quick approve modal and confirms approval', async () => {
    vi.mocked(courseReviewApi.approve).mockResolvedValueOnce({
      ...sampleRequest,
      status: 'APPROVED',
    });

    render(
      <MemoryRouter>
        <CourseReviewList />
      </MemoryRouter>,
    );

    await screen.findByText('Toán học nâng cao 10');
    const approveBtn = screen.getByRole('button', { name: 'Duyệt' });
    fireEvent.click(approveBtn);

    expect(await screen.findByText('Xác nhận phê duyệt khóa học')).toBeTruthy();

    const confirmBtn = screen.getByRole('button', { name: 'Xác nhận phê duyệt' });
    fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(courseReviewApi.approve).toHaveBeenCalledWith(sampleRequest, expect.any(String));
    });
  });
});
