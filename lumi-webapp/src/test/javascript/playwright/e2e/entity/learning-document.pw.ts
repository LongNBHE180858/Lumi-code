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

const learningDocumentPageUrl = '/learning-document';
// const learningDocumentSample = {"sourceType":"legend","title":"formal hence","fileObjectKey":"lovingly","sourceVersion":3721,"processingStatus":"politely irritably","indexStatus":"unfortunate inhibit ","createdAt":"2026-11-03T00:37:59.366Z","updatedAt":"2026-11-03T07:38:08.690Z","sourceMetadata":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="};

const learningDocumentApiUrl = '/api/learning-documents';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === learningDocumentApiUrl && response.request().method() === 'GET');

test.describe('LearningDocument e2e test', () => {
  let learningDocument;
  // let user;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    user = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/users',
        body: {"login":"XuanHoa.Bui83","firstName":"Huy Việt","lastName":"Mai","email":"CongHau.To30@gmail.com","langKey":"settler","imageUrl":"and aw"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/course-materials',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/class-materials',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [user].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/subjects',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/grades',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (learningDocument) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/learning-documents/${learningDocument.id}`,
      });
      learningDocument = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (user) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      });
      user = undefined;
    }
  });
   */

  test('LearningDocuments menu should load LearningDocuments page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('learning-document');
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
    await expect(jhi.entity.getEntityHeading('LearningDocument')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
  });

  test.describe('LearningDocument page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(learningDocumentPageUrl);
      await expect(jhi.entity.getEntityHeading('LearningDocument')).not.toContainText('lumiApp.learningDocument.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(learningDocumentPageUrl);
        await response;
      });

      test('should load create LearningDocument page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${learningDocumentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('LearningDocument')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        learningDocument = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/learning-documents',
            body: {
              ...learningDocumentSample,
              uploadedBy: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === learningDocumentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/learning-documents?page=0&size=20>; rel="last",<http://localhost/api/learning-documents?page=0&size=20>; rel="first"',
              },
              json: [learningDocument],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(learningDocumentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(learningDocumentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing LearningDocument to exercise');
      });

      test('detail button click should load details LearningDocument page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('learningDocument')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
      });

      test('edit button click should load edit LearningDocument page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('LearningDocument')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
      });

      test('edit button click should load edit LearningDocument page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('LearningDocument')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of LearningDocument', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${learningDocumentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('learningDocument')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);

        learningDocument = undefined;
      });
    });
  });

  test.describe('new LearningDocument page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(learningDocumentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('LearningDocument')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of LearningDocument', async ({ page }) => {
      await page.locator(`[data-cy="sourceType"]`).fill('uh-huh though among');
      await expect(page.locator(`[data-cy="sourceType"]`)).toHaveValue('uh-huh though among');

      await page.locator(`[data-cy="title"]`).fill('mom');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('mom');

      await page.locator(`[data-cy="fileObjectKey"]`).fill('after yahoo gah');
      await expect(page.locator(`[data-cy="fileObjectKey"]`)).toHaveValue('after yahoo gah');

      await page.locator(`[data-cy="contentHash"]`).fill('able');
      await expect(page.locator(`[data-cy="contentHash"]`)).toHaveValue('able');

      await page.locator(`[data-cy="sourceVersion"]`).fill('31240');
      await expect(page.locator(`[data-cy="sourceVersion"]`)).toHaveValue('31240');

      await page.locator(`[data-cy="processingStatus"]`).fill('dissemble what gust');
      await expect(page.locator(`[data-cy="processingStatus"]`)).toHaveValue('dissemble what gust');

      await page.locator(`[data-cy="indexStatus"]`).fill('happy-go-lucky');
      await expect(page.locator(`[data-cy="indexStatus"]`)).toHaveValue('happy-go-lucky');

      await page.locator(`[data-cy="extractedTextObjectKey"]`).fill('indeed yesterday merge');
      await expect(page.locator(`[data-cy="extractedTextObjectKey"]`)).toHaveValue('indeed yesterday merge');

      await page.locator(`[data-cy="embeddingModel"]`).fill('tomorrow');
      await expect(page.locator(`[data-cy="embeddingModel"]`)).toHaveValue('tomorrow');

      await page.locator(`[data-cy="errorMessage"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="errorMessage"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="createdAt"]`).fill('2026-11-03T06:40');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-11-03T06:40');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-11-02T11:33');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-11-02T11:33');

      await page.locator(`[data-cy="bookSeries"]`).fill('jumbo fatally');
      await expect(page.locator(`[data-cy="bookSeries"]`)).toHaveValue('jumbo fatally');

      await page.locator(`[data-cy="sourceMetadata"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="sourceMetadata"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="uploadedBy"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === learningDocumentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      learningDocument = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === learningDocumentPageUrl);
    });
  });
});
