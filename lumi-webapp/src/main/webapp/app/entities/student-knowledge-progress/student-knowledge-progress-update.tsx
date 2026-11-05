import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './student-knowledge-progress.reducer';

export const StudentKnowledgeProgressUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const studentKnowledgeProgressEntity = useAppSelector(state => state.studentKnowledgeProgress.entity);
  const loading = useAppSelector(state => state.studentKnowledgeProgress.loading);
  const updating = useAppSelector(state => state.studentKnowledgeProgress.updating);
  const updateSuccess = useAppSelector(state => state.studentKnowledgeProgress.updateSuccess);

  const handleClose = () => {
    navigate(`/student-knowledge-progress${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.masteryScore !== undefined && typeof values.masteryScore !== 'number') {
      values.masteryScore = Number(values.masteryScore);
    }
    if (values.evidenceCount !== undefined && typeof values.evidenceCount !== 'number') {
      values.evidenceCount = Number(values.evidenceCount);
    }
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...studentKnowledgeProgressEntity,
      ...values,
      student: users.find(it => it.id.toString() === values.student?.toString()),
      knowledgeNode: values.knowledgeNode ? { id: Number(values.knowledgeNode) } : null,
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
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...studentKnowledgeProgressEntity,
          updatedAt: convertDateTimeFromServer(studentKnowledgeProgressEntity.updatedAt),
          student: studentKnowledgeProgressEntity?.student?.id,
          knowledgeNode: studentKnowledgeProgressEntity?.knowledgeNode?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.studentKnowledgeProgress.home.createOrEditLabel" data-cy="StudentKnowledgeProgressCreateUpdateHeading">
            <Translate contentKey="lumiApp.studentKnowledgeProgress.home.createOrEditLabel">
              Create or edit a StudentKnowledgeProgress
            </Translate>
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
                  id="student-knowledge-progress-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.studentKnowledgeProgress.masteryScore')}
                id="student-knowledge-progress-masteryScore"
                name="masteryScore"
                data-cy="masteryScore"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 100, message: translate('entity.validation.max', { max: 100 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentKnowledgeProgress.evidenceCount')}
                id="student-knowledge-progress-evidenceCount"
                name="evidenceCount"
                data-cy="evidenceCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentKnowledgeProgress.calculationVersion')}
                id="student-knowledge-progress-calculationVersion"
                name="calculationVersion"
                data-cy="calculationVersion"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.studentKnowledgeProgress.updatedAt')}
                id="student-knowledge-progress-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="student-knowledge-progress-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.studentKnowledgeProgress.student')}
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
                id="student-knowledge-progress-knowledgeNode"
                name="knowledgeNode"
                data-cy="knowledgeNode"
                label={translate('lumiApp.studentKnowledgeProgress.knowledgeNode')}
                type="number"
                min="1"
                step="1"
                required
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => !v || Number.isSafeInteger(Number(v)) || translate('entity.validation.number'),
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/student-knowledge-progress"
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

export default StudentKnowledgeProgressUpdate;
