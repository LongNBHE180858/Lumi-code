import { type APIRequestContext, type APIResponse, type Page, expect, test as base } from '@playwright/test';

import { type Credentials, credentials } from './commands';
import { EntityHelper } from './entity';
import { managementInfo } from './management';
import { Navbar } from './navbar';

export interface AuthenticatedRequest {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  url: string;
  body?: unknown;
  form?: Record<string, string>;
  headers?: Record<string, string>;
  failOnStatusCode?: boolean;
}

/**
 * The Cypress suite relies on custom commands (`cy.login`, `cy.authenticatedRequest`, …). Playwright has no
 * command registry, so the same surface is exposed through fixtures and the specs stay a line-by-line port.
 */
export class JHipster {
  constructor(readonly page: Page) {}

  /**
   * `cy.request` shares the browser's cookie jar. The standalone `request` fixture does not: it is an isolated
   * context, so session and oauth2 applications would answer 401. `page.request` is the sharing equivalent.
   */
  get request(): APIRequestContext {
    return this.page.request;
  }

  readonly navbar = new Navbar(this.page);
  readonly entity = new EntityHelper(this.page);

  credentials(): Credentials {
    return credentials();
  }

  private async bearerToken(): Promise<string | undefined> {
    const raw = await this.page.evaluate(name => window.sessionStorage.getItem(name), 'jhi-authenticationToken');
    if (!raw) return undefined;
    return JSON.parse(raw) as string;
  }

  /** Mirrors `cy.authenticatedRequest`: attaches the credentials the client stores after a login. */
  async authenticatedRequest({ method = 'GET', url, body, form, headers = {} }: AuthenticatedRequest): Promise<APIResponse> {
    const token = await this.bearerToken();
    return this.request.fetch(url, {
      method,
      headers: { ...headers, ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      ...(form === undefined ? {} : { form }),
      ...(body === undefined ? {} : { data: body }),
    });
  }

  /** Mirrors `cy.login`. Cypress caches this with `cy.session`; Playwright reuses the browser context per file. */
  async login(username: string, password: string): Promise<void> {
    await this.page.goto('/');
    // Cypress warms the cookie jar with a GET and then posts through its authenticatedRequest command, which
    // carries the csrf header. Posting directly is rejected by a session application.
    await this.request.get('/api/account');
    const response = await this.authenticatedRequest({
      method: 'POST',
      url: '/api/authenticate',
      body: { username, password },
    });
    expect(response.status()).toBe(200);
    const { id_token: idToken } = (await response.json()) as { id_token: string };
    await this.page.evaluate(
      ([name, token]) => window.sessionStorage.setItem(name, token),
      ['jhi-authenticationToken', JSON.stringify(idToken)],
    );
  }

  /** Mirrors `cy.getManagementInfo`. */
  async managementInfo(): Promise<Record<string, unknown>> {
    return managementInfo(this.request);
  }
}

export const test = base.extend<{ jhi: JHipster }>({
  jhi: async ({ page }, use) => {
    await use(new JHipster(page));
  },
});

export { expect };
