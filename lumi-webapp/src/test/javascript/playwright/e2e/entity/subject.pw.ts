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

const subjectPageUrl = '/subject';
const subjectSample = { name: 'cautiously duh' };

const subjectApiUrl = '/api/subjects';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === subjectApiUrl && response.request().method() === 'GET');

test.describe('Subject e2e test', () => {
  let subject;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  test.afterEach(async ({ jhi }) => {
    if (subject) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/subjects/${subject.id}`,
      });
      subject = undefined;
    }
  });

  test('Subjects menu should load Subjects page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('subject');
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
    await expect(jhi.entity.getEntityHeading('Subject')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
  });

  test.describe('Subject page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(subjectPageUrl);
      await expect(jhi.entity.getEntityHeading('Subject')).not.toContainText('lumiApp.subject.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(subjectPageUrl);
        await response;
      });

      test('should load create Subject page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${subjectPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Subject')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
      });
    });

    test.describe('with existing value', () => {
      test.beforeEach(async ({ page, jhi }) => {
        subject = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/subjects',
            body: subjectSample,
          })
        ).json();

        await page.route(
          url => url.pathname === subjectApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/subjects?page=0&size=20>; rel="last",<http://localhost/api/subjects?page=0&size=20>; rel="first"',
              },
              json: [subject],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(subjectPageUrl);
        await stubbedRequest;
      });

      test('detail button click should load details Subject page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('subject')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
      });

      test('edit button click should load edit Subject page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Subject')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
      });

      test('edit button click should load edit Subject page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Subject')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
      });

      test('last delete button click should delete instance of Subject', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${subjectApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('subject')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);

        subject = undefined;
      });
    });
  });

  test.describe('new Subject page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(subjectPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Subject')).toBeVisible();
    });

    test('should create an instance of Subject', async ({ page }) => {
      await page.locator(`[data-cy="name"]`).fill('pine ew');
      await expect(page.locator(`[data-cy="name"]`)).toHaveValue('pine ew');

      await page.locator(`[data-cy="description"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="description"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === subjectApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      subject = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === subjectPageUrl);
    });
  });
});
