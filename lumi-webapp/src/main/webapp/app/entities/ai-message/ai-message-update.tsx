import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getAiConversations } from 'app/entities/ai-conversation/ai-conversation.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, updateEntity } from './ai-message.reducer';

export const AiMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const aiConversations = useAppSelector(state => state.aiConversation.entities);
  const aiMessageEntity = useAppSelector(state => state.aiMessage.entity);
  const loading = useAppSelector(state => state.aiMessage.loading);
  const updating = useAppSelector(state => state.aiMessage.updating);
  const updateSuccess = useAppSelector(state => state.aiMessage.updateSuccess);

  const handleClose = () => {
    navigate('/ai-message');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getAiConversations({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    if (values.feedbackRating !== undefined && typeof values.feedbackRating !== 'number') {
      values.feedbackRating = Number(values.feedbackRating);
    }
    values.feedbackAt = convertDateTimeToServer(values.feedbackAt);

    const entity = {
      ...aiMessageEntity,
      ...values,
      conversation: aiConversations.find(it => it.id.toString() === values.conversation?.toString()),
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
          feedbackAt: displayDefaultDateTime(),
        }
      : {
          ...aiMessageEntity,
          createdAt: convertDateTimeFromServer(aiMessageEntity.createdAt),
          feedbackAt: convertDateTimeFromServer(aiMessageEntity.feedbackAt),
          conversation: aiMessageEntity?.conversation?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.aiMessage.home.createOrEditLabel" data-cy="AiMessageCreateUpdateHeading">
            <Translate contentKey="lumiApp.aiMessage.home.createOrEditLabel">Create or edit a AiMessage</Translate>
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
                  id="ai-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.aiMessage.senderRole')}
                id="ai-message-senderRole"
                name="senderRole"
                data-cy="senderRole"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.content')}
                id="ai-message-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.createdAt')}
                id="ai-message-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.citations')}
                id="ai-message-citations"
                name="citations"
                data-cy="citations"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.generationStatus')}
                id="ai-message-generationStatus"
                name="generationStatus"
                data-cy="generationStatus"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.modelName')}
                id="ai-message-modelName"
                name="modelName"
                data-cy="modelName"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.feedbackRating')}
                id="ai-message-feedbackRating"
                name="feedbackRating"
                data-cy="feedbackRating"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.feedbackNote')}
                id="ai-message-feedbackNote"
                name="feedbackNote"
                data-cy="feedbackNote"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.aiMessage.feedbackAt')}
                id="ai-message-feedbackAt"
                name="feedbackAt"
                data-cy="feedbackAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="ai-message-conversation"
                name="conversation"
                data-cy="conversation"
                label={translate('lumiApp.aiMessage.conversation')}
                type="select"
                required
              >
                <option value="" key="0" />
                {aiConversations
                  ? aiConversations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/ai-message" replace variant="info">
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

export default AiMessageUpdate;
