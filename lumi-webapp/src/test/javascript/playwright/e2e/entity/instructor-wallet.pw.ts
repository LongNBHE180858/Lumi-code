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

const instructorWalletPageUrl = '/instructor-wallet';
// const instructorWalletSample = {};

const instructorWalletApiUrl = '/api/instructor-wallets';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === instructorWalletApiUrl && response.request().method() === 'GET');

test.describe('InstructorWallet e2e test', () => {
  let instructorWallet;
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
        body: {"login":"ThuyMien_Ha1","firstName":"Duy Thanh","lastName":"Nguyễn","email":"HuuTrac.7kang@gmail.com","langKey":"inject bec","imageUrl":"geez over"},
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
    if (instructorWallet) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instructor-wallets/${instructorWallet.id}`,
      });
      instructorWallet = undefined;
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

  test('InstructorWallets menu should load InstructorWallets page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('instructor-wallet');
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
    await expect(jhi.entity.getEntityHeading('InstructorWallet')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
  });

  test.describe('InstructorWallet page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(instructorWalletPageUrl);
      await expect(jhi.entity.getEntityHeading('InstructorWallet')).not.toContainText('lumiApp.instructorWallet.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(instructorWalletPageUrl);
        await response;
      });

      test('should load create InstructorWallet page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${instructorWalletPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('InstructorWallet')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        instructorWallet = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/instructor-wallets',
            body: {
              ...instructorWalletSample,
              instructor: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === instructorWalletApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/instructor-wallets?page=0&size=20>; rel="last",<http://localhost/api/instructor-wallets?page=0&size=20>; rel="first"',
              },
              json: [instructorWallet],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(instructorWalletPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(instructorWalletPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing InstructorWallet to exercise');
      });

      test('detail button click should load details InstructorWallet page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('instructorWallet')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
      });

      test('edit button click should load edit InstructorWallet page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('InstructorWallet')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
      });

      test('edit button click should load edit InstructorWallet page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('InstructorWallet')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of InstructorWallet', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${instructorWalletApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('instructorWallet')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);

        instructorWallet = undefined;
      });
    });
  });

  test.describe('new InstructorWallet page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(instructorWalletPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('InstructorWallet')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of InstructorWallet', async ({ page }) => {
      await page.locator(`[data-cy="pendingAmount"]`).fill('17897.04');
      await expect(page.locator(`[data-cy="pendingAmount"]`)).toHaveValue('17897.04');

      await page.locator(`[data-cy="availableAmount"]`).fill('5367.05');
      await expect(page.locator(`[data-cy="availableAmount"]`)).toHaveValue('5367.05');

      await page.locator(`[data-cy="rowVersion"]`).fill('4312');
      await expect(page.locator(`[data-cy="rowVersion"]`)).toHaveValue('4312');

      await page.locator(`[data-cy="instructor"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === instructorWalletApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      instructorWallet = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === instructorWalletPageUrl);
    });
  });
});
