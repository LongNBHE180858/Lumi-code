import { expect, test, type Page } from '@playwright/test';

type Ticket = {
  id: number;
  type: string;
  subject: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';
  createdAt: string;
  userId: number;
  userLogin: string;
};

type Message = {
  id: number;
  content: string;
  createdAt: string;
  senderId: number;
  senderLogin: string;
};

const createdAt = '2026-11-03T10:00:00Z';

const mockApi = async (page: Page, role: 'ROLE_USER' | 'ROLE_ADMIN' | null, initialTicket?: Ticket) => {
  let ticket = initialTicket;
  const messages: Message[] = initialTicket ? [{ id: 1, content: 'Nội dung ban đầu', createdAt, senderId: 2, senderLogin: 'user' }] : [];

  await page.route('**/management/info', route => route.fulfill({ json: { activeProfiles: ['dev'] } }));
  await page.route(
    url => url.pathname.startsWith('/api/'),
    async route => {
      const request = route.request();
      const { pathname } = new URL(request.url());
      const method = request.method();
      const reply = (json: unknown, status = 200, headers: Record<string, string> = {}) => route.fulfill({ status, json, headers });

      if (pathname === '/api/account') {
        return role
          ? reply({
              id: role === 'ROLE_ADMIN' ? 1 : 2,
              login: role === 'ROLE_ADMIN' ? 'admin' : 'user',
              activated: true,
              authorities: [role],
              langKey: 'vi',
            })
          : reply({}, 401);
      }
      if (pathname === '/api/support-tickets' && method === 'POST') {
        const input = request.postDataJSON() as { type: string; subject: string; content: string };
        ticket = { id: 42, type: input.type, subject: input.subject, status: 'OPEN', createdAt, userId: 2, userLogin: 'user' };
        messages.push({ id: 1, content: input.content, createdAt, senderId: 2, senderLogin: 'user' });
        return reply(ticket, 201);
      }
      if ((pathname === '/api/support-tickets' || pathname === '/api/admin/support-tickets') && method === 'GET') {
        return reply(ticket ? [ticket] : [], 200, { 'x-total-count': ticket ? '1' : '0' });
      }
      if (pathname === '/api/support-tickets/42' && method === 'GET') return reply(ticket);
      if (pathname === '/api/support-tickets/42/messages' && method === 'GET') {
        return reply(messages, 200, { 'x-total-count': String(messages.length) });
      }
      if (pathname === '/api/support-tickets/42/messages' && method === 'POST') {
        const input = request.postDataJSON() as { content: string };
        const message = {
          id: messages.length + 1,
          content: input.content,
          createdAt,
          senderId: role === 'ROLE_ADMIN' ? 1 : 2,
          senderLogin: role === 'ROLE_ADMIN' ? 'admin' : 'user',
        };
        messages.push(message);
        if (ticket?.status === 'RESOLVED') ticket.status = 'IN_PROGRESS';
        return reply(message, 201);
      }
      if (pathname === '/api/support-tickets/42/reopen' && method === 'POST') {
        if (ticket) ticket.status = 'IN_PROGRESS';
        return reply(ticket);
      }
      if (pathname === '/api/admin/support-tickets/42/status' && method === 'PATCH') {
        if (ticket) ticket.status = (request.postDataJSON() as { status: Ticket['status'] }).status;
        return reply(ticket);
      }
      return reply({}, 404);
    },
  );
};

