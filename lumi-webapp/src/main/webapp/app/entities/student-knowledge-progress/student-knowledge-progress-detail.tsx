import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './student-knowledge-progress.reducer';

export const StudentKnowledgeProgressDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const studentKnowledgeProgressEntity = useAppSelector(state => state.studentKnowledgeProgress.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="studentKnowledgeProgressDetailsHeading">
          <Translate contentKey="lumiApp.studentKnowledgeProgress.detail.title">StudentKnowledgeProgress</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{studentKnowledgeProgressEntity.id}</dd>
          <dt>
            <span id="masteryScore">
              <Translate contentKey="lumiApp.studentKnowledgeProgress.masteryScore">Mastery Score</Translate>
            </span>
          </dt>
          <dd>{studentKnowledgeProgressEntity.masteryScore}</dd>
          <dt>
            <span id="evidenceCount">
              <Translate contentKey="lumiApp.studentKnowledgeProgress.evidenceCount">Evidence Count</Translate>
            </span>
          </dt>
          <dd>{studentKnowledgeProgressEntity.evidenceCount}</dd>
          <dt>
            <span id="calculationVersion">
              <Translate contentKey="lumiApp.studentKnowledgeProgress.calculationVersion">Calculation Version</Translate>
            </span>
          </dt>
          <dd>{studentKnowledgeProgressEntity.calculationVersion}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.studentKnowledgeProgress.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {studentKnowledgeProgressEntity.updatedAt ? (
              <TextFormat value={studentKnowledgeProgressEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.studentKnowledgeProgress.student">Student</Translate>
          </dt>
          <dd>{studentKnowledgeProgressEntity.student ? studentKnowledgeProgressEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.studentKnowledgeProgress.knowledgeNode">Knowledge Node</Translate>
          </dt>
          <dd>{studentKnowledgeProgressEntity.knowledgeNode ? studentKnowledgeProgressEntity.knowledgeNode.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/student-knowledge-progress" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/student-knowledge-progress/${studentKnowledgeProgressEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StudentKnowledgeProgressDetail;
