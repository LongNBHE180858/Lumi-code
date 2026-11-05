import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './refund.reducer';

export const RefundUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const refundEntity = useAppSelector(state => state.refund.entity);
  const loading = useAppSelector(state => state.refund.loading);
  const updating = useAppSelector(state => state.refund.updating);
  const updateSuccess = useAppSelector(state => state.refund.updateSuccess);

  const handleClose = () => {
    navigate(`/refund${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    values.accessRevokedAt = convertDateTimeToServer(values.accessRevokedAt);

    const entity = {
      ...refundEntity,
      ...values,
      order: values.order ? { id: Number(values.order) } : null,
      payment: values.payment ? { id: Number(values.payment) } : null,
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
          accessRevokedAt: displayDefaultDateTime(),
        }
      : {
          ...refundEntity,
          createdAt: convertDateTimeFromServer(refundEntity.createdAt),
          processedAt: convertDateTimeFromServer(refundEntity.processedAt),
          accessRevokedAt: convertDateTimeFromServer(refundEntity.accessRevokedAt),
          order: refundEntity?.order?.id,
          payment: refundEntity?.payment?.id,
          processedBy: refundEntity?.processedBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.refund.home.createOrEditLabel" data-cy="RefundCreateUpdateHeading">
            <Translate contentKey="lumiApp.refund.home.createOrEditLabel">Create or edit a Refund</Translate>
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
                  id="refund-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.refund.amount')}
                id="refund-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.refund.reason')}
                id="refund-reason"
                name="reason"
                data-cy="reason"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.refund.status')}
                id="refund-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.refund.createdAt')}
                id="refund-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.refund.processedAt')}
                id="refund-processedAt"
                name="processedAt"
                data-cy="processedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.refund.reviewNote')}
                id="refund-reviewNote"
                name="reviewNote"
                data-cy="reviewNote"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.refund.refundReference')}
                id="refund-refundReference"
                name="refundReference"
                data-cy="refundReference"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.refund.accessRevokedAt')}
                id="refund-accessRevokedAt"
                name="accessRevokedAt"
                data-cy="accessRevokedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="refund-order"
                name="order"
                data-cy="order"
                label={translate('lumiApp.refund.order')}
                type="number"
                min="1"
                step="1"
                required
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => !v || Number.isSafeInteger(Number(v)) || translate('entity.validation.number'),
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="refund-payment"
                name="payment"
                data-cy="payment"
                label={translate('lumiApp.refund.payment')}
                type="number"
                min="1"
                step="1"
                required
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => !v || Number.isSafeInteger(Number(v)) || translate('entity.validation.number'),
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="refund-processedBy"
                name="processedBy"
                data-cy="processedBy"
                label={translate('lumiApp.refund.processedBy')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/refund" replace variant="info">
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

export default RefundUpdate;
