import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './question-knowledge-link.reducer';

export const QuestionKnowledgeLinkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const questionKnowledgeLinkEntity = useAppSelector(state => state.questionKnowledgeLink.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="questionKnowledgeLinkDetailsHeading">
          <Translate contentKey="lumiApp.questionKnowledgeLink.detail.title">QuestionKnowledgeLink</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{questionKnowledgeLinkEntity.id}</dd>
          <dt>
            <span id="weight">
              <Translate contentKey="lumiApp.questionKnowledgeLink.weight">Weight</Translate>
            </span>
          </dt>
          <dd>{questionKnowledgeLinkEntity.weight}</dd>
          <dt>
            <Translate contentKey="lumiApp.questionKnowledgeLink.question">Question</Translate>
          </dt>
          <dd>{questionKnowledgeLinkEntity.question ? questionKnowledgeLinkEntity.question.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.questionKnowledgeLink.knowledgeNode">Knowledge Node</Translate>
          </dt>
          <dd>{questionKnowledgeLinkEntity.knowledgeNode ? questionKnowledgeLinkEntity.knowledgeNode.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/question-knowledge-link" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/question-knowledge-link/${questionKnowledgeLinkEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuestionKnowledgeLinkDetail;
