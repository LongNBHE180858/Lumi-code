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

import { getEntities } from './course-review-request.reducer';

export const CourseReviewRequest = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const courseReviewRequestList = useAppSelector(state => state.courseReviewRequest.entities);
  const loading = useAppSelector(state => state.courseReviewRequest.loading);
  const totalItems = useAppSelector(state => state.courseReviewRequest.totalItems);

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
      <h2 id="course-review-request-heading" data-cy="CourseReviewRequestHeading">
        <Translate contentKey="lumiApp.courseReviewRequest.home.title">Course Review Requests</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.courseReviewRequest.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/course-review-request/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.courseReviewRequest.home.createLabel">Create new Course Review Request</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {courseReviewRequestList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('courseVersion')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.courseVersion">Course Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('courseVersion')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('submissionNote')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.submissionNote">Submission Note</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submissionNote')} />
                </th>
                <th className="hand" onClick={sort('rightsConfirmedAt')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.rightsConfirmedAt">Rights Confirmed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rightsConfirmedAt')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('reviewedAt')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.reviewedAt">Reviewed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewedAt')} />
                </th>
                <th className="hand" onClick={sort('reviewerFeedback')}>
                  <Translate contentKey="lumiApp.courseReviewRequest.reviewerFeedback">Reviewer Feedback</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reviewerFeedback')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseReviewRequest.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseReviewRequest.submittedBy">Submitted By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseReviewRequest.reviewedBy">Reviewed By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courseReviewRequestList.map(courseReviewRequest => (
                <tr key={`entity-${courseReviewRequest.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/course-review-request/${courseReviewRequest.id}`} variant="link" size="sm">
                      {courseReviewRequest.id}
                    </Button>
                  </td>
                  <td>{courseReviewRequest.courseVersion}</td>
                  <td>{courseReviewRequest.status}</td>
                  <td>{courseReviewRequest.submissionNote}</td>
                  <td>
                    {courseReviewRequest.rightsConfirmedAt ? (
                      <TextFormat type="date" value={courseReviewRequest.rightsConfirmedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {courseReviewRequest.submittedAt ? (
                      <TextFormat type="date" value={courseReviewRequest.submittedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {courseReviewRequest.reviewedAt ? (
                      <TextFormat type="date" value={courseReviewRequest.reviewedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{courseReviewRequest.reviewerFeedback}</td>
                  <td>
                    {courseReviewRequest.course ? (
                      <Link to={`/course/${courseReviewRequest.course.id}`}>{courseReviewRequest.course.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{courseReviewRequest.submittedBy ? courseReviewRequest.submittedBy.login : ''}</td>
                  <td>{courseReviewRequest.reviewedBy ? courseReviewRequest.reviewedBy.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/course-review-request/${courseReviewRequest.id}`}
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
                        to={`/course-review-request/${courseReviewRequest.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/course-review-request/${courseReviewRequest.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.courseReviewRequest.home.notFound">No Course Review Requests found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={courseReviewRequestList && courseReviewRequestList.length > 0 ? '' : 'd-none'}>
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

export default CourseReviewRequest;
