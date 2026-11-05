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

const lessonProgressPageUrl = '/lesson-progress';
// const lessonProgressSample = {"status":"fortunate","lastPositionSeconds":4349.56,"rowVersion":5731};

const lessonProgressApiUrl = '/api/lesson-progresses';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === lessonProgressApiUrl && response.request().method() === 'GET');

test.describe('LessonProgress e2e test', () => {
  let lessonProgress;
  // let courseEnrollment;
  // let lesson;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    courseEnrollment = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/course-enrollments',
        body: {"progressPercent":15470.19,"status":"uh-huh unlike greedi","enrolledAt":"2026-10-12T10:36:28.214Z","expiresAt":"2026-10-12T21:17:59.484Z","updatedAt":"2026-10-12T13:19:39.435Z"},
      })
    ).json();
    // create an instance at the required relationship entity:
    lesson = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/lessons',
        body: {"title":"government though consequently","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","contentType":"charter taxicab blus","videoUrl":"lovingly","videoFileName":"freely energetically drat","videoSizeBytes":26050,"orderIndex":16154,"isPreview":true,"durationMinutes":20931,"status":"HIDDEN","durationSeconds":16611,"videoObjectKey":"overdub briskly near","updatedAt":"2026-10-12T21:30:01.288Z"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/course-enrollments',
      route => route.fulfill({ status: 200, json: [courseEnrollment].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/lessons',
      route => route.fulfill({ status: 200, json: [lesson].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (lessonProgress) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/lesson-progresses/${lessonProgress.id}`,
      });
      lessonProgress = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (courseEnrollment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-enrollments/${courseEnrollment.id}`,
      });
      courseEnrollment = undefined;
    }
    if (lesson) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/lessons/${lesson.id}`,
      });
      lesson = undefined;
    }
  });
   */

  test('LessonProgresses menu should load LessonProgresses page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('lesson-progress');
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
    await expect(jhi.entity.getEntityHeading('LessonProgress')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
  });

  test.describe('LessonProgress page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(lessonProgressPageUrl);
      await expect(jhi.entity.getEntityHeading('LessonProgress')).not.toContainText('lumiApp.lessonProgress.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(lessonProgressPageUrl);
        await response;
      });

      test('should load create LessonProgress page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${lessonProgressPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('LessonProgress')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        lessonProgress = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/lesson-progresses',
            body: {
              ...lessonProgressSample,
              enrollment: courseEnrollment,
              lesson: lesson,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === lessonProgressApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/lesson-progresses?page=0&size=20>; rel="last",<http://localhost/api/lesson-progresses?page=0&size=20>; rel="first"',
              },
              json: [lessonProgress],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(lessonProgressPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(lessonProgressPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing LessonProgress to exercise');
      });

      test('detail button click should load details LessonProgress page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('lessonProgress')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
      });

      test('edit button click should load edit LessonProgress page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('LessonProgress')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
      });

      test('edit button click should load edit LessonProgress page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('LessonProgress')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of LessonProgress', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${lessonProgressApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('lessonProgress')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);

        lessonProgress = undefined;
      });
    });
  });

  test.describe('new LessonProgress page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(lessonProgressPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('LessonProgress')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of LessonProgress', async ({ page }) => {
      await page.locator(`[data-cy="status"]`).fill('likewise');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('likewise');

      await page.locator(`[data-cy="lastPositionSeconds"]`).fill('27245.03');
      await expect(page.locator(`[data-cy="lastPositionSeconds"]`)).toHaveValue('27245.03');

      await page.locator(`[data-cy="lastAccessedAt"]`).fill('2026-11-02T18:31');
      await page.locator(`[data-cy="lastAccessedAt"]`).blur();
      await expect(page.locator(`[data-cy="lastAccessedAt"]`)).toHaveValue('2026-11-02T18:31');

      await page.locator(`[data-cy="completedAt"]`).fill('2026-11-02T07:49');
      await page.locator(`[data-cy="completedAt"]`).blur();
      await expect(page.locator(`[data-cy="completedAt"]`)).toHaveValue('2026-11-02T07:49');

      await page.locator(`[data-cy="rowVersion"]`).fill('21348');
      await expect(page.locator(`[data-cy="rowVersion"]`)).toHaveValue('21348');

      await page.locator(`[data-cy="enrollment"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="lesson"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === lessonProgressApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      lessonProgress = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === lessonProgressPageUrl);
    });
  });
});
