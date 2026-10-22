import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getFlashcards } from 'app/entities/flashcard/flashcard.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './flashcard-review.reducer';

export const FlashcardReviewUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const flashcards = useAppSelector(state => state.flashcard.entities);
  const flashcardReviewEntity = useAppSelector(state => state.flashcardReview.entity);
  const loading = useAppSelector(state => state.flashcardReview.loading);
  const updating = useAppSelector(state => state.flashcardReview.updating);
  const updateSuccess = useAppSelector(state => state.flashcardReview.updateSuccess);

  const handleClose = () => {
    navigate('/flashcard-review');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getFlashcards({}));
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
    values.nextReviewAt = convertDateTimeToServer(values.nextReviewAt);
    if (values.intervalDays !== undefined && typeof values.intervalDays !== 'number') {
      values.intervalDays = Number(values.intervalDays);
    }

    const entity = {
      ...flashcardReviewEntity,
      ...values,
      student: users.find(it => it.id.toString() === values.student?.toString()),
      flashcard: flashcards.find(it => it.id.toString() === values.flashcard?.toString()),
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
          nextReviewAt: displayDefaultDateTime(),
        }
      : {
          ...flashcardReviewEntity,
          nextReviewAt: convertDateTimeFromServer(flashcardReviewEntity.nextReviewAt),
          student: flashcardReviewEntity?.student?.id,
          flashcard: flashcardReviewEntity?.flashcard?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.flashcardReview.home.createOrEditLabel" data-cy="FlashcardReviewCreateUpdateHeading">
            <Translate contentKey="lumiApp.flashcardReview.home.createOrEditLabel">Create or edit a FlashcardReview</Translate>
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
                  id="flashcard-review-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.flashcardReview.nextReviewAt')}
                id="flashcard-review-nextReviewAt"
                name="nextReviewAt"
                data-cy="nextReviewAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.flashcardReview.intervalDays')}
                id="flashcard-review-intervalDays"
                name="intervalDays"
                data-cy="intervalDays"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="flashcard-review-student"
                name="student"
                data-cy="student"
                label={translate('lumiApp.flashcardReview.student')}
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
                id="flashcard-review-flashcard"
                name="flashcard"
                data-cy="flashcard"
                label={translate('lumiApp.flashcardReview.flashcard')}
                type="select"
                required
              >
                <option value="" key="0" />
                {flashcards
                  ? flashcards.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/flashcard-review" replace variant="info">
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

export default FlashcardReviewUpdate;
