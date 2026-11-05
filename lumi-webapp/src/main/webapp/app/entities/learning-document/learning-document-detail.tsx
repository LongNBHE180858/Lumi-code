import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './learning-document.reducer';

export const LearningDocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const learningDocumentEntity = useAppSelector(state => state.learningDocument.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="learningDocumentDetailsHeading">
          <Translate contentKey="lumiApp.learningDocument.detail.title">LearningDocument</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.id}</dd>
          <dt>
            <span id="sourceType">
              <Translate contentKey="lumiApp.learningDocument.sourceType">Source Type</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.sourceType}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.learningDocument.title">Title</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.title}</dd>
          <dt>
            <span id="fileObjectKey">
              <Translate contentKey="lumiApp.learningDocument.fileObjectKey">File Object Key</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.fileObjectKey}</dd>
          <dt>
            <span id="contentHash">
              <Translate contentKey="lumiApp.learningDocument.contentHash">Content Hash</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.contentHash}</dd>
          <dt>
            <span id="sourceVersion">
              <Translate contentKey="lumiApp.learningDocument.sourceVersion">Source Version</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.sourceVersion}</dd>
          <dt>
            <span id="processingStatus">
              <Translate contentKey="lumiApp.learningDocument.processingStatus">Processing Status</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.processingStatus}</dd>
          <dt>
            <span id="indexStatus">
              <Translate contentKey="lumiApp.learningDocument.indexStatus">Index Status</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.indexStatus}</dd>
          <dt>
            <span id="extractedTextObjectKey">
              <Translate contentKey="lumiApp.learningDocument.extractedTextObjectKey">Extracted Text Object Key</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.extractedTextObjectKey}</dd>
          <dt>
            <span id="embeddingModel">
              <Translate contentKey="lumiApp.learningDocument.embeddingModel">Embedding Model</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.embeddingModel}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="lumiApp.learningDocument.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.errorMessage}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.learningDocument.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {learningDocumentEntity.createdAt ? (
              <TextFormat value={learningDocumentEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.learningDocument.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {learningDocumentEntity.updatedAt ? (
              <TextFormat value={learningDocumentEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="bookSeries">
              <Translate contentKey="lumiApp.learningDocument.bookSeries">Book Series</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.bookSeries}</dd>
          <dt>
            <span id="sourceMetadata">
              <Translate contentKey="lumiApp.learningDocument.sourceMetadata">Source Metadata</Translate>
            </span>
          </dt>
          <dd>{learningDocumentEntity.sourceMetadata}</dd>
          <dt>
            <Translate contentKey="lumiApp.learningDocument.courseMaterial">Course Material</Translate>
          </dt>
          <dd>{learningDocumentEntity.courseMaterial ? learningDocumentEntity.courseMaterial.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.learningDocument.classMaterial">Class Material</Translate>
          </dt>
          <dd>{learningDocumentEntity.classMaterial ? learningDocumentEntity.classMaterial.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.learningDocument.uploadedBy">Uploaded By</Translate>
          </dt>
          <dd>{learningDocumentEntity.uploadedBy ? learningDocumentEntity.uploadedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.learningDocument.subject">Subject</Translate>
          </dt>
          <dd>{learningDocumentEntity.subject ? learningDocumentEntity.subject.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.learningDocument.grade">Grade</Translate>
          </dt>
          <dd>{learningDocumentEntity.grade ? learningDocumentEntity.grade.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/learning-document" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/learning-document/${learningDocumentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LearningDocumentDetail;
