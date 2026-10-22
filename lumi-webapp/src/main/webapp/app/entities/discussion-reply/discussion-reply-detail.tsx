import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './discussion-reply.reducer';

export const DiscussionReplyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const discussionReplyEntity = useAppSelector(state => state.discussionReply.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="discussionReplyDetailsHeading">
          <Translate contentKey="lumiApp.discussionReply.detail.title">DiscussionReply</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{discussionReplyEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.discussionReply.content">Content</Translate>
            </span>
          </dt>
          <dd>{discussionReplyEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.discussionReply.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {discussionReplyEntity.createdAt ? (
              <TextFormat value={discussionReplyEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.discussionReply.user">User</Translate>
          </dt>
          <dd>{discussionReplyEntity.user ? discussionReplyEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.discussionReply.discussion">Discussion</Translate>
          </dt>
          <dd>{discussionReplyEntity.discussion ? discussionReplyEntity.discussion.title : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.discussionReply.parent">Parent</Translate>
          </dt>
          <dd>{discussionReplyEntity.parent ? discussionReplyEntity.parent.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/discussion-reply" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/discussion-reply/${discussionReplyEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DiscussionReplyDetail;
