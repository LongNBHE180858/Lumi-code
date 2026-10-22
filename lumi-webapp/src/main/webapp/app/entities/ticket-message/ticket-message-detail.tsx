import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-message.reducer';

export const TicketMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const ticketMessageEntity = useAppSelector(state => state.ticketMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketMessageDetailsHeading">
          <Translate contentKey="lumiApp.ticketMessage.detail.title">TicketMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketMessageEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.ticketMessage.content">Content</Translate>
            </span>
          </dt>
          <dd>{ticketMessageEntity.content}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.ticketMessage.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {ticketMessageEntity.createdAt ? (
              <TextFormat value={ticketMessageEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.ticketMessage.sender">Sender</Translate>
          </dt>
          <dd>{ticketMessageEntity.sender ? ticketMessageEntity.sender.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.ticketMessage.ticket">Ticket</Translate>
          </dt>
          <dd>{ticketMessageEntity.ticket ? ticketMessageEntity.ticket.subject : ''}</dd>
        </dl>
        <Button as={Link as any} to="/ticket-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/ticket-message/${ticketMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketMessageDetail;
