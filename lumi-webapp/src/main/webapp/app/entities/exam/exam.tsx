import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './exam.reducer';

export const Exam = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const examList = useAppSelector(state => state.exam.entities);
  const loading = useAppSelector(state => state.exam.loading);
  const totalItems = useAppSelector(state => state.exam.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
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
      <h2 id="exam-heading" data-cy="ExamHeading">
        <Translate contentKey="lumiApp.exam.home.title">Exams</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.exam.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/exam/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.exam.home.createLabel">Create new Exam</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {examList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.exam.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.exam.title">Title</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('durationMinutes')}>
                  <Translate contentKey="lumiApp.exam.durationMinutes">Duration Minutes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('durationMinutes')} />
                </th>
                <th className="hand" onClick={sort('passScore')}>
                  <Translate contentKey="lumiApp.exam.passScore">Pass Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('passScore')} />
                </th>
                <th className="hand" onClick={sort('maxAttempts')}>
                  <Translate contentKey="lumiApp.exam.maxAttempts">Max Attempts</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxAttempts')} />
                </th>
                <th className="hand" onClick={sort('shuffleQuestions')}>
                  <Translate contentKey="lumiApp.exam.shuffleQuestions">Shuffle Questions</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shuffleQuestions')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.exam.status">Status</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('isDeleted')}>
                  <Translate contentKey="lumiApp.exam.isDeleted">Is Deleted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isDeleted')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.exam.studyClass">Study Class</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {examList.map(exam => (
                <tr key={`entity-${exam.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/exam/${exam.id}`} variant="link" size="sm">
                      {exam.id}
                    </Button>
                  </td>
                  <td>{exam.title}</td>
                  <td>{exam.durationMinutes}</td>
                  <td>{exam.passScore}</td>
                  <td>{exam.maxAttempts}</td>
                  <td>{exam.shuffleQuestions ? 'true' : 'false'}</td>
                  <td>
                    <Translate contentKey={`lumiApp.RecordStatus.${exam.status}`} />
                  </td>
                  <td>{exam.isDeleted ? 'true' : 'false'}</td>
                  <td>{exam.studyClass ? <Link to={`/study-class/${exam.studyClass.id}`}>{exam.studyClass.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/exam/${exam.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/exam/${exam.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                        onClick={() =>
                          (globalThis.location.href = `/exam/${exam.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
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
              <Translate contentKey="lumiApp.exam.home.notFound">No Exams found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={examList && examList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Exam;