test('người dùng tạo yêu cầu và gửi tin nhắn', async ({ page }) => {
  await mockApi(page, 'ROLE_USER');
  await page.goto('/support-tickets');
  await expect(page.getByRole('heading', { name: 'Yêu cầu hỗ trợ của tôi' })).toBeVisible({ timeout: 30000 });
  await page.getByRole('button', { name: 'Tạo yêu cầu' }).click();
  await page.getByRole('textbox', { name: 'Tiêu đề' }).fill('Không truy cập được khóa học');
  await page.getByRole('textbox', { name: 'Nội dung' }).fill('Tôi cần hỗ trợ đăng nhập khóa học.');
  await page.getByRole('button', { name: 'Gửi yêu cầu' }).click();
  await expect(page).toHaveURL(/\/support-tickets\/42$/);
  await expect(page.getByRole('heading', { name: 'Không truy cập được khóa học' })).toBeVisible();
  await expect(page.getByText('Tôi cần hỗ trợ đăng nhập khóa học.')).toBeVisible();

  await page.getByRole('textbox', { name: 'Trả lời' }).fill('Tôi vẫn cần hỗ trợ.');
  await page.getByRole('button', { name: 'Gửi tin nhắn' }).click();
  await expect(page.getByText('Tôi vẫn cần hỗ trợ.')).toBeVisible();
});

test('tin nhắn mới trên ticket đã giải quyết chuyển trạng thái sang đang xử lý', async ({ page }) => {
  await mockApi(page, 'ROLE_USER', {
    id: 42,
    type: 'GENERAL',
    subject: 'Cần hỗ trợ',
    status: 'RESOLVED',
    createdAt,
    userId: 2,
    userLogin: 'user',
  });
  await page.goto('/support-tickets/42');
  await expect(page.getByRole('button', { name: 'Mở lại yêu cầu' })).toBeVisible();
  await page.getByRole('textbox', { name: 'Trả lời' }).fill('Vấn đề vẫn còn.');
  await page.getByRole('button', { name: 'Gửi tin nhắn' }).click();
  await expect(page.getByText('Vấn đề vẫn còn.')).toBeVisible();
  await expect(page.locator('.ticket-detail-heading .ticket-status')).toHaveText('Đang xử lý');
  await expect(page.getByRole('button', { name: 'Mở lại yêu cầu' })).toHaveCount(0);
});

test('người tạo mở lại ticket đã giải quyết', async ({ page }) => {
  await mockApi(page, 'ROLE_USER', {
    id: 42,
    type: 'GENERAL',
    subject: 'Cần hỗ trợ',
    status: 'RESOLVED',
    createdAt,
    userId: 2,
    userLogin: 'user',
  });
  await page.goto('/support-tickets/42');
  await page.getByRole('button', { name: 'Mở lại yêu cầu' }).click();
  await expect(page.locator('.ticket-detail-heading .ticket-status')).toHaveText('Đang xử lý');
});

test('admin lọc danh sách, giải quyết và mở lại yêu cầu', async ({ page }) => {
  await mockApi(page, 'ROLE_ADMIN', {
    id: 42,
    type: 'GENERAL',
    subject: 'Cần hỗ trợ',
    status: 'OPEN',
    createdAt,
    userId: 2,
    userLogin: 'user',
  });
  await page.goto('/admin/support-tickets');
  await expect(page.getByRole('heading', { name: 'Quản lý yêu cầu hỗ trợ' })).toBeVisible();
  const filteredRequest = page.waitForRequest(
    request => request.url().includes('/api/admin/support-tickets?') && request.url().includes('status=OPEN'),
  );
  await page.getByRole('combobox', { name: 'Trạng thái' }).selectOption('OPEN');
  await filteredRequest;
  await page.getByRole('link', { name: 'Cần hỗ trợ' }).click();
  await expect(page).toHaveURL(/\/admin\/support-tickets\/42$/);
  await page.getByRole('button', { name: 'Đánh dấu đã giải quyết' }).click();
  await expect(page.locator('.ticket-detail-heading .ticket-status')).toHaveText('Đã giải quyết');
  await page.getByRole('button', { name: 'Mở lại yêu cầu' }).click();
  await expect(page.locator('.ticket-detail-heading .ticket-status')).toHaveText('Đang xử lý');
});

test('khách chưa đăng nhập được chuyển tới trang đăng nhập', async ({ page }) => {
  await mockApi(page, null);
  await page.goto('/support-tickets');
  await expect(page).toHaveURL(/\/login$/);
});
