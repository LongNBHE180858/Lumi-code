import './header.scss';

import React, { useEffect, useRef } from 'react';
import { Nav, Navbar } from 'react-bootstrap';

import LoadingBar, { LoadingBarRef } from 'react-top-loading-bar';

import { useAppSelector } from 'app/config/store';
import { AccountMenu, AdminMenu, EntitiesMenu } from '../menus';

import { Brand, Home, LumiNavItem } from './header-components';
import NotificationDropdown from 'app/shared/notification/notification-dropdown';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
}

const Header = (props: IHeaderProps) => {
  const loadingBarRef = useRef<LoadingBarRef>(null);
  const loadingCount = useAppSelector(state => state.loadingBar.count);
  const account = useAppSelector(state => state.authentication.account);

  useEffect(() => {
    if (loadingCount > 0) {
      loadingBarRef.current?.continuousStart();
    } else {
      loadingBarRef.current?.complete();
    }
  }, [loadingCount]);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      <LoadingBar ref={loadingBarRef} className="loading-bar" color="#24a8e8" />
      <Navbar data-cy="navbar" expand="lg" fixed="top" className="jh-navbar lumi-navbar" collapseOnSelect>
        <div className="container-fluid d-flex align-items-center justify-content-between p-0">
          <Brand />
          <Navbar.Toggle aria-controls="header-tabs" aria-label="Menu" />
          <Navbar.Collapse id="header-tabs" className="justify-content-between flex-grow-1">
            {/* Center Navigation Links from Figma */}
            <Nav className="lumi-nav-links mx-auto">
              <Home />
              <LumiNavItem to="/courses" label="Khóa học" />
              {props.isAuthenticated && account?.authorities?.includes('ROLE_STUDENT') && <LumiNavItem to="/ai-chat" label="Trợ lý AI" />}
              <LumiNavItem
                to={account?.authorities?.includes('ROLE_STUDENT') ? '/student/classes' : '/instructor/classes'}
                label="Lớp học"
              />
              <LumiNavItem to="/instructors" label="Giảng viên" />
              <LumiNavItem to="/roadmap" label="Lộ trình" />
              <LumiNavItem to="/news" label="Tin tức" />
              <LumiNavItem to="/contact" label="Liên hệ" />
              {/* {props.isAuthenticated && (
                <LumiNavItem
                  to="/wishlist"
                  label="Yêu thích"
                  data-cy="wishlistHeaderNav"
                />
              )} */}
              {props.isAuthenticated && props.isAdmin && <EntitiesMenu />}
              {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            </Nav>

            {/* Right Action Buttons & Account */}
            <div className="lumi-header-actions">
              {props.isAuthenticated && <NotificationDropdown />}
              <AccountMenu isAuthenticated={props.isAuthenticated} account={account} />
            </div>
          </Navbar.Collapse>
        </div>
      </Navbar>
    </div>
  );
};

export default Header;
