import { BadgeCheck, BookOpenCheck, BookPlus, ContactRound, MessageCircle, UserPlus, UserRoundCheck } from 'lucide-react';

import type { MonitoringItem } from '../components/monitoring-card';
import type { ActivityEvent, DashboardData, PendingRequest } from './dashboard-api';

const activityLabels = {
  USER_REGISTERED: { label: 'Người dùng mới đăng ký', icon: UserPlus },
  INSTRUCTOR_APPROVED: { label: 'Giảng viên mới tham gia', icon: ContactRound },
  COURSE_CREATED: { label: 'Khóa học mới được tạo', icon: BookPlus },
  COURSE_APPROVED: { label: 'Khóa học được phê duyệt', icon: BadgeCheck },
};

const relativeTime = (value: string) => {
  const elapsed = Math.max(0, Date.now() - new Date(value).getTime());
  const minutes = Math.floor(elapsed / 60000);
  if (minutes < 1) return 'Vừa xong';
  if (minutes < 60) return `${minutes} phút trước`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} giờ trước`;
  const days = Math.floor(hours / 24);
  return `${days} ngày trước`;
};

export const getActivityItems = (activities: ActivityEvent[], startIndex = 0): MonitoringItem[] =>
  activities.map((activity, index) => ({
    id: `${activity.kind}-${activity.occurredAt}-${startIndex + index}`,
    ...activityLabels[activity.kind],
    time: relativeTime(activity.occurredAt),
  }));

export const getRequestItems = (requests: DashboardData['requests']): MonitoringItem[] => [
  {
    id: 'instructor-applications',
    label: 'Giảng viên chờ xác minh',
    status: `${requests.instructorApplications} chờ`,
    icon: UserRoundCheck,
    href: '/admin/instructor-applications',
  },
  {
    id: 'course-reviews',
    label: 'Khóa học chờ duyệt',
    status: `${requests.courseReviews} chờ`,
    icon: BookOpenCheck,
    href: '/admin/course-review',
  },
  {
    id: 'support-tickets',
    label: 'Yêu cầu hỗ trợ',
    status: `${requests.supportTickets} mới`,
    icon: MessageCircle,
    href: '/admin/support-tickets',
  },
];

export const getPendingRequestItems = (requests: PendingRequest[]): MonitoringItem[] =>
  requests.map(request => {
    switch (request.kind) {
      case 'INSTRUCTOR_APPLICATION':
        return {
          id: `${request.kind}-${request.id}`,
          label: request.label,
          status: 'Chờ',
          icon: UserRoundCheck,
          href: '/admin/instructor-applications',
        };
      case 'COURSE_REVIEW':
        return {
          id: `${request.kind}-${request.id}`,
          label: request.label,
          status: 'Chờ',
          icon: BookOpenCheck,
          href: `/admin/course-review/${request.id}`,
        };
      case 'SUPPORT_TICKET':
        return {
          id: `${request.kind}-${request.id}`,
          label: request.label,
          status: 'Mới',
          icon: MessageCircle,
          href: `/admin/support-tickets/${request.id}`,
        };
      default:
        return {
          id: `${request.kind}-${request.id}`,
          label: request.label,
          status: 'Chờ',
          icon: MessageCircle,
        };
    }
  });
