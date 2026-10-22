import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './knowledge-edge.reducer';

export const KnowledgeEdgeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const knowledgeEdgeEntity = useAppSelector(state => state.knowledgeEdge.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="knowledgeEdgeDetailsHeading">
          <Translate contentKey="lumiApp.knowledgeEdge.detail.title">KnowledgeEdge</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{knowledgeEdgeEntity.id}</dd>
          <dt>
            <span id="relationType">
              <Translate contentKey="lumiApp.knowledgeEdge.relationType">Relation Type</Translate>
            </span>
          </dt>
          <dd>{knowledgeEdgeEntity.relationType}</dd>
          <dt>
            <Translate contentKey="lumiApp.knowledgeEdge.fromNode">From Node</Translate>
          </dt>
          <dd>{knowledgeEdgeEntity.fromNode ? knowledgeEdgeEntity.fromNode.title : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.knowledgeEdge.toNode">To Node</Translate>
          </dt>
          <dd>{knowledgeEdgeEntity.toNode ? knowledgeEdgeEntity.toNode.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/knowledge-edge" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/knowledge-edge/${knowledgeEdgeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default KnowledgeEdgeDetail;
