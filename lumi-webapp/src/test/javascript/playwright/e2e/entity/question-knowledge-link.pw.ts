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

const questionKnowledgeLinkPageUrl = '/question-knowledge-link';
// const questionKnowledgeLinkSample = {"weight":15437.98};

const questionKnowledgeLinkApiUrl = '/api/question-knowledge-links';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(
    response => new URL(response.url()).pathname === questionKnowledgeLinkApiUrl && response.request().method() === 'GET',
  );

test.describe('QuestionKnowledgeLink e2e test', () => {
  let questionKnowledgeLink;
  // let question;
  // let knowledgeNode;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    question = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/questions',
        body: {"content":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","type":"narrowcast inasmuch","difficulty":4,"explanation":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","isDeleted":true,"sourceType":"scholarship geez","contentHash":"what conservative","visibility":"radiant strident upo","version":27955,"createdAt":"2026-10-12T15:00:41.086Z","updatedAt":"2026-10-12T22:38:04.947Z"},
      })
    ).json();
    // create an instance at the required relationship entity:
    knowledgeNode = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/knowledge-nodes',
        body: {"title":"forgather monumental duh","type":"towards after the"},
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
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/knowledge-nodes',
      route => route.fulfill({ status: 200, json: [knowledgeNode].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (questionKnowledgeLink) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/question-knowledge-links/${questionKnowledgeLink.id}`,
      });
      questionKnowledgeLink = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (question) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/questions/${question.id}`,
      });
      question = undefined;
    }
    if (knowledgeNode) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/knowledge-nodes/${knowledgeNode.id}`,
      });
      knowledgeNode = undefined;
    }
  });
   */

  test('QuestionKnowledgeLinks menu should load QuestionKnowledgeLinks page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('question-knowledge-link');
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
    await expect(jhi.entity.getEntityHeading('QuestionKnowledgeLink')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
  });

  test.describe('QuestionKnowledgeLink page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(questionKnowledgeLinkPageUrl);
      await expect(jhi.entity.getEntityHeading('QuestionKnowledgeLink')).not.toContainText('lumiApp.questionKnowledgeLink.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(questionKnowledgeLinkPageUrl);
        await response;
      });

      test('should load create QuestionKnowledgeLink page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${questionKnowledgeLinkPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionKnowledgeLink')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        questionKnowledgeLink = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/question-knowledge-links',
            body: {
              ...questionKnowledgeLinkSample,
              question: question,
              knowledgeNode: knowledgeNode,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === questionKnowledgeLinkApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/question-knowledge-links?page=0&size=20>; rel="last",<http://localhost/api/question-knowledge-links?page=0&size=20>; rel="first"',
              },
              json: [questionKnowledgeLink],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(questionKnowledgeLinkPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(questionKnowledgeLinkPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing QuestionKnowledgeLink to exercise');
      });

      test('detail button click should load details QuestionKnowledgeLink page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('questionKnowledgeLink')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
      });

      test('edit button click should load edit QuestionKnowledgeLink page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionKnowledgeLink')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
      });

      test('edit button click should load edit QuestionKnowledgeLink page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionKnowledgeLink')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of QuestionKnowledgeLink', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${questionKnowledgeLinkApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('questionKnowledgeLink')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);

        questionKnowledgeLink = undefined;
      });
    });
  });

  test.describe('new QuestionKnowledgeLink page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(questionKnowledgeLinkPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('QuestionKnowledgeLink')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of QuestionKnowledgeLink', async ({ page }) => {
      await page.locator(`[data-cy="weight"]`).fill('14695.11');
      await expect(page.locator(`[data-cy="weight"]`)).toHaveValue('14695.11');

      await page.locator(`[data-cy="question"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="knowledgeNode"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === questionKnowledgeLinkApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      questionKnowledgeLink = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === questionKnowledgeLinkPageUrl);
    });
  });
});
