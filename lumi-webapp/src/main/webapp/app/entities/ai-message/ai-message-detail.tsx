import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ai-message.reducer';

export const AiMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const aiMessageEntity = useAppSelector(state => state.aiMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="aiMessageDetailsHeading">
          <Translate contentKey="lumiApp.aiMessage.detail.title">AiMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{aiMessageEntity.id}</dd>
          <dt>
            <span id="senderRole">
              <Translate contentKey="lumiApp.aiMessage.senderRole">Sender Role</Translate>
            </span>
          </dt>
          <dd>{aiMessageEntity.senderRole}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.aiMessage.content">Content</Translate>
            </span>
          </dt>
          <dd>{aiMessageEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.aiMessage.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {aiMessageEntity.createdAt ? <TextFormat value={aiMessageEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.aiMessage.conversation">Conversation</Translate>
          </dt>
          <dd>{aiMessageEntity.conversation ? aiMessageEntity.conversation.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/ai-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/ai-message/${aiMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AiMessageDetail;
