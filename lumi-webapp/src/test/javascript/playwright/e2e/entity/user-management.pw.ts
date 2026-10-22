import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';
import { expect, test } from '../../support/fixtures';

const userManagementPageUrl = '/admin/user-management';
const userManagementSample = { login: 'KimLong_Phan', email: 'ThuVong.Hoang@gmail.com' };

const userManagementApiUrl = '/api/admin/users';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === userManagementApiUrl && response.request().method() === 'GET');

test.describe('UserManagement e2e test', () => {
  let userManagement;

  test.beforeEach(async ({ jhi }) => {
    const { adminUsername: username, adminPassword: password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (userManagement) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/admin/users/${userManagement.login}`,
      });
      userManagement = undefined;
    }
  });

  test('UserManagements menu should load UserManagements page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnAdminMenuItem('admin/user-management');
    // Chrome drops the response body once the page navigates, unlike the cypress proxy, so the table
    // assertion is only made when the payload is still readable.
    const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
    if (Array.isArray(body)) {
      if (body.length === 0) {
        await expect(page.locator(entityTableSelector)).toHaveCount(0);
      } else {
        // The clients render one entityTable element per row, so match the first one.
        await expect(page.locator(entityTableSelector).first()).toBeVisible();
      }
    }
    await expect(jhi.entity.getEntityHeading('UserManagement')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
  });

  test.describe('UserManagement page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(userManagementPageUrl);
      await expect(jhi.entity.getEntityHeading('UserManagement')).not.toContainText('userManagement.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(userManagementPageUrl);
        await response;
      });

      test('should load create UserManagement page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${userManagementPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('UserManagement')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        userManagement = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/admin/users',
            body: userManagementSample,
          })
        ).json();

        await page.route(
          url => url.pathname === userManagementApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/admin/users?page=0&size=20>; rel="last",<http://localhost/api/admin/users?page=0&size=20>; rel="first"',
              },
              json: [userManagement],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(userManagementPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details UserManagement page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('userManagement')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
      });

      test('edit button click should load edit UserManagement page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('UserManagement')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
      });

      test('edit button click should load edit UserManagement page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('UserManagement')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
      });

      test('last delete button click should delete instance of UserManagement', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${userManagementApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('userManagement')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);

        userManagement = undefined;
      });
    });
  });

  test.describe('new UserManagement page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(userManagementPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('UserManagement')).toBeVisible();
    });

    test('should create an instance of UserManagement', async ({ page }) => {
      await page.locator(`[data-cy="login"]`).fill('ThanhTuyen.7ko80');
      await expect(page.locator(`[data-cy="login"]`)).toHaveValue('ThanhTuyen.7ko80');

      await page.locator(`[data-cy="firstName"]`).fill('Thanh Thiên');
      await expect(page.locator(`[data-cy="firstName"]`)).toHaveValue('Thanh Thiên');

      await page.locator(`[data-cy="lastName"]`).fill('Vũ');
      await expect(page.locator(`[data-cy="lastName"]`)).toHaveValue('Vũ');

      await page.locator(`[data-cy="email"]`).fill('BichHanh.Nguyen@hotmail.com');
      await expect(page.locator(`[data-cy="email"]`)).toHaveValue('BichHanh.Nguyen@hotmail.com');

      await expect(page.locator(`[data-cy="activated"]`)).toBeChecked();
      await page.locator(`[data-cy="activated"]`).click();
      await expect(page.locator(`[data-cy="activated"]`)).not.toBeChecked();

      await page.locator(`[data-cy="langKey"]`).selectOption('vi');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === userManagementApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      userManagement = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === userManagementPageUrl);
    });
  });
});
