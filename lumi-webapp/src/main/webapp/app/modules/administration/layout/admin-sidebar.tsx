import React, { useState } from 'react';
import { Link, useLocation } from 'react-router';
import {
  Bell,
  BookOpen,
  ChartNoAxesCombined,
  ContactRound,
  Folder,
  GraduationCap,
  LayoutDashboard,
  Leaf,
  LogOut,
  Menu,
  NotebookPen,
  Settings,
  UsersRound,
  Wallet,
  type LucideIcon,
} from 'lucide-react';

type NavItem = { label: string; icon: LucideIcon; to?: string };

const navigation: { title: string; items: NavItem[] }[] = [
  { title: 'TỔNG QUAN', items: [{ label: 'Tổng quan', icon: LayoutDashboard, to: '/admin/dashboard' }] },
  {
    title: 'QUẢN LÝ',
    items: [
      { label: 'Người dùng', icon: UsersRound, to: '/admin/user-management' },
      { label: 'Học sinh', icon: GraduationCap },
      { label: 'Giảng viên', icon: ContactRound, to: '/admin/instructor-applications' },
      { label: 'Khóa học', icon: BookOpen },
      { label: 'Nội dung học tập', icon: NotebookPen },
      { label: 'Danh mục', icon: Folder, to: '/admin/catalog' },
    ],
  },
  {
    title: 'KINH DOANH',
    items: [
      { label: 'Doanh thu & Thanh toán', icon: Wallet },
      { label: 'Báo cáo & Thống kê', icon: ChartNoAxesCombined },
    ],
  },
  {
    title: 'HỆ THỐNG',
    items: [
      { label: 'Thông báo', icon: Bell },
      { label: 'Cài đặt hệ thống', icon: Settings },
    ],
  },
];

const AdminSidebar = () => {
  const [mobileNavigationOpen, setMobileNavigationOpen] = useState(false);
  const { pathname } = useLocation();

  return (
    <aside className={`admin-sidebar${mobileNavigationOpen ? ' admin-sidebar-open' : ''}`} aria-label="Điều hướng quản trị">
      <div className="admin-brand">
        <Link className="admin-brand-logo" to="/" aria-label="LUMI - Trang chủ">
          <span className="admin-brand-mark" aria-hidden="true">
            L
            <Leaf size={13} />
          </span>
          <strong>LUMI</strong>
        </Link>
        <button
          className="admin-mobile-menu-button"
          type="button"
          aria-label="Mở điều hướng"
          aria-expanded={mobileNavigationOpen}
          onClick={() => setMobileNavigationOpen(!mobileNavigationOpen)}
        >
          <Menu size={24} aria-hidden="true" />
        </button>
      </div>
      <nav className="admin-sidebar-nav">
        {navigation.map(group => (
          <div className="admin-nav-group" key={group.title}>
            <h2>{group.title}</h2>
            {group.items.map(item => {
              const Icon = item.icon;
              return item.to ? (
                <Link
                  className={`admin-nav-item${pathname === item.to ? ' active' : ''}`}
                  to={item.to}
                  key={item.label}
                  aria-current={pathname === item.to ? 'page' : undefined}
                >
                  <Icon size={18} aria-hidden="true" />
                  <span>{item.label}</span>
                </Link>
              ) : (
                <span className="admin-nav-item" key={item.label}>
                  <Icon size={18} aria-hidden="true" />
                  <span>{item.label}</span>
                </span>
              );
            })}
          </div>
        ))}
      </nav>
      <div className="admin-sidebar-bottom">
        <Link className="admin-nav-item" to="/logout">
          <LogOut size={18} aria-hidden="true" />
          <span>Đăng xuất</span>
        </Link>
      </div>
    </aside>
  );
};

export default AdminSidebar;
