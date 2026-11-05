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

const courseReviewRequestPageUrl = '/course-review-request';
// const courseReviewRequestSample = {"courseVersion":23274,"status":"after","rightsConfirmedAt":"2026-10-18T05:14:30.077Z","submittedAt":"2026-10-18T03:36:03.196Z"};

const courseReviewRequestApiUrl = '/api/course-review-requests';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseReviewRequestApiUrl && response.request().method() === 'GET');

test.describe('CourseReviewRequest e2e test', () => {
  let courseReviewRequest;
  // let course;
  // let user;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    course = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/courses',
        body: {"title":"familiar thick","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","thumbnailUrl":"anneal","price":6768.55,"shortDescription":"expense","bookSeries":"pace sustenance","targetAudience":"ruddy","prerequisites":"frantically fuzzy","learningObjectives":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","introVideoUrl":"yuck ecliptic affect","validDurationDays":5299,"accessDurationValue":2261,"accessDurationUnit":"drab","version":671,"status":"HIDDEN","isDeleted":true,"createdAt":"2026-10-12T09:15:09.787Z","updatedAt":"2026-10-12T06:16:09.702Z","storageLimitBytes":21443},
      })
    ).json();
    // create an instance at the required relationship entity:
    user = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/users',
        body: {"login":"QuyVinh.Ha69","firstName":"Nguyên Nhân","lastName":"Đinh","email":"MinhTuyet88@gmail.com","langKey":"misappropr","imageUrl":"under"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/courses',
      route => route.fulfill({ status: 200, json: [course].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [user].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (courseReviewRequest) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-review-requests/${courseReviewRequest.id}`,
      });
      courseReviewRequest = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (course) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/courses/${course.id}`,
      });
      course = undefined;
    }
    if (user) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      });
      user = undefined;
    }
  });
   */

  test('CourseReviewRequests menu should load CourseReviewRequests page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course-review-request');
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
    await expect(jhi.entity.getEntityHeading('CourseReviewRequest')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
  });

  test.describe('CourseReviewRequest page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(courseReviewRequestPageUrl);
      await expect(jhi.entity.getEntityHeading('CourseReviewRequest')).not.toContainText('lumiApp.courseReviewRequest.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseReviewRequestPageUrl);
        await response;
      });

      test('should load create CourseReviewRequest page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${courseReviewRequestPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReviewRequest')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        courseReviewRequest = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/course-review-requests',
            body: {
              ...courseReviewRequestSample,
              course: course,
              submittedBy: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseReviewRequestApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/course-review-requests?page=0&size=20>; rel="last",<http://localhost/api/course-review-requests?page=0&size=20>; rel="first"',
              },
              json: [courseReviewRequest],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(courseReviewRequestPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseReviewRequestPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing CourseReviewRequest to exercise');
      });

      test('detail button click should load details CourseReviewRequest page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('courseReviewRequest')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
      });

      test('edit button click should load edit CourseReviewRequest page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReviewRequest')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
      });

      test('edit button click should load edit CourseReviewRequest page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReviewRequest')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of CourseReviewRequest', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${courseReviewRequestApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('courseReviewRequest')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);

        courseReviewRequest = undefined;
      });
    });
  });

  test.describe('new CourseReviewRequest page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(courseReviewRequestPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('CourseReviewRequest')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of CourseReviewRequest', async ({ page }) => {
      await page.locator(`[data-cy="courseVersion"]`).fill('27142');
      await expect(page.locator(`[data-cy="courseVersion"]`)).toHaveValue('27142');

      await page.locator(`[data-cy="status"]`).fill('courageously as');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('courageously as');

      await page.locator(`[data-cy="submissionNote"]`).fill('after');
      await expect(page.locator(`[data-cy="submissionNote"]`)).toHaveValue('after');

      await page.locator(`[data-cy="rightsConfirmedAt"]`).fill('2026-10-18T01:31');
      await page.locator(`[data-cy="rightsConfirmedAt"]`).blur();
      await expect(page.locator(`[data-cy="rightsConfirmedAt"]`)).toHaveValue('2026-10-18T01:31');

      await page.locator(`[data-cy="submittedAt"]`).fill('2026-10-18T17:17');
      await page.locator(`[data-cy="submittedAt"]`).blur();
      await expect(page.locator(`[data-cy="submittedAt"]`)).toHaveValue('2026-10-18T17:17');

      await page.locator(`[data-cy="reviewedAt"]`).fill('2026-10-18T20:16');
      await page.locator(`[data-cy="reviewedAt"]`).blur();
      await expect(page.locator(`[data-cy="reviewedAt"]`)).toHaveValue('2026-10-18T20:16');

      await page.locator(`[data-cy="reviewerFeedback"]`).fill('descent');
      await expect(page.locator(`[data-cy="reviewerFeedback"]`)).toHaveValue('descent');

      await page.locator(`[data-cy="course"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="submittedBy"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseReviewRequestApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      courseReviewRequest = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === courseReviewRequestPageUrl);
    });
  });
});
