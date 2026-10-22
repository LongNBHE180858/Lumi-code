import { AccessDurationUnit } from 'app/shared/model/enumerations/access-duration-unit.model';
import { optionalNumber, objectivesFromText, objectivesToText } from 'app/shared/util/course-builder-fields';
import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { getEntities as getGrades } from 'app/entities/grade/grade.reducer';
import { getEntities as getSubjects } from 'app/entities/subject/subject.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './course.reducer';

export const CourseUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const categories = useAppSelector(state => state.category.entities);
  const subjects = useAppSelector(state => state.subject.entities);
  const grades = useAppSelector(state => state.grade.entities);
  const courseEntity = useAppSelector(state => state.course.entity);
  const loading = useAppSelector(state => state.course.loading);
  const updating = useAppSelector(state => state.course.updating);
  const updateSuccess = useAppSelector(state => state.course.updateSuccess);
  const recordStatusValues = Object.keys(RecordStatus);

  const handleClose = () => {
    navigate(`/course${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getCategories({}));
    dispatch(getSubjects({}));
    dispatch(getGrades({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.learningObjectives = objectivesFromText(values.learningObjectives);
    values.accessDurationUnit = values.accessDurationUnit || null;
    values.accessDurationValue = optionalNumber(values.accessDurationValue);
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.price = optionalNumber(values.price);
    values.validDurationDays = optionalNumber(values.validDurationDays);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...courseEntity,
      ...values,
      version: isNew ? undefined : courseEntity.version,
      author: users.find(it => it.id.toString() === values.author?.toString()),
      category: categories.find(it => it.id.toString() === values.category?.toString()),
      subject: subjects.find(it => it.id.toString() === values.subject?.toString()),
      grade: grades.find(it => it.id.toString() === values.grade?.toString()),
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
          updatedAt: displayDefaultDateTime(),
        }
      : {
          status: 'DRAFT',
          ...courseEntity,
          learningObjectives: objectivesToText(courseEntity.learningObjectives),
          createdAt: convertDateTimeFromServer(courseEntity.createdAt),
          updatedAt: convertDateTimeFromServer(courseEntity.updatedAt),
          author: courseEntity?.author?.id,
          category: courseEntity?.category?.id,
          subject: courseEntity?.subject?.id,
          grade: courseEntity?.grade?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.course.home.createOrEditLabel" data-cy="CourseCreateUpdateHeading">
            <Translate contentKey="lumiApp.course.home.createOrEditLabel">Create or edit a Course</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              <ValidatedField
                label={translate('lumiApp.course.shortDescription')}
                id="course-shortDescription"
                name="shortDescription"
                type="textarea"
                validate={{ maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.course.bookSeries')}
                id="course-bookSeries"
                name="bookSeries"
                type="text"
                validate={{ maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.course.targetAudience')}
                id="course-targetAudience"
                name="targetAudience"
                type="textarea"
                validate={{ maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.course.prerequisites')}
                id="course-prerequisites"
                name="prerequisites"
                type="textarea"
                validate={{ maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.course.learningObjectives')}
                id="course-learningObjectives"
                name="learningObjectives"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.course.introVideoUrl')}
                id="course-introVideoUrl"
                name="introVideoUrl"
                type="text"
                validate={{ maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.course.accessDurationValue')}
                id="course-accessDurationValue"
                name="accessDurationValue"
                type="number"
                validate={{
                  validate: value =>
                    value === '' ||
                    value == null ||
                    (Number.isSafeInteger(Number(value)) && Number(value) >= 1) ||
                    translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.course.accessDurationUnit')}
                id="course-accessDurationUnit"
                name="accessDurationUnit"
                type="select"
              >
                <option value="" />
                {Object.values(AccessDurationUnit).map(unit => (
                  <option key={unit} value={unit}>
                    {translate(`lumiApp.AccessDurationUnit.${unit}`)}
                  </option>
                ))}
              </ValidatedField>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="course-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.course.title')}
                id="course-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.course.description')}
                id="course-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.course.thumbnailUrl')}
                id="course-thumbnailUrl"
                name="thumbnailUrl"
                data-cy="thumbnailUrl"
                type="text"
                validate={{
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.course.price')}
                id="course-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.course.validDurationDays')}
                id="course-validDurationDays"
                name="validDurationDays"
                data-cy="validDurationDays"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => v === '' || v == null || isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('lumiApp.course.status')} id="course-status" name="status" data-cy="status" type="select">
                {recordStatusValues.map(recordStatus => (
                  <option value={recordStatus} key={recordStatus}>
                    {translate(`lumiApp.RecordStatus.${recordStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.course.isDeleted')}
                id="course-isDeleted"
                name="isDeleted"
                data-cy="isDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.course.createdAt')}
                id="course-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.course.updatedAt')}
                id="course-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="course-author"
                name="author"
                data-cy="author"
                label={translate('lumiApp.course.author')}
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
                id="course-category"
                name="category"
                data-cy="category"
                label={translate('lumiApp.course.category')}
                type="select"
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="course-subject"
                name="subject"
                data-cy="subject"
                label={translate('lumiApp.course.subject')}
                type="select"
              >
                <option value="" key="0" />
                {subjects
                  ? subjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="course-grade" name="grade" data-cy="grade" label={translate('lumiApp.course.grade')} type="select">
                <option value="" key="0" />
                {grades
                  ? grades.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/course" replace variant="info">
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

export default CourseUpdate;
