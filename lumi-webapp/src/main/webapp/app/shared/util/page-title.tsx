import { useEffect } from 'react';
import { useLocation } from 'react-router';
import { useAppSelector } from 'app/config/store';

// Bảng ánh xạ Route -> Tên Tab (hỗ trợ cả Tiếng Việt và Tiếng Anh)
const TITLE_MAP: Record<string, { vi: string; en: string }> = {
  '/': { vi: 'Trang chủ - LUMI', en: 'Home - LUMI' },
  '/login': { vi: 'Đăng nhập - LUMI', en: 'Sign in - LUMI' },
  '/account/register': { vi: 'Đăng ký tài khoản - LUMI', en: 'Register - LUMI' },
  '/account/settings': { vi: 'Cài đặt tài khoản - LUMI', en: 'Settings - LUMI' },
  '/account/password': { vi: 'Đổi mật khẩu - LUMI', en: 'Change Password - LUMI' },
  '/account/reset/request': { vi: 'Quên mật khẩu - LUMI', en: 'Reset Password - LUMI' },
  '/courses': { vi: 'Khóa học - LUMI', en: 'Courses - LUMI' },
  '/instructors': { vi: 'Giảng viên - LUMI', en: 'Instructors - LUMI' },
  '/roadmap': { vi: 'Lộ trình học tập - LUMI', en: 'Learning Roadmap - LUMI' },
  '/news': { vi: 'Tin tức - LUMI', en: 'News - LUMI' },
  '/contact': { vi: 'Liên hệ - LUMI', en: 'Contact - LUMI' },
  '/admin/user-management': { vi: 'Quản lý người dùng - LUMI', en: 'User Management - LUMI' },
  '/admin/instructor-applications': { vi: 'Quản lý giảng viên - LUMI', en: 'Instructor Applications - LUMI' },
  '/admin/catalog': { vi: 'Quản lý danh mục - LUMI', en: 'Catalog Management - LUMI' },
  '/admin/metrics': { vi: 'Chỉ số hệ thống - LUMI', en: 'Application Metrics - LUMI' },
  '/admin/health': { vi: 'Trạng thái hệ thống - LUMI', en: 'System Health - LUMI' },
  '/admin/logs': { vi: 'Nhật ký hệ thống - LUMI', en: 'System Logs - LUMI' },
  '/admin/docs': { vi: 'Tài liệu API - LUMI', en: 'API Docs - LUMI' },
};

export const PageTitleUpdater = () => {
  const location = useLocation();
  const currentLocale = useAppSelector(state => state.locale.currentLocale);

  useEffect(() => {
    const path = location.pathname;
    const lang = currentLocale === 'en' ? 'en' : 'vi';

    // 1. Kiểm tra chính xác đường dẫn trong bảng map
    if (TITLE_MAP[path]) {
      document.title = TITLE_MAP[path][lang];
      return;
    }

    // 2. Xử lý các trang con của /admin/ hoặc /account/
    if (path.startsWith('/admin')) {
      document.title = lang === 'en' ? 'Administration - LUMI' : 'Quản trị hệ thống - LUMI';
      return;
    }

    // 3. Xử lý các trang thực thể (Entities, ví dụ: /course, /lesson,...)
    const firstSegment = path.split('/')[1];
    if (firstSegment) {
      const formatted = firstSegment.charAt(0).toUpperCase() + firstSegment.slice(1);
      document.title = `${formatted} - LUMI`;
      return;
    }

    // Mặc định
    document.title = 'LUMI — Nền tảng học tập trực tuyến';
  }, [location.pathname, currentLocale]);

  return null;
};
