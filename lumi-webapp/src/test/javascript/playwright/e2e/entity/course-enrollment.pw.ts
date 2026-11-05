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

const courseEnrollmentPageUrl = '/course-enrollment';
// const courseEnrollmentSample = {"expiresAt":"2026-10-12T07:27:19.914Z","updatedAt":"2026-10-12T20:43:43.405Z"};

const courseEnrollmentApiUrl = '/api/course-enrollments';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseEnrollmentApiUrl && response.request().method() === 'GET');

test.describe('CourseEnrollment e2e test', () => {
  let courseEnrollment;
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
        body: {"title":"demob publicity","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","thumbnailUrl":"who duh","price":22389.89,"shortDescription":"meh psst strictly","bookSeries":"or","targetAudience":"sad victorious aha","prerequisites":"fat for","learningObjectives":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","introVideoUrl":"almighty cake independence","validDurationDays":24764,"accessDurationValue":25911,"accessDurationUnit":"briefly we","version":15913,"status":"BLOCKED","isDeleted":true,"createdAt":"2026-10-12T01:02:05.048Z","updatedAt":"2026-10-12T05:51:47.148Z","storageLimitBytes":27251},
      })
    ).json();
    // create an instance at the required relationship entity:
    user = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/users',
        body: {"login":"QuangNhat.7kinh27","firstName":"Tú Nguyệt","lastName":"Trần","email":"BaoSon_7ko27@hotmail.com","langKey":"pant leagu","imageUrl":"aw incidentally"},
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
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/lessons',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (courseEnrollment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-enrollments/${courseEnrollment.id}`,
      });
      courseEnrollment = undefined;
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

  test('CourseEnrollments menu should load CourseEnrollments page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course-enrollment');
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
    await expect(jhi.entity.getEntityHeading('CourseEnrollment')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
  });

  test.describe('CourseEnrollment page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(courseEnrollmentPageUrl);
      await expect(jhi.entity.getEntityHeading('CourseEnrollment')).not.toContainText('lumiApp.courseEnrollment.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseEnrollmentPageUrl);
        await response;
      });

      test('should load create CourseEnrollment page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${courseEnrollmentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseEnrollment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        courseEnrollment = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/course-enrollments',
            body: {
              ...courseEnrollmentSample,
              course: course,
              student: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseEnrollmentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/course-enrollments?page=0&size=20>; rel="last",<http://localhost/api/course-enrollments?page=0&size=20>; rel="first"',
              },
              json: [courseEnrollment],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(courseEnrollmentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseEnrollmentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing CourseEnrollment to exercise');
      });

      test('detail button click should load details CourseEnrollment page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('courseEnrollment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
      });

      test('edit button click should load edit CourseEnrollment page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseEnrollment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
      });

      test('edit button click should load edit CourseEnrollment page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseEnrollment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of CourseEnrollment', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${courseEnrollmentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('courseEnrollment')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);

        courseEnrollment = undefined;
      });
    });
  });

  test.describe('new CourseEnrollment page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(courseEnrollmentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('CourseEnrollment')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of CourseEnrollment', async ({ page }) => {
      await page.locator(`[data-cy="progressPercent"]`).fill('16182.99');
      await expect(page.locator(`[data-cy="progressPercent"]`)).toHaveValue('16182.99');

      await page.locator(`[data-cy="status"]`).fill('soup average');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('soup average');

      await page.locator(`[data-cy="enrolledAt"]`).fill('2026-10-12T18:10');
      await page.locator(`[data-cy="enrolledAt"]`).blur();
      await expect(page.locator(`[data-cy="enrolledAt"]`)).toHaveValue('2026-10-12T18:10');

      await page.locator(`[data-cy="expiresAt"]`).fill('2026-10-12T20:41');
      await page.locator(`[data-cy="expiresAt"]`).blur();
      await expect(page.locator(`[data-cy="expiresAt"]`)).toHaveValue('2026-10-12T20:41');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-10-12T06:10');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-10-12T06:10');

      await page.locator(`[data-cy="course"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseEnrollmentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      courseEnrollment = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === courseEnrollmentPageUrl);
    });
  });
});
