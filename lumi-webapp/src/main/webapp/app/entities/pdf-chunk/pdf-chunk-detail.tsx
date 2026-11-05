import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './pdf-chunk.reducer';

export const PdfChunkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const pdfChunkEntity = useAppSelector(state => state.pdfChunk.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pdfChunkDetailsHeading">
          <Translate contentKey="lumiApp.pdfChunk.detail.title">PdfChunk</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.id}</dd>
          <dt>
            <span id="pageNumber">
              <Translate contentKey="lumiApp.pdfChunk.pageNumber">Page Number</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.pageNumber}</dd>
          <dt>
            <span id="chunkText">
              <Translate contentKey="lumiApp.pdfChunk.chunkText">Chunk Text</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.chunkText}</dd>
          <dt>
            <span id="embedding">
              <Translate contentKey="lumiApp.pdfChunk.embedding">Embedding</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.embedding}</dd>
          <dt>
            <span id="chunkIndex">
              <Translate contentKey="lumiApp.pdfChunk.chunkIndex">Chunk Index</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.chunkIndex}</dd>
          <dt>
            <span id="indexVersion">
              <Translate contentKey="lumiApp.pdfChunk.indexVersion">Index Version</Translate>
            </span>
          </dt>
          <dd>{pdfChunkEntity.indexVersion}</dd>
          <dt>
            <Translate contentKey="lumiApp.pdfChunk.material">Material</Translate>
          </dt>
          <dd>{pdfChunkEntity.material ? pdfChunkEntity.material.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/pdf-chunk" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/pdf-chunk/${pdfChunkEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PdfChunkDetail;
