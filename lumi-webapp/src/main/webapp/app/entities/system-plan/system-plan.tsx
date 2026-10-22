import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './system-plan.reducer';

export const SystemPlan = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const systemPlanList = useAppSelector(state => state.systemPlan.entities);
  const loading = useAppSelector(state => state.systemPlan.loading);
  const totalItems = useAppSelector(state => state.systemPlan.totalItems);

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
      <h2 id="system-plan-heading" data-cy="SystemPlanHeading">
        <Translate contentKey="lumiApp.systemPlan.home.title">System Plans</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.systemPlan.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/system-plan/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.systemPlan.home.createLabel">Create new System Plan</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {systemPlanList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.systemPlan.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="lumiApp.systemPlan.name">Name</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  <Translate contentKey="lumiApp.systemPlan.price">Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('durationDays')}>
                  <Translate contentKey="lumiApp.systemPlan.durationDays">Duration Days</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('durationDays')} />
                </th>
                <th className="hand" onClick={sort('storageQuotaMb')}>
                  <Translate contentKey="lumiApp.systemPlan.storageQuotaMb">Storage Quota Mb</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('storageQuotaMb')} />
                </th>
                <th className="hand" onClick={sort('aiGenerationLimit')}>
                  <Translate contentKey="lumiApp.systemPlan.aiGenerationLimit">Ai Generation Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('aiGenerationLimit')} />
                </th>
                <th className="hand" onClick={sort('maxClasses')}>
                  <Translate contentKey="lumiApp.systemPlan.maxClasses">Max Classes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxClasses')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {systemPlanList.map(systemPlan => (
                <tr key={`entity-${systemPlan.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/system-plan/${systemPlan.id}`} variant="link" size="sm">
                      {systemPlan.id}
                    </Button>
                  </td>
                  <td>{systemPlan.name}</td>
                  <td>{systemPlan.price}</td>
                  <td>{systemPlan.durationDays}</td>
                  <td>{systemPlan.storageQuotaMb}</td>
                  <td>{systemPlan.aiGenerationLimit}</td>
                  <td>{systemPlan.maxClasses}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/system-plan/${systemPlan.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/system-plan/${systemPlan.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/system-plan/${systemPlan.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.systemPlan.home.notFound">No System Plans found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={systemPlanList && systemPlanList.length > 0 ? '' : 'd-none'}>
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

export default SystemPlan;
