import { optionalNumber } from 'app/shared/util/course-builder-fields';
import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';

import { createEntity, getEntity, reset, updateEntity } from './course-material.reducer';

export const CourseMaterialUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const lessons = useAppSelector(state => state.lesson.entities);
  const courseMaterialEntity = useAppSelector(state => state.courseMaterial.entity);
  const loading = useAppSelector(state => state.courseMaterial.loading);
  const updating = useAppSelector(state => state.courseMaterial.updating);
  const updateSuccess = useAppSelector(state => state.courseMaterial.updateSuccess);

  const handleClose = () => {
    navigate(`/course-material${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getLessons({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.orderIndex = optionalNumber(values.orderIndex);
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.fileSizeBytes = optionalNumber(values.fileSizeBytes);

    const entity = {
      ...courseMaterialEntity,
      ...values,
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
      ? {}
      : {
          ...courseMaterialEntity,
          lesson: courseMaterialEntity?.lesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.courseMaterial.home.createOrEditLabel" data-cy="CourseMaterialCreateUpdateHeading">
            <Translate contentKey="lumiApp.courseMaterial.home.createOrEditLabel">Create or edit a CourseMaterial</Translate>
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
                label={translate('lumiApp.courseMaterial.fileName')}
                id="course-material-fileName"
                name="fileName"
                type="text"
                validate={{ maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.courseMaterial.displayName')}
                id="course-material-displayName"
                name="displayName"
                type="text"
                validate={{ maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.courseMaterial.mimeType')}
                id="course-material-mimeType"
                name="mimeType"
                type="text"
                validate={{ maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.courseMaterial.orderIndex')}
                id="course-material-orderIndex"
                name="orderIndex"
                type="number"
                validate={{
                  validate: value =>
                    value === '' ||
                    value == null ||
                    (Number.isSafeInteger(Number(value)) && Number(value) >= 0) ||
                    translate('entity.validation.number'),
                }}
              />
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="course-material-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.courseMaterial.fileType')}
                id="course-material-fileType"
                name="fileType"
                data-cy="fileType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseMaterial.fileUrl')}
                id="course-material-fileUrl"
                name="fileUrl"
                data-cy="fileUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.courseMaterial.fileSizeBytes')}
                id="course-material-fileSizeBytes"
                name="fileSizeBytes"
                data-cy="fileSizeBytes"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="course-material-lesson"
                name="lesson"
                data-cy="lesson"
                label={translate('lumiApp.courseMaterial.lesson')}
                type="select"
                required
              >
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/course-material" replace variant="info">
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

export default CourseMaterialUpdate;
