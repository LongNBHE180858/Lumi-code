import {
  classInvalid,
  classValid,
  confirmPasswordSelector,
  currentPasswordSelector,
  newPasswordSelector,
  submitPasswordSelector,
} from '../../support/commands';
import { expect, test } from '../../support/fixtures';

const passwordSave = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => response.url().includes('/api/account/change-password') && response.request().method() === 'POST');

test.describe('/account/password', () => {
  test.beforeEach(async ({ page, jhi }) => {
    const { username, password } = jhi.credentials();
    await jhi.login(username, password);
    await page.goto('/account/password');
  });

  test('should be accessible through menu', async ({ page, jhi }) => {
    await page.goto('');
    await jhi.navbar.clickOnPasswordItem();
    await expect(page).toHaveURL(url => url.pathname === '/account/password');
  });

  test('requires current password', async ({ page }) => {
    await page.locator(submitPasswordSelector).click();
    await expect(page.locator(currentPasswordSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(currentPasswordSelector).fill('wrong-current-password');
    await page.locator(currentPasswordSelector).blur();
    await expect(page.locator(currentPasswordSelector)).toHaveClass(new RegExp(classValid));
  });

  test('requires new password', async ({ page }) => {
    await page.locator(submitPasswordSelector).click();
    await expect(page.locator(newPasswordSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(newPasswordSelector).fill('jhipster');
    await page.locator(newPasswordSelector).blur();
    await expect(page.locator(newPasswordSelector)).toHaveClass(new RegExp(classValid));
  });

  test('requires confirm new password', async ({ page }) => {
    await page.locator(submitPasswordSelector).click();
    await page.locator(newPasswordSelector).fill('jhipster');
    await expect(page.locator(confirmPasswordSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(confirmPasswordSelector).fill('jhipster');
    await page.locator(confirmPasswordSelector).blur();
    await expect(page.locator(confirmPasswordSelector)).toHaveClass(new RegExp(classValid));
  });

  test('should fail to update password when using incorrect current password', async ({ page }) => {
    const response = passwordSave(page);
    await page.locator(currentPasswordSelector).fill('wrong-current-password');
    await page.locator(newPasswordSelector).fill('jhipster');
    await page.locator(confirmPasswordSelector).fill('jhipster');
    await page.locator(submitPasswordSelector).click();
    expect((await response).status()).toBe(400);
  });

  test('should be able to update password', async ({ page, jhi }) => {
    const { password } = jhi.credentials();
    const response = passwordSave(page);
    await page.locator(currentPasswordSelector).fill(password);
    await page.locator(newPasswordSelector).fill(password);
    await page.locator(confirmPasswordSelector).fill(password);
    await page.locator(submitPasswordSelector).click();
    expect((await response).status()).toBe(200);
  });
});
