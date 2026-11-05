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

const payoutRequestPageUrl = '/payout-request';
// const payoutRequestSample = {"amount":16886.4,"bankCode":"yum","bankAccount":"reluctantly ack webbed","accountHolderName":"bestride","updatedAt":"2026-10-12T10:04:49.904Z"};

const payoutRequestApiUrl = '/api/payout-requests';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === payoutRequestApiUrl && response.request().method() === 'GET');

test.describe('PayoutRequest e2e test', () => {
  let payoutRequest;
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
        body: {"pendingAmount":24426.22,"availableAmount":12439.84,"rowVersion":23793},
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
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (payoutRequest) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payout-requests/${payoutRequest.id}`,
      });
      payoutRequest = undefined;
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

  test('PayoutRequests menu should load PayoutRequests page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('payout-request');
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
    await expect(jhi.entity.getEntityHeading('PayoutRequest')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
  });

  test.describe('PayoutRequest page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(payoutRequestPageUrl);
      await expect(jhi.entity.getEntityHeading('PayoutRequest')).not.toContainText('lumiApp.payoutRequest.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(payoutRequestPageUrl);
        await response;
      });

      test('should load create PayoutRequest page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${payoutRequestPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('PayoutRequest')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        payoutRequest = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/payout-requests',
            body: {
              ...payoutRequestSample,
              wallet: instructorWallet,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === payoutRequestApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/payout-requests?page=0&size=20>; rel="last",<http://localhost/api/payout-requests?page=0&size=20>; rel="first"',
              },
              json: [payoutRequest],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(payoutRequestPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(payoutRequestPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing PayoutRequest to exercise');
      });

      test('detail button click should load details PayoutRequest page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('payoutRequest')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
      });

      test('edit button click should load edit PayoutRequest page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('PayoutRequest')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
      });

      test('edit button click should load edit PayoutRequest page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('PayoutRequest')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of PayoutRequest', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${payoutRequestApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('payoutRequest')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);

        payoutRequest = undefined;
      });
    });
  });

  test.describe('new PayoutRequest page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(payoutRequestPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('PayoutRequest')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of PayoutRequest', async ({ page }) => {
      await page.locator(`[data-cy="amount"]`).fill('717.72');
      await expect(page.locator(`[data-cy="amount"]`)).toHaveValue('717.72');

      await page.locator(`[data-cy="bankCode"]`).fill('awesome drug');
      await expect(page.locator(`[data-cy="bankCode"]`)).toHaveValue('awesome drug');

      await page.locator(`[data-cy="bankAccount"]`).fill('makeover destock');
      await expect(page.locator(`[data-cy="bankAccount"]`)).toHaveValue('makeover destock');

      await page.locator(`[data-cy="status"]`).fill('clinch boo proselyti');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('clinch boo proselyti');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-11T23:47');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-11T23:47');

      await page.locator(`[data-cy="accountHolderName"]`).fill('barring dismal brr');
      await expect(page.locator(`[data-cy="accountHolderName"]`)).toHaveValue('barring dismal brr');

      await page.locator(`[data-cy="processedAt"]`).fill('2026-10-11T23:49');
      await page.locator(`[data-cy="processedAt"]`).blur();
      await expect(page.locator(`[data-cy="processedAt"]`)).toHaveValue('2026-10-11T23:49');

      await page.locator(`[data-cy="rejectionReason"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="rejectionReason"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="transferReference"]`).fill('unlike');
      await expect(page.locator(`[data-cy="transferReference"]`)).toHaveValue('unlike');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-10-12T04:08');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-10-12T04:08');

      await page.locator(`[data-cy="wallet"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === payoutRequestApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      payoutRequest = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === payoutRequestPageUrl);
    });
  });
});
