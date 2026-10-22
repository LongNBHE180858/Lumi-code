import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './exam.reducer';

export const ExamDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const examEntity = useAppSelector(state => state.exam.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="examDetailsHeading">
          <Translate contentKey="lumiApp.exam.detail.title">Exam</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{examEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.exam.title">Title</Translate>
            </span>
          </dt>
          <dd>{examEntity.title}</dd>
          <dt>
            <span id="durationMinutes">
              <Translate contentKey="lumiApp.exam.durationMinutes">Duration Minutes</Translate>
            </span>
          </dt>
          <dd>{examEntity.durationMinutes}</dd>
          <dt>
            <span id="passScore">
              <Translate contentKey="lumiApp.exam.passScore">Pass Score</Translate>
            </span>
          </dt>
          <dd>{examEntity.passScore}</dd>
          <dt>
            <span id="maxAttempts">
              <Translate contentKey="lumiApp.exam.maxAttempts">Max Attempts</Translate>
            </span>
          </dt>
          <dd>{examEntity.maxAttempts}</dd>
          <dt>
            <span id="shuffleQuestions">
              <Translate contentKey="lumiApp.exam.shuffleQuestions">Shuffle Questions</Translate>
            </span>
          </dt>
          <dd>{examEntity.shuffleQuestions ? 'true' : 'false'}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.exam.status">Status</Translate>
            </span>
          </dt>
          <dd>{examEntity.status}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="lumiApp.exam.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{examEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.exam.studyClass">Study Class</Translate>
          </dt>
          <dd>{examEntity.studyClass ? examEntity.studyClass.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/exam" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/exam/${examEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExamDetail;
