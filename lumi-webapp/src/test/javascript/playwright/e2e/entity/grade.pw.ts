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

const gradePageUrl = '/grade';
const gradeSample = { level: 1, name: 'cardboard furthermore' };

const gradeApiUrl = '/api/grades';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === gradeApiUrl && response.request().method() === 'GET');

test.describe('Grade e2e test', () => {
  let grade;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (grade) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/grades/${grade.id}`,
      });
      grade = undefined;
    }
  });

  test('Grades menu should load Grades page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('grade');
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
    await expect(jhi.entity.getEntityHeading('Grade')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
  });

  test.describe('Grade page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(gradePageUrl);
      await expect(jhi.entity.getEntityHeading('Grade')).not.toContainText('lumiApp.grade.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(gradePageUrl);
        await response;
      });

      test('should load create Grade page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${gradePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Grade')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        grade = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/grades',
            body: gradeSample,
          })
        ).json();

        await page.route(
          url => url.pathname === gradeApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/grades?page=0&size=20>; rel="last",<http://localhost/api/grades?page=0&size=20>; rel="first"',
              },
              json: [grade],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(gradePageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Grade page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('grade')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
      });

      test('edit button click should load edit Grade page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Grade')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
      });

      test('edit button click should load edit Grade page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Grade')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
      });

      test('last delete button click should delete instance of Grade', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${gradeApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('grade')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === gradePageUrl);

        grade = undefined;
      });
    });
  });

  test.describe('new Grade page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(gradePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Grade')).toBeVisible();
    });

    test('should create an instance of Grade', async ({ page }) => {
      await page.locator(`[data-cy="level"]`).fill('6');
      await expect(page.locator(`[data-cy="level"]`)).toHaveValue('6');

      await page.locator(`[data-cy="name"]`).fill('shabby rawhide');
      await expect(page.locator(`[data-cy="name"]`)).toHaveValue('shabby rawhide');

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === gradeApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      grade = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === gradePageUrl);
    });
  });
});
