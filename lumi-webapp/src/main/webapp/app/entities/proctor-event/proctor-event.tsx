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

import { getEntities, reset } from './proctor-event.reducer';

export const ProctorEvent = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const proctorEventList = useAppSelector(state => state.proctorEvent.entities);
  const loading = useAppSelector(state => state.proctorEvent.loading);
  const links = useAppSelector(state => state.proctorEvent.links);
  const updateSuccess = useAppSelector(state => state.proctorEvent.updateSuccess);

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
      <h2 id="proctor-event-heading" data-cy="ProctorEventHeading">
        <Translate contentKey="lumiApp.proctorEvent.home.title">Proctor Events</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.proctorEvent.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/proctor-event/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.proctorEvent.home.createLabel">Create new Proctor Event</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={proctorEventList ? proctorEventList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {proctorEventList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.proctorEvent.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('violationType')}>
                    <Translate contentKey="lumiApp.proctorEvent.violationType">Violation Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('violationType')} />
                  </th>
                  <th className="hand" onClick={sort('photoUrl')}>
                    <Translate contentKey="lumiApp.proctorEvent.photoUrl">Photo Url</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('photoUrl')} />
                  </th>
                  <th className="hand" onClick={sort('occurredAt')}>
                    <Translate contentKey="lumiApp.proctorEvent.occurredAt">Occurred At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('occurredAt')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.proctorEvent.session">Session</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {proctorEventList.map(proctorEvent => (
                  <tr key={`entity-${proctorEvent.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/proctor-event/${proctorEvent.id}`} variant="link" size="sm">
                        {proctorEvent.id}
                      </Button>
                    </td>
                    <td>{proctorEvent.violationType}</td>
                    <td>{proctorEvent.photoUrl}</td>
                    <td>
                      {proctorEvent.occurredAt ? <TextFormat type="date" value={proctorEvent.occurredAt} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>
                      {proctorEvent.session ? (
                        <Link to={`/proctor-session/${proctorEvent.session.id}`}>{proctorEvent.session.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/proctor-event/${proctorEvent.id}`}
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
                          to={`/proctor-event/${proctorEvent.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/proctor-event/${proctorEvent.id}/delete`)}
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
                <Translate contentKey="lumiApp.proctorEvent.home.notFound">No Proctor Events found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default ProctorEvent;
