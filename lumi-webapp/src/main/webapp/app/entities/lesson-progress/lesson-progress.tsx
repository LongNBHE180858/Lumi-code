import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './lesson-progress.reducer';

export const LessonProgress = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const lessonProgressList = useAppSelector(state => state.lessonProgress.entities);
  const loading = useAppSelector(state => state.lessonProgress.loading);
  const totalItems = useAppSelector(state => state.lessonProgress.totalItems);

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
      <h2 id="lesson-progress-heading" data-cy="LessonProgressHeading">
        <Translate contentKey="lumiApp.lessonProgress.home.title">Lesson Progresses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.lessonProgress.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/lesson-progress/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.lessonProgress.home.createLabel">Create new Lesson Progress</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {lessonProgressList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.lessonProgress.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.lessonProgress.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('lastPositionSeconds')}>
                  <Translate contentKey="lumiApp.lessonProgress.lastPositionSeconds">Last Position Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastPositionSeconds')} />
                </th>
                <th className="hand" onClick={sort('lastAccessedAt')}>
                  <Translate contentKey="lumiApp.lessonProgress.lastAccessedAt">Last Accessed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastAccessedAt')} />
                </th>
                <th className="hand" onClick={sort('completedAt')}>
                  <Translate contentKey="lumiApp.lessonProgress.completedAt">Completed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('completedAt')} />
                </th>
                <th className="hand" onClick={sort('rowVersion')}>
                  <Translate contentKey="lumiApp.lessonProgress.rowVersion">Row Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rowVersion')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lessonProgress.enrollment">Enrollment</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lessonProgress.lesson">Lesson</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {lessonProgressList.map(lessonProgress => (
                <tr key={`entity-${lessonProgress.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/lesson-progress/${lessonProgress.id}`} variant="link" size="sm">
                      {lessonProgress.id}
                    </Button>
                  </td>
                  <td>{lessonProgress.status}</td>
                  <td>{lessonProgress.lastPositionSeconds}</td>
                  <td>
                    {lessonProgress.lastAccessedAt ? (
                      <TextFormat type="date" value={lessonProgress.lastAccessedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {lessonProgress.completedAt ? (
                      <TextFormat type="date" value={lessonProgress.completedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{lessonProgress.rowVersion}</td>
                  <td>
                    {lessonProgress.enrollment ? (
                      <Link to={`/course-enrollment/${lessonProgress.enrollment.id}`}>{lessonProgress.enrollment.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{lessonProgress.lesson ? <Link to={`/lesson/${lessonProgress.lesson.id}`}>{lessonProgress.lesson.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/lesson-progress/${lessonProgress.id}`}
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
                        to={`/lesson-progress/${lessonProgress.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/lesson-progress/${lessonProgress.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.lessonProgress.home.notFound">No Lesson Progresses found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={lessonProgressList && lessonProgressList.length > 0 ? '' : 'd-none'}>
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

export default LessonProgress;
