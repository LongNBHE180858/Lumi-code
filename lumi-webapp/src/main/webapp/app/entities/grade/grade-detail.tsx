import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './grade.reducer';

export const GradeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const gradeEntity = useAppSelector(state => state.grade.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="gradeDetailsHeading">
          <Translate contentKey="lumiApp.grade.detail.title">Grade</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{gradeEntity.id}</dd>
          <dt>
            <span id="level">
              <Translate contentKey="lumiApp.grade.level">Level</Translate>
            </span>
          </dt>
          <dd>{gradeEntity.level}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.grade.name">Name</Translate>
            </span>
          </dt>
          <dd>{gradeEntity.name}</dd>
        </dl>
        <Button as={Link as any} to="/grade" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/grade/${gradeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default GradeDetail;
