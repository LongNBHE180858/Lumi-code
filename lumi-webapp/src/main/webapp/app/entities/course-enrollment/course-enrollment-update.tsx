import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './course-enrollment.reducer';

export const CourseEnrollmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const lessons = useAppSelector(state => state.lesson.entities);
  const courseEnrollmentEntity = useAppSelector(state => state.courseEnrollment.entity);
  const loading = useAppSelector(state => state.courseEnrollment.loading);
  const updating = useAppSelector(state => state.courseEnrollment.updating);
  const updateSuccess = useAppSelector(state => state.courseEnrollment.updateSuccess);

  const handleClose = () => {
    navigate(`/course-enrollment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
    dispatch(getUsers({}));
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
    if (values.progressPercent !== undefined && typeof values.progressPercent !== 'number') {
      values.progressPercent = Number(values.progressPercent);
    }
    values.enrolledAt = convertDateTimeToServer(values.enrolledAt);
    values.expiresAt = convertDateTimeToServer(values.expiresAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...courseEnrollmentEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      student: users.find(it => it.id.toString() === values.student?.toString()),
      lastAccessedLesson: lessons.find(it => it.id.toString() === values.lastAccessedLesson?.toString()),
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
          enrolledAt: displayDefaultDateTime(),
          expiresAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...courseEnrollmentEntity,
          enrolledAt: convertDateTimeFromServer(courseEnrollmentEntity.enrolledAt),
          expiresAt: convertDateTimeFromServer(courseEnrollmentEntity.expiresAt),
          updatedAt: convertDateTimeFromServer(courseEnrollmentEntity.updatedAt),
          course: courseEnrollmentEntity?.course?.id,
          student: courseEnrollmentEntity?.student?.id,
          lastAccessedLesson: courseEnrollmentEntity?.lastAccessedLesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.courseEnrollment.home.createOrEditLabel" data-cy="CourseEnrollmentCreateUpdateHeading">
            <Translate contentKey="lumiApp.courseEnrollment.home.createOrEditLabel">Create or edit a CourseEnrollment</Translate>
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
                  id="course-enrollment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.courseEnrollment.progressPercent')}
                id="course-enrollment-progressPercent"
                name="progressPercent"
                data-cy="progressPercent"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.courseEnrollment.status')}
                id="course-enrollment-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseEnrollment.enrolledAt')}
                id="course-enrollment-enrolledAt"
                name="enrolledAt"
                data-cy="enrolledAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.courseEnrollment.expiresAt')}
                id="course-enrollment-expiresAt"
                name="expiresAt"
                data-cy="expiresAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseEnrollment.updatedAt')}
                id="course-enrollment-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="course-enrollment-course"
                name="course"
                data-cy="course"
                label={translate('lumiApp.courseEnrollment.course')}
                type="select"
                required
              >
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
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
                id="course-enrollment-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.courseEnrollment.student')}
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
                id="course-enrollment-lastAccessedLesson"
                name="lastAccessedLesson"
                data-cy="lastAccessedLesson"
                label={translate('lumiApp.courseEnrollment.lastAccessedLesson')}
                type="select"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/course-enrollment" replace variant="info">
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

export default CourseEnrollmentUpdate;
