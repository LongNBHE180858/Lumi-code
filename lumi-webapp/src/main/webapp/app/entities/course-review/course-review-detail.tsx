import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course-review.reducer';

export const CourseReviewDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseReviewEntity = useAppSelector(state => state.courseReview.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseReviewDetailsHeading">
          <Translate contentKey="lumiApp.courseReview.detail.title">CourseReview</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseReviewEntity.id}</dd>
          <dt>
            <span id="rating">
              <Translate contentKey="lumiApp.courseReview.rating">Rating</Translate>
            </span>
          </dt>
          <dd>{courseReviewEntity.rating}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.courseReview.content">Content</Translate>
            </span>
          </dt>
          <dd>{courseReviewEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.courseReview.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {courseReviewEntity.createdAt ? <TextFormat value={courseReviewEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.courseReview.student">Student</Translate>
          </dt>
          <dd>{courseReviewEntity.student ? courseReviewEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseReview.course">Course</Translate>
          </dt>
          <dd>{courseReviewEntity.course ? courseReviewEntity.course.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course-review" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course-review/${courseReviewEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseReviewDetail;
