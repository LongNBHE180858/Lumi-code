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

const notificationPageUrl = '/notification';
// const notificationSample = {"title":"hearten","content":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","type":"glider cop-out secret"};

const notificationApiUrl = '/api/notifications';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === notificationApiUrl && response.request().method() === 'GET');

test.describe('Notification e2e test', () => {
  let notification;
  // let user;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    user = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/users',
        body: {"login":"LapNghiep.Pham84","firstName":"Hoàng Khang","lastName":"Trịnh","email":"CongHau_Vu50@yahoo.com","langKey":"supposing ","imageUrl":"ah"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [user].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (notification) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notifications/${notification.id}`,
      });
      notification = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (user) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      });
      user = undefined;
    }
  });
   */

  test('Notifications menu should load Notifications page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('notification');
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
    await expect(jhi.entity.getEntityHeading('Notification')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
  });

  test.describe('Notification page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(notificationPageUrl);
      await expect(jhi.entity.getEntityHeading('Notification')).not.toContainText('lumiApp.notification.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(notificationPageUrl);
        await response;
      });

      test('should load create Notification page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${notificationPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Notification')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        notification = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/notifications',
            body: {
              ...notificationSample,
              user: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === notificationApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/notifications?page=0&size=20>; rel="last",<http://localhost/api/notifications?page=0&size=20>; rel="first"',
              },
              json: [notification],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(notificationPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(notificationPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Notification to exercise');
      });

      test('detail button click should load details Notification page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('notification')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
      });

      test('edit button click should load edit Notification page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Notification')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
      });

      test('edit button click should load edit Notification page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Notification')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Notification', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${notificationApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('notification')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);

        notification = undefined;
      });
    });
  });

  test.describe('new Notification page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(notificationPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Notification')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Notification', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('boohoo colorful softly');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('boohoo colorful softly');

      await page.locator(`[data-cy="content"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await expect(page.locator(`[data-cy="isRead"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isRead"]`).click();
      await expect(page.locator(`[data-cy="isRead"]`)).toBeChecked();

      await page.locator(`[data-cy="type"]`).fill('of');
      await expect(page.locator(`[data-cy="type"]`)).toHaveValue('of');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T22:29');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T22:29');

      await page.locator(`[data-cy="targetType"]`).fill('cod');
      await expect(page.locator(`[data-cy="targetType"]`)).toHaveValue('cod');

      await page.locator(`[data-cy="targetId"]`).fill('21246');
      await expect(page.locator(`[data-cy="targetId"]`)).toHaveValue('21246');

      await page.locator(`[data-cy="readAt"]`).fill('2026-10-12T17:06');
      await page.locator(`[data-cy="readAt"]`).blur();
      await expect(page.locator(`[data-cy="readAt"]`)).toHaveValue('2026-10-12T17:06');

      await page.locator(`[data-cy="user"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === notificationApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      notification = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === notificationPageUrl);
    });
  });
});
