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

import { getEntities } from './study-class.reducer';

export const StudyClass = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const studyClassList = useAppSelector(state => state.studyClass.entities);
  const loading = useAppSelector(state => state.studyClass.loading);
  const totalItems = useAppSelector(state => state.studyClass.totalItems);

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
      <h2 id="study-class-heading" data-cy="StudyClassHeading">
        <Translate contentKey="lumiApp.studyClass.home.title">Study Classes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.studyClass.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/study-class/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.studyClass.home.createLabel">Create new Study Class</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {studyClassList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.studyClass.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="lumiApp.studyClass.name">Name</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="lumiApp.studyClass.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('joinCode')}>
                  <Translate contentKey="lumiApp.studyClass.joinCode">Join Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('joinCode')} />
                </th>
                <th className="hand" onClick={sort('joinMode')}>
                  <Translate contentKey="lumiApp.studyClass.joinMode">Join Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('joinMode')} />
                </th>
                <th className="hand" onClick={sort('maxStudents')}>
                  <Translate contentKey="lumiApp.studyClass.maxStudents">Max Students</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxStudents')} />
                </th>
                <th className="hand" onClick={sort('currentStudents')}>
                  <Translate contentKey="lumiApp.studyClass.currentStudents">Current Students</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currentStudents')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.studyClass.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('isDeleted')}>
                  <Translate contentKey="lumiApp.studyClass.isDeleted">Is Deleted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isDeleted')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.studyClass.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studyClass.instructor">Instructor</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studyClass.subject">Subject</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studyClass.grade">Grade</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {studyClassList.map(studyClass => (
                <tr key={`entity-${studyClass.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/study-class/${studyClass.id}`} variant="link" size="sm">
                      {studyClass.id}
                    </Button>
                  </td>
                  <td>{studyClass.name}</td>
                  <td>{studyClass.description}</td>
                  <td>{studyClass.joinCode}</td>
                  <td>
                    <Translate contentKey={`lumiApp.ClassJoinMode.${studyClass.joinMode}`} />
                  </td>
                  <td>{studyClass.maxStudents}</td>
                  <td>{studyClass.currentStudents}</td>
                  <td>
                    <Translate contentKey={`lumiApp.RecordStatus.${studyClass.status}`} />
                  </td>
                  <td>{studyClass.isDeleted ? 'true' : 'false'}</td>
                  <td>{studyClass.createdAt ? <TextFormat type="date" value={studyClass.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{studyClass.instructor ? studyClass.instructor.login : ''}</td>
                  <td>{studyClass.subject ? <Link to={`/subject/${studyClass.subject.id}`}>{studyClass.subject.name}</Link> : ''}</td>
                  <td>{studyClass.grade ? <Link to={`/grade/${studyClass.grade.id}`}>{studyClass.grade.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/study-class/${studyClass.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/study-class/${studyClass.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/study-class/${studyClass.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.studyClass.home.notFound">No Study Classes found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={studyClassList && studyClassList.length > 0 ? '' : 'd-none'}>
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

export default StudyClass;
