import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './flashcard-review.reducer';

export const FlashcardReviewDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const flashcardReviewEntity = useAppSelector(state => state.flashcardReview.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="flashcardReviewDetailsHeading">
          <Translate contentKey="lumiApp.flashcardReview.detail.title">FlashcardReview</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{flashcardReviewEntity.id}</dd>
          <dt>
            <span id="nextReviewAt">
              <Translate contentKey="lumiApp.flashcardReview.nextReviewAt">Next Review At</Translate>
            </span>
          </dt>
          <dd>
            {flashcardReviewEntity.nextReviewAt ? (
              <TextFormat value={flashcardReviewEntity.nextReviewAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="intervalDays">
              <Translate contentKey="lumiApp.flashcardReview.intervalDays">Interval Days</Translate>
            </span>
          </dt>
          <dd>{flashcardReviewEntity.intervalDays}</dd>
          <dt>
            <Translate contentKey="lumiApp.flashcardReview.student">Student</Translate>
          </dt>
          <dd>{flashcardReviewEntity.student ? flashcardReviewEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.flashcardReview.flashcard">Flashcard</Translate>
          </dt>
          <dd>{flashcardReviewEntity.flashcard ? flashcardReviewEntity.flashcard.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/flashcard-review" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/flashcard-review/${flashcardReviewEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FlashcardReviewDetail;
