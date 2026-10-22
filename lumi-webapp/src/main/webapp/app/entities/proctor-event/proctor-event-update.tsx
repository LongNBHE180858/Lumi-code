import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getProctorSessions } from 'app/entities/proctor-session/proctor-session.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, updateEntity } from './proctor-event.reducer';

export const ProctorEventUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const proctorSessions = useAppSelector(state => state.proctorSession.entities);
  const proctorEventEntity = useAppSelector(state => state.proctorEvent.entity);
  const loading = useAppSelector(state => state.proctorEvent.loading);
  const updating = useAppSelector(state => state.proctorEvent.updating);
  const updateSuccess = useAppSelector(state => state.proctorEvent.updateSuccess);

  const handleClose = () => {
    navigate('/proctor-event');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getProctorSessions({}));
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
    values.occurredAt = convertDateTimeToServer(values.occurredAt);

    const entity = {
      ...proctorEventEntity,
      ...values,
      session: proctorSessions.find(it => it.id.toString() === values.session?.toString()),
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
          occurredAt: displayDefaultDateTime(),
        }
      : {
          ...proctorEventEntity,
          occurredAt: convertDateTimeFromServer(proctorEventEntity.occurredAt),
          session: proctorEventEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.proctorEvent.home.createOrEditLabel" data-cy="ProctorEventCreateUpdateHeading">
            <Translate contentKey="lumiApp.proctorEvent.home.createOrEditLabel">Create or edit a ProctorEvent</Translate>
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
                  id="proctor-event-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.proctorEvent.violationType')}
                id="proctor-event-violationType"
                name="violationType"
                data-cy="violationType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.proctorEvent.photoUrl')}
                id="proctor-event-photoUrl"
                name="photoUrl"
                data-cy="photoUrl"
                type="text"
                validate={{
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.proctorEvent.occurredAt')}
                id="proctor-event-occurredAt"
                name="occurredAt"
                data-cy="occurredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="proctor-event-session"
                name="session"
                data-cy="session"
                label={translate('lumiApp.proctorEvent.session')}
                type="select"
                required
              >
                <option value="" key="0" />
                {proctorSessions
                  ? proctorSessions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/proctor-event" replace variant="info">
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

export default ProctorEventUpdate;
