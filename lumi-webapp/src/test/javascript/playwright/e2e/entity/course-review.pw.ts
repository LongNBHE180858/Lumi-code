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

const courseReviewPageUrl = '/course-review';
// const courseReviewSample = {};

const courseReviewApiUrl = '/api/course-reviews';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseReviewApiUrl && response.request().method() === 'GET');

test.describe('CourseReview e2e test', () => {
  let courseReview;
  // let user;
  // let course;

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
        body: {"login":"LanTruc_Pham","firstName":"Đức Chính","lastName":"Đinh","email":"QuangTuan.Phung@yahoo.com","langKey":"astride do","imageUrl":"along till serve"},
      })
    ).json();
    // create an instance at the required relationship entity:
    course = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/courses',
        body: {"title":"that meatloaf","description":"immediately","thumbnailUrl":"gosh murky","price":1867.36,"validDurationDays":29334,"status":"DRAFT","isDeleted":true,"createdAt":"2026-10-12T09:51:02.417Z","updatedAt":"2026-10-12T04:17:51.125Z"},
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
      route => route.fulfill({ status: 200, json: [course].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (courseReview) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-reviews/${courseReview.id}`,
      });
      courseReview = undefined;
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
    if (course) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/courses/${course.id}`,
      });
      course = undefined;
    }
  });
   */

  test('CourseReviews menu should load CourseReviews page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course-review');
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
    await expect(jhi.entity.getEntityHeading('CourseReview')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
  });

  test.describe('CourseReview page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(courseReviewPageUrl);
      await expect(jhi.entity.getEntityHeading('CourseReview')).not.toContainText('lumiApp.courseReview.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseReviewPageUrl);
        await response;
      });

      test('should load create CourseReview page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${courseReviewPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReview')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        courseReview = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/course-reviews',
            body: {
              ...courseReviewSample,
              student: user,
              course: course,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseReviewApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/course-reviews?page=0&size=20>; rel="last",<http://localhost/api/course-reviews?page=0&size=20>; rel="first"',
              },
              json: [courseReview],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(courseReviewPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseReviewPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing CourseReview to exercise');
      });

      test('detail button click should load details CourseReview page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('courseReview')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
      });

      test('edit button click should load edit CourseReview page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReview')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
      });

      test('edit button click should load edit CourseReview page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReview')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of CourseReview', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${courseReviewApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('courseReview')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);

        courseReview = undefined;
      });
    });
  });

  test.describe('new CourseReview page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(courseReviewPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReview')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of CourseReview', async ({ page }) => {
      await page.locator(`[data-cy="rating"]`).fill('4');
      await expect(page.locator(`[data-cy="rating"]`)).toHaveValue('4');

      await page.locator(`[data-cy="content"]`).fill('instead');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue('instead');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-11T23:44');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-11T23:44');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="course"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseReviewApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      courseReview = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === courseReviewPageUrl);
    });
  });
});
