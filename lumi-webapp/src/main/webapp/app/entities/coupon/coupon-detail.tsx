import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './coupon.reducer';

export const CouponDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const couponEntity = useAppSelector(state => state.coupon.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="couponDetailsHeading">
          <Translate contentKey="lumiApp.coupon.detail.title">Coupon</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{couponEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.coupon.code">Code</Translate>
            </span>
          </dt>
          <dd>{couponEntity.code}</dd>
          <dt>
            <span id="discountPercent">
              <Translate contentKey="lumiApp.coupon.discountPercent">Discount Percent</Translate>
            </span>
          </dt>
          <dd>{couponEntity.discountPercent}</dd>
          <dt>
            <span id="maxUses">
              <Translate contentKey="lumiApp.coupon.maxUses">Max Uses</Translate>
            </span>
          </dt>
          <dd>{couponEntity.maxUses}</dd>
          <dt>
            <span id="usedCount">
              <Translate contentKey="lumiApp.coupon.usedCount">Used Count</Translate>
            </span>
          </dt>
          <dd>{couponEntity.usedCount}</dd>
          <dt>
            <span id="validTo">
              <Translate contentKey="lumiApp.coupon.validTo">Valid To</Translate>
            </span>
          </dt>
          <dd>{couponEntity.validTo ? <TextFormat value={couponEntity.validTo} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="lumiApp.coupon.instructor">Instructor</Translate>
          </dt>
          <dd>{couponEntity.instructor ? couponEntity.instructor.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/coupon" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/coupon/${couponEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CouponDetail;
