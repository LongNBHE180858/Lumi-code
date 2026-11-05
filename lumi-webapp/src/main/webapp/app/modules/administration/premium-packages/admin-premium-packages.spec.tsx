import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import AdminPremiumPackages from './admin-premium-packages';
import { premiumPackagesApi, type PremiumPackage } from './premium-packages-api';

vi.mock('../layout/admin-layout', () => ({ default: ({ children }: { children: React.ReactNode }) => <div>{children}</div> }));
vi.mock('./premium-packages-api', () => ({
  premiumPackagesApi: {
    list: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    setAvailability: vi.fn(),
  },
}));

const family: PremiumPackage = {
  id: 12,
  name: 'Gia đình Plus',
  packageType: 'PARENT_FAMILY',
  price: 150000,
  durationDays: 30,
  storageQuotaMb: 1024,
  aiGenerationLimit: 10,
  aiLimitPeriod: 'MONTH',
  maxClasses: null,
  coveredPeople: 4,
  revision: 1,
  available: false,
  needsClassification: false,
};

const listResponse = (items: PremiumPackage[], total = items.length) =>
  ({ data: items, headers: { 'x-total-count': String(total) } }) as never;

describe('AdminPremiumPackages', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(premiumPackagesApi.list).mockResolvedValue(listResponse([family]));
  });

  it('shows package details and changes sale availability', async () => {
    vi.mocked(premiumPackagesApi.setAvailability).mockResolvedValue({ data: { ...family, available: true } } as never);
    render(<AdminPremiumPackages />);

    expect(await screen.findByText('Gia đình Plus')).toBeTruthy();
    expect(screen.getByText(/150[.,]000/)).toBeTruthy();
    expect(screen.getByText('Ngừng bán')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Bật bán' }));
    await waitFor(() => expect(premiumPackagesApi.setAvailability).toHaveBeenCalledWith(12, true));
    expect(await screen.findByText('Đang bán')).toBeTruthy();
  });

  it('creates a family package with per-person limits and no class limit', async () => {
    vi.mocked(premiumPackagesApi.create).mockResolvedValue({ data: family } as never);
    render(<AdminPremiumPackages />);
    await screen.findByText('Gia đình Plus');
    fireEvent.click(screen.getByRole('button', { name: 'Thêm gói' }));
    fireEvent.change(screen.getByLabelText('Tên gói'), { target: { value: 'Gia đình mới' } });
    fireEvent.change(screen.getByLabelText('Loại gói'), { target: { value: 'PARENT_FAMILY' } });
    expect(screen.getByText(/Tối đa 4 người/)).toBeTruthy();
    expect(screen.queryByLabelText('Số lớp tối đa của giảng viên')).toBeNull();
    fireEvent.change(screen.getByLabelText('Giá (VNĐ)'), { target: { value: '200000' } });
    fireEvent.change(screen.getByLabelText('Thời hạn (ngày)'), { target: { value: '30' } });
    fireEvent.change(screen.getByLabelText('Dung lượng mỗi người (MB)'), { target: { value: '500' } });
    fireEvent.change(screen.getByLabelText('Lượt tạo AI mỗi người'), { target: { value: '20' } });
    fireEvent.change(screen.getByLabelText('Kỳ tính lượt AI'), { target: { value: 'DAY' } });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu gói' }));

    await waitFor(() =>
      expect(premiumPackagesApi.create).toHaveBeenCalledWith({
        name: 'Gia đình mới',
        packageType: 'PARENT_FAMILY',
        price: 200000,
        durationDays: 30,
        storageQuotaMb: 500,
        aiGenerationLimit: 20,
        aiLimitPeriod: 'DAY',
        maxClasses: null,
      }),
    );
  });

  it('edits an instructor package without changing its type', async () => {
    const instructor: PremiumPackage = { ...family, id: 20, name: 'Lớp Premium', packageType: 'INSTRUCTOR_CLASS', maxClasses: 2 };
    vi.mocked(premiumPackagesApi.list).mockResolvedValue(listResponse([instructor]));
    vi.mocked(premiumPackagesApi.update).mockResolvedValue({ data: { ...instructor, revision: 2 } } as never);
    render(<AdminPremiumPackages />);
    await screen.findByText('Lớp Premium');
    fireEvent.click(screen.getByRole('button', { name: 'Sửa Lớp Premium' }));
    expect((screen.getByLabelText('Loại gói') as HTMLSelectElement).disabled).toBe(true);
    expect(screen.getByText(/Học sinh vào lớp sau lúc mua/)).toBeTruthy();
    fireEvent.change(screen.getByLabelText('Số lớp tối đa của giảng viên'), { target: { value: '3' } });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu gói' }));
    await waitFor(() =>
      expect(premiumPackagesApi.update).toHaveBeenCalledWith(
        20,
        expect.objectContaining({ packageType: 'INSTRUCTOR_CLASS', maxClasses: 3 }),
      ),
    );
  });

  it('requires classification before an old package can be sold', async () => {
    vi.mocked(premiumPackagesApi.list).mockResolvedValue(listResponse([{ ...family, packageType: null, needsClassification: true }]));
    render(<AdminPremiumPackages />);
    await screen.findByText('Chưa phân loại');
    expect((screen.getByRole('button', { name: 'Bật bán' }) as HTMLButtonElement).disabled).toBe(true);
    fireEvent.click(screen.getByRole('button', { name: 'Sửa Gia đình Plus' }));
    expect((screen.getByLabelText('Loại gói') as HTMLSelectElement).disabled).toBe(false);
  });

  it('requests the next page from the server', async () => {
    vi.mocked(premiumPackagesApi.list).mockResolvedValue(listResponse([family], 11));
    render(<AdminPremiumPackages />);
    await screen.findByText('Gia đình Plus');
    fireEvent.click(screen.getByRole('button', { name: 'Trang sau' }));
    await waitFor(() => expect(premiumPackagesApi.list).toHaveBeenCalledWith(1, 10));
  });

  it('keeps the form open and rejects a fractional VND price', async () => {
    render(<AdminPremiumPackages />);
    await screen.findByText('Gia đình Plus');
    fireEvent.click(screen.getByRole('button', { name: 'Thêm gói' }));
    fireEvent.change(screen.getByLabelText('Tên gói'), { target: { value: 'Gói thử' } });
    fireEvent.change(screen.getByLabelText('Loại gói'), { target: { value: 'PARENT_PERSONAL' } });
    fireEvent.change(screen.getByLabelText('Giá (VNĐ)'), { target: { value: '1000.5' } });
    fireEvent.change(screen.getByLabelText('Thời hạn (ngày)'), { target: { value: '30' } });
    fireEvent.change(screen.getByLabelText('Dung lượng mỗi người (MB)'), { target: { value: '100' } });
    fireEvent.change(screen.getByLabelText('Lượt tạo AI mỗi người'), { target: { value: '10' } });
    fireEvent.submit(screen.getByRole('button', { name: 'Lưu gói' }).closest('form')!);
    expect(screen.getByRole('alert').textContent).toContain('số nguyên');
    expect(premiumPackagesApi.create).not.toHaveBeenCalled();
  });

  it('shows a load error and retries the list', async () => {
    vi.mocked(premiumPackagesApi.list).mockRejectedValueOnce(new Error('network'));
    render(<AdminPremiumPackages />);
    expect((await screen.findByRole('alert')).textContent).toContain('Không tải được');
    fireEvent.click(screen.getByRole('button', { name: 'Làm mới' }));
    expect(await screen.findByText('Gia đình Plus')).toBeTruthy();
    expect(premiumPackagesApi.list).toHaveBeenCalledTimes(2);
  });

  it('keeps the sale status when the request fails', async () => {
    vi.mocked(premiumPackagesApi.setAvailability).mockRejectedValue(new Error('network'));
    render(<AdminPremiumPackages />);
    await screen.findByText('Gia đình Plus');
    fireEvent.click(screen.getByRole('button', { name: 'Bật bán' }));
    expect((await screen.findByRole('alert')).textContent).toContain('Không thể hoàn tất');
    expect(screen.getByText('Ngừng bán')).toBeTruthy();
  });
});
