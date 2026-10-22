import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assignment-student.reducer';

export const AssignmentStudentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const assignmentStudentEntity = useAppSelector(state => state.assignmentStudent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assignmentStudentDetailsHeading">
          <Translate contentKey="lumiApp.assignmentStudent.detail.title">AssignmentStudent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assignmentStudentEntity.id}</dd>
          <dt>
            <Translate contentKey="lumiApp.assignmentStudent.student">Student</Translate>
          </dt>
          <dd>{assignmentStudentEntity.student ? assignmentStudentEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.assignmentStudent.assignment">Assignment</Translate>
          </dt>
          <dd>{assignmentStudentEntity.assignment ? assignmentStudentEntity.assignment.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/assignment-student" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/assignment-student/${assignmentStudentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssignmentStudentDetail;
