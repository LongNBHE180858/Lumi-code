import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './teacher-verification.reducer';

export const TeacherVerificationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const teacherVerificationEntity = useAppSelector(state => state.teacherVerification.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="teacherVerificationDetailsHeading">
          <Translate contentKey="lumiApp.teacherVerification.detail.title">TeacherVerification</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{teacherVerificationEntity.id}</dd>
          <dt>
            <span id="identityDocumentUrl">
              <Translate contentKey="lumiApp.teacherVerification.identityDocumentUrl">Identity Document Url</Translate>
            </span>
          </dt>
          <dd>{teacherVerificationEntity.identityDocumentUrl}</dd>
          <dt>
            <span id="verificationStatus">
              <Translate contentKey="lumiApp.teacherVerification.verificationStatus">Verification Status</Translate>
            </span>
          </dt>
          <dd>{teacherVerificationEntity.verificationStatus}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="lumiApp.teacherVerification.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{teacherVerificationEntity.notes}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="lumiApp.teacherVerification.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {teacherVerificationEntity.submittedAt ? (
              <TextFormat value={teacherVerificationEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="approvedAt">
              <Translate contentKey="lumiApp.teacherVerification.approvedAt">Approved At</Translate>
            </span>
          </dt>
          <dd>
            {teacherVerificationEntity.approvedAt ? (
              <TextFormat value={teacherVerificationEntity.approvedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.teacherVerification.user">User</Translate>
          </dt>
          <dd>{teacherVerificationEntity.user ? teacherVerificationEntity.user.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/teacher-verification" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/teacher-verification/${teacherVerificationEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TeacherVerificationDetail;
