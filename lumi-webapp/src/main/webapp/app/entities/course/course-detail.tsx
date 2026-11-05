import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course.reducer';

export const CourseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseEntity = useAppSelector(state => state.course.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseDetailsHeading">
          <Translate contentKey="lumiApp.course.detail.title">Course</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.course.title">Title</Translate>
            </span>
          </dt>
          <dd>{courseEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="lumiApp.course.description">Description</Translate>
            </span>
          </dt>
          <dd>{courseEntity.description}</dd>
          <dt>
            <span id="thumbnailUrl">
              <Translate contentKey="lumiApp.course.thumbnailUrl">Thumbnail Url</Translate>
            </span>
          </dt>
          <dd>{courseEntity.thumbnailUrl}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="lumiApp.course.price">Price</Translate>
            </span>
          </dt>
          <dd>{courseEntity.price}</dd>
          <dt>
            <span id="shortDescription">
              <Translate contentKey="lumiApp.course.shortDescription">Short Description</Translate>
            </span>
          </dt>
          <dd>{courseEntity.shortDescription}</dd>
          <dt>
            <span id="bookSeries">
              <Translate contentKey="lumiApp.course.bookSeries">Book Series</Translate>
            </span>
          </dt>
          <dd>{courseEntity.bookSeries}</dd>
          <dt>
            <span id="targetAudience">
              <Translate contentKey="lumiApp.course.targetAudience">Target Audience</Translate>
            </span>
          </dt>
          <dd>{courseEntity.targetAudience}</dd>
          <dt>
            <span id="prerequisites">
              <Translate contentKey="lumiApp.course.prerequisites">Prerequisites</Translate>
            </span>
          </dt>
          <dd>{courseEntity.prerequisites}</dd>
          <dt>
            <span id="learningObjectives">
              <Translate contentKey="lumiApp.course.learningObjectives">Learning Objectives</Translate>
            </span>
          </dt>
          <dd>{courseEntity.learningObjectives}</dd>
          <dt>
            <span id="introVideoUrl">
              <Translate contentKey="lumiApp.course.introVideoUrl">Intro Video Url</Translate>
            </span>
          </dt>
          <dd>{courseEntity.introVideoUrl}</dd>
          <dt>
            <span id="validDurationDays">
              <Translate contentKey="lumiApp.course.validDurationDays">Valid Duration Days</Translate>
            </span>
          </dt>
          <dd>{courseEntity.validDurationDays}</dd>
          <dt>
            <span id="accessDurationValue">
              <Translate contentKey="lumiApp.course.accessDurationValue">Access Duration Value</Translate>
            </span>
          </dt>
          <dd>{courseEntity.accessDurationValue}</dd>
          <dt>
            <span id="accessDurationUnit">
              <Translate contentKey="lumiApp.course.accessDurationUnit">Access Duration Unit</Translate>
            </span>
          </dt>
          <dd>{courseEntity.accessDurationUnit}</dd>
          <dt>
            <span id="version">
              <Translate contentKey="lumiApp.course.version">Version</Translate>
            </span>
          </dt>
          <dd>{courseEntity.version}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.course.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseEntity.status}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="lumiApp.course.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{courseEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.course.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{courseEntity.createdAt ? <TextFormat value={courseEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.course.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{courseEntity.updatedAt ? <TextFormat value={courseEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="storageLimitBytes">
              <Translate contentKey="lumiApp.course.storageLimitBytes">Storage Limit Bytes</Translate>
            </span>
          </dt>
          <dd>{courseEntity.storageLimitBytes}</dd>
          <dt>
            <Translate contentKey="lumiApp.course.author">Author</Translate>
          </dt>
          <dd>{courseEntity.author ? courseEntity.author.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.course.subject">Subject</Translate>
          </dt>
          <dd>{courseEntity.subject ? courseEntity.subject.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.course.grade">Grade</Translate>
          </dt>
          <dd>{courseEntity.grade ? courseEntity.grade.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course/${courseEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseDetail;
