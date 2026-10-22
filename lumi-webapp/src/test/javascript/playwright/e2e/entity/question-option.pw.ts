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

const questionOptionPageUrl = '/question-option';
const questionOptionSample = { content: 'generally' };

const questionOptionApiUrl = '/api/question-options';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === questionOptionApiUrl && response.request().method() === 'GET');

test.describe('QuestionOption e2e test', () => {
  let questionOption;
  let question;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    question = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/questions',
        body: { content: 'elegantly', type: 'once hippodrome dash', difficulty: 1, explanation: 'hence', isDeleted: true },
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/questions',
      route => route.fulfill({ status: 200, json: [question].filter(Boolean) }),
    );
  });

  test.afterEach(async ({ jhi }) => {
    if (questionOption) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/question-options/${questionOption.id}`,
      });
      questionOption = undefined;
    }
  });

  test.afterEach(async ({ jhi }) => {
    if (question) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/questions/${question.id}`,
      });
      question = undefined;
    }
  });

  test('QuestionOptions menu should load QuestionOptions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('question-option');
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
    await expect(jhi.entity.getEntityHeading('QuestionOption')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
  });

  test.describe('QuestionOption page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(questionOptionPageUrl);
      await expect(jhi.entity.getEntityHeading('QuestionOption')).not.toContainText('lumiApp.questionOption.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(questionOptionPageUrl);
        await response;
      });

      test('should load create QuestionOption page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${questionOptionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionOption')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        questionOption = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/question-options',
            body: {
              ...questionOptionSample,
              question,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === questionOptionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [questionOption],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(questionOptionPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details QuestionOption page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('questionOption')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
      });

      test('edit button click should load edit QuestionOption page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionOption')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
      });

      test('edit button click should load edit QuestionOption page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionOption')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
      });

      test('last delete button click should delete instance of QuestionOption', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${questionOptionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('questionOption')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);

        questionOption = undefined;
      });
    });
  });

  test.describe('new QuestionOption page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(questionOptionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionOption')).toBeVisible();
    });

    test('should create an instance of QuestionOption', async ({ page }) => {
      await page.locator(`[data-cy="content"]`).fill('sashay');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue('sashay');

      await expect(page.locator(`[data-cy="isCorrect"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isCorrect"]`).click();
      await expect(page.locator(`[data-cy="isCorrect"]`)).toBeChecked();

      await page.locator(`[data-cy="question"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === questionOptionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      questionOption = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === questionOptionPageUrl);
    });
  });
});
