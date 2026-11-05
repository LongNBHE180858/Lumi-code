import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getInstructorWallets } from 'app/entities/instructor-wallet/instructor-wallet.reducer';
import { getEntities as getPayoutRequests } from 'app/entities/payout-request/payout-request.reducer';
import { getEntities as getRefunds } from 'app/entities/refund/refund.reducer';
import { TransactionType } from 'app/shared/model/enumerations/transaction-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, updateEntity } from './wallet-transaction.reducer';

export const WalletTransactionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const instructorWallets = useAppSelector(state => state.instructorWallet.entities);
  const payoutRequests = useAppSelector(state => state.payoutRequest.entities);
  const refunds = useAppSelector(state => state.refund.entities);
  const walletTransactionEntity = useAppSelector(state => state.walletTransaction.entity);
  const loading = useAppSelector(state => state.walletTransaction.loading);
  const updating = useAppSelector(state => state.walletTransaction.updating);
  const updateSuccess = useAppSelector(state => state.walletTransaction.updateSuccess);
  const transactionTypeValues = Object.keys(TransactionType);

  const handleClose = () => {
    navigate('/wallet-transaction');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getInstructorWallets({}));
    dispatch(getPayoutRequests({}));
    dispatch(getRefunds({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.amount !== undefined && typeof values.amount !== 'number') {
      values.amount = Number(values.amount);
    }
    values.occurredAt = convertDateTimeToServer(values.occurredAt);

    const entity = {
      ...walletTransactionEntity,
      ...values,
      wallet: instructorWallets.find(it => it.id.toString() === values.wallet?.toString()),
      order: values.order ? { id: Number(values.order) } : null,
      payoutRequest: payoutRequests.find(it => it.id.toString() === values.payoutRequest?.toString()),
      refund: refunds.find(it => it.id.toString() === values.refund?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          occurredAt: displayDefaultDateTime(),
        }
      : {
          type: 'SALE',
          ...walletTransactionEntity,
          occurredAt: convertDateTimeFromServer(walletTransactionEntity.occurredAt),
          wallet: walletTransactionEntity?.wallet?.id,
          order: walletTransactionEntity?.order?.id,
          payoutRequest: walletTransactionEntity?.payoutRequest?.id,
          refund: walletTransactionEntity?.refund?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.walletTransaction.home.createOrEditLabel" data-cy="WalletTransactionCreateUpdateHeading">
            <Translate contentKey="lumiApp.walletTransaction.home.createOrEditLabel">Create or edit a WalletTransaction</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="wallet-transaction-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.walletTransaction.type')}
                id="wallet-transaction-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {transactionTypeValues.map(transactionType => (
                  <option value={transactionType} key={transactionType}>
                    {translate(`lumiApp.TransactionType.${transactionType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.walletTransaction.amount')}
                id="wallet-transaction-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.walletTransaction.status')}
                id="wallet-transaction-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.walletTransaction.idempotencyKey')}
                id="wallet-transaction-idempotencyKey"
                name="idempotencyKey"
                data-cy="idempotencyKey"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.walletTransaction.occurredAt')}
                id="wallet-transaction-occurredAt"
                name="occurredAt"
                data-cy="occurredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.walletTransaction.balanceBucket')}
                id="wallet-transaction-balanceBucket"
                name="balanceBucket"
                data-cy="balanceBucket"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                id="wallet-transaction-wallet"
                name="wallet"
                data-cy="wallet"
                label={translate('lumiApp.walletTransaction.wallet')}
                type="select"
                required
              >
                <option value="" key="0" />
                {instructorWallets
                  ? instructorWallets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="wallet-transaction-order"
                name="order"
                data-cy="order"
                label={translate('lumiApp.walletTransaction.order')}
                type="number"
                min="1"
                step="1"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => !v || Number.isSafeInteger(Number(v)) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="wallet-transaction-payoutRequest"
                name="payoutRequest"
                data-cy="payoutRequest"
                label={translate('lumiApp.walletTransaction.payoutRequest')}
                type="select"
              >
                <option value="" key="0" />
                {payoutRequests
                  ? payoutRequests.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="wallet-transaction-refund"
                name="refund"
                data-cy="refund"
                label={translate('lumiApp.walletTransaction.refund')}
                type="select"
              >
                <option value="" key="0" />
                {refunds
                  ? refunds.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/wallet-transaction" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default WalletTransactionUpdate;
