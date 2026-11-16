import axios from 'axios';

export const auditLogPageSize = 10;

export interface AuditLogItem {
  id: number;
  actorLogin: string;
  action: string;
  targetEntity: string;
  targetId: string;
  ipAddress: string | null;
  details: string | null;
  occurredAt: string | null;
}

export interface AuditLogFilters {
  actor: string;
  action: string;
  targetEntity: string;
  targetId: string;
  from: string;
  to: string;
}

export interface AuditLogPage {
  content: AuditLogItem[];
  totalElements: number;
  totalPages: number;
  number: number;
}

export type AuditLogExportOptions = { mode: 'count'; limit: number } | { mode: 'range'; from: string; to: string };

const paramsFor = (filters: AuditLogFilters) => {
  const params: Record<string, string> = {};
  for (const field of ['actor', 'action', 'targetEntity', 'targetId'] as const) {
    if (filters[field].trim()) params[field] = filters[field].trim();
  }
  if (filters.from) params.from = new Date(filters.from).toISOString();
  if (filters.to) params.to = new Date(filters.to).toISOString();
  return params;
};

export const auditLogsApi = {
  list: (filters: AuditLogFilters, page: number) =>
    axios.get<AuditLogPage>('api/admin/audit-logs', { params: { ...paramsFor(filters), page, size: auditLogPageSize } }),
  export(filters: AuditLogFilters, options: AuditLogExportOptions) {
    const params = paramsFor({ ...filters, from: '', to: '' });
    if (options.mode === 'count') {
      params.limit = String(options.limit);
    } else {
      params.from = new Date(options.from).toISOString();
      params.to = new Date(options.to).toISOString();
    }
    return axios.get<Blob>('api/admin/audit-logs/export', { params, responseType: 'blob' });
  },
};
