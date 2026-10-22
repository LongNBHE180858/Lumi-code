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

const studentNotePageUrl = '/student-note';
// const studentNoteSample = {"content":"certainly finding"};

const studentNoteApiUrl = '/api/student-notes';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === studentNoteApiUrl && response.request().method() === 'GET');

test.describe('StudentNote e2e test', () => {
  let studentNote;
  // let user;
  // let lesson;

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
        body: {"login":"PhuongHoa_Mai20","firstName":"Khả Ái","lastName":"Lý","email":"LeQuynh_Pham54@hotmail.com","langKey":"airbrush","imageUrl":"so"},
      })
    ).json();
    // create an instance at the required relationship entity:
    lesson = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/lessons',
        body: {"title":"speedily ack","contentType":"bashfully lieu volle","orderIndex":26985,"isPreview":true,"durationMinutes":18743,"status":"ARCHIVED"},
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
      url => url.pathname === '/api/lessons',
      route => route.fulfill({ status: 200, json: [lesson].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (studentNote) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/student-notes/${studentNote.id}`,
      });
      studentNote = undefined;
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
    if (lesson) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/lessons/${lesson.id}`,
      });
      lesson = undefined;
    }
  });
   */

  test('StudentNotes menu should load StudentNotes page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('student-note');
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
    await expect(jhi.entity.getEntityHeading('StudentNote')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
  });

  test.describe('StudentNote page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(studentNotePageUrl);
      await expect(jhi.entity.getEntityHeading('StudentNote')).not.toContainText('lumiApp.studentNote.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentNotePageUrl);
        await response;
      });

      test('should load create StudentNote page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${studentNotePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentNote')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        studentNote = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/student-notes',
            body: {
              ...studentNoteSample,
              student: user,
              lesson: lesson,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === studentNoteApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/student-notes?page=0&size=20>; rel="last",<http://localhost/api/student-notes?page=0&size=20>; rel="first"',
              },
              json: [studentNote],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(studentNotePageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentNotePageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing StudentNote to exercise');
      });

      test('detail button click should load details StudentNote page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('studentNote')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
      });

      test('edit button click should load edit StudentNote page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentNote')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
      });

      test('edit button click should load edit StudentNote page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentNote')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of StudentNote', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${studentNoteApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('studentNote')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);

        studentNote = undefined;
      });
    });
  });

  test.describe('new StudentNote page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(studentNotePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('StudentNote')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of StudentNote', async ({ page }) => {
      await page.locator(`[data-cy="videoTimestamp"]`).fill('4345');
      await expect(page.locator(`[data-cy="videoTimestamp"]`)).toHaveValue('4345');

      await page.locator(`[data-cy="content"]`).fill('although');
      await expect(page.locator(`[data-cy="content"]`)).toHaveValue('although');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T11:11');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T11:11');

      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="lesson"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === studentNoteApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      studentNote = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === studentNotePageUrl);
    });
  });
});
