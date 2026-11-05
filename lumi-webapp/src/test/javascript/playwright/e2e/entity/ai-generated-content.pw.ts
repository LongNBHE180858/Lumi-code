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

const aiGeneratedContentPageUrl = '/ai-generated-content';
// const aiGeneratedContentSample = {"contentType":"staid","generationConfig":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","content":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","generationStatus":"ashamed whoa blah","visibility":"suburban rosin after","version":27689,"reviewStatus":"anxiously","createdAt":"2026-11-02T13:07:12.764Z","updatedAt":"2026-11-03T06:21:31.921Z"};

const aiGeneratedContentApiUrl = '/api/ai-generated-contents';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === aiGeneratedContentApiUrl && response.request().method() === 'GET');

test.describe('AiGeneratedContent e2e test', () => {
  let aiGeneratedContent;
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
        body: {"login":"HaiLong39","firstName":"Hồng Hạnh","lastName":"Hoàng","email":"ThaiNguyen93@yahoo.com","langKey":"around","imageUrl":"sweetly broadside until"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [user].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/learning-documents',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/lessons',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/courses',
      route => route.fulfill({ status: 200, json: [].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (aiGeneratedContent) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ai-generated-contents/${aiGeneratedContent.id}`,
      });
      aiGeneratedContent = undefined;
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

  test('AiGeneratedContents menu should load AiGeneratedContents page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('ai-generated-content');
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
    await expect(jhi.entity.getEntityHeading('AiGeneratedContent')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
  });

  test.describe('AiGeneratedContent page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(aiGeneratedContentPageUrl);
      await expect(jhi.entity.getEntityHeading('AiGeneratedContent')).not.toContainText('lumiApp.aiGeneratedContent.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(aiGeneratedContentPageUrl);
        await response;
      });

      test('should load create AiGeneratedContent page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${aiGeneratedContentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiGeneratedContent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        aiGeneratedContent = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/ai-generated-contents',
            body: {
              ...aiGeneratedContentSample,
              createdBy: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === aiGeneratedContentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/ai-generated-contents?page=0&size=20>; rel="last",<http://localhost/api/ai-generated-contents?page=0&size=20>; rel="first"',
              },
              json: [aiGeneratedContent],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(aiGeneratedContentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(aiGeneratedContentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing AiGeneratedContent to exercise');
      });

      test('detail button click should load details AiGeneratedContent page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('aiGeneratedContent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
      });

      test('edit button click should load edit AiGeneratedContent page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiGeneratedContent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
      });

      test('edit button click should load edit AiGeneratedContent page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AiGeneratedContent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of AiGeneratedContent', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${aiGeneratedContentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('aiGeneratedContent')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);

        aiGeneratedContent = undefined;
      });
    });
  });

  test.describe('new AiGeneratedContent page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(aiGeneratedContentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AiGeneratedContent')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of AiGeneratedContent', async ({ page }) => {
      await page.locator(`[data-cy="contentType"]`).fill('daily wing');
      await expect(page.locator(`[data-cy="contentType"]`)).toHaveValue('daily wing');

      await page.locator(`[data-cy="requestHash"]`).fill('pish what knuckle');
      await expect(page.locator(`[data-cy="requestHash"]`)).toHaveValue('pish what knuckle');

      await page.locator(`[data-cy="generationConfig"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="generationConfig"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="sourceVersion"]`).fill('merrily');
      await expect(page.locator(`[data-cy="sourceVersion"]`)).toHaveValue('merrily');

      await page.locator(`[data-cy="modelName"]`).fill('psst dishearten');
      await expect(page.locator(`[data-cy="modelName"]`)).toHaveValue('psst dishearten');

      await page.locator(`[data-cy="promptVersion"]`).fill('anneal');
      await expect(page.locator(`[data-cy="promptVersion"]`)).toHaveValue('anneal');

      await page.locator(`[data-cy="content"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="generationStatus"]`).fill('once');
      await expect(page.locator(`[data-cy="generationStatus"]`)).toHaveValue('once');

      await page.locator(`[data-cy="visibility"]`).fill('whoa supposing thoro');
      await expect(page.locator(`[data-cy="visibility"]`)).toHaveValue('whoa supposing thoro');

      await page.locator(`[data-cy="version"]`).fill('426');
      await expect(page.locator(`[data-cy="version"]`)).toHaveValue('426');

      await page.locator(`[data-cy="reviewStatus"]`).fill('merrily ew shear');
      await expect(page.locator(`[data-cy="reviewStatus"]`)).toHaveValue('merrily ew shear');

      await page.locator(`[data-cy="reviewedAt"]`).fill('2026-11-02T11:29');
      await page.locator(`[data-cy="reviewedAt"]`).blur();
      await expect(page.locator(`[data-cy="reviewedAt"]`)).toHaveValue('2026-11-02T11:29');

      await page.locator(`[data-cy="reviewerFeedback"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="reviewerFeedback"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="createdAt"]`).fill('2026-11-02T12:32');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-11-02T12:32');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-11-02T11:56');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-11-02T11:56');

      await page.locator(`[data-cy="sourceQuestionId"]`).fill('28717');
      await expect(page.locator(`[data-cy="sourceQuestionId"]`)).toHaveValue('28717');

      await page.locator(`[data-cy="contentHash"]`).fill('hmph');
      await expect(page.locator(`[data-cy="contentHash"]`)).toHaveValue('hmph');

      await page.locator(`[data-cy="createdBy"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === aiGeneratedContentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      aiGeneratedContent = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === aiGeneratedContentPageUrl);
    });
  });
});
