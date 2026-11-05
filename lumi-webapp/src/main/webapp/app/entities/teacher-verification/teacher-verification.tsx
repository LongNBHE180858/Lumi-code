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

import { getEntities } from './teacher-verification.reducer';

export const TeacherVerification = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const teacherVerificationList = useAppSelector(state => state.teacherVerification.entities);
  const loading = useAppSelector(state => state.teacherVerification.loading);
  const totalItems = useAppSelector(state => state.teacherVerification.totalItems);

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
      <h2 id="teacher-verification-heading" data-cy="TeacherVerificationHeading">
        <Translate contentKey="lumiApp.teacherVerification.home.title">Teacher Verifications</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.teacherVerification.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/teacher-verification/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.teacherVerification.home.createLabel">Create new Teacher Verification</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {teacherVerificationList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.teacherVerification.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('identityDocumentUrl')}>
                  <Translate contentKey="lumiApp.teacherVerification.identityDocumentUrl">Identity Document Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('identityDocumentUrl')} />
                </th>
                <th className="hand" onClick={sort('verificationStatus')}>
                  <Translate contentKey="lumiApp.teacherVerification.verificationStatus">Verification Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('verificationStatus')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="lumiApp.teacherVerification.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="lumiApp.teacherVerification.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('approvedAt')}>
                  <Translate contentKey="lumiApp.teacherVerification.approvedAt">Approved At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('approvedAt')} />
                </th>
                <th className="hand" onClick={sort('reviewedAt')}>
                  <Translate contentKey="lumiApp.teacherVerification.reviewedAt">Reviewed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewedAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.teacherVerification.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.teacherVerification.reviewedBy">Reviewed By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {teacherVerificationList.map(teacherVerification => (
                <tr key={`entity-${teacherVerification.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/teacher-verification/${teacherVerification.id}`} variant="link" size="sm">
                      {teacherVerification.id}
                    </Button>
                  </td>
                  <td>{teacherVerification.identityDocumentUrl}</td>
                  <td>{teacherVerification.verificationStatus}</td>
                  <td>{teacherVerification.notes}</td>
                  <td>
                    {teacherVerification.submittedAt ? (
                      <TextFormat type="date" value={teacherVerification.submittedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {teacherVerification.approvedAt ? (
                      <TextFormat type="date" value={teacherVerification.approvedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {teacherVerification.reviewedAt ? (
                      <TextFormat type="date" value={teacherVerification.reviewedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{teacherVerification.user ? teacherVerification.user.login : ''}</td>
                  <td>{teacherVerification.reviewedBy ? teacherVerification.reviewedBy.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/teacher-verification/${teacherVerification.id}`}
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
                        to={`/teacher-verification/${teacherVerification.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/teacher-verification/${teacherVerification.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.teacherVerification.home.notFound">No Teacher Verifications found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={teacherVerificationList && teacherVerificationList.length > 0 ? '' : 'd-none'}>
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

export default TeacherVerification;
