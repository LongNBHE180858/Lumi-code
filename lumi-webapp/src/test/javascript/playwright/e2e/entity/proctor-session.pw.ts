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

const proctorSessionPageUrl = '/proctor-session';
// const proctorSessionSample = {};

const proctorSessionApiUrl = '/api/proctor-sessions';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === proctorSessionApiUrl && response.request().method() === 'GET');

test.describe('ProctorSession e2e test', () => {
  let proctorSession;
  // let examAttempt;

  test.beforeEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
  });

  /* Disabled due to incompatibility
  test.beforeEach(async ({ jhi }) => {
    // create an instance at the required relationship entity:
    examAttempt = await (
      await jhi.authenticatedRequest({
        method: 'POST',
        url: '/api/exam-attempts',
        body: {"startTime":"2026-10-12T17:58:07.707Z","endTime":"2026-10-12T16:33:08.057Z","score":27018.63,"status":"fiercely chiffonier ","rowVersion":23317},
      })
    ).json();
  });

  test.beforeEach(async ({ page }) => {
    // Simulate relationships api for better performance and reproducibility.
    // A url predicate rather than a glob: a glob's wildcards would close the "Disabled due to
    // incompatibility" block comment that wraps this section when the create test is skipped.
    await page.route(
      url => url.pathname === '/api/exam-attempts',
      route => route.fulfill({ status: 200, json: [examAttempt].filter(Boolean) }),
    );
  });
   */

  test.afterEach(async ({ jhi }) => {
    if (proctorSession) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/proctor-sessions/${proctorSession.id}`,
      });
      proctorSession = undefined;
    }
  });

  /* Disabled due to incompatibility
  test.afterEach(async ({ jhi }) => {
    if (examAttempt) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exam-attempts/${examAttempt.id}`,
      });
      examAttempt = undefined;
    }
  });
   */

  test('ProctorSessions menu should load ProctorSessions page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('proctor-session');
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
    await expect(jhi.entity.getEntityHeading('ProctorSession')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
  });

  test.describe('ProctorSession page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(proctorSessionPageUrl);
      await expect(jhi.entity.getEntityHeading('ProctorSession')).not.toContainText('lumiApp.proctorSession.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(proctorSessionPageUrl);
        await response;
      });

      test('should load create ProctorSession page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${proctorSessionPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorSession')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        proctorSession = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/proctor-sessions',
            body: {
              ...proctorSessionSample,
              attempt: examAttempt,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === proctorSessionApiUrl,
          route =>
            route.fulfill({
              status: 200,
              json: [proctorSession],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(proctorSessionPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(proctorSessionPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing ProctorSession to exercise');
      });

      test('detail button click should load details ProctorSession page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('proctorSession')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
      });

      test('edit button click should load edit ProctorSession page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorSession')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
      });

      test('edit button click should load edit ProctorSession page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorSession')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of ProctorSession', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${proctorSessionApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('proctorSession')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);

        proctorSession = undefined;
      });
    });
  });

  test.describe('new ProctorSession page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(proctorSessionPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('ProctorSession')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of ProctorSession', async ({ page }) => {
      await expect(page.locator(`[data-cy="faceVerified"]`)).not.toBeChecked();
      await page.locator(`[data-cy="faceVerified"]`).click();
      await expect(page.locator(`[data-cy="faceVerified"]`)).toBeChecked();

      await page.locator(`[data-cy="warningCount"]`).fill('26246');
      await expect(page.locator(`[data-cy="warningCount"]`)).toHaveValue('26246');

      await page.locator(`[data-cy="status"]`).fill('coaxingly');
      await expect(page.locator(`[data-cy="status"]`)).toHaveValue('coaxingly');

      await page.locator(`[data-cy="attempt"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === proctorSessionApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      proctorSession = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === proctorSessionPageUrl);
    });
  });
});
