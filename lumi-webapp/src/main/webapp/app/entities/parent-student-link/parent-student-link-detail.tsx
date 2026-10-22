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
            <Translate contentKey="lumiApp.parentStudentLink.parent">Parent</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.parent ? parentStudentLinkEntity.parent.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.parentStudentLink.student">Student</Translate>
          </dt>
          <dd>{parentStudentLinkEntity.student ? parentStudentLinkEntity.student.login : ''}</dd>
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
