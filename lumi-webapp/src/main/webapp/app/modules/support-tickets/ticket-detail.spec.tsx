import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ticketApi, type Ticket } from './ticket-api';
import TicketDetail from './ticket-detail';

vi.mock('./ticket-api', () => ({
  ticketApi: {
    get: vi.fn(),
    messages: vi.fn(),
    sendMessage: vi.fn(),
    reopen: vi.fn(),
    changeStatus: vi.fn(),
  },
}));

const ticket: Ticket = {
  id: 42,
  type: 'GENERAL',
  subject: 'Cần hỗ trợ đăng nhập',
  status: 'RESOLVED',
  createdAt: '2026-11-03T10:00:00Z',
  userId: 10,
  userLogin: 'ticketowner',
};

const renderDetail = (admin = false) => {
  const store = configureStore({ reducer: { authentication: () => ({ account: { login: admin ? 'ticketadmin' : 'ticketowner' } }) } });
  return render(
    <Provider store={store}>
      <MemoryRouter initialEntries={[admin ? '/admin/support-tickets/42' : '/support-tickets/42']}>
        <Routes>
          <Route path={admin ? '/admin/support-tickets/:id' : '/support-tickets/:id'} element={<TicketDetail admin={admin} />} />
        </Routes>
      </MemoryRouter>
    </Provider>,
  );
};

describe('TicketDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(ticketApi.get).mockResolvedValue({ data: ticket } as never);
    vi.mocked(ticketApi.messages).mockResolvedValue({ data: [], headers: { 'x-total-count': '0' } } as never);
  });

  it('allows the creator to reply to a resolved ticket', async () => {
    vi.mocked(ticketApi.sendMessage).mockResolvedValue({ data: { id: 1 } } as never);
    renderDetail();

    await screen.findByText('Cần hỗ trợ đăng nhập');
    fireEvent.change(screen.getByLabelText('Trả lời'), { target: { value: 'Tôi vẫn cần hỗ trợ.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi tin nhắn' }));

    await waitFor(() => expect(ticketApi.sendMessage).toHaveBeenCalledWith(42, 'Tôi vẫn cần hỗ trợ.'));
  });

  it('allows an admin to mark a ticket resolved', async () => {
    vi.mocked(ticketApi.get).mockResolvedValue({ data: { ...ticket, status: 'IN_PROGRESS' } } as never);
    vi.mocked(ticketApi.changeStatus).mockResolvedValue({ data: ticket } as never);
    renderDetail(true);

    fireEvent.click(await screen.findByRole('button', { name: 'Đánh dấu đã giải quyết' }));

    await waitFor(() => expect(ticketApi.changeStatus).toHaveBeenCalledWith(42, 'RESOLVED'));
    expect(await screen.findByText('Đã giải quyết')).toBeTruthy();
  });
});
