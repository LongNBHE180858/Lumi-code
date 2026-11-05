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

import { getEntities } from './ai-generated-content.reducer';

export const AiGeneratedContent = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const aiGeneratedContentList = useAppSelector(state => state.aiGeneratedContent.entities);
  const loading = useAppSelector(state => state.aiGeneratedContent.loading);
  const totalItems = useAppSelector(state => state.aiGeneratedContent.totalItems);

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
      <h2 id="ai-generated-content-heading" data-cy="AiGeneratedContentHeading">
        <Translate contentKey="lumiApp.aiGeneratedContent.home.title">Ai Generated Contents</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.aiGeneratedContent.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/ai-generated-content/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.aiGeneratedContent.home.createLabel">Create new Ai Generated Content</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {aiGeneratedContentList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('contentType')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.contentType">Content Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentType')} />
                </th>
                <th className="hand" onClick={sort('requestHash')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.requestHash">Request Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestHash')} />
                </th>
                <th className="hand" onClick={sort('generationConfig')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.generationConfig">Generation Config</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('generationConfig')} />
                </th>
                <th className="hand" onClick={sort('sourceVersion')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.sourceVersion">Source Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceVersion')} />
                </th>
                <th className="hand" onClick={sort('modelName')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.modelName">Model Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('modelName')} />
                </th>
                <th className="hand" onClick={sort('promptVersion')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.promptVersion">Prompt Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('promptVersion')} />
                </th>
                <th className="hand" onClick={sort('content')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.content">Content</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('content')} />
                </th>
                <th className="hand" onClick={sort('generationStatus')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.generationStatus">Generation Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('generationStatus')} />
                </th>
                <th className="hand" onClick={sort('visibility')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.visibility">Visibility</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('visibility')} />
                </th>
                <th className="hand" onClick={sort('version')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.version">Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('version')} />
                </th>
                <th className="hand" onClick={sort('reviewStatus')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.reviewStatus">Review Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewStatus')} />
                </th>
                <th className="hand" onClick={sort('reviewedAt')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.reviewedAt">Reviewed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewedAt')} />
                </th>
                <th className="hand" onClick={sort('reviewerFeedback')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.reviewerFeedback">Reviewer Feedback</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewerFeedback')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th className="hand" onClick={sort('sourceQuestionId')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.sourceQuestionId">Source Question Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceQuestionId')} />
                </th>
                <th className="hand" onClick={sort('contentHash')}>
                  <Translate contentKey="lumiApp.aiGeneratedContent.contentHash">Content Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentHash')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.aiGeneratedContent.createdBy">Created By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.aiGeneratedContent.document">Document</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.aiGeneratedContent.lesson">Lesson</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.aiGeneratedContent.reviewedBy">Reviewed By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.aiGeneratedContent.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {aiGeneratedContentList.map(aiGeneratedContent => (
                <tr key={`entity-${aiGeneratedContent.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/ai-generated-content/${aiGeneratedContent.id}`} variant="link" size="sm">
                      {aiGeneratedContent.id}
                    </Button>
                  </td>
                  <td>{aiGeneratedContent.contentType}</td>
                  <td>{aiGeneratedContent.requestHash}</td>
                  <td>{aiGeneratedContent.generationConfig}</td>
                  <td>{aiGeneratedContent.sourceVersion}</td>
                  <td>{aiGeneratedContent.modelName}</td>
                  <td>{aiGeneratedContent.promptVersion}</td>
                  <td>{aiGeneratedContent.content}</td>
                  <td>{aiGeneratedContent.generationStatus}</td>
                  <td>{aiGeneratedContent.visibility}</td>
                  <td>{aiGeneratedContent.version}</td>
                  <td>{aiGeneratedContent.reviewStatus}</td>
                  <td>
                    {aiGeneratedContent.reviewedAt ? (
                      <TextFormat type="date" value={aiGeneratedContent.reviewedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{aiGeneratedContent.reviewerFeedback}</td>
                  <td>
                    {aiGeneratedContent.createdAt ? (
                      <TextFormat type="date" value={aiGeneratedContent.createdAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {aiGeneratedContent.updatedAt ? (
                      <TextFormat type="date" value={aiGeneratedContent.updatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{aiGeneratedContent.sourceQuestionId}</td>
                  <td>{aiGeneratedContent.contentHash}</td>
                  <td>{aiGeneratedContent.createdBy ? aiGeneratedContent.createdBy.login : ''}</td>
                  <td>
                    {aiGeneratedContent.document ? (
                      <Link to={`/learning-document/${aiGeneratedContent.document.id}`}>{aiGeneratedContent.document.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {aiGeneratedContent.lesson ? (
                      <Link to={`/lesson/${aiGeneratedContent.lesson.id}`}>{aiGeneratedContent.lesson.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{aiGeneratedContent.reviewedBy ? aiGeneratedContent.reviewedBy.login : ''}</td>
                  <td>
                    {aiGeneratedContent.course ? (
                      <Link to={`/course/${aiGeneratedContent.course.id}`}>{aiGeneratedContent.course.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/ai-generated-content/${aiGeneratedContent.id}`}
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
                        to={`/ai-generated-content/${aiGeneratedContent.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/ai-generated-content/${aiGeneratedContent.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.aiGeneratedContent.home.notFound">No Ai Generated Contents found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={aiGeneratedContentList && aiGeneratedContentList.length > 0 ? '' : 'd-none'}>
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

export default AiGeneratedContent;
