import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './auth-session.reducer';

export const AuthSessionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const authSessionEntity = useAppSelector(state => state.authSession.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="authSessionDetailsHeading">
          <Translate contentKey="lumiApp.authSession.detail.title">AuthSession</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{authSessionEntity.id}</dd>
          <dt>
            <span id="deviceInfo">
              <Translate contentKey="lumiApp.authSession.deviceInfo">Device Info</Translate>
            </span>
          </dt>
          <dd>{authSessionEntity.deviceInfo}</dd>
          <dt>
            <span id="refreshToken">
              <Translate contentKey="lumiApp.authSession.refreshToken">Refresh Token</Translate>
            </span>
          </dt>
          <dd>{authSessionEntity.refreshToken}</dd>
          <dt>
            <span id="expiresAt">
              <Translate contentKey="lumiApp.authSession.expiresAt">Expires At</Translate>
            </span>
          </dt>
          <dd>
            {authSessionEntity.expiresAt ? <TextFormat value={authSessionEntity.expiresAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="isRevoked">
              <Translate contentKey="lumiApp.authSession.isRevoked">Is Revoked</Translate>
            </span>
          </dt>
          <dd>{authSessionEntity.isRevoked ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.authSession.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {authSessionEntity.createdAt ? <TextFormat value={authSessionEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.authSession.user">User</Translate>
          </dt>
          <dd>{authSessionEntity.user ? authSessionEntity.user.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/auth-session" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/auth-session/${authSessionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuthSessionDetail;
