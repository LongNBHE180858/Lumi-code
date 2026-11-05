import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getGrades } from 'app/entities/grade/grade.reducer';
import { getEntities as getSubjects } from 'app/entities/subject/subject.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ClassJoinMode } from 'app/shared/model/enumerations/class-join-mode.model';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './study-class.reducer';

export const StudyClassUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const subjects = useAppSelector(state => state.subject.entities);
  const grades = useAppSelector(state => state.grade.entities);
  const studyClassEntity = useAppSelector(state => state.studyClass.entity);
  const loading = useAppSelector(state => state.studyClass.loading);
  const updating = useAppSelector(state => state.studyClass.updating);
  const updateSuccess = useAppSelector(state => state.studyClass.updateSuccess);
  const classJoinModeValues = Object.keys(ClassJoinMode);
  const recordStatusValues = Object.keys(RecordStatus);

  const handleClose = () => {
    navigate(`/study-class${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.maxStudents !== undefined && typeof values.maxStudents !== 'number') {
      values.maxStudents = Number(values.maxStudents);
    }
    if (values.currentStudents !== undefined && typeof values.currentStudents !== 'number') {
      values.currentStudents = Number(values.currentStudents);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.joinCodeExpiresAt = convertDateTimeToServer(values.joinCodeExpiresAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...studyClassEntity,
      ...values,
      instructor: users.find(it => it.id.toString() === values.instructor?.toString()),
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
          joinCodeExpiresAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          joinMode: 'INVITE_ONLY',
          status: 'DRAFT',
          ...studyClassEntity,
          createdAt: convertDateTimeFromServer(studyClassEntity.createdAt),
          joinCodeExpiresAt: convertDateTimeFromServer(studyClassEntity.joinCodeExpiresAt),
          updatedAt: convertDateTimeFromServer(studyClassEntity.updatedAt),
          instructor: studyClassEntity?.instructor?.id,
          subject: studyClassEntity?.subject?.id,
          grade: studyClassEntity?.grade?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.studyClass.home.createOrEditLabel" data-cy="StudyClassCreateUpdateHeading">
            <Translate contentKey="lumiApp.studyClass.home.createOrEditLabel">Create or edit a StudyClass</Translate>
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
                  id="study-class-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.studyClass.name')}
                id="study-class-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.description')}
                id="study-class-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.joinCode')}
                id="study-class-joinCode"
                name="joinCode"
                data-cy="joinCode"
                type="text"
                validate={{
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.joinMode')}
                id="study-class-joinMode"
                name="joinMode"
                data-cy="joinMode"
                type="select"
              >
                {classJoinModeValues.map(classJoinMode => (
                  <option value={classJoinMode} key={classJoinMode}>
                    {translate(`lumiApp.ClassJoinMode.${classJoinMode}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.studyClass.maxStudents')}
                id="study-class-maxStudents"
                name="maxStudents"
                data-cy="maxStudents"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.currentStudents')}
                id="study-class-currentStudents"
                name="currentStudents"
                data-cy="currentStudents"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.status')}
                id="study-class-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {recordStatusValues.map(recordStatus => (
                  <option value={recordStatus} key={recordStatus}>
                    {translate(`lumiApp.RecordStatus.${recordStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.studyClass.isDeleted')}
                id="study-class-isDeleted"
                name="isDeleted"
                data-cy="isDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.createdAt')}
                id="study-class-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.joinCodeExpiresAt')}
                id="study-class-joinCodeExpiresAt"
                name="joinCodeExpiresAt"
                data-cy="joinCodeExpiresAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.studyClass.updatedAt')}
                id="study-class-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="study-class-instructor"
                name="instructor"
                data-cy="instructor"
                label={translate('lumiApp.studyClass.instructor')}
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
                id="study-class-subject"
                name="subject"
                data-cy="subject"
                label={translate('lumiApp.studyClass.subject')}
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
                id="study-class-grade"
                name="grade"
                data-cy="grade"
                label={translate('lumiApp.studyClass.grade')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/study-class" replace variant="info">
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

export default StudyClassUpdate;
