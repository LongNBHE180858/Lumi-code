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

import { getEntities } from './course.reducer';

export const Course = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const courseList = useAppSelector(state => state.course.entities);
  const loading = useAppSelector(state => state.course.loading);
  const totalItems = useAppSelector(state => state.course.totalItems);

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
      <h2 id="course-heading" data-cy="CourseHeading">
        <Translate contentKey="lumiApp.course.home.title">Courses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.course.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/course/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.course.home.createLabel">Create new Course</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {courseList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.course.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="lumiApp.course.title">Title</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="lumiApp.course.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('thumbnailUrl')}>
                  <Translate contentKey="lumiApp.course.thumbnailUrl">Thumbnail Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('thumbnailUrl')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  <Translate contentKey="lumiApp.course.price">Price</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('validDurationDays')}>
                  <Translate contentKey="lumiApp.course.validDurationDays">Valid Duration Days</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('validDurationDays')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.course.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('isDeleted')}>
                  <Translate contentKey="lumiApp.course.isDeleted">Is Deleted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isDeleted')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.course.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.course.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.author">Author</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.category">Category</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.subject">Subject</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.grade">Grade</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.shortDescription" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.bookSeries" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.targetAudience" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.prerequisites" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.learningObjectives" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.introVideoUrl" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.accessDurationValue" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.accessDurationUnit" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.course.version" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courseList.map(course => (
                <tr key={`entity-${course.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/course/${course.id}`} variant="link" size="sm">
                      {course.id}
                    </Button>
                  </td>
                  <td>{course.title}</td>
                  <td>{course.description}</td>
                  <td>{course.thumbnailUrl}</td>
                  <td>{course.price}</td>
                  <td>{course.validDurationDays}</td>
                  <td>
                    <Translate contentKey={`lumiApp.RecordStatus.${course.status}`} />
                  </td>
                  <td>{course.isDeleted ? 'true' : 'false'}</td>
                  <td>{course.createdAt ? <TextFormat type="date" value={course.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{course.updatedAt ? <TextFormat type="date" value={course.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{course.author ? course.author.login : ''}</td>
                  <td>{course.category ? <Link to={`/category/${course.category.id}`}>{course.category.name}</Link> : ''}</td>
                  <td>{course.subject ? <Link to={`/subject/${course.subject.id}`}>{course.subject.name}</Link> : ''}</td>
                  <td>{course.grade ? <Link to={`/grade/${course.grade.id}`}>{course.grade.name}</Link> : ''}</td>
                  <td>{course.shortDescription}</td>
                  <td>{course.bookSeries}</td>
                  <td>{course.targetAudience}</td>
                  <td>{course.prerequisites}</td>
                  <td>{course.learningObjectives?.join('; ')}</td>
                  <td>{course.introVideoUrl}</td>
                  <td>{course.accessDurationValue}</td>
                  <td>
                    {course.accessDurationUnit ? (
                      <Translate contentKey={`lumiApp.AccessDurationUnit.${course.accessDurationUnit}`} />
                    ) : null}
                  </td>
                  <td>{course.version}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/course/${course.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/course/${course.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/course/${course.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.course.home.notFound">No Courses found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={courseList && courseList.length > 0 ? '' : 'd-none'}>
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

export default Course;
