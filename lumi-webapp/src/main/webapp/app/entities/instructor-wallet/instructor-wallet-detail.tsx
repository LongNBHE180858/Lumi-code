import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './instructor-wallet.reducer';

export const InstructorWalletDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const instructorWalletEntity = useAppSelector(state => state.instructorWallet.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="instructorWalletDetailsHeading">
          <Translate contentKey="lumiApp.instructorWallet.detail.title">InstructorWallet</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{instructorWalletEntity.id}</dd>
          <dt>
            <span id="pendingAmount">
              <Translate contentKey="lumiApp.instructorWallet.pendingAmount">Pending Amount</Translate>
            </span>
          </dt>
          <dd>{instructorWalletEntity.pendingAmount}</dd>
          <dt>
            <span id="availableAmount">
              <Translate contentKey="lumiApp.instructorWallet.availableAmount">Available Amount</Translate>
            </span>
          </dt>
          <dd>{instructorWalletEntity.availableAmount}</dd>
          <dt>
            <span id="rowVersion">
              <Translate contentKey="lumiApp.instructorWallet.rowVersion">Row Version</Translate>
            </span>
          </dt>
          <dd>{instructorWalletEntity.rowVersion}</dd>
          <dt>
            <Translate contentKey="lumiApp.instructorWallet.instructor">Instructor</Translate>
          </dt>
          <dd>{instructorWalletEntity.instructor ? instructorWalletEntity.instructor.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/instructor-wallet" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/instructor-wallet/${instructorWalletEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstructorWalletDetail;
