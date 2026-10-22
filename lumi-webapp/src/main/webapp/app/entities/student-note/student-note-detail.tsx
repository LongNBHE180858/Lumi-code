import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './student-note.reducer';

export const StudentNoteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const studentNoteEntity = useAppSelector(state => state.studentNote.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="studentNoteDetailsHeading">
          <Translate contentKey="lumiApp.studentNote.detail.title">StudentNote</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{studentNoteEntity.id}</dd>
          <dt>
            <span id="videoTimestamp">
              <Translate contentKey="lumiApp.studentNote.videoTimestamp">Video Timestamp</Translate>
            </span>
          </dt>
          <dd>{studentNoteEntity.videoTimestamp}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.studentNote.content">Content</Translate>
            </span>
          </dt>
          <dd>{studentNoteEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.studentNote.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {studentNoteEntity.createdAt ? <TextFormat value={studentNoteEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.studentNote.student">Student</Translate>
          </dt>
          <dd>{studentNoteEntity.student ? studentNoteEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.studentNote.lesson">Lesson</Translate>
          </dt>
          <dd>{studentNoteEntity.lesson ? studentNoteEntity.lesson.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/student-note" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/student-note/${studentNoteEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StudentNoteDetail;
