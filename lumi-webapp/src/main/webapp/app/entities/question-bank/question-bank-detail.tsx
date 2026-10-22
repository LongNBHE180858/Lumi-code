import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './question-bank.reducer';

export const QuestionBankDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const questionBankEntity = useAppSelector(state => state.questionBank.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="questionBankDetailsHeading">
          <Translate contentKey="lumiApp.questionBank.detail.title">QuestionBank</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{questionBankEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.questionBank.name">Name</Translate>
            </span>
          </dt>
          <dd>{questionBankEntity.name}</dd>
          <dt>
            <Translate contentKey="lumiApp.questionBank.instructor">Instructor</Translate>
          </dt>
          <dd>{questionBankEntity.instructor ? questionBankEntity.instructor.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/question-bank" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/question-bank/${questionBankEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuestionBankDetail;
