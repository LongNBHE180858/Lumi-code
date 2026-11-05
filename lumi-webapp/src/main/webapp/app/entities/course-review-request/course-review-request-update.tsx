import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './course-review-request.reducer';

export const CourseReviewRequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const courseReviewRequestEntity = useAppSelector(state => state.courseReviewRequest.entity);
  const loading = useAppSelector(state => state.courseReviewRequest.loading);
  const updating = useAppSelector(state => state.courseReviewRequest.updating);
  const updateSuccess = useAppSelector(state => state.courseReviewRequest.updateSuccess);

  const handleClose = () => {
    navigate(`/course-review-request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
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
    if (values.courseVersion !== undefined && typeof values.courseVersion !== 'number') {
      values.courseVersion = Number(values.courseVersion);
    }
    values.rightsConfirmedAt = convertDateTimeToServer(values.rightsConfirmedAt);
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    values.reviewedAt = convertDateTimeToServer(values.reviewedAt);

    const entity = {
      ...courseReviewRequestEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      submittedBy: users.find(it => it.id.toString() === values.submittedBy?.toString()),
      reviewedBy: users.find(it => it.id.toString() === values.reviewedBy?.toString()),
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
          rightsConfirmedAt: displayDefaultDateTime(),
          submittedAt: displayDefaultDateTime(),
          reviewedAt: displayDefaultDateTime(),
        }
      : {
          ...courseReviewRequestEntity,
          rightsConfirmedAt: convertDateTimeFromServer(courseReviewRequestEntity.rightsConfirmedAt),
          submittedAt: convertDateTimeFromServer(courseReviewRequestEntity.submittedAt),
          reviewedAt: convertDateTimeFromServer(courseReviewRequestEntity.reviewedAt),
          course: courseReviewRequestEntity?.course?.id,
          submittedBy: courseReviewRequestEntity?.submittedBy?.id,
          reviewedBy: courseReviewRequestEntity?.reviewedBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.courseReviewRequest.home.createOrEditLabel" data-cy="CourseReviewRequestCreateUpdateHeading">
            <Translate contentKey="lumiApp.courseReviewRequest.home.createOrEditLabel">Create or edit a CourseReviewRequest</Translate>
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
                  id="course-review-request-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.courseVersion')}
                id="course-review-request-courseVersion"
                name="courseVersion"
                data-cy="courseVersion"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.status')}
                id="course-review-request-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.submissionNote')}
                id="course-review-request-submissionNote"
                name="submissionNote"
                data-cy="submissionNote"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.rightsConfirmedAt')}
                id="course-review-request-rightsConfirmedAt"
                name="rightsConfirmedAt"
                data-cy="rightsConfirmedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.submittedAt')}
                id="course-review-request-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.reviewedAt')}
                id="course-review-request-reviewedAt"
                name="reviewedAt"
                data-cy="reviewedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.courseReviewRequest.reviewerFeedback')}
                id="course-review-request-reviewerFeedback"
                name="reviewerFeedback"
                data-cy="reviewerFeedback"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                id="course-review-request-course"
                name="course"
                data-cy="course"
                label={translate('lumiApp.courseReviewRequest.course')}
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
                id="course-review-request-submittedBy"
                name="submittedBy"
                data-cy="submittedBy"
                label={translate('lumiApp.courseReviewRequest.submittedBy')}
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
                id="course-review-request-reviewedBy"
                name="reviewedBy"
                data-cy="reviewedBy"
                label={translate('lumiApp.courseReviewRequest.reviewedBy')}
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
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/course-review-request"
                replace
                variant="info"
              >
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

export default CourseReviewRequestUpdate;
