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

import { getEntities } from './assignment.reducer';

export const Assignment = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const assignmentList = useAppSelector(state => state.assignment.entities);
  const loading = useAppSelector(state => state.assignment.loading);
  const totalItems = useAppSelector(state => state.assignment.totalItems);

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
      <h2 id="assignment-heading" data-cy="AssignmentHeading">
        <Translate contentKey="lumiApp.assignment.home.title">Assignments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.assignment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/assignment/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.assignment.home.createLabel">Create new Assignment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {assignmentList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.assignment.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.assignment.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('instructions')}>
                  <Translate contentKey="lumiApp.assignment.instructions">Instructions</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('instructions')} />
                </th>
                <th className="hand" onClick={sort('dueAt')}>
                  <Translate contentKey="lumiApp.assignment.dueAt">Due At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dueAt')} />
                </th>
                <th className="hand" onClick={sort('maxScore')}>
                  <Translate contentKey="lumiApp.assignment.maxScore">Max Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxScore')} />
                </th>
                <th className="hand" onClick={sort('allowLate')}>
                  <Translate contentKey="lumiApp.assignment.allowLate">Allow Late</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowLate')} />
                </th>
                <th className="hand" onClick={sort('isDeleted')}>
                  <Translate contentKey="lumiApp.assignment.isDeleted">Is Deleted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isDeleted')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.assignment.studyClass">Study Class</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {assignmentList.map(assignment => (
                <tr key={`entity-${assignment.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/assignment/${assignment.id}`} variant="link" size="sm">
                      {assignment.id}
                    </Button>
                  </td>
                  <td>{assignment.title}</td>
                  <td>{assignment.instructions}</td>
                  <td>{assignment.dueAt ? <TextFormat type="date" value={assignment.dueAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{assignment.maxScore}</td>
                  <td>{assignment.allowLate ? 'true' : 'false'}</td>
                  <td>{assignment.isDeleted ? 'true' : 'false'}</td>
                  <td>
                    {assignment.studyClass ? <Link to={`/study-class/${assignment.studyClass.id}`}>{assignment.studyClass.name}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/assignment/${assignment.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/assignment/${assignment.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/assignment/${assignment.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.assignment.home.notFound">No Assignments found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={assignmentList && assignmentList.length > 0 ? '' : 'd-none'}>
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

export default Assignment;
