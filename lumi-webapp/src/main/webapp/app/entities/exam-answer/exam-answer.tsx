import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import InfiniteScroll from 'react-infinite-scroll-component';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

import { getEntities, reset } from './exam-answer.reducer';

export const ExamAnswer = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const examAnswerList = useAppSelector(state => state.examAnswer.entities);
  const loading = useAppSelector(state => state.examAnswer.loading);
  const links = useAppSelector(state => state.examAnswer.links);
  const updateSuccess = useAppSelector(state => state.examAnswer.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if (globalThis.scrollY > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="exam-answer-heading" data-cy="ExamAnswerHeading">
        <Translate contentKey="lumiApp.examAnswer.home.title">Exam Answers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.examAnswer.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/exam-answer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.examAnswer.home.createLabel">Create new Exam Answer</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={examAnswerList ? examAnswerList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {examAnswerList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.examAnswer.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('textAnswer')}>
                    <Translate contentKey="lumiApp.examAnswer.textAnswer">Text Answer</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('textAnswer')} />
                  </th>
                  <th className="hand" onClick={sort('isCorrect')}>
                    <Translate contentKey="lumiApp.examAnswer.isCorrect">Is Correct</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('isCorrect')} />
                  </th>
                  <th className="hand" onClick={sort('score')}>
                    <Translate contentKey="lumiApp.examAnswer.score">Score</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('score')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.examAnswer.attempt">Attempt</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.examAnswer.question">Question</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.examAnswer.option">Option</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {examAnswerList.map(examAnswer => (
                  <tr key={`entity-${examAnswer.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/exam-answer/${examAnswer.id}`} variant="link" size="sm">
                        {examAnswer.id}
                      </Button>
                    </td>
                    <td>{examAnswer.textAnswer}</td>
                    <td>{examAnswer.isCorrect ? 'true' : 'false'}</td>
                    <td>{examAnswer.score}</td>
                    <td>{examAnswer.attempt ? <Link to={`/exam-attempt/${examAnswer.attempt.id}`}>{examAnswer.attempt.id}</Link> : ''}</td>
                    <td>{examAnswer.question ? <Link to={`/question/${examAnswer.question.id}`}>{examAnswer.question.id}</Link> : ''}</td>
                    <td>{examAnswer.option ? <Link to={`/question-option/${examAnswer.option.id}`}>{examAnswer.option.id}</Link> : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/exam-answer/${examAnswer.id}`}
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
                          to={`/exam-answer/${examAnswer.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/exam-answer/${examAnswer.id}/delete`)}
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
                <Translate contentKey="lumiApp.examAnswer.home.notFound">No Exam Answers found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default ExamAnswer;
