import {
  classInvalid,
  classValid,
  emailRegisterSelector,
  firstPasswordRegisterSelector,
  secondPasswordRegisterSelector,
  submitRegisterSelector,
  usernameRegisterSelector,
} from '../../support/commands';
import { expect, test } from '../../support/fixtures';

test.describe('/account/register', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/account/register');
  });

  test('should be accessible through menu', async ({ page, jhi }) => {
    await page.goto('');
    await jhi.navbar.clickOnRegisterItem();
    await expect(page).toHaveURL(url => url.pathname === '/account/register');
  });

  test('should load the register page', async ({ page }) => {
    await expect(page.locator(submitRegisterSelector)).toBeVisible();
  });

  test('requires username', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(usernameRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(usernameRegisterSelector).fill('test');
    await page.locator(usernameRegisterSelector).blur();
    await expect(page.locator(usernameRegisterSelector)).toHaveClass(new RegExp(classValid));
  });

  test('should not accept invalid email', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(emailRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(emailRegisterSelector).fill('testtest.fr');
    await page.locator(emailRegisterSelector).blur();
    await expect(page.locator(emailRegisterSelector)).toHaveClass(new RegExp(classInvalid));
  });

  test('requires email in correct format', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(emailRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(emailRegisterSelector).fill('test@test.fr');
    await page.locator(emailRegisterSelector).blur();
    await expect(page.locator(emailRegisterSelector)).toHaveClass(new RegExp(classValid));
  });

  test('requires first password', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(firstPasswordRegisterSelector).fill('test@test.fr');
    await page.locator(firstPasswordRegisterSelector).blur();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classValid));
  });

  test('requires password and confirm password to be same', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(firstPasswordRegisterSelector).fill('test');
    await page.locator(firstPasswordRegisterSelector).blur();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classValid));
    await expect(page.locator(secondPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(secondPasswordRegisterSelector).fill('test');
    await page.locator(secondPasswordRegisterSelector).blur();
    await expect(page.locator(secondPasswordRegisterSelector)).toHaveClass(new RegExp(classValid));
  });

  test('requires password and confirm password have not the same value', async ({ page }) => {
    await page.locator(submitRegisterSelector).click();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(firstPasswordRegisterSelector).fill('test');
    await page.locator(firstPasswordRegisterSelector).blur();
    await expect(page.locator(firstPasswordRegisterSelector)).toHaveClass(new RegExp(classValid));
    await expect(page.locator(secondPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
    await page.locator(secondPasswordRegisterSelector).fill('otherPassword');
    await page.locator(secondPasswordRegisterSelector).blur();
    await expect(page.locator(secondPasswordRegisterSelector)).toHaveClass(new RegExp(classInvalid));
  });

  test('register a valid user', async ({ page }) => {
    const randomEmail = 'Ned.Glover@yahoo.com';
    const randomUsername = 'Carey.Schmitt';
    const registerSave = page.waitForResponse(
      response => response.url().includes('/api/register') && response.request().method() === 'POST',
    );
    await page.locator(usernameRegisterSelector).fill(randomUsername);
    await page.locator(emailRegisterSelector).fill(randomEmail);
    await page.locator(firstPasswordRegisterSelector).fill('jondoe');
    await page.locator(secondPasswordRegisterSelector).fill('jondoe');
    await page.locator(submitRegisterSelector).click();
    expect((await registerSave).status()).toBe(201);
  });
});
