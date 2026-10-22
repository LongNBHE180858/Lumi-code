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

const examAttemptPageUrl = '/exam-attempt';
// const examAttemptSample = {"startTime":"2026-10-12T18:20:03.828Z"};

const examAttemptApiUrl = '/api/exam-attempts';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === examAttemptApiUrl && response.request().method() === 'GET');

test.describe('ExamAttempt e2e test', () => {
  let examAttempt;
  // let user;
  // let exam;

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
        body: {"login":"HongThao_Phan","firstName":"Quốc Hiển","lastName":"Tăng","email":"MinhDuyen_7ko83@hotmail.com","langKey":"vibrant pr","imageUrl":"homely however"},
      })
    ).json();
    // create an instance at the required relationship entity:
    exam = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/exams',
        body: {"title":"whoa weep backburn","durationMinutes":23097,"passScore":22803.43,"maxAttempts":1274,"shuffleQuestions":false,"status":"BLOCKED","isDeleted":true},
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
      url => url.pathname === '/api/exams',
      route => route.fulfill({ status: 200, json: [exam].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/proctor-sessions',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (examAttempt) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exam-attempts/${examAttempt.id}`,
      });
      examAttempt = undefined;
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
    if (exam) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exams/${exam.id}`,
      });
      exam = undefined;
    }
  });
   */

  test('ExamAttempts menu should load ExamAttempts page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('exam-attempt');
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
    await expect(jhi.entity.getEntityHeading('ExamAttempt')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
  });

  test.describe('ExamAttempt page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(examAttemptPageUrl);
      await expect(jhi.entity.getEntityHeading('ExamAttempt')).not.toContainText('lumiApp.examAttempt.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examAttemptPageUrl);
        await response;
      });

      test('should load create ExamAttempt page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${examAttemptPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAttempt')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        examAttempt = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/exam-attempts',
            body: {
              ...examAttemptSample,
              student: user,
              exam: exam,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === examAttemptApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/exam-attempts?page=0&size=20>; rel="last",<http://localhost/api/exam-attempts?page=0&size=20>; rel="first"',
              },
              json: [examAttempt],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(examAttemptPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examAttemptPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ExamAttempt to exercise');
      });

      test('detail button click should load details ExamAttempt page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('examAttempt')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
      });

      test('edit button click should load edit ExamAttempt page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAttempt')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
      });

      test('edit button click should load edit ExamAttempt page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAttempt')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ExamAttempt', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${examAttemptApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('examAttempt')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);

        examAttempt = undefined;
      });
    });
  });

  test.describe('new ExamAttempt page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(examAttemptPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAttempt')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ExamAttempt', async ({ page }) => {
      await page.locator(`[data-cy="startTime"]`).fill('2026-10-12T02:15');
      await page.locator(`[data-cy="startTime"]`).blur();
      await expect(page.locator(`[data-cy="startTime"]`)).toHaveValue('2026-10-12T02:15');

      await page.locator(`[data-cy="endTime"]`).fill('2026-10-12T05:38');
      await page.locator(`[data-cy="endTime"]`).blur();
      await expect(page.locator(`[data-cy="endTime"]`)).toHaveValue('2026-10-12T05:38');

      await page.locator(`[data-cy="score"]`).fill('24576.24');
      await expect(page.locator(`[data-cy="score"]`)).toHaveValue('24576.24');

      await page.locator(`[data-cy="status"]`).fill('pension spattering');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('pension spattering');

      await page.locator(`[data-cy="rowVersion"]`).fill('20944');
      await expect(page.locator(`[data-cy="rowVersion"]`)).toHaveValue('20944');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="exam"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === examAttemptApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      examAttempt = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === examAttemptPageUrl);
    });
  });
});
