import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getLearningDocuments } from 'app/entities/learning-document/learning-document.reducer';

import { createEntity, getEntity, reset, updateEntity } from './pdf-chunk.reducer';

export const PdfChunkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const learningDocuments = useAppSelector(state => state.learningDocument.entities);
  const pdfChunkEntity = useAppSelector(state => state.pdfChunk.entity);
  const loading = useAppSelector(state => state.pdfChunk.loading);
  const updating = useAppSelector(state => state.pdfChunk.updating);
  const updateSuccess = useAppSelector(state => state.pdfChunk.updateSuccess);

  const handleClose = () => {
    navigate(`/pdf-chunk${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getLearningDocuments({}));
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
    if (values.pageNumber !== undefined && typeof values.pageNumber !== 'number') {
      values.pageNumber = Number(values.pageNumber);
    }
    if (values.chunkIndex !== undefined && typeof values.chunkIndex !== 'number') {
      values.chunkIndex = Number(values.chunkIndex);
    }
    if (values.indexVersion !== undefined && typeof values.indexVersion !== 'number') {
      values.indexVersion = Number(values.indexVersion);
    }

    const entity = {
      ...pdfChunkEntity,
      ...values,
      material: learningDocuments.find(it => it.id.toString() === values.material?.toString()),
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
          ...pdfChunkEntity,
          material: pdfChunkEntity?.material?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.pdfChunk.home.createOrEditLabel" data-cy="PdfChunkCreateUpdateHeading">
            <Translate contentKey="lumiApp.pdfChunk.home.createOrEditLabel">Create or edit a PdfChunk</Translate>
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
                  id="pdf-chunk-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.pdfChunk.pageNumber')}
                id="pdf-chunk-pageNumber"
                name="pageNumber"
                data-cy="pageNumber"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.pdfChunk.chunkText')}
                id="pdf-chunk-chunkText"
                name="chunkText"
                data-cy="chunkText"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.pdfChunk.embedding')}
                id="pdf-chunk-embedding"
                name="embedding"
                data-cy="embedding"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.pdfChunk.chunkIndex')}
                id="pdf-chunk-chunkIndex"
                name="chunkIndex"
                data-cy="chunkIndex"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.pdfChunk.indexVersion')}
                id="pdf-chunk-indexVersion"
                name="indexVersion"
                data-cy="indexVersion"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="pdf-chunk-material"
                name="material"
                data-cy="material"
                label={translate('lumiApp.pdfChunk.material')}
                type="select"
                required
              >
                <option value="" key="0" />
                {learningDocuments
                  ? learningDocuments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/pdf-chunk" replace variant="info">
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

export default PdfChunkUpdate;
