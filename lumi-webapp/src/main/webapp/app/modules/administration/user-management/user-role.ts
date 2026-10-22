import { translate } from 'react-jhipster';

import { Authority } from 'app/shared/jhipster/constants';

export const MANAGED_ROLES = [Authority.STUDENT, Authority.PARENT, Authority.INSTRUCTOR, Authority.ADMIN] as const;

export const getManagedRole = (authorities: readonly string[] = []): Authority => {
  if (authorities.includes(Authority.ADMIN)) return Authority.ADMIN;
  if (authorities.includes(Authority.INSTRUCTOR)) return Authority.INSTRUCTOR;
  if (authorities.includes(Authority.PARENT)) return Authority.PARENT;
  return Authority.STUDENT;
};

export const getManagedRoleLabel = (role: Authority): string => {
  switch (role) {
    case Authority.ADMIN:
      return translate('userManagement.roles.admin');
    case Authority.INSTRUCTOR:
      return translate('userManagement.roles.teacher');
    case Authority.PARENT:
      return translate('userManagement.roles.parent');
    default:
      return translate('userManagement.roles.student');
  }
};
