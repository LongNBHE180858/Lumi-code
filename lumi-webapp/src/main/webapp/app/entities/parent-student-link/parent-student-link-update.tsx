import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './parent-student-link.reducer';

export const ParentStudentLinkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const parentStudentLinkEntity = useAppSelector(state => state.parentStudentLink.entity);
  const loading = useAppSelector(state => state.parentStudentLink.loading);
  const updating = useAppSelector(state => state.parentStudentLink.updating);
  const updateSuccess = useAppSelector(state => state.parentStudentLink.updateSuccess);

  const handleClose = () => {
    navigate(`/parent-student-link${location.search}`);
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
    values.linkedAt = convertDateTimeToServer(values.linkedAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.codeExpiresAt = convertDateTimeToServer(values.codeExpiresAt);
    values.confirmedAt = convertDateTimeToServer(values.confirmedAt);
    values.revokedAt = convertDateTimeToServer(values.revokedAt);
    values.guardianVerifiedAt = convertDateTimeToServer(values.guardianVerifiedAt);

    const entity = {
      ...parentStudentLinkEntity,
      ...values,
      parent: users.find(it => it.id.toString() === values.parent?.toString()),
      student: users.find(it => it.id.toString() === values.student?.toString()),
      confirmedBy: users.find(it => it.id.toString() === values.confirmedBy?.toString()),
      guardianVerifiedBy: users.find(it => it.id.toString() === values.guardianVerifiedBy?.toString()),
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
          linkedAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          codeExpiresAt: displayDefaultDateTime(),
          confirmedAt: displayDefaultDateTime(),
          revokedAt: displayDefaultDateTime(),
          guardianVerifiedAt: displayDefaultDateTime(),
        }
      : {
          ...parentStudentLinkEntity,
          linkedAt: convertDateTimeFromServer(parentStudentLinkEntity.linkedAt),
          createdAt: convertDateTimeFromServer(parentStudentLinkEntity.createdAt),
          codeExpiresAt: convertDateTimeFromServer(parentStudentLinkEntity.codeExpiresAt),
          confirmedAt: convertDateTimeFromServer(parentStudentLinkEntity.confirmedAt),
          revokedAt: convertDateTimeFromServer(parentStudentLinkEntity.revokedAt),
          guardianVerifiedAt: convertDateTimeFromServer(parentStudentLinkEntity.guardianVerifiedAt),
          parent: parentStudentLinkEntity?.parent?.id,
          student: parentStudentLinkEntity?.student?.id,
          confirmedBy: parentStudentLinkEntity?.confirmedBy?.id,
          guardianVerifiedBy: parentStudentLinkEntity?.guardianVerifiedBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.parentStudentLink.home.createOrEditLabel" data-cy="ParentStudentLinkCreateUpdateHeading">
            <Translate contentKey="lumiApp.parentStudentLink.home.createOrEditLabel">Create or edit a ParentStudentLink</Translate>
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
                  id="parent-student-link-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.linkCode')}
                id="parent-student-link-linkCode"
                name="linkCode"
                data-cy="linkCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.status')}
                id="parent-student-link-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.linkedAt')}
                id="parent-student-link-linkedAt"
                name="linkedAt"
                data-cy="linkedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.createdAt')}
                id="parent-student-link-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.codeExpiresAt')}
                id="parent-student-link-codeExpiresAt"
                name="codeExpiresAt"
                data-cy="codeExpiresAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.confirmedAt')}
                id="parent-student-link-confirmedAt"
                name="confirmedAt"
                data-cy="confirmedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.revokedAt')}
                id="parent-student-link-revokedAt"
                name="revokedAt"
                data-cy="revokedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.relationshipType')}
                id="parent-student-link-relationshipType"
                name="relationshipType"
                data-cy="relationshipType"
                type="text"
                validate={{
                  maxLength: { value: 30, message: translate('entity.validation.maxlength', { max: 30 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.guardianVerificationStatus')}
                id="parent-student-link-guardianVerificationStatus"
                name="guardianVerificationStatus"
                data-cy="guardianVerificationStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.guardianVerifiedAt')}
                id="parent-student-link-guardianVerifiedAt"
                name="guardianVerifiedAt"
                data-cy="guardianVerifiedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.parentStudentLink.guardianVerificationNote')}
                id="parent-student-link-guardianVerificationNote"
                name="guardianVerificationNote"
                data-cy="guardianVerificationNote"
                type="textarea"
              />
              <ValidatedField
                id="parent-student-link-parent"
                name="parent"
                data-cy="parent"
                label={translate('lumiApp.parentStudentLink.parent')}
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
                id="parent-student-link-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.parentStudentLink.student')}
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
                id="parent-student-link-confirmedBy"
                name="confirmedBy"
                data-cy="confirmedBy"
                label={translate('lumiApp.parentStudentLink.confirmedBy')}
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
                id="parent-student-link-guardianVerifiedBy"
                name="guardianVerifiedBy"
                data-cy="guardianVerifiedBy"
                label={translate('lumiApp.parentStudentLink.guardianVerifiedBy')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/parent-student-link" replace variant="info">
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

export default ParentStudentLinkUpdate;
