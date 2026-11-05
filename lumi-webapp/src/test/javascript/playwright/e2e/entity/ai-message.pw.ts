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

const aiMessagePageUrl = '/ai-message';
// const aiMessageSample = {"senderRole":"deeply","content":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","citations":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","generationStatus":"energetically step u"};

const aiMessageApiUrl = '/api/ai-messages';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === aiMessageApiUrl && response.request().method() === 'GET');

test.describe('AiMessage e2e test', () => {
  let aiMessage;
  // let aiConversation;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    aiConversation = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/ai-conversations',
        body: {"type":"fax","createdAt":"2026-10-12T15:07:43.339Z","updatedAt":"2026-10-12T17:01:09.292Z"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/ai-conversations',
      route => route.fulfill({ status: 200, json: [aiConversation].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (aiMessage) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ai-messages/${aiMessage.id}`,
      });
      aiMessage = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (aiConversation) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ai-conversations/${aiConversation.id}`,
      });
      aiConversation = undefined;
    }
  });
   */

  test('AiMessages menu should load AiMessages page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('ai-message');
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
    await expect(jhi.entity.getEntityHeading('AiMessage')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
  });

  test.describe('AiMessage page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(aiMessagePageUrl);
      await expect(jhi.entity.getEntityHeading('AiMessage')).not.toContainText('lumiApp.aiMessage.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(aiMessagePageUrl);
        await response;
      });

      test('should load create AiMessage page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${aiMessagePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiMessage')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        aiMessage = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/ai-messages',
            body: {
              ...aiMessageSample,
              conversation: aiConversation,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === aiMessageApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/ai-messages?page=0&size=20>; rel="last",<http://localhost/api/ai-messages?page=0&size=20>; rel="first"',
              },
              json: [aiMessage],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(aiMessagePageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(aiMessagePageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing AiMessage to exercise');
      });

      test('detail button click should load details AiMessage page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('aiMessage')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
      });

      test('edit button click should load edit AiMessage page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiMessage')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
      });

      test('edit button click should load edit AiMessage page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiMessage')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of AiMessage', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${aiMessageApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('aiMessage')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);

        aiMessage = undefined;
      });
    });
  });

  test.describe('new AiMessage page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(aiMessagePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AiMessage')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of AiMessage', async ({ page }) => {
      await page.locator(`[data-cy="senderRole"]`).fill('rough remarkable');
      await expect(page.locator(`[data-cy="senderRole"]`)).toHaveValue('rough remarkable');

      await page.locator(`[data-cy="content"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T10:24');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T10:24');

      await page.locator(`[data-cy="citations"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="citations"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="generationStatus"]`).fill('knickers override');
      await expect(page.locator(`[data-cy="generationStatus"]`)).toHaveValue('knickers override');

      await page.locator(`[data-cy="modelName"]`).fill('per receptor shampoo');
      await expect(page.locator(`[data-cy="modelName"]`)).toHaveValue('per receptor shampoo');

      await page.locator(`[data-cy="feedbackRating"]`).fill('27837');
      await expect(page.locator(`[data-cy="feedbackRating"]`)).toHaveValue('27837');

      await page.locator(`[data-cy="feedbackNote"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="feedbackNote"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="feedbackAt"]`).fill('2026-10-12T11:07');
      await page.locator(`[data-cy="feedbackAt"]`).blur();
      await expect(page.locator(`[data-cy="feedbackAt"]`)).toHaveValue('2026-10-12T11:07');

      await page.locator(`[data-cy="conversation"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === aiMessageApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      aiMessage = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === aiMessagePageUrl);
    });
  });
});
