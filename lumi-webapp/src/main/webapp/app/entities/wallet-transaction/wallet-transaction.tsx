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

import { getEntities, reset } from './wallet-transaction.reducer';

export const WalletTransaction = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const walletTransactionList = useAppSelector(state => state.walletTransaction.entities);
  const loading = useAppSelector(state => state.walletTransaction.loading);
  const links = useAppSelector(state => state.walletTransaction.links);
  const updateSuccess = useAppSelector(state => state.walletTransaction.updateSuccess);

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
      <h2 id="wallet-transaction-heading" data-cy="WalletTransactionHeading">
        <Translate contentKey="lumiApp.walletTransaction.home.title">Wallet Transactions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.walletTransaction.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/wallet-transaction/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.walletTransaction.home.createLabel">Create new Wallet Transaction</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={walletTransactionList ? walletTransactionList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {walletTransactionList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.walletTransaction.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('type')}>
                    <Translate contentKey="lumiApp.walletTransaction.type">Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                  </th>
                  <th className="hand" onClick={sort('amount')}>
                    <Translate contentKey="lumiApp.walletTransaction.amount">Amount</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('amount')} />
                  </th>
                  <th className="hand" onClick={sort('status')}>
                    <Translate contentKey="lumiApp.walletTransaction.status">Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                  </th>
                  <th className="hand" onClick={sort('idempotencyKey')}>
                    <Translate contentKey="lumiApp.walletTransaction.idempotencyKey">Idempotency Key</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('idempotencyKey')} />
                  </th>
                  <th className="hand" onClick={sort('occurredAt')}>
                    <Translate contentKey="lumiApp.walletTransaction.occurredAt">Occurred At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('occurredAt')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.walletTransaction.wallet">Wallet</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.walletTransaction.order">Order</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {walletTransactionList.map(walletTransaction => (
                  <tr key={`entity-${walletTransaction.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/wallet-transaction/${walletTransaction.id}`} variant="link" size="sm">
                        {walletTransaction.id}
                      </Button>
                    </td>
                    <td>
                      <Translate contentKey={`lumiApp.TransactionType.${walletTransaction.type}`} />
                    </td>
                    <td>{walletTransaction.amount}</td>
                    <td>{walletTransaction.status}</td>
                    <td>{walletTransaction.idempotencyKey}</td>
                    <td>
                      {walletTransaction.occurredAt ? (
                        <TextFormat type="date" value={walletTransaction.occurredAt} format={APP_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>
                      {walletTransaction.wallet ? (
                        <Link to={`/instructor-wallet/${walletTransaction.wallet.id}`}>{walletTransaction.wallet.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td>
                      {walletTransaction.order ? (
                        <Link to={`/course-order/${walletTransaction.order.id}`}>{walletTransaction.order.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/wallet-transaction/${walletTransaction.id}`}
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
                          to={`/wallet-transaction/${walletTransaction.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/wallet-transaction/${walletTransaction.id}/delete`)}
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
                <Translate contentKey="lumiApp.walletTransaction.home.notFound">No Wallet Transactions found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default WalletTransaction;
