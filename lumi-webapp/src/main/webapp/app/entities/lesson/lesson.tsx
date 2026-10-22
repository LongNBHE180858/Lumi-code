import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './lesson.reducer';

export const Lesson = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const lessonList = useAppSelector(state => state.lesson.entities);
  const loading = useAppSelector(state => state.lesson.loading);
  const totalItems = useAppSelector(state => state.lesson.totalItems);

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
      <h2 id="lesson-heading" data-cy="LessonHeading">
        <Translate contentKey="lumiApp.lesson.home.title">Lessons</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.lesson.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/lesson/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.lesson.home.createLabel">Create new Lesson</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {lessonList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.lesson.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.lesson.title">Title</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('contentType')}>
                  <Translate contentKey="lumiApp.lesson.contentType">Content Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentType')} />
                </th>
                <th className="hand" onClick={sort('orderIndex')}>
                  <Translate contentKey="lumiApp.lesson.orderIndex">Order Index</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('orderIndex')} />
                </th>
                <th className="hand" onClick={sort('isPreview')}>
                  <Translate contentKey="lumiApp.lesson.isPreview">Is Preview</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPreview')} />
                </th>
                <th className="hand" onClick={sort('durationMinutes')}>
                  <Translate contentKey="lumiApp.lesson.durationMinutes">Duration Minutes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('durationMinutes')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.lesson.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lesson.chapter">Chapter</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lesson.description" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lesson.videoUrl" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lesson.videoFileName" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.lesson.videoSizeBytes" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {lessonList.map(lesson => (
                <tr key={`entity-${lesson.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/lesson/${lesson.id}`} variant="link" size="sm">
                      {lesson.id}
                    </Button>
                  </td>
                  <td>{lesson.title}</td>
                  <td>{lesson.contentType}</td>
                  <td>{lesson.orderIndex}</td>
                  <td>{lesson.isPreview ? 'true' : 'false'}</td>
                  <td>{lesson.durationMinutes}</td>
                  <td>
                    <Translate contentKey={`lumiApp.RecordStatus.${lesson.status}`} />
                  </td>
                  <td>{lesson.chapter ? <Link to={`/chapter/${lesson.chapter.id}`}>{lesson.chapter.title}</Link> : ''}</td>
                  <td>{lesson.description}</td>
                  <td>{lesson.videoUrl}</td>
                  <td>{lesson.videoFileName}</td>
                  <td>{lesson.videoSizeBytes}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/lesson/${lesson.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/lesson/${lesson.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/lesson/${lesson.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.lesson.home.notFound">No Lessons found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={lessonList && lessonList.length > 0 ? '' : 'd-none'}>
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

export default Lesson;
