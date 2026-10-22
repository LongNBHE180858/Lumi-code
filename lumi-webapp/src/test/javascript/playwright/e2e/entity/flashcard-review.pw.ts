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

const flashcardReviewPageUrl = '/flashcard-review';
// const flashcardReviewSample = {};

const flashcardReviewApiUrl = '/api/flashcard-reviews';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === flashcardReviewApiUrl && response.request().method() === 'GET');

test.describe('FlashcardReview e2e test', () => {
  let flashcardReview;
  // let user;
  // let flashcard;

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
        body: {"login":"TanPhat_7kinh","firstName":"Lam Giang","lastName":"Tô","email":"MinhVy_To70@yahoo.com","langKey":"brr geez h","imageUrl":"deed prime"},
      })
    ).json();
    // create an instance at the required relationship entity:
    flashcard = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/flashcards',
        body: {"frontText":"march obnoxiously meanwhile","backText":"er"},
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
      url => url.pathname === '/api/flashcards',
      route => route.fulfill({ status: 200, json: [flashcard].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (flashcardReview) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flashcard-reviews/${flashcardReview.id}`,
      });
      flashcardReview = undefined;
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
    if (flashcard) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flashcards/${flashcard.id}`,
      });
      flashcard = undefined;
    }
  });
   */

  test('FlashcardReviews menu should load FlashcardReviews page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('flashcard-review');
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
    await expect(jhi.entity.getEntityHeading('FlashcardReview')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
  });

  test.describe('FlashcardReview page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(flashcardReviewPageUrl);
      await expect(jhi.entity.getEntityHeading('FlashcardReview')).not.toContainText('lumiApp.flashcardReview.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardReviewPageUrl);
        await response;
      });

      test('should load create FlashcardReview page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${flashcardReviewPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardReview')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        flashcardReview = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/flashcard-reviews',
            body: {
              ...flashcardReviewSample,
              student: user,
              flashcard: flashcard,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === flashcardReviewApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [flashcardReview],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(flashcardReviewPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardReviewPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing FlashcardReview to exercise');
      });

      test('detail button click should load details FlashcardReview page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('flashcardReview')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
      });

      test('edit button click should load edit FlashcardReview page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardReview')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
      });

      test('edit button click should load edit FlashcardReview page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardReview')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of FlashcardReview', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${flashcardReviewApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('flashcardReview')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);

        flashcardReview = undefined;
      });
    });
  });

  test.describe('new FlashcardReview page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(flashcardReviewPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardReview')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of FlashcardReview', async ({ page }) => {
      await page.locator(`[data-cy="nextReviewAt"]`).fill('2026-10-12T03:03');
      await page.locator(`[data-cy="nextReviewAt"]`).blur();
      await expect(page.locator(`[data-cy="nextReviewAt"]`)).toHaveValue('2026-10-12T03:03');

      await page.locator(`[data-cy="intervalDays"]`).fill('10375.59');
      await expect(page.locator(`[data-cy="intervalDays"]`)).toHaveValue('10375.59');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="flashcard"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === flashcardReviewApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      flashcardReview = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === flashcardReviewPageUrl);
    });
  });
});
