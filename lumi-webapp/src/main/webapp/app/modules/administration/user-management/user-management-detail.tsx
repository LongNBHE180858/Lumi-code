import React, { useEffect } from 'react';
import { Button, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';
import { ArrowLeft } from 'lucide-react';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { languages } from 'app/config/translation';

import { getUser } from './user-management.reducer';
import { getManagedRole, getManagedRoleLabel } from './user-role';

export const UserManagementDetail = () => {
  const dispatch = useAppDispatch();

  const { login } = useParams<'login'>();

  useEffect(() => {
    dispatch(getUser(login!));
  }, []);

  const user = useAppSelector(state => state.userManagement.user);

  return (
    <div className="admin-user-management admin-user-detail">
      <h2 data-cy="userManagementDetailsHeading">
        <Translate contentKey="userManagement.detail.title">User</Translate> [<strong>{user.login}</strong>]
      </h2>
      <Row size="md">
        <dl className="jh-entity-details">
          <dt>
            <Translate contentKey="global.field.id">ID</Translate>
          </dt>
          <dd>{user.id}</dd>
          <dt>
            <Translate contentKey="userManagement.login">Login</Translate>
          </dt>
          <dd>
            <span>{user.login}</span>&nbsp;
            {user.activated ? (
              <span className="admin-user-status admin-user-status-active">
                <Translate contentKey="userManagement.activated">Activated</Translate>
              </span>
            ) : (
              <span className="admin-user-status admin-user-status-inactive">
                <Translate contentKey="userManagement.deactivated">Deactivated</Translate>
              </span>
            )}
          </dd>
          <dt>
            <Translate contentKey="userManagement.firstName">First Name</Translate>
          </dt>
          <dd>{user.firstName}</dd>
          <dt>
            <Translate contentKey="userManagement.lastName">Last Name</Translate>
          </dt>
          <dd>{user.lastName}</dd>
          <dt>
            <Translate contentKey="userManagement.email">Email</Translate>
          </dt>
          <dd>{user.email}</dd>
          <dt>
            <Translate contentKey="userManagement.langKey">Lang Key</Translate>
          </dt>
          <dd>{user.langKey ? languages[user.langKey].name : undefined}</dd>
          <dt>
            <Translate contentKey="userManagement.createdBy">Created By</Translate>
          </dt>
          <dd>{user.createdBy}</dd>
          <dt>
            <Translate contentKey="userManagement.createdDate">Created Date</Translate>
          </dt>
          <dd>{user.createdDate && <TextFormat value={user.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}</dd>
          <dt>
            <Translate contentKey="userManagement.lastModifiedBy">Last Modified By</Translate>
          </dt>
          <dd>{user.lastModifiedBy}</dd>
          <dt>
            <Translate contentKey="userManagement.lastModifiedDate">Last Modified Date</Translate>
          </dt>
          <dd>
            {user.lastModifiedDate && <TextFormat value={user.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}
          </dd>
          <dt>
            <Translate contentKey="userManagement.profiles">Profiles</Translate>
          </dt>
          <dd>
            <span className={`admin-user-role admin-user-role-${getManagedRole(user.authorities).toLowerCase().replace('role_', '')}`}>
              {getManagedRoleLabel(getManagedRole(user.authorities))}
            </span>
          </dd>
        </dl>
      </Row>
      <Button
        as={Link as any}
        to="/admin/user-management"
        replace
        variant="light"
        className="admin-user-button admin-user-button-secondary"
        data-cy="entityDetailsBackButton"
      >
        <ArrowLeft size={16} className="align-middle" aria-hidden="true" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.back">Back</Translate>
        </span>
      </Button>
    </div>
  );
};

export default UserManagementDetail;
