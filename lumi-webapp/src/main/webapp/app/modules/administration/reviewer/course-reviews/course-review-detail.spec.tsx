import React from 'react';
import { MemoryRouter, Routes, Route } from 'react-router';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import CourseReviewDetail from './course-review-detail';
import { courseReviewApi, type ICourseReviewRequest } from './course-review-api';

vi.mock('../../layout/admin-layout', () => ({ default: ({ children }: { children: React.ReactNode }) => <div>{children}</div> }));

vi.mock('./course-review-api', async importOriginal => {
  const actual = await importOriginal<typeof import('./course-review-api')>();
  return {
    ...actual,
    courseReviewApi: {
      ...actual.courseReviewApi,
      get: vi.fn(),
      getLessons: vi.fn(),
      getBuilderData: vi.fn(),
      approve: vi.fn(),
      reject: vi.fn(),
      getThumbnailUrl: vi.fn().mockResolvedValue(null),
    },
  };
});

const sampleDetail: ICourseReviewRequest = {
  id: 101,
  courseVersion: 2,
  status: 'PENDING',
  submissionNote: 'Ghi chú gửi duyệt chi tiết',
  rightsConfirmedAt: '2026-11-01T10:00:00Z',
  submittedAt: '2026-11-01T10:00:00Z',
  course: {
    id: 50,
    title: 'Toán học nâng cao 10',
    description: 'Mô tả khóa học chi tiết và đầy đủ',
    shortDescription: 'Khóa học dành cho học sinh khá giỏi',
    version: 2,
    status: 'PENDING',
    price: 300000,
    accessDurationValue: 6,
    accessDurationUnit: 'MONTH',
    learningObjectives: ['Nắm chắc đại số', 'Luyện giải hình học'],
    subject: { id: 1, name: 'Toán' },
    grade: { id: 10, name: 'Lớp 10' },
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

const sampleLessons = [
  {
    id: 201,
    title: 'Bài 1: Mệnh đề và tập hợp',
    contentType: 'VIDEO',
    durationMinutes: 45,
    isPreview: true,
    orderIndex: 0,
    chapter: { id: 1, title: 'Chương 1: Mệnh đề toán học', orderIndex: 0 },
  },
];

describe('CourseReviewDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(courseReviewApi.get).mockResolvedValue({ data: sampleDetail } as never);
    vi.mocked(courseReviewApi.getLessons).mockResolvedValue({ data: sampleLessons } as never);
    vi.mocked(courseReviewApi.getBuilderData).mockResolvedValue(null);
  });

  const renderComponent = (id = '101') =>
    render(
      <MemoryRouter initialEntries={[`/admin/course-review/${id}`]}>
        <Routes>
          <Route path="/admin/course-review/:id" element={<CourseReviewDetail />} />
        </Routes>
      </MemoryRouter>,
    );

  it('renders submission info, course details, objectives, and curriculum', async () => {
    renderComponent();

    const requestBadges = await screen.findAllByText(/Yêu cầu #101/);
    expect(requestBadges.length).toBeGreaterThan(0);
    expect(screen.getByText('Toán học nâng cao 10')).toBeTruthy();
    expect(screen.getByText('Khóa học dành cho học sinh khá giỏi')).toBeTruthy();
    expect(screen.getByText('Mô tả khóa học chi tiết và đầy đủ')).toBeTruthy();
    expect(screen.getByText('Nắm chắc đại số')).toBeTruthy();
    expect(screen.getByText('Luyện giải hình học')).toBeTruthy();
    expect(screen.getByText('Ghi chú gửi duyệt chi tiết')).toBeTruthy();
    expect(screen.getByText(/Bài 1: Mệnh đề và tập hợp/)).toBeTruthy();
  });

  it('submits rejection when reject modal filled with reason', async () => {
    vi.mocked(courseReviewApi.reject).mockResolvedValueOnce({
      ...sampleDetail,
      status: 'REJECTED',
      reviewerFeedback: 'Cần bổ sung video bài tập',
    });

    renderComponent();

    await screen.findByText('Toán học nâng cao 10');
    const rejectButtons = screen.getAllByRole('button', { name: /Từ chối/i });
    fireEvent.click(rejectButtons[0]);

    expect(await screen.findByText('Yêu cầu chỉnh sửa / Từ chối khóa học')).toBeTruthy();

    const textarea = screen.getAllByRole('textbox')[0];
    fireEvent.change(textarea, { target: { value: 'Cần bổ sung video bài tập' } });

    const confirmReject = screen.getByRole('button', { name: 'Xác nhận từ chối' });
    fireEvent.click(confirmReject);

    await waitFor(() => {
      expect(courseReviewApi.reject).toHaveBeenCalledWith(sampleDetail, 'Cần bổ sung video bài tập');
    });
  });
});
