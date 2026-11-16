import {
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityTableSelector,
} from '../../support/entity';
import { expect, test } from '../../support/fixtures';

const pageUrl = '/admin/user-management';
const apiUrl = '/api/admin/users';
const sampleUser = {
  id: 9001,
  login: 'test-managed-student',
  firstName: 'Test',
  lastName: 'Student',
  email: 'test-managed-student@example.com',
  activated: true,
  langKey: 'vi',
  authorities: ['ROLE_USER', 'ROLE_STUDENT'],
};

const usersRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === apiUrl && response.request().method() === 'GET');

test.describe('User management', () => {
  test.beforeEach(async ({ jhi }) => {
    const { adminUsername, adminPassword } = jhi.credentials();
    await jhi.login(adminUsername, adminPassword);
  });

  test('opens from the admin menu', async ({ page, jhi }) => {
    await page.goto('/');
    const response = usersRequest(page);
    await jhi.navbar.clickOnAdminMenuItem('admin/user-management');
    expect((await response).status()).toBe(200);
    await expect(jhi.entity.getEntityHeading('UserManagement')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === pageUrl);
  });

  test('shows a user and opens the detail page', async ({ page, jhi }) => {
    await page.route(
      url => url.pathname === apiUrl,
      route => route.fulfill({ status: 200, headers: { 'x-total-count': '1' }, json: [sampleUser] }),
    );
    await page.route(
      url => url.pathname === `${apiUrl}/${sampleUser.login}`,
      route => route.fulfill({ status: 200, json: sampleUser }),
    );

    const response = usersRequest(page);
    await page.goto(pageUrl);
    await response;
    await expect(page.locator(entityTableSelector)).toHaveCount(1);
    await page.locator(entityDetailsButtonSelector).click();
    await expect(jhi.entity.getEntityDetailsHeading('userManagement')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === `${pageUrl}/${sampleUser.login}`);

    await page.locator(entityDetailsBackButtonSelector).click();
    await expect(page).toHaveURL(url => url.pathname === pageUrl);
  });

  test('opens and closes the create page', async ({ page, jhi }) => {
    const response = usersRequest(page);
    await page.goto(pageUrl);
    await response;
    await page.locator(entityCreateButtonSelector).click();
    await expect(page).toHaveURL(url => url.pathname === `${pageUrl}/new`);
    await expect(jhi.entity.getEntityCreateUpdateHeading('UserManagement')).toBeVisible();
    await page.locator(entityCreateCancelButtonSelector).click();
    await expect(page).toHaveURL(url => url.pathname === pageUrl);
  });

  test('submits a new account without changing the database', async ({ page }) => {
    await page.route(
      url => url.pathname === apiUrl,
      route =>
        route.request().method() === 'POST'
          ? route.fulfill({ status: 201, json: sampleUser })
          : route.fulfill({ status: 200, headers: { 'x-total-count': '1' }, json: [sampleUser] }),
    );

    await page.goto(`${pageUrl}/new`);
    await page.locator('[data-cy="login"]').fill(sampleUser.login);
    await page.locator('[data-cy="email"]').fill(sampleUser.email);
    await page.locator('[data-cy="businessRole"]').selectOption('ROLE_STUDENT');

    const postResponse = page.waitForResponse(
      response => new URL(response.url()).pathname === apiUrl && response.request().method() === 'POST',
    );
    await page.locator(entityCreateSaveButtonSelector).click();
    expect((await postResponse).status()).toBe(201);
    await expect(page).toHaveURL(url => url.pathname === pageUrl);
  });
});
