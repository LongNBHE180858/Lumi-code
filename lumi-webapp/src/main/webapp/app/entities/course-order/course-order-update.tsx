import React, { useEffect, useState } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCoupons } from 'app/entities/coupon/coupon.reducer';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { premiumPackagesApi } from 'app/modules/administration/premium-packages/premium-packages-api';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import type { ISystemPlan } from 'app/shared/model/system-plan.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './course-order.reducer';

export const CourseOrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const courses = useAppSelector(state => state.course.entities);
  const [systemPlans, setSystemPlans] = useState<ISystemPlan[]>([]);
  const coupons = useAppSelector(state => state.coupon.entities);
  const courseOrderEntity = useAppSelector(state => state.courseOrder.entity);
  const loading = useAppSelector(state => state.courseOrder.loading);
  const updating = useAppSelector(state => state.courseOrder.updating);
  const updateSuccess = useAppSelector(state => state.courseOrder.updateSuccess);
  const orderStatusValues = Object.keys(OrderStatus);

  const handleClose = () => {
    navigate(`/course-order${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getCourses({}));
    premiumPackagesApi
      .list(0, 100)
      .then(response => setSystemPlans(response.data.map(plan => ({ id: plan.id, name: plan.name }))))
      .catch(() => setSystemPlans([]));
    dispatch(getCoupons({}));
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
    if (values.originalPrice !== undefined && typeof values.originalPrice !== 'number') {
      values.originalPrice = Number(values.originalPrice);
    }
    if (values.discountAmount !== undefined && typeof values.discountAmount !== 'number') {
      values.discountAmount = Number(values.discountAmount);
    }
    if (values.finalPrice !== undefined && typeof values.finalPrice !== 'number') {
      values.finalPrice = Number(values.finalPrice);
    }
    if (values.rowVersion !== undefined && typeof values.rowVersion !== 'number') {
      values.rowVersion = Number(values.rowVersion);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...courseOrderEntity,
      ...values,
      student: users.find(it => it.id.toString() === values.student?.toString()),
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      plan: systemPlans.find(it => it.id?.toString() === values.plan?.toString()),
      coupon: coupons.find(it => it.id.toString() === values.coupon?.toString()),
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
        }
      : {
          status: 'PENDING',
          ...courseOrderEntity,
          createdAt: convertDateTimeFromServer(courseOrderEntity.createdAt),
          student: courseOrderEntity?.student?.id,
          course: courseOrderEntity?.course?.id,
          plan: courseOrderEntity?.plan?.id,
          coupon: courseOrderEntity?.coupon?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.courseOrder.home.createOrEditLabel" data-cy="CourseOrderCreateUpdateHeading">
            <Translate contentKey="lumiApp.courseOrder.home.createOrEditLabel">Create or edit a CourseOrder</Translate>
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
                  id="course-order-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.courseOrder.originalPrice')}
                id="course-order-originalPrice"
                name="originalPrice"
                data-cy="originalPrice"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.discountAmount')}
                id="course-order-discountAmount"
                name="discountAmount"
                data-cy="discountAmount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.finalPrice')}
                id="course-order-finalPrice"
                name="finalPrice"
                data-cy="finalPrice"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.revenueSplitRate')}
                id="course-order-revenueSplitRate"
                name="revenueSplitRate"
                data-cy="revenueSplitRate"
                type="text"
                validate={{
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.status')}
                id="course-order-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {translate(`lumiApp.OrderStatus.${orderStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.courseOrder.idempotencyKey')}
                id="course-order-idempotencyKey"
                name="idempotencyKey"
                data-cy="idempotencyKey"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.rowVersion')}
                id="course-order-rowVersion"
                name="rowVersion"
                data-cy="rowVersion"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseOrder.createdAt')}
                id="course-order-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="course-order-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.courseOrder.student')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="course-order-course"
                name="course"
                data-cy="course"
                label={translate('lumiApp.courseOrder.course')}
                type="select"
              >
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="course-order-plan" name="plan" data-cy="plan" label={translate('lumiApp.courseOrder.plan')} type="select">
                <option value="" key="0" />
                {systemPlans
                  ? systemPlans.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="course-order-coupon"
                name="coupon"
                data-cy="coupon"
                label={translate('lumiApp.courseOrder.coupon')}
                type="select"
              >
                <option value="" key="0" />
                {coupons
                  ? coupons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/course-order" replace variant="info">
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

export default CourseOrderUpdate;
