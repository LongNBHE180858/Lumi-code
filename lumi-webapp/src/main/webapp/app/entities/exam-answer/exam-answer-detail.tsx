import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './exam-answer.reducer';

export const ExamAnswerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const examAnswerEntity = useAppSelector(state => state.examAnswer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="examAnswerDetailsHeading">
          <Translate contentKey="lumiApp.examAnswer.detail.title">ExamAnswer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{examAnswerEntity.id}</dd>
          <dt>
            <span id="textAnswer">
              <Translate contentKey="lumiApp.examAnswer.textAnswer">Text Answer</Translate>
            </span>
          </dt>
          <dd>{examAnswerEntity.textAnswer}</dd>
          <dt>
            <span id="isCorrect">
              <Translate contentKey="lumiApp.examAnswer.isCorrect">Is Correct</Translate>
            </span>
          </dt>
          <dd>{examAnswerEntity.isCorrect ? 'true' : 'false'}</dd>
          <dt>
            <span id="score">
              <Translate contentKey="lumiApp.examAnswer.score">Score</Translate>
            </span>
          </dt>
          <dd>{examAnswerEntity.score}</dd>
          <dt>
            <Translate contentKey="lumiApp.examAnswer.attempt">Attempt</Translate>
          </dt>
          <dd>{examAnswerEntity.attempt ? examAnswerEntity.attempt.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.examAnswer.question">Question</Translate>
          </dt>
          <dd>{examAnswerEntity.question ? examAnswerEntity.question.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.examAnswer.option">Option</Translate>
          </dt>
          <dd>{examAnswerEntity.option ? examAnswerEntity.option.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/exam-answer" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/exam-answer/${examAnswerEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExamAnswerDetail;
