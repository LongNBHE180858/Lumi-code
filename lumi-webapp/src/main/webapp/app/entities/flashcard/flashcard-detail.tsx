import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './flashcard.reducer';

export const FlashcardDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const flashcardEntity = useAppSelector(state => state.flashcard.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="flashcardDetailsHeading">
          <Translate contentKey="lumiApp.flashcard.detail.title">Flashcard</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{flashcardEntity.id}</dd>
          <dt>
            <span id="frontText">
              <Translate contentKey="lumiApp.flashcard.frontText">Front Text</Translate>
            </span>
          </dt>
          <dd>{flashcardEntity.frontText}</dd>
          <dt>
            <span id="backText">
              <Translate contentKey="lumiApp.flashcard.backText">Back Text</Translate>
            </span>
          </dt>
          <dd>{flashcardEntity.backText}</dd>
          <dt>
            <Translate contentKey="lumiApp.flashcard.deck">Deck</Translate>
          </dt>
          <dd>{flashcardEntity.deck ? flashcardEntity.deck.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/flashcard" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/flashcard/${flashcardEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FlashcardDetail;
