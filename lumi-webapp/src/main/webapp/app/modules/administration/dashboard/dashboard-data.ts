import {
  BadgeCheck,
  BookOpen,
  BookOpenCheck,
  BookPlus,
  ContactRound,
  Flag,
  GraduationCap,
  MessageCircle,
  UserPlus,
  UserRoundCheck,
  Users,
} from 'lucide-react';

import type { MonitoringItem } from '../components/monitoring-card';
import type { StatCardData } from '../components/stat-card';

export const stats: StatCardData[] = [
  { label: 'Tổng người dùng', value: '12.480', trend: '+12,8%', icon: Users },
  { label: 'Tổng học sinh', value: '11.920', trend: '+13,2%', icon: GraduationCap },
  { label: 'Tổng giảng viên', value: '520', trend: '+8,3%', icon: ContactRound },
  { label: 'Tổng khóa học', value: '186', trend: '+6,9%', icon: BookOpen },
];

export const activities: MonitoringItem[] = [
  { label: 'Người dùng mới đăng ký', time: '5 phút trước', icon: UserPlus },
  { label: 'Giảng viên mới tham gia', time: '20 phút trước', icon: ContactRound },
  { label: 'Khóa học mới được tạo', time: '1 giờ trước', icon: BookPlus },
  { label: 'Khóa học được phê duyệt', time: 'Hôm qua', icon: BadgeCheck },
];

export const requests: MonitoringItem[] = [
  { label: 'Giảng viên chờ xác minh', status: '12 chờ', icon: UserRoundCheck },
  { label: 'Khóa học chờ duyệt', status: '8 chờ', icon: BookOpenCheck },
  { label: 'Nội dung bị báo cáo', status: '3 báo cáo', icon: Flag, warning: true },
  { label: 'Yêu cầu hỗ trợ', status: '6 mới', icon: MessageCircle },
];

export const chartLabels = ['Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10'];
export const totalUsers = '12.480';
export const notificationMessage = '6 yêu cầu hỗ trợ mới cần xem xét.';

export const userDistribution = [
  { label: 'Học sinh', count: '11.920', percentage: '95,51%', role: 'student' },
  { label: 'Giảng viên', count: '520', percentage: '4,17%', role: 'instructor' },
  { label: 'Quản trị viên', count: '40', percentage: '0,32%', role: 'admin' },
];
