import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getClassMaterials } from 'app/entities/class-material/class-material.reducer';
import { getEntities as getCourseMaterials } from 'app/entities/course-material/course-material.reducer';
import { getEntities as getGrades } from 'app/entities/grade/grade.reducer';
import { getEntities as getSubjects } from 'app/entities/subject/subject.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './learning-document.reducer';

export const LearningDocumentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courseMaterials = useAppSelector(state => state.courseMaterial.entities);
  const classMaterials = useAppSelector(state => state.classMaterial.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const subjects = useAppSelector(state => state.subject.entities);
  const grades = useAppSelector(state => state.grade.entities);
  const learningDocumentEntity = useAppSelector(state => state.learningDocument.entity);
  const loading = useAppSelector(state => state.learningDocument.loading);
  const updating = useAppSelector(state => state.learningDocument.updating);
  const updateSuccess = useAppSelector(state => state.learningDocument.updateSuccess);

  const handleClose = () => {
    navigate(`/learning-document${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourseMaterials({}));
    dispatch(getClassMaterials({}));
    dispatch(getUsers({}));
    dispatch(getSubjects({}));
    dispatch(getGrades({}));
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
    if (values.sourceVersion !== undefined && typeof values.sourceVersion !== 'number') {
      values.sourceVersion = Number(values.sourceVersion);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...learningDocumentEntity,
      ...values,
      courseMaterial: courseMaterials.find(it => it.id.toString() === values.courseMaterial?.toString()),
      classMaterial: classMaterials.find(it => it.id.toString() === values.classMaterial?.toString()),
      uploadedBy: users.find(it => it.id.toString() === values.uploadedBy?.toString()),
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
          ...learningDocumentEntity,
          createdAt: convertDateTimeFromServer(learningDocumentEntity.createdAt),
          updatedAt: convertDateTimeFromServer(learningDocumentEntity.updatedAt),
          courseMaterial: learningDocumentEntity?.courseMaterial?.id,
          classMaterial: learningDocumentEntity?.classMaterial?.id,
          uploadedBy: learningDocumentEntity?.uploadedBy?.id,
          subject: learningDocumentEntity?.subject?.id,
          grade: learningDocumentEntity?.grade?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.learningDocument.home.createOrEditLabel" data-cy="LearningDocumentCreateUpdateHeading">
            <Translate contentKey="lumiApp.learningDocument.home.createOrEditLabel">Create or edit a LearningDocument</Translate>
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
                  id="learning-document-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.learningDocument.sourceType')}
                id="learning-document-sourceType"
                name="sourceType"
                data-cy="sourceType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.title')}
                id="learning-document-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.fileObjectKey')}
                id="learning-document-fileObjectKey"
                name="fileObjectKey"
                data-cy="fileObjectKey"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.contentHash')}
                id="learning-document-contentHash"
                name="contentHash"
                data-cy="contentHash"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.sourceVersion')}
                id="learning-document-sourceVersion"
                name="sourceVersion"
                data-cy="sourceVersion"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.processingStatus')}
                id="learning-document-processingStatus"
                name="processingStatus"
                data-cy="processingStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.indexStatus')}
                id="learning-document-indexStatus"
                name="indexStatus"
                data-cy="indexStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.extractedTextObjectKey')}
                id="learning-document-extractedTextObjectKey"
                name="extractedTextObjectKey"
                data-cy="extractedTextObjectKey"
                type="text"
                validate={{
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.embeddingModel')}
                id="learning-document-embeddingModel"
                name="embeddingModel"
                data-cy="embeddingModel"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.errorMessage')}
                id="learning-document-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.createdAt')}
                id="learning-document-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.updatedAt')}
                id="learning-document-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.bookSeries')}
                id="learning-document-bookSeries"
                name="bookSeries"
                data-cy="bookSeries"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.learningDocument.sourceMetadata')}
                id="learning-document-sourceMetadata"
                name="sourceMetadata"
                data-cy="sourceMetadata"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="learning-document-courseMaterial"
                name="courseMaterial"
                data-cy="courseMaterial"
                label={translate('lumiApp.learningDocument.courseMaterial')}
                type="select"
              >
                <option value="" key="0" />
                {courseMaterials
                  ? courseMaterials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="learning-document-classMaterial"
                name="classMaterial"
                data-cy="classMaterial"
                label={translate('lumiApp.learningDocument.classMaterial')}
                type="select"
              >
                <option value="" key="0" />
                {classMaterials
                  ? classMaterials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="learning-document-uploadedBy"
                name="uploadedBy"
                data-cy="uploadedBy"
                label={translate('lumiApp.learningDocument.uploadedBy')}
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
                id="learning-document-subject"
                name="subject"
                data-cy="subject"
                label={translate('lumiApp.learningDocument.subject')}
                type="select"
              >
                <option value="" key="0" />
                {subjects
                  ? subjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="learning-document-grade"
                name="grade"
                data-cy="grade"
                label={translate('lumiApp.learningDocument.grade')}
                type="select"
              >
                <option value="" key="0" />
                {grades
                  ? grades.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/learning-document" replace variant="info">
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

export default LearningDocumentUpdate;
