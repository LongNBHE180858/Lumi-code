import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course-enrollment.reducer';

export const CourseEnrollmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseEnrollmentEntity = useAppSelector(state => state.courseEnrollment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseEnrollmentDetailsHeading">
          <Translate contentKey="lumiApp.courseEnrollment.detail.title">CourseEnrollment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseEnrollmentEntity.id}</dd>
          <dt>
            <span id="progressPercent">
              <Translate contentKey="lumiApp.courseEnrollment.progressPercent">Progress Percent</Translate>
            </span>
          </dt>
          <dd>{courseEnrollmentEntity.progressPercent}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.courseEnrollment.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseEnrollmentEntity.status}</dd>
          <dt>
            <span id="enrolledAt">
              <Translate contentKey="lumiApp.courseEnrollment.enrolledAt">Enrolled At</Translate>
            </span>
          </dt>
          <dd>
            {courseEnrollmentEntity.enrolledAt ? (
              <TextFormat value={courseEnrollmentEntity.enrolledAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="expiresAt">
              <Translate contentKey="lumiApp.courseEnrollment.expiresAt">Expires At</Translate>
            </span>
          </dt>
          <dd>
            {courseEnrollmentEntity.expiresAt ? (
              <TextFormat value={courseEnrollmentEntity.expiresAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.courseEnrollment.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {courseEnrollmentEntity.updatedAt ? (
              <TextFormat value={courseEnrollmentEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.courseEnrollment.course">Course</Translate>
          </dt>
          <dd>{courseEnrollmentEntity.course ? courseEnrollmentEntity.course.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseEnrollment.student">Student</Translate>
          </dt>
          <dd>{courseEnrollmentEntity.student ? courseEnrollmentEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseEnrollment.lastAccessedLesson">Last Accessed Lesson</Translate>
          </dt>
          <dd>{courseEnrollmentEntity.lastAccessedLesson ? courseEnrollmentEntity.lastAccessedLesson.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course-enrollment" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course-enrollment/${courseEnrollmentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseEnrollmentDetail;
