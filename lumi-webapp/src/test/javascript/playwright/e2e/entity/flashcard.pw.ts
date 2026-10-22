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

const flashcardPageUrl = '/flashcard';
// const flashcardSample = {"frontText":"questioningly effector","backText":"thankfully"};

const flashcardApiUrl = '/api/flashcards';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === flashcardApiUrl && response.request().method() === 'GET');

test.describe('Flashcard e2e test', () => {
  let flashcard;
  // let flashcardDeck;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    flashcardDeck = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/flashcard-decks',
        body: {"title":"usefully rag boo","isPublic":false},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/flashcard-decks',
      route => route.fulfill({ status: 200, json: [flashcardDeck].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (flashcard) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flashcards/${flashcard.id}`,
      });
      flashcard = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (flashcardDeck) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flashcard-decks/${flashcardDeck.id}`,
      });
      flashcardDeck = undefined;
    }
  });
   */

  test('Flashcards menu should load Flashcards page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('flashcard');
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
    await expect(jhi.entity.getEntityHeading('Flashcard')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
  });

  test.describe('Flashcard page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(flashcardPageUrl);
      await expect(jhi.entity.getEntityHeading('Flashcard')).not.toContainText('lumiApp.flashcard.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardPageUrl);
        await response;
      });

      test('should load create Flashcard page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${flashcardPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Flashcard')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        flashcard = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/flashcards',
            body: {
              ...flashcardSample,
              deck: flashcardDeck,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === flashcardApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/flashcards?page=0&size=20>; rel="last",<http://localhost/api/flashcards?page=0&size=20>; rel="first"',
              },
              json: [flashcard],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(flashcardPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Flashcard to exercise');
      });

      test('detail button click should load details Flashcard page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('flashcard')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
      });

      test('edit button click should load edit Flashcard page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Flashcard')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
      });

      test('edit button click should load edit Flashcard page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Flashcard')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Flashcard', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${flashcardApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('flashcard')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);

        flashcard = undefined;
      });
    });
  });

  test.describe('new Flashcard page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(flashcardPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Flashcard')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Flashcard', async ({ page }) => {
      await page.locator(`[data-cy="frontText"]`).fill('miserly transplant');
      await expect(page.locator(`[data-cy="frontText"]`)).toHaveValue('miserly transplant');

      await page.locator(`[data-cy="backText"]`).fill('until');
      await expect(page.locator(`[data-cy="backText"]`)).toHaveValue('until');

      await page.locator(`[data-cy="deck"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === flashcardApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      flashcard = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === flashcardPageUrl);
    });
  });
});
