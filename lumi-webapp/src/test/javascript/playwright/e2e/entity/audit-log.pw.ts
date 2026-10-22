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

const auditLogPageUrl = '/audit-log';
const auditLogSample = { action: 'now chops', targetEntity: 'woot', targetId: 'absent finally yowza' };

const auditLogApiUrl = '/api/audit-logs';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === auditLogApiUrl && response.request().method() === 'GET');

test.describe('AuditLog e2e test', () => {
  let auditLog;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (auditLog) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/audit-logs/${auditLog.id}`,
      });
      auditLog = undefined;
    }
  });

  test('AuditLogs menu should load AuditLogs page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('audit-log');
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
    await expect(jhi.entity.getEntityHeading('AuditLog')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
  });

  test.describe('AuditLog page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(auditLogPageUrl);
      await expect(jhi.entity.getEntityHeading('AuditLog')).not.toContainText('lumiApp.auditLog.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(auditLogPageUrl);
        await response;
      });

      test('should load create AuditLog page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${auditLogPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuditLog')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        auditLog = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/audit-logs',
            body: auditLogSample,
          })
        ).json();

        await page.route(
          url => url.pathname === auditLogApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/audit-logs?page=0&size=20>; rel="last",<http://localhost/api/audit-logs?page=0&size=20>; rel="first"',
              },
              json: [auditLog],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(auditLogPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details AuditLog page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('auditLog')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
      });

      test('edit button click should load edit AuditLog page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuditLog')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
      });

      test('edit button click should load edit AuditLog page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AuditLog')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
      });

      test('last delete button click should delete instance of AuditLog', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${auditLogApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('auditLog')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);

        auditLog = undefined;
      });
    });
  });

  test.describe('new AuditLog page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(auditLogPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AuditLog')).toBeVisible();
    });

    test('should create an instance of AuditLog', async ({ page }) => {
      await page.locator(`[data-cy="action"]`).fill('times gadzooks');
      await expect(page.locator(`[data-cy="action"]`)).toHaveValue('times gadzooks');

      await page.locator(`[data-cy="targetEntity"]`).fill('yet minion');
      await expect(page.locator(`[data-cy="targetEntity"]`)).toHaveValue('yet minion');

      await page.locator(`[data-cy="targetId"]`).fill('assured');
      await expect(page.locator(`[data-cy="targetId"]`)).toHaveValue('assured');

      await page.locator(`[data-cy="ipAddress"]`).fill('trash now harmful');
      await expect(page.locator(`[data-cy="ipAddress"]`)).toHaveValue('trash now harmful');

      await page.locator(`[data-cy="details"]`).fill('the satisfy');
      await expect(page.locator(`[data-cy="details"]`)).toHaveValue('the satisfy');

      await page.locator(`[data-cy="occurredAt"]`).fill('2026-10-12T22:47');
      await page.locator(`[data-cy="occurredAt"]`).blur();
      await expect(page.locator(`[data-cy="occurredAt"]`)).toHaveValue('2026-10-12T22:47');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === auditLogApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      auditLog = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === auditLogPageUrl);
    });
  });
});
