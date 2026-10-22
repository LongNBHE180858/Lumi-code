export const MESSAGE_ALERT_HEADER_NAME = 'x-lumi-alert';
export const MESSAGE_ERROR_HEADER_NAME = 'x-lumi-error';
export const MESSAGE_PARAM_HEADER_NAME = 'x-lumi-params';

export const AUTHENTICATION_TOKEN_KEY = 'jhi-authenticationToken';

export enum Authority {
  ADMIN = 'ROLE_ADMIN',
  USER = 'ROLE_USER',
  STUDENT = 'ROLE_STUDENT',
  INSTRUCTOR = 'ROLE_INSTRUCTOR',
  PARENT = 'ROLE_PARENT',
}
