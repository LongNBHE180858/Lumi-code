import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getStudyClasses } from 'app/entities/study-class/study-class.reducer';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';

import { createEntity, getEntity, reset, updateEntity } from './exam.reducer';

export const ExamUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const studyClasses = useAppSelector(state => state.studyClass.entities);
  const examEntity = useAppSelector(state => state.exam.entity);
  const loading = useAppSelector(state => state.exam.loading);
  const updating = useAppSelector(state => state.exam.updating);
  const updateSuccess = useAppSelector(state => state.exam.updateSuccess);
  const recordStatusValues = Object.keys(RecordStatus);

  const handleClose = () => {
    navigate(`/exam${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStudyClasses({}));
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
    if (values.durationMinutes !== undefined && typeof values.durationMinutes !== 'number') {
      values.durationMinutes = Number(values.durationMinutes);
    }
    if (values.passScore !== undefined && typeof values.passScore !== 'number') {
      values.passScore = Number(values.passScore);
    }
    if (values.maxAttempts !== undefined && typeof values.maxAttempts !== 'number') {
      values.maxAttempts = Number(values.maxAttempts);
    }

    const entity = {
      ...examEntity,
      ...values,
      studyClass: studyClasses.find(it => it.id.toString() === values.studyClass?.toString()),
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
          ...examEntity,
          studyClass: examEntity?.studyClass?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.exam.home.createOrEditLabel" data-cy="ExamCreateUpdateHeading">
            <Translate contentKey="lumiApp.exam.home.createOrEditLabel">Create or edit a Exam</Translate>
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
                  id="exam-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.exam.title')}
                id="exam-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.exam.durationMinutes')}
                id="exam-durationMinutes"
                name="durationMinutes"
                data-cy="durationMinutes"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.exam.passScore')}
                id="exam-passScore"
                name="passScore"
                data-cy="passScore"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.exam.maxAttempts')}
                id="exam-maxAttempts"
                name="maxAttempts"
                data-cy="maxAttempts"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.exam.shuffleQuestions')}
                id="exam-shuffleQuestions"
                name="shuffleQuestions"
                data-cy="shuffleQuestions"
                check
                type="checkbox"
              />
              <ValidatedField label={translate('lumiApp.exam.status')} id="exam-status" name="status" data-cy="status" type="select">
                {recordStatusValues.map(recordStatus => (
                  <option value={recordStatus} key={recordStatus}>
                    {translate(`lumiApp.RecordStatus.${recordStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.exam.isDeleted')}
                id="exam-isDeleted"
                name="isDeleted"
                data-cy="isDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                id="exam-studyClass"
                name="studyClass"
                data-cy="studyClass"
                label={translate('lumiApp.exam.studyClass')}
                type="select"
              >
                <option value="" key="0" />
                {studyClasses
                  ? studyClasses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/exam" replace variant="info">
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

export default ExamUpdate;
