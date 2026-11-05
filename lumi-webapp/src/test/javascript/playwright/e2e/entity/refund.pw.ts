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

const refundPageUrl = '/refund';
// const refundSample = {"amount":22278.13};

const refundApiUrl = '/api/refunds';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === refundApiUrl && response.request().method() === 'GET');

test.describe('Refund e2e test', () => {
  let refund;
  // let courseOrder;
  // let payment;

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
        body: {"originalPrice":15733.94,"discountAmount":17354.33,"finalPrice":13600.47,"revenueSplitRate":"courageous","status":"PAID","idempotencyKey":"calmly","rowVersion":23064,"createdAt":"2026-10-12T13:42:24.995Z","instructorSharePercent":8.4,"instructorAmount":13883.7,"platformAmount":23307.39,"orderType":"supposing where","expiresAt":"2026-10-12T22:05:57.933Z","paidAt":"2026-10-12T00:12:54.896Z","accessDurationValue":19310,"accessDurationUnit":"which wasa","entitlementStartsAt":"2026-10-11T23:25:58.841Z","entitlementExpiresAt":"2026-10-12T20:21:48.389Z","entitlementStatus":"ha out if","planSnapshot":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","updatedAt":"2026-10-12T12:03:56.135Z","pricingSnapshot":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
      })
    ).json();
    // create an instance at the required relationship entity:
    payment = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/payments',
        body: {"gateway":"furthermore yum","transactionNo":"marimba whose","amount":28397.66,"status":"confirm","paidAt":"2026-10-12T19:16:13.853Z","paymentReference":"hearten replacement","providerEventId":"behest vice officially","receivedAt":"2026-10-12T08:15:48.954Z"},
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
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/payments',
      route => route.fulfill({ status: 200, json: [payment].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (refund) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/refunds/${refund.id}`,
      });
      refund = undefined;
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
    if (payment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payments/${payment.id}`,
      });
      payment = undefined;
    }
  });
   */

  test('Refunds menu should load Refunds page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('refund');
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
    await expect(jhi.entity.getEntityHeading('Refund')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
  });

  test.describe('Refund page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(refundPageUrl);
      await expect(jhi.entity.getEntityHeading('Refund')).not.toContainText('lumiApp.refund.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(refundPageUrl);
        await response;
      });

      test('should load create Refund page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${refundPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Refund')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        refund = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/refunds',
            body: {
              ...refundSample,
              order: courseOrder,
              payment: payment,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === refundApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/refunds?page=0&size=20>; rel="last",<http://localhost/api/refunds?page=0&size=20>; rel="first"',
              },
              json: [refund],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(refundPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(refundPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Refund to exercise');
      });

      test('detail button click should load details Refund page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('refund')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
      });

      test('edit button click should load edit Refund page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Refund')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
      });

      test('edit button click should load edit Refund page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Refund')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Refund', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${refundApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('refund')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === refundPageUrl);

        refund = undefined;
      });
    });
  });

  test.describe('new Refund page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(refundPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Refund')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Refund', async ({ page }) => {
      await page.locator(`[data-cy="amount"]`).fill('15505.96');
      await expect(page.locator(`[data-cy="amount"]`)).toHaveValue('15505.96');

      await page.locator(`[data-cy="reason"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="reason"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="status"]`).fill('juicy even geez');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('juicy even geez');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T12:10');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T12:10');

      await page.locator(`[data-cy="processedAt"]`).fill('2026-10-12T08:03');
      await page.locator(`[data-cy="processedAt"]`).blur();
      await expect(page.locator(`[data-cy="processedAt"]`)).toHaveValue('2026-10-12T08:03');

      await page.locator(`[data-cy="reviewNote"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="reviewNote"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="refundReference"]`).fill('er');
      await expect(page.locator(`[data-cy="refundReference"]`)).toHaveValue('er');

      await page.locator(`[data-cy="accessRevokedAt"]`).fill('2026-10-12T22:17');
      await page.locator(`[data-cy="accessRevokedAt"]`).blur();
      await expect(page.locator(`[data-cy="accessRevokedAt"]`)).toHaveValue('2026-10-12T22:17');

      await page.locator(`[data-cy="order"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="payment"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === refundApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      refund = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === refundPageUrl);
    });
  });
});
