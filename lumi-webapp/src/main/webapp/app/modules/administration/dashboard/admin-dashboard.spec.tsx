import React from 'react';
import { MemoryRouter } from 'react-router';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import AdminDashboard from './admin-dashboard';
import { getAdminDashboard, getDashboardActivities, getDashboardRequests, type DashboardData } from './dashboard-api';

vi.mock('../layout/admin-layout', () => ({ default: ({ children }: { children: React.ReactNode }) => <div>{children}</div> }));
vi.mock('./dashboard-api', () => ({ getAdminDashboard: vi.fn(), getDashboardActivities: vi.fn(), getDashboardRequests: vi.fn() }));

const dashboard: DashboardData = {
  counts: { students: 7, parents: 3, instructors: 1, courses: 4, users: 11 },
  previousMonth: null,
  monthlyUsers: [{ month: '2026-11', users: 11 }],
  activities: [{ kind: 'INSTRUCTOR_APPROVED', occurredAt: '2026-11-11T12:00:00Z' }],
  requests: { instructorApplications: 2, courseReviews: 3, supportTickets: 4 },
  unclassifiedUsers: 1,
};

describe('AdminDashboard', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(getAdminDashboard).mockResolvedValue({ data: dashboard } as never);
  });

  it('renders live counts and leaves missing history unestimated', async () => {
    render(
      <MemoryRouter>
        <AdminDashboard />
      </MemoryRouter>,
    );

    await waitFor(() => expect(screen.queryByText('Tổng người dùng')).not.toBeNull());
    expect(screen.getAllByText('11')).toHaveLength(3);
    expect(screen.getAllByText('--%')).toHaveLength(4);
    expect(screen.queryByText('Phụ huynh')).not.toBeNull();
    expect(screen.queryByText('Giảng viên mới tham gia')).not.toBeNull();
    expect(screen.queryByText('2 chờ')).not.toBeNull();
    expect(screen.queryByText('3 chờ')).not.toBeNull();
    expect(screen.queryByText('4 mới')).not.toBeNull();
    expect(screen.queryByText('Chưa đủ mốc lịch sử')).not.toBeNull();
    expect(screen.queryByText('Nội dung bị báo cáo')).toBeNull();
  });

  it('shows increases, decreases and unchanged counts with distinct trends', async () => {
    vi.mocked(getAdminDashboard).mockResolvedValue({
      data: {
        ...dashboard,
        previousMonth: { students: 6, parents: 3, instructors: 1, courses: 5, users: 10 },
      },
    } as never);
    render(
      <MemoryRouter>
        <AdminDashboard />
      </MemoryRouter>,
    );

    await waitFor(() => expect(screen.queryByText('Tổng người dùng')).not.toBeNull());
    const trendFor = (label: string) => screen.getByText(label).closest('article')?.querySelector('.admin-stat-trend');
    expect(trendFor('Tổng người dùng')?.classList.contains('admin-stat-trend-up')).toBe(true);
    expect(trendFor('Tổng giảng viên')?.classList.contains('admin-stat-trend-flat')).toBe(true);
    expect(trendFor('Tổng khóa học')?.classList.contains('admin-stat-trend-down')).toBe(true);
    expect(screen.queryByText('-20,0%')).not.toBeNull();
  });

  it('loads each full list only when opened and requests later pages near the scroll end', async () => {
    const previousBodyOverflow = document.body.style.overflow;
    const previousRootOverflow = document.documentElement.style.overflow;
    vi.mocked(getDashboardActivities).mockImplementation(page =>
      Promise.resolve({
        data: {
          items: [
            {
              kind: page === 0 ? 'COURSE_APPROVED' : 'COURSE_CREATED',
              occurredAt: page === 0 ? '2026-11-12T12:00:00Z' : '2026-11-11T12:00:00Z',
            },
          ],
          hasMore: page === 0,
        },
      } as never),
    );
    vi.mocked(getDashboardRequests).mockResolvedValue({
      data: { items: [{ kind: 'SUPPORT_TICKET', id: 42, label: 'Hỗ trợ: Cần giúp đỡ' }], hasMore: false },
    } as never);

    render(
      <MemoryRouter>
        <AdminDashboard />
      </MemoryRouter>,
    );

    await waitFor(() => expect(screen.queryByText('Tổng người dùng')).not.toBeNull());
    expect(getDashboardActivities).not.toHaveBeenCalled();
    expect(getDashboardRequests).not.toHaveBeenCalled();

    fireEvent.click(screen.getAllByRole('button', { name: 'Xem tất cả' })[0]);
    expect(document.body.style.overflow).toBe('hidden');
    expect(document.documentElement.style.overflow).toBe('hidden');
    await waitFor(() => expect(screen.queryByText('Khóa học được phê duyệt')).not.toBeNull());
    expect(getDashboardActivities).toHaveBeenCalledWith(0, '', expect.any(AbortSignal));
    expect(getDashboardRequests).not.toHaveBeenCalled();

    const dialog = screen.getByRole('dialog', { name: 'Hoạt động gần đây' });
    Object.defineProperties(dialog, {
      scrollHeight: { configurable: true, value: 800 },
      clientHeight: { configurable: true, value: 400 },
      scrollTop: { configurable: true, value: 300 },
    });
    fireEvent.scroll(dialog);
    fireEvent.scroll(dialog);
    await waitFor(() => expect(screen.queryByText('Khóa học mới được tạo')).not.toBeNull());
    expect(getDashboardActivities).toHaveBeenCalledWith(1, '', expect.any(AbortSignal));
    expect(getDashboardActivities).toHaveBeenCalledTimes(2);
    expect(screen.queryByRole('button', { name: 'Tải thêm' })).toBeNull();

    fireEvent.click(screen.getByRole('button', { name: 'Đóng' }));
    expect(document.body.style.overflow).toBe(previousBodyOverflow);
    expect(document.documentElement.style.overflow).toBe(previousRootOverflow);
    fireEvent.click(screen.getAllByRole('button', { name: 'Xem tất cả' })[1]);
    expect(document.body.style.overflow).toBe('hidden');
    expect(document.documentElement.style.overflow).toBe('hidden');
    await waitFor(() => expect(screen.queryByText('Hỗ trợ: Cần giúp đỡ')).not.toBeNull());
    expect(getDashboardRequests).toHaveBeenCalledWith(0, '', expect.any(AbortSignal));
    fireEvent.click(screen.getByRole('button', { name: 'Đóng' }));
    expect(document.body.style.overflow).toBe(previousBodyOverflow);
    expect(document.documentElement.style.overflow).toBe(previousRootOverflow);
  });
});
