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

const contentReportPageUrl = '/content-report';
// const contentReportSample = {"targetType":"snappy oxidise what","targetId":28129,"reason":"fooey woot phooey"};

const contentReportApiUrl = '/api/content-reports';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === contentReportApiUrl && response.request().method() === 'GET');

test.describe('ContentReport e2e test', () => {
  let contentReport;
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
        body: {"login":"HoDiep25","firstName":"Xuân Linh","lastName":"Lâm","email":"YenNhi49@hotmail.com","langKey":"folklore","imageUrl":"silver"},
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
    if (contentReport) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/content-reports/${contentReport.id}`,
      });
      contentReport = undefined;
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

  test('ContentReports menu should load ContentReports page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('content-report');
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
    await expect(jhi.entity.getEntityHeading('ContentReport')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
  });

  test.describe('ContentReport page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(contentReportPageUrl);
      await expect(jhi.entity.getEntityHeading('ContentReport')).not.toContainText('lumiApp.contentReport.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(contentReportPageUrl);
        await response;
      });

      test('should load create ContentReport page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${contentReportPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ContentReport')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        contentReport = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/content-reports',
            body: {
              ...contentReportSample,
              reporter: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === contentReportApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/content-reports?page=0&size=20>; rel="last",<http://localhost/api/content-reports?page=0&size=20>; rel="first"',
              },
              json: [contentReport],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(contentReportPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(contentReportPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ContentReport to exercise');
      });

      test('detail button click should load details ContentReport page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('contentReport')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
      });

      test('edit button click should load edit ContentReport page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ContentReport')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
      });

      test('edit button click should load edit ContentReport page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ContentReport')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ContentReport', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${contentReportApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('contentReport')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);

        contentReport = undefined;
      });
    });
  });

  test.describe('new ContentReport page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(contentReportPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ContentReport')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ContentReport', async ({ page }) => {
      await page.locator(`[data-cy="targetType"]`).fill('overcoat');
      await expect(page.locator(`[data-cy="targetType"]`)).toHaveValue('overcoat');

      await page.locator(`[data-cy="targetId"]`).fill('30805');
      await expect(page.locator(`[data-cy="targetId"]`)).toHaveValue('30805');

      await page.locator(`[data-cy="reason"]`).fill('canter into when');
      await expect(page.locator(`[data-cy="reason"]`)).toHaveValue('canter into when');

      await page.locator(`[data-cy="status"]`).fill('vet once loyally');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('vet once loyally');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T08:14');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T08:14');

      await page.locator(`[data-cy="reporter"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === contentReportApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      contentReport = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === contentReportPageUrl);
    });
  });
});
