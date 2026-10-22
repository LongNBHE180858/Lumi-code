import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSubjects } from 'app/entities/subject/subject.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';

import { createEntity, getEntity, reset, updateEntity } from './flashcard-deck.reducer';

export const FlashcardDeckUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const subjects = useAppSelector(state => state.subject.entities);
  const flashcardDeckEntity = useAppSelector(state => state.flashcardDeck.entity);
  const loading = useAppSelector(state => state.flashcardDeck.loading);
  const updating = useAppSelector(state => state.flashcardDeck.updating);
  const updateSuccess = useAppSelector(state => state.flashcardDeck.updateSuccess);

  const handleClose = () => {
    navigate(`/flashcard-deck${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getSubjects({}));
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

    const entity = {
      ...flashcardDeckEntity,
      ...values,
      owner: users.find(it => it.id.toString() === values.owner?.toString()),
      subject: subjects.find(it => it.id.toString() === values.subject?.toString()),
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
          ...flashcardDeckEntity,
          owner: flashcardDeckEntity?.owner?.id,
          subject: flashcardDeckEntity?.subject?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.flashcardDeck.home.createOrEditLabel" data-cy="FlashcardDeckCreateUpdateHeading">
            <Translate contentKey="lumiApp.flashcardDeck.home.createOrEditLabel">Create or edit a FlashcardDeck</Translate>
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
                  id="flashcard-deck-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.flashcardDeck.title')}
                id="flashcard-deck-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.flashcardDeck.isPublic')}
                id="flashcard-deck-isPublic"
                name="isPublic"
                data-cy="isPublic"
                check
                type="checkbox"
              />
              <ValidatedField
                id="flashcard-deck-owner"
                name="owner"
                data-cy="owner"
                label={translate('lumiApp.flashcardDeck.owner')}
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
                id="flashcard-deck-subject"
                name="subject"
                data-cy="subject"
                label={translate('lumiApp.flashcardDeck.subject')}
                type="select"
              >
                <option value="" key="0" />
                {subjects
                  ? subjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/flashcard-deck" replace variant="info">
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

export default FlashcardDeckUpdate;
