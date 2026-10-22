import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './support-ticket.reducer';

export const SupportTicketDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const supportTicketEntity = useAppSelector(state => state.supportTicket.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supportTicketDetailsHeading">
          <Translate contentKey="lumiApp.supportTicket.detail.title">SupportTicket</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{supportTicketEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.supportTicket.type">Type</Translate>
            </span>
          </dt>
          <dd>{supportTicketEntity.type}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="lumiApp.supportTicket.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{supportTicketEntity.subject}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.supportTicket.status">Status</Translate>
            </span>
          </dt>
          <dd>{supportTicketEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.supportTicket.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {supportTicketEntity.createdAt ? (
              <TextFormat value={supportTicketEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.supportTicket.user">User</Translate>
          </dt>
          <dd>{supportTicketEntity.user ? supportTicketEntity.user.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/support-ticket" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/support-ticket/${supportTicketEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupportTicketDetail;
