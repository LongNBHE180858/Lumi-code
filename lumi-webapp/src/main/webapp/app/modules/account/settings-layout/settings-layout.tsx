import './settings-layout.scss';

import React from 'react';
import { Link } from 'react-router';

import { useAppSelector } from 'app/config/store';

export type SettingsTabId = 'profile' | 'change-password' | 'manage-security';

export interface ISettingsLayoutProps {
  activeTab: SettingsTabId;
  pageTitle: string;
  pageSubtitle: string;
  currentBreadcrumb: string;
  children: React.ReactNode;
}

export const SettingsLayout: React.FC<ISettingsLayoutProps> = ({ activeTab, pageTitle, pageSubtitle, currentBreadcrumb, children }) => {
  const account = useAppSelector(state => state.authentication.account);

  const fullName = `${account?.firstName || ''} ${account?.lastName || ''}`.trim() || account?.login || 'Người dùng LUMI';

  const userEmail = account?.email || 'Chưa cập nhật email';

  const sidebarItems: { id: SettingsTabId; label: string; to: string }[] = [
    { id: 'profile', label: 'Hồ sơ cá nhân', to: '/account/settings' },
    { id: 'change-password', label: 'Đổi mật khẩu', to: '/account/password' },
    { id: 'manage-security', label: 'Bảo mật & phiên đăng nhập', to: '/account/security' },
  ];

  return (
    <div className="settings-page-wrapper">
      <div className="settings-page-container">
        {/* Breadcrumb Navigation */}
        <nav className="breadcrumb-bar" aria-label="Đường dẫn điều hướng">
          <Link to="/" className="breadcrumb-item">
            Trang chủ
          </Link>
          <span className="breadcrumb-sep">/</span>
          <Link to="/account/settings" className="breadcrumb-item">
            Cài đặt tài khoản
          </Link>
          <span className="breadcrumb-sep">/</span>
          <span className="breadcrumb-item active">{currentBreadcrumb}</span>
        </nav>

        {/* Page Banner */}
        <div className="settings-header-banner">
          <h1 className="settings-page-title">{pageTitle}</h1>
          <p className="settings-page-desc">{pageSubtitle}</p>
        </div>

        {/* 2-Column Grid */}
        <div className="settings-grid">
          {/* Left Sidebar */}
          <aside className="settings-sidebar-card">
            <div className="sidebar-user-block">
              <span className="sidebar-user-name">{fullName}</span>
              <span className="sidebar-user-email">{userEmail}</span>
              <span className="sidebar-user-status">Đã đăng nhập</span>
            </div>

            <div className="sidebar-nav-list">
              {sidebarItems.map(item => {
                const isActive = item.id === activeTab;
                return (
                  <Link key={item.id} to={item.to} className={`sidebar-nav-item ${isActive ? 'active' : ''}`}>
                    <span>{item.label}</span>
                    {isActive && (
                      <svg
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      >
                        <polyline points="9 18 15 12 9 6" />
                      </svg>
                    )}
                  </Link>
                );
              })}
            </div>
          </aside>

          {/* Right Main Content */}
          <div className="settings-content-wrapper">{children}</div>
        </div>
      </div>
    </div>
  );
};

export default SettingsLayout;
