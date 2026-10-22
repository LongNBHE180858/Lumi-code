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

import { getEntities } from './course-enrollment.reducer';

export const CourseEnrollment = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const courseEnrollmentList = useAppSelector(state => state.courseEnrollment.entities);
  const loading = useAppSelector(state => state.courseEnrollment.loading);
  const totalItems = useAppSelector(state => state.courseEnrollment.totalItems);

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
      <h2 id="course-enrollment-heading" data-cy="CourseEnrollmentHeading">
        <Translate contentKey="lumiApp.courseEnrollment.home.title">Course Enrollments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.courseEnrollment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/course-enrollment/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.courseEnrollment.home.createLabel">Create new Course Enrollment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {courseEnrollmentList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.courseEnrollment.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('progressPercent')}>
                  <Translate contentKey="lumiApp.courseEnrollment.progressPercent">Progress Percent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('progressPercent')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.courseEnrollment.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('enrolledAt')}>
                  <Translate contentKey="lumiApp.courseEnrollment.enrolledAt">Enrolled At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('enrolledAt')} />
                </th>
                <th className="hand" onClick={sort('expiresAt')}>
                  <Translate contentKey="lumiApp.courseEnrollment.expiresAt">Expires At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('expiresAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseEnrollment.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseEnrollment.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courseEnrollmentList.map(courseEnrollment => (
                <tr key={`entity-${courseEnrollment.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/course-enrollment/${courseEnrollment.id}`} variant="link" size="sm">
                      {courseEnrollment.id}
                    </Button>
                  </td>
                  <td>{courseEnrollment.progressPercent}</td>
                  <td>{courseEnrollment.status}</td>
                  <td>
                    {courseEnrollment.enrolledAt ? (
                      <TextFormat type="date" value={courseEnrollment.enrolledAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {courseEnrollment.expiresAt ? (
                      <TextFormat type="date" value={courseEnrollment.expiresAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{courseEnrollment.student ? courseEnrollment.student.login : ''}</td>
                  <td>
                    {courseEnrollment.course ? (
                      <Link to={`/course/${courseEnrollment.course.id}`}>{courseEnrollment.course.title}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/course-enrollment/${courseEnrollment.id}`}
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
                        to={`/course-enrollment/${courseEnrollment.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/course-enrollment/${courseEnrollment.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.courseEnrollment.home.notFound">No Course Enrollments found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={courseEnrollmentList && courseEnrollmentList.length > 0 ? '' : 'd-none'}>
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

export default CourseEnrollment;
