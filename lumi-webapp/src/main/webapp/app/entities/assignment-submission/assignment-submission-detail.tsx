import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assignment-submission.reducer';

export const AssignmentSubmissionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const assignmentSubmissionEntity = useAppSelector(state => state.assignmentSubmission.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assignmentSubmissionDetailsHeading">
          <Translate contentKey="lumiApp.assignmentSubmission.detail.title">AssignmentSubmission</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.assignmentSubmission.content">Content</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.content}</dd>
          <dt>
            <span id="score">
              <Translate contentKey="lumiApp.assignmentSubmission.score">Score</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.score}</dd>
          <dt>
            <span id="teacherFeedback">
              <Translate contentKey="lumiApp.assignmentSubmission.teacherFeedback">Teacher Feedback</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.teacherFeedback}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.assignmentSubmission.status">Status</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.status}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="lumiApp.assignmentSubmission.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {assignmentSubmissionEntity.submittedAt ? (
              <TextFormat value={assignmentSubmissionEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.assignmentSubmission.student">Student</Translate>
          </dt>
          <dd>{assignmentSubmissionEntity.student ? assignmentSubmissionEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.assignmentSubmission.assignment">Assignment</Translate>
          </dt>
          <dd>{assignmentSubmissionEntity.assignment ? assignmentSubmissionEntity.assignment.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/assignment-submission" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/assignment-submission/${assignmentSubmissionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssignmentSubmissionDetail;
