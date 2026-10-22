import React from 'react';
import { NavItem, NavLink, NavbarBrand } from 'react-bootstrap';
import { NavLink as Link, useLocation } from 'react-router';

import logo from '/content/images/logo-lumi.png';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src={logo} alt="LUMI Logo" className="lumi-logo-img" />
  </div>
);

export const Brand = () => (
  <NavbarBrand as={Link} to="/" className="brand-logo d-flex align-items-center">
    <BrandIcon />
  </NavbarBrand>
);

export interface NavMenuProps {
  to: string;
  label: React.ReactNode;
  exact?: boolean;
  'data-cy'?: string;
}

export const LumiNavItem = ({ to, label, exact = false, 'data-cy': dataCy }: NavMenuProps) => {
  const location = useLocation();
  const isActive = exact ? location.pathname === to : location.pathname.startsWith(to) && to !== '/' ? true : location.pathname === to;

  return (
    <NavItem className="lumi-nav-item">
      <NavLink as={Link} to={to} className={`lumi-nav-link ${isActive ? 'active' : ''}`} data-cy={dataCy}>
        <span>{label}</span>
        {isActive && <span className="active-indicator" />}
      </NavLink>
    </NavItem>
  );
};

export const Home = () => <LumiNavItem to="/" label="Trang chủ" exact />;
