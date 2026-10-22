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

const courseOrderPageUrl = '/course-order';
// const courseOrderSample = {"originalPrice":27484.4,"finalPrice":24828.92};

const courseOrderApiUrl = '/api/course-orders';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseOrderApiUrl && response.request().method() === 'GET');

test.describe('CourseOrder e2e test', () => {
  let courseOrder;
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
        body: {"login":"YenPhuong3","firstName":"Thục Đình","lastName":"Lý","email":"7kucKhai7@gmail.com","langKey":"plus quiet","imageUrl":"norm convection trench"},
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
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/courses',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/system-plans',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/coupons',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (courseOrder) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-orders/${courseOrder.id}`,
      });
      courseOrder = undefined;
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

  test('CourseOrders menu should load CourseOrders page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course-order');
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
    await expect(jhi.entity.getEntityHeading('CourseOrder')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
  });

  test.describe('CourseOrder page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(courseOrderPageUrl);
      await expect(jhi.entity.getEntityHeading('CourseOrder')).not.toContainText('lumiApp.courseOrder.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseOrderPageUrl);
        await response;
      });

      test('should load create CourseOrder page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${courseOrderPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseOrder')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        courseOrder = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/course-orders',
            body: {
              ...courseOrderSample,
              student: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseOrderApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/course-orders?page=0&size=20>; rel="last",<http://localhost/api/course-orders?page=0&size=20>; rel="first"',
              },
              json: [courseOrder],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(courseOrderPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseOrderPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing CourseOrder to exercise');
      });

      test('detail button click should load details CourseOrder page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('courseOrder')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
      });

      test('edit button click should load edit CourseOrder page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseOrder')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
      });

      test('edit button click should load edit CourseOrder page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseOrder')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of CourseOrder', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${courseOrderApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('courseOrder')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);

        courseOrder = undefined;
      });
    });
  });

  test.describe('new CourseOrder page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(courseOrderPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('CourseOrder')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of CourseOrder', async ({ page }) => {
      await page.locator(`[data-cy="originalPrice"]`).fill('15101.3');
      await expect(page.locator(`[data-cy="originalPrice"]`)).toHaveValue('15101.3');

      await page.locator(`[data-cy="discountAmount"]`).fill('32208.37');
      await expect(page.locator(`[data-cy="discountAmount"]`)).toHaveValue('32208.37');

      await page.locator(`[data-cy="finalPrice"]`).fill('6101.64');
      await expect(page.locator(`[data-cy="finalPrice"]`)).toHaveValue('6101.64');

      await page.locator(`[data-cy="revenueSplitRate"]`).fill('hurtful co');
      await expect(page.locator(`[data-cy="revenueSplitRate"]`)).toHaveValue('hurtful co');

      await page.locator(`[data-cy="status"]`).selectOption('PAID');

      await page.locator(`[data-cy="idempotencyKey"]`).fill('nor lest who');
      await expect(page.locator(`[data-cy="idempotencyKey"]`)).toHaveValue('nor lest who');

      await page.locator(`[data-cy="rowVersion"]`).fill('24192');
      await expect(page.locator(`[data-cy="rowVersion"]`)).toHaveValue('24192');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T06:20');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T06:20');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseOrderApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      courseOrder = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === courseOrderPageUrl);
    });
  });
});
