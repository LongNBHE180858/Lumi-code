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

const categoryPageUrl = '/category';
const categorySample = { name: 'now step-mother' };

const categoryApiUrl = '/api/categories';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === categoryApiUrl && response.request().method() === 'GET');

test.describe('Category e2e test', () => {
  let category;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (category) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/categories/${category.id}`,
      });
      category = undefined;
    }
  });

  test('Categories menu should load Categories page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('category');
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
    await expect(jhi.entity.getEntityHeading('Category')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
  });

  test.describe('Category page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(categoryPageUrl);
      await expect(jhi.entity.getEntityHeading('Category')).not.toContainText('lumiApp.category.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(categoryPageUrl);
        await response;
      });

      test('should load create Category page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${categoryPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Category')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        category = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/categories',
            body: categorySample,
          })
        ).json();

        await page.route(
          url => url.pathname === categoryApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/categories?page=0&size=20>; rel="last",<http://localhost/api/categories?page=0&size=20>; rel="first"',
              },
              json: [category],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(categoryPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Category page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('category')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
      });

      test('edit button click should load edit Category page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Category')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
      });

      test('edit button click should load edit Category page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Category')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
      });

      test('last delete button click should delete instance of Category', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${categoryApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('category')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);

        category = undefined;
      });
    });
  });

  test.describe('new Category page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(categoryPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Category')).toBeVisible();
    });

    test('should create an instance of Category', async ({ page }) => {
      await page.locator(`[data-cy="name"]`).fill('since at');
      await expect(page.locator(`[data-cy="name"]`)).toHaveValue('since at');

      await page.locator(`[data-cy="description"]`).fill('joshingly phooey');
      await expect(page.locator(`[data-cy="description"]`)).toHaveValue('joshingly phooey');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === categoryApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      category = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === categoryPageUrl);
    });
  });
});
