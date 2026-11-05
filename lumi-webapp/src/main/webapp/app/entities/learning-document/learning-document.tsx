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

import { getEntities } from './learning-document.reducer';

export const LearningDocument = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const learningDocumentList = useAppSelector(state => state.learningDocument.entities);
  const loading = useAppSelector(state => state.learningDocument.loading);
  const totalItems = useAppSelector(state => state.learningDocument.totalItems);

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
      <h2 id="learning-document-heading" data-cy="LearningDocumentHeading">
        <Translate contentKey="lumiApp.learningDocument.home.title">Learning Documents</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.learningDocument.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/learning-document/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.learningDocument.home.createLabel">Create new Learning Document</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {learningDocumentList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.learningDocument.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('sourceType')}>
                  <Translate contentKey="lumiApp.learningDocument.sourceType">Source Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceType')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.learningDocument.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('fileObjectKey')}>
                  <Translate contentKey="lumiApp.learningDocument.fileObjectKey">File Object Key</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileObjectKey')} />
                </th>
                <th className="hand" onClick={sort('contentHash')}>
                  <Translate contentKey="lumiApp.learningDocument.contentHash">Content Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentHash')} />
                </th>
                <th className="hand" onClick={sort('sourceVersion')}>
                  <Translate contentKey="lumiApp.learningDocument.sourceVersion">Source Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceVersion')} />
                </th>
                <th className="hand" onClick={sort('processingStatus')}>
                  <Translate contentKey="lumiApp.learningDocument.processingStatus">Processing Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('processingStatus')} />
                </th>
                <th className="hand" onClick={sort('indexStatus')}>
                  <Translate contentKey="lumiApp.learningDocument.indexStatus">Index Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('indexStatus')} />
                </th>
                <th className="hand" onClick={sort('extractedTextObjectKey')}>
                  <Translate contentKey="lumiApp.learningDocument.extractedTextObjectKey">Extracted Text Object Key</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('extractedTextObjectKey')} />
                </th>
                <th className="hand" onClick={sort('embeddingModel')}>
                  <Translate contentKey="lumiApp.learningDocument.embeddingModel">Embedding Model</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('embeddingModel')} />
                </th>
                <th className="hand" onClick={sort('errorMessage')}>
                  <Translate contentKey="lumiApp.learningDocument.errorMessage">Error Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.learningDocument.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.learningDocument.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th className="hand" onClick={sort('bookSeries')}>
                  <Translate contentKey="lumiApp.learningDocument.bookSeries">Book Series</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bookSeries')} />
                </th>
                <th className="hand" onClick={sort('sourceMetadata')}>
                  <Translate contentKey="lumiApp.learningDocument.sourceMetadata">Source Metadata</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceMetadata')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.learningDocument.courseMaterial">Course Material</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.learningDocument.classMaterial">Class Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.learningDocument.uploadedBy">Uploaded By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.learningDocument.subject">Subject</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.learningDocument.grade">Grade</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {learningDocumentList.map(learningDocument => (
                <tr key={`entity-${learningDocument.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/learning-document/${learningDocument.id}`} variant="link" size="sm">
                      {learningDocument.id}
                    </Button>
                  </td>
                  <td>{learningDocument.sourceType}</td>
                  <td>{learningDocument.title}</td>
                  <td>{learningDocument.fileObjectKey}</td>
                  <td>{learningDocument.contentHash}</td>
                  <td>{learningDocument.sourceVersion}</td>
                  <td>{learningDocument.processingStatus}</td>
                  <td>{learningDocument.indexStatus}</td>
                  <td>{learningDocument.extractedTextObjectKey}</td>
                  <td>{learningDocument.embeddingModel}</td>
                  <td>{learningDocument.errorMessage}</td>
                  <td>
                    {learningDocument.createdAt ? (
                      <TextFormat type="date" value={learningDocument.createdAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {learningDocument.updatedAt ? (
                      <TextFormat type="date" value={learningDocument.updatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{learningDocument.bookSeries}</td>
                  <td>{learningDocument.sourceMetadata}</td>
                  <td>
                    {learningDocument.courseMaterial ? (
                      <Link to={`/course-material/${learningDocument.courseMaterial.id}`}>{learningDocument.courseMaterial.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {learningDocument.classMaterial ? (
                      <Link to={`/class-material/${learningDocument.classMaterial.id}`}>{learningDocument.classMaterial.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{learningDocument.uploadedBy ? learningDocument.uploadedBy.login : ''}</td>
                  <td>
                    {learningDocument.subject ? (
                      <Link to={`/subject/${learningDocument.subject.id}`}>{learningDocument.subject.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {learningDocument.grade ? <Link to={`/grade/${learningDocument.grade.id}`}>{learningDocument.grade.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/learning-document/${learningDocument.id}`}
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
                        to={`/learning-document/${learningDocument.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/learning-document/${learningDocument.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.learningDocument.home.notFound">No Learning Documents found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={learningDocumentList && learningDocumentList.length > 0 ? '' : 'd-none'}>
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

export default LearningDocument;
