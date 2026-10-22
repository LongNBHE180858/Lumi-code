import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './refund.reducer';

export const RefundDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const refundEntity = useAppSelector(state => state.refund.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="refundDetailsHeading">
          <Translate contentKey="lumiApp.refund.detail.title">Refund</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{refundEntity.id}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="lumiApp.refund.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{refundEntity.amount}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="lumiApp.refund.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{refundEntity.reason}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.refund.status">Status</Translate>
            </span>
          </dt>
          <dd>{refundEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.refund.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{refundEntity.createdAt ? <TextFormat value={refundEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="lumiApp.refund.order">Order</Translate>
          </dt>
          <dd>{refundEntity.order ? refundEntity.order.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.refund.payment">Payment</Translate>
          </dt>
          <dd>{refundEntity.payment ? refundEntity.payment.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/refund" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/refund/${refundEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RefundDetail;
