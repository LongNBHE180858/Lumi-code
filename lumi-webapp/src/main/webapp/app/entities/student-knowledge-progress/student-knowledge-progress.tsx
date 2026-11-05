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

import { getEntities } from './student-knowledge-progress.reducer';

export const StudentKnowledgeProgress = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const studentKnowledgeProgressList = useAppSelector(state => state.studentKnowledgeProgress.entities);
  const loading = useAppSelector(state => state.studentKnowledgeProgress.loading);
  const totalItems = useAppSelector(state => state.studentKnowledgeProgress.totalItems);

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
      <h2 id="student-knowledge-progress-heading" data-cy="StudentKnowledgeProgressHeading">
        <Translate contentKey="lumiApp.studentKnowledgeProgress.home.title">Student Knowledge Progresses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.studentKnowledgeProgress.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/student-knowledge-progress/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.studentKnowledgeProgress.home.createLabel">Create new Student Knowledge Progress</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {studentKnowledgeProgressList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('masteryScore')}>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.masteryScore">Mastery Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('masteryScore')} />
                </th>
                <th className="hand" onClick={sort('evidenceCount')}>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.evidenceCount">Evidence Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('evidenceCount')} />
                </th>
                <th className="hand" onClick={sort('calculationVersion')}>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.calculationVersion">Calculation Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('calculationVersion')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studentKnowledgeProgress.knowledgeNode">Knowledge Node</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {studentKnowledgeProgressList.map(studentKnowledgeProgress => (
                <tr key={`entity-${studentKnowledgeProgress.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/student-knowledge-progress/${studentKnowledgeProgress.id}`} variant="link" size="sm">
                      {studentKnowledgeProgress.id}
                    </Button>
                  </td>
                  <td>{studentKnowledgeProgress.masteryScore}</td>
                  <td>{studentKnowledgeProgress.evidenceCount}</td>
                  <td>{studentKnowledgeProgress.calculationVersion}</td>
                  <td>
                    {studentKnowledgeProgress.updatedAt ? (
                      <TextFormat type="date" value={studentKnowledgeProgress.updatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{studentKnowledgeProgress.student ? studentKnowledgeProgress.student.login : ''}</td>
                  <td>{studentKnowledgeProgress.knowledgeNode?.id ?? ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/student-knowledge-progress/${studentKnowledgeProgress.id}`}
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
                        to={`/student-knowledge-progress/${studentKnowledgeProgress.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/student-knowledge-progress/${studentKnowledgeProgress.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.studentKnowledgeProgress.home.notFound">No Student Knowledge Progresses found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={studentKnowledgeProgressList && studentKnowledgeProgressList.length > 0 ? '' : 'd-none'}>
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

export default StudentKnowledgeProgress;
