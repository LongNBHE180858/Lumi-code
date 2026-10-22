import React from 'react';
import { DropdownItem } from 'react-bootstrap';
import { NavLink as Link } from 'react-router';

import { IconProp } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { type LucideIcon } from 'lucide-react';

export interface IMenuItem {
  children: React.ReactNode;
  icon?: IconProp;
  lucideIcon?: LucideIcon;
  to: string;
  id?: string;
  'data-cy'?: string;
  end?: boolean;
}

const MenuItem = (props: IMenuItem) => {
  const { to, icon, lucideIcon: Lucide, id, children } = props;

  return (
    <DropdownItem as={Link} to={to} id={id} data-cy={props['data-cy']}>
      {Lucide ? <Lucide size={16} className="align-middle" aria-hidden="true" /> : icon && <FontAwesomeIcon icon={icon} />} {children}
    </DropdownItem>
  );
};

export default MenuItem;
