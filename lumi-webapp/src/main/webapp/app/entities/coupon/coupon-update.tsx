import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './coupon.reducer';

export const CouponUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const couponEntity = useAppSelector(state => state.coupon.entity);
  const loading = useAppSelector(state => state.coupon.loading);
  const updating = useAppSelector(state => state.coupon.updating);
  const updateSuccess = useAppSelector(state => state.coupon.updateSuccess);

  const handleClose = () => {
    navigate(`/coupon${location.search}`);
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
    if (values.discountPercent !== undefined && typeof values.discountPercent !== 'number') {
      values.discountPercent = Number(values.discountPercent);
    }
    if (values.maxUses !== undefined && typeof values.maxUses !== 'number') {
      values.maxUses = Number(values.maxUses);
    }
    if (values.usedCount !== undefined && typeof values.usedCount !== 'number') {
      values.usedCount = Number(values.usedCount);
    }
    values.validTo = convertDateTimeToServer(values.validTo);

    const entity = {
      ...couponEntity,
      ...values,
      instructor: users.find(it => it.id.toString() === values.instructor?.toString()),
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
          validTo: displayDefaultDateTime(),
        }
      : {
          ...couponEntity,
          validTo: convertDateTimeFromServer(couponEntity.validTo),
          instructor: couponEntity?.instructor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.coupon.home.createOrEditLabel" data-cy="CouponCreateUpdateHeading">
            <Translate contentKey="lumiApp.coupon.home.createOrEditLabel">Create or edit a Coupon</Translate>
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
                  id="coupon-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.coupon.code')}
                id="coupon-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.coupon.discountPercent')}
                id="coupon-discountPercent"
                name="discountPercent"
                data-cy="discountPercent"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  max: { value: 100, message: translate('entity.validation.max', { max: 100 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.coupon.maxUses')}
                id="coupon-maxUses"
                name="maxUses"
                data-cy="maxUses"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.coupon.usedCount')}
                id="coupon-usedCount"
                name="usedCount"
                data-cy="usedCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.coupon.validTo')}
                id="coupon-validTo"
                name="validTo"
                data-cy="validTo"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="coupon-instructor"
                name="instructor"
                data-cy="instructor"
                label={translate('lumiApp.coupon.instructor')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/coupon" replace variant="info">
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

export default CouponUpdate;
