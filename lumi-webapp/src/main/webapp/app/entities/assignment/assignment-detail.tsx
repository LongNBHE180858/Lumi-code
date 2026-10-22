import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assignment.reducer';

export const AssignmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const assignmentEntity = useAppSelector(state => state.assignment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assignmentDetailsHeading">
          <Translate contentKey="lumiApp.assignment.detail.title">Assignment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.assignment.title">Title</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.title}</dd>
          <dt>
            <span id="instructions">
              <Translate contentKey="lumiApp.assignment.instructions">Instructions</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.instructions}</dd>
          <dt>
            <span id="dueAt">
              <Translate contentKey="lumiApp.assignment.dueAt">Due At</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.dueAt ? <TextFormat value={assignmentEntity.dueAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="maxScore">
              <Translate contentKey="lumiApp.assignment.maxScore">Max Score</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.maxScore}</dd>
          <dt>
            <span id="allowLate">
              <Translate contentKey="lumiApp.assignment.allowLate">Allow Late</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.allowLate ? 'true' : 'false'}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="lumiApp.assignment.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.assignment.studyClass">Study Class</Translate>
          </dt>
          <dd>{assignmentEntity.studyClass ? assignmentEntity.studyClass.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/assignment" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/assignment/${assignmentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssignmentDetail;
