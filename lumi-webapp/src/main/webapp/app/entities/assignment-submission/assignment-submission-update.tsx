import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getAssignments } from 'app/entities/assignment/assignment.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './assignment-submission.reducer';

export const AssignmentSubmissionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const assignments = useAppSelector(state => state.assignment.entities);
  const assignmentSubmissionEntity = useAppSelector(state => state.assignmentSubmission.entity);
  const loading = useAppSelector(state => state.assignmentSubmission.loading);
  const updating = useAppSelector(state => state.assignmentSubmission.updating);
  const updateSuccess = useAppSelector(state => state.assignmentSubmission.updateSuccess);

  const handleClose = () => {
    navigate(`/assignment-submission${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getAssignments({}));
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
    if (values.score !== undefined && typeof values.score !== 'number') {
      values.score = Number(values.score);
    }
    values.submittedAt = convertDateTimeToServer(values.submittedAt);

    const entity = {
      ...assignmentSubmissionEntity,
      ...values,
      student: users.find(it => it.id.toString() === values.student?.toString()),
      assignment: assignments.find(it => it.id.toString() === values.assignment?.toString()),
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
          submittedAt: displayDefaultDateTime(),
        }
      : {
          ...assignmentSubmissionEntity,
          submittedAt: convertDateTimeFromServer(assignmentSubmissionEntity.submittedAt),
          student: assignmentSubmissionEntity?.student?.id,
          assignment: assignmentSubmissionEntity?.assignment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.assignmentSubmission.home.createOrEditLabel" data-cy="AssignmentSubmissionCreateUpdateHeading">
            <Translate contentKey="lumiApp.assignmentSubmission.home.createOrEditLabel">Create or edit a AssignmentSubmission</Translate>
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
                  id="assignment-submission-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.assignmentSubmission.content')}
                id="assignment-submission-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignmentSubmission.score')}
                id="assignment-submission-score"
                name="score"
                data-cy="score"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignmentSubmission.teacherFeedback')}
                id="assignment-submission-teacherFeedback"
                name="teacherFeedback"
                data-cy="teacherFeedback"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignmentSubmission.status')}
                id="assignment-submission-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.assignmentSubmission.submittedAt')}
                id="assignment-submission-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="assignment-submission-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.assignmentSubmission.student')}
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
                id="assignment-submission-assignment"
                name="assignment"
                data-cy="assignment"
                label={translate('lumiApp.assignmentSubmission.assignment')}
                type="select"
                required
              >
                <option value="" key="0" />
                {assignments
                  ? assignments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/assignment-submission"
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

export default AssignmentSubmissionUpdate;
