import React, { useState } from 'react';
import { Link } from 'react-router';
import { Bell, ChevronDown, Search, UserRound } from 'lucide-react';

interface AdminHeaderProps {
  searchValue?: string;
  onSearchChange?: (value: string) => void;
  searchLabel?: string;
  notificationMessage?: string;
}

const AdminHeader = ({ searchValue = '', onSearchChange, searchLabel = 'Tìm kiếm', notificationMessage }: AdminHeaderProps) => {
  const [openMenu, setOpenMenu] = useState<'profile' | 'notifications' | null>(null);

  return (
    <header className="admin-utility-header">
      {onSearchChange && (
        <label className="admin-search">
          <Search size={18} aria-hidden="true" />
          <input
            value={searchValue}
            onChange={event => onSearchChange(event.target.value)}
            placeholder="Tìm kiếm..."
            aria-label={searchLabel}
          />
        </label>
      )}
      <div className="admin-header-popover-anchor">
        <button
          className="admin-notification-button"
          type="button"
          aria-label="Thông báo"
          aria-expanded={openMenu === 'notifications'}
          onClick={() => setOpenMenu(openMenu === 'notifications' ? null : 'notifications')}
        >
          <Bell size={20} aria-hidden="true" />
          {notificationMessage && <span className="admin-notification-dot" aria-hidden="true" />}
        </button>
        {openMenu === 'notifications' && (
          <div className="admin-popover">
            <strong>Thông báo</strong>
            <p>{notificationMessage ?? 'Chưa có thông báo mới.'}</p>
          </div>
        )}
      </div>
      <span className="admin-header-divider" />
      <div className="admin-header-popover-anchor">
        <button
          className="admin-profile-button"
          type="button"
          aria-expanded={openMenu === 'profile'}
          onClick={() => setOpenMenu(openMenu === 'profile' ? null : 'profile')}
        >
          <span className="admin-avatar">
            <UserRound size={20} aria-hidden="true" />
          </span>
          <span className="admin-profile-label">
            <strong>Quản trị viên</strong>
            <small>Admin</small>
          </span>
          <ChevronDown size={16} aria-hidden="true" />
        </button>
        {openMenu === 'profile' && (
          <div className="admin-popover admin-profile-popover">
            <Link to="/logout">Đăng xuất</Link>
          </div>
        )}
      </div>
    </header>
  );
};

export default AdminHeader;
