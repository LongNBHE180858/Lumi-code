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

const lessonPageUrl = '/lesson';
// const lessonSample = {"title":"nor confide","contentType":"consequently","orderIndex":24721};

const lessonApiUrl = '/api/lessons';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === lessonApiUrl && response.request().method() === 'GET');

test.describe('Lesson e2e test', () => {
  let lesson;
  // let chapter;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    chapter = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/chapters',
        body: {"title":"psst petty","orderIndex":28900},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/chapters',
      route => route.fulfill({ status: 200, json: [chapter].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (lesson) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/lessons/${lesson.id}`,
      });
      lesson = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (chapter) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/chapters/${chapter.id}`,
      });
      chapter = undefined;
    }
  });
   */

  test('Lessons menu should load Lessons page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('lesson');
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
    await expect(jhi.entity.getEntityHeading('Lesson')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
  });

  test.describe('Lesson page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(lessonPageUrl);
      await expect(jhi.entity.getEntityHeading('Lesson')).not.toContainText('lumiApp.lesson.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(lessonPageUrl);
        await response;
      });

      test('should load create Lesson page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${lessonPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Lesson')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        lesson = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/lessons',
            body: {
              ...lessonSample,
              chapter: chapter,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === lessonApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/lessons?page=0&size=20>; rel="last",<http://localhost/api/lessons?page=0&size=20>; rel="first"',
              },
              json: [lesson],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(lessonPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(lessonPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Lesson to exercise');
      });

      test('detail button click should load details Lesson page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('lesson')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
      });

      test('edit button click should load edit Lesson page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Lesson')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
      });

      test('edit button click should load edit Lesson page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Lesson')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Lesson', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${lessonApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('lesson')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);

        lesson = undefined;
      });
    });
  });

  test.describe('new Lesson page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(lessonPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Lesson')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Lesson', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('swing cheerfully');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('swing cheerfully');

      await page.locator(`[data-cy="contentType"]`).fill('whether towards');
      await expect(page.locator(`[data-cy="contentType"]`)).toHaveValue('whether towards');

      await page.locator(`[data-cy="orderIndex"]`).fill('32583');
      await expect(page.locator(`[data-cy="orderIndex"]`)).toHaveValue('32583');

      await expect(page.locator(`[data-cy="isPreview"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isPreview"]`).click();
      await expect(page.locator(`[data-cy="isPreview"]`)).toBeChecked();

      await page.locator(`[data-cy="durationMinutes"]`).fill('20337');
      await expect(page.locator(`[data-cy="durationMinutes"]`)).toHaveValue('20337');

      await page.locator(`[data-cy="status"]`).selectOption('HIDDEN');

      await page.locator(`[data-cy="chapter"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === lessonApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      lesson = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === lessonPageUrl);
    });
  });
});
