import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getExamAttempts } from 'app/entities/exam-attempt/exam-attempt.reducer';

import { createEntity, getEntity, reset, updateEntity } from './proctor-session.reducer';

export const ProctorSessionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const examAttempts = useAppSelector(state => state.examAttempt.entities);
  const proctorSessionEntity = useAppSelector(state => state.proctorSession.entity);
  const loading = useAppSelector(state => state.proctorSession.loading);
  const updating = useAppSelector(state => state.proctorSession.updating);
  const updateSuccess = useAppSelector(state => state.proctorSession.updateSuccess);

  const handleClose = () => {
    navigate('/proctor-session');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getExamAttempts({}));
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
    if (values.warningCount !== undefined && typeof values.warningCount !== 'number') {
      values.warningCount = Number(values.warningCount);
    }

    const entity = {
      ...proctorSessionEntity,
      ...values,
      attempt: examAttempts.find(it => it.id.toString() === values.attempt?.toString()),
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
          ...proctorSessionEntity,
          attempt: proctorSessionEntity?.attempt?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.proctorSession.home.createOrEditLabel" data-cy="ProctorSessionCreateUpdateHeading">
            <Translate contentKey="lumiApp.proctorSession.home.createOrEditLabel">Create or edit a ProctorSession</Translate>
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
                  id="proctor-session-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.proctorSession.faceVerified')}
                id="proctor-session-faceVerified"
                name="faceVerified"
                data-cy="faceVerified"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.proctorSession.warningCount')}
                id="proctor-session-warningCount"
                name="warningCount"
                data-cy="warningCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.proctorSession.status')}
                id="proctor-session-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                id="proctor-session-attempt"
                name="attempt"
                data-cy="attempt"
                label={translate('lumiApp.proctorSession.attempt')}
                type="select"
                required
              >
                <option value="" key="0" />
                {examAttempts
                  ? examAttempts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/proctor-session" replace variant="info">
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

export default ProctorSessionUpdate;
