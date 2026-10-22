import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './class-member.reducer';

export const ClassMemberDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const classMemberEntity = useAppSelector(state => state.classMember.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="classMemberDetailsHeading">
          <Translate contentKey="lumiApp.classMember.detail.title">ClassMember</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{classMemberEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.classMember.status">Status</Translate>
            </span>
          </dt>
          <dd>{classMemberEntity.status}</dd>
          <dt>
            <span id="joinedAt">
              <Translate contentKey="lumiApp.classMember.joinedAt">Joined At</Translate>
            </span>
          </dt>
          <dd>
            {classMemberEntity.joinedAt ? <TextFormat value={classMemberEntity.joinedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.classMember.student">Student</Translate>
          </dt>
          <dd>{classMemberEntity.student ? classMemberEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.classMember.studyClass">Study Class</Translate>
          </dt>
          <dd>{classMemberEntity.studyClass ? classMemberEntity.studyClass.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/class-member" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/class-member/${classMemberEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClassMemberDetail;
