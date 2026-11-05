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

const walletTransactionPageUrl = '/wallet-transaction';
// const walletTransactionSample = {"type":"SALE","amount":3675.03,"status":"growing","balanceBucket":"meanwhile marvelous"};

const walletTransactionApiUrl = '/api/wallet-transactions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === walletTransactionApiUrl && response.request().method() === 'GET');

test.describe('WalletTransaction e2e test', () => {
  let walletTransaction;
  // let instructorWallet;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    instructorWallet = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/instructor-wallets',
        body: {"pendingAmount":12517.61,"availableAmount":20367.66,"rowVersion":5165},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/instructor-wallets',
      route => route.fulfill({ status: 200, json: [instructorWallet].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/course-orders',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/payout-requests',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/refunds',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (walletTransaction) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/wallet-transactions/${walletTransaction.id}`,
      });
      walletTransaction = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (instructorWallet) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instructor-wallets/${instructorWallet.id}`,
      });
      instructorWallet = undefined;
    }
  });
   */

  test('WalletTransactions menu should load WalletTransactions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('wallet-transaction');
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
    await expect(jhi.entity.getEntityHeading('WalletTransaction')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
  });

  test.describe('WalletTransaction page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(walletTransactionPageUrl);
      await expect(jhi.entity.getEntityHeading('WalletTransaction')).not.toContainText('lumiApp.walletTransaction.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(walletTransactionPageUrl);
        await response;
      });

      test('should load create WalletTransaction page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${walletTransactionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('WalletTransaction')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        walletTransaction = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/wallet-transactions',
            body: {
              ...walletTransactionSample,
              wallet: instructorWallet,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === walletTransactionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/wallet-transactions?page=0&size=20>; rel="last",<http://localhost/api/wallet-transactions?page=0&size=20>; rel="first"',
              },
              json: [walletTransaction],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(walletTransactionPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(walletTransactionPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing WalletTransaction to exercise');
      });

      test('detail button click should load details WalletTransaction page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('walletTransaction')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
      });

      test('edit button click should load edit WalletTransaction page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('WalletTransaction')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
      });

      test('edit button click should load edit WalletTransaction page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('WalletTransaction')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of WalletTransaction', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${walletTransactionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('walletTransaction')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);

        walletTransaction = undefined;
      });
    });
  });

  test.describe('new WalletTransaction page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(walletTransactionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('WalletTransaction')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of WalletTransaction', async ({ page }) => {
      await page.locator(`[data-cy="type"]`).selectOption('REFUND');

      await page.locator(`[data-cy="amount"]`).fill('8955.17');
      await expect(page.locator(`[data-cy="amount"]`)).toHaveValue('8955.17');

      await page.locator(`[data-cy="status"]`).fill('gee black');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('gee black');

      await page.locator(`[data-cy="idempotencyKey"]`).fill('squid able');
      await expect(page.locator(`[data-cy="idempotencyKey"]`)).toHaveValue('squid able');

      await page.locator(`[data-cy="occurredAt"]`).fill('2026-10-11T23:43');
      await page.locator(`[data-cy="occurredAt"]`).blur();
      await expect(page.locator(`[data-cy="occurredAt"]`)).toHaveValue('2026-10-11T23:43');

      await page.locator(`[data-cy="balanceBucket"]`).fill('poorly accessorise t');
      await expect(page.locator(`[data-cy="balanceBucket"]`)).toHaveValue('poorly accessorise t');

      await page.locator(`[data-cy="wallet"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === walletTransactionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      walletTransaction = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === walletTransactionPageUrl);
    });
  });
});
