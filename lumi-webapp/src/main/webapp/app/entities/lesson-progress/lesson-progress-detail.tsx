import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './lesson-progress.reducer';

export const LessonProgressDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const lessonProgressEntity = useAppSelector(state => state.lessonProgress.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonProgressDetailsHeading">
          <Translate contentKey="lumiApp.lessonProgress.detail.title">LessonProgress</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{lessonProgressEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.lessonProgress.status">Status</Translate>
            </span>
          </dt>
          <dd>{lessonProgressEntity.status}</dd>
          <dt>
            <span id="lastPositionSeconds">
              <Translate contentKey="lumiApp.lessonProgress.lastPositionSeconds">Last Position Seconds</Translate>
            </span>
          </dt>
          <dd>{lessonProgressEntity.lastPositionSeconds}</dd>
          <dt>
            <span id="lastAccessedAt">
              <Translate contentKey="lumiApp.lessonProgress.lastAccessedAt">Last Accessed At</Translate>
            </span>
          </dt>
          <dd>
            {lessonProgressEntity.lastAccessedAt ? (
              <TextFormat value={lessonProgressEntity.lastAccessedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="completedAt">
              <Translate contentKey="lumiApp.lessonProgress.completedAt">Completed At</Translate>
            </span>
          </dt>
          <dd>
            {lessonProgressEntity.completedAt ? (
              <TextFormat value={lessonProgressEntity.completedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="rowVersion">
              <Translate contentKey="lumiApp.lessonProgress.rowVersion">Row Version</Translate>
            </span>
          </dt>
          <dd>{lessonProgressEntity.rowVersion}</dd>
          <dt>
            <Translate contentKey="lumiApp.lessonProgress.enrollment">Enrollment</Translate>
          </dt>
          <dd>{lessonProgressEntity.enrollment ? lessonProgressEntity.enrollment.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.lessonProgress.lesson">Lesson</Translate>
          </dt>
          <dd>{lessonProgressEntity.lesson ? lessonProgressEntity.lesson.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/lesson-progress" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/lesson-progress/${lessonProgressEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonProgressDetail;
