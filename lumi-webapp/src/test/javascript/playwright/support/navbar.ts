import type { Page } from '@playwright/test';

import {
  accountMenuSelector,
  adminMenuSelector,
  entityItemSelector,
  loginItemSelector,
  logoutItemSelector,
  navbarSelector,
  passwordItemSelector,
  registerItemSelector,
  settingsItemSelector,
} from './commands';

/** Ports the Cypress `clickOn*Item` commands; the selectors and the click order are unchanged. */
export class Navbar {
  constructor(private readonly page: Page) {}

  private menu(menuSelector: string) {
    return this.page.locator(navbarSelector).locator(menuSelector);
  }

  private async clickOnAccountItem(itemSelector: string): Promise<void> {
    await this.menu(accountMenuSelector).click();
    await this.menu(accountMenuSelector).locator(itemSelector).click();
  }

  clickOnLoginItem(): Promise<void> {
    return this.clickOnAccountItem(loginItemSelector);
  }

  clickOnLogoutItem(): Promise<void> {
    return this.clickOnAccountItem(logoutItemSelector);
  }

  clickOnRegisterItem(): Promise<void> {
    return this.clickOnAccountItem(registerItemSelector);
  }

  clickOnSettingsItem(): Promise<void> {
    return this.clickOnAccountItem(settingsItemSelector);
  }

  clickOnPasswordItem(): Promise<void> {
    return this.clickOnAccountItem(passwordItemSelector);
  }

  async clickOnAdminMenuItem(item: string): Promise<void> {
    await this.menu(adminMenuSelector).click();
    await this.menu(adminMenuSelector).locator(`.dropdown-item[href="/${item}"]`).click();
  }

  async clickOnEntityMenuItem(entityName: string): Promise<void> {
    await this.menu(entityItemSelector).click();
    await this.menu(entityItemSelector).locator(`.dropdown-item[href="/${entityName}"]`).click();
  }
}
