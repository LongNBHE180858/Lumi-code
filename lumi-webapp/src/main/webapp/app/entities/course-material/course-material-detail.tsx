import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course-material.reducer';

export const CourseMaterialDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const courseMaterialEntity = useAppSelector(state => state.courseMaterial.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseMaterialDetailsHeading">
          <Translate contentKey="lumiApp.courseMaterial.detail.title">CourseMaterial</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <Translate contentKey="lumiApp.courseMaterial.fileName" />
          </dt>
          <dd>{courseMaterialEntity.fileName}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseMaterial.displayName" />
          </dt>
          <dd>{courseMaterialEntity.displayName}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseMaterial.mimeType" />
          </dt>
          <dd>{courseMaterialEntity.mimeType}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseMaterial.orderIndex" />
          </dt>
          <dd>{courseMaterialEntity.orderIndex}</dd>
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseMaterialEntity.id}</dd>
          <dt>
            <span id="fileType">
              <Translate contentKey="lumiApp.courseMaterial.fileType">File Type</Translate>
            </span>
          </dt>
          <dd>{courseMaterialEntity.fileType}</dd>
          <dt>
            <span id="fileUrl">
              <Translate contentKey="lumiApp.courseMaterial.fileUrl">File Url</Translate>
            </span>
          </dt>
          <dd>{courseMaterialEntity.fileUrl}</dd>
          <dt>
            <span id="fileSizeBytes">
              <Translate contentKey="lumiApp.courseMaterial.fileSizeBytes">File Size Bytes</Translate>
            </span>
          </dt>
          <dd>{courseMaterialEntity.fileSizeBytes}</dd>
          <dt>
            <Translate contentKey="lumiApp.courseMaterial.lesson">Lesson</Translate>
          </dt>
          <dd>{courseMaterialEntity.lesson ? courseMaterialEntity.lesson.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/course-material" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/course-material/${courseMaterialEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseMaterialDetail;
