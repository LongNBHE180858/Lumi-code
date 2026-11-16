import axios from 'axios';

export interface DashboardCounts {
  students: number;
  parents: number;
  instructors: number;
  courses: number;
  users: number;
}

export interface DashboardData {
  counts: DashboardCounts;
  previousMonth: DashboardCounts | null;
  monthlyUsers: { month: string; users: number | null }[];
  activities: ActivityEvent[];
  requests: { instructorApplications: number; courseReviews: number; supportTickets: number };
  unclassifiedUsers: number;
}

export interface ActivityEvent {
  kind: 'USER_REGISTERED' | 'INSTRUCTOR_APPROVED' | 'COURSE_CREATED' | 'COURSE_APPROVED';
  occurredAt: string;
}

export interface PendingRequest {
  kind: 'INSTRUCTOR_APPLICATION' | 'COURSE_REVIEW' | 'SUPPORT_TICKET';
  id: number;
  label: string;
}

export interface DashboardPage<T> {
  items: T[];
  hasMore: boolean;
}

export const getAdminDashboard = (signal?: AbortSignal) => axios.get<DashboardData>('/api/admin/dashboard', { signal });

export const getDashboardActivities = (page: number, search: string, signal?: AbortSignal) =>
  axios.get<DashboardPage<ActivityEvent>>('/api/admin/dashboard/activities', { params: { page, size: 20, search }, signal });

export const getDashboardRequests = (page: number, search: string, signal?: AbortSignal) =>
  axios.get<DashboardPage<PendingRequest>>('/api/admin/dashboard/requests', { params: { page, size: 20, search }, signal });
