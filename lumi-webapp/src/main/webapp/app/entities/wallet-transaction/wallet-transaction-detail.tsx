import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './wallet-transaction.reducer';

export const WalletTransactionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const walletTransactionEntity = useAppSelector(state => state.walletTransaction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="walletTransactionDetailsHeading">
          <Translate contentKey="lumiApp.walletTransaction.detail.title">WalletTransaction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{walletTransactionEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.walletTransaction.type">Type</Translate>
            </span>
          </dt>
          <dd>{walletTransactionEntity.type}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="lumiApp.walletTransaction.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{walletTransactionEntity.amount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.walletTransaction.status">Status</Translate>
            </span>
          </dt>
          <dd>{walletTransactionEntity.status}</dd>
          <dt>
            <span id="idempotencyKey">
              <Translate contentKey="lumiApp.walletTransaction.idempotencyKey">Idempotency Key</Translate>
            </span>
          </dt>
          <dd>{walletTransactionEntity.idempotencyKey}</dd>
          <dt>
            <span id="occurredAt">
              <Translate contentKey="lumiApp.walletTransaction.occurredAt">Occurred At</Translate>
            </span>
          </dt>
          <dd>
            {walletTransactionEntity.occurredAt ? (
              <TextFormat value={walletTransactionEntity.occurredAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.walletTransaction.wallet">Wallet</Translate>
          </dt>
          <dd>{walletTransactionEntity.wallet ? walletTransactionEntity.wallet.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.walletTransaction.order">Order</Translate>
          </dt>
          <dd>{walletTransactionEntity.order ? walletTransactionEntity.order.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/wallet-transaction" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/wallet-transaction/${walletTransactionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WalletTransactionDetail;
