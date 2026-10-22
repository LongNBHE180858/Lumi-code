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

const authSessionPageUrl = '/auth-session';
// const authSessionSample = {"expiresAt":"2026-10-12T13:20:05.784Z"};

const authSessionApiUrl = '/api/auth-sessions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === authSessionApiUrl && response.request().method() === 'GET');

test.describe('AuthSession e2e test', () => {
  let authSession;
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
        body: {"login":"HuongNhi.Vuong25","firstName":"Ngọc Cầm","lastName":"Phan","email":"NhuHao_Lam15@hotmail.com","langKey":"empty","imageUrl":"snow meh oddball"},
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
    if (authSession) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/auth-sessions/${authSession.id}`,
      });
      authSession = undefined;
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

  test('AuthSessions menu should load AuthSessions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('auth-session');
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
    await expect(jhi.entity.getEntityHeading('AuthSession')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
  });

  test.describe('AuthSession page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(authSessionPageUrl);
      await expect(jhi.entity.getEntityHeading('AuthSession')).not.toContainText('lumiApp.authSession.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(authSessionPageUrl);
        await response;
      });

      test('should load create AuthSession page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${authSessionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuthSession')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        authSession = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/auth-sessions',
            body: {
              ...authSessionSample,
              user: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === authSessionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/auth-sessions?page=0&size=20>; rel="last",<http://localhost/api/auth-sessions?page=0&size=20>; rel="first"',
              },
              json: [authSession],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(authSessionPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(authSessionPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing AuthSession to exercise');
      });

      test('detail button click should load details AuthSession page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('authSession')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
      });

      test('edit button click should load edit AuthSession page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuthSession')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
      });

      test('edit button click should load edit AuthSession page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuthSession')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of AuthSession', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${authSessionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('authSession')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);

        authSession = undefined;
      });
    });
  });

  test.describe('new AuthSession page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(authSessionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AuthSession')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of AuthSession', async ({ page }) => {
      await page.locator(`[data-cy="deviceInfo"]`).fill('which');
      await expect(page.locator(`[data-cy="deviceInfo"]`)).toHaveValue('which');

      await page.locator(`[data-cy="refreshToken"]`).fill('hateful usefully');
      await expect(page.locator(`[data-cy="refreshToken"]`)).toHaveValue('hateful usefully');

      await page.locator(`[data-cy="expiresAt"]`).fill('2026-10-12T12:50');
      await page.locator(`[data-cy="expiresAt"]`).blur();
      await expect(page.locator(`[data-cy="expiresAt"]`)).toHaveValue('2026-10-12T12:50');

      await expect(page.locator(`[data-cy="isRevoked"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isRevoked"]`).click();
      await expect(page.locator(`[data-cy="isRevoked"]`)).toBeChecked();

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T07:41');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T07:41');

      await page.locator(`[data-cy="user"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === authSessionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      authSession = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === authSessionPageUrl);
    });
  });
});
