import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './flashcard-review.reducer';

export const FlashcardReview = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const flashcardReviewList = useAppSelector(state => state.flashcardReview.entities);
  const loading = useAppSelector(state => state.flashcardReview.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="flashcard-review-heading" data-cy="FlashcardReviewHeading">
        <Translate contentKey="lumiApp.flashcardReview.home.title">Flashcard Reviews</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.flashcardReview.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/flashcard-review/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.flashcardReview.home.createLabel">Create new Flashcard Review</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {flashcardReviewList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.flashcardReview.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('nextReviewAt')}>
                  <Translate contentKey="lumiApp.flashcardReview.nextReviewAt">Next Review At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('nextReviewAt')} />
                </th>
                <th className="hand" onClick={sort('intervalDays')}>
                  <Translate contentKey="lumiApp.flashcardReview.intervalDays">Interval Days</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('intervalDays')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.flashcardReview.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.flashcardReview.flashcard">Flashcard</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {flashcardReviewList.map(flashcardReview => (
                <tr key={`entity-${flashcardReview.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/flashcard-review/${flashcardReview.id}`} variant="link" size="sm">
                      {flashcardReview.id}
                    </Button>
                  </td>
                  <td>
                    {flashcardReview.nextReviewAt ? (
                      <TextFormat type="date" value={flashcardReview.nextReviewAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{flashcardReview.intervalDays}</td>
                  <td>{flashcardReview.student ? flashcardReview.student.login : ''}</td>
                  <td>
                    {flashcardReview.flashcard ? (
                      <Link to={`/flashcard/${flashcardReview.flashcard.id}`}>{flashcardReview.flashcard.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/flashcard-review/${flashcardReview.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/flashcard-review/${flashcardReview.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/flashcard-review/${flashcardReview.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="lumiApp.flashcardReview.home.notFound">No Flashcard Reviews found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default FlashcardReview;
