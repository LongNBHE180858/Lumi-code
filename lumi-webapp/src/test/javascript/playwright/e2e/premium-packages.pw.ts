import { expect, test, type Page } from '@playwright/test';

type PackageType = 'PARENT_PERSONAL' | 'PARENT_FAMILY' | 'INSTRUCTOR_PERSONAL' | 'INSTRUCTOR_CLASS';

type PremiumPackage = {
  id: number;
  name: string;
  packageType: PackageType | null;
  price: number;
  durationDays: number;
  storageQuotaMb: number;
  aiGenerationLimit: number;
  aiLimitPeriod: string;
  maxClasses: number | null;
  coveredPeople: number | null;
  revision: number;
  available: boolean;
  needsClassification: boolean;
};

const initialPackage: PremiumPackage = {
  id: 12,
  name: 'Gói cũ',
  packageType: null,
  price: 100000,
  durationDays: 30,
  storageQuotaMb: 512,
  aiGenerationLimit: 5,
  aiLimitPeriod: 'MONTH',
  maxClasses: 1,
  coveredPeople: null,
  revision: 1,
  available: false,
  needsClassification: true,
};

const mockApi = async (page: Page, initial: PremiumPackage[] = []) => {
  let packages = [...initial];
  let nextId = 100;
  const received: { method: string; body: Record<string, unknown> }[] = [];

  await page.route('**/management/info', route => route.fulfill({ json: { activeProfiles: ['dev'] } }));
  await page.route(
    url => url.pathname.startsWith('/api/'),
    route => {
      const request = route.request();
      const { pathname } = new URL(request.url());
      const method = request.method();
      const reply = (json: unknown, status = 200, headers: Record<string, string> = {}) => route.fulfill({ status, json, headers });

      if (pathname === '/api/account') {
        return reply({ id: 1, login: 'admin', activated: true, authorities: ['ROLE_ADMIN'], langKey: 'vi' });
      }
      if (pathname === '/api/admin/premium-packages' && method === 'GET') {
        const url = new URL(request.url());
        const pageNumber = Number(url.searchParams.get('page') ?? 0);
        const size = Number(url.searchParams.get('size') ?? 10);
        return reply(packages.slice(pageNumber * size, (pageNumber + 1) * size), 200, { 'x-total-count': String(packages.length) });
      }
      if (pathname === '/api/admin/premium-packages' && method === 'POST') {
        const body = request.postDataJSON() as Record<string, unknown>;
        received.push({ method, body });
        const type = body.packageType as PackageType;
        const created: PremiumPackage = {
          ...(body as Omit<PremiumPackage, 'id' | 'coveredPeople' | 'revision' | 'available' | 'needsClassification'>),
          id: nextId++,
          coveredPeople: type === 'PARENT_FAMILY' ? 4 : null,
          revision: 1,
          available: false,
          needsClassification: false,
        };
        packages = [created, ...packages];
        return reply(created, 201);
      }
      const match = pathname.match(/^\/api\/admin\/premium-packages\/(\d+)(\/availability)?$/);
      if (match) {
        const id = Number(match[1]);
        const current = packages.find(item => item.id === id);
        if (!current) return reply({}, 404);
        const body = request.postDataJSON() as Record<string, unknown>;
        received.push({ method, body });
        if (method === 'PUT') {
          const type = body.packageType as PackageType;
          const revised: PremiumPackage = {
            ...(body as Omit<PremiumPackage, 'id' | 'coveredPeople' | 'revision' | 'available' | 'needsClassification'>),
            id: nextId++,
            coveredPeople: type === 'PARENT_FAMILY' ? 4 : null,
            revision: current.revision + 1,
            available: current.available,
            needsClassification: false,
          };
          packages = [revised, ...packages.filter(item => item.id !== id)];
          return reply(revised);
        }
        if (method === 'PATCH' && match[2] === '/availability') {
          const updated = { ...current, available: body.available as boolean };
          packages = packages.map(item => (item.id === id ? updated : item));
          return reply(updated);
        }
      }
      return reply({}, 404);
    },
  );

  return received;
};

test('admin thêm gói gia đình, bật bán rồi tạo phiên bản giá mới', async ({ page }) => {
  const received = await mockApi(page);
  await page.goto('/admin/premium-packages');
  await expect(page.getByRole('heading', { name: 'Gói Premium' })).toBeVisible({ timeout: 30000 });
  await page.getByRole('button', { name: 'Thêm gói' }).click();
  await page.getByLabel('Tên gói').fill('Gia đình Plus');
  await page.getByLabel('Loại gói').selectOption('PARENT_FAMILY');
  await expect(page.getByText(/Tối đa 4 người/)).toBeVisible();
  await expect(page.getByLabel('Số lớp tối đa của giảng viên')).toHaveCount(0);
  await page.getByLabel('Giá (VNĐ)').fill('150000');
  await page.getByLabel('Thời hạn (ngày)').fill('30');
  await page.getByLabel('Dung lượng mỗi người (MB)').fill('1024');
  await page.getByLabel('Lượt tạo AI mỗi người').fill('10');
  await page.getByLabel('Kỳ tính lượt AI').selectOption('MONTH');
  await page.getByRole('button', { name: 'Lưu gói' }).click();

  await expect(page.getByText('Gia đình Plus')).toBeVisible();
  await expect(page.getByText('Ngừng bán', { exact: true })).toBeVisible();
  expect(received[0]).toMatchObject({
    method: 'POST',
    body: { packageType: 'PARENT_FAMILY', maxClasses: null, price: 150000, aiLimitPeriod: 'MONTH' },
  });

  await page.getByRole('button', { name: 'Bật bán' }).click();
  await expect(page.getByText('Đang bán')).toBeVisible();
  await page.getByRole('button', { name: 'Sửa Gia đình Plus' }).click();
  await expect(page.getByLabel('Loại gói')).toBeDisabled();
  await page.getByLabel('Giá (VNĐ)').fill('200000');
  await page.getByRole('button', { name: 'Lưu gói' }).click();
  await expect(page.getByText('200.000 ₫')).toBeVisible();
  await expect(page.getByText('2', { exact: true })).toBeVisible();
  expect(received.some(value => value.method === 'PUT' && value.body.price === 200000)).toBe(true);
});

test('admin phải phân loại gói cũ trước khi bật bán', async ({ page }) => {
  await mockApi(page, [initialPackage]);
  await page.goto('/admin/premium-packages');
  await expect(page.getByText('Chưa phân loại')).toBeVisible({ timeout: 30000 });
  await expect(page.getByRole('button', { name: 'Bật bán' })).toBeDisabled();
  await page.getByRole('button', { name: 'Sửa Gói cũ' }).click();
  await page.getByLabel('Loại gói').selectOption('INSTRUCTOR_CLASS');
  await expect(page.getByText(/Học sinh vào lớp sau lúc mua/)).toBeVisible();
  await page.getByLabel('Số lớp tối đa của giảng viên').fill('2');
  await page.getByRole('button', { name: 'Lưu gói' }).click();
  await expect(page.getByText('Giảng viên và một lớp')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Bật bán' })).toBeEnabled();
});
