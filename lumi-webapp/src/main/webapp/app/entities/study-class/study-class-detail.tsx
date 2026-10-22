import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './study-class.reducer';

export const StudyClassDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const studyClassEntity = useAppSelector(state => state.studyClass.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="studyClassDetailsHeading">
          <Translate contentKey="lumiApp.studyClass.detail.title">StudyClass</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.studyClass.name">Name</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="lumiApp.studyClass.description">Description</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.description}</dd>
          <dt>
            <span id="joinCode">
              <Translate contentKey="lumiApp.studyClass.joinCode">Join Code</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.joinCode}</dd>
          <dt>
            <span id="joinMode">
              <Translate contentKey="lumiApp.studyClass.joinMode">Join Mode</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.joinMode}</dd>
          <dt>
            <span id="maxStudents">
              <Translate contentKey="lumiApp.studyClass.maxStudents">Max Students</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.maxStudents}</dd>
          <dt>
            <span id="currentStudents">
              <Translate contentKey="lumiApp.studyClass.currentStudents">Current Students</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.currentStudents}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.studyClass.status">Status</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.status}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="lumiApp.studyClass.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{studyClassEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.studyClass.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {studyClassEntity.createdAt ? <TextFormat value={studyClassEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.studyClass.instructor">Instructor</Translate>
          </dt>
          <dd>{studyClassEntity.instructor ? studyClassEntity.instructor.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.studyClass.subject">Subject</Translate>
          </dt>
          <dd>{studyClassEntity.subject ? studyClassEntity.subject.name : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.studyClass.grade">Grade</Translate>
          </dt>
          <dd>{studyClassEntity.grade ? studyClassEntity.grade.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/study-class" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/study-class/${studyClassEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StudyClassDetail;
