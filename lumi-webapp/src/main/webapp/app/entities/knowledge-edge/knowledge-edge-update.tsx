import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getKnowledgeNodes } from 'app/entities/knowledge-node/knowledge-node.reducer';

import { createEntity, getEntity, reset, updateEntity } from './knowledge-edge.reducer';

export const KnowledgeEdgeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const knowledgeNodes = useAppSelector(state => state.knowledgeNode.entities);
  const knowledgeEdgeEntity = useAppSelector(state => state.knowledgeEdge.entity);
  const loading = useAppSelector(state => state.knowledgeEdge.loading);
  const updating = useAppSelector(state => state.knowledgeEdge.updating);
  const updateSuccess = useAppSelector(state => state.knowledgeEdge.updateSuccess);

  const handleClose = () => {
    navigate('/knowledge-edge');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getKnowledgeNodes({}));
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
      ...knowledgeEdgeEntity,
      ...values,
      fromNode: knowledgeNodes.find(it => it.id.toString() === values.fromNode?.toString()),
      toNode: knowledgeNodes.find(it => it.id.toString() === values.toNode?.toString()),
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
          ...knowledgeEdgeEntity,
          fromNode: knowledgeEdgeEntity?.fromNode?.id,
          toNode: knowledgeEdgeEntity?.toNode?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.knowledgeEdge.home.createOrEditLabel" data-cy="KnowledgeEdgeCreateUpdateHeading">
            <Translate contentKey="lumiApp.knowledgeEdge.home.createOrEditLabel">Create or edit a KnowledgeEdge</Translate>
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
                  id="knowledge-edge-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.knowledgeEdge.relationType')}
                id="knowledge-edge-relationType"
                name="relationType"
                data-cy="relationType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                id="knowledge-edge-fromNode"
                name="fromNode"
                data-cy="fromNode"
                label={translate('lumiApp.knowledgeEdge.fromNode')}
                type="select"
                required
              >
                <option value="" key="0" />
                {knowledgeNodes
                  ? knowledgeNodes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="knowledge-edge-toNode"
                name="toNode"
                data-cy="toNode"
                label={translate('lumiApp.knowledgeEdge.toNode')}
                type="select"
                required
              >
                <option value="" key="0" />
                {knowledgeNodes
                  ? knowledgeNodes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/knowledge-edge" replace variant="info">
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

export default KnowledgeEdgeUpdate;
