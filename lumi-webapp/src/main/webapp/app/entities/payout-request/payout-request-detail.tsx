import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './payout-request.reducer';

export const PayoutRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const payoutRequestEntity = useAppSelector(state => state.payoutRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="payoutRequestDetailsHeading">
          <Translate contentKey="lumiApp.payoutRequest.detail.title">PayoutRequest</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.id}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="lumiApp.payoutRequest.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.amount}</dd>
          <dt>
            <span id="bankCode">
              <Translate contentKey="lumiApp.payoutRequest.bankCode">Bank Code</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.bankCode}</dd>
          <dt>
            <span id="bankAccount">
              <Translate contentKey="lumiApp.payoutRequest.bankAccount">Bank Account</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.bankAccount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.payoutRequest.status">Status</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.payoutRequest.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {payoutRequestEntity.createdAt ? (
              <TextFormat value={payoutRequestEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="accountHolderName">
              <Translate contentKey="lumiApp.payoutRequest.accountHolderName">Account Holder Name</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.accountHolderName}</dd>
          <dt>
            <span id="processedAt">
              <Translate contentKey="lumiApp.payoutRequest.processedAt">Processed At</Translate>
            </span>
          </dt>
          <dd>
            {payoutRequestEntity.processedAt ? (
              <TextFormat value={payoutRequestEntity.processedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="rejectionReason">
              <Translate contentKey="lumiApp.payoutRequest.rejectionReason">Rejection Reason</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.rejectionReason}</dd>
          <dt>
            <span id="transferReference">
              <Translate contentKey="lumiApp.payoutRequest.transferReference">Transfer Reference</Translate>
            </span>
          </dt>
          <dd>{payoutRequestEntity.transferReference}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.payoutRequest.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {payoutRequestEntity.updatedAt ? (
              <TextFormat value={payoutRequestEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.payoutRequest.wallet">Wallet</Translate>
          </dt>
          <dd>{payoutRequestEntity.wallet ? payoutRequestEntity.wallet.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.payoutRequest.processedBy">Processed By</Translate>
          </dt>
          <dd>{payoutRequestEntity.processedBy ? payoutRequestEntity.processedBy.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/payout-request" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/payout-request/${payoutRequestEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PayoutRequestDetail;
