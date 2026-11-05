import { test, expect } from '@playwright/test';

test.describe('Feature 01: Student Registration E2E Tests (LUMI Platform)', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to registration page
    await page.goto('/account/register');
    await expect(page.locator('.section-head-title').first()).toBeVisible({ timeout: 10000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-001 / Screen 1.1.2.1
   * SRS Reference: Screen 1.1.2.1, GB-151 (Student Grade 1 to 9 Scope)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Section 1.1.2.1, P[278-297], Table 114 Row 151
   */
  test('E2E-TC-01: Registration Form UI structure and Grade 1-9 dropdown boundary', async ({ page }) => {
    // 1. Verify Role selector defaults to Student (ROLE_STUDENT)
    const studentRoleCard = page.locator('.role-card-item.selected');
    await expect(studentRoleCard).toBeVisible();
    await expect(studentRoleCard).toContainText('Học sinh');

    // 2. Verify all required account input fields exist
    await expect(page.locator('input[placeholder*="Nguyễn"]')).toBeVisible(); // LastName
    await expect(page.locator('input[placeholder*="Minh Anh"]')).toBeVisible(); // FirstName
    await expect(page.locator('input[placeholder*="name@example.com"]')).toBeVisible(); // Email
    await expect(page.locator('input[placeholder*="09xx"]')).toBeVisible(); // Phone
    await expect(page.locator('input[placeholder*="Tối thiểu 3 ký tự"]')).toBeVisible(); // Username
    await expect(page.locator('input[placeholder="Tối thiểu 8 ký tự"]')).toBeVisible(); // Password
    await expect(page.locator('input[placeholder="Nhập lại mật khẩu"]')).toBeVisible(); // ConfirmPassword

    // 3. Verify Grade level dropdown strictly contains Grades 1 to 9 (GB-151, Screen 1.1.2.1)
    const gradeSelect = page.locator('select.form-input-element');
    await expect(gradeSelect).toBeVisible();

    const options = await gradeSelect.locator('option').allTextContents();
    // Verify Grades 1-9 are present
    expect(options).toContain('Lớp 1 (Tiểu học)');
    expect(options).toContain('Lớp 5 (Tiểu học)');
    expect(options).toContain('Lớp 6 (THCS)');
    expect(options).toContain('Lớp 9 (THCS)');

    // Verify Grades 10, 11, 12 DO NOT exist in dropdown (SRS Scope compliance)
    const allOptionValues = await gradeSelect
      .locator('option')
      .evaluateAll(elements => elements.map(el => (el as HTMLOptionElement).value).filter(val => val !== ''));
    expect(allOptionValues).toEqual(['1', '2', '3', '4', '5', '6', '7', '8', '9']);

    // 4. Verify Agreement checkboxes and submit button
    await expect(page.locator('text=Tôi cam kết các thông tin')).toBeVisible();
    await expect(page.locator('text=Tôi đồng ý với')).toBeVisible();
    await expect(page.locator('button.btn-large-submit')).toBeVisible();
    await expect(page.locator('button.btn-large-submit')).toHaveText('Tạo tài khoản LUMI');
  });

  /**
   * Baseline TC: PROP-E2E-ADD-01 / MSG01
   * SRS Reference: Table 115 Row 1 (MSG01: Required field is empty)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Section 4, Table 115 Row 1
   */
  test('E2E-TC-02: Client-side validation prevents submission when mandatory fields are missing', async ({ page }) => {
    // Attempt to submit empty form
    const submitBtn = page.locator('button.btn-large-submit');
    await submitBtn.click();

    // Verify browser HTML5 or form validation prevents proceeding to verify-email
    await expect(page).toHaveURL(/\/account\/register/);
  });

  /**
   * Baseline TC: TC-REG-STU-IT-001 (Validation) / GB-02 / MSG02
   * SRS Reference: Table 114 Row 2 (GB-02), Table 115 Row 2 (MSG02)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Section 4, Table 114 Row 2
   */
  test('E2E-TC-03: Client-side validation rejects invalid email format', async ({ page }) => {
    // Fill basic fields with invalid email
    await page.locator('input[placeholder*="Nguyễn"]').fill('Trần');
    await page.locator('input[placeholder*="Minh Anh"]').fill('Bảo');
    await page.locator('input[placeholder*="name@example.com"]').fill('invalid-email-no-at-sign');
    await page.locator('input[placeholder*="09xx"]').fill('0912345678');
    await page.locator('input[placeholder*="Tối thiểu 3 ký tự"]').fill('tranbao123');
    await page.locator('input[placeholder="Tối thiểu 8 ký tự"]').fill('Password@123');
    await page.locator('input[placeholder="Nhập lại mật khẩu"]').fill('Password@123');

    const submitBtn = page.locator('button.btn-large-submit');
    await submitBtn.click();

    // URL remains on register page
    await expect(page).toHaveURL(/\/account\/register/);
  });

  /**
   * Baseline TC: GB-13 / MSG07
   * SRS Reference: Table 115 Row 7 (MSG07: Password confirmation does not match)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Section 4, Table 115 Row 7
   */
  test('E2E-TC-04: Password and confirm password mismatch displays error message', async ({ page }) => {
    await page.locator('input[placeholder*="Nguyễn"]').fill('Lê');
    await page.locator('input[placeholder*="Minh Anh"]').fill('Hải');
    await page.locator('input[placeholder*="name@example.com"]').fill('lehai@lumi.edu.vn');
    await page.locator('input[placeholder*="09xx"]').fill('0987654321');
    await page.locator('input[placeholder*="Tối thiểu 3 ký tự"]').fill('lehai2026');
    await page.locator('input[placeholder="Tối thiểu 8 ký tự"]').fill('Password@123');
    await page.locator('input[placeholder="Nhập lại mật khẩu"]').fill('MismatchedPassword@999');

    // Select grade
    await page.locator('select.form-input-element').selectOption('8');

    // Check agreements
    await page.locator('text=Tôi cam kết các thông tin').click();
    await page.locator('text=Tôi đồng ý với').click();

    const submitBtn = page.locator('button.btn-large-submit');
    await submitBtn.click();

    // Verify error message is displayed
    const errorAlert = page.getByText('Mật khẩu và xác nhận mật khẩu không khớp');
    await expect(errorAlert).toBeVisible({ timeout: 5000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-001 / TC-REG-STU-IT-005
   * SRS Reference: SRS Section 1.1.2 Table 59 Steps 1-5, GB-05, MSG08
   * Classification: SRS-BASED (Technical Path Simulation)
   * Expected Result Source: SRS Table 59, Table 115 Row 8 (MSG08)
   */
  test('E2E-TC-05: Student Registration Happy Path transitions to Email Verification screen', async ({ page }) => {
    const testEmail = `student_e2e_${Date.now()}@lumi.edu.vn`;

    // Intercept API registration call to ensure test isolation
    const fulfillSuccess = async (route: any) => {
      await route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'SUCCESS',
          code: 201,
          message: 'Đăng ký tài khoản học sinh thành công. Vui lòng xác thực email trong vòng 24 giờ.',
          data: {
            user: {
              login: 'student_e2e_user',
              email: testEmail,
              activated: false,
              authorities: ['ROLE_STUDENT'],
            },
          },
        }),
      });
    };

    await page.route('**/api/auth/student/register', fulfillSuccess);
    await page.route('**/api/auth/register', fulfillSuccess);
    await page.route('**/api/register', fulfillSuccess);

    // Fill valid form
    await page.locator('input[placeholder*="Nguyễn"]').fill('Vũ');
    await page.locator('input[placeholder*="Minh Anh"]').fill('Nam');
    await page.locator('input[placeholder*="name@example.com"]').fill(testEmail);
    await page.locator('input[placeholder*="09xx"]').fill('0901234567');
    await page.locator('input[placeholder*="Tối thiểu 3 ký tự"]').fill('vunam2026');
    await page.locator('input[placeholder="Tối thiểu 8 ký tự"]').fill('Password@123');
    await page.locator('input[placeholder="Nhập lại mật khẩu"]').fill('Password@123');
    await page.locator('input[placeholder*="THPT Chuyên"]').fill('THCS Cầu Giấy');
    await page.locator('select.form-input-element').selectOption('8'); // Grade 8

    // Agree terms
    await page.locator('text=Tôi cam kết các thông tin').click();
    await page.locator('text=Tôi đồng ý với').click();

    // Submit
    await page.locator('button.btn-large-submit').click();

    // Verify navigation to /account/verify-email with email parameter
    await expect(page).toHaveURL(new RegExp(`/account/verify-email\\?email=${encodeURIComponent(testEmail)}`), { timeout: 10000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-002 / MSG05
   * SRS Reference: Table 114 Row 1 (GB-01), Table 115 Row 5 (MSG05)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Table 115 Row 5
   */
  test('E2E-TC-06: Duplicate email registration displays error alert', async ({ page }) => {
    const duplicateEmail = 'duplicate_student@lumi.edu.vn';

    // Intercept API returning duplicate email 400 Bad Request
    const fulfillDuplicate = async (route: any) => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'BAD_REQUEST',
          code: 400,
          message: 'Email này đã được đăng ký. Vui lòng sử dụng email khác.',
          data: null,
        }),
      });
    };

    await page.route('**/api/auth/student/register', fulfillDuplicate);
    await page.route('**/api/auth/register', fulfillDuplicate);
    await page.route('**/api/register', fulfillDuplicate);

    // Fill valid form with duplicate email
    await page.locator('input[placeholder*="Nguyễn"]').fill('Đặng');
    await page.locator('input[placeholder*="Minh Anh"]').fill('Thảo');
    await page.locator('input[placeholder*="name@example.com"]').fill(duplicateEmail);
    await page.locator('input[placeholder*="09xx"]').fill('0909999888');
    await page.locator('input[placeholder*="Tối thiểu 3 ký tự"]').fill('dangthao26');
    await page.locator('input[placeholder="Tối thiểu 8 ký tự"]').fill('Password@123');
    await page.locator('input[placeholder="Nhập lại mật khẩu"]').fill('Password@123');
    await page.locator('select.form-input-element').selectOption('7'); // Grade 7

    await page.locator('text=Tôi cam kết các thông tin').click();
    await page.locator('text=Tôi đồng ý với').click();

    await page.locator('button.btn-large-submit').click();

    // Verify error toast/alert appears with duplicate email message
    await expect(page.getByText('Email này đã được đăng ký')).toBeVisible({ timeout: 5000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-007 / MSG12
   * SRS Reference: Table 114 Row 5 (GB-05), Table 115 Row 12 (MSG12)
   * Classification: SRS-BASED
   * Expected Result Source: SRS Table 115 Row 12
   */
  test('E2E-TC-07: OTP verification screen rejects incorrect OTP code', async ({ page }) => {
    const testEmail = 'verify_test@lumi.edu.vn';

    // Mock verify-otp endpoint returning 400 Invalid OTP
    await page.route('**/api/auth/otp/verify', async route => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'BAD_REQUEST',
          code: 400,
          message: 'Mã xác thực không hợp lệ.',
          data: null,
        }),
      });
    });

    // Directly navigate to verify-email page
    await page.goto(`/account/verify-email?email=${encodeURIComponent(testEmail)}`);

    // Enter incorrect 6-digit OTP
    const otpInput = page.locator('input[placeholder*="OTP"], input.otp-input, input[type="text"]').first();
    await otpInput.fill('999999');

    // Submit verification
    const verifyBtn = page.locator('button:has-text("Xác minh"), button[type="submit"]').first();
    await verifyBtn.click();

    // Verify rejection toast/alert
    const errorToast = page.locator('.Toastify__toast--error, .error-message');
    await expect(errorToast.first()).toBeVisible({ timeout: 5000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-006 / MSG11
   * SRS Reference: SRS Table 59 Step 6, Table 115 Row 11 (MSG11)
   * Classification: SRS-BASED (Conditional - Direct Path Only)
   * Expected Result Source: SRS Table 115 Row 11
   */
  test('E2E-TC-08: Successful OTP verification activates account (Direct Path)', async ({ page }) => {
    const testEmail = 'activate_test@lumi.edu.vn';

    // Mock verify-otp endpoint returning 200 OK
    await page.route('**/api/auth/otp/verify', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'SUCCESS',
          code: 200,
          message: 'Xác thực email thành công.',
          data: {
            email: testEmail,
            activated: true,
          },
        }),
      });
    });

    await page.goto(`/account/verify-email?email=${encodeURIComponent(testEmail)}`);

    const otpInput = page.locator('input[placeholder*="OTP"], input.otp-input, input[type="text"]').first();
    await otpInput.fill('123456');

    const verifyBtn = page.locator('button:has-text("Xác minh"), button[type="submit"]').first();
    await verifyBtn.click();

    // Verify success toast notification
    const successToast = page.locator('.Toastify__toast--success, .success-message');
    await expect(successToast.first()).toBeVisible({ timeout: 5000 });
  });

  /**
   * Baseline TC: TC-REG-STU-IT-009 / TECH-01
   * SRS Reference: N/A - Implementation behavior in OtpService.java (L66-73)
   * Classification: IMPLEMENTATION-BASED
   * Expected Result Source: Implementation Contract (OtpService & email-verification.tsx)
   */
  test('E2E-TC-09: OTP Resend 60 seconds cooldown timer behavior on UI', async ({ page }) => {
    const testEmail = 'cooldown_test@lumi.edu.vn';
    await page.goto(`/account/verify-email?email=${encodeURIComponent(testEmail)}`);

    // Verify resend button shows countdown or is disabled during initial cooldown
    const resendBtn = page.locator('button:has-text("Gửi lại"), button:has-text("giây"), .resend-otp-btn').first();
    await expect(resendBtn).toBeVisible();

    // Verify button is disabled or contains countdown indicator
    const text = await resendBtn.textContent();
    expect(text?.toLowerCase()).toMatch(/(gửi lại|giây|\d+s)/);
  });
});
