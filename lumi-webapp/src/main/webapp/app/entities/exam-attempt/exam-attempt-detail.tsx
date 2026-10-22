import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './exam-attempt.reducer';

export const ExamAttemptDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const examAttemptEntity = useAppSelector(state => state.examAttempt.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="examAttemptDetailsHeading">
          <Translate contentKey="lumiApp.examAttempt.detail.title">ExamAttempt</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{examAttemptEntity.id}</dd>
          <dt>
            <span id="startTime">
              <Translate contentKey="lumiApp.examAttempt.startTime">Start Time</Translate>
            </span>
          </dt>
          <dd>
            {examAttemptEntity.startTime ? <TextFormat value={examAttemptEntity.startTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endTime">
              <Translate contentKey="lumiApp.examAttempt.endTime">End Time</Translate>
            </span>
          </dt>
          <dd>
            {examAttemptEntity.endTime ? <TextFormat value={examAttemptEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="score">
              <Translate contentKey="lumiApp.examAttempt.score">Score</Translate>
            </span>
          </dt>
          <dd>{examAttemptEntity.score}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.examAttempt.status">Status</Translate>
            </span>
          </dt>
          <dd>{examAttemptEntity.status}</dd>
          <dt>
            <span id="rowVersion">
              <Translate contentKey="lumiApp.examAttempt.rowVersion">Row Version</Translate>
            </span>
          </dt>
          <dd>{examAttemptEntity.rowVersion}</dd>
          <dt>
            <Translate contentKey="lumiApp.examAttempt.student">Student</Translate>
          </dt>
          <dd>{examAttemptEntity.student ? examAttemptEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.examAttempt.exam">Exam</Translate>
          </dt>
          <dd>{examAttemptEntity.exam ? examAttemptEntity.exam.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/exam-attempt" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/exam-attempt/${examAttemptEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExamAttemptDetail;
