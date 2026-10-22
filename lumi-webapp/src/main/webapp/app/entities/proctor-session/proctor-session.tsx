import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './proctor-session.reducer';

export const ProctorSession = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const proctorSessionList = useAppSelector(state => state.proctorSession.entities);
  const loading = useAppSelector(state => state.proctorSession.loading);

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
      <h2 id="proctor-session-heading" data-cy="ProctorSessionHeading">
        <Translate contentKey="lumiApp.proctorSession.home.title">Proctor Sessions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.proctorSession.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/proctor-session/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.proctorSession.home.createLabel">Create new Proctor Session</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {proctorSessionList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.proctorSession.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('faceVerified')}>
                  <Translate contentKey="lumiApp.proctorSession.faceVerified">Face Verified</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('faceVerified')} />
                </th>
                <th className="hand" onClick={sort('warningCount')}>
                  <Translate contentKey="lumiApp.proctorSession.warningCount">Warning Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('warningCount')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.proctorSession.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.proctorSession.attempt">Attempt</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {proctorSessionList.map(proctorSession => (
                <tr key={`entity-${proctorSession.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/proctor-session/${proctorSession.id}`} variant="link" size="sm">
                      {proctorSession.id}
                    </Button>
                  </td>
                  <td>{proctorSession.faceVerified ? 'true' : 'false'}</td>
                  <td>{proctorSession.warningCount}</td>
                  <td>{proctorSession.status}</td>
                  <td>
                    {proctorSession.attempt ? (
                      <Link to={`/exam-attempt/${proctorSession.attempt.id}`}>{proctorSession.attempt.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/proctor-session/${proctorSession.id}`}
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
                        to={`/proctor-session/${proctorSession.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/proctor-session/${proctorSession.id}/delete`)}
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
              <Translate contentKey="lumiApp.proctorSession.home.notFound">No Proctor Sessions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ProctorSession;
