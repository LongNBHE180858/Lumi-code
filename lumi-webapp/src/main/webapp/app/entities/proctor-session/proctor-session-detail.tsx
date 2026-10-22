import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './proctor-session.reducer';

export const ProctorSessionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const proctorSessionEntity = useAppSelector(state => state.proctorSession.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="proctorSessionDetailsHeading">
          <Translate contentKey="lumiApp.proctorSession.detail.title">ProctorSession</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{proctorSessionEntity.id}</dd>
          <dt>
            <span id="faceVerified">
              <Translate contentKey="lumiApp.proctorSession.faceVerified">Face Verified</Translate>
            </span>
          </dt>
          <dd>{proctorSessionEntity.faceVerified ? 'true' : 'false'}</dd>
          <dt>
            <span id="warningCount">
              <Translate contentKey="lumiApp.proctorSession.warningCount">Warning Count</Translate>
            </span>
          </dt>
          <dd>{proctorSessionEntity.warningCount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.proctorSession.status">Status</Translate>
            </span>
          </dt>
          <dd>{proctorSessionEntity.status}</dd>
          <dt>
            <Translate contentKey="lumiApp.proctorSession.attempt">Attempt</Translate>
          </dt>
          <dd>{proctorSessionEntity.attempt ? proctorSessionEntity.attempt.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/proctor-session" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/proctor-session/${proctorSessionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProctorSessionDetail;
