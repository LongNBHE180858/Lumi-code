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

const systemPlanPageUrl = '/system-plan';
const systemPlanSample = {
  name: 'meanwhile cellar',
  price: 22248.41,
  durationDays: 21008,
  storageQuotaMb: 21179,
  aiGenerationLimit: 17413,
  maxClasses: 23705,
};

const systemPlanApiUrl = '/api/system-plans';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === systemPlanApiUrl && response.request().method() === 'GET');

test.describe('SystemPlan e2e test', () => {
  let systemPlan;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (systemPlan) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/system-plans/${systemPlan.id}`,
      });
      systemPlan = undefined;
    }
  });

  test('SystemPlans menu should load SystemPlans page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('system-plan');
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
    await expect(jhi.entity.getEntityHeading('SystemPlan')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
  });

  test.describe('SystemPlan page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(systemPlanPageUrl);
      await expect(jhi.entity.getEntityHeading('SystemPlan')).not.toContainText('lumiApp.systemPlan.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(systemPlanPageUrl);
        await response;
      });

      test('should load create SystemPlan page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${systemPlanPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('SystemPlan')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        systemPlan = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/system-plans',
            body: systemPlanSample,
          })
        ).json();

        await page.route(
          url => url.pathname === systemPlanApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/system-plans?page=0&size=20>; rel="last",<http://localhost/api/system-plans?page=0&size=20>; rel="first"',
              },
              json: [systemPlan],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(systemPlanPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details SystemPlan page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('systemPlan')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
      });

      test('edit button click should load edit SystemPlan page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('SystemPlan')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
      });

      test('edit button click should load edit SystemPlan page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('SystemPlan')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
      });

      test('last delete button click should delete instance of SystemPlan', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${systemPlanApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('systemPlan')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);

        systemPlan = undefined;
      });
    });
  });

  test.describe('new SystemPlan page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(systemPlanPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('SystemPlan')).toBeVisible();
    });

    test('should create an instance of SystemPlan', async ({ page }) => {
      await page.locator(`[data-cy="name"]`).fill('pop');
      await expect(page.locator(`[data-cy="name"]`)).toHaveValue('pop');

      await page.locator(`[data-cy="price"]`).fill('23205.66');
      await expect(page.locator(`[data-cy="price"]`)).toHaveValue('23205.66');

      await page.locator(`[data-cy="durationDays"]`).fill('21534');
      await expect(page.locator(`[data-cy="durationDays"]`)).toHaveValue('21534');

      await page.locator(`[data-cy="storageQuotaMb"]`).fill('15779');
      await expect(page.locator(`[data-cy="storageQuotaMb"]`)).toHaveValue('15779');

      await page.locator(`[data-cy="aiGenerationLimit"]`).fill('2407');
      await expect(page.locator(`[data-cy="aiGenerationLimit"]`)).toHaveValue('2407');

      await page.locator(`[data-cy="maxClasses"]`).fill('7374');
      await expect(page.locator(`[data-cy="maxClasses"]`)).toHaveValue('7374');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === systemPlanApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      systemPlan = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === systemPlanPageUrl);
    });
  });
});
