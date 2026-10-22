import {
  classInvalid,
  classValid,
  emailResetPasswordSelector,
  forgetYourPasswordSelector,
  submitInitResetPasswordSelector,
  usernameLoginSelector,
} from '../../support/commands';
import { expect, test } from '../../support/fixtures';

test.describe('forgot your password', () => {
  test.beforeEach(async ({ page, jhi }) => {
    const { username } = jhi.credentials();
    await page.goto('');
    await jhi.navbar.clickOnLoginItem();
    await expect(page.locator(usernameLoginSelector)).toBeVisible();
    await page.locator(usernameLoginSelector).fill(username);
    await page.locator(forgetYourPasswordSelector).click();
  });

  test('requires email', async ({ page }) => {
    // Submitting is what validates the form. No force: it would skip the actionability wait and race the
    // hydration of the react form, which is what made this assertion flaky.
    await page.locator(submitInitResetPasswordSelector).click();
    await expect(page.locator(emailResetPasswordSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(emailResetPasswordSelector).fill('user@gmail.com');
    await page.locator(submitInitResetPasswordSelector).click();
    await expect(page.locator(emailResetPasswordSelector)).toHaveClass(new RegExp(classValid));
  });

  test('should be able to init reset password', async ({ page }) => {
    const response = page.waitForResponse(r => r.url().includes('/api/account/reset-password/init') && r.request().method() === 'POST');
    await page.locator(emailResetPasswordSelector).fill('user@gmail.com');
    await page.locator(submitInitResetPasswordSelector).click();
    expect((await response).status()).toBe(200);
  });
});
