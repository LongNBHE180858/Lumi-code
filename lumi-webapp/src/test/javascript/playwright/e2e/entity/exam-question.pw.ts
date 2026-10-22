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

const examQuestionPageUrl = '/exam-question';
const examQuestionSample = { orderIndex: 12682, score: 12541.87 };

const examQuestionApiUrl = '/api/exam-questions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === examQuestionApiUrl && response.request().method() === 'GET');

test.describe('ExamQuestion e2e test', () => {
  let examQuestion;
  let exam;
  let question;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    exam = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/exams',
        body: {
          title: 'inasmuch',
          durationMinutes: 10507,
          passScore: 1193.77,
          maxAttempts: 11313,
          shuffleQuestions: false,
          status: 'BLOCKED',
          isDeleted: true,
        },
      })
    ).json();
    // create an instance at the required relationship entity:
    question = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/questions',
        body: { content: 'flight', type: 'pish account', difficulty: 5, explanation: 'amongst whereas yet', isDeleted: true },
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/exams',
      route => route.fulfill({ status: 200, json: [exam].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/questions',
      route => route.fulfill({ status: 200, json: [question].filter(Boolean) }),
    );
  });

  test.afterEach(async ({ jhi }) => {
    if (examQuestion) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exam-questions/${examQuestion.id}`,
      });
      examQuestion = undefined;
    }
  });

  test.afterEach(async ({ jhi }) => {
    if (exam) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exams/${exam.id}`,
      });
      exam = undefined;
    }
    if (question) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/questions/${question.id}`,
      });
      question = undefined;
    }
  });

  test('ExamQuestions menu should load ExamQuestions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('exam-question');
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
    await expect(jhi.entity.getEntityHeading('ExamQuestion')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
  });

  test.describe('ExamQuestion page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(examQuestionPageUrl);
      await expect(jhi.entity.getEntityHeading('ExamQuestion')).not.toContainText('lumiApp.examQuestion.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examQuestionPageUrl);
        await response;
      });

      test('should load create ExamQuestion page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${examQuestionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamQuestion')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        examQuestion = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/exam-questions',
            body: {
              ...examQuestionSample,
              exam,
              question,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === examQuestionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [examQuestion],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(examQuestionPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details ExamQuestion page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('examQuestion')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
      });

      test('edit button click should load edit ExamQuestion page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamQuestion')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
      });

      test('edit button click should load edit ExamQuestion page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ExamQuestion')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
      });

      test('last delete button click should delete instance of ExamQuestion', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${examQuestionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('examQuestion')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);

        examQuestion = undefined;
      });
    });
  });

  test.describe('new ExamQuestion page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(examQuestionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ExamQuestion')).toBeVisible();
    });

    test('should create an instance of ExamQuestion', async ({ page }) => {
      await page.locator(`[data-cy="orderIndex"]`).fill('29345');
      await expect(page.locator(`[data-cy="orderIndex"]`)).toHaveValue('29345');

      await page.locator(`[data-cy="score"]`).fill('9205.3');
      await expect(page.locator(`[data-cy="score"]`)).toHaveValue('9205.3');

      await page.locator(`[data-cy="exam"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="question"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === examQuestionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      examQuestion = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === examQuestionPageUrl);
    });
  });
});
