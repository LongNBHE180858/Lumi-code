import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './discussion.reducer';

export const DiscussionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const discussionEntity = useAppSelector(state => state.discussion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="discussionDetailsHeading">
          <Translate contentKey="lumiApp.discussion.detail.title">Discussion</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{discussionEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.discussion.title">Title</Translate>
            </span>
          </dt>
          <dd>{discussionEntity.title}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.discussion.content">Content</Translate>
            </span>
          </dt>
          <dd>{discussionEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.discussion.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {discussionEntity.createdAt ? <TextFormat value={discussionEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.discussion.student">Student</Translate>
          </dt>
          <dd>{discussionEntity.student ? discussionEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.discussion.lesson">Lesson</Translate>
          </dt>
          <dd>{discussionEntity.lesson ? discussionEntity.lesson.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/discussion" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/discussion/${discussionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DiscussionDetail;
