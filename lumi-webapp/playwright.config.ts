import { defineConfig, devices } from '@playwright/test';

// `ng e2e` starts the dev server itself and reports its url through PLAYWRIGHT_TEST_BASE_URL; without it the
// suite would target the backend port, which serves no frontend while the dev server is the one hosting it.
const baseURL = process.env.E2E_BASE_URL ?? process.env.PLAYWRIGHT_TEST_BASE_URL ?? 'http://localhost:8080/';

export default defineConfig({
  testDir: './src/test/javascript/playwright/e2e',
  // Not *.spec.ts: the client unit test runner (vitest) globs that by default and would try to run the e2e specs.
  testMatch: '**/*.pw.ts',
  outputDir: 'target/playwright/results',
  // The Cypress suite retries twice; keep the same tolerance for a fair parity comparison.
  retries: 2,
  fullyParallel: false,
  // The specs create and delete server side data, so they must not race each other.
  workers: 1,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never', outputFolder: 'target/playwright/report' }]] : [['list']],
  use: {
    baseURL,
    viewport: { width: 1200, height: 720 },
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    // Off like cypress; the ci workflows turn it on through the `ci: e2e-video` pull request label.
    video: process.env.PLAYWRIGHT_VIDEO === 'true' ? 'on' : 'off',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
