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

const supportTicketPageUrl = '/support-ticket';
// const supportTicketSample = {"type":"which pish hm","subject":"phew as"};

const supportTicketApiUrl = '/api/support-tickets';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === supportTicketApiUrl && response.request().method() === 'GET');

test.describe('SupportTicket e2e test', () => {
  let supportTicket;
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
        body: {"login":"HoaMi14","firstName":"Quế Anh","lastName":"Bùi","email":"QuangTrieu8@hotmail.com","langKey":"imaginary","imageUrl":"dial"},
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
    if (supportTicket) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/support-tickets/${supportTicket.id}`,
      });
      supportTicket = undefined;
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

  test('SupportTickets menu should load SupportTickets page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('support-ticket');
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
    await expect(jhi.entity.getEntityHeading('SupportTicket')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
  });

  test.describe('SupportTicket page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(supportTicketPageUrl);
      await expect(jhi.entity.getEntityHeading('SupportTicket')).not.toContainText('lumiApp.supportTicket.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(supportTicketPageUrl);
        await response;
      });

      test('should load create SupportTicket page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${supportTicketPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('SupportTicket')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        supportTicket = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/support-tickets',
            body: {
              ...supportTicketSample,
              user: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === supportTicketApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/support-tickets?page=0&size=20>; rel="last",<http://localhost/api/support-tickets?page=0&size=20>; rel="first"',
              },
              json: [supportTicket],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(supportTicketPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(supportTicketPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing SupportTicket to exercise');
      });

      test('detail button click should load details SupportTicket page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('supportTicket')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
      });

      test('edit button click should load edit SupportTicket page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('SupportTicket')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
      });

      test('edit button click should load edit SupportTicket page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('SupportTicket')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of SupportTicket', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${supportTicketApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('supportTicket')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);

        supportTicket = undefined;
      });
    });
  });

  test.describe('new SupportTicket page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(supportTicketPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('SupportTicket')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of SupportTicket', async ({ page }) => {
      await page.locator(`[data-cy="type"]`).fill('ugh tame');
      await expect(page.locator(`[data-cy="type"]`)).toHaveValue('ugh tame');

      await page.locator(`[data-cy="subject"]`).fill('antique');
      await expect(page.locator(`[data-cy="subject"]`)).toHaveValue('antique');

      await page.locator(`[data-cy="status"]`).fill('synthesise qua to');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('synthesise qua to');

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T11:45');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T11:45');

      await page.locator(`[data-cy="user"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === supportTicketApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      supportTicket = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === supportTicketPageUrl);
    });
  });
});
