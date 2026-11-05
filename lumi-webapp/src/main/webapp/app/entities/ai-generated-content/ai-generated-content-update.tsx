import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getLearningDocuments } from 'app/entities/learning-document/learning-document.reducer';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './ai-generated-content.reducer';

export const AiGeneratedContentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const learningDocuments = useAppSelector(state => state.learningDocument.entities);
  const lessons = useAppSelector(state => state.lesson.entities);
  const courses = useAppSelector(state => state.course.entities);
  const aiGeneratedContentEntity = useAppSelector(state => state.aiGeneratedContent.entity);
  const loading = useAppSelector(state => state.aiGeneratedContent.loading);
  const updating = useAppSelector(state => state.aiGeneratedContent.updating);
  const updateSuccess = useAppSelector(state => state.aiGeneratedContent.updateSuccess);

  const handleClose = () => {
    navigate(`/ai-generated-content${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getLearningDocuments({}));
    dispatch(getLessons({}));
    dispatch(getCourses({}));
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
    if (values.version !== undefined && typeof values.version !== 'number') {
      values.version = Number(values.version);
    }
    values.reviewedAt = convertDateTimeToServer(values.reviewedAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);
    if (values.sourceQuestionId !== undefined && typeof values.sourceQuestionId !== 'number') {
      values.sourceQuestionId = Number(values.sourceQuestionId);
    }

    const entity = {
      ...aiGeneratedContentEntity,
      ...values,
      createdBy: users.find(it => it.id.toString() === values.createdBy?.toString()),
      document: learningDocuments.find(it => it.id.toString() === values.document?.toString()),
      lesson: lessons.find(it => it.id.toString() === values.lesson?.toString()),
      reviewedBy: users.find(it => it.id.toString() === values.reviewedBy?.toString()),
      course: courses.find(it => it.id.toString() === values.course?.toString()),
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
          reviewedAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...aiGeneratedContentEntity,
          reviewedAt: convertDateTimeFromServer(aiGeneratedContentEntity.reviewedAt),
          createdAt: convertDateTimeFromServer(aiGeneratedContentEntity.createdAt),
          updatedAt: convertDateTimeFromServer(aiGeneratedContentEntity.updatedAt),
          createdBy: aiGeneratedContentEntity?.createdBy?.id,
          document: aiGeneratedContentEntity?.document?.id,
          lesson: aiGeneratedContentEntity?.lesson?.id,
          reviewedBy: aiGeneratedContentEntity?.reviewedBy?.id,
          course: aiGeneratedContentEntity?.course?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.aiGeneratedContent.home.createOrEditLabel" data-cy="AiGeneratedContentCreateUpdateHeading">
            <Translate contentKey="lumiApp.aiGeneratedContent.home.createOrEditLabel">Create or edit a AiGeneratedContent</Translate>
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
                  id="ai-generated-content-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.contentType')}
                id="ai-generated-content-contentType"
                name="contentType"
                data-cy="contentType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 30, message: translate('entity.validation.maxlength', { max: 30 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.requestHash')}
                id="ai-generated-content-requestHash"
                name="requestHash"
                data-cy="requestHash"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.generationConfig')}
                id="ai-generated-content-generationConfig"
                name="generationConfig"
                data-cy="generationConfig"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.sourceVersion')}
                id="ai-generated-content-sourceVersion"
                name="sourceVersion"
                data-cy="sourceVersion"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.modelName')}
                id="ai-generated-content-modelName"
                name="modelName"
                data-cy="modelName"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.promptVersion')}
                id="ai-generated-content-promptVersion"
                name="promptVersion"
                data-cy="promptVersion"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.content')}
                id="ai-generated-content-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.generationStatus')}
                id="ai-generated-content-generationStatus"
                name="generationStatus"
                data-cy="generationStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.visibility')}
                id="ai-generated-content-visibility"
                name="visibility"
                data-cy="visibility"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.version')}
                id="ai-generated-content-version"
                name="version"
                data-cy="version"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.reviewStatus')}
                id="ai-generated-content-reviewStatus"
                name="reviewStatus"
                data-cy="reviewStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.reviewedAt')}
                id="ai-generated-content-reviewedAt"
                name="reviewedAt"
                data-cy="reviewedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.reviewerFeedback')}
                id="ai-generated-content-reviewerFeedback"
                name="reviewerFeedback"
                data-cy="reviewerFeedback"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.createdAt')}
                id="ai-generated-content-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.updatedAt')}
                id="ai-generated-content-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.sourceQuestionId')}
                id="ai-generated-content-sourceQuestionId"
                name="sourceQuestionId"
                data-cy="sourceQuestionId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.aiGeneratedContent.contentHash')}
                id="ai-generated-content-contentHash"
                name="contentHash"
                data-cy="contentHash"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                id="ai-generated-content-createdBy"
                name="createdBy"
                data-cy="createdBy"
                label={translate('lumiApp.aiGeneratedContent.createdBy')}
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
                id="ai-generated-content-document"
                name="document"
                data-cy="document"
                label={translate('lumiApp.aiGeneratedContent.document')}
                type="select"
              >
                <option value="" key="0" />
                {learningDocuments
                  ? learningDocuments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="ai-generated-content-lesson"
                name="lesson"
                data-cy="lesson"
                label={translate('lumiApp.aiGeneratedContent.lesson')}
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
              <ValidatedField
                id="ai-generated-content-reviewedBy"
                name="reviewedBy"
                data-cy="reviewedBy"
                label={translate('lumiApp.aiGeneratedContent.reviewedBy')}
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
              <ValidatedField
                id="ai-generated-content-course"
                name="course"
                data-cy="course"
                label={translate('lumiApp.aiGeneratedContent.course')}
                type="select"
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
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/ai-generated-content"
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

export default AiGeneratedContentUpdate;
