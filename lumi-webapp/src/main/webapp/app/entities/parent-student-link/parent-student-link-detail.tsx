import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './parent-student-link.reducer';

export const ParentStudentLinkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const parentStudentLinkEntity = useAppSelector(state => state.parentStudentLink.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="parentStudentLinkDetailsHeading">
          <Translate contentKey="lumiApp.parentStudentLink.detail.title">ParentStudentLink</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.id}</dd>
          <dt>
            <span id="linkCode">
              <Translate contentKey="lumiApp.parentStudentLink.linkCode">Link Code</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.linkCode}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.parentStudentLink.status">Status</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.status}</dd>
          <dt>
            <span id="linkedAt">
              <Translate contentKey="lumiApp.parentStudentLink.linkedAt">Linked At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.linkedAt ? (
              <TextFormat value={parentStudentLinkEntity.linkedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.parentStudentLink.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.createdAt ? (
              <TextFormat value={parentStudentLinkEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="codeExpiresAt">
              <Translate contentKey="lumiApp.parentStudentLink.codeExpiresAt">Code Expires At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.codeExpiresAt ? (
              <TextFormat value={parentStudentLinkEntity.codeExpiresAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="confirmedAt">
              <Translate contentKey="lumiApp.parentStudentLink.confirmedAt">Confirmed At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.confirmedAt ? (
              <TextFormat value={parentStudentLinkEntity.confirmedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="revokedAt">
              <Translate contentKey="lumiApp.parentStudentLink.revokedAt">Revoked At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.revokedAt ? (
              <TextFormat value={parentStudentLinkEntity.revokedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="relationshipType">
              <Translate contentKey="lumiApp.parentStudentLink.relationshipType">Relationship Type</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.relationshipType}</dd>
          <dt>
            <span id="guardianVerificationStatus">
              <Translate contentKey="lumiApp.parentStudentLink.guardianVerificationStatus">Guardian Verification Status</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.guardianVerificationStatus}</dd>
          <dt>
            <span id="guardianVerifiedAt">
              <Translate contentKey="lumiApp.parentStudentLink.guardianVerifiedAt">Guardian Verified At</Translate>
            </span>
          </dt>
          <dd>
            {parentStudentLinkEntity.guardianVerifiedAt ? (
              <TextFormat value={parentStudentLinkEntity.guardianVerifiedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="guardianVerificationNote">
              <Translate contentKey="lumiApp.parentStudentLink.guardianVerificationNote">Guardian Verification Note</Translate>
            </span>
          </dt>
          <dd>{parentStudentLinkEntity.guardianVerificationNote}</dd>
          <dt>
            <Translate contentKey="lumiApp.parentStudentLink.parent">Parent</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.parent ? parentStudentLinkEntity.parent.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.parentStudentLink.student">Student</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.student ? parentStudentLinkEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.parentStudentLink.confirmedBy">Confirmed By</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.confirmedBy ? parentStudentLinkEntity.confirmedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.parentStudentLink.guardianVerifiedBy">Guardian Verified By</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.guardianVerifiedBy ? parentStudentLinkEntity.guardianVerifiedBy.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/parent-student-link" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/parent-student-link/${parentStudentLinkEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParentStudentLinkDetail;
