import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ticketApi } from './ticket-api';
import TicketList from './ticket-list';

vi.mock('./ticket-api', () => ({
  ticketApi: {
    listMine: vi.fn(),
    listAdmin: vi.fn(),
    create: vi.fn(),
  },
}));

describe('TicketList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    Object.defineProperty(URL, 'createObjectURL', { configurable: true, value: vi.fn(() => 'blob:ticket-preview') });
    Object.defineProperty(URL, 'revokeObjectURL', { configurable: true, value: vi.fn() });
    vi.mocked(ticketApi.listMine).mockResolvedValue({ data: [], headers: { 'x-total-count': '0' } } as never);
  });

  it('creates a ticket with the first message and opens its detail page', async () => {
    vi.mocked(ticketApi.create).mockResolvedValue({ data: { id: 42 } } as never);
    render(
      <MemoryRouter initialEntries={['/support-tickets']}>
        <Routes>
          <Route path="/support-tickets" element={<TicketList />} />
          <Route path="/support-tickets/:id" element={<div>Chi tiết yêu cầu 42</div>} />
        </Routes>
      </MemoryRouter>,
    );

    fireEvent.click(screen.getByRole('button', { name: 'Tạo yêu cầu' }));
    fireEvent.change(screen.getByLabelText('Tiêu đề'), { target: { value: 'Cần hỗ trợ đăng nhập' } });
    fireEvent.change(screen.getByLabelText('Nội dung'), { target: { value: 'Tôi không nhận được mã.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi yêu cầu' }));

    await waitFor(() =>
      expect(ticketApi.create).toHaveBeenCalledWith({
        type: 'GENERAL',
        subject: 'Cần hỗ trợ đăng nhập',
        content: 'Tôi không nhận được mã.',
      }),
    );
    expect(await screen.findByText('Chi tiết yêu cầu 42')).toBeTruthy();
  });

  it('includes selected images in the first ticket message', async () => {
    vi.mocked(ticketApi.create).mockResolvedValue({ data: { id: 43 } } as never);
    render(
      <MemoryRouter initialEntries={['/support-tickets']}>
        <Routes>
          <Route path="/support-tickets" element={<TicketList />} />
          <Route path="/support-tickets/:id" element={<div>Chi tiết yêu cầu</div>} />
        </Routes>
      </MemoryRouter>,
    );

    fireEvent.click(screen.getByRole('button', { name: 'Tạo yêu cầu' }));
    fireEvent.change(screen.getByLabelText('Tiêu đề'), { target: { value: 'Ảnh lỗi' } });
    fireEvent.change(screen.getByLabelText('Nội dung'), { target: { value: 'Giao diện bị lỗi' } });
    const image = new File([new Uint8Array([1])], 'issue.png', { type: 'image/png' });
    fireEvent.change(screen.getByLabelText(/Ảnh đính kèm/), { target: { files: [image] } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi yêu cầu' }));

    await waitFor(() =>
      expect(ticketApi.create).toHaveBeenCalledWith({ type: 'GENERAL', subject: 'Ảnh lỗi', content: 'Giao diện bị lỗi' }, [image]),
    );
  });
});
