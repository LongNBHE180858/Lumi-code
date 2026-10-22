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

const assignmentPageUrl = '/assignment';
// const assignmentSample = {"title":"likely ick"};

const assignmentApiUrl = '/api/assignments';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === assignmentApiUrl && response.request().method() === 'GET');

test.describe('Assignment e2e test', () => {
  let assignment;
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
        body: {"name":"after truly interestingly","description":"ack","joinCode":"victorious","joinMode":"CODE_APPROVAL","maxStudents":14731,"currentStudents":7751,"status":"ARCHIVED","isDeleted":true,"createdAt":"2026-10-12T18:15:27.107Z"},
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
    if (assignment) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assignments/${assignment.id}`,
      });
      assignment = undefined;
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

  test('Assignments menu should load Assignments page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('assignment');
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
    await expect(jhi.entity.getEntityHeading('Assignment')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
  });

  test.describe('Assignment page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(assignmentPageUrl);
      await expect(jhi.entity.getEntityHeading('Assignment')).not.toContainText('lumiApp.assignment.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentPageUrl);
        await response;
      });

      test('should load create Assignment page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${assignmentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Assignment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        assignment = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/assignments',
            body: {
              ...assignmentSample,
              studyClass: studyClass,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === assignmentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/assignments?page=0&size=20>; rel="last",<http://localhost/api/assignments?page=0&size=20>; rel="first"',
              },
              json: [assignment],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(assignmentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Assignment to exercise');
      });

      test('detail button click should load details Assignment page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('assignment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
      });

      test('edit button click should load edit Assignment page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Assignment')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
      });

      test('edit button click should load edit Assignment page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Assignment')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Assignment', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${assignmentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('assignment')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);

        assignment = undefined;
      });
    });
  });

  test.describe('new Assignment page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(assignmentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Assignment')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Assignment', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('so blacken');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('so blacken');

      await page.locator(`[data-cy="instructions"]`).fill('low');
      await expect(page.locator(`[data-cy="instructions"]`)).toHaveValue('low');

      await page.locator(`[data-cy="dueAt"]`).fill('2026-10-12T17:13');
      await page.locator(`[data-cy="dueAt"]`).blur();
      await expect(page.locator(`[data-cy="dueAt"]`)).toHaveValue('2026-10-12T17:13');

      await page.locator(`[data-cy="maxScore"]`).fill('18134.25');
      await expect(page.locator(`[data-cy="maxScore"]`)).toHaveValue('18134.25');

      await expect(page.locator(`[data-cy="allowLate"]`)).not.toBeChecked();
      await page.locator(`[data-cy="allowLate"]`).click();
      await expect(page.locator(`[data-cy="allowLate"]`)).toBeChecked();

      await expect(page.locator(`[data-cy="isDeleted"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isDeleted"]`).click();
      await expect(page.locator(`[data-cy="isDeleted"]`)).toBeChecked();

      await page.locator(`[data-cy="studyClass"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === assignmentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      assignment = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === assignmentPageUrl);
    });
  });
});
