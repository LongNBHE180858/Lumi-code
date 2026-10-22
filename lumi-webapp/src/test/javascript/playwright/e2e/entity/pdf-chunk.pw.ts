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

const pdfChunkPageUrl = '/pdf-chunk';
// const pdfChunkSample = {"chunkText":"ethyl avow"};

const pdfChunkApiUrl = '/api/pdf-chunks';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === pdfChunkApiUrl && response.request().method() === 'GET');

test.describe('PdfChunk e2e test', () => {
  let pdfChunk;
  // let courseMaterial;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    courseMaterial = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/course-materials',
        body: {"fileType":"gah hollow hearten","fileUrl":"searchingly pluck oof","fileSizeBytes":16104},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/course-materials',
      route => route.fulfill({ status: 200, json: [courseMaterial].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (pdfChunk) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pdf-chunks/${pdfChunk.id}`,
      });
      pdfChunk = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (courseMaterial) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/course-materials/${courseMaterial.id}`,
      });
      courseMaterial = undefined;
    }
  });
   */

  test('PdfChunks menu should load PdfChunks page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('pdf-chunk');
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
    await expect(jhi.entity.getEntityHeading('PdfChunk')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
  });

  test.describe('PdfChunk page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(pdfChunkPageUrl);
      await expect(jhi.entity.getEntityHeading('PdfChunk')).not.toContainText('lumiApp.pdfChunk.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(pdfChunkPageUrl);
        await response;
      });

      test('should load create PdfChunk page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${pdfChunkPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('PdfChunk')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        pdfChunk = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/pdf-chunks',
            body: {
              ...pdfChunkSample,
              material: courseMaterial,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === pdfChunkApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [pdfChunk],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(pdfChunkPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(pdfChunkPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing PdfChunk to exercise');
      });

      test('detail button click should load details PdfChunk page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('pdfChunk')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
      });

      test('edit button click should load edit PdfChunk page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('PdfChunk')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
      });

      test('edit button click should load edit PdfChunk page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('PdfChunk')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of PdfChunk', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${pdfChunkApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('pdfChunk')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);

        pdfChunk = undefined;
      });
    });
  });

  test.describe('new PdfChunk page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(pdfChunkPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('PdfChunk')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of PdfChunk', async ({ page }) => {
      await page.locator(`[data-cy="pageNumber"]`).fill('32529');
      await expect(page.locator(`[data-cy="pageNumber"]`)).toHaveValue('32529');

      await page.locator(`[data-cy="chunkText"]`).fill('from');
      await expect(page.locator(`[data-cy="chunkText"]`)).toHaveValue('from');

      await page.locator(`[data-cy="embedding"]`).fill('offensively');
      await expect(page.locator(`[data-cy="embedding"]`)).toHaveValue('offensively');

      await page.locator(`[data-cy="material"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === pdfChunkApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      pdfChunk = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === pdfChunkPageUrl);
    });
  });
});
