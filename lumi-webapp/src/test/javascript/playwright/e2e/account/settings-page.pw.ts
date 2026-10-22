import { type Account, getAccount, saveAccount } from '../../support/account';
import { emailSettingsSelector, firstNameSettingsSelector, lastNameSettingsSelector, submitSettingsSelector } from '../../support/commands';
import { expect, test } from '../../support/fixtures';

const settingsSave = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => response.url().includes('/api/account') && response.request().method() === 'POST');

const testUserEmail = 'user@localhost.fr';

test.describe('/account/settings', () => {
  let originalUserAccount: Account;
  let testUserAccount: Account;

  test.beforeEach(async ({ page, jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);

    if (!originalUserAccount) {
      originalUserAccount = await getAccount(jhi);
      testUserAccount = { ...originalUserAccount, email: testUserEmail };
    }
    // need to modify email because default email does not match regex in some frameworks
    expect((await saveAccount(jhi, testUserAccount)).status()).toBe(200);

    await page.goto('/account/settings');
    await expect(page.locator(emailSettingsSelector)).toHaveValue(testUserEmail);
  });

  // Cypress restores the test account between tests and the original one at the end. Restoring the original
  // one after every test reaches the same final state without a worker scoped fixture, and beforeEach puts the
  // test email back before each test.
  test.afterEach(async ({ jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
    expect((await saveAccount(jhi, originalUserAccount)).status()).toBe(200);
  });

  test('should be accessible through menu', async ({ page, jhi }) => {
    await page.goto('');
    await jhi.navbar.clickOnSettingsItem();
    await expect(page).toHaveURL(url => url.pathname === '/account/settings');
  });

  test("should be able to change 'user' firstname settings", async ({ page }) => {
    const response = settingsSave(page);
    await page.locator(firstNameSettingsSelector).fill('jhipster');
    await page.locator(submitSettingsSelector).click();
    expect((await response).status()).toBe(200);
  });

  test("should be able to change 'user' lastname settings", async ({ page }) => {
    const response = settingsSave(page);
    await page.locator(firstNameSettingsSelector).fill('jhipster');
    await page.locator(lastNameSettingsSelector).fill('retspihj');
    await page.locator(submitSettingsSelector).click();
    expect((await response).status()).toBe(200);
  });

  test("should be able to change 'user' email settings", async ({ page }) => {
    const response = settingsSave(page);
    await page.locator(firstNameSettingsSelector).fill('jhipster');
    await page.locator(emailSettingsSelector).fill('user@localhost.fr');
    await page.locator(submitSettingsSelector).click();
    expect((await response).status()).toBe(200);
  });

  test.describe('if there is another user with an email', () => {
    const testAdminEmail = 'admin@localhost.fr';
    let originalAdminAccount: Account;

    // Cypress ran this once in a `before`, so it happened before the outer hook opened the settings page.
    // A playwright beforeEach runs after the outer one and login navigates away, so the page is reopened here.
    test.beforeEach(async ({ page, jhi }) => {
      const { adminUsername, adminPassword, username, password } = jhi.credentials();
      await jhi.login(adminUsername, adminPassword);
      originalAdminAccount = await getAccount(jhi);
      // need to modify email because default email does not match regex in some frameworks
      expect((await saveAccount(jhi, { ...originalAdminAccount, email: testAdminEmail })).status()).toBe(200);
      await jhi.login(username, password);
      await page.goto('/account/settings');
      await expect(page.locator(emailSettingsSelector)).toHaveValue(testUserEmail);
    });

    test.afterEach(async ({ jhi }) => {
      const { adminUsername, adminPassword } = jhi.credentials();
      await jhi.login(adminUsername, adminPassword);
      expect((await saveAccount(jhi, originalAdminAccount)).status()).toBe(200);
    });

    test("should not be able to change 'user' email to same value", async ({ page }) => {
      const response = settingsSave(page);
      await page.locator(firstNameSettingsSelector).fill('jhipster');
      await page.locator(emailSettingsSelector).fill(testAdminEmail);
      await page.locator(submitSettingsSelector).click();
      expect((await response).status()).toBe(400);
    });
  });
});
