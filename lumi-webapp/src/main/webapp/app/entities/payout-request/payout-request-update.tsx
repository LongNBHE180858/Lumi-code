import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getInstructorWallets } from 'app/entities/instructor-wallet/instructor-wallet.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './payout-request.reducer';

export const PayoutRequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const instructorWallets = useAppSelector(state => state.instructorWallet.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const payoutRequestEntity = useAppSelector(state => state.payoutRequest.entity);
  const loading = useAppSelector(state => state.payoutRequest.loading);
  const updating = useAppSelector(state => state.payoutRequest.updating);
  const updateSuccess = useAppSelector(state => state.payoutRequest.updateSuccess);

  const handleClose = () => {
    navigate(`/payout-request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInstructorWallets({}));
    dispatch(getUsers({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.processedAt = convertDateTimeToServer(values.processedAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...payoutRequestEntity,
      ...values,
      wallet: instructorWallets.find(it => it.id.toString() === values.wallet?.toString()),
      processedBy: users.find(it => it.id.toString() === values.processedBy?.toString()),
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
          createdAt: displayDefaultDateTime(),
          processedAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...payoutRequestEntity,
          createdAt: convertDateTimeFromServer(payoutRequestEntity.createdAt),
          processedAt: convertDateTimeFromServer(payoutRequestEntity.processedAt),
          updatedAt: convertDateTimeFromServer(payoutRequestEntity.updatedAt),
          wallet: payoutRequestEntity?.wallet?.id,
          processedBy: payoutRequestEntity?.processedBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.payoutRequest.home.createOrEditLabel" data-cy="PayoutRequestCreateUpdateHeading">
            <Translate contentKey="lumiApp.payoutRequest.home.createOrEditLabel">Create or edit a PayoutRequest</Translate>
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
                  id="payout-request-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.payoutRequest.amount')}
                id="payout-request-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.bankCode')}
                id="payout-request-bankCode"
                name="bankCode"
                data-cy="bankCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.bankAccount')}
                id="payout-request-bankAccount"
                name="bankAccount"
                data-cy="bankAccount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.status')}
                id="payout-request-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.createdAt')}
                id="payout-request-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.accountHolderName')}
                id="payout-request-accountHolderName"
                name="accountHolderName"
                data-cy="accountHolderName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.processedAt')}
                id="payout-request-processedAt"
                name="processedAt"
                data-cy="processedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.rejectionReason')}
                id="payout-request-rejectionReason"
                name="rejectionReason"
                data-cy="rejectionReason"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.transferReference')}
                id="payout-request-transferReference"
                name="transferReference"
                data-cy="transferReference"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.payoutRequest.updatedAt')}
                id="payout-request-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="payout-request-wallet"
                name="wallet"
                data-cy="wallet"
                label={translate('lumiApp.payoutRequest.wallet')}
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
                id="payout-request-processedBy"
                name="processedBy"
                data-cy="processedBy"
                label={translate('lumiApp.payoutRequest.processedBy')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/payout-request" replace variant="info">
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

export default PayoutRequestUpdate;
