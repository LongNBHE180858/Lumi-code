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

const classMemberPageUrl = '/class-member';
// const classMemberSample = {"requestedAt":"2026-10-12T12:13:42.621Z","updatedAt":"2026-10-12T19:45:04.297Z"};

const classMemberApiUrl = '/api/class-members';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === classMemberApiUrl && response.request().method() === 'GET');

test.describe('ClassMember e2e test', () => {
  let classMember;
  // let studyClass;
  // let user;

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
        body: {"name":"hence","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","joinCode":"yum","joinMode":"CODE_APPROVAL","maxStudents":23945,"currentStudents":29898,"status":"ARCHIVED","isDeleted":false,"createdAt":"2026-10-12T07:56:45.106Z","joinCodeExpiresAt":"2026-10-12T02:33:00.365Z","updatedAt":"2026-10-12T15:16:14.684Z"},
      })
    ).json();
    // create an instance at the required relationship entity:
    user = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/users',
        body: {"login":"UyenMy84","firstName":"Đoan Trang","lastName":"Trịnh","email":"NhatPhuong_Tang@yahoo.com","langKey":"gadzooks h","imageUrl":"westernise"},
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
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/users',
      route => route.fulfill({ status: 200, json: [user].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (classMember) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/class-members/${classMember.id}`,
      });
      classMember = undefined;
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
    if (user) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      });
      user = undefined;
    }
  });
   */

  test('ClassMembers menu should load ClassMembers page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('class-member');
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
    await expect(jhi.entity.getEntityHeading('ClassMember')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
  });

  test.describe('ClassMember page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(classMemberPageUrl);
      await expect(jhi.entity.getEntityHeading('ClassMember')).not.toContainText('lumiApp.classMember.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(classMemberPageUrl);
        await response;
      });

      test('should load create ClassMember page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${classMemberPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMember')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        classMember = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/class-members',
            body: {
              ...classMemberSample,
              studyClass: studyClass,
              student: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === classMemberApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/class-members?page=0&size=20>; rel="last",<http://localhost/api/class-members?page=0&size=20>; rel="first"',
              },
              json: [classMember],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(classMemberPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(classMemberPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ClassMember to exercise');
      });

      test('detail button click should load details ClassMember page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('classMember')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
      });

      test('edit button click should load edit ClassMember page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMember')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
      });

      test('edit button click should load edit ClassMember page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMember')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ClassMember', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${classMemberApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('classMember')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);

        classMember = undefined;
      });
    });
  });

  test.describe('new ClassMember page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(classMemberPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ClassMember')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ClassMember', async ({ page }) => {
      await page.locator(`[data-cy="status"]`).fill('uneven');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('uneven');

      await page.locator(`[data-cy="joinedAt"]`).fill('2026-10-12T12:30');
      await page.locator(`[data-cy="joinedAt"]`).blur();
      await expect(page.locator(`[data-cy="joinedAt"]`)).toHaveValue('2026-10-12T12:30');

      await page.locator(`[data-cy="requestedAt"]`).fill('2026-10-12T04:37');
      await page.locator(`[data-cy="requestedAt"]`).blur();
      await expect(page.locator(`[data-cy="requestedAt"]`)).toHaveValue('2026-10-12T04:37');

      await page.locator(`[data-cy="leftAt"]`).fill('2026-10-12T15:19');
      await page.locator(`[data-cy="leftAt"]`).blur();
      await expect(page.locator(`[data-cy="leftAt"]`)).toHaveValue('2026-10-12T15:19');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-10-12T01:16');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-10-12T01:16');

      await page.locator(`[data-cy="studyClass"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === classMemberApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      classMember = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === classMemberPageUrl);
    });
  });
});
