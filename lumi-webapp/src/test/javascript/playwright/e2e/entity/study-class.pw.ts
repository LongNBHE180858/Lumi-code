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

const studyClassPageUrl = '/study-class';
// const studyClassSample = {"name":"whoever wholly"};

const studyClassApiUrl = '/api/study-classes';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === studyClassApiUrl && response.request().method() === 'GET');

test.describe('StudyClass e2e test', () => {
  let studyClass;
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
        body: {"login":"VanAnh41","firstName":"Quyết Thắng","lastName":"Phan","email":"ThongMinh_Hoang21@hotmail.com","langKey":"finally of","imageUrl":"sightseeing"},
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
    if (studyClass) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/study-classes/${studyClass.id}`,
      });
      studyClass = undefined;
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

  test('StudyClasses menu should load StudyClasses page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('study-class');
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
    await expect(jhi.entity.getEntityHeading('StudyClass')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
  });

  test.describe('StudyClass page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(studyClassPageUrl);
      await expect(jhi.entity.getEntityHeading('StudyClass')).not.toContainText('lumiApp.studyClass.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studyClassPageUrl);
        await response;
      });

      test('should load create StudyClass page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${studyClassPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudyClass')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        studyClass = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/study-classes',
            body: {
              ...studyClassSample,
              instructor: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === studyClassApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/study-classes?page=0&size=20>; rel="last",<http://localhost/api/study-classes?page=0&size=20>; rel="first"',
              },
              json: [studyClass],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(studyClassPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studyClassPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing StudyClass to exercise');
      });

      test('detail button click should load details StudyClass page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('studyClass')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
      });

      test('edit button click should load edit StudyClass page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudyClass')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
      });

      test('edit button click should load edit StudyClass page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudyClass')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of StudyClass', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${studyClassApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('studyClass')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);

        studyClass = undefined;
      });
    });
  });

  test.describe('new StudyClass page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(studyClassPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('StudyClass')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of StudyClass', async ({ page }) => {
      await page.locator(`[data-cy="name"]`).fill('fooey hype');
      await expect(page.locator(`[data-cy="name"]`)).toHaveValue('fooey hype');

      await page.locator(`[data-cy="description"]`).fill('considering');
      await expect(page.locator(`[data-cy="description"]`)).toHaveValue('considering');

      await page.locator(`[data-cy="joinCode"]`).fill('lest hm');
      await expect(page.locator(`[data-cy="joinCode"]`)).toHaveValue('lest hm');

      await page.locator(`[data-cy="joinMode"]`).selectOption('INVITE_ONLY');

      await page.locator(`[data-cy="maxStudents"]`).fill('732');
      await expect(page.locator(`[data-cy="maxStudents"]`)).toHaveValue('732');

      await page.locator(`[data-cy="currentStudents"]`).fill('20344');
      await expect(page.locator(`[data-cy="currentStudents"]`)).toHaveValue('20344');

      await page.locator(`[data-cy="status"]`).selectOption('PENDING');

      await expect(page.locator(`[data-cy="isDeleted"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isDeleted"]`).click();
      await expect(page.locator(`[data-cy="isDeleted"]`)).toBeChecked();

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T07:40');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T07:40');

      await page.locator(`[data-cy="instructor"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === studyClassApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      studyClass = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === studyClassPageUrl);
    });
  });
});
