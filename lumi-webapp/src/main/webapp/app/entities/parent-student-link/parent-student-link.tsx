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

import { getEntities } from './parent-student-link.reducer';

export const ParentStudentLink = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const parentStudentLinkList = useAppSelector(state => state.parentStudentLink.entities);
  const loading = useAppSelector(state => state.parentStudentLink.loading);
  const totalItems = useAppSelector(state => state.parentStudentLink.totalItems);

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
      <h2 id="parent-student-link-heading" data-cy="ParentStudentLinkHeading">
        <Translate contentKey="lumiApp.parentStudentLink.home.title">Parent Student Links</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.parentStudentLink.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/parent-student-link/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.parentStudentLink.home.createLabel">Create new Parent Student Link</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {parentStudentLinkList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.parentStudentLink.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('linkCode')}>
                  <Translate contentKey="lumiApp.parentStudentLink.linkCode">Link Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('linkCode')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.parentStudentLink.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('linkedAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.linkedAt">Linked At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('linkedAt')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('codeExpiresAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.codeExpiresAt">Code Expires At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('codeExpiresAt')} />
                </th>
                <th className="hand" onClick={sort('confirmedAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.confirmedAt">Confirmed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('confirmedAt')} />
                </th>
                <th className="hand" onClick={sort('revokedAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.revokedAt">Revoked At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('revokedAt')} />
                </th>
                <th className="hand" onClick={sort('relationshipType')}>
                  <Translate contentKey="lumiApp.parentStudentLink.relationshipType">Relationship Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('relationshipType')} />
                </th>
                <th className="hand" onClick={sort('guardianVerificationStatus')}>
                  <Translate contentKey="lumiApp.parentStudentLink.guardianVerificationStatus">Guardian Verification Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('guardianVerificationStatus')} />
                </th>
                <th className="hand" onClick={sort('guardianVerifiedAt')}>
                  <Translate contentKey="lumiApp.parentStudentLink.guardianVerifiedAt">Guardian Verified At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('guardianVerifiedAt')} />
                </th>
                <th className="hand" onClick={sort('guardianVerificationNote')}>
                  <Translate contentKey="lumiApp.parentStudentLink.guardianVerificationNote">Guardian Verification Note</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('guardianVerificationNote')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.parent">Parent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.confirmedBy">Confirmed By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.guardianVerifiedBy">Guardian Verified By</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {parentStudentLinkList.map(parentStudentLink => (
                <tr key={`entity-${parentStudentLink.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/parent-student-link/${parentStudentLink.id}`} variant="link" size="sm">
                      {parentStudentLink.id}
                    </Button>
                  </td>
                  <td>{parentStudentLink.linkCode}</td>
                  <td>{parentStudentLink.status}</td>
                  <td>
                    {parentStudentLink.linkedAt ? (
                      <TextFormat type="date" value={parentStudentLink.linkedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {parentStudentLink.createdAt ? (
                      <TextFormat type="date" value={parentStudentLink.createdAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {parentStudentLink.codeExpiresAt ? (
                      <TextFormat type="date" value={parentStudentLink.codeExpiresAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {parentStudentLink.confirmedAt ? (
                      <TextFormat type="date" value={parentStudentLink.confirmedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {parentStudentLink.revokedAt ? (
                      <TextFormat type="date" value={parentStudentLink.revokedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{parentStudentLink.relationshipType}</td>
                  <td>{parentStudentLink.guardianVerificationStatus}</td>
                  <td>
                    {parentStudentLink.guardianVerifiedAt ? (
                      <TextFormat type="date" value={parentStudentLink.guardianVerifiedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{parentStudentLink.guardianVerificationNote}</td>
                  <td>{parentStudentLink.parent ? parentStudentLink.parent.login : ''}</td>
                  <td>{parentStudentLink.student ? parentStudentLink.student.login : ''}</td>
                  <td>{parentStudentLink.confirmedBy ? parentStudentLink.confirmedBy.login : ''}</td>
                  <td>{parentStudentLink.guardianVerifiedBy ? parentStudentLink.guardianVerifiedBy.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/parent-student-link/${parentStudentLink.id}`}
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
                        to={`/parent-student-link/${parentStudentLink.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/parent-student-link/${parentStudentLink.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.parentStudentLink.home.notFound">No Parent Student Links found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={parentStudentLinkList && parentStudentLinkList.length > 0 ? '' : 'd-none'}>
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

export default ParentStudentLink;
