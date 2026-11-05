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

const teacherVerificationPageUrl = '/teacher-verification';
// const teacherVerificationSample = {"identityDocumentUrl":"hammock promise"};

const teacherVerificationApiUrl = '/api/teacher-verifications';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === teacherVerificationApiUrl && response.request().method() === 'GET');

test.describe('TeacherVerification e2e test', () => {
  let teacherVerification;
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
        body: {"login":"ThanhKien_7kinh90","firstName":"Ngọc Sơn","lastName":"Đoàn","email":"7koanTu43@gmail.com","langKey":"like fast ","imageUrl":"circa"},
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
    if (teacherVerification) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/teacher-verifications/${teacherVerification.id}`,
      });
      teacherVerification = undefined;
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

  test('TeacherVerifications menu should load TeacherVerifications page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('teacher-verification');
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
    await expect(jhi.entity.getEntityHeading('TeacherVerification')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
  });

  test.describe('TeacherVerification page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(teacherVerificationPageUrl);
      await expect(jhi.entity.getEntityHeading('TeacherVerification')).not.toContainText('lumiApp.teacherVerification.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(teacherVerificationPageUrl);
        await response;
      });

      test('should load create TeacherVerification page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${teacherVerificationPageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('TeacherVerification')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        teacherVerification = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/teacher-verifications',
            body: {
              ...teacherVerificationSample,
              user: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === teacherVerificationApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/teacher-verifications?page=0&size=20>; rel="last",<http://localhost/api/teacher-verifications?page=0&size=20>; rel="first"',
              },
              json: [teacherVerification],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(teacherVerificationPageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(teacherVerificationPageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing TeacherVerification to exercise');
      });

      test('detail button click should load details TeacherVerification page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('teacherVerification')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
      });

      test('edit button click should load edit TeacherVerification page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('TeacherVerification')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
      });

      test('edit button click should load edit TeacherVerification page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('TeacherVerification')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of TeacherVerification', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response =>
            new URL(response.url()).pathname.startsWith(`${teacherVerificationApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('teacherVerification')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);

        teacherVerification = undefined;
      });
    });
  });

  test.describe('new TeacherVerification page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(teacherVerificationPageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('TeacherVerification')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of TeacherVerification', async ({ page }) => {
      await page.locator(`[data-cy="identityDocumentUrl"]`).fill('confute cosset vista');
      await expect(page.locator(`[data-cy="identityDocumentUrl"]`)).toHaveValue('confute cosset vista');

      await page.locator(`[data-cy="verificationStatus"]`).fill('reluctantly apropos ');
      await expect(page.locator(`[data-cy="verificationStatus"]`)).toHaveValue('reluctantly apropos ');

      await page.locator(`[data-cy="notes"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="notes"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="submittedAt"]`).fill('2026-10-12T05:19');
      await page.locator(`[data-cy="submittedAt"]`).blur();
      await expect(page.locator(`[data-cy="submittedAt"]`)).toHaveValue('2026-10-12T05:19');

      await page.locator(`[data-cy="approvedAt"]`).fill('2026-10-12T17:20');
      await page.locator(`[data-cy="approvedAt"]`).blur();
      await expect(page.locator(`[data-cy="approvedAt"]`)).toHaveValue('2026-10-12T17:20');

      await page.locator(`[data-cy="reviewedAt"]`).fill('2026-10-12T13:07');
      await page.locator(`[data-cy="reviewedAt"]`).blur();
      await expect(page.locator(`[data-cy="reviewedAt"]`)).toHaveValue('2026-10-12T13:07');

      await page.locator(`[data-cy="user"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === teacherVerificationApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      teacherVerification = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === teacherVerificationPageUrl);
    });
  });
});
