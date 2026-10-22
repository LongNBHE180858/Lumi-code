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

const chapterPageUrl = '/chapter';
// const chapterSample = {"title":"bonnet spattering concentration","orderIndex":17851};

const chapterApiUrl = '/api/chapters';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === chapterApiUrl && response.request().method() === 'GET');

test.describe('Chapter e2e test', () => {
  let chapter;
  // let course;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    course = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/courses',
        body: {"title":"shameful","description":"academics","thumbnailUrl":"whenever woot","price":8415.15,"validDurationDays":17116,"status":"DRAFT","isDeleted":false,"createdAt":"2026-10-12T16:58:55.957Z","updatedAt":"2026-10-12T13:55:55.766Z"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/courses',
      route => route.fulfill({ status: 200, json: [course].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (chapter) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/chapters/${chapter.id}`,
      });
      chapter = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (course) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/courses/${course.id}`,
      });
      course = undefined;
    }
  });
   */

  test('Chapters menu should load Chapters page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('chapter');
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
    await expect(jhi.entity.getEntityHeading('Chapter')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
  });

  test.describe('Chapter page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(chapterPageUrl);
      await expect(jhi.entity.getEntityHeading('Chapter')).not.toContainText('lumiApp.chapter.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(chapterPageUrl);
        await response;
      });

      test('should load create Chapter page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${chapterPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Chapter')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        chapter = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/chapters',
            body: {
              ...chapterSample,
              course: course,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === chapterApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/chapters?page=0&size=20>; rel="last",<http://localhost/api/chapters?page=0&size=20>; rel="first"',
              },
              json: [chapter],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(chapterPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(chapterPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Chapter to exercise');
      });

      test('detail button click should load details Chapter page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('chapter')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
      });

      test('edit button click should load edit Chapter page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Chapter')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
      });

      test('edit button click should load edit Chapter page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Chapter')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Chapter', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${chapterApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('chapter')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);

        chapter = undefined;
      });
    });
  });

  test.describe('new Chapter page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(chapterPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Chapter')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Chapter', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('athwart teriyaki afterwards');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('athwart teriyaki afterwards');

      await page.locator(`[data-cy="orderIndex"]`).fill('16947');
      await expect(page.locator(`[data-cy="orderIndex"]`)).toHaveValue('16947');

      await page.locator(`[data-cy="course"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === chapterApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      chapter = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === chapterPageUrl);
    });
  });
});
