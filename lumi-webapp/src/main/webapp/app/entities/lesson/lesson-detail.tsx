import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
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
            <span id="description">
              <Translate contentKey="lumiApp.lesson.description">Description</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.description}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="lumiApp.lesson.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.contentType}</dd>
          <dt>
            <span id="videoUrl">
              <Translate contentKey="lumiApp.lesson.videoUrl">Video Url</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.videoUrl}</dd>
          <dt>
            <span id="videoFileName">
              <Translate contentKey="lumiApp.lesson.videoFileName">Video File Name</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.videoFileName}</dd>
          <dt>
            <span id="videoSizeBytes">
              <Translate contentKey="lumiApp.lesson.videoSizeBytes">Video Size Bytes</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.videoSizeBytes}</dd>
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
            <span id="durationSeconds">
              <Translate contentKey="lumiApp.lesson.durationSeconds">Duration Seconds</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.durationSeconds}</dd>
          <dt>
            <span id="videoObjectKey">
              <Translate contentKey="lumiApp.lesson.videoObjectKey">Video Object Key</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.videoObjectKey}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.lesson.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.updatedAt ? <TextFormat value={lessonEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="lumiApp.lesson.chapter">Chapter</Translate>
          </dt>
          <dd>{lessonEntity.chapter ? lessonEntity.chapter.id : ''}</dd>
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
