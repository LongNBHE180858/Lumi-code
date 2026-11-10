import { expect, test, type Page } from '@playwright/test';

const account = {
  id: 42,
  login: 'instructor',
  firstName: 'An',
  lastName: 'Nguyen',
  email: 'instructor@example.com',
  activated: true,
  authorities: ['ROLE_USER', 'ROLE_INSTRUCTOR'],
};

const mockAccount = async (page: Page, verificationStatus: string) => {
  await page.route('**/api/account', route => route.fulfill({ status: 200, json: account }));
  await page.route('**/management/info', route =>
    route.fulfill({ status: 200, json: { activeProfiles: ['dev'], 'display-ribbon-on-profiles': false } }),
  );
  await page.route('**/api/profile', route =>
    route.fulfill({
      status: 200,
      json: {
        code: 1000,
        status: 200,
        data: {
          ...account,
          fullName: 'An Nguyen',
          imageUrl: null,
          bio: null,
          phoneNumber: null,
          schoolName: null,
          gradeLevel: null,
          verificationStatus,
        },
      },
    }),
  );
};

for (const [status, label] of [
  ['PENDING', 'Chờ duyệt hồ sơ'],
  ['REJECTED', 'Hồ sơ bị từ chối'],
] as const) {
  test(`${status} instructor cannot open instructor routes`, async ({ page }) => {
    await mockAccount(page, status);
    await page.goto('/instructor/courses');
    await expect(page).toHaveURL(/\/profile$/, { timeout: 15000 });
    await expect(page.getByText(label)).toBeVisible();
    await page.locator('#account-menu .dropdown-toggle').click();
    await expect(page.locator('[data-cy="instructorCourses"]')).toHaveCount(0);
    await expect(page.locator('[data-cy="instructorApplicationStatus"]')).toBeVisible();
  });
}

test('approved instructor can open instructor routes', async ({ page }) => {
  await mockAccount(page, 'APPROVED');
  await page.goto('/instructor/courses');
  await expect(page.getByRole('heading', { name: 'Course List' })).toBeVisible();
  await page.locator('#account-menu .dropdown-toggle').click();
  await expect(page.locator('[data-cy="instructorCourses"]')).toBeVisible();
});
