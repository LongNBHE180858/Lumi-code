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

const knowledgeNodePageUrl = '/knowledge-node';
const knowledgeNodeSample = { title: 'than collaboration shameful' };

const knowledgeNodeApiUrl = '/api/knowledge-nodes';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === knowledgeNodeApiUrl && response.request().method() === 'GET');

test.describe('KnowledgeNode e2e test', () => {
  let knowledgeNode;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
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

  test('KnowledgeNodes menu should load KnowledgeNodes page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('knowledge-node');
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
    await expect(jhi.entity.getEntityHeading('KnowledgeNode')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
  });

  test.describe('KnowledgeNode page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(knowledgeNodePageUrl);
      await expect(jhi.entity.getEntityHeading('KnowledgeNode')).not.toContainText('lumiApp.knowledgeNode.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(knowledgeNodePageUrl);
        await response;
      });

      test('should load create KnowledgeNode page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${knowledgeNodePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeNode')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        knowledgeNode = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/knowledge-nodes',
            body: knowledgeNodeSample,
          })
        ).json();

        await page.route(
          url => url.pathname === knowledgeNodeApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/knowledge-nodes?page=0&size=20>; rel="last",<http://localhost/api/knowledge-nodes?page=0&size=20>; rel="first"',
              },
              json: [knowledgeNode],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(knowledgeNodePageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details KnowledgeNode page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('knowledgeNode')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
      });

      test('edit button click should load edit KnowledgeNode page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeNode')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
      });

      test('edit button click should load edit KnowledgeNode page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeNode')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
      });

      test('last delete button click should delete instance of KnowledgeNode', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${knowledgeNodeApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('knowledgeNode')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);

        knowledgeNode = undefined;
      });
    });
  });

  test.describe('new KnowledgeNode page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(knowledgeNodePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('KnowledgeNode')).toBeVisible();
    });

    test('should create an instance of KnowledgeNode', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('velvety');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('velvety');

      await page.locator(`[data-cy="type"]`).fill('though who');
      await expect(page.locator(`[data-cy="type"]`)).toHaveValue('though who');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === knowledgeNodeApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      knowledgeNode = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === knowledgeNodePageUrl);
    });
  });
});
