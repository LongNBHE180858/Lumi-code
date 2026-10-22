import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './audit-log.reducer';

export const AuditLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const auditLogEntity = useAppSelector(state => state.auditLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditLogDetailsHeading">
          <Translate contentKey="lumiApp.auditLog.detail.title">AuditLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.id}</dd>
          <dt>
            <span id="action">
              <Translate contentKey="lumiApp.auditLog.action">Action</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.action}</dd>
          <dt>
            <span id="targetEntity">
              <Translate contentKey="lumiApp.auditLog.targetEntity">Target Entity</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.targetEntity}</dd>
          <dt>
            <span id="targetId">
              <Translate contentKey="lumiApp.auditLog.targetId">Target Id</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.targetId}</dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="lumiApp.auditLog.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.ipAddress}</dd>
          <dt>
            <span id="details">
              <Translate contentKey="lumiApp.auditLog.details">Details</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.details}</dd>
          <dt>
            <span id="occurredAt">
              <Translate contentKey="lumiApp.auditLog.occurredAt">Occurred At</Translate>
            </span>
          </dt>
          <dd>
            {auditLogEntity.occurredAt ? <TextFormat value={auditLogEntity.occurredAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.auditLog.actor">Actor</Translate>
          </dt>
          <dd>{auditLogEntity.actor ? auditLogEntity.actor.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/audit-log" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/audit-log/${auditLogEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuditLogDetail;
