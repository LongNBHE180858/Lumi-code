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

const paymentPageUrl = '/payment';
// const paymentSample = {"gateway":"afterwards minion","amount":27479.02};

const paymentApiUrl = '/api/payments';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === paymentApiUrl && response.request().method() === 'GET');

test.describe('Payment e2e test', () => {
  let payment;
  // let courseOrder;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    courseOrder = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/course-orders',
        body: {"originalPrice":11447.32,"discountAmount":31460.98,"finalPrice":21765.83,"revenueSplitRate":"worst last","status":"CANCELLED","idempotencyKey":"knowledgeably","rowVersion":10013,"createdAt":"2026-10-12T08:41:08.049Z"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/course-orders',
      route => route.fulfill({ status: 200, json: [courseOrder].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (payment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payments/${payment.id}`,
      });
      payment = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (courseOrder) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-orders/${courseOrder.id}`,
      });
      courseOrder = undefined;
    }
  });
   */

  test('Payments menu should load Payments page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('payment');
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
    await expect(jhi.entity.getEntityHeading('Payment')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
  });

  test.describe('Payment page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(paymentPageUrl);
      await expect(jhi.entity.getEntityHeading('Payment')).not.toContainText('lumiApp.payment.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(paymentPageUrl);
        await response;
      });

      test('should load create Payment page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${paymentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Payment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        payment = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/payments',
            body: {
              ...paymentSample,
              order: courseOrder,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === paymentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/payments?page=0&size=20>; rel="last",<http://localhost/api/payments?page=0&size=20>; rel="first"',
              },
              json: [payment],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(paymentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(paymentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Payment to exercise');
      });

      test('detail button click should load details Payment page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('payment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
      });

      test('edit button click should load edit Payment page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Payment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
      });

      test('edit button click should load edit Payment page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Payment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Payment', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${paymentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('payment')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);

        payment = undefined;
      });
    });
  });

  test.describe('new Payment page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(paymentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Payment')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Payment', async ({ page }) => {
      await page.locator(`[data-cy="gateway"]`).fill('gallery bah beside');
      await expect(page.locator(`[data-cy="gateway"]`)).toHaveValue('gallery bah beside');

      await page.locator(`[data-cy="transactionNo"]`).fill('ew offset gadzooks');
      await expect(page.locator(`[data-cy="transactionNo"]`)).toHaveValue('ew offset gadzooks');

      await page.locator(`[data-cy="amount"]`).fill('3062.66');
      await expect(page.locator(`[data-cy="amount"]`)).toHaveValue('3062.66');

      await page.locator(`[data-cy="status"]`).fill('save');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('save');

      await page.locator(`[data-cy="paidAt"]`).fill('2026-10-12T14:47');
      await page.locator(`[data-cy="paidAt"]`).blur();
      await expect(page.locator(`[data-cy="paidAt"]`)).toHaveValue('2026-10-12T14:47');

      await page.locator(`[data-cy="order"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === paymentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      payment = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === paymentPageUrl);
    });
  });
});
