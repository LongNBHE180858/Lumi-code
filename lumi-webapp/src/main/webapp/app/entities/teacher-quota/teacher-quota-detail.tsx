import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './teacher-quota.reducer';

export const TeacherQuotaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const teacherQuotaEntity = useAppSelector(state => state.teacherQuota.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="teacherQuotaDetailsHeading">
          <Translate contentKey="lumiApp.teacherQuota.detail.title">TeacherQuota</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{teacherQuotaEntity.id}</dd>
          <dt>
            <span id="planExpiresAt">
              <Translate contentKey="lumiApp.teacherQuota.planExpiresAt">Plan Expires At</Translate>
            </span>
          </dt>
          <dd>
            {teacherQuotaEntity.planExpiresAt ? (
              <TextFormat value={teacherQuotaEntity.planExpiresAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="usedStorageBytes">
              <Translate contentKey="lumiApp.teacherQuota.usedStorageBytes">Used Storage Bytes</Translate>
            </span>
          </dt>
          <dd>{teacherQuotaEntity.usedStorageBytes}</dd>
          <dt>
            <span id="aiUsedCount">
              <Translate contentKey="lumiApp.teacherQuota.aiUsedCount">Ai Used Count</Translate>
            </span>
          </dt>
          <dd>{teacherQuotaEntity.aiUsedCount}</dd>
          <dt>
            <span id="rowVersion">
              <Translate contentKey="lumiApp.teacherQuota.rowVersion">Row Version</Translate>
            </span>
          </dt>
          <dd>{teacherQuotaEntity.rowVersion}</dd>
          <dt>
            <Translate contentKey="lumiApp.teacherQuota.user">User</Translate>
          </dt>
          <dd>{teacherQuotaEntity.user ? teacherQuotaEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.teacherQuota.currentPlan">Current Plan</Translate>
          </dt>
          <dd>{teacherQuotaEntity.currentPlan ? teacherQuotaEntity.currentPlan.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/teacher-quota" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/teacher-quota/${teacherQuotaEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TeacherQuotaDetail;
