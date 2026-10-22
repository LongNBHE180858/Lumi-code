import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './knowledge-node.reducer';

export const KnowledgeNodeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const knowledgeNodeEntity = useAppSelector(state => state.knowledgeNode.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="knowledgeNodeDetailsHeading">
          <Translate contentKey="lumiApp.knowledgeNode.detail.title">KnowledgeNode</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{knowledgeNodeEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.knowledgeNode.title">Title</Translate>
            </span>
          </dt>
          <dd>{knowledgeNodeEntity.title}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.knowledgeNode.type">Type</Translate>
            </span>
          </dt>
          <dd>{knowledgeNodeEntity.type}</dd>
          <dt>
            <Translate contentKey="lumiApp.knowledgeNode.subject">Subject</Translate>
          </dt>
          <dd>{knowledgeNodeEntity.subject ? knowledgeNodeEntity.subject.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/knowledge-node" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/knowledge-node/${knowledgeNodeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default KnowledgeNodeDetail;
