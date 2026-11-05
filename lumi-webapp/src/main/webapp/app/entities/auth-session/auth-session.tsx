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

import { getEntities, reset } from './auth-session.reducer';

export const AuthSession = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const authSessionList = useAppSelector(state => state.authSession.entities);
  const loading = useAppSelector(state => state.authSession.loading);
  const links = useAppSelector(state => state.authSession.links);
  const updateSuccess = useAppSelector(state => state.authSession.updateSuccess);

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
      <h2 id="auth-session-heading" data-cy="AuthSessionHeading">
        <Translate contentKey="lumiApp.authSession.home.title">Auth Sessions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.authSession.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/auth-session/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.authSession.home.createLabel">Create new Auth Session</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={authSessionList ? authSessionList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {authSessionList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.authSession.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('deviceInfo')}>
                    <Translate contentKey="lumiApp.authSession.deviceInfo">Device Info</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('deviceInfo')} />
                  </th>
                  <th className="hand" onClick={sort('refreshToken')}>
                    <Translate contentKey="lumiApp.authSession.refreshToken">Refresh Token</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('refreshToken')} />
                  </th>
                  <th className="hand" onClick={sort('expiresAt')}>
                    <Translate contentKey="lumiApp.authSession.expiresAt">Expires At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('expiresAt')} />
                  </th>
                  <th className="hand" onClick={sort('isRevoked')}>
                    <Translate contentKey="lumiApp.authSession.isRevoked">Is Revoked</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('isRevoked')} />
                  </th>
                  <th className="hand" onClick={sort('createdAt')}>
                    <Translate contentKey="lumiApp.authSession.createdAt">Created At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                  </th>
                  <th className="hand" onClick={sort('refreshTokenHash')}>
                    <Translate contentKey="lumiApp.authSession.refreshTokenHash">Refresh Token Hash</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('refreshTokenHash')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.authSession.user">User</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {authSessionList.map(authSession => (
                  <tr key={`entity-${authSession.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/auth-session/${authSession.id}`} variant="link" size="sm">
                        {authSession.id}
                      </Button>
                    </td>
                    <td>{authSession.deviceInfo}</td>
                    <td>{authSession.refreshToken}</td>
                    <td>
                      {authSession.expiresAt ? <TextFormat type="date" value={authSession.expiresAt} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>{authSession.isRevoked ? 'true' : 'false'}</td>
                    <td>
                      {authSession.createdAt ? <TextFormat type="date" value={authSession.createdAt} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>{authSession.refreshTokenHash}</td>
                    <td>{authSession.user ? authSession.user.login : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/auth-session/${authSession.id}`}
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
                          to={`/auth-session/${authSession.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/auth-session/${authSession.id}/delete`)}
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
                <Translate contentKey="lumiApp.authSession.home.notFound">No Auth Sessions found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default AuthSession;
