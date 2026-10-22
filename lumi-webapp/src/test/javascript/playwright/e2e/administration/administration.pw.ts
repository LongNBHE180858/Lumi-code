import {
  configurationPageHeadingSelector,
  healthPageHeadingSelector,
  logsPageHeadingSelector,
  metricsPageHeadingSelector,
  swaggerFrameSelector,
  swaggerPageSelector,
} from '../../support/commands';
import { expect, test } from '../../support/fixtures';

// Swagger UI fetches and renders the api definition, which is slow on a gateway.
const SWAGGER_TIMEOUT = 30000;

test.describe('/admin', () => {
  test.beforeEach(async ({ page, jhi }) => {
    const { adminUsername, adminPassword } = jhi.credentials();
    await jhi.login(adminUsername, adminPassword);
    await page.goto('');
  });

  test.describe('/metrics', () => {
    test('should load the page', async ({ page, jhi }) => {
      await jhi.navbar.clickOnAdminMenuItem('admin/metrics');
      await expect(page.locator(metricsPageHeadingSelector)).toBeVisible();
    });
  });

  test.describe('/health', () => {
    test('should load the page', async ({ page, jhi }) => {
      await jhi.navbar.clickOnAdminMenuItem('admin/health');
      await expect(page.locator(healthPageHeadingSelector)).toBeVisible();
    });
  });

  test.describe('/logs', () => {
    test('should load the page', async ({ page, jhi }) => {
      await jhi.navbar.clickOnAdminMenuItem('admin/logs');
      await expect(page.locator(logsPageHeadingSelector)).toBeVisible();
    });
  });

  test.describe('/configuration', () => {
    test('should load the page', async ({ page, jhi }) => {
      await jhi.navbar.clickOnAdminMenuItem('admin/configuration');
      await expect(page.locator(configurationPageHeadingSelector)).toBeVisible();
    });
  });

  test.describe('/docs', () => {
    test('should load the page', async ({ page, jhi }) => {
      const info = (await jhi.managementInfo()) as { activeProfiles?: string[] };
      test.skip(!info.activeProfiles?.includes('api-docs'), 'the api-docs profile is not active');

      await jhi.navbar.clickOnAdminMenuItem('admin/docs');
      await expect(page.locator(swaggerFrameSelector)).toBeVisible({ timeout: SWAGGER_TIMEOUT });

      // Swagger UI renders the definition asynchronously. Cypress retried each of these through `should`;
      // a count() read is a single sample, so assert on the locators instead.
      const swagger = page.frameLocator(swaggerFrameSelector);
      // Cypress waited for the iframe body to stop being empty before looking inside it.
      await expect(swagger.locator('body')).not.toBeEmpty({ timeout: SWAGGER_TIMEOUT });
      await expect(swagger.locator(swaggerPageSelector)).toBeVisible({ timeout: SWAGGER_TIMEOUT });
      await expect(swagger.locator('[id="select"] > option').first()).toBeAttached({ timeout: SWAGGER_TIMEOUT });
      await expect(swagger.locator(`${swaggerPageSelector} .information-container`).first()).toBeVisible({
        timeout: SWAGGER_TIMEOUT,
      });
    });
  });
});
