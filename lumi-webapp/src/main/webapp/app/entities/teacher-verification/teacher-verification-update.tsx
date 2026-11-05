import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './teacher-verification.reducer';

export const TeacherVerificationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const teacherVerificationEntity = useAppSelector(state => state.teacherVerification.entity);
  const loading = useAppSelector(state => state.teacherVerification.loading);
  const updating = useAppSelector(state => state.teacherVerification.updating);
  const updateSuccess = useAppSelector(state => state.teacherVerification.updateSuccess);

  const handleClose = () => {
    navigate(`/teacher-verification${location.search}`);
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
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    values.approvedAt = convertDateTimeToServer(values.approvedAt);
    values.reviewedAt = convertDateTimeToServer(values.reviewedAt);

    const entity = {
      ...teacherVerificationEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      reviewedBy: users.find(it => it.id.toString() === values.reviewedBy?.toString()),
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
          approvedAt: displayDefaultDateTime(),
          reviewedAt: displayDefaultDateTime(),
        }
      : {
          ...teacherVerificationEntity,
          submittedAt: convertDateTimeFromServer(teacherVerificationEntity.submittedAt),
          approvedAt: convertDateTimeFromServer(teacherVerificationEntity.approvedAt),
          reviewedAt: convertDateTimeFromServer(teacherVerificationEntity.reviewedAt),
          user: teacherVerificationEntity?.user?.id,
          reviewedBy: teacherVerificationEntity?.reviewedBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.teacherVerification.home.createOrEditLabel" data-cy="TeacherVerificationCreateUpdateHeading">
            <Translate contentKey="lumiApp.teacherVerification.home.createOrEditLabel">Create or edit a TeacherVerification</Translate>
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
                  id="teacher-verification-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.teacherVerification.identityDocumentUrl')}
                id="teacher-verification-identityDocumentUrl"
                name="identityDocumentUrl"
                data-cy="identityDocumentUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.teacherVerification.verificationStatus')}
                id="teacher-verification-verificationStatus"
                name="verificationStatus"
                data-cy="verificationStatus"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.teacherVerification.notes')}
                id="teacher-verification-notes"
                name="notes"
                data-cy="notes"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.teacherVerification.submittedAt')}
                id="teacher-verification-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.teacherVerification.approvedAt')}
                id="teacher-verification-approvedAt"
                name="approvedAt"
                data-cy="approvedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.teacherVerification.reviewedAt')}
                id="teacher-verification-reviewedAt"
                name="reviewedAt"
                data-cy="reviewedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="teacher-verification-user"
                name="user"
                data-cy="user"
                label={translate('lumiApp.teacherVerification.user')}
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
                id="teacher-verification-reviewedBy"
                name="reviewedBy"
                data-cy="reviewedBy"
                label={translate('lumiApp.teacherVerification.reviewedBy')}
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
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/teacher-verification"
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

export default TeacherVerificationUpdate;
