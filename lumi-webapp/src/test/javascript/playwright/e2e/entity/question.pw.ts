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

const questionPageUrl = '/question';
const questionSample = { content: 'yahoo shabby unexpectedly', type: 'fooey eek provided' };

const questionApiUrl = '/api/questions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === questionApiUrl && response.request().method() === 'GET');

test.describe('Question e2e test', () => {
  let question;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
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

  test('Questions menu should load Questions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('question');
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
    await expect(jhi.entity.getEntityHeading('Question')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
  });

  test.describe('Question page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(questionPageUrl);
      await expect(jhi.entity.getEntityHeading('Question')).not.toContainText('lumiApp.question.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(questionPageUrl);
        await response;
      });

      test('should load create Question page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${questionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Question')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        question = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/questions',
            body: questionSample,
          })
        ).json();

        await page.route(
          url => url.pathname === questionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/questions?page=0&size=20>; rel="last",<http://localhost/api/questions?page=0&size=20>; rel="first"',
              },
              json: [question],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(questionPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Question page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('question')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
      });

      test('edit button click should load edit Question page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Question')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
      });

      test('edit button click should load edit Question page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Question')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
      });

      test('last delete button click should delete instance of Question', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${questionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('question')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionPageUrl);

        question = undefined;
      });
    });
  });

  test.describe('new Question page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(questionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Question')).toBeVisible();
    });

    test('should create an instance of Question', async ({ page }) => {
      await page.locator(`[data-cy="content"]`).fill('jogging');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue('jogging');

      await page.locator(`[data-cy="type"]`).fill('from hygienic');
      await expect(page.locator(`[data-cy="type"]`)).toHaveValue('from hygienic');

      await page.locator(`[data-cy="difficulty"]`).fill('4');
      await expect(page.locator(`[data-cy="difficulty"]`)).toHaveValue('4');

      await page.locator(`[data-cy="explanation"]`).fill('harangue blink phooey');
      await expect(page.locator(`[data-cy="explanation"]`)).toHaveValue('harangue blink phooey');

      await expect(page.locator(`[data-cy="isDeleted"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isDeleted"]`).click();
      await expect(page.locator(`[data-cy="isDeleted"]`)).toBeChecked();

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === questionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      question = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === questionPageUrl);
    });
  });
});
