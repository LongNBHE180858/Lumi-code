import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './chapter.reducer';

export const ChapterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const chapterEntity = useAppSelector(state => state.chapter.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="chapterDetailsHeading">
          <Translate contentKey="lumiApp.chapter.detail.title">Chapter</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{chapterEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.chapter.title">Title</Translate>
            </span>
          </dt>
          <dd>{chapterEntity.title}</dd>
          <dt>
            <span id="orderIndex">
              <Translate contentKey="lumiApp.chapter.orderIndex">Order Index</Translate>
            </span>
          </dt>
          <dd>{chapterEntity.orderIndex}</dd>
          <dt>
            <Translate contentKey="lumiApp.chapter.course">Course</Translate>
          </dt>
          <dd>{chapterEntity.course ? chapterEntity.course.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/chapter" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/chapter/${chapterEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChapterDetail;
