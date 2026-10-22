import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './knowledge-edge.reducer';

export const KnowledgeEdge = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const knowledgeEdgeList = useAppSelector(state => state.knowledgeEdge.entities);
  const loading = useAppSelector(state => state.knowledgeEdge.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="knowledge-edge-heading" data-cy="KnowledgeEdgeHeading">
        <Translate contentKey="lumiApp.knowledgeEdge.home.title">Knowledge Edges</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.knowledgeEdge.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/knowledge-edge/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.knowledgeEdge.home.createLabel">Create new Knowledge Edge</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {knowledgeEdgeList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.knowledgeEdge.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('relationType')}>
                  <Translate contentKey="lumiApp.knowledgeEdge.relationType">Relation Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('relationType')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.knowledgeEdge.fromNode">From Node</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.knowledgeEdge.toNode">To Node</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {knowledgeEdgeList.map(knowledgeEdge => (
                <tr key={`entity-${knowledgeEdge.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/knowledge-edge/${knowledgeEdge.id}`} variant="link" size="sm">
                      {knowledgeEdge.id}
                    </Button>
                  </td>
                  <td>{knowledgeEdge.relationType}</td>
                  <td>
                    {knowledgeEdge.fromNode ? (
                      <Link to={`/knowledge-node/${knowledgeEdge.fromNode.id}`}>{knowledgeEdge.fromNode.title}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {knowledgeEdge.toNode ? (
                      <Link to={`/knowledge-node/${knowledgeEdge.toNode.id}`}>{knowledgeEdge.toNode.title}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/knowledge-edge/${knowledgeEdge.id}`}
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
                        to={`/knowledge-edge/${knowledgeEdge.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/knowledge-edge/${knowledgeEdge.id}/delete`)}
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
              <Translate contentKey="lumiApp.knowledgeEdge.home.notFound">No Knowledge Edges found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default KnowledgeEdge;
