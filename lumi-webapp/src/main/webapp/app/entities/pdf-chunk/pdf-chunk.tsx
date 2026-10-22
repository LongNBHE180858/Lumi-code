import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './pdf-chunk.reducer';

export const PdfChunk = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const pdfChunkList = useAppSelector(state => state.pdfChunk.entities);
  const loading = useAppSelector(state => state.pdfChunk.loading);

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
      <h2 id="pdf-chunk-heading" data-cy="PdfChunkHeading">
        <Translate contentKey="lumiApp.pdfChunk.home.title">Pdf Chunks</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.pdfChunk.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/pdf-chunk/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.pdfChunk.home.createLabel">Create new Pdf Chunk</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {pdfChunkList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.pdfChunk.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('pageNumber')}>
                  <Translate contentKey="lumiApp.pdfChunk.pageNumber">Page Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pageNumber')} />
                </th>
                <th className="hand" onClick={sort('chunkText')}>
                  <Translate contentKey="lumiApp.pdfChunk.chunkText">Chunk Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('chunkText')} />
                </th>
                <th className="hand" onClick={sort('embedding')}>
                  <Translate contentKey="lumiApp.pdfChunk.embedding">Embedding</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('embedding')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.pdfChunk.material">Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {pdfChunkList.map(pdfChunk => (
                <tr key={`entity-${pdfChunk.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/pdf-chunk/${pdfChunk.id}`} variant="link" size="sm">
                      {pdfChunk.id}
                    </Button>
                  </td>
                  <td>{pdfChunk.pageNumber}</td>
                  <td>{pdfChunk.chunkText}</td>
                  <td>{pdfChunk.embedding}</td>
                  <td>{pdfChunk.material ? <Link to={`/course-material/${pdfChunk.material.id}`}>{pdfChunk.material.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/pdf-chunk/${pdfChunk.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/pdf-chunk/${pdfChunk.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/pdf-chunk/${pdfChunk.id}/delete`)}
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
              <Translate contentKey="lumiApp.pdfChunk.home.notFound">No Pdf Chunks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default PdfChunk;
