import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getStudyClasses } from 'app/entities/study-class/study-class.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './class-member.reducer';

export const ClassMemberUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const studyClasses = useAppSelector(state => state.studyClass.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const classMemberEntity = useAppSelector(state => state.classMember.entity);
  const loading = useAppSelector(state => state.classMember.loading);
  const updating = useAppSelector(state => state.classMember.updating);
  const updateSuccess = useAppSelector(state => state.classMember.updateSuccess);

  const handleClose = () => {
    navigate(`/class-member${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStudyClasses({}));
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
    values.joinedAt = convertDateTimeToServer(values.joinedAt);
    values.requestedAt = convertDateTimeToServer(values.requestedAt);
    values.leftAt = convertDateTimeToServer(values.leftAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...classMemberEntity,
      ...values,
      studyClass: studyClasses.find(it => it.id.toString() === values.studyClass?.toString()),
      student: users.find(it => it.id.toString() === values.student?.toString()),
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
          joinedAt: displayDefaultDateTime(),
          requestedAt: displayDefaultDateTime(),
          leftAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...classMemberEntity,
          joinedAt: convertDateTimeFromServer(classMemberEntity.joinedAt),
          requestedAt: convertDateTimeFromServer(classMemberEntity.requestedAt),
          leftAt: convertDateTimeFromServer(classMemberEntity.leftAt),
          updatedAt: convertDateTimeFromServer(classMemberEntity.updatedAt),
          studyClass: classMemberEntity?.studyClass?.id,
          student: classMemberEntity?.student?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.classMember.home.createOrEditLabel" data-cy="ClassMemberCreateUpdateHeading">
            <Translate contentKey="lumiApp.classMember.home.createOrEditLabel">Create or edit a ClassMember</Translate>
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
                  id="class-member-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.classMember.status')}
                id="class-member-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMember.joinedAt')}
                id="class-member-joinedAt"
                name="joinedAt"
                data-cy="joinedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.classMember.requestedAt')}
                id="class-member-requestedAt"
                name="requestedAt"
                data-cy="requestedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.classMember.leftAt')}
                id="class-member-leftAt"
                name="leftAt"
                data-cy="leftAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.classMember.updatedAt')}
                id="class-member-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="class-member-studyClass"
                name="studyClass"
                data-cy="studyClass"
                label={translate('lumiApp.classMember.studyClass')}
                type="select"
                required
              >
                <option value="" key="0" />
                {studyClasses
                  ? studyClasses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="class-member-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.classMember.student')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/class-member" replace variant="info">
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

export default ClassMemberUpdate;
