import './header.scss';

import React, { useEffect, useRef } from 'react';
import { Nav, Navbar } from 'react-bootstrap';

import LoadingBar, { LoadingBarRef } from 'react-top-loading-bar';

import { useAppSelector } from 'app/config/store';
import { AccountMenu, AdminMenu, EntitiesMenu } from '../menus';

import { Brand, Home, LumiNavItem } from './header-components';

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
              {props.isAuthenticated && account.authorities.includes(props.isAdmin) && <EntitiesMenu />}
              {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            </Nav>

            {/* Right Action Buttons & Account */}
            <div className="lumi-header-actions">
              {props.isAuthenticated && (
                <button type="button" className="lumi-notification-btn" aria-label="Thông báo" title="Thông báo" data-cy="notificationBtn">
                  <svg
                    width="18"
                    height="20"
                    viewBox="0 0 18 20"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                    className="lumi-bell-icon"
                    aria-hidden="true"
                  >
                    <path
                      d="M14.25 6.75C14.25 3.85 11.9 1.5 9 1.5C6.1 1.5 3.75 3.85 3.75 6.75C3.75 11.25 1.5 12.75 1.5 14.25H16.5C16.5 12.75 14.25 11.25 14.25 6.75Z"
                      stroke="#102B59"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M7.28 16.5C7.62 17.38 8.24 18 9 18C9.76 18 10.38 17.38 10.72 16.5"
                      stroke="#102B59"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                  <span className="lumi-notification-dot" />
                </button>
              )}
              <AccountMenu isAuthenticated={props.isAuthenticated} account={account} />
            </div>
          </Navbar.Collapse>
        </div>
      </Navbar>
    </div>
  );
};

export default Header;
