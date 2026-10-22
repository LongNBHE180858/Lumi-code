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

import { getEntities } from './class-material.reducer';

export const ClassMaterial = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const classMaterialList = useAppSelector(state => state.classMaterial.entities);
  const loading = useAppSelector(state => state.classMaterial.loading);
  const totalItems = useAppSelector(state => state.classMaterial.totalItems);

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
      <h2 id="class-material-heading" data-cy="ClassMaterialHeading">
        <Translate contentKey="lumiApp.classMaterial.home.title">Class Materials</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.classMaterial.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/class-material/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.classMaterial.home.createLabel">Create new Class Material</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {classMaterialList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.classMaterial.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.classMaterial.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('fileType')}>
                  <Translate contentKey="lumiApp.classMaterial.fileType">File Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileType')} />
                </th>
                <th className="hand" onClick={sort('fileUrl')}>
                  <Translate contentKey="lumiApp.classMaterial.fileUrl">File Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileUrl')} />
                </th>
                <th className="hand" onClick={sort('fileSizeBytes')}>
                  <Translate contentKey="lumiApp.classMaterial.fileSizeBytes">File Size Bytes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileSizeBytes')} />
                </th>
                <th className="hand" onClick={sort('uploadedAt')}>
                  <Translate contentKey="lumiApp.classMaterial.uploadedAt">Uploaded At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uploadedAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.classMaterial.studyClass">Study Class</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {classMaterialList.map(classMaterial => (
                <tr key={`entity-${classMaterial.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/class-material/${classMaterial.id}`} variant="link" size="sm">
                      {classMaterial.id}
                    </Button>
                  </td>
                  <td>{classMaterial.title}</td>
                  <td>{classMaterial.fileType}</td>
                  <td>{classMaterial.fileUrl}</td>
                  <td>{classMaterial.fileSizeBytes}</td>
                  <td>
                    {classMaterial.uploadedAt ? <TextFormat type="date" value={classMaterial.uploadedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {classMaterial.studyClass ? (
                      <Link to={`/study-class/${classMaterial.studyClass.id}`}>{classMaterial.studyClass.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/class-material/${classMaterial.id}`}
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
                        to={`/class-material/${classMaterial.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/class-material/${classMaterial.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.classMaterial.home.notFound">No Class Materials found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={classMaterialList && classMaterialList.length > 0 ? '' : 'd-none'}>
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

export default ClassMaterial;
