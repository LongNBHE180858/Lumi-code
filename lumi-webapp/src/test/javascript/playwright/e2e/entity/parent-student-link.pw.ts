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

const parentStudentLinkPageUrl = '/parent-student-link';
// const parentStudentLinkSample = {"linkCode":"masticate "};

const parentStudentLinkApiUrl = '/api/parent-student-links';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === parentStudentLinkApiUrl && response.request().method() === 'GET');

test.describe('ParentStudentLink e2e test', () => {
  let parentStudentLink;
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
        body: {"login":"HoangLam.7kinh","firstName":"Hồng Nhạn","lastName":"Lâm","email":"TuongLam47@yahoo.com","langKey":"till","imageUrl":"bludgeon deform supposing"},
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
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (parentStudentLink) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parent-student-links/${parentStudentLink.id}`,
      });
      parentStudentLink = undefined;
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

  test('ParentStudentLinks menu should load ParentStudentLinks page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('parent-student-link');
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
    await expect(jhi.entity.getEntityHeading('ParentStudentLink')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
  });

  test.describe('ParentStudentLink page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(parentStudentLinkPageUrl);
      await expect(jhi.entity.getEntityHeading('ParentStudentLink')).not.toContainText('lumiApp.parentStudentLink.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(parentStudentLinkPageUrl);
        await response;
      });

      test('should load create ParentStudentLink page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${parentStudentLinkPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ParentStudentLink')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        parentStudentLink = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/parent-student-links',
            body: {
              ...parentStudentLinkSample,
              parent: user,
              student: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === parentStudentLinkApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [parentStudentLink],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(parentStudentLinkPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(parentStudentLinkPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ParentStudentLink to exercise');
      });

      test('detail button click should load details ParentStudentLink page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('parentStudentLink')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
      });

      test('edit button click should load edit ParentStudentLink page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ParentStudentLink')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
      });

      test('edit button click should load edit ParentStudentLink page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ParentStudentLink')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ParentStudentLink', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${parentStudentLinkApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('parentStudentLink')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);

        parentStudentLink = undefined;
      });
    });
  });

  test.describe('new ParentStudentLink page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(parentStudentLinkPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ParentStudentLink')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ParentStudentLink', async ({ page }) => {
      await page.locator(`[data-cy="linkCode"]`).fill('but frail');
      await expect(page.locator(`[data-cy="linkCode"]`)).toHaveValue('but frail');

      await page.locator(`[data-cy="status"]`).fill('astride adrenalin fu');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('astride adrenalin fu');

      await page.locator(`[data-cy="linkedAt"]`).fill('2026-10-12T00:38');
      await page.locator(`[data-cy="linkedAt"]`).blur();
      await expect(page.locator(`[data-cy="linkedAt"]`)).toHaveValue('2026-10-12T00:38');

      await page.locator(`[data-cy="parent"]`).selectOption({ index: 1 });
      await page.locator(`[data-cy="student"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === parentStudentLinkApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      parentStudentLink = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === parentStudentLinkPageUrl);
    });
  });
});
