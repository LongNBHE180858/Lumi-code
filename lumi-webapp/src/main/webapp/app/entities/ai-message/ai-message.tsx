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

import { getEntities, reset } from './ai-message.reducer';

export const AiMessage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const aiMessageList = useAppSelector(state => state.aiMessage.entities);
  const loading = useAppSelector(state => state.aiMessage.loading);
  const links = useAppSelector(state => state.aiMessage.links);
  const updateSuccess = useAppSelector(state => state.aiMessage.updateSuccess);

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
      <h2 id="ai-message-heading" data-cy="AiMessageHeading">
        <Translate contentKey="lumiApp.aiMessage.home.title">Ai Messages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.aiMessage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/ai-message/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.aiMessage.home.createLabel">Create new Ai Message</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={aiMessageList ? aiMessageList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {aiMessageList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="lumiApp.aiMessage.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('senderRole')}>
                    <Translate contentKey="lumiApp.aiMessage.senderRole">Sender Role</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('senderRole')} />
                  </th>
                  <th className="hand" onClick={sort('content')}>
                    <Translate contentKey="lumiApp.aiMessage.content">Content</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('content')} />
                  </th>
                  <th className="hand" onClick={sort('createdAt')}>
                    <Translate contentKey="lumiApp.aiMessage.createdAt">Created At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                  </th>
                  <th className="hand" onClick={sort('citations')}>
                    <Translate contentKey="lumiApp.aiMessage.citations">Citations</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('citations')} />
                  </th>
                  <th className="hand" onClick={sort('generationStatus')}>
                    <Translate contentKey="lumiApp.aiMessage.generationStatus">Generation Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('generationStatus')} />
                  </th>
                  <th className="hand" onClick={sort('modelName')}>
                    <Translate contentKey="lumiApp.aiMessage.modelName">Model Name</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('modelName')} />
                  </th>
                  <th className="hand" onClick={sort('feedbackRating')}>
                    <Translate contentKey="lumiApp.aiMessage.feedbackRating">Feedback Rating</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('feedbackRating')} />
                  </th>
                  <th className="hand" onClick={sort('feedbackNote')}>
                    <Translate contentKey="lumiApp.aiMessage.feedbackNote">Feedback Note</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('feedbackNote')} />
                  </th>
                  <th className="hand" onClick={sort('feedbackAt')}>
                    <Translate contentKey="lumiApp.aiMessage.feedbackAt">Feedback At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('feedbackAt')} />
                  </th>
                  <th>
                    <Translate contentKey="lumiApp.aiMessage.conversation">Conversation</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {aiMessageList.map(aiMessage => (
                  <tr key={`entity-${aiMessage.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/ai-message/${aiMessage.id}`} variant="link" size="sm">
                        {aiMessage.id}
                      </Button>
                    </td>
                    <td>{aiMessage.senderRole}</td>
                    <td>{aiMessage.content}</td>
                    <td>{aiMessage.createdAt ? <TextFormat type="date" value={aiMessage.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{aiMessage.citations}</td>
                    <td>{aiMessage.generationStatus}</td>
                    <td>{aiMessage.modelName}</td>
                    <td>{aiMessage.feedbackRating}</td>
                    <td>{aiMessage.feedbackNote}</td>
                    <td>
                      {aiMessage.feedbackAt ? <TextFormat type="date" value={aiMessage.feedbackAt} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>
                      {aiMessage.conversation ? (
                        <Link to={`/ai-conversation/${aiMessage.conversation.id}`}>{aiMessage.conversation.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button as={Link as any} to={`/ai-message/${aiMessage.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          as={Link as any}
                          to={`/ai-message/${aiMessage.id}/edit`}
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
                          onClick={() => (globalThis.location.href = `/ai-message/${aiMessage.id}/delete`)}
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
                <Translate contentKey="lumiApp.aiMessage.home.notFound">No Ai Messages found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default AiMessage;
