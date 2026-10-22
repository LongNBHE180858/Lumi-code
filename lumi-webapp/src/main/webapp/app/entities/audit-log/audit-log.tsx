import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import InfiniteScroll from 'react-infinite-scroll-component';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

import { getEntities, reset } from './audit-log.reducer';

export const AuditLog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const auditLogList = useAppSelector(state => state.auditLog.entities);
  const loading = useAppSelector(state => state.auditLog.loading);
  const links = useAppSelector(state => state.auditLog.links);
  const updateSuccess = useAppSelector(state => state.auditLog.updateSuccess);

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
      <h2 id="audit-log-heading" data-cy="AuditLogHeading">
        <Translate contentKey="lumiApp.auditLog.home.title">Audit Logs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.auditLog.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/audit-log/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.auditLog.home.createLabel">Create new Audit Log</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={auditLogList ? auditLogList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {auditLogList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.auditLog.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('action')}>
                    <Translate contentKey="lumiApp.auditLog.action">Action</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('action')} />
                  </th>
                  <th className="hand" onClick={sort('targetEntity')}>
                    <Translate contentKey="lumiApp.auditLog.targetEntity">Target Entity</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('targetEntity')} />
                  </th>
                  <th className="hand" onClick={sort('targetId')}>
                    <Translate contentKey="lumiApp.auditLog.targetId">Target Id</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('targetId')} />
                  </th>
                  <th className="hand" onClick={sort('ipAddress')}>
                    <Translate contentKey="lumiApp.auditLog.ipAddress">Ip Address</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('ipAddress')} />
                  </th>
                  <th className="hand" onClick={sort('details')}>
                    <Translate contentKey="lumiApp.auditLog.details">Details</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('details')} />
                  </th>
                  <th className="hand" onClick={sort('occurredAt')}>
                    <Translate contentKey="lumiApp.auditLog.occurredAt">Occurred At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('occurredAt')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.auditLog.actor">Actor</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {auditLogList.map(auditLog => (
                  <tr key={`entity-${auditLog.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/audit-log/${auditLog.id}`} variant="link" size="sm">
                        {auditLog.id}
                      </Button>
                    </td>
                    <td>{auditLog.action}</td>
                    <td>{auditLog.targetEntity}</td>
                    <td>{auditLog.targetId}</td>
                    <td>{auditLog.ipAddress}</td>
                    <td>{auditLog.details}</td>
                    <td>{auditLog.occurredAt ? <TextFormat type="date" value={auditLog.occurredAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{auditLog.actor ? auditLog.actor.login : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button as={Link as any} to={`/audit-log/${auditLog.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          as={Link as any}
                          to={`/audit-log/${auditLog.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/audit-log/${auditLog.id}/delete`)}
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
                <Translate contentKey="lumiApp.auditLog.home.notFound">No Audit Logs found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default AuditLog;
