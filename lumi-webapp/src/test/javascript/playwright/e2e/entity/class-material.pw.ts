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

const classMaterialPageUrl = '/class-material';
// const classMaterialSample = {"title":"readily task untried","fileType":"illiterate reckless","fileUrl":"standard aside towards","fileSizeBytes":5372};

const classMaterialApiUrl = '/api/class-materials';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === classMaterialApiUrl && response.request().method() === 'GET');

test.describe('ClassMaterial e2e test', () => {
  let classMaterial;
  // let studyClass;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    studyClass = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/study-classes',
        body: {"name":"save","description":"amid hence","joinCode":"substantia","joinMode":"INVITE_ONLY","maxStudents":1225,"currentStudents":28819,"status":"ACTIVE","isDeleted":false,"createdAt":"2026-10-12T03:47:15.251Z"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/study-classes',
      route => route.fulfill({ status: 200, json: [studyClass].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (classMaterial) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/class-materials/${classMaterial.id}`,
      });
      classMaterial = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (studyClass) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/study-classes/${studyClass.id}`,
      });
      studyClass = undefined;
    }
  });
   */

  test('ClassMaterials menu should load ClassMaterials page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('class-material');
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
    await expect(jhi.entity.getEntityHeading('ClassMaterial')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
  });

  test.describe('ClassMaterial page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(classMaterialPageUrl);
      await expect(jhi.entity.getEntityHeading('ClassMaterial')).not.toContainText('lumiApp.classMaterial.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(classMaterialPageUrl);
        await response;
      });

      test('should load create ClassMaterial page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${classMaterialPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMaterial')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        classMaterial = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/class-materials',
            body: {
              ...classMaterialSample,
              studyClass: studyClass,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === classMaterialApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/class-materials?page=0&size=20>; rel="last",<http://localhost/api/class-materials?page=0&size=20>; rel="first"',
              },
              json: [classMaterial],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(classMaterialPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(classMaterialPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ClassMaterial to exercise');
      });

      test('detail button click should load details ClassMaterial page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('classMaterial')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
      });

      test('edit button click should load edit ClassMaterial page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMaterial')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
      });

      test('edit button click should load edit ClassMaterial page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMaterial')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ClassMaterial', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${classMaterialApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('classMaterial')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);

        classMaterial = undefined;
      });
    });
  });

  test.describe('new ClassMaterial page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(classMaterialPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMaterial')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ClassMaterial', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('ugh who bonfire');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('ugh who bonfire');

      await page.locator(`[data-cy="fileType"]`).fill('frail of because');
      await expect(page.locator(`[data-cy="fileType"]`)).toHaveValue('frail of because');

      await page.locator(`[data-cy="fileUrl"]`).fill('apropos thorny');
      await expect(page.locator(`[data-cy="fileUrl"]`)).toHaveValue('apropos thorny');

      await page.locator(`[data-cy="fileSizeBytes"]`).fill('25368');
      await expect(page.locator(`[data-cy="fileSizeBytes"]`)).toHaveValue('25368');

      await page.locator(`[data-cy="uploadedAt"]`).fill('2026-10-12T05:02');
      await page.locator(`[data-cy="uploadedAt"]`).blur();
      await expect(page.locator(`[data-cy="uploadedAt"]`)).toHaveValue('2026-10-12T05:02');

      await page.locator(`[data-cy="studyClass"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === classMaterialApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      classMaterial = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === classMaterialPageUrl);
    });
  });
});
