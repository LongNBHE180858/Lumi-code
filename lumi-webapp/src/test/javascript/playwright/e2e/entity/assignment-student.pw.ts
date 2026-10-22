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

const assignmentStudentPageUrl = '/assignment-student';
// const assignmentStudentSample = {};

const assignmentStudentApiUrl = '/api/assignment-students';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === assignmentStudentApiUrl && response.request().method() === 'GET');

test.describe('AssignmentStudent e2e test', () => {
  let assignmentStudent;
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
        body: {"login":"ThuHue21","firstName":"Việt Hồng","lastName":"Đặng","email":"YBinh.Nguyen87@yahoo.com","langKey":"if","imageUrl":"drat following"},
      })
    ).json();
    // create an instance at the required relationship entity:
    assignment = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/assignments',
        body: {"title":"surprisingly warmly disposer","instructions":"ick unless","dueAt":"2026-10-12T18:22:32.224Z","maxScore":12618.95,"allowLate":false,"isDeleted":false},
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
    if (assignmentStudent) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assignment-students/${assignmentStudent.id}`,
      });
      assignmentStudent = undefined;
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

  test('AssignmentStudents menu should load AssignmentStudents page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('assignment-student');
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
    await expect(jhi.entity.getEntityHeading('AssignmentStudent')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
  });

  test.describe('AssignmentStudent page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(assignmentStudentPageUrl);
      await expect(jhi.entity.getEntityHeading('AssignmentStudent')).not.toContainText('lumiApp.assignmentStudent.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentStudentPageUrl);
        await response;
      });

      test('should load create AssignmentStudent page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${assignmentStudentPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentStudent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        assignmentStudent = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/assignment-students',
            body: {
              ...assignmentStudentSample,
              student: user,
              assignment: assignment,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === assignmentStudentApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/assignment-students?page=0&size=20>; rel="last",<http://localhost/api/assignment-students?page=0&size=20>; rel="first"',
              },
              json: [assignmentStudent],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(assignmentStudentPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(assignmentStudentPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing AssignmentStudent to exercise');
      });

      test('detail button click should load details AssignmentStudent page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('assignmentStudent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
      });

      test('edit button click should load edit AssignmentStudent page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentStudent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
      });

      test('edit button click should load edit AssignmentStudent page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentStudent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of AssignmentStudent', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${assignmentStudentApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('assignmentStudent')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);

        assignmentStudent = undefined;
      });
    });
  });

  test.describe('new AssignmentStudent page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(assignmentStudentPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('AssignmentStudent')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of AssignmentStudent', async ({ page }) => {
      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="assignment"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === assignmentStudentApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      assignmentStudent = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === assignmentStudentPageUrl);
    });
  });
});
