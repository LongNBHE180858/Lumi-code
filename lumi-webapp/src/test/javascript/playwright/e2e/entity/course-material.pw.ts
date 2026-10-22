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

const courseMaterialPageUrl = '/course-material';
// const courseMaterialSample = {"fileType":"omelet joyfully","fileUrl":"gifted"};

const courseMaterialApiUrl = '/api/course-materials';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseMaterialApiUrl && response.request().method() === 'GET');

test.describe('CourseMaterial e2e test', () => {
  let courseMaterial;
  // let lesson;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    lesson = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/lessons',
        body: {"title":"simplistic","contentType":"after knickers","orderIndex":28230,"isPreview":true,"durationMinutes":23882,"status":"DRAFT"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/lessons',
      route => route.fulfill({ status: 200, json: [lesson].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (courseMaterial) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-materials/${courseMaterial.id}`,
      });
      courseMaterial = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (lesson) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/lessons/${lesson.id}`,
      });
      lesson = undefined;
    }
  });
   */

  test('CourseMaterials menu should load CourseMaterials page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course-material');
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
    await expect(jhi.entity.getEntityHeading('CourseMaterial')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
  });

  test.describe('CourseMaterial page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(courseMaterialPageUrl);
      await expect(jhi.entity.getEntityHeading('CourseMaterial')).not.toContainText('lumiApp.courseMaterial.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseMaterialPageUrl);
        await response;
      });

      test('should load create CourseMaterial page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${courseMaterialPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseMaterial')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        courseMaterial = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/course-materials',
            body: {
              ...courseMaterialSample,
              lesson: lesson,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseMaterialApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/course-materials?page=0&size=20>; rel="last",<http://localhost/api/course-materials?page=0&size=20>; rel="first"',
              },
              json: [courseMaterial],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(courseMaterialPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(courseMaterialPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing CourseMaterial to exercise');
      });

      test('detail button click should load details CourseMaterial page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('courseMaterial')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
      });

      test('edit button click should load edit CourseMaterial page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseMaterial')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
      });

      test('edit button click should load edit CourseMaterial page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('CourseMaterial')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of CourseMaterial', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${courseMaterialApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('courseMaterial')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);

        courseMaterial = undefined;
      });
    });
  });

  test.describe('new CourseMaterial page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(courseMaterialPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('CourseMaterial')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of CourseMaterial', async ({ page }) => {
      await page.locator(`[data-cy="fileType"]`).fill('lieu ew malfunction');
      await expect(page.locator(`[data-cy="fileType"]`)).toHaveValue('lieu ew malfunction');

      await page.locator(`[data-cy="fileUrl"]`).fill('recklessly fishery');
      await expect(page.locator(`[data-cy="fileUrl"]`)).toHaveValue('recklessly fishery');

      await page.locator(`[data-cy="fileSizeBytes"]`).fill('2615');
      await expect(page.locator(`[data-cy="fileSizeBytes"]`)).toHaveValue('2615');

      await page.locator(`[data-cy="lesson"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseMaterialApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      courseMaterial = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === courseMaterialPageUrl);
    });
  });
});
