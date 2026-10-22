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

const assignmentSubmissionPageUrl = '/assignment-submission';
// const assignmentSubmissionSample = {};

const assignmentSubmissionApiUrl = '/api/assignment-submissions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(
    response => new URL(response.url()).pathname === assignmentSubmissionApiUrl && response.request().method() === 'GET',
  );

test.describe('AssignmentSubmission e2e test', () => {
  let assignmentSubmission;
  // let user;
  // let assignment;

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
        body: {"login":"UyVu_7ko53","firstName":"Quốc Thiện","lastName":"Phan","email":"AnNguyen_Truong60@gmail.com","langKey":"regarding ","imageUrl":"thankfully ha"},
      })
    ).json();
    // create an instance at the required relationship entity:
    assignment = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/assignments',
        body: {"title":"telescope","instructions":"deform","dueAt":"2026-10-12T00:03:28.920Z","maxScore":1818.95,"allowLate":false,"isDeleted":true},
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
      url => url.pathname === '/api/assignments',
      route => route.fulfill({ status: 200, json: [assignment].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (assignmentSubmission) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assignment-submissions/${assignmentSubmission.id}`,
      });
      assignmentSubmission = undefined;
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
    if (assignment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assignments/${assignment.id}`,
      });
      assignment = undefined;
    }
  });
   */

  test('AssignmentSubmissions menu should load AssignmentSubmissions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('assignment-submission');
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
    await expect(jhi.entity.getEntityHeading('AssignmentSubmission')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
  });

  test.describe('AssignmentSubmission page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(assignmentSubmissionPageUrl);
      await expect(jhi.entity.getEntityHeading('AssignmentSubmission')).not.toContainText('lumiApp.assignmentSubmission.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentSubmissionPageUrl);
        await response;
      });

      test('should load create AssignmentSubmission page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${assignmentSubmissionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentSubmission')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        assignmentSubmission = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/assignment-submissions',
            body: {
              ...assignmentSubmissionSample,
              student: user,
              assignment: assignment,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === assignmentSubmissionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/assignment-submissions?page=0&size=20>; rel="last",<http://localhost/api/assignment-submissions?page=0&size=20>; rel="first"',
              },
              json: [assignmentSubmission],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(assignmentSubmissionPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentSubmissionPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing AssignmentSubmission to exercise');
      });

      test('detail button click should load details AssignmentSubmission page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('assignmentSubmission')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
      });

      test('edit button click should load edit AssignmentSubmission page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentSubmission')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
      });

      test('edit button click should load edit AssignmentSubmission page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentSubmission')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of AssignmentSubmission', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${assignmentSubmissionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('assignmentSubmission')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);

        assignmentSubmission = undefined;
      });
    });
  });

  test.describe('new AssignmentSubmission page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(assignmentSubmissionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentSubmission')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of AssignmentSubmission', async ({ page }) => {
      await page.locator(`[data-cy="content"]`).fill('qua yet positively');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue('qua yet positively');

      await page.locator(`[data-cy="score"]`).fill('27578.51');
      await expect(page.locator(`[data-cy="score"]`)).toHaveValue('27578.51');

      await page.locator(`[data-cy="teacherFeedback"]`).fill('yippee switch');
      await expect(page.locator(`[data-cy="teacherFeedback"]`)).toHaveValue('yippee switch');

      await page.locator(`[data-cy="status"]`).fill('tender');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('tender');

      await page.locator(`[data-cy="submittedAt"]`).fill('2026-10-12T11:59');
      await page.locator(`[data-cy="submittedAt"]`).blur();
      await expect(page.locator(`[data-cy="submittedAt"]`)).toHaveValue('2026-10-12T11:59');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="assignment"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === assignmentSubmissionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      assignmentSubmission = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === assignmentSubmissionPageUrl);
    });
  });
});
