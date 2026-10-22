import './admin-layout.scss';

import React, { type PropsWithChildren } from 'react';

import AdminHeader from './admin-header';
import AdminSidebar from './admin-sidebar';

interface AdminLayoutProps extends PropsWithChildren {
  searchValue?: string;
  onSearchChange?: (value: string) => void;
  searchLabel?: string;
  notificationMessage?: string;
}

const AdminLayout = ({ children, searchValue, onSearchChange, searchLabel, notificationMessage }: AdminLayoutProps) => (
  <div className="admin-layout">
    <AdminSidebar />
    <div className="admin-workspace">
      <AdminHeader
        searchValue={searchValue}
        onSearchChange={onSearchChange}
        searchLabel={searchLabel}
        notificationMessage={notificationMessage}
      />
      {children}
    </div>
  </div>
);

export default AdminLayout;
