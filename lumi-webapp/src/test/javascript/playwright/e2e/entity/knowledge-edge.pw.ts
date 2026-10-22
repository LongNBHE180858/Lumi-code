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

const knowledgeEdgePageUrl = '/knowledge-edge';
const knowledgeEdgeSample = { relationType: 'zowie' };

const knowledgeEdgeApiUrl = '/api/knowledge-edges';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === knowledgeEdgeApiUrl && response.request().method() === 'GET');

test.describe('KnowledgeEdge e2e test', () => {
  let knowledgeEdge;
  let knowledgeNode;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    knowledgeNode = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/knowledge-nodes',
        body: { title: 'naughty', type: 'bossy where' },
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/knowledge-nodes',
      route => route.fulfill({ status: 200, json: [knowledgeNode].filter(Boolean) }),
    );
  });

  test.afterEach(async ({ jhi }) => {
    if (knowledgeEdge) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/knowledge-edges/${knowledgeEdge.id}`,
      });
      knowledgeEdge = undefined;
    }
  });

  test.afterEach(async ({ jhi }) => {
    if (knowledgeNode) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/knowledge-nodes/${knowledgeNode.id}`,
      });
      knowledgeNode = undefined;
    }
  });

  test('KnowledgeEdges menu should load KnowledgeEdges page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('knowledge-edge');
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
    await expect(jhi.entity.getEntityHeading('KnowledgeEdge')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
  });

  test.describe('KnowledgeEdge page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(knowledgeEdgePageUrl);
      await expect(jhi.entity.getEntityHeading('KnowledgeEdge')).not.toContainText('lumiApp.knowledgeEdge.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(knowledgeEdgePageUrl);
        await response;
      });

      test('should load create KnowledgeEdge page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${knowledgeEdgePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeEdge')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        knowledgeEdge = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/knowledge-edges',
            body: {
              ...knowledgeEdgeSample,
              fromNode: knowledgeNode,
              toNode: knowledgeNode,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === knowledgeEdgeApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [knowledgeEdge],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(knowledgeEdgePageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details KnowledgeEdge page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('knowledgeEdge')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
      });

      test('edit button click should load edit KnowledgeEdge page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeEdge')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
      });

      test('edit button click should load edit KnowledgeEdge page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeEdge')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
      });

      test('last delete button click should delete instance of KnowledgeEdge', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${knowledgeEdgeApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('knowledgeEdge')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);

        knowledgeEdge = undefined;
      });
    });
  });

  test.describe('new KnowledgeEdge page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(knowledgeEdgePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeEdge')).toBeVisible();
    });

    test('should create an instance of KnowledgeEdge', async ({ page }) => {
      await page.locator(`[data-cy="relationType"]`).fill('regularly');
      await expect(page.locator(`[data-cy="relationType"]`)).toHaveValue('regularly');

      await page.locator(`[data-cy="fromNode"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="toNode"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === knowledgeEdgeApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      knowledgeEdge = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === knowledgeEdgePageUrl);
    });
  });
});
