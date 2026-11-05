import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course-review-request.reducer';

export const CourseReviewRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseReviewRequestEntity = useAppSelector(state => state.courseReviewRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseReviewRequestDetailsHeading">
          <Translate contentKey="lumiApp.courseReviewRequest.detail.title">CourseReviewRequest</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseReviewRequestEntity.id}</dd>
          <dt>
            <span id="courseVersion">
              <Translate contentKey="lumiApp.courseReviewRequest.courseVersion">Course Version</Translate>
            </span>
          </dt>
          <dd>{courseReviewRequestEntity.courseVersion}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.courseReviewRequest.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseReviewRequestEntity.status}</dd>
          <dt>
            <span id="submissionNote">
              <Translate contentKey="lumiApp.courseReviewRequest.submissionNote">Submission Note</Translate>
            </span>
          </dt>
          <dd>{courseReviewRequestEntity.submissionNote}</dd>
          <dt>
            <span id="rightsConfirmedAt">
              <Translate contentKey="lumiApp.courseReviewRequest.rightsConfirmedAt">Rights Confirmed At</Translate>
            </span>
          </dt>
          <dd>
            {courseReviewRequestEntity.rightsConfirmedAt ? (
              <TextFormat value={courseReviewRequestEntity.rightsConfirmedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="lumiApp.courseReviewRequest.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {courseReviewRequestEntity.submittedAt ? (
              <TextFormat value={courseReviewRequestEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="reviewedAt">
              <Translate contentKey="lumiApp.courseReviewRequest.reviewedAt">Reviewed At</Translate>
            </span>
          </dt>
          <dd>
            {courseReviewRequestEntity.reviewedAt ? (
              <TextFormat value={courseReviewRequestEntity.reviewedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="reviewerFeedback">
              <Translate contentKey="lumiApp.courseReviewRequest.reviewerFeedback">Reviewer Feedback</Translate>
            </span>
          </dt>
          <dd>{courseReviewRequestEntity.reviewerFeedback}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseReviewRequest.course">Course</Translate>
          </dt>
          <dd>{courseReviewRequestEntity.course ? courseReviewRequestEntity.course.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseReviewRequest.submittedBy">Submitted By</Translate>
          </dt>
          <dd>{courseReviewRequestEntity.submittedBy ? courseReviewRequestEntity.submittedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseReviewRequest.reviewedBy">Reviewed By</Translate>
          </dt>
          <dd>{courseReviewRequestEntity.reviewedBy ? courseReviewRequestEntity.reviewedBy.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course-review-request" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course-review-request/${courseReviewRequestEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseReviewRequestDetail;
