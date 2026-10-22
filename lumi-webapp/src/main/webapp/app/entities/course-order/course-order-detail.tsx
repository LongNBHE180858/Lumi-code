import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course-order.reducer';

export const CourseOrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseOrderEntity = useAppSelector(state => state.courseOrder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseOrderDetailsHeading">
          <Translate contentKey="lumiApp.courseOrder.detail.title">CourseOrder</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.id}</dd>
          <dt>
            <span id="originalPrice">
              <Translate contentKey="lumiApp.courseOrder.originalPrice">Original Price</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.originalPrice}</dd>
          <dt>
            <span id="discountAmount">
              <Translate contentKey="lumiApp.courseOrder.discountAmount">Discount Amount</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.discountAmount}</dd>
          <dt>
            <span id="finalPrice">
              <Translate contentKey="lumiApp.courseOrder.finalPrice">Final Price</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.finalPrice}</dd>
          <dt>
            <span id="revenueSplitRate">
              <Translate contentKey="lumiApp.courseOrder.revenueSplitRate">Revenue Split Rate</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.revenueSplitRate}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.courseOrder.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.status}</dd>
          <dt>
            <span id="idempotencyKey">
              <Translate contentKey="lumiApp.courseOrder.idempotencyKey">Idempotency Key</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.idempotencyKey}</dd>
          <dt>
            <span id="rowVersion">
              <Translate contentKey="lumiApp.courseOrder.rowVersion">Row Version</Translate>
            </span>
          </dt>
          <dd>{courseOrderEntity.rowVersion}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.courseOrder.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {courseOrderEntity.createdAt ? <TextFormat value={courseOrderEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.courseOrder.student">Student</Translate>
          </dt>
          <dd>{courseOrderEntity.student ? courseOrderEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseOrder.course">Course</Translate>
          </dt>
          <dd>{courseOrderEntity.course ? courseOrderEntity.course.title : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseOrder.plan">Plan</Translate>
          </dt>
          <dd>{courseOrderEntity.plan ? courseOrderEntity.plan.name : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseOrder.coupon">Coupon</Translate>
          </dt>
          <dd>{courseOrderEntity.coupon ? courseOrderEntity.coupon.code : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course-order" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course-order/${courseOrderEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseOrderDetail;
