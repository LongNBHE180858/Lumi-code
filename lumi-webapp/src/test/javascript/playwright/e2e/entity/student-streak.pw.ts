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

const studentStreakPageUrl = '/student-streak';
// const studentStreakSample = {};

const studentStreakApiUrl = '/api/student-streaks';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === studentStreakApiUrl && response.request().method() === 'GET');

test.describe('StudentStreak e2e test', () => {
  let studentStreak;
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
        body: {"login":"VanLy54","firstName":"Hoàng Hải","lastName":"Dương","email":"AnKhe_7ko86@gmail.com","langKey":"reproachfu","imageUrl":"to geez"},
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
    if (studentStreak) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/student-streaks/${studentStreak.id}`,
      });
      studentStreak = undefined;
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

  test('StudentStreaks menu should load StudentStreaks page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('student-streak');
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
    await expect(jhi.entity.getEntityHeading('StudentStreak')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
  });

  test.describe('StudentStreak page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(studentStreakPageUrl);
      await expect(jhi.entity.getEntityHeading('StudentStreak')).not.toContainText('lumiApp.studentStreak.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentStreakPageUrl);
        await response;
      });

      test('should load create StudentStreak page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${studentStreakPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentStreak')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        studentStreak = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/student-streaks',
            body: {
              ...studentStreakSample,
              user: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === studentStreakApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [studentStreak],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(studentStreakPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(studentStreakPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing StudentStreak to exercise');
      });

      test('detail button click should load details StudentStreak page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('studentStreak')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
      });

      test('edit button click should load edit StudentStreak page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentStreak')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
      });

      test('edit button click should load edit StudentStreak page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('StudentStreak')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of StudentStreak', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${studentStreakApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('studentStreak')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);

        studentStreak = undefined;
      });
    });
  });

  test.describe('new StudentStreak page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(studentStreakPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('StudentStreak')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of StudentStreak', async ({ page }) => {
      await page.locator(`[data-cy="currentStreak"]`).fill('28273');
      await expect(page.locator(`[data-cy="currentStreak"]`)).toHaveValue('28273');

      await page.locator(`[data-cy="longestStreak"]`).fill('12255');
      await expect(page.locator(`[data-cy="longestStreak"]`)).toHaveValue('12255');

      await page.locator(`[data-cy="gemsBalance"]`).fill('19983');
      await expect(page.locator(`[data-cy="gemsBalance"]`)).toHaveValue('19983');

      await page.locator(`[data-cy="lastActiveDate"]`).fill('2026-10-12');
      await page.locator(`[data-cy="lastActiveDate"]`).blur();
      await expect(page.locator(`[data-cy="lastActiveDate"]`)).toHaveValue('2026-10-12');

      await page.locator(`[data-cy="user"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === studentStreakApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      studentStreak = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === studentStreakPageUrl);
    });
  });
});
