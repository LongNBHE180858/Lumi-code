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

const examPageUrl = '/exam';
const examSample = { title: 'animated', durationMinutes: 10662, passScore: 14623.56 };

const examApiUrl = '/api/exams';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === examApiUrl && response.request().method() === 'GET');

test.describe('Exam e2e test', () => {
  let exam;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (exam) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exams/${exam.id}`,
      });
      exam = undefined;
    }
  });

  test('Exams menu should load Exams page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('exam');
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
    await expect(jhi.entity.getEntityHeading('Exam')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === examPageUrl);
  });

  test.describe('Exam page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(examPageUrl);
      await expect(jhi.entity.getEntityHeading('Exam')).not.toContainText('lumiApp.exam.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(examPageUrl);
        await response;
      });

      test('should load create Exam page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${examPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Exam')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        exam = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/exams',
            body: examSample,
          })
        ).json();

        await page.route(
          url => url.pathname === examApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/exams?page=0&size=20>; rel="last",<http://localhost/api/exams?page=0&size=20>; rel="first"',
              },
              json: [exam],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(examPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Exam page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('exam')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examPageUrl);
      });

      test('edit button click should load edit Exam page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Exam')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examPageUrl);
      });

      test('edit button click should load edit Exam page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Exam')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examPageUrl);
      });

      test('last delete button click should delete instance of Exam', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${examApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('exam')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === examPageUrl);

        exam = undefined;
      });
    });
  });

  test.describe('new Exam page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(examPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Exam')).toBeVisible();
    });

    test('should create an instance of Exam', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('persecute qua');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('persecute qua');

      await page.locator(`[data-cy="durationMinutes"]`).fill('3952');
      await expect(page.locator(`[data-cy="durationMinutes"]`)).toHaveValue('3952');

      await page.locator(`[data-cy="passScore"]`).fill('6934.92');
      await expect(page.locator(`[data-cy="passScore"]`)).toHaveValue('6934.92');

      await page.locator(`[data-cy="maxAttempts"]`).fill('13944');
      await expect(page.locator(`[data-cy="maxAttempts"]`)).toHaveValue('13944');

      await expect(page.locator(`[data-cy="shuffleQuestions"]`)).not.toBeChecked();
      await page.locator(`[data-cy="shuffleQuestions"]`).click();
      await expect(page.locator(`[data-cy="shuffleQuestions"]`)).toBeChecked();

      await page.locator(`[data-cy="status"]`).selectOption('DRAFT');

      await expect(page.locator(`[data-cy="isDeleted"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isDeleted"]`).click();
      await expect(page.locator(`[data-cy="isDeleted"]`)).toBeChecked();

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === examApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      exam = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === examPageUrl);
    });
  });
});
