import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './class-material.reducer';

export const ClassMaterialDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const classMaterialEntity = useAppSelector(state => state.classMaterial.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="classMaterialDetailsHeading">
          <Translate contentKey="lumiApp.classMaterial.detail.title">ClassMaterial</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.classMaterial.title">Title</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.title}</dd>
          <dt>
            <span id="fileType">
              <Translate contentKey="lumiApp.classMaterial.fileType">File Type</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.fileType}</dd>
          <dt>
            <span id="fileUrl">
              <Translate contentKey="lumiApp.classMaterial.fileUrl">File Url</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.fileUrl}</dd>
          <dt>
            <span id="fileSizeBytes">
              <Translate contentKey="lumiApp.classMaterial.fileSizeBytes">File Size Bytes</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.fileSizeBytes}</dd>
          <dt>
            <span id="uploadedAt">
              <Translate contentKey="lumiApp.classMaterial.uploadedAt">Uploaded At</Translate>
            </span>
          </dt>
          <dd>
            {classMaterialEntity.uploadedAt ? (
              <TextFormat value={classMaterialEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="fileObjectKey">
              <Translate contentKey="lumiApp.classMaterial.fileObjectKey">File Object Key</Translate>
            </span>
          </dt>
          <dd>{classMaterialEntity.fileObjectKey}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.classMaterial.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {classMaterialEntity.updatedAt ? (
              <TextFormat value={classMaterialEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.classMaterial.studyClass">Study Class</Translate>
          </dt>
          <dd>{classMaterialEntity.studyClass ? classMaterialEntity.studyClass.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/class-material" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/class-material/${classMaterialEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClassMaterialDetail;
