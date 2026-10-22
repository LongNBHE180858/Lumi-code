import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './payment.reducer';

export const PaymentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const paymentEntity = useAppSelector(state => state.payment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paymentDetailsHeading">
          <Translate contentKey="lumiApp.payment.detail.title">Payment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.id}</dd>
          <dt>
            <span id="gateway">
              <Translate contentKey="lumiApp.payment.gateway">Gateway</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.gateway}</dd>
          <dt>
            <span id="transactionNo">
              <Translate contentKey="lumiApp.payment.transactionNo">Transaction No</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.transactionNo}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="lumiApp.payment.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.amount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.payment.status">Status</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.status}</dd>
          <dt>
            <span id="paidAt">
              <Translate contentKey="lumiApp.payment.paidAt">Paid At</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.paidAt ? <TextFormat value={paymentEntity.paidAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="lumiApp.payment.order">Order</Translate>
          </dt>
          <dd>{paymentEntity.order ? paymentEntity.order.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/payment" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/payment/${paymentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PaymentDetail;
