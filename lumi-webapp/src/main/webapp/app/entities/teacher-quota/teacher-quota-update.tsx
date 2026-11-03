import React, { useEffect, useState } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { premiumPackagesApi } from 'app/modules/administration/premium-packages/premium-packages-api';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import type { ISystemPlan } from 'app/shared/model/system-plan.model';

import { createEntity, getEntity, reset, updateEntity } from './teacher-quota.reducer';

export const TeacherQuotaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const [systemPlans, setSystemPlans] = useState<ISystemPlan[]>([]);
  const teacherQuotaEntity = useAppSelector(state => state.teacherQuota.entity);
  const loading = useAppSelector(state => state.teacherQuota.loading);
  const updating = useAppSelector(state => state.teacherQuota.updating);
  const updateSuccess = useAppSelector(state => state.teacherQuota.updateSuccess);

  const handleClose = () => {
    navigate('/teacher-quota');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    premiumPackagesApi
      .list(0, 100)
      .then(response => setSystemPlans(response.data.map(plan => ({ id: plan.id, name: plan.name }))))
      .catch(() => setSystemPlans([]));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.planExpiresAt = convertDateTimeToServer(values.planExpiresAt);
    if (values.usedStorageBytes !== undefined && typeof values.usedStorageBytes !== 'number') {
      values.usedStorageBytes = Number(values.usedStorageBytes);
    }
    if (values.aiUsedCount !== undefined && typeof values.aiUsedCount !== 'number') {
      values.aiUsedCount = Number(values.aiUsedCount);
    }
    if (values.rowVersion !== undefined && typeof values.rowVersion !== 'number') {
      values.rowVersion = Number(values.rowVersion);
    }

    const entity = {
      ...teacherQuotaEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      currentPlan: systemPlans.find(it => it.id?.toString() === values.currentPlan?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          planExpiresAt: displayDefaultDateTime(),
        }
      : {
          ...teacherQuotaEntity,
          planExpiresAt: convertDateTimeFromServer(teacherQuotaEntity.planExpiresAt),
          user: teacherQuotaEntity?.user?.id,
          currentPlan: teacherQuotaEntity?.currentPlan?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.teacherQuota.home.createOrEditLabel" data-cy="TeacherQuotaCreateUpdateHeading">
            <Translate contentKey="lumiApp.teacherQuota.home.createOrEditLabel">Create or edit a TeacherQuota</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="teacher-quota-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.teacherQuota.planExpiresAt')}
                id="teacher-quota-planExpiresAt"
                name="planExpiresAt"
                data-cy="planExpiresAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.teacherQuota.usedStorageBytes')}
                id="teacher-quota-usedStorageBytes"
                name="usedStorageBytes"
                data-cy="usedStorageBytes"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.teacherQuota.aiUsedCount')}
                id="teacher-quota-aiUsedCount"
                name="aiUsedCount"
                data-cy="aiUsedCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.teacherQuota.rowVersion')}
                id="teacher-quota-rowVersion"
                name="rowVersion"
                data-cy="rowVersion"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="teacher-quota-user"
                name="user"
                data-cy="user"
                label={translate('lumiApp.teacherQuota.user')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="teacher-quota-currentPlan"
                name="currentPlan"
                data-cy="currentPlan"
                label={translate('lumiApp.teacherQuota.currentPlan')}
                type="select"
              >
                <option value="" key="0" />
                {systemPlans
                  ? systemPlans.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/teacher-quota" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TeacherQuotaUpdate;
