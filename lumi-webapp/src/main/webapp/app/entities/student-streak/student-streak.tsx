import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './student-streak.reducer';

export const StudentStreak = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const studentStreakList = useAppSelector(state => state.studentStreak.entities);
  const loading = useAppSelector(state => state.studentStreak.loading);

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
      <h2 id="student-streak-heading" data-cy="StudentStreakHeading">
        <Translate contentKey="lumiApp.studentStreak.home.title">Student Streaks</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.studentStreak.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/student-streak/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.studentStreak.home.createLabel">Create new Student Streak</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {studentStreakList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.studentStreak.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('currentStreak')}>
                  <Translate contentKey="lumiApp.studentStreak.currentStreak">Current Streak</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currentStreak')} />
                </th>
                <th className="hand" onClick={sort('longestStreak')}>
                  <Translate contentKey="lumiApp.studentStreak.longestStreak">Longest Streak</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('longestStreak')} />
                </th>
                <th className="hand" onClick={sort('gemsBalance')}>
                  <Translate contentKey="lumiApp.studentStreak.gemsBalance">Gems Balance</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('gemsBalance')} />
                </th>
                <th className="hand" onClick={sort('lastActiveDate')}>
                  <Translate contentKey="lumiApp.studentStreak.lastActiveDate">Last Active Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastActiveDate')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.studentStreak.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {studentStreakList.map(studentStreak => (
                <tr key={`entity-${studentStreak.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/student-streak/${studentStreak.id}`} variant="link" size="sm">
                      {studentStreak.id}
                    </Button>
                  </td>
                  <td>{studentStreak.currentStreak}</td>
                  <td>{studentStreak.longestStreak}</td>
                  <td>{studentStreak.gemsBalance}</td>
                  <td>
                    {studentStreak.lastActiveDate ? (
                      <TextFormat type="date" value={studentStreak.lastActiveDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{studentStreak.user ? studentStreak.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/student-streak/${studentStreak.id}`}
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
                        to={`/student-streak/${studentStreak.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/student-streak/${studentStreak.id}/delete`)}
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
              <Translate contentKey="lumiApp.studentStreak.home.notFound">No Student Streaks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default StudentStreak;
