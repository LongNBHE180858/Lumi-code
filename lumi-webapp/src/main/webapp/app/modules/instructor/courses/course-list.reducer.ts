import axios from 'axios';

export const COURSES_API = '/api/instructor/courses';
export type CourseStatus = 'DRAFT' | 'PENDING' | 'ACTIVE' | 'HIDDEN' | 'ARCHIVED' | 'BLOCKED';
export interface ICourseListItem {
  id: number;
  version: number;
  title: string;
  subject?: { id: number; name: string } | null;
  grade?: { id: number; name: string } | null;
  bookSeries?: string | null;
  chapterCount: number;
  videoCount: number;
  totalDurationHours: number;
  status: CourseStatus;
  price?: number | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: 'DAY' | 'MONTH' | 'YEAR' | null;
  updatedAt?: string | null;
  thumbnailUrl?: string | null;
}

export const courseStatusLabels: Record<CourseStatus, string> = {
  DRAFT: 'Bản nháp',
  PENDING: 'Chờ duyệt',
  ACTIVE: 'Đã xuất bản',
  HIDDEN: 'Đã ẩn',
  ARCHIVED: 'Đã lưu trữ',
  BLOCKED: 'Đã khóa',
};

export const accessDurationText = (course: ICourseListItem) => {
  const units = { DAY: 'ngày', MONTH: 'tháng', YEAR: 'năm' };
  const price = course.price == null ? 'Chưa đặt học phí' : course.price === 0 ? 'Miễn phí' : 'Một lần';
  return course.accessDurationValue && course.accessDurationUnit
    ? `${price} • ${course.accessDurationValue} ${units[course.accessDurationUnit]}`
    : `${price} • Chưa đặt thời hạn`;
};

export const courseListError = (error: unknown, fallback: string) => {
  const status = (error as { response?: { status?: number } })?.response?.status;
  if (status === 401) return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
  if (status === 403) return 'Tài khoản chưa có quyền giảng viên đang hoạt động.';
  if (status === 404) return 'Khóa học không còn tồn tại hoặc bạn không có quyền truy cập.';
  if (status === 409)
    return 'Khóa học đã thay đổi hoặc đang chờ duyệt/đã xuất bản. Danh sách đã được tải lại, vui lòng kiểm tra trước khi xóa.';
  return fallback;
};

export const fetchCoursePage = async (params: Record<string, string | number>, signal: AbortSignal) => {
  const response = await axios.get<ICourseListItem[]>(COURSES_API, { params, signal });
  const total = Number(response.headers['x-total-count']);
  if (!Array.isArray(response.data) || !Number.isInteger(total) || total < 0) throw new Error('Invalid course list response');
  return { courses: response.data, total };
};
