import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './lesson.reducer';

export const LessonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const lessonEntity = useAppSelector(state => state.lesson.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonDetailsHeading">
          <Translate contentKey="lumiApp.lesson.detail.title">Lesson</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <Translate contentKey="lumiApp.lesson.description" />
          </dt>
          <dd>{lessonEntity.description}</dd>
          <dt>
            <Translate contentKey="lumiApp.lesson.videoUrl" />
          </dt>
          <dd>{lessonEntity.videoUrl}</dd>
          <dt>
            <Translate contentKey="lumiApp.lesson.videoFileName" />
          </dt>
          <dd>{lessonEntity.videoFileName}</dd>
          <dt>
            <Translate contentKey="lumiApp.lesson.videoSizeBytes" />
          </dt>
          <dd>{lessonEntity.videoSizeBytes}</dd>
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.lesson.title">Title</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.title}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="lumiApp.lesson.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.contentType}</dd>
          <dt>
            <span id="orderIndex">
              <Translate contentKey="lumiApp.lesson.orderIndex">Order Index</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.orderIndex}</dd>
          <dt>
            <span id="isPreview">
              <Translate contentKey="lumiApp.lesson.isPreview">Is Preview</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.isPreview ? 'true' : 'false'}</dd>
          <dt>
            <span id="durationMinutes">
              <Translate contentKey="lumiApp.lesson.durationMinutes">Duration Minutes</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.durationMinutes}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.lesson.status">Status</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.status}</dd>
          <dt>
            <Translate contentKey="lumiApp.lesson.chapter">Chapter</Translate>
          </dt>
          <dd>{lessonEntity.chapter ? lessonEntity.chapter.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/lesson" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/lesson/${lessonEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonDetail;
