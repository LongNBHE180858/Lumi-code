// ***********************************************
// Begin Specific Selector Attributes
// ***********************************************

// Navbar
export const navbarSelector = '[data-cy="navbar"]';
export const adminMenuSelector = '[data-cy="adminMenu"]';
export const accountMenuSelector = '[data-cy="accountMenu"]';
export const registerItemSelector = '[data-cy="register"]';
export const settingsItemSelector = '[data-cy="settings"]';
export const passwordItemSelector = '[data-cy="passwordItem"]';
export const loginItemSelector = '[data-cy="login"]';
export const logoutItemSelector = '[data-cy="logout"]';
export const entityItemSelector = '[data-cy="entity"]';

// Login
export const titleLoginSelector = '[data-cy="loginTitle"]';
export const errorLoginSelector = '[data-cy="loginError"]';
export const usernameLoginSelector = '[data-cy="username"]';
export const passwordLoginSelector = '[data-cy="password"]';
export const forgetYourPasswordSelector = '[data-cy="forgetYourPasswordSelector"]';
export const submitLoginSelector = '[data-cy="submit"]';

// Register
export const usernameRegisterSelector = '[data-cy="username"]';
export const emailRegisterSelector = '[data-cy="email"]';
export const firstPasswordRegisterSelector = '[data-cy="firstPassword"]';
export const secondPasswordRegisterSelector = '[data-cy="secondPassword"]';
export const submitRegisterSelector = '[data-cy="submit"]';

// Settings
export const firstNameSettingsSelector = '[data-cy="firstname"]';
export const lastNameSettingsSelector = '[data-cy="lastname"]';
export const emailSettingsSelector = '[data-cy="email"]';
export const submitSettingsSelector = '[data-cy="submit"]';

// Password
export const currentPasswordSelector = '[data-cy="currentPassword"]';
export const newPasswordSelector = '[data-cy="newPassword"]';
export const confirmPasswordSelector = '[data-cy="confirmPassword"]';
export const submitPasswordSelector = '[data-cy="submit"]';

// Reset Password
export const emailResetPasswordSelector = '[data-cy="emailResetPassword"]';
export const submitInitResetPasswordSelector = '[data-cy="submit"]';

// Administration
export const swaggerFrameSelector = 'iframe[data-cy="swagger-frame"]';
export const swaggerPageSelector = '[id="swagger-ui"]';
export const metricsPageHeadingSelector = '[data-cy="metricsPageHeading"]';
export const healthPageHeadingSelector = '[data-cy="healthPageHeading"]';
export const logsPageHeadingSelector = '[data-cy="logsPageHeading"]';
export const configurationPageHeadingSelector = '[data-cy="configurationPageHeading"]';

// ***********************************************
// End Specific Selector Attributes
// ***********************************************

export const classInvalid = 'is-invalid';

export const classValid = 'is-valid';

export interface Credentials {
  adminUsername: string;
  adminPassword: string;
  username: string;
  password: string;
}

/** Mirrors the Cypress `credentials` command: environment overrides win over the generated defaults. */
export const credentials = (): Credentials => ({
  adminUsername: process.env.E2E_USERNAME ?? 'admin',
  adminPassword: process.env.E2E_PASSWORD ?? 'admin',
  username: process.env.E2E_USERNAME ?? 'user',
  password: process.env.E2E_PASSWORD ?? 'user',
});
