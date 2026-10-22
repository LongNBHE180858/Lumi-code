import React, { use } from 'react';
import { Dropdown, Nav } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { ReactReduxContext } from 'react-redux';
import { NavLink as Link } from 'react-router';

import MenuItem from 'app/shared/layout/menus/menu-item';

export interface IAccountMenuProps {
  isAuthenticated?: boolean;
  account?: any;
}

export const getRoleLabel = (authorities: string[] = []): string => {
  if (!authorities || authorities.length === 0) {
    return 'Giảng viên';
  }
  if (authorities.includes('ROLE_ADMIN')) {
    return 'Quản trị viên';
  }
  if (authorities.includes('ROLE_INSTRUCTOR')) {
    return 'Giảng viên';
  }
  if (authorities.includes('ROLE_PARENT')) {
    return 'Phụ huynh';
  }
  if (authorities.includes('ROLE_STUDENT')) {
    return 'Học sinh';
  }
  if (authorities.includes('ROLE_USER')) {
    return 'Học sinh';
  }
  return 'Thành viên';
};

export const getAccountDisplayName = (account: any): string => {
  if (!account) return 'Thầy Nguyễn Văn Nam';

  const fullName = [account.firstName, account.lastName].filter(Boolean).join(' ').trim();
  const isInstructor =
    !account.authorities ||
    account.authorities.length === 0 ||
    account.authorities?.includes('ROLE_INSTRUCTOR') ||
    account.authorities?.includes('ROLE_TEACHER');
  if (fullName) {
    if (isInstructor && !/^(thầy|cô)\s+/i.test(fullName)) {
      return `Thầy ${fullName}`;
    }
    return fullName;
  }

  if (account.login) {
    if (isInstructor) {
      return `Thầy ${account.login}`;
    }
    return account.login;
  }

  return 'Thầy Nguyễn Văn Nam';
};

export const getInitials = (name: string): string => {
  if (!name) return 'NN';
  const cleanName = name.replace(/^(thầy|cô|mr|ms|mrs)\.?\s+/i, '').trim();
  const parts = cleanName.split(/\s+/).filter(Boolean);
  if (parts.length === 0) return 'NN';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
};

const accountMenuItemsAuthenticated = (account: any = {}) => {
  const authorities: string[] = account?.authorities || [];
  const isAdmin = authorities.includes('ROLE_ADMIN');
  const isInstructor =
    authorities.includes('ROLE_INSTRUCTOR') || authorities.includes('ROLE_TEACHER') || (!isAdmin && authorities.length === 0);

  return (
    <>
      <MenuItem icon="user" to="/account/settings" data-cy="profileItem">
        Hồ sơ cá nhân
      </MenuItem>

      {/* Menu Wishlist & Feedback */}
      <MenuItem icon="heart" to="/wishlist" data-cy="wishlistMenuItem">
        <Translate contentKey="global.menu.entities.wishlist">Wishlist</Translate>
      </MenuItem>
      <MenuItem icon="comment-dots" to="/feedback" data-cy="feedbackMenuItem">
        <Translate contentKey="global.menu.feedback">Feedback</Translate>
      </MenuItem>

      {/* Menu dành cho Quản trị viên */}
      {isAdmin && (
        <MenuItem icon="users-cog" to="/admin/dashboard" data-cy="adminManagement">
          Quản trị hệ thống
        </MenuItem>
      )}

      {/* Menu dành cho Giảng viên */}
      {(isInstructor || isAdmin) && (
        <>
          <MenuItem icon="book" to="/instructor/courses" data-cy="instructorCourses" end>
            Khóa học của tôi
          </MenuItem>
          <MenuItem icon="tasks" to="/instructor/courses/new" data-cy="builderCourses">
            Tạo khóa học mới
          </MenuItem>
        </>
      )}

      <MenuItem icon="lock" to="/account/password" data-cy="passwordItem">
        <Translate contentKey="global.menu.account.password">Password</Translate>
      </MenuItem>
      <MenuItem icon="shield-alt" to="/account/security" data-cy="securityItem">
        Bảo mật & Phiên đăng nhập
      </MenuItem>
      <MenuItem icon="sign-out-alt" to="/logout" data-cy="logout">
        <Translate contentKey="global.menu.account.logout">Sign out</Translate>
      </MenuItem>
    </>
  );
};

const accountMenuItems = () => (
  <div className="d-flex align-items-center gap-2">
    <Link to="/login" id="login-item" className="lumi-btn-login" data-cy="login">
      Đăng nhập
    </Link>
    <Link to="/account/register" id="register-item" className="lumi-btn-register" data-cy="register">
      Bắt đầu miễn phí
    </Link>
  </div>
);

export const AccountMenu = ({ isAuthenticated = false, account: propAccount }: IAccountMenuProps) => {
  const reduxContext = use(ReactReduxContext);
  const reduxAccount = reduxContext ? reduxContext.store?.getState()?.authentication?.account : null;
  const account = propAccount || reduxAccount || {};

  if (!isAuthenticated) {
    return (
      <div id="account-menu" data-cy="accountMenu" className="lumi-account-guest">
        {accountMenuItems()}
      </div>
    );
  }

  const roleLabel = getRoleLabel(account.authorities);
  const displayName = getAccountDisplayName(account);
  const initials = getInitials(displayName);

  return (
    <Dropdown as={Nav.Item} id="account-menu" data-cy="accountMenu" className="lumi-account-dropdown">
      <Dropdown.Toggle as={Nav.Link} className="lumi-account-trigger d-flex align-items-center">
        {/* Avatar circle (Figma #355:1807) */}
        <div className="lumi-avatar-circle" data-cy="accountAvatar">
          <span className="lumi-avatar-initials">{initials}</span>
        </div>

        {/* User info (Figma #355:1809) */}
        <div className="lumi-user-info">
          <span className="lumi-user-name">{displayName}</span>
          <span className="lumi-user-role">{roleLabel}</span>
        </div>

        {/* Chevron down (Figma #355:1812) */}
        <svg
          width="10"
          height="6"
          viewBox="0 0 10 6"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className="lumi-chevron-down"
          aria-hidden="true"
        >
          <path
            d="M0.569065 0.569127L4.86168 4.86175L9.1543 0.569127"
            stroke="#7894AF"
            strokeWidth="1.6"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </Dropdown.Toggle>

      <Dropdown.Menu renderOnMount align="end" className="lumi-account-dropdown-menu">
        {accountMenuItemsAuthenticated(account)}
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default AccountMenu;
