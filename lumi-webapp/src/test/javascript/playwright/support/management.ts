import type { APIRequestContext } from '@playwright/test';

/** Ports the Cypress `getManagementInfo` command. */
export const managementInfo = async (request: APIRequestContext): Promise<Record<string, unknown>> => {
  const response = await request.get('/management/info');
  return (await response.json()) as Record<string, unknown>;
};
