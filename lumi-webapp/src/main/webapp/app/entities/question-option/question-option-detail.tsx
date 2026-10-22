import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './question-option.reducer';

export const QuestionOptionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const questionOptionEntity = useAppSelector(state => state.questionOption.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="questionOptionDetailsHeading">
          <Translate contentKey="lumiApp.questionOption.detail.title">QuestionOption</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{questionOptionEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.questionOption.content">Content</Translate>
            </span>
          </dt>
          <dd>{questionOptionEntity.content}</dd>
          <dt>
            <span id="isCorrect">
              <Translate contentKey="lumiApp.questionOption.isCorrect">Is Correct</Translate>
            </span>
          </dt>
          <dd>{questionOptionEntity.isCorrect ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.questionOption.question">Question</Translate>
          </dt>
          <dd>{questionOptionEntity.question ? questionOptionEntity.question.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/question-option" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/question-option/${questionOptionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuestionOptionDetail;
