import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './question.reducer';

export const QuestionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const questionEntity = useAppSelector(state => state.question.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="questionDetailsHeading">
          <Translate contentKey="lumiApp.question.detail.title">Question</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{questionEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.question.content">Content</Translate>
            </span>
          </dt>
          <dd>{questionEntity.content}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.question.type">Type</Translate>
            </span>
          </dt>
          <dd>{questionEntity.type}</dd>
          <dt>
            <span id="difficulty">
              <Translate contentKey="lumiApp.question.difficulty">Difficulty</Translate>
            </span>
          </dt>
          <dd>{questionEntity.difficulty}</dd>
          <dt>
            <span id="explanation">
              <Translate contentKey="lumiApp.question.explanation">Explanation</Translate>
            </span>
          </dt>
          <dd>{questionEntity.explanation}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="lumiApp.question.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{questionEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.question.bank">Bank</Translate>
          </dt>
          <dd>{questionEntity.bank ? questionEntity.bank.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/question" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/question/${questionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuestionDetail;
