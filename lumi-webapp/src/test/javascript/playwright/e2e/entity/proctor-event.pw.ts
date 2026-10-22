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

const proctorEventPageUrl = '/proctor-event';
// const proctorEventSample = {"violationType":"quip"};

const proctorEventApiUrl = '/api/proctor-events';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === proctorEventApiUrl && response.request().method() === 'GET');

test.describe('ProctorEvent e2e test', () => {
  let proctorEvent;
  // let proctorSession;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    proctorSession = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/proctor-sessions',
        body: {"faceVerified":false,"warningCount":7279,"status":"besides"},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/proctor-sessions',
      route => route.fulfill({ status: 200, json: [proctorSession].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (proctorEvent) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/proctor-events/${proctorEvent.id}`,
      });
      proctorEvent = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (proctorSession) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/proctor-sessions/${proctorSession.id}`,
      });
      proctorSession = undefined;
    }
  });
   */

  test('ProctorEvents menu should load ProctorEvents page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('proctor-event');
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
    await expect(jhi.entity.getEntityHeading('ProctorEvent')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
  });

  test.describe('ProctorEvent page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(proctorEventPageUrl);
      await expect(jhi.entity.getEntityHeading('ProctorEvent')).not.toContainText('lumiApp.proctorEvent.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(proctorEventPageUrl);
        await response;
      });

      test('should load create ProctorEvent page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${proctorEventPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorEvent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        proctorEvent = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/proctor-events',
            body: {
              ...proctorEventSample,
              session: proctorSession,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === proctorEventApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/proctor-events?page=0&size=20>; rel="last",<http://localhost/api/proctor-events?page=0&size=20>; rel="first"',
              },
              json: [proctorEvent],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(proctorEventPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(proctorEventPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ProctorEvent to exercise');
      });

      test('detail button click should load details ProctorEvent page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('proctorEvent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
      });

      test('edit button click should load edit ProctorEvent page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorEvent')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
      });

      test('edit button click should load edit ProctorEvent page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorEvent')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ProctorEvent', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${proctorEventApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('proctorEvent')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);

        proctorEvent = undefined;
      });
    });
  });

  test.describe('new ProctorEvent page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(proctorEventPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorEvent')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ProctorEvent', async ({ page }) => {
      await page.locator(`[data-cy="violationType"]`).fill('but drat halt');
      await expect(page.locator(`[data-cy="violationType"]`)).toHaveValue('but drat halt');

      await page.locator(`[data-cy="photoUrl"]`).fill('instead among');
      await expect(page.locator(`[data-cy="photoUrl"]`)).toHaveValue('instead among');

      await page.locator(`[data-cy="occurredAt"]`).fill('2026-10-12T14:45');
      await page.locator(`[data-cy="occurredAt"]`).blur();
      await expect(page.locator(`[data-cy="occurredAt"]`)).toHaveValue('2026-10-12T14:45');

      await page.locator(`[data-cy="session"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === proctorEventApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      proctorEvent = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === proctorEventPageUrl);
    });
  });
});
