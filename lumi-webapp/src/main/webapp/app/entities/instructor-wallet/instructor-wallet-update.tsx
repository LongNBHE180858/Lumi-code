import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';

import { createEntity, getEntity, reset, updateEntity } from './instructor-wallet.reducer';

export const InstructorWalletUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const instructorWalletEntity = useAppSelector(state => state.instructorWallet.entity);
  const loading = useAppSelector(state => state.instructorWallet.loading);
  const updating = useAppSelector(state => state.instructorWallet.updating);
  const updateSuccess = useAppSelector(state => state.instructorWallet.updateSuccess);

  const handleClose = () => {
    navigate(`/instructor-wallet${location.search}`);
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
    if (values.pendingAmount !== undefined && typeof values.pendingAmount !== 'number') {
      values.pendingAmount = Number(values.pendingAmount);
    }
    if (values.availableAmount !== undefined && typeof values.availableAmount !== 'number') {
      values.availableAmount = Number(values.availableAmount);
    }
    if (values.rowVersion !== undefined && typeof values.rowVersion !== 'number') {
      values.rowVersion = Number(values.rowVersion);
    }

    const entity = {
      ...instructorWalletEntity,
      ...values,
      instructor: users.find(it => it.id.toString() === values.instructor?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...instructorWalletEntity,
          instructor: instructorWalletEntity?.instructor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.instructorWallet.home.createOrEditLabel" data-cy="InstructorWalletCreateUpdateHeading">
            <Translate contentKey="lumiApp.instructorWallet.home.createOrEditLabel">Create or edit a InstructorWallet</Translate>
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
                  id="instructor-wallet-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.instructorWallet.pendingAmount')}
                id="instructor-wallet-pendingAmount"
                name="pendingAmount"
                data-cy="pendingAmount"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.instructorWallet.availableAmount')}
                id="instructor-wallet-availableAmount"
                name="availableAmount"
                data-cy="availableAmount"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.instructorWallet.rowVersion')}
                id="instructor-wallet-rowVersion"
                name="rowVersion"
                data-cy="rowVersion"
                type="text"
              />
              <ValidatedField
                id="instructor-wallet-instructor"
                name="instructor"
                data-cy="instructor"
                label={translate('lumiApp.instructorWallet.instructor')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/instructor-wallet" replace variant="info">
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

export default InstructorWalletUpdate;
