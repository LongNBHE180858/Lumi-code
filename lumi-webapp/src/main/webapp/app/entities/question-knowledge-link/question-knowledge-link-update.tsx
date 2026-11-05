import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './question-knowledge-link.reducer';

export const QuestionKnowledgeLinkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const questionKnowledgeLinkEntity = useAppSelector(state => state.questionKnowledgeLink.entity);
  const loading = useAppSelector(state => state.questionKnowledgeLink.loading);
  const updating = useAppSelector(state => state.questionKnowledgeLink.updating);
  const updateSuccess = useAppSelector(state => state.questionKnowledgeLink.updateSuccess);

  const handleClose = () => {
    navigate(`/question-knowledge-link${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.weight !== undefined && typeof values.weight !== 'number') {
      values.weight = Number(values.weight);
    }

    const entity = {
      ...questionKnowledgeLinkEntity,
      ...values,
      question: values.question ? { id: Number(values.question) } : null,
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
      ? {}
      : {
          ...questionKnowledgeLinkEntity,
          question: questionKnowledgeLinkEntity?.question?.id,
          knowledgeNode: questionKnowledgeLinkEntity?.knowledgeNode?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.questionKnowledgeLink.home.createOrEditLabel" data-cy="QuestionKnowledgeLinkCreateUpdateHeading">
            <Translate contentKey="lumiApp.questionKnowledgeLink.home.createOrEditLabel">Create or edit a QuestionKnowledgeLink</Translate>
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
                  id="question-knowledge-link-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.questionKnowledgeLink.weight')}
                id="question-knowledge-link-weight"
                name="weight"
                data-cy="weight"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="question-knowledge-link-question"
                name="question"
                data-cy="question"
                label={translate('lumiApp.questionKnowledgeLink.question')}
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
              <ValidatedField
                id="question-knowledge-link-knowledgeNode"
                name="knowledgeNode"
                data-cy="knowledgeNode"
                label={translate('lumiApp.questionKnowledgeLink.knowledgeNode')}
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
                to="/question-knowledge-link"
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

export default QuestionKnowledgeLinkUpdate;
