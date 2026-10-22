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

const flashcardDeckPageUrl = '/flashcard-deck';
// const flashcardDeckSample = {"title":"reluctantly ew"};

const flashcardDeckApiUrl = '/api/flashcard-decks';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === flashcardDeckApiUrl && response.request().method() === 'GET');

test.describe('FlashcardDeck e2e test', () => {
  let flashcardDeck;
  // let user;

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
        body: {"login":"CaoNghiep2","firstName":"Thu Thuận","lastName":"Phùng","email":"BaoHuynh.Hoang27@hotmail.com","langKey":"toward yah","imageUrl":"afore cute left"},
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
      url => url.pathname === '/api/subjects',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (flashcardDeck) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flashcard-decks/${flashcardDeck.id}`,
      });
      flashcardDeck = undefined;
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
  });
   */

  test('FlashcardDecks menu should load FlashcardDecks page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('flashcard-deck');
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
    await expect(jhi.entity.getEntityHeading('FlashcardDeck')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
  });

  test.describe('FlashcardDeck page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(flashcardDeckPageUrl);
      await expect(jhi.entity.getEntityHeading('FlashcardDeck')).not.toContainText('lumiApp.flashcardDeck.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardDeckPageUrl);
        await response;
      });

      test('should load create FlashcardDeck page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${flashcardDeckPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardDeck')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        flashcardDeck = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/flashcard-decks',
            body: {
              ...flashcardDeckSample,
              owner: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === flashcardDeckApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/flashcard-decks?page=0&size=20>; rel="last",<http://localhost/api/flashcard-decks?page=0&size=20>; rel="first"',
              },
              json: [flashcardDeck],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(flashcardDeckPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(flashcardDeckPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing FlashcardDeck to exercise');
      });

      test('detail button click should load details FlashcardDeck page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('flashcardDeck')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
      });

      test('edit button click should load edit FlashcardDeck page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardDeck')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
      });

      test('edit button click should load edit FlashcardDeck page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardDeck')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of FlashcardDeck', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${flashcardDeckApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('flashcardDeck')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);

        flashcardDeck = undefined;
      });
    });
  });

  test.describe('new FlashcardDeck page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(flashcardDeckPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('FlashcardDeck')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of FlashcardDeck', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('saw scratchy browse');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('saw scratchy browse');

      await expect(page.locator(`[data-cy="isPublic"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isPublic"]`).click();
      await expect(page.locator(`[data-cy="isPublic"]`)).toBeChecked();

      await page.locator(`[data-cy="owner"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === flashcardDeckApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      flashcardDeck = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === flashcardDeckPageUrl);
    });
  });
});
