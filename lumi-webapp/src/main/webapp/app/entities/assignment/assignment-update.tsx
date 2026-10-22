import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getStudyClasses } from 'app/entities/study-class/study-class.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './assignment.reducer';

export const AssignmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const studyClasses = useAppSelector(state => state.studyClass.entities);
  const assignmentEntity = useAppSelector(state => state.assignment.entity);
  const loading = useAppSelector(state => state.assignment.loading);
  const updating = useAppSelector(state => state.assignment.updating);
  const updateSuccess = useAppSelector(state => state.assignment.updateSuccess);

  const handleClose = () => {
    navigate(`/assignment${location.search}`);
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
    values.dueAt = convertDateTimeToServer(values.dueAt);
    if (values.maxScore !== undefined && typeof values.maxScore !== 'number') {
      values.maxScore = Number(values.maxScore);
    }

    const entity = {
      ...assignmentEntity,
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
      ? {
          dueAt: displayDefaultDateTime(),
        }
      : {
          ...assignmentEntity,
          dueAt: convertDateTimeFromServer(assignmentEntity.dueAt),
          studyClass: assignmentEntity?.studyClass?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.assignment.home.createOrEditLabel" data-cy="AssignmentCreateUpdateHeading">
            <Translate contentKey="lumiApp.assignment.home.createOrEditLabel">Create or edit a Assignment</Translate>
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
                  id="assignment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.assignment.title')}
                id="assignment-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignment.instructions')}
                id="assignment-instructions"
                name="instructions"
                data-cy="instructions"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignment.dueAt')}
                id="assignment-dueAt"
                name="dueAt"
                data-cy="dueAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.assignment.maxScore')}
                id="assignment-maxScore"
                name="maxScore"
                data-cy="maxScore"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignment.allowLate')}
                id="assignment-allowLate"
                name="allowLate"
                data-cy="allowLate"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.assignment.isDeleted')}
                id="assignment-isDeleted"
                name="isDeleted"
                data-cy="isDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                id="assignment-studyClass"
                name="studyClass"
                data-cy="studyClass"
                label={translate('lumiApp.assignment.studyClass')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/assignment" replace variant="info">
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

export default AssignmentUpdate;
