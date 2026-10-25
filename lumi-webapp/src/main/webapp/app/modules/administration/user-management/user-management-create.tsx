import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isEmail, translate } from 'react-jhipster';
import { Link, useNavigate } from 'react-router';
import { ArrowLeft, Save } from 'lucide-react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { languages, locales } from 'app/config/translation';
import { Authority } from 'app/shared/jhipster/constants';

import { createUser, getRoles, reset } from './user-management.reducer';
import { getManagedRoleLabel, MANAGED_ROLES } from './user-role';

export const UserManagementCreate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  useEffect(() => {
    dispatch(reset());
    dispatch(getRoles());
    return () => {
      dispatch(reset());
    };
  }, [dispatch]);

  const handleClose = () => {
    navigate('/admin/user-management');
  };

  const saveUser = values => {
    const { businessRole, ...userValues } = values;
    const payload = {
      ...userValues,
      authorities: [Authority.USER, businessRole],
    };
    dispatch(createUser(payload));
  };

  const loading = useAppSelector(state => state.userManagement.loading);
  const updating = useAppSelector(state => state.userManagement.updating);
  const updateSuccess = useAppSelector(state => state.userManagement.updateSuccess);
  const authorities = useAppSelector(state => state.userManagement.authorities);
  const availableRoles = MANAGED_ROLES.filter(role => role !== Authority.ADMIN && authorities.includes(role));
  const formValues = { businessRole: Authority.STUDENT };

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
            <Translate contentKey="userManagement.home.createLabel">Create a new user</Translate>
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" onSubmit={saveUser} defaultValues={formValues}>
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
              <ValidatedField type="select" name="langKey" data-cy="langKey" label={translate('userManagement.langKey')}>
                {locales.map(locale => (
                  <option value={locale} key={locale}>
                    {languages[locale].name}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField type="select" name="businessRole" data-cy="businessRole" label={translate('userManagement.businessRole')}>
                {availableRoles.map(role => (
                  <option value={role} key={role}>
                    {getManagedRoleLabel(role)}
                  </option>
                ))}
              </ValidatedField>
              {availableRoles.length === 0 && <p className="admin-user-role-unavailable">{translate('userManagement.rolesUnavailable')}</p>}
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

export default UserManagementCreate;
