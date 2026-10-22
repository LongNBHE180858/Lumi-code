import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './parent-student-link.reducer';

export const ParentStudentLink = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const parentStudentLinkList = useAppSelector(state => state.parentStudentLink.entities);
  const loading = useAppSelector(state => state.parentStudentLink.loading);

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
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.parent">Parent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.parentStudentLink.student">Student</Translate> <FontAwesomeIcon icon="sort" />
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
                  <td>{parentStudentLink.parent ? parentStudentLink.parent.login : ''}</td>
                  <td>{parentStudentLink.student ? parentStudentLink.student.login : ''}</td>
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
                        to={`/parent-student-link/${parentStudentLink.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/parent-student-link/${parentStudentLink.id}/delete`)}
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
    </div>
  );
};

export default ParentStudentLink;
