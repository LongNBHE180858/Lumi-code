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

const coursePageUrl = '/course';
// const courseSample = {"title":"cricket","version":1506};

const courseApiUrl = '/api/courses';

const entitiesRequest = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => new URL(response.url()).pathname === courseApiUrl && response.request().method() === 'GET');

test.describe('Course e2e test', () => {
  let course;
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
        body: {"login":"VanHuong_Truong","firstName":"Linh Châu","lastName":"Ngô","email":"TrucQuynh.Bui65@gmail.com","langKey":"painfully ","imageUrl":"sans"},
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
    if (course) {
      await jhi.authenticatedRequest({
        method: 'DELETE',
        url: `/api/courses/${course.id}`,
      });
      course = undefined;
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

  test('Courses menu should load Courses page', async ({ page, jhi }) => {
    const response = entitiesRequest(page);
    await page.goto('/');
    await jhi.navbar.clickOnEntityMenuItem('course');
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
    await expect(jhi.entity.getEntityHeading('Course')).toBeVisible();
    await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
  });

  test.describe('Course page', () => {
    test('should have translated page title', async ({ page, jhi }) => {
      await page.goto(coursePageUrl);
      await expect(jhi.entity.getEntityHeading('Course')).not.toContainText('lumiApp.course.home.title');
    });

    test.describe('create button click', () => {
      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(coursePageUrl);
        await response;
      });

      test('should load create Course page', async ({ page, jhi }) => {
        await page.locator(entityCreateButtonSelector).click();
        await expect(page).toHaveURL(url => url.pathname === `${coursePageUrl}/new`);
        await expect(jhi.entity.getEntityCreateUpdateHeading('Course')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
      });
    });

    test.describe('with existing value', () => {
      /* Disabled due to incompatibility
      test.beforeEach(async ({ page, jhi }) => {
        course = await (
          await jhi.authenticatedRequest({
            method: 'POST',
            url: '/api/courses',
            body: {
              ...courseSample,
              author: user,
            },
          })
        ).json();

        await page.route(
          url => url.pathname === courseApiUrl,
          route =>
            route.fulfill({
              status: 200,
              headers: {
                link: '<http://localhost/api/courses?page=0&size=20>; rel="last",<http://localhost/api/courses?page=0&size=20>; rel="first"',
              },
              json: [course],
            }),
          { times: 1 },
        );

        // Cypress waited on the stubbed request (cy.wait('@entitiesRequestInternal')); without it the test
        // can act before the row is rendered.
        const stubbedRequest = entitiesRequest(page);
        await page.goto(coursePageUrl);
        await stubbedRequest;
      });
       */

      test.beforeEach(async ({ page }) => {
        const response = entitiesRequest(page);
        await page.goto(coursePageUrl);
        const body = (await (await response).json().catch(() => undefined)) as unknown[] | undefined;
        test.skip(Array.isArray(body) && body.length === 0, 'no existing Course to exercise');
      });

      test('detail button click should load details Course page', async ({ page, jhi }) => {
        await page.locator(entityDetailsButtonSelector).first().click();
        await expect(jhi.entity.getEntityDetailsHeading('course')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityDetailsBackButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
      });

      test('edit button click should load edit Course page and go back', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Course')).toBeVisible();
        await expect(page.locator(entityCreateSaveButtonSelector)).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateCancelButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
      });

      test('edit button click should load edit Course page and save', async ({ page, jhi }) => {
        await page.locator(entityEditButtonSelector).first().click();
        await expect(jhi.entity.getEntityCreateUpdateHeading('Course')).toBeVisible();
        const response = entitiesRequest(page);
        await page.locator(entityCreateSaveButtonSelector).click();
        expect((await response).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      test.skip('last delete button click should delete instance of Course', async ({ page, jhi }) => {
        const deleteRequest = page.waitForResponse(
          response => new URL(response.url()).pathname.startsWith(`${courseApiUrl}/`) && response.request().method() === 'DELETE',
        );
        const listRequest = entitiesRequest(page);
        await page.locator(entityDeleteButtonSelector).last().click();
        await expect(jhi.entity.getEntityDeleteDialogHeading('course')).toBeVisible();
        await page.locator(entityConfirmDeleteButtonSelector).click();
        expect((await deleteRequest).status()).toBe(204);
        expect((await listRequest).status()).toBe(200);
        await expect(page).toHaveURL(url => url.pathname === coursePageUrl);

        course = undefined;
      });
    });
  });

  test.describe('new Course page', () => {
    test.beforeEach(async ({ page, jhi }) => {
      await page.goto(coursePageUrl);
      await page.locator(entityCreateButtonSelector).click();
      await expect(jhi.entity.getEntityCreateUpdateHeading('Course')).toBeVisible();
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    test.skip('should create an instance of Course', async ({ page }) => {
      await page.locator(`[data-cy="title"]`).fill('celebrate near morbidity');
      await expect(page.locator(`[data-cy="title"]`)).toHaveValue('celebrate near morbidity');

      await page.locator(`[data-cy="description"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="description"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="thumbnailUrl"]`).fill('apropos');
      await expect(page.locator(`[data-cy="thumbnailUrl"]`)).toHaveValue('apropos');

      await page.locator(`[data-cy="price"]`).fill('14825.89');
      await expect(page.locator(`[data-cy="price"]`)).toHaveValue('14825.89');

      await page.locator(`[data-cy="shortDescription"]`).fill('mount doubtfully unaware');
      await expect(page.locator(`[data-cy="shortDescription"]`)).toHaveValue('mount doubtfully unaware');

      await page.locator(`[data-cy="bookSeries"]`).fill('weird unabashedly consequently');
      await expect(page.locator(`[data-cy="bookSeries"]`)).toHaveValue('weird unabashedly consequently');

      await page.locator(`[data-cy="targetAudience"]`).fill('unlearn');
      await expect(page.locator(`[data-cy="targetAudience"]`)).toHaveValue('unlearn');

      await page.locator(`[data-cy="prerequisites"]`).fill('victoriously');
      await expect(page.locator(`[data-cy="prerequisites"]`)).toHaveValue('victoriously');

      await page.locator(`[data-cy="learningObjectives"]`).fill('../fake-data/blob/hipster.txt');
      await expect(page.locator(`[data-cy="learningObjectives"]`)).toHaveValue(new RegExp('../fake-data/blob/hipster.txt'));

      await page.locator(`[data-cy="introVideoUrl"]`).fill('fair eek deprave');
      await expect(page.locator(`[data-cy="introVideoUrl"]`)).toHaveValue('fair eek deprave');

      await page.locator(`[data-cy="validDurationDays"]`).fill('2226');
      await expect(page.locator(`[data-cy="validDurationDays"]`)).toHaveValue('2226');

      await page.locator(`[data-cy="accessDurationValue"]`).fill('9064');
      await expect(page.locator(`[data-cy="accessDurationValue"]`)).toHaveValue('9064');

      await page.locator(`[data-cy="accessDurationUnit"]`).fill('whispered');
      await expect(page.locator(`[data-cy="accessDurationUnit"]`)).toHaveValue('whispered');

      await page.locator(`[data-cy="version"]`).fill('15611');
      await expect(page.locator(`[data-cy="version"]`)).toHaveValue('15611');

      await page.locator(`[data-cy="status"]`).selectOption('DRAFT');

      await expect(page.locator(`[data-cy="isDeleted"]`)).not.toBeChecked();
      await page.locator(`[data-cy="isDeleted"]`).click();
      await expect(page.locator(`[data-cy="isDeleted"]`)).toBeChecked();

      await page.locator(`[data-cy="createdAt"]`).fill('2026-10-12T09:48');
      await page.locator(`[data-cy="createdAt"]`).blur();
      await expect(page.locator(`[data-cy="createdAt"]`)).toHaveValue('2026-10-12T09:48');

      await page.locator(`[data-cy="updatedAt"]`).fill('2026-10-12T05:33');
      await page.locator(`[data-cy="updatedAt"]`).blur();
      await expect(page.locator(`[data-cy="updatedAt"]`)).toHaveValue('2026-10-12T05:33');

      await page.locator(`[data-cy="storageLimitBytes"]`).fill('15512');
      await expect(page.locator(`[data-cy="storageLimitBytes"]`)).toHaveValue('15512');

      await page.locator(`[data-cy="author"]`).selectOption({ index: 1 });

      const postRequest = page.waitForResponse(
        response => new URL(response.url()).pathname === courseApiUrl && response.request().method() === 'POST',
      );
      const listRequest = entitiesRequest(page);
      await page.locator(entityCreateSaveButtonSelector).click();

      const postResponse = await postRequest;
      expect(postResponse.status()).toBe(201);
      // Best effort: the body is only needed to delete the instance again in afterEach.
      course = await postResponse.json().catch(() => undefined);
      expect((await listRequest).status()).toBe(200);
      await expect(page).toHaveURL(url => url.pathname === coursePageUrl);
    });
  });
});
