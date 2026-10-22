import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './proctor-event.reducer';

export const ProctorEventDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const proctorEventEntity = useAppSelector(state => state.proctorEvent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="proctorEventDetailsHeading">
          <Translate contentKey="lumiApp.proctorEvent.detail.title">ProctorEvent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{proctorEventEntity.id}</dd>
          <dt>
            <span id="violationType">
              <Translate contentKey="lumiApp.proctorEvent.violationType">Violation Type</Translate>
            </span>
          </dt>
          <dd>{proctorEventEntity.violationType}</dd>
          <dt>
            <span id="photoUrl">
              <Translate contentKey="lumiApp.proctorEvent.photoUrl">Photo Url</Translate>
            </span>
          </dt>
          <dd>{proctorEventEntity.photoUrl}</dd>
          <dt>
            <span id="occurredAt">
              <Translate contentKey="lumiApp.proctorEvent.occurredAt">Occurred At</Translate>
            </span>
          </dt>
          <dd>
            {proctorEventEntity.occurredAt ? (
              <TextFormat value={proctorEventEntity.occurredAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.proctorEvent.session">Session</Translate>
          </dt>
          <dd>{proctorEventEntity.session ? proctorEventEntity.session.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/proctor-event" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/proctor-event/${proctorEventEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProctorEventDetail;
