import React, { useEffect, useState } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourseEnrollments } from 'app/entities/course-enrollment/course-enrollment.reducer';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import type { ISystemPlan } from 'app/shared/model/system-plan.model';

import { createEntity, getEntity, reset, updateEntity } from './lesson-progress.reducer';

export const LessonProgressUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courseEnrollments = useAppSelector(state => state.courseEnrollment.entities);
  const lessons = useAppSelector(state => state.lesson.entities);
  const lessonProgressEntity = useAppSelector(state => state.lessonProgress.entity);
  const loading = useAppSelector(state => state.lessonProgress.loading);
  const updating = useAppSelector(state => state.lessonProgress.updating);
  const updateSuccess = useAppSelector(state => state.lessonProgress.updateSuccess);

  const handleClose = () => {
    navigate(`/lesson-progress${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourseEnrollments({}));
    dispatch(getLessons({}));
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
    if (values.lastPositionSeconds !== undefined && typeof values.lastPositionSeconds !== 'number') {
      values.lastPositionSeconds = Number(values.lastPositionSeconds);
    }
    values.lastAccessedAt = convertDateTimeToServer(values.lastAccessedAt);
    values.completedAt = convertDateTimeToServer(values.completedAt);
    if (values.rowVersion !== undefined && typeof values.rowVersion !== 'number') {
      values.rowVersion = Number(values.rowVersion);
    }

    const entity = {
      ...lessonProgressEntity,
      ...values,
      enrollment: courseEnrollments.find(it => it.id.toString() === values.enrollment?.toString()),
      lesson: lessons.find(it => it.id.toString() === values.lesson?.toString()),
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
          lastAccessedAt: displayDefaultDateTime(),
          completedAt: displayDefaultDateTime(),
        }
      : {
          ...lessonProgressEntity,
          lastAccessedAt: convertDateTimeFromServer(lessonProgressEntity.lastAccessedAt),
          completedAt: convertDateTimeFromServer(lessonProgressEntity.completedAt),
          enrollment: lessonProgressEntity?.enrollment?.id,
          lesson: lessonProgressEntity?.lesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.lessonProgress.home.createOrEditLabel" data-cy="LessonProgressCreateUpdateHeading">
            <Translate contentKey="lumiApp.lessonProgress.home.createOrEditLabel">Create or edit a LessonProgress</Translate>
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
                  id="lesson-progress-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.lessonProgress.status')}
                id="lesson-progress-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: {
                    value: true,
                    message: translate('entity.validation.required'),
                  },
                  maxLength: {
                    value: 20,
                    message: translate('entity.validation.maxlength', {
                      max: 20,
                    }),
                  },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.lessonProgress.lastPositionSeconds')}
                id="lesson-progress-lastPositionSeconds"
                name="lastPositionSeconds"
                data-cy="lastPositionSeconds"
                type="text"
                validate={{
                  required: {
                    value: true,
                    message: translate('entity.validation.required'),
                  },
                  min: {
                    value: 0,
                    message: translate('entity.validation.min', { min: 0 }),
                  },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.lessonProgress.lastAccessedAt')}
                id="lesson-progress-lastAccessedAt"
                name="lastAccessedAt"
                data-cy="lastAccessedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.lessonProgress.completedAt')}
                id="lesson-progress-completedAt"
                name="completedAt"
                data-cy="completedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.lessonProgress.rowVersion')}
                id="lesson-progress-rowVersion"
                name="rowVersion"
                data-cy="rowVersion"
                type="text"
                validate={{
                  required: {
                    value: true,
                    message: translate('entity.validation.required'),
                  },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="lesson-progress-enrollment"
                name="enrollment"
                data-cy="enrollment"
                label={translate('lumiApp.lessonProgress.enrollment')}
                type="select"
                required
              >
                <option value="" key="0" />
                {courseEnrollments
                  ? courseEnrollments.map(otherEntity => (
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
                id="lesson-progress-lesson"
                name="lesson"
                data-cy="lesson"
                label={translate('lumiApp.lessonProgress.lesson')}
                type="select"
                required
              >
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/lesson-progress" replace variant="info">
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

export default LessonProgressUpdate;
