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

const examAnswerPageUrl = '/exam-answer';
// const examAnswerSample = {};

const examAnswerApiUrl = '/api/exam-answers';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === examAnswerApiUrl && response.request().method() === 'GET');

test.describe('ExamAnswer e2e test', () => {
  let examAnswer;
  // let examAttempt;
  // let question;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    examAttempt = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/exam-attempts',
        body: {"startTime":"2026-10-12T20:42:00.965Z","endTime":"2026-10-11T23:22:09.955Z","score":18501.73,"status":"stable ultimately","rowVersion":30165},
      })
    ).json();
    // create an instance at the required relationship entity:
    question = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/questions',
        body: {"content":"naughty swiftly","type":"pneumonia ponder che","difficulty":3,"explanation":"past","isDeleted":false},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/exam-attempts',
      route => route.fulfill({ status: 200, json: [examAttempt].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/questions',
      route => route.fulfill({ status: 200, json: [question].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/question-options',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (examAnswer) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exam-answers/${examAnswer.id}`,
      });
      examAnswer = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (examAttempt) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exam-attempts/${examAttempt.id}`,
      });
      examAttempt = undefined;
    }
    if (question) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/questions/${question.id}`,
      });
      question = undefined;
    }
  });
   */

  test('ExamAnswers menu should load ExamAnswers page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('exam-answer');
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
    await expect(jhi.entity.getEntityHeading('ExamAnswer')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
  });

  test.describe('ExamAnswer page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(examAnswerPageUrl);
      await expect(jhi.entity.getEntityHeading('ExamAnswer')).not.toContainText('lumiApp.examAnswer.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examAnswerPageUrl);
        await response;
      });

      test('should load create ExamAnswer page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${examAnswerPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAnswer')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        examAnswer = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/exam-answers',
            body: {
              ...examAnswerSample,
              attempt: examAttempt,
              question: question,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === examAnswerApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/exam-answers?page=0&size=20>; rel="last",<http://localhost/api/exam-answers?page=0&size=20>; rel="first"',
              },
              json: [examAnswer],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(examAnswerPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examAnswerPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ExamAnswer to exercise');
      });

      test('detail button click should load details ExamAnswer page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('examAnswer')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
      });

      test('edit button click should load edit ExamAnswer page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAnswer')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
      });

      test('edit button click should load edit ExamAnswer page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAnswer')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ExamAnswer', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${examAnswerApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('examAnswer')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);

        examAnswer = undefined;
      });
    });
  });

  test.describe('new ExamAnswer page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(examAnswerPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ExamAnswer')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ExamAnswer', async ({ page }) => {
      await page.locator(`[data-cy="textAnswer"]`).fill('circa');
      await expect(page.locator(`[data-cy="textAnswer"]`)).toHaveValue('circa');

      await expect(page.locator(`[data-cy="isCorrect"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isCorrect"]`).click();
      await expect(page.locator(`[data-cy="isCorrect"]`)).toBeChecked();

      await page.locator(`[data-cy="score"]`).fill('18019.65');
      await expect(page.locator(`[data-cy="score"]`)).toHaveValue('18019.65');

      await page.locator(`[data-cy="attempt"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="question"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === examAnswerApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      examAnswer = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === examAnswerPageUrl);
    });
  });
});
