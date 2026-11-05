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

const studentKnowledgeProgressPageUrl = '/student-knowledge-progress';
// const studentKnowledgeProgressSample = {"evidenceCount":18198,"updatedAt":"2026-11-03T02:34:30.714Z"};

const studentKnowledgeProgressApiUrl = '/api/student-knowledge-progresses';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(
    response => new URL(response.url()).pathname === studentKnowledgeProgressApiUrl && response.request().method() === 'GET',
  );

test.describe('StudentKnowledgeProgress e2e test', () => {
  let studentKnowledgeProgress;
  // let user;
  // let knowledgeNode;

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
        body: {"login":"TinhTu_Mai62","firstName":"Quang Lộc","lastName":"Đặng","email":"XuanHien55@yahoo.com","langKey":"never","imageUrl":"lest"},
      })
    ).json();
    // create an instance at the required relationship entity:
    knowledgeNode = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/knowledge-nodes',
        body: {"title":"valiantly","type":"rudely"},
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
      url => url.pathname === '/api/knowledge-nodes',
      route => route.fulfill({ status: 200, json: [knowledgeNode].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (studentKnowledgeProgress) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/student-knowledge-progresses/${studentKnowledgeProgress.id}`,
      });
      studentKnowledgeProgress = undefined;
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
    if (knowledgeNode) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/knowledge-nodes/${knowledgeNode.id}`,
      });
      knowledgeNode = undefined;
    }
  });
   */

  test('StudentKnowledgeProgresses menu should load StudentKnowledgeProgresses page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('student-knowledge-progress');
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
    await expect(jhi.entity.getEntityHeading('StudentKnowledgeProgress')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
  });

  test.describe('StudentKnowledgeProgress page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(studentKnowledgeProgressPageUrl);
      await expect(jhi.entity.getEntityHeading('StudentKnowledgeProgress')).not.toContainText(
        'lumiApp.studentKnowledgeProgress.home.title',
      );
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentKnowledgeProgressPageUrl);
        await response;
      });

      test('should load create StudentKnowledgeProgress page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${studentKnowledgeProgressPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentKnowledgeProgress')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        studentKnowledgeProgress = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/student-knowledge-progresses',
            body: {
              ...studentKnowledgeProgressSample,
              student: user,
              knowledgeNode: knowledgeNode,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === studentKnowledgeProgressApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/student-knowledge-progresses?page=0&size=20>; rel="last",<http://localhost/api/student-knowledge-progresses?page=0&size=20>; rel="first"',
              },
              json: [studentKnowledgeProgress],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(studentKnowledgeProgressPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentKnowledgeProgressPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing StudentKnowledgeProgress to exercise');
      });

      test('detail button click should load details StudentKnowledgeProgress page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('studentKnowledgeProgress')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
      });

      test('edit button click should load edit StudentKnowledgeProgress page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentKnowledgeProgress')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
      });

      test('edit button click should load edit StudentKnowledgeProgress page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentKnowledgeProgress')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of StudentKnowledgeProgress', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${studentKnowledgeProgressApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('studentKnowledgeProgress')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);

        studentKnowledgeProgress = undefined;
      });
    });
  });

  test.describe('new StudentKnowledgeProgress page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(studentKnowledgeProgressPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('StudentKnowledgeProgress')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of StudentKnowledgeProgress', async ({ page }) => {
      await page.locator(`[data-cy="masteryScore"]`).fill('10.49');
      await expect(page.locator(`[data-cy="masteryScore"]`)).toHaveValue('10.49');

      await page.locator(`[data-cy="evidenceCount"]`).fill('13493');
      await expect(page.locator(`[data-cy="evidenceCount"]`)).toHaveValue('13493');

      await page.locator(`[data-cy="calculationVersion"]`).fill('athwart now');
      await expect(page.locator(`[data-cy="calculationVersion"]`)).toHaveValue('athwart now');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-11-02T22:58');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-11-02T22:58');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="knowledgeNode"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === studentKnowledgeProgressApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      studentKnowledgeProgress = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === studentKnowledgeProgressPageUrl);
    });
  });
});
