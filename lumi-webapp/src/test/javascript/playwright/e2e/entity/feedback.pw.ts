import { expect, test } from '../../support/fixtures';

const feedbackSubmitUrl = '/feedback';
const feedbackAdminUrl = '/admin/feedback';
const feedbackApiUrl = '/api/feedbacks';

test.describe('Feedback E2E test', () => {
  test.describe('User Feedback Submission', () => {
    test.beforeEach(async ({ jhi }) => {
      const { username, password } = jhi.credentials();
      await jhi.login(username, password);
    });

    test('should load feedback submission page', async ({ page }) => {
      await page.goto(feedbackSubmitUrl);
      await expect(page.getByRole('heading', { name: 'Gửi phản hồi', level: 1 })).toBeVisible();
      await expect(page.locator('[data-cy="feedback-title"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedback-content"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedback-contact-email"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedback-confirm-email"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedback-submit-btn"]')).toBeVisible();
    });

    test('should fit the feedback form on desktop, tablet, and mobile', async ({ page }) => {
      await page.goto(feedbackSubmitUrl);
      await expect(page.locator('#feedback-submit-form')).toBeVisible();

      for (const width of [1440, 800, 390]) {
        await page.setViewportSize({ width, height: 900 });
        const hasHorizontalOverflow = await page.evaluate(() => document.documentElement.scrollWidth > window.innerWidth);
        expect(hasHorizontalOverflow).toBe(false);
        await expect(page.locator('[data-cy="feedback-submit-btn"]')).toBeVisible();
      }
    });

    test('should validate required fields before submission', async ({ page }) => {
      await page.goto(feedbackSubmitUrl);
      await page.locator('[data-cy="feedback-submit-btn"]').click();

      await expect(page.locator('text=Tiêu đề là bắt buộc')).toBeVisible();
      await expect(page.locator('text=Nội dung phản hồi là bắt buộc')).toBeVisible();
      await expect(page.locator('text=Bạn cần xác nhận email liên hệ trước khi gửi')).toBeVisible();
    });

    test('should validate content min length of 10 characters', async ({ page }) => {
      await page.goto(feedbackSubmitUrl);
      await page.locator('[data-cy="feedback-title"]').fill('Tiêu đề hợp lệ');
      await page.locator('[data-cy="feedback-content"]').fill('123456789');
      await page.locator('[data-cy="feedback-confirm-email"]').check();
      await page.locator('[data-cy="feedback-submit-btn"]').click();

      await expect(page.locator('text=Nội dung phản hồi phải có ít nhất 10 ký tự')).toBeVisible();
    });

    test('should submit feedback successfully when valid', async ({ page }) => {
      await page.goto(feedbackSubmitUrl);

      // Select BUG_REPORT type
      await page.locator('[data-cy="feedback-type-BUG_REPORT"]').click();

      // Enter title
      await page.locator('[data-cy="feedback-title"]').fill('Gặp sự cố khi tải video bài học');

      // Enter content
      await page
        .locator('[data-cy="feedback-content"]')
        .fill('Video bài học thường xuyên bị đứng hình ở phút thứ 5. Vui lòng kiểm tra lại chất lượng CDN.');

      // Enter valid contact email (since default user@localhost lacks domain TLD)
      await page.locator('[data-cy="feedback-contact-email"]').fill('student@lumi.edu.vn');

      // Check confirm email checkbox
      await page.locator('[data-cy="feedback-confirm-email"]').check();

      // Intercept submit POST request
      const postPromise = page.waitForResponse(
        response => response.url().includes(feedbackApiUrl) && response.request().method() === 'POST',
      );

      await page.locator('[data-cy="feedback-submit-btn"]').click();

      const postResponse = await postPromise;
      expect(postResponse.status()).toBe(201);

      // Wait for redirect to home
      await page.waitForURL('/', { timeout: 5000 });
      expect(page.url()).toContain('/');
    });
  });

  test.describe('Admin Feedback Management', () => {
    test.beforeEach(async ({ jhi }) => {
      // Login with admin credentials
      await jhi.login('admin', 'admin');
    });

    test('should load admin feedback list page', async ({ page }) => {
      await page.goto(feedbackAdminUrl);
      await expect(page.locator('[data-cy="feedbackAdminHeading"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedbackRefreshBtn"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedbackFilterType"]')).toBeVisible();
      await expect(page.locator('[data-cy="feedbackFilterStatus"]')).toBeVisible();
    });

    test('should filter feedbacks by type and status', async ({ page }) => {
      await page.goto(feedbackAdminUrl);

      // Filter by BUG_REPORT
      await page.locator('[data-cy="feedbackFilterType"]').selectOption('BUG_REPORT');
      await expect(page.locator('[data-cy="feedbackFilterType"]')).toHaveValue('BUG_REPORT');

      // Filter by NEW
      await page.locator('[data-cy="feedbackFilterStatus"]').selectOption('NEW');
      await expect(page.locator('[data-cy="feedbackFilterStatus"]')).toHaveValue('NEW');

      await expect(page.locator('[data-cy="feedbackAdminHeading"]')).toBeVisible();
    });
  });
});
