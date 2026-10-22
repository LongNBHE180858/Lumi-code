import {
  errorLoginSelector,
  passwordLoginSelector,
  submitLoginSelector,
  titleLoginSelector,
  usernameLoginSelector,
} from '../../support/commands';
import { expect, test } from '../../support/fixtures';

const authenticate = (page: import('@playwright/test').Page) =>
  page.waitForResponse(response => response.url().includes('/api/authenticate') && response.request().method() === 'POST');

test.describe('login page', () => {
  test.beforeEach(async ({ page, jhi }) => {
    await page.goto('');
    await jhi.navbar.clickOnLoginItem();
    await expect(page.locator(usernameLoginSelector)).toBeFocused();
  });

  test('greets with signin', async ({ page }) => {
    await expect(page.locator(titleLoginSelector)).toBeVisible();
  });

  test('requires username', async ({ page }) => {
    await expect(page.locator(passwordLoginSelector)).toBeVisible();
    await page.locator(passwordLoginSelector).fill('a-password');
    await page.locator(submitLoginSelector).click();
    // login page should stay open when login fails
    await expect(page.locator(titleLoginSelector)).toBeVisible();
  });

  test('requires password', async ({ page }) => {
    await expect(page.locator(usernameLoginSelector)).toBeVisible();
    await page.locator(usernameLoginSelector).fill('a-login');
    await page.locator(submitLoginSelector).click();
    // login page should stay open when login fails
    await expect(page.locator(titleLoginSelector)).toBeVisible();
  });

  test('errors when password is incorrect', async ({ page, jhi }) => {
    const { username } = jhi.credentials();
    const response = authenticate(page);
    await expect(page.locator(usernameLoginSelector)).toBeVisible();
    await page.locator(usernameLoginSelector).fill(username);
    await expect(page.locator(passwordLoginSelector)).toBeVisible();
    await page.locator(passwordLoginSelector).fill('bad-password');
    await page.locator(submitLoginSelector).click();
    expect((await response).status()).toBe(401);
    await expect(page.locator(errorLoginSelector)).toBeVisible();
  });

  test('go to home page when successfully logs in', async ({ page, jhi }) => {
    const { username, password } = jhi.credentials();
    const response = authenticate(page);
    await expect(page.locator(usernameLoginSelector)).toBeVisible();
    await page.locator(usernameLoginSelector).fill(username);
    await expect(page.locator(passwordLoginSelector)).toBeVisible();
    await page.locator(passwordLoginSelector).fill(password);
    await page.locator(submitLoginSelector).click();
    expect((await response).status()).toBe(200);
    await expect(page).toHaveURL(url => url.hash === '');
  });
});
