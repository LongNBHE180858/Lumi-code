import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ai-conversation.reducer';

export const AiConversationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const aiConversationEntity = useAppSelector(state => state.aiConversation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="aiConversationDetailsHeading">
          <Translate contentKey="lumiApp.aiConversation.detail.title">AiConversation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{aiConversationEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.aiConversation.type">Type</Translate>
            </span>
          </dt>
          <dd>{aiConversationEntity.type}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.aiConversation.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {aiConversationEntity.createdAt ? (
              <TextFormat value={aiConversationEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.aiConversation.user">User</Translate>
          </dt>
          <dd>{aiConversationEntity.user ? aiConversationEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiConversation.lesson">Lesson</Translate>
          </dt>
          <dd>{aiConversationEntity.lesson ? aiConversationEntity.lesson.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/ai-conversation" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/ai-conversation/${aiConversationEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AiConversationDetail;
