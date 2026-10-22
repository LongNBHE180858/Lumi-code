import type { InstructorApplication, InstructorApplicationStatus } from './instructor-applications-api';

export type InstructorDialogMode = 'details' | 'approve' | 'reject';

export const statuses: { value: InstructorApplicationStatus; label: string }[] = [
  { value: 'PENDING', label: 'Chờ duyệt' },
  { value: 'ACTIVE', label: 'Đã duyệt' },
  { value: 'REJECTED', label: 'Đã từ chối' },
];

export const statusLabel = (status: InstructorApplicationStatus) => statuses.find(item => item.value === status)?.label ?? status;
export const fullName = (item: InstructorApplication) => [item.firstName, item.lastName].filter(Boolean).join(' ') || item.login;
export const formatDate = (value: string | null) =>
  value ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value)) : '—';
