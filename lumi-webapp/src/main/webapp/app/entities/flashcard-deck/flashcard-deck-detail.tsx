import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './flashcard-deck.reducer';

export const FlashcardDeckDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const flashcardDeckEntity = useAppSelector(state => state.flashcardDeck.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="flashcardDeckDetailsHeading">
          <Translate contentKey="lumiApp.flashcardDeck.detail.title">FlashcardDeck</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{flashcardDeckEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.flashcardDeck.title">Title</Translate>
            </span>
          </dt>
          <dd>{flashcardDeckEntity.title}</dd>
          <dt>
            <span id="isPublic">
              <Translate contentKey="lumiApp.flashcardDeck.isPublic">Is Public</Translate>
            </span>
          </dt>
          <dd>{flashcardDeckEntity.isPublic ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.flashcardDeck.owner">Owner</Translate>
          </dt>
          <dd>{flashcardDeckEntity.owner ? flashcardDeckEntity.owner.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.flashcardDeck.subject">Subject</Translate>
          </dt>
          <dd>{flashcardDeckEntity.subject ? flashcardDeckEntity.subject.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/flashcard-deck" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/flashcard-deck/${flashcardDeckEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FlashcardDeckDetail;
