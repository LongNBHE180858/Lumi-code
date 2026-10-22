import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './exam-question.reducer';

export const ExamQuestionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const examQuestionEntity = useAppSelector(state => state.examQuestion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="examQuestionDetailsHeading">
          <Translate contentKey="lumiApp.examQuestion.detail.title">ExamQuestion</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{examQuestionEntity.id}</dd>
          <dt>
            <span id="orderIndex">
              <Translate contentKey="lumiApp.examQuestion.orderIndex">Order Index</Translate>
            </span>
          </dt>
          <dd>{examQuestionEntity.orderIndex}</dd>
          <dt>
            <span id="score">
              <Translate contentKey="lumiApp.examQuestion.score">Score</Translate>
            </span>
          </dt>
          <dd>{examQuestionEntity.score}</dd>
          <dt>
            <Translate contentKey="lumiApp.examQuestion.exam">Exam</Translate>
          </dt>
          <dd>{examQuestionEntity.exam ? examQuestionEntity.exam.title : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.examQuestion.question">Question</Translate>
          </dt>
          <dd>{examQuestionEntity.question ? examQuestionEntity.question.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/exam-question" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/exam-question/${examQuestionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExamQuestionDetail;
