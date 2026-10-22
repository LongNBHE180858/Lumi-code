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

const couponPageUrl = '/coupon';
const couponSample = { code: 'these hypothesize beard' };

const couponApiUrl = '/api/coupons';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === couponApiUrl && response.request().method() === 'GET');

test.describe('Coupon e2e test', () => {
  let coupon;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (coupon) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/coupons/${coupon.id}`,
      });
      coupon = undefined;
    }
  });

  test('Coupons menu should load Coupons page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('coupon');
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
    await expect(jhi.entity.getEntityHeading('Coupon')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
  });

  test.describe('Coupon page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(couponPageUrl);
      await expect(jhi.entity.getEntityHeading('Coupon')).not.toContainText('lumiApp.coupon.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(couponPageUrl);
        await response;
      });

      test('should load create Coupon page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${couponPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Coupon')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        coupon = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/coupons',
            body: couponSample,
          })
        ).json();

        await page.route(
          url => url.pathname === couponApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/coupons?page=0&size=20>; rel="last",<http://localhost/api/coupons?page=0&size=20>; rel="first"',
              },
              json: [coupon],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(couponPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Coupon page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('coupon')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
      });

      test('edit button click should load edit Coupon page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Coupon')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
      });

      test('edit button click should load edit Coupon page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Coupon')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
      });

      test('last delete button click should delete instance of Coupon', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${couponApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('coupon')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === couponPageUrl);

        coupon = undefined;
      });
    });
  });

  test.describe('new Coupon page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(couponPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Coupon')).toBeVisible();
    });

    test('should create an instance of Coupon', async ({ page }) => {
      await page.locator(`[data-cy="code"]`).fill('dishearten so');
      await expect(page.locator(`[data-cy="code"]`)).toHaveValue('dishearten so');

      await page.locator(`[data-cy="discountPercent"]`).fill('26');
      await expect(page.locator(`[data-cy="discountPercent"]`)).toHaveValue('26');

      await page.locator(`[data-cy="maxUses"]`).fill('19441');
      await expect(page.locator(`[data-cy="maxUses"]`)).toHaveValue('19441');

      await page.locator(`[data-cy="usedCount"]`).fill('4513');
      await expect(page.locator(`[data-cy="usedCount"]`)).toHaveValue('4513');

      await page.locator(`[data-cy="validTo"]`).fill('2026-10-12T10:12');
      await page.locator(`[data-cy="validTo"]`).blur();
      await expect(page.locator(`[data-cy="validTo"]`)).toHaveValue('2026-10-12T10:12');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === couponApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      coupon = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === couponPageUrl);
    });
  });
});
