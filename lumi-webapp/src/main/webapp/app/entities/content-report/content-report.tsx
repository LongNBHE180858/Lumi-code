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

import { getEntities, reset } from './content-report.reducer';

export const ContentReport = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const contentReportList = useAppSelector(state => state.contentReport.entities);
  const loading = useAppSelector(state => state.contentReport.loading);
  const links = useAppSelector(state => state.contentReport.links);
  const updateSuccess = useAppSelector(state => state.contentReport.updateSuccess);

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
      <h2 id="content-report-heading" data-cy="ContentReportHeading">
        <Translate contentKey="lumiApp.contentReport.home.title">Content Reports</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.contentReport.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/content-report/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.contentReport.home.createLabel">Create new Content Report</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={contentReportList ? contentReportList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {contentReportList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.contentReport.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('targetType')}>
                    <Translate contentKey="lumiApp.contentReport.targetType">Target Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('targetType')} />
                  </th>
                  <th className="hand" onClick={sort('targetId')}>
                    <Translate contentKey="lumiApp.contentReport.targetId">Target Id</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('targetId')} />
                  </th>
                  <th className="hand" onClick={sort('reason')}>
                    <Translate contentKey="lumiApp.contentReport.reason">Reason</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                  </th>
                  <th className="hand" onClick={sort('status')}>
                    <Translate contentKey="lumiApp.contentReport.status">Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                  </th>
                  <th className="hand" onClick={sort('createdAt')}>
                    <Translate contentKey="lumiApp.contentReport.createdAt">Created At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                  </th>
                  <th className="hand" onClick={sort('resolvedAt')}>
                    <Translate contentKey="lumiApp.contentReport.resolvedAt">Resolved At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('resolvedAt')} />
                  </th>
                  <th className="hand" onClick={sort('resolutionNote')}>
                    <Translate contentKey="lumiApp.contentReport.resolutionNote">Resolution Note</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('resolutionNote')} />
                  </th>
                  <th className="hand" onClick={sort('targetVersion')}>
                    <Translate contentKey="lumiApp.contentReport.targetVersion">Target Version</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('targetVersion')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.contentReport.reporter">Reporter</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.contentReport.assignedTo">Assigned To</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.contentReport.resolvedBy">Resolved By</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {contentReportList.map(contentReport => (
                  <tr key={`entity-${contentReport.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/content-report/${contentReport.id}`} variant="link" size="sm">
                        {contentReport.id}
                      </Button>
                    </td>
                    <td>{contentReport.targetType}</td>
                    <td>{contentReport.targetId}</td>
                    <td>{contentReport.reason}</td>
                    <td>{contentReport.status}</td>
                    <td>
                      {contentReport.createdAt ? <TextFormat type="date" value={contentReport.createdAt} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>
                      {contentReport.resolvedAt ? (
                        <TextFormat type="date" value={contentReport.resolvedAt} format={APP_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>{contentReport.resolutionNote}</td>
                    <td>{contentReport.targetVersion}</td>
                    <td>{contentReport.reporter ? contentReport.reporter.login : ''}</td>
                    <td>{contentReport.assignedTo ? contentReport.assignedTo.login : ''}</td>
                    <td>{contentReport.resolvedBy ? contentReport.resolvedBy.login : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/content-report/${contentReport.id}`}
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
                          to={`/content-report/${contentReport.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/content-report/${contentReport.id}/delete`)}
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
                <Translate contentKey="lumiApp.contentReport.home.notFound">No Content Reports found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default ContentReport;
