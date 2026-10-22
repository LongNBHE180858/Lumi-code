import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';

import { createEntity, getEntity, reset, updateEntity } from './student-streak.reducer';

export const StudentStreakUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const studentStreakEntity = useAppSelector(state => state.studentStreak.entity);
  const loading = useAppSelector(state => state.studentStreak.loading);
  const updating = useAppSelector(state => state.studentStreak.updating);
  const updateSuccess = useAppSelector(state => state.studentStreak.updateSuccess);

  const handleClose = () => {
    navigate('/student-streak');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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
    if (values.currentStreak !== undefined && typeof values.currentStreak !== 'number') {
      values.currentStreak = Number(values.currentStreak);
    }
    if (values.longestStreak !== undefined && typeof values.longestStreak !== 'number') {
      values.longestStreak = Number(values.longestStreak);
    }
    if (values.gemsBalance !== undefined && typeof values.gemsBalance !== 'number') {
      values.gemsBalance = Number(values.gemsBalance);
    }

    const entity = {
      ...studentStreakEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...studentStreakEntity,
          user: studentStreakEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.studentStreak.home.createOrEditLabel" data-cy="StudentStreakCreateUpdateHeading">
            <Translate contentKey="lumiApp.studentStreak.home.createOrEditLabel">Create or edit a StudentStreak</Translate>
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
                  id="student-streak-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.studentStreak.currentStreak')}
                id="student-streak-currentStreak"
                name="currentStreak"
                data-cy="currentStreak"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentStreak.longestStreak')}
                id="student-streak-longestStreak"
                name="longestStreak"
                data-cy="longestStreak"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentStreak.gemsBalance')}
                id="student-streak-gemsBalance"
                name="gemsBalance"
                data-cy="gemsBalance"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentStreak.lastActiveDate')}
                id="student-streak-lastActiveDate"
                name="lastActiveDate"
                data-cy="lastActiveDate"
                type="date"
              />
              <ValidatedField
                id="student-streak-user"
                name="user"
                data-cy="user"
                label={translate('lumiApp.studentStreak.user')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/student-streak" replace variant="info">
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

export default StudentStreakUpdate;
