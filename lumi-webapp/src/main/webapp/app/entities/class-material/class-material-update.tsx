import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getStudyClasses } from 'app/entities/study-class/study-class.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './class-material.reducer';

export const ClassMaterialUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const studyClasses = useAppSelector(state => state.studyClass.entities);
  const classMaterialEntity = useAppSelector(state => state.classMaterial.entity);
  const loading = useAppSelector(state => state.classMaterial.loading);
  const updating = useAppSelector(state => state.classMaterial.updating);
  const updateSuccess = useAppSelector(state => state.classMaterial.updateSuccess);

  const handleClose = () => {
    navigate(`/class-material${location.search}`);
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
    if (values.fileSizeBytes !== undefined && typeof values.fileSizeBytes !== 'number') {
      values.fileSizeBytes = Number(values.fileSizeBytes);
    }
    values.uploadedAt = convertDateTimeToServer(values.uploadedAt);

    const entity = {
      ...classMaterialEntity,
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
          uploadedAt: displayDefaultDateTime(),
        }
      : {
          ...classMaterialEntity,
          uploadedAt: convertDateTimeFromServer(classMaterialEntity.uploadedAt),
          studyClass: classMaterialEntity?.studyClass?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.classMaterial.home.createOrEditLabel" data-cy="ClassMaterialCreateUpdateHeading">
            <Translate contentKey="lumiApp.classMaterial.home.createOrEditLabel">Create or edit a ClassMaterial</Translate>
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
                  id="class-material-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.classMaterial.title')}
                id="class-material-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMaterial.fileType')}
                id="class-material-fileType"
                name="fileType"
                data-cy="fileType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMaterial.fileUrl')}
                id="class-material-fileUrl"
                name="fileUrl"
                data-cy="fileUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMaterial.fileSizeBytes')}
                id="class-material-fileSizeBytes"
                name="fileSizeBytes"
                data-cy="fileSizeBytes"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMaterial.uploadedAt')}
                id="class-material-uploadedAt"
                name="uploadedAt"
                data-cy="uploadedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="class-material-studyClass"
                name="studyClass"
                data-cy="studyClass"
                label={translate('lumiApp.classMaterial.studyClass')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/class-material" replace variant="info">
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

export default ClassMaterialUpdate;
