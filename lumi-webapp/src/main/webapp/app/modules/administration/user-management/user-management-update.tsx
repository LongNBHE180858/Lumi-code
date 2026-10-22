import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isEmail, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';
import { ArrowLeft, CircleAlert, Save } from 'lucide-react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { languages, locales } from 'app/config/translation';
import { Authority } from 'app/shared/jhipster/constants';

import { createUser, getAdminCounts, getRoles, getUser, reset, updateUser } from './user-management.reducer';
import { getManagedRole, getManagedRoleLabel, MANAGED_ROLES } from './user-role';

export const UserManagementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { login } = useParams<'login'>();
  const isNew = login === undefined;

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getUser(login));
      dispatch(getAdminCounts());
    }
    dispatch(getRoles());
    return () => {
      dispatch(reset());
    };
  }, [login]);

  const handleClose = () => {
    navigate('/admin/user-management');
  };

  const saveUser = values => {
    const { businessRole, ...userValues } = values;
    const selectedRole = roleLocked ? Authority.ADMIN : businessRole;
    const payload = {
      ...userValues,
      id: isNew ? undefined : user.id,
      activated: roleLocked ? user.activated : userValues.activated,
      authorities: [Authority.USER, selectedRole],
    };
    if (isNew) {
      dispatch(createUser(payload));
    } else {
      dispatch(updateUser(payload));
    }
  };

  const user = useAppSelector(state => state.userManagement.user);
  const loading = useAppSelector(state => state.userManagement.loading);
  const updating = useAppSelector(state => state.userManagement.updating);
  const updateSuccess = useAppSelector(state => state.userManagement.updateSuccess);
  const account = useAppSelector(state => state.authentication.account);
  const adminCounts = useAppSelector(state => state.userManagement.adminCounts);
  const authorities = useAppSelector(state => state.userManagement.authorities);
  const availableRoles = MANAGED_ROLES.filter(role => authorities.includes(role));
  const currentRole = getManagedRole(user.authorities);
  const isSelf = Boolean(!isNew && account.login && user.login && account.login.toLowerCase() === user.login.toLowerCase());
  const isLastAdmin =
    !isNew &&
    currentRole === Authority.ADMIN &&
    (!adminCounts || adminCounts.adminCount <= 1 || (user.activated && adminCounts.activeAdminCount <= 1));
  const roleLocked = currentRole === Authority.ADMIN && (isSelf || isLastAdmin);
  const formValues = { ...user, businessRole: currentRole };

  // Navigate back only once the request has completed, otherwise a concurrent request may observe a stale state.
  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  return (
    <div className="admin-user-management admin-user-form">
      <Row className="justify-content-center">
        <Col md="8">
          <h1 data-cy="UserManagementCreateUpdateHeading">
            {isNew ? (
              <Translate contentKey="userManagement.home.createLabel">Create a new user</Translate>
            ) : (
              <Translate contentKey="userManagement.home.editLabel">Edit user</Translate>
            )}
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" onSubmit={saveUser} defaultValues={formValues}>
              {!isNew && user.id != null && (
                <div className="admin-user-readonly-id" data-cy="id">
                  <span>{translate('global.field.id')}</span>
                  <strong>{user.id}</strong>
                </div>
              )}
              <ValidatedField
                type="text"
                name="login"
                className="admin-user-required-field"
                data-cy="login"
                label={translate('userManagement.login')}
                validate={{
                  required: {
                    value: true,
                    message: translate('register.messages.validate.login.required'),
                  },
                  pattern: {
                    value: /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/,
                    message: translate('register.messages.validate.login.pattern'),
                  },
                  minLength: {
                    value: 1,
                    message: translate('register.messages.validate.login.minlength'),
                  },
                  maxLength: {
                    value: 50,
                    message: translate('register.messages.validate.login.maxlength'),
                  },
                }}
              />
              <ValidatedField
                type="text"
                name="firstName"
                data-cy="firstName"
                label={translate('userManagement.firstName')}
                validate={{
                  maxLength: {
                    value: 50,
                    message: translate('entity.validation.maxlength', { max: 50 }),
                  },
                }}
              />
              <ValidatedField
                type="text"
                name="lastName"
                data-cy="lastName"
                label={translate('userManagement.lastName')}
                validate={{
                  maxLength: {
                    value: 50,
                    message: translate('entity.validation.maxlength', { max: 50 }),
                  },
                }}
              />
              <ValidatedField
                name="email"
                className="admin-user-required-field"
                data-cy="email"
                label={translate('global.form.email.label')}
                placeholder={translate('global.form.email.placeholder')}
                type="email"
                validate={{
                  required: {
                    value: true,
                    message: translate('global.messages.validate.email.required'),
                  },
                  minLength: {
                    value: 5,
                    message: translate('global.messages.validate.email.minlength'),
                  },
                  maxLength: {
                    value: 254,
                    message: translate('global.messages.validate.email.maxlength'),
                  },
                  validate: v => isEmail(v) || translate('global.messages.validate.email.invalid'),
                }}
              />
              <ValidatedField
                type="checkbox"
                name="activated"
                data-cy="activated"
                check
                value={true}
                label={translate('userManagement.activated')}
                disabled={roleLocked}
              />
              <ValidatedField type="select" name="langKey" data-cy="langKey" label={translate('userManagement.langKey')}>
                {locales.map(locale => (
                  <option value={locale} key={locale}>
                    {languages[locale].name}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                type="select"
                name="businessRole"
                data-cy="businessRole"
                label={translate('userManagement.businessRole')}
                disabled={roleLocked}
              >
                {availableRoles.map(role => (
                  <option value={role} key={role}>
                    {getManagedRoleLabel(role)}
                  </option>
                ))}
              </ValidatedField>
              {availableRoles.length === 0 && <p className="admin-user-role-locked">{translate('userManagement.rolesUnavailable')}</p>}
              {roleLocked && (
                <p className="admin-user-role-locked">
                  <CircleAlert size={16} aria-hidden="true" />
                  <span>{translate('userManagement.protectedAdminRole')}</span>
                </p>
              )}
              <div className="admin-user-form-actions">
                <Button
                  as={Link as any}
                  to="/admin/user-management"
                  replace
                  variant="light"
                  className="admin-user-button admin-user-button-secondary"
                  data-cy="entityCreateCancelButton"
                >
                  <ArrowLeft size={16} aria-hidden="true" />
                  <span>
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                <Button
                  variant="primary"
                  className="admin-user-button admin-user-button-primary"
                  type="submit"
                  disabled={updating || availableRoles.length === 0}
                  data-cy="entityCreateSaveButton"
                >
                  <Save size={16} aria-hidden="true" />
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </div>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default UserManagementUpdate;
