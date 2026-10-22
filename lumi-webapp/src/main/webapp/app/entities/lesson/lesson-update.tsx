import { optionalNumber } from 'app/shared/util/course-builder-fields';
import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getChapters } from 'app/entities/chapter/chapter.reducer';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';

import { createEntity, getEntity, reset, updateEntity } from './lesson.reducer';

export const LessonUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const chapters = useAppSelector(state => state.chapter.entities);
  const lessonEntity = useAppSelector(state => state.lesson.entity);
  const loading = useAppSelector(state => state.lesson.loading);
  const updating = useAppSelector(state => state.lesson.updating);
  const updateSuccess = useAppSelector(state => state.lesson.updateSuccess);
  const recordStatusValues = Object.keys(RecordStatus);

  const handleClose = () => {
    navigate(`/lesson${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getChapters({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.videoSizeBytes = optionalNumber(values.videoSizeBytes);
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.orderIndex !== undefined && typeof values.orderIndex !== 'number') {
      values.orderIndex = Number(values.orderIndex);
    }
    values.durationMinutes = optionalNumber(values.durationMinutes);

    const entity = {
      ...lessonEntity,
      ...values,
      chapter: chapters.find(it => it.id.toString() === values.chapter?.toString()),
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
          status: 'DRAFT',
          ...lessonEntity,
          chapter: lessonEntity?.chapter?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.lesson.home.createOrEditLabel" data-cy="LessonCreateUpdateHeading">
            <Translate contentKey="lumiApp.lesson.home.createOrEditLabel">Create or edit a Lesson</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              <ValidatedField label={translate('lumiApp.lesson.description')} id="lesson-description" name="description" type="textarea" />
              <ValidatedField
                label={translate('lumiApp.lesson.videoUrl')}
                id="lesson-videoUrl"
                name="videoUrl"
                type="text"
                validate={{ maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.lesson.videoFileName')}
                id="lesson-videoFileName"
                name="videoFileName"
                type="text"
                validate={{ maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) } }}
              />
              <ValidatedField
                label={translate('lumiApp.lesson.videoSizeBytes')}
                id="lesson-videoSizeBytes"
                name="videoSizeBytes"
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
                  id="lesson-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.lesson.title')}
                id="lesson-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.lesson.contentType')}
                id="lesson-contentType"
                name="contentType"
                data-cy="contentType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.lesson.orderIndex')}
                id="lesson-orderIndex"
                name="orderIndex"
                data-cy="orderIndex"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.lesson.isPreview')}
                id="lesson-isPreview"
                name="isPreview"
                data-cy="isPreview"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.lesson.durationMinutes')}
                id="lesson-durationMinutes"
                name="durationMinutes"
                data-cy="durationMinutes"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('lumiApp.lesson.status')} id="lesson-status" name="status" data-cy="status" type="select">
                {recordStatusValues.map(recordStatus => (
                  <option value={recordStatus} key={recordStatus}>
                    {translate(`lumiApp.RecordStatus.${recordStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="lesson-chapter"
                name="chapter"
                data-cy="chapter"
                label={translate('lumiApp.lesson.chapter')}
                type="select"
                required
              >
                <option value="" key="0" />
                {chapters
                  ? chapters.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/lesson" replace variant="info">
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

export default LessonUpdate;
